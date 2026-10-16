package com.acme.fx.model;

import java.math.BigDecimal;

public record FxRate(String from, String to, BigDecimal rate) {
}
