package org.payment.ledger;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.payment.consVar.ConstantEnum.Direction;
import org.payment.consVar.ConstantEnum.Bucket;
import org.payment.escrow.EscrowInteralRequest;
import org.payment.util.CurrencyConverter;
import org.payment.util.CurrencyFactory;
import java.time.Instant;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
@Slf4j
public class ValidatorEngine {

    @Inject
    LedgerRepository ledgerRepository;

    public List<LedgerEvent> validate(PayEvent event) throws SQLException,RuntimeException {
        UUID payEventId = event.getPayEventId();
        if(ledgerRepository.eventExists(payEventId)){
            throw new RuntimeException("Duplicate event: " + event.getPayEventId());
        }
        long balance = ledgerRepository.getBalance(event.getActorId(), Bucket.USER_ESCROW);
        if (balance < 0) {
            throw new RuntimeException("Insufficient DEBIT balance for event: " + event.getPayEventId());
        }
        List<LedgerEvent> ledgerEvents = createLedgerEvents(event);
        log.debug("Validation successful for event: {}, created ledger events: {}, {}", event.getPayEventId(), ledgerEvents.get(0).getEventId(), ledgerEvents.get(1).getEventId());
        return ledgerEvents;
    }

    public List<LedgerEvent> validateLoadBalanceEvent(EscrowInteralRequest event) throws SQLException,RuntimeException {
        UUID loadBalanceEventId = event.getReferenceId();
        if(ledgerRepository.eventExists(loadBalanceEventId)){
            throw new RuntimeException("Duplicate event: " + event.getReferenceId());
        }
        List<LedgerEvent> ledgerEvents = createLedgerEventsForLoadBalance(event);
        log.debug("Validation successful for load balance event: {}, created ledger events: {}, {}", event.getReferenceId(), ledgerEvents.get(0).getEventId(), ledgerEvents.get(1).getEventId());
        return ledgerEvents;
    }

    private List<LedgerEvent> createLedgerEvents(PayEvent event){
        log.debug("Creating ledger events for pay event: {}", event.getPayEventId());
        CurrencyConverter converter = CurrencyFactory.defaultCurrency(); //INR only
        LedgerEvent debitEvent = LedgerEvent.builder()
                .eventId(UUID.randomUUID())
                .timestamp(event.getTimestamp())
                .actorId(event.getActorId())
                .bucket(Bucket.USER_ESCROW)
                .direction(Direction.DEBIT)
                .amount(event.getAmount())
                .reference(event.getPayEventId())
                .signature(event.getSignature())
                .build();

        log.debug("Created debit ledger event: {}", debitEvent.getEventId());
        LedgerEvent creditEvent = LedgerEvent.builder()
                .eventId(UUID.randomUUID())
                .timestamp(event.getTimestamp())
                .actorId(event.getCounterActorId())
                .bucket(Bucket.USER_PENDING)
                .direction(Direction.CREDIT)
                .amount(event.getAmount())
                .reference(event.getPayEventId())
                .signature(event.getSignature())
                .build();
        log.debug("Created credit ledger event: {}", creditEvent.getEventId());
        return List.of(debitEvent, creditEvent);
    }

    private List<LedgerEvent> createLedgerEventsForLoadBalance(EscrowInteralRequest event){
        log.debug("Creating ledger event for load balance event: {}", event.getReferenceId());
        LedgerEvent loadBalanceEvent = LedgerEvent.builder()
                .eventId(UUID.randomUUID())
                .timestamp(Instant.now())
                .actorId(event.getActorId())
                .bucket(Bucket.USER_ESCROW)
                .direction(Direction.CREDIT)
                .amount(event.getLoadAmountInMinorUnit())
                .reference(event.getReferenceId())
                .signature(event.getSignature())
                .build();

        LedgerEvent debitEvent = LedgerEvent.builder()
                .eventId(UUID.randomUUID())
                .timestamp(Instant.now())
                .actorId("EXTERNAL_PAYMENT_GATEWAY")
                .bucket(Bucket.USER_ESCROW)
                .direction(Direction.DEBIT)
                .amount(event.getLoadAmountInMinorUnit())
                .reference(event.getReferenceId())
                .signature(event.getSignature())
                .build();

        log.debug("Created load balance ledger event: {}", loadBalanceEvent.getEventId());
        return List.of(loadBalanceEvent, debitEvent);
    }

}
