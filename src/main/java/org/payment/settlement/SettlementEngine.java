package org.payment.settlement;

import io.quarkus.runtime.Startup;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

@ApplicationScoped
@Startup
@Slf4j
public class SettlementEngine {

    @Inject
    SettlementProcessor processor;

    @Scheduled(every = "30s", delayed = "10s", concurrentExecution = Scheduled.ConcurrentExecution.SKIP)
    void runSettlement() {
        log.debug("Starting settlement batch processing at {}", java.time.LocalDateTime.now());
        processor.processBatch();
        log.debug("Finished settlement batch processing at {}", java.time.LocalDateTime.now());
    }
}