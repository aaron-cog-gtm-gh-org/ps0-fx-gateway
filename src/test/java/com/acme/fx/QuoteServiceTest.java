package com.acme.fx;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.acme.commons.http.AcmeHttpClient;
import com.acme.commons.http.HttpClientFactory;
import com.acme.fx.client.RateProviderClient;
import com.acme.fx.model.FxQuote;
import com.acme.fx.service.QuoteService;
import com.acme.fx.service.UnsupportedCurrencyPairException;
import com.github.tomakehurst.wiremock.WireMockServer;
import java.math.BigDecimal;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class QuoteServiceTest {
    private static final String QUOTE_JSON = """
            {
              "pair": "EURUSD",
              "bid": 1.0825,
              "ask": 1.0850,
              "timestamp": "2024-10-15T10:15:30Z"
            }
            """;

    private WireMockServer server;
    private AcmeHttpClient httpClient;
    private QuoteService quoteService;

    @BeforeEach
    void setUp() {
        server = new WireMockServer(com.github.tomakehurst.wiremock.core.WireMockConfiguration.options().dynamicPort());
        server.start();
        httpClient = HttpClientFactory.create(server.baseUrl());
        quoteService = new QuoteService(new RateProviderClient(httpClient));
    }

    @AfterEach
    void tearDown() {
        httpClient.close();
        server.stop();
    }

    @Test
    void returnsProviderQuote() {
        server.stubFor(get(urlEqualTo("/v1/quotes/EURUSD"))
                .willReturn(aResponse().withStatus(200).withHeader("Content-Type", "application/json")
                        .withBody(QUOTE_JSON)));

        assertThat(quoteService.quote("EUR", "USD"))
                .isEqualTo(new FxQuote("EURUSD", new BigDecimal("1.0825"), new BigDecimal("1.0850"),
                        "2024-10-15T10:15:30Z"));
    }

    @Test
    void convertsAmountUsingAskRateAndHalfEvenRounding() {
        server.stubFor(get(urlEqualTo("/v1/quotes/EURUSD"))
                .willReturn(aResponse().withStatus(200).withHeader("Content-Type", "application/json")
                        .withBody(QUOTE_JSON)));

        assertThat(quoteService.convert(new BigDecimal("100.00"), "EUR", "USD"))
                .isEqualByComparingTo("108.50");
    }

    @Test
    void mapsMissingQuoteToUnsupportedCurrencyPair() {
        server.stubFor(get(urlEqualTo("/v1/quotes/EURUSD")).willReturn(aResponse().withStatus(404)));

        assertThatThrownBy(() -> quoteService.quote("EUR", "USD"))
                .isInstanceOf(UnsupportedCurrencyPairException.class);
    }

    @Test
    void rejectsInvalidCurrencyCodeBeforeMakingHttpCall() {
        assertThatThrownBy(() -> quoteService.quote("eur", "USD"))
                .isInstanceOf(IllegalArgumentException.class);

        assertThat(server.getAllServeEvents()).isEmpty();
    }
}
