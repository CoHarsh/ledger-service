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
            // In real implementation, we would have more complex logic here
            // For example, we might check if the settlement already exists, or if the actors are valid
            // We might also have some business rules around the amount or the timing of the settlement
            // For this example, we will just insert the settlement into the database
            settlementRepository.insertSettlement(settlement);
            log.debug("Successfully created settlement: {}", settlement);
        } catch (Exception e) {
            log.error("Error creating settlement: {}", settlement, e);
            throw new RuntimeException(e);
        }
    }
}
