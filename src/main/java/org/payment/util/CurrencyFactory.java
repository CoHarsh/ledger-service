package org.payment.util;

import lombok.NoArgsConstructor;

public class CurrencyFactory {

    private CurrencyFactory() {}

    public static CurrencyConverter getConverter(String currencyCode) {

        if (currencyCode == null || currencyCode.equalsIgnoreCase("INR")) {
            return new InrCurrencyConverter();
        }

        throw new IllegalArgumentException("Unsupported currency: " + currencyCode);
    }

    public static CurrencyConverter defaultCurrency() {
        return new InrCurrencyConverter();
    }
}