package org.payment.escrow;

import lombok.Data;

@Data
public class EscrowRequest {
    private String actorId;
    private float loadAmount;
    private String signature;
}

