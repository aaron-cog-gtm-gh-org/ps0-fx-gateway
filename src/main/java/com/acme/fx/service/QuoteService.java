package com.acme.fx.service;

import com.acme.commons.http.AcmeHttpException;
import com.acme.fx.client.RateProviderClient;
import com.acme.fx.model.FxQuote;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;
import org.springframework.stereotype.Service;

@Service
public class QuoteService {
    private final RateProviderClient rateProviderClient;

    public QuoteService(RateProviderClient rateProviderClient) {
        this.rateProviderClient = rateProviderClient;
    }

    public FxQuote quote(String baseCurrency, String quoteCurrency) {
        validateCurrency(baseCurrency);
        validateCurrency(quoteCurrency);
        String pair = baseCurrency + quoteCurrency;
        try {
            return rateProviderClient.fetchQuote(pair);
        } catch (AcmeHttpException e) {
            if (e.status() == 404) {
                throw new UnsupportedCurrencyPairException(pair);
            }
            throw e;
        }
    }

    public BigDecimal convert(BigDecimal amount, String from, String to) {
        return amount.multiply(quote(from, to).ask()).setScale(2, RoundingMode.HALF_EVEN);
    }

    private void validateCurrency(String currencyCode) {
        if (currencyCode == null || !currencyCode.matches("[A-Z]{3}")) {
            throw new IllegalArgumentException("Currency must be a three-letter uppercase ISO code");
        }
        try {
            Currency.getInstance(currencyCode);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Currency must be a three-letter uppercase ISO code", e);
        }
    }
}
