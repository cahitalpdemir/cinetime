-- KEYS: koltuk key'leri
-- ARGV[1]: lockToken
-- ARGV[2]: yeni TTL (saniye)

for i, key in ipairs(KEYS) do
    if redis.call("GET", key) ~= ARGV[1] then
        return 0
    end
end

for i, key in ipairs(KEYS) do
    redis.call("EXPIRE", key, ARGV[2])
end

return 1