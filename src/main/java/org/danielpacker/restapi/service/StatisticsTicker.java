package org.danielpacker.restapi.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

// This class is only used as a timer.
@Component
public class StatisticsTicker {

    // Requires a constant, so using Statistics field.
    @Scheduled(fixedRate = Statistics.REFRESH_RATE_MS)
    public void doTick() {
        Statistics.tickBuckets();
    }
}