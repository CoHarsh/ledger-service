package org.payment.ledger;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@ToString
@Setter
public class PayEventRequest {
    @NotNull
    public UUID payEventId; // client generated id for idempotency
    public long timestamp;
    public String actorId; // current user
    public String counterActorId; // counterparty
    @NotNull
    @Positive
    public float amount;
    public String signature;
}

