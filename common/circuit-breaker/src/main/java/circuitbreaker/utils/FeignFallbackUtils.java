package circuitbreaker.utils;

import circuitbreaker.exception.CircuitBreakerErrorCase;
import exception.ApplicationException;
import feign.FeignException;
import feign.RetryableException;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;

public class FeignFallbackUtils {

    private FeignFallbackUtils() {
    }

    public static <T> T handleFallback(Throwable cause) {
        if (cause instanceof CallNotPermittedException) {
            throw new ApplicationException(CircuitBreakerErrorCase.SERVICE_IS_OPEN);
        }
        else if (
                cause instanceof FeignException.GatewayTimeout ||
                        cause instanceof FeignException.ServiceUnavailable ||
                        cause instanceof FeignException.BadGateway ||
                        cause instanceof FeignException.TooManyRequests ||
                        cause instanceof RetryableException
        ) {
            throw new ApplicationException(CircuitBreakerErrorCase.SERVICE_UNAVAILABLE);
        }
        else if (cause instanceof FeignException) {
            throw (FeignException) cause;
        } else {
            throw new ApplicationException(CircuitBreakerErrorCase.SERVICE_UNAVAILABLE);
        }
    }

}
