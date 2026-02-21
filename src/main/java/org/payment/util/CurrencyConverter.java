package org.payment.util;

import java.math.BigInteger;

public interface CurrencyConverter {

    BigInteger toMinorUnit(float majorAmount);      // ₹10.50 → 1050

    float toMajorUnit(BigInteger minorAmount);     // 1050 → ₹10.50

    String getCurrencyCode();
}