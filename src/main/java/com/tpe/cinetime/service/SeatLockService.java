package com.tpe.cinetime.service;

import com.tpe.cinetime.constants.messages.ErrorMessages;
import com.tpe.cinetime.constants.messages.SuccessMessages;
import com.tpe.cinetime.exception.SeatUnavailableException;
import com.tpe.cinetime.payload.response.SeatLockResponse;
import com.tpe.cinetime.payload.responseMessage.ResponseMessage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Getter
@Service
@RequiredArgsConstructor
public class SeatLockService {

    private final RedisTemplate<String, String> redisTemplate;

    private final DefaultRedisScript<Long> lockSeatsScript;
    private final DefaultRedisScript<Long> unlockSeatsScript;
    private final DefaultRedisScript<Long> extendLockScript;

    private final DefaultRedisScript<Long> lockOrExtendSeatScript;
    private final DefaultRedisScript<Long> unlockSingleSeatScript;

    // Kilit süresi: koltuk secildikten sonra ödeme icin tanina süre
    @Value("${app.seat-lock.ttl-seconds:600}")
    private long lockTtlSeconds;


    // Format: seat:lock:{showtimeId}:{seatId}
    // seat:lock:42:107
    // showtimeId'yi key'e dahil etmemizin sebebi ayni koltuk farkli seanslarda
    // bagimsiz olarak dolu/bos olabilir.
    private String buildKey(Long showtimeId, Long seatId) {
        return "seat:lock:%d:%d".formatted(showtimeId, seatId);
    }

    private String buildSeatKey(Long showtimeId, Long seatId) {
        return "seat:lock:%d:%d".formatted(showtimeId, seatId);
    }

    // Bir lockToken'a hangi koltukların bağlı olduğunu tutan SET'in key'i
    private String buildOwnerSetKey(String lockToken) {
        return "lock:owner:%s".formatted(lockToken);
    }


    // Kullanicinin sectigi tüm koltuklari ya hep ya hic sekilde kilitlemeye calisir
    public String tryLockSeats(Long showtimeId, List<Long> seatIds) {
        // Her koltuk id'sini kendi Redis key'ine çeviriyoruz
        // Örn: [107, 108] -> ["seat:lock:42:107", "seat:lock:42:108"]
        List<String > key = seatIds.stream()
                .map(seatId -> buildKey(showtimeId, seatId))
                .toList();

        // Bu kilidin "sahiplik kanıtı" olacak benzersiz token
        // Bunu kullanıcıya (frontend'e) response olarak döneceğiz,
        // frontend sonraki isteklerinde (unlock, confirm) bu token'ı geri gönderecek
        // Böylece "bu kilit gerçekten bu kullanıcıya mı ait" kontrolü yapabiliyoruz
        String lockToken = UUID.randomUUID().toString();

        // redisTemplate.execute(script, keys, args...) Lua script'ini Redis üzerinde çalıştırır
        Long result = redisTemplate.execute(
                lockSeatsScript,
                key,
                lockToken,
                String.valueOf(lockTtlSeconds)
        );

        // Lua scriptimiz başarılıysa 1, koltuklardan biri doluysa 0 döndürüyordu (bkz. lock_seats.lua)
        // result == 1 ise kilitleme başarılı, token'ı döndürüyoruz
        // result == 0 ya da null ise (network hatası vs.) başarısız, null döndürüyoruz
        return (result != null && result == 1L) ? lockToken : null;
    }
    //Kilidi serbest bırakır — ama SADECE gönderilen token, key'in mevcut sahibiyle eşleşiyorsa.
    // Bu kontrolü Lua script içinde yapıyoruz (bkz. unlock_seats.lua),
    // çünkü "kontrol et + sil" işlemi de atomic olmalı.
    // Kullanım senaryoları:
    // Kullanıcı "vazgeçtim" deyip başka koltuk seçmek istediğinde
    // Ödeme başarısız olduğunda (booking'i CANCELLED yaparken kilidi de hemen boşaltmak için,
    // TTL'in dolmasını beklemek yerine koltuğu anında başkasına açmak isteyebiliriz)
    public boolean releaseSeats(Long showtimeId, List<Long> seatIds, String lockToken) {
        List<String> keys = seatIds.stream()
                .map(seatId -> buildKey(showtimeId, seatId))
                .toList();

        Long result = redisTemplate.execute(unlockSeatsScript, keys, lockToken);

        return result != null && result == 1L;
    }

