package com.petstore.api.core;

import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Predicate;
import java.util.function.Supplier;

public final class HttpRetry {

    private HttpRetry() {
    }

    public static <T> T executeWithRetry(Supplier<T> supplier,
                                         Predicate<T> retryIf,
                                         int retryCount,
                                         long baseDelayMs) {
        int attempts = Math.max(0, retryCount) + 1;
        RuntimeException lastException = null;
        for (int i = 0; i < attempts; i++) {
            try {
                T result = supplier.get();
                if (retryIf != null && retryIf.test(result) && i < attempts - 1) {
                    sleep(expBackoffWithJitter(i, baseDelayMs));
                    continue;
                }
                return result;
            } catch (RuntimeException e) {
                lastException = e;
                if (i < attempts - 1) {
                    sleep(expBackoffWithJitter(i, baseDelayMs));
                }
            }
        }
        throw lastException;
    }

    public static long expBackoffWithJitter(int attempt, long baseDelayMs) {
        long exp = (long) Math.min(10_000, baseDelayMs * Math.pow(2, attempt));
        long jitter = ThreadLocalRandom.current().nextLong(0, Math.max(1, exp / 2));
        return exp + jitter;
    }

    private static void sleep(long delayMs) {
        if (delayMs <= 0) {
            return;
        }
        try {
            Thread.sleep(delayMs);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
        }
    }
}
