package com.acme.fx.client;

import com.acme.commons.http.AcmeHttpClient;
import com.acme.fx.model.FxQuote;
import org.springframework.stereotype.Component;

@Component
public class RateProviderClient {
    private final AcmeHttpClient httpClient;

    public RateProviderClient(AcmeHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public FxQuote fetchQuote(String pair) {
        return httpClient.get("/v1/quotes/" + pair, FxQuote.class);
    }
}
