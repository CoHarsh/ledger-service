package org.payment.escrow;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigInteger;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
public class EscrowInteralRequest extends EscrowRequest {
    private BigInteger loadAmountInMinorUnit;
    private UUID referenceId;
}
