package com.ticketPing.queue_manage.common.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 환경 설정
 */
@Component
public class ConfigHolder {

    private static String tokenValueSecretKey;
    private static int workingQueueMaxSize;
    private static int initialWorkingQueueTokenTTL;
    private static int extendedWorkingQueueTokenTTL;

    @Value("${token-value.secret-key}")
    public void setSecretKey(String secretKey) {
        tokenValueSecretKey = secretKey;
    }

    @Value("${working-queue.max-size}")
    public void setWorkingQueueMaxSize(int maxSize) {
        workingQueueMaxSize = maxSize;
    }

    @Value("${working-queue.initial-token-ttl}")
    public void setInitialWorkingQueueTokenTTL(int tokenTTL) {
        initialWorkingQueueTokenTTL = tokenTTL;
    }

    @Value("${working-queue.extended-token-ttl}")
    public void setExtendedWorkingQueueTokenTTL(int tokenTTL) {
        extendedWorkingQueueTokenTTL = tokenTTL;
    }

    public static String tokenValueSecretKey() {
        return tokenValueSecretKey;
    }

    public static int workingQueueMaxSize() {
        return workingQueueMaxSize;
    }

    public static int initialWorkingQueueTokenTTL() {
        return initialWorkingQueueTokenTTL;
    }

    public static int extendedWorkingQueueTokenTTL() { return extendedWorkingQueueTokenTTL; }

}