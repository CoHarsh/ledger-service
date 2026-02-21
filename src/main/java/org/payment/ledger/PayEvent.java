package org.payment.ledger;

import lombok.*;

import java.math.BigInteger;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
public class PayEvent {
    private UUID payEventId; // client generated id for idempotency
    private Instant timestamp;
    private String actorId; // current user
    private String counterActorId; // counterparty
    private BigInteger amount;
    private String signature;
}
