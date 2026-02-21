package org.payment.ledger;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.payment.consVar.settlement.SettlementStatusEnum;
import org.payment.settlement.Settlement;
import org.payment.settlement.SettlementService;
import java.util.List;
import org.payment.ledger.PayEvent;


@ApplicationScoped
@Slf4j
public class LedgerService {

    @Inject
    ValidatorEngine validatorEngine;

    @Inject
    LedgerRepository ledgerRepository;

    @Inject
    SettlementService settlementService;

    @Transactional
    public void processBatch(List<PayEvent> events) {
        log.debug("Processing batch of {} events", events.size());
        try{
            for (PayEvent event : events) {
                process(event);
                log.debug("Successfully processed event: {}", event);
            }
        } catch (Exception e) {
            log.error("Error processing batch of events", e);
            throw new RuntimeException(e);
        }
    }

    private void process(PayEvent event) throws Exception {
        log.debug("Processing event: {}", event);
        List<LedgerEvent> ledgerEvents = validatorEngine.validate(event);
        if (ledgerEvents == null || ledgerEvents.isEmpty()) {
            throw new RuntimeException("Validation failed for event: " + event);
        }
        ledgerRepository.insertLedgerEvents(ledgerEvents);
        settlementService.createSettlement(payEventToSettlement(event));
    }

    private Settlement payEventToSettlement(PayEvent event) {
        return Settlement.builder()
                .settlementId(java.util.UUID.randomUUID())
                .reference(event.getPayEventId())
                .fromActorId(event.getActorId())
                .toActorId(event.getCounterActorId())
                .amount(event.getAmount())
                .status(SettlementStatusEnum.PENDING)
                .build();
    }

}
