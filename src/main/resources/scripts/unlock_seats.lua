-- KEYS: koltuk key'leri
-- ARGV[1]: lockToken

for i, key in ipairs(KEYS) do
    if redis.call("GET", key) == ARGV[1] then
        redis.call("DEL", key)
    end
end

return 1