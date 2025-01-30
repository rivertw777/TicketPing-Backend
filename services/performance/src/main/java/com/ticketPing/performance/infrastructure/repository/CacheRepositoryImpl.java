package com.ticketPing.performance.infrastructure.repository;

import com.ticketPing.performance.common.exception.SeatExceptionCase;
import com.ticketPing.performance.domain.model.entity.SeatCache;
import com.ticketPing.performance.domain.repository.CacheRepository;
import com.ticketPing.performance.infrastructure.service.LuaScriptService;
import exception.ApplicationException;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RBucket;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static caching.enums.RedisKeyPrefix.AVAILABLE_SEATS;
import static com.ticketPing.performance.common.constants.SeatConstants.*;

@Service
@RequiredArgsConstructor
public class CacheRepositoryImpl implements CacheRepository {

    private final RedissonClient redissonClient;
    private final LuaScriptService luaScriptService;

    public void cacheSeats(UUID scheduleId, Map<String, SeatCache> seatMap, Duration ttl) {
        String key = SEAT_CACHE_KEY +":{" + scheduleId + "}";
        RMap<String, SeatCache> seatCache = redissonClient.getMap(key, JsonJacksonCodec.INSTANCE);
        seatCache.putAll(seatMap);
        seatCache.expire(ttl);
    }

    public Map<String, SeatCache> getSeatCaches(UUID scheduleId) {
        String key = SEAT_CACHE_KEY +":{" + scheduleId + "}";
        RMap<String, SeatCache> seatCacheRMap = redissonClient.getMap(key, JsonJacksonCodec.INSTANCE);
        return seatCacheRMap.readAllMap();
    }

    public SeatCache getSeatCache(UUID scheduleId, UUID seatId) {
        String seatKey = SEAT_CACHE_KEY +":{" + scheduleId + "}";
        RMap<String, SeatCache> seatCacheMap = redissonClient.getMap(seatKey, JsonJacksonCodec.INSTANCE);
        return Optional.ofNullable(seatCacheMap.get(seatId.toString()))
                .orElseThrow(() -> new ApplicationException(SeatExceptionCase.SEAT_CACHE_NOT_FOUND));
    }

    public void putSeatCache(SeatCache seatCache, UUID scheduleId, UUID seatId) {
        String seatKey = SEAT_CACHE_KEY +":{" + scheduleId + "}";
        RMap<String, SeatCache> seatCacheMap = redissonClient.getMap(seatKey, JsonJacksonCodec.INSTANCE);
        seatCacheMap.put(seatId.toString(), seatCache);
    }

    public void preReserveSeatCache(UUID scheduleId, UUID seatId, UUID userId) {
        luaScriptService.preReserveSeat(scheduleId, seatId, userId);
    }

    public String getPreReserveUserId(UUID scheduleId, UUID seatId) {
        String ttlKey = PRE_RESERVE_SEAT_KEY + ":{" + scheduleId + "}:" + seatId;
        RBucket<String> bucket = redissonClient.getBucket(ttlKey);
        return Optional.ofNullable(bucket.get())
                .orElseThrow(() -> new ApplicationException(SeatExceptionCase.TTL_NOT_EXIST));
    }

    public void extendPreReserveTTL(UUID scheduleId, UUID seatId, Duration ttl) {
        String ttlKey = PRE_RESERVE_SEAT_KEY + ":{" + scheduleId + "}:" + seatId;
        RBucket<Object> bucket = redissonClient.getBucket(ttlKey);

        boolean success = bucket.expire(ttl);
        if (!success) {
            throw new ApplicationException(SeatExceptionCase.TTL_NOT_EXIST);
        }
    }

    public void deletePreReserveTTL(UUID scheduleId, UUID seatId) {
        String ttlKey = PRE_RESERVE_SEAT_KEY + ":{" + scheduleId + "}:" + seatId;
        RBucket<Object> bucket = redissonClient.getBucket(ttlKey);

        boolean deleted = bucket.delete();
        if (!deleted) {
            throw new ApplicationException(SeatExceptionCase.TTL_NOT_EXIST);
        }
    }

    public void cacheAvailableSeats(UUID performanceId, long availableSeats) {
        String key = AVAILABLE_SEATS.getValue() + performanceId;
        redissonClient.getBucket(key).set(availableSeats);
    }
}
