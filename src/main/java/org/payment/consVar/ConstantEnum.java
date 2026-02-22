package org.payment.consVar;

public class ConstantEnum {
        public enum Bucket {
            USER_ESCROW,
            USER_PENDING,
            EXTERNAL_PAYMENT_GATEWAY,
        }

        public enum Direction {
            CREDIT,
            DEBIT
        }
}
