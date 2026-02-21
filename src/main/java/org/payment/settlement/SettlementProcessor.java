package org.payment.settlement;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@ApplicationScoped
public class SettlementProcessor {

    @Inject
    SettlementRepository repository;

    public void processBatch() {

        List<Settlement> records = repository.lockBatch(50);

        for (Settlement s : records) {
            try {
                log.info("Processing settlement: {}", s);
                process(s);
            } catch (Exception e) {
                log.error("Error processing settlement: {}", s, e);
            }
        }
    }

    private void process(Settlement s) {
        System.out.println("Processing: " + s.getSettlementId());
    }

}