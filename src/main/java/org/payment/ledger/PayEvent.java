package org.payment.ledger;

import lombok.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.UUID;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
public class PayEvent {
    private UUID payEventId; // client generated id for idempotency
    private Timestamp timestamp;
    private String actorId; // current user
    private String counterActorId; // counterparty
    private BigDecimal amount;
    private String signature;
}
