-- KEYS: kilitlenecek koltuk key'leri (ör: seat:lock:showtimeId:seatId)
-- ARGV[1]: lockToken
-- ARGV[2]: TTL (saniye)

for i, key in ipairs(KEYS) do
    if redis.call("EXISTS", key) == 1 then
        return 0
    end
end

for i, key in ipairs(KEYS) do
    redis.call("SET", key, ARGV[1], "EX", ARGV[2])
end

return 1