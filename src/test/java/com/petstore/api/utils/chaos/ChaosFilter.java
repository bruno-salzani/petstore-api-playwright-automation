package com.petstore.api.utils.chaos;

import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.builder.ResponseBuilder;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;

public class ChaosFilter implements Filter {

    private static final ThreadLocal<AtomicBoolean> FAILED_ONCE = ThreadLocal.withInitial(() -> new AtomicBoolean(false));

    @Override
    public Response filter(FilterableRequestSpecification req, FilterableResponseSpecification res, FilterContext ctx) {
        String prop = System.getProperty("enable.chaos");
        String env = System.getenv("ENABLE_CHAOS");
        boolean enabled = "true".equalsIgnoreCase(prop) || "true".equalsIgnoreCase(env);
        if (enabled && !FAILED_ONCE.get().get()) {
            FAILED_ONCE.get().set(true);
            int status = ThreadLocalRandom.current().nextBoolean() ? 503 : 429;
            return new ResponseBuilder()
                    .setStatusCode(status)
                    .setBody("Chaos Induced Failure")
                    .setContentType("text/plain")
                    .build();
        }
        return ctx.next(req, res);
    }
}
