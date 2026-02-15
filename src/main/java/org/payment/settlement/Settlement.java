package org.payment.settlement;

import lombok.*;
import org.payment.constant.Settlement.SettlementStatusEnum;

import java.math.BigDecimal;
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
    private BigDecimal amount;
    private SettlementStatusEnum status; // PENDING, SETTLED, FAILED, ATTENTION_REQUIRED
    private Timestamp createdAt;
    private Timestamp updatedAt;
}
