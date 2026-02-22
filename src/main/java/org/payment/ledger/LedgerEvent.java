package org.payment.ledger;

import lombok.*;
import org.payment.consVar.ConstantEnum.Bucket;
import org.payment.consVar.ConstantEnum.Direction;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

@Getter
@ToString
@AllArgsConstructor
@Builder
public class LedgerEvent {
    public UUID eventId;
    public Instant timestamp;
    public String actorId;
    public Bucket bucket;
    public Direction direction;
    public BigInteger amount;
    public UUID reference;
    public String signature;
    public Timestamp createdAt; // populated by database, not set during creation
}
