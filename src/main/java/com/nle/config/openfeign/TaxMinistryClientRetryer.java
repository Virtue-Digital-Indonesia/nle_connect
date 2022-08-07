package com.nle.config.openfeign;

import feign.RetryableException;
import feign.Retryer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Slf4j
@Component
public class TaxMinistryClientRetryer implements Retryer {
    int attempt;
    long sleptForMillis;
    private long period;
    private long maxPeriod;
    private int maxAttempts;

    public TaxMinistryClientRetryer() {
    }

    @PostConstruct
    public void init() {
        this.period = 1000;
        this.maxPeriod = 3;
        this.maxAttempts = 3;
        this.attempt = 1;
    }

    // visible for testing;
    protected long currentTimeMillis() {
        return System.currentTimeMillis();
    }

    @Override
    public void continueOrPropagate(RetryableException e) {
        if (attempt++ >= maxAttempts) {
            throw e;
        }

        long interval;
        if (e.retryAfter() != null) {
            interval = e.retryAfter().getTime() - currentTimeMillis();
            if (interval > maxPeriod) {
                interval = maxPeriod;
            }
            if (interval < 0) {
                return;
            }
        } else {
            interval = nextMaxInterval();
        }
        try {
            Thread.sleep(interval);
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
            throw e;
        }
        sleptForMillis += interval;
        log.info(String.format("Retrying: %s  %d  times. Max: %d. Total requesting time %d", e.request().url(), attempt, maxAttempts, sleptForMillis));
    }

    long nextMaxInterval() {
        long interval = (long) (period * Math.pow(1.5, (double) attempt - 1));
        return Math.min(interval, maxPeriod);
    }

    @Override
    public Retryer clone() {
        return this;
    }
}
