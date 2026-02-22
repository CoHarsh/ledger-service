package org.payment.settlement;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.payment.consVar.settlement.SettlementStatusEnum;

import java.util.List;
import java.util.UUID;

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

    public void updateSettlementStatus(List<Settlement> settlements, SettlementStatusEnum status) throws RuntimeException {
        log.debug("Updating settlement status to {} for settlements: {}", status, settlements);
        List<UUID> settlementIds = settlements.stream().map(Settlement::getSettlementId).toList();
        try {
            settlementRepository.updateSettlementStatus(settlementIds, status);
            log.debug("Successfully updated settlement status to {} for settlements: {}", status, settlements);
        } catch (Exception e) {
            log.error("Error updating settlement status to {} for settlements: {}", status, settlements, e);
            throw new RuntimeException(e);
        }
    }
}
