package org.payment.escrow;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.payment.consVar.EscrowFundStatus;
import org.payment.consVar.SettlementType;
import org.payment.consVar.settlement.SettlementStatusEnum;
import org.payment.ledger.LedgerEvent;
import org.payment.ledger.LedgerRepository;
import org.payment.ledger.ValidatorEngine;
import org.payment.settlement.Settlement;
import org.payment.settlement.SettlementService;
import org.payment.util.CurrencyConverter;
import org.payment.util.CurrencyFactory;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import java.math.BigInteger;

@Slf4j
@ApplicationScoped
public class EscrowService {

    @Inject SettlementService settlementService;
    @Inject LedgerRepository ledgerRepository;
    @Inject ValidatorEngine validatorEngine;

    @Transactional
    public EscrowResponse loadBalance(@Valid EscrowRequest request) {
        EscrowInteralRequest interalRequest;
        try {
            interalRequest = adaptToInternalRequest(request);
            validateRequest(interalRequest);
        } catch (Exception e) {
            log.error("Error validating request: {}", request, e);
            throw new RuntimeException("Invalid request: " + e.getMessage());
        }
        try{
            settlementService.createSettlement(escrowRequestToSettlement(interalRequest));
        } catch (Exception e) {
            log.error("Error creating settlement for request: {}", interalRequest, e);
            throw new RuntimeException("Error creating settlement: " + e.getMessage());
        }
        boolean loadBalanceResult = false;
        try {
            loadBalanceResult = externalLoadBalance(interalRequest);
        } catch (Exception e) {
            log.error("Error loading balance for request: {}", interalRequest, e);
            throw new RuntimeException("Error loading balance: " + e.getMessage());
        }
        if(loadBalanceResult) {
            try{
                List<LedgerEvent> ledgerEventList = validatorEngine.validateLoadBalanceEvent(interalRequest);
                ledgerRepository.insertLedgerEvents(ledgerEventList);
                settlementService.updateSettlementStatus(List.of(escrowRequestToSettlement(interalRequest)), SettlementStatusEnum.SETTLED);
            } catch (SQLException e) {
                log.error("Error inserting ledger events for request: {}", interalRequest, e);
                throw new RuntimeException("Error inserting ledger events: " + e.getMessage());
            }
        }
        return EscrowResponse.builder()
                .actorId(interalRequest.getActorId())
                .balance(request.getLoadAmount())
                .status(EscrowFundStatus.ADDED)
                .message("Successfully loaded balance for actorId: " + interalRequest.getActorId())
                .build();
    }

    private void validateRequest(EscrowInteralRequest interalRequest) throws RuntimeException {
        if(interalRequest.getLoadAmountInMinorUnit().compareTo(BigInteger.ZERO) <= 0) {
            log.error("Invalid load amount: {}", interalRequest.getLoadAmountInMinorUnit());
            throw new RuntimeException("Load amount must be greater than 0");
        }
        if(interalRequest.getSignature() == null || interalRequest.getSignature().isEmpty()) {
            log.error("Missing signature for request: {}", interalRequest);
            throw new RuntimeException("Signature is required");
        }
        if(interalRequest.getActorId() == null || interalRequest.getActorId().isEmpty()) {
            log.error("Missing actorId for request: {}", interalRequest);
            throw new RuntimeException("ActorId is required");
        }
    }

    private EscrowInteralRequest adaptToInternalRequest(EscrowRequest request) {
        CurrencyConverter converter = CurrencyFactory.defaultCurrency();
        BigInteger loadAmountInMinorUnit = converter.toMinorUnit(request.getLoadAmount());
        EscrowInteralRequest internalRequest = new EscrowInteralRequest();
        internalRequest.setActorId(request.getActorId());
        internalRequest.setLoadAmountInMinorUnit(loadAmountInMinorUnit);
        internalRequest.setSignature(request.getSignature());
        internalRequest.setReferenceId(UUID.randomUUID());
        return internalRequest;
    }

    private boolean externalLoadBalance(EscrowInteralRequest internalRequest) {
        //TODO: Implement the actual external call to load balance
        if(Math.random() < 0.8) {
            log.debug("Successfully loaded balance for request: {}", internalRequest);
            return true;
        } else {
            log.error("Failed to load balance for request: {}", internalRequest);
            throw new RuntimeException("Failed to load balance from external payment gateway");
        }
    }

    private Settlement escrowRequestToSettlement(EscrowInteralRequest internalRequest) {
        return Settlement.builder()
                .settlementId(UUID.randomUUID())
                .reference(internalRequest.getReferenceId())
                .fromActorId("EXTERNAL_PAYMENT_GATEWAY")
                .toActorId(internalRequest.getActorId())
                .amount(internalRequest.getLoadAmountInMinorUnit())
                .status(SettlementStatusEnum.PROCESSING)
                .settlementType(SettlementType.FUNDING)
                .build();
    }

}
