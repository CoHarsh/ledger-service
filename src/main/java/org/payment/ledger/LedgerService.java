package org.payment.ledger;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.payment.consVar.ConstantEnum.Bucket;
import org.payment.consVar.ConstantEnum.Direction;
import org.payment.consVar.EscrowFundStatus;
import org.payment.consVar.settlement.SettlementStatusEnum;
import org.payment.escrow.EscrowRequest;
import org.payment.escrow.EscrowResponse;
import org.payment.settlement.Settlement;
import org.payment.settlement.SettlementService;
import org.payment.util.CurrencyConverter;
import org.payment.util.CurrencyFactory;

import java.util.List;


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

    public EscrowResponse getEscrowBalance(String actorId) {
        log.debug("Retrieving escrow balance for actorId: {}", actorId);
        CurrencyConverter converter = CurrencyFactory.defaultCurrency();
            try {
                long balance = ledgerRepository.getBalance(actorId, Bucket.USER_ESCROW);
                return EscrowResponse.builder()
                        .actorId(actorId)
                        .balance(converter.toMajorUnit(balance))
                        .message("Successfully retrieved escrow balance for actorId: " + actorId)
                        .status(EscrowFundStatus.FETCHED)
                        .currency(converter.getCurrencyCode())
                        .build();
            } catch (Exception e) {
                log.error("Error retrieving escrow balance for actorId: {}", actorId, e);
                throw new RuntimeException(e);
            }
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
