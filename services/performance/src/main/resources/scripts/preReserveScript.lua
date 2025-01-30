local hashKey  = KEYS[1]
local ttlKey = KEYS[2]
local seatId = "\"" .. ARGV[1] .. "\""
local userId = ARGV[2]
local ttl = tonumber(ARGV[3])

local seatData = redis.call("HGET", hashKey, seatId)

if not seatData then
    return "SEAT_CACHE_NOT_FOUND"
end

local seatData = redis.call("HGET", hashKey, seatId)
local seatObj = cjson.decode(seatData)

if seatObj.seatStatus ~= "AVAILABLE" then
    return "SEAT_ALREADY_TAKEN"
end

seatObj.seatStatus = "HELD"
redis.call("HSET", hashKey, seatId, cjson.encode(seatObj))

redis.call("SET", ttlKey, userId)
redis.call("EXPIRE", ttlKey, ttl)

return "SUCCESS"