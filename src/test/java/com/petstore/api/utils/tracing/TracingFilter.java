package com.petstore.api.utils.tracing;

import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;

import java.util.UUID;

public class TracingFilter implements Filter {

    private static final ThreadLocal<String> TRACEPARENT = ThreadLocal.withInitial(() -> {
        String traceId = UUID.randomUUID().toString().replace("-", "");
        String spanId = UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        return "00-" + traceId.substring(0, 32) + "-" + spanId + "-01";
    });

    @Override
    public Response filter(FilterableRequestSpecification requestSpec, FilterableResponseSpecification responseSpec, FilterContext ctx) {
        requestSpec.header("traceparent", TRACEPARENT.get());
        return ctx.next(requestSpec, responseSpec);
    }

    public static String currentTraceId() {
        String tp = TRACEPARENT.get();
        if (tp == null || tp.length() < 3) {
            return null;
        }
        // Format: 00-<traceId>-<spanId>-01
        String[] parts = tp.split("-");
        if (parts.length >= 3) {
            return parts[1];
        }
        return null;
    }
}
