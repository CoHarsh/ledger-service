package org.payment.settlement;

import lombok.*;
import org.payment.consVar.SettlementType;
import org.payment.consVar.settlement.SettlementStatusEnum;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@ToString
public class Settlement {
    private UUID settlementId;
    private UUID reference;
    private String fromActorId;
    private String toActorId;
    private BigInteger amount;
    private SettlementStatusEnum status; // PENDING, SETTLED, FAILED, ATTENTION_REQUIRED
    private SettlementType settlementType; // PAYMENT, FUNDING, WITHDRAWAL
    private Timestamp createdAt;
    private Timestamp updatedAt;
}
