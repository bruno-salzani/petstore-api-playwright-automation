package com.petstore.api.listener;

import com.petstore.api.utils.tracing.TracingFilter;
import io.qameta.allure.Allure;
import io.qameta.allure.model.Link;
import io.qameta.allure.model.Label;
import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestIdentifier;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Instant;

public class MetricsListener implements TestExecutionListener {

    private long startTime;

    @Override
    public void executionStarted(TestIdentifier testIdentifier) {
        if (testIdentifier.isTest()) {
            startTime = System.currentTimeMillis();
            Allure.getLifecycle().getCurrentTestCase().ifPresent(uuid ->
                    Allure.getLifecycle().updateTestCase(uuid, tr -> tr.getLabels().add(new Label().setName("owner").setValue("Bruno Salzani")))
            );
        }
    }

    @Override
    public void executionFinished(TestIdentifier testIdentifier, TestExecutionResult testExecutionResult) {
        if (testIdentifier.isTest()) {
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;

            String testName = testIdentifier.getDisplayName();
            String status = testExecutionResult.getStatus().toString();

            String json = String.format("{\"timestamp\": \"%s\", \"test\": \"%s\", \"status\": \"%s\", \"duration_ms\": %d}%n",
                    Instant.now().toString(), testName, status, duration);

            try {
                Files.write(Paths.get("target/metrics.jsonl"), json.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (testExecutionResult.getStatus() == TestExecutionResult.Status.FAILED) {
                String base = firstNonBlank(System.getProperty("jaeger.baseUrl"), System.getenv("JAEGER_BASE_URL"), "http://localhost:16686");
                String traceId = TracingFilter.currentTraceId();
                if (traceId != null) {
                    String url = base.replaceAll("/+$", "") + "/trace/" + traceId;
                    var link = new Link().setName("View Trace in Jaeger").setType("jaeger").setUrl(url);
                    Allure.getLifecycle().getCurrentTestCase().ifPresent(uuid ->
                            Allure.getLifecycle().updateTestCase(uuid, tr -> tr.getLinks().add(link))
                    );
                }
            }
        }
    }

    private static String firstNonBlank(String... values) {
        for (String v : values) {
            if (v != null && !v.isBlank()) return v;
        }
        return null;
    }
}
