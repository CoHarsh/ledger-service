package org.payment.settlement;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

@ApplicationScoped
@Slf4j
public class SettlementService {

    @Inject
    SettlementRepository settlementRepository;

    public void createSettlement(Settlement settlement) throws RuntimeException {
        log.debug("Creating settlement: {}", settlement);
        try {
            settlementRepository.insertSettlement(settlement);
            log.debug("Successfully created settlement: {}", settlement);
        } catch (Exception e) {
            log.error("Error creating settlement: {}", settlement, e);
            throw new RuntimeException(e);
        }
    }
}
