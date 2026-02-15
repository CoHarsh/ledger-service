package org.payment.ledger;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@ToString
@Setter
public class PayEventRequest {
    public UUID payEventId; // client generated id for idempotency
    public long timestamp;
    public String actorId; // current user
    public String counterActorId; // counterparty
    public BigDecimal amount;
    public String signature;
}