    /**
     * Verilen TÜM koltukların, gerçekten bu lockToken'a ait olup olmadığını kontrol eder.
     *
     * Bu metod DOĞRUDAN Redis komutu (MGET) kullanıyor, Lua script'e gerek yok —
     * çünkü burada "yazma" yapmıyoruz, sadece "okuma" yapıyoruz.
     * Okuma işlemlerinde race condition riski yok (biz bir şeyi değiştirmiyoruz),
     * bu yüzden atomic script şart değil.
     *
     * NE ZAMAN KULLANILACAK: Ödeme onaylanmadan hemen önce, booking'i DB'ye
     * kalıcı olarak yazmadan önce son bir doğrulama katmanı olarak.
     *
     * Neden gerekli: Kullanıcı ödeme ekranında 10 dakikadan uzun süre oyalanmış olabilir.
     * Bu durumda TTL dolmuş, kilit kalkmış, belki başka biri o koltuğu almış olabilir.
     * Bu kontrolü yapmadan direkt booking kaydetseydik, süresi geçmiş bir kilitle
     * işlem onaylanmış olur ve double-booking riski geri dönerdi.
     *
     * @return true: verilen koltukların HEPSİ hâlâ bu token'a ait ve geçerli
     *         false: koltuklardan en az biri artık bu token'a ait değil (süre dolmuş/başkası almış)
     */
    public boolean areAllOwnedBy(Long showtimeId, List<Long> seatIds, String lockToken) {

        List<String> keys = seatIds.stream()
                .map(seatId -> buildKey(showtimeId, seatId))
                .toList();

        // MGET: birden fazla key'in değerini TEK Redis çağrısıyla almamızı sağlar
        // Neden tek tek GET yerine MGET: N koltuk için N ayrı network round-trip yerine
        // 1 round-trip yapıyoruz, performans için önemli
        List<String> values = redisTemplate.opsForValue().multiGet(keys);

        if (values == null) {
            return false;
        }

        // values listesindeki HER değerin, bize verilen lockToken ile eşleşmesi gerekiyor
        // Eşleşmeyen tek bir tane bile varsa (null dahil - yani key hiç yoksa/silinmişse),
        // bu artık "hepsi bizim" durumu değil demektir
        return values.stream().allMatch(lockToken::equals);
    }

    /**
     * Kilit süresini uzatır (opsiyonel özellik).
     * Kullanım senaryosu: ödeme sayfasında kullanıcıya "süreniz doluyor, uzatmak ister misiniz?"
     * gibi bir buton gösterip, tıklarsa bu metodu çağırabiliriz.
     *
     * @return true: uzatma başarılı (tüm koltuklar hâlâ bu token'a aitti)
     *         false: uzatma başarısız (koltuklardan biri artık bu token'a ait değildi)
     */
    public boolean extendLock(Long showtimeId, List<Long> seatIds, String lockToken) {

        List<String> keys = seatIds.stream()
                .map(seatId -> buildKey(showtimeId, seatId))
                .toList();

        Long result = redisTemplate.execute(
                extendLockScript,
                keys,
                lockToken,
                String.valueOf(lockTtlSeconds)
        );

        return result != null && result == 1L;
    }

    /**
     * Kullanıcı bir koltuğa tıkladığında çağrılır.
     * Artık tüm iş mantığı (hata kontrolü + response hazırlama) burada,
     * controller sadece bu metodu çağırıp döneni aynen geri veriyor.
     */
    public ResponseMessage<SeatLockResponse> lockOrExtendSeat(
            Long showtimeId, Long seatId, String existingLockToken) {

        String token = (existingLockToken != null) ? existingLockToken : UUID.randomUUID().toString();

        String seatKey = buildSeatKey(showtimeId, seatId);
        String ownerSetKey = buildOwnerSetKey(token);

        Long result = redisTemplate.execute(
                lockOrExtendSeatScript,
                List.of(seatKey, ownerSetKey),
                token,
                String.valueOf(lockTtlSeconds),
                String.valueOf(showtimeId),
                String.valueOf(seatId)
        );

        if (result == null || result != 1L) {
            throw new SeatUnavailableException(ErrorMessages.SEATS_ALREADY_LOCKED);
        }

        Instant expiresAt = Instant.now().plus(Duration.ofSeconds(lockTtlSeconds));
        SeatLockResponse dto = new SeatLockResponse(token, seatId, expiresAt);

        return ResponseMessage.<SeatLockResponse>builder()
                .object(dto)
                .message(SuccessMessages.SEATS_LOCKED_SUCCESSFULLY)
                .httpStatus(HttpStatus.OK)
                .build();
    }

