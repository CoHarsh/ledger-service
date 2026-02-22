package org.payment.escrow;

import lombok.Builder;
import lombok.Data;
import org.payment.consVar.EscrowFundStatus;

@Data
@Builder
public class EscrowResponse {
    private String actorId;
    private EscrowFundStatus status;
    private String message;
    private float balance;
    private String currency;
}
