package org.payment.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

public class InrCurrencyConverter implements CurrencyConverter {

    private static final BigInteger HUNDRED = BigInteger.valueOf(100);

    @Override
    public BigInteger toMinorUnit(float majorAmount) {
        // ₹10.5 → 1050 paise
        return new BigDecimal(majorAmount)
                .multiply(new BigDecimal(HUNDRED))
                .setScale(0, RoundingMode.HALF_UP)
                .toBigIntegerExact();
    }

    @Override
    public float toMajorUnit(long minorAmount) {
        // 1050 paise → ₹10.50
        return new BigDecimal(minorAmount)
                .divide(new BigDecimal(HUNDRED), 2, RoundingMode.HALF_UP)
                .floatValue();
    }

    @Override
    public String getCurrencyCode() {
        return "INR";
    }
}