-- KEYS[1]: seat:lock:{showtimeId}:{seatId}  -> kaldırılacak koltuğun key'i
-- KEYS[2]: lock:owner:{lockToken}           -> bu token'a ait koltukların SET'i
-- ARGV[1]: lockToken
-- ARGV[2]: kaldırılacak koltuğun seatId'si (set'ten çıkarmak için)

local seatKey = KEYS[1]
local ownerSetKey = KEYS[2]
local token = ARGV[1]
local seatId = ARGV[2]

-- Koltuğun gerçekten bu token'a ait olduğunu doğrula
local currentOwner = redis.call("GET", seatKey)

if currentOwner == token then
    -- Bize aitse: koltuk key'ini sil + set'ten bu koltuğu çıkar
    redis.call("DEL", seatKey)
    redis.call("SREM", ownerSetKey, seatId)
    return 1
end

-- Bize ait değilse (zaten süresi dolmuş, başkası almış vs.) hiçbir şeye dokunma
return 0