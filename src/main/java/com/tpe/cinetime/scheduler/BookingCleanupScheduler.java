package com.tpe.cinetime.scheduler;

import com.tpe.cinetime.entity.Booking;
import com.tpe.cinetime.enums.BookingStatus;
import com.tpe.cinetime.repository.booking.BookingRepository;
import com.tpe.cinetime.service.SeatLockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class BookingCleanupScheduler {

    private final BookingRepository bookingRepository;
    private final SeatLockService seatLockService;

    @Value("${app.booking.pending-timeout-minutes:10}")
    private long pendingTimeoutMinutes;

    /**
     * Her 1 dakikada bir çalışır (60000 ms = 60 sn).
     * Belirlenen süreden (varsayılan 10 dk) daha uzun süredir PENDING durumda
     * bekleyen booking'leri bulup CANCELLED'a çeker.
     *
     * Bu, kullanıcının ödeme yapmadan siteyi terk ettiği (tarayıcıyı kapattığı,
     * "X" ile pop-up'ı kapatmadığı, hiçbir sinyal göndermediği) senaryolar için
     * son bir güvenlik ağı — normal akışta zaten unlock/payment ile temizlenmiş olur.
     */
    @Scheduled(fixedRate = 30000)
    @Transactional
    public void cancelAbandonedPendingBookings() {

        LocalDateTime cutoffTime = LocalDateTime.now().minusMinutes(pendingTimeoutMinutes);

        List<Booking> abandonedBookings = bookingRepository.findByStatusAndCreatedAtBefore(
                BookingStatus.PENDING, cutoffTime);

        if (abandonedBookings.isEmpty()) {
            return; // temizlenecek bir şey yok, sessizce çık
        }

        for (Booking booking : abandonedBookings) {
            booking.setStatus(BookingStatus.CANCELLED);
            // YENİ EKLENEN — booking terk edildiği için Redis kilidini de temizliyoruz
            // (normalde TTL zaten kendiliğinden düşmüş olacaktır ama garantiye alıyoruz)
            if (booking.getLockToken() != null) {
                List<Long> seatIds = booking.getBookingSeats().stream()
                        .map(bs -> bs.getSeat().getId())
                        .toList();
                seatLockService.releaseAllSeatsForToken(
                        booking.getShowtime().getId(), seatIds, booking.getLockToken());
            }
        }

        bookingRepository.saveAll(abandonedBookings);

        log.info("Cleaned up {} abandoned PENDING booking(s)", abandonedBookings.size());
    }
}
