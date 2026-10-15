package com.acme.fx.service;

public class UnsupportedCurrencyPairException extends RuntimeException {
    public UnsupportedCurrencyPairException(String pair) {
        super("Unsupported currency pair: " + pair);
    }
}
