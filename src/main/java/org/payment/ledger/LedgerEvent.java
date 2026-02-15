package org.payment.ledger;

import lombok.*;
import org.payment.constant.ConstantEnum.Bucket;
import org.payment.constant.ConstantEnum.Direction;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.UUID;

@Getter
@ToString
@AllArgsConstructor
@Builder
public class LedgerEvent {
    public UUID eventId;
    public Timestamp timestamp;
    public String actorId;
    public Bucket bucket;
    public Direction direction;
    public BigDecimal amount;
    public UUID reference;
    public String signature;
    public Timestamp createdAt; // populated by database, not set during creation
}
