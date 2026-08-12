-- KEYS[1]: seat:lock:{showtimeId}:{seatId}  -> kilitlenecek YENİ koltuğun key'i
-- KEYS[2]: lock:owner:{lockToken}           -> bu token'a ait koltukların SET'i
-- ARGV[1]: lockToken
-- ARGV[2]: TTL (saniye)
-- ARGV[3]: showtimeId (yeni key'leri kurmak için)
-- ARGV[4]: yeni koltuğun seatId'si

local seatKey = KEYS[1]
local ownerSetKey = KEYS[2]
local token = ARGV[1]
local ttl = tonumber(ARGV[2])
local showtimeId = ARGV[3]
local newSeatId = ARGV[4]

-- 1. ADIM: Bu token'a daha önce bağlanmış koltuklar varsa, hepsinin HÂLÂ
--    bu token'a ait olduğunu doğrula
local existingSeatIds = redis.call("SMEMBERS", ownerSetKey)

for i, sid in ipairs(existingSeatIds) do
    local key = "seat:lock:" .. showtimeId .. ":" .. sid
    local owner = redis.call("GET", key)
    if owner ~= token then
        -- Mevcut koltuklardan biri artık bize ait değil -> tüm işlemi reddet
        return 0
    end
end

-- 2. ADIM: Yeni istenen koltuk başkasına ait mi kontrol et
local currentOwnerOfNewSeat = redis.call("GET", seatKey)
if currentOwnerOfNewSeat and currentOwnerOfNewSeat ~= token then
    return 0
end

-- 3. ADIM: Yeni koltuğu kilitle
redis.call("SET", seatKey, token, "EX", ttl)

-- 4. ADIM: Owner set'ine ekle, set'in kendi TTL'ini yenile
redis.call("SADD", ownerSetKey, newSeatId)
redis.call("EXPIRE", ownerSetKey, ttl)

-- 5. ADIM: Artık set'teki TÜM koltukların (yeni dahil) TTL'ini yenile
local allSeatIds = redis.call("SMEMBERS", ownerSetKey)
for i, sid in ipairs(allSeatIds) do
    local key = "seat:lock:" .. showtimeId .. ":" .. sid
    redis.call("EXPIRE", key, ttl)
end

return 1