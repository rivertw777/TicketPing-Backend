package com.ticketPing.performance.infrastructure.service;

import com.ticketPing.performance.common.exception.SeatExceptionCase;
import exception.ApplicationException;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RScript;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.UUID;

import static com.ticketPing.performance.common.constants.SeatConstants.*;

@Service
@RequiredArgsConstructor
public class LuaScriptService {
    private final RedissonClient redissonClient;
    private final String preReserveScript;

    public void preReserveSeat(UUID scheduleId, UUID seatId, UUID  userId) {
        String hashKey = SEAT_CACHE_KEY +":{" + scheduleId + "}";
        String ttlKey = PRE_RESERVE_SEAT_KEY + ":{" + scheduleId + "}:" + seatId;

        String response = redissonClient.getScript(StringCodec.INSTANCE)
                .evalSha(
                        RScript.Mode.READ_WRITE,
                        preReserveScript,
                        RScript.ReturnType.VALUE,
                        Arrays.asList(hashKey, ttlKey),
                        seatId.toString(), userId.toString(), PRE_RESERVE_TTL
                );

        if (!response.equals("SUCCESS")) {
            throw new ApplicationException(SeatExceptionCase.getByValue(response));
        }
    }
}