    /**
     * Kullanıcı bir koltuğu kaldırdığında (deselect) çağrılır.
     */
    public ResponseMessage<?> unlockSingleSeat(Long showtimeId, Long seatId, String lockToken) {

        String seatKey = buildSeatKey(showtimeId, seatId);
        String ownerSetKey = buildOwnerSetKey(lockToken);

        Long result = redisTemplate.execute(
                unlockSingleSeatScript,
                List.of(seatKey, ownerSetKey),
                lockToken,
                String.valueOf(seatId)
        );

        if (result == null || result != 1L) {
            throw new SeatUnavailableException(ErrorMessages.SEAT_LOCK_EXPIRED_OR_INVALID);
        }

        return ResponseMessage.builder()
                .message(SuccessMessages.SEAT_LOCK_RELEASED_SUCCESSFULLY)
                .httpStatus(HttpStatus.OK)
                .build();
    }

    /**
     * Verilen tüm koltukların, gerçekten bu lockToken'a ait olup olmadığını kontrol eder.
     * "Ödemeye geç" anında, booking oluşturmadan hemen önce çağrılır.
     *
     * Sadece okuma yaptığımız için (MGET), Lua script'e gerek yok —
     * atomicity endişesi yok, biz burada Redis'in state'ini değiştirmiyoruz.
     */
    public boolean areAllSeatsOwnedByToken(Long showtimeId, List<Long> seatIds, String lockToken) {

        List<String> keys = seatIds.stream()
                .map(seatId -> buildSeatKey(showtimeId, seatId))
                .toList();

        List<String> values = redisTemplate.opsForValue().multiGet(keys);

        if (values == null) {
            return false;
        }

        return values.stream().allMatch(lockToken::equals);
    }

    /**
     * Booking DB'ye başarıyla kaydedildikten sonra, artık gerek kalmayan
     * Redis kilitlerini (tüm koltuk key'leri + owner set) temizler.
     *
     * Bunun "best effort" bir temizlik olduğunu unutmayın — bu adım başarısız olsa bile
     * TTL zaten kendiliğinden dolup key'leri silecektir, kritik bir tutarlılık riski yok.
     */
    public void releaseAllSeatsForToken(Long showtimeId, List<Long> seatIds, String lockToken) {

        List<String> seatKeys = seatIds.stream()
                .map(seatId -> buildSeatKey(showtimeId, seatId))
                .toList();

        redisTemplate.delete(seatKeys);
        redisTemplate.delete(buildOwnerSetKey(lockToken));
    }

    /**
     * Verilen koltuk id'lerinden hangilerinin şu an Redis'te kilitli (herhangi bir
     * kullanıcı tarafından seçilmiş) olduğunu bulur. Koltuk haritası endpoint'i
     * tarafından, DB'deki booking kontrolüne EK olarak kullanılır.
     *
     * Tek tek GET yerine tek bir MGET ile tüm koltukları sorguluyoruz — performans için.
     */
    public Set<Long> findLockedSeatIds(Long showtimeId, List<Long> allSeatIds) {

        List<String> keys = allSeatIds.stream()
                .map(seatId -> buildSeatKey(showtimeId, seatId))
                .toList();

        List<String> values = redisTemplate.opsForValue().multiGet(keys);

        if (values == null) {
            return Set.of();
        }

        Set<Long> lockedSeatIds = new HashSet<>();
        for (int i = 0; i < allSeatIds.size(); i++) {
            if (values.get(i) != null) {  // key'in bir değeri varsa (herhangi bir token'a ait olsa da), kilitli demektir
                lockedSeatIds.add(allSeatIds.get(i));
            }
        }

        return lockedSeatIds;
    }


}
