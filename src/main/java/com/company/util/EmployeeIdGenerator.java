package com.company.util;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Demonstrates custom bean creation with {@code @Bean} (AppConfig) and
 * {@code @Scope("prototype")} — every lookup creates a new instance.
 * <p>
 * The database (IDENTITY) remains the real PK generator; this utility only
 * produces a human-friendly registration reference code.
 */
public class EmployeeIdGenerator {

    private static final AtomicLong SEQUENCE = new AtomicLong(1000);
    private final String emailFrom;

    public EmployeeIdGenerator(String emailFrom) {
        this.emailFrom = emailFrom;
    }

    @PostConstruct
    public void init() {
        System.out.println("[EmployeeIdGenerator] Initialised (@PostConstruct, prototype bean)");
    }

    @PreDestroy
    public void destroy() {
        System.out.println("[EmployeeIdGenerator] Destroyed (@PreDestroy) -- "
                + "NOTE: for prototype beans this callback is not invoked by the container");
    }

    public String generateReferenceCode() {
        return "EMP-REF-" + SEQUENCE.incrementAndGet() + " (" + emailFrom + ")";
    }
}
