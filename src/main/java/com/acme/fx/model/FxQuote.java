package com.acme.fx.model;

import java.math.BigDecimal;

public record FxQuote(String pair, BigDecimal bid, BigDecimal ask, String timestamp) {
}
