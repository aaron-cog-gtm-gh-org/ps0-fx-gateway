package com.acme.fx;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.acme.commons.http.AcmeHttpException;
import com.acme.fx.model.FxQuote;
import com.acme.fx.service.QuoteService;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(RateController.class)
class RateControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private QuoteService quoteService;

    @Test
    void returnsRateInPaymentsServiceFormat() throws Exception {
        when(quoteService.quote("EUR", "USD"))
                .thenReturn(new FxQuote("EURUSD", new BigDecimal("1.0825"),
                        new BigDecimal("1.0850"), "2024-10-15T10:15:30Z"));

        mockMvc.perform(get("/v1/rates/EUR/USD"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.from").value("EUR"))
                .andExpect(jsonPath("$.to").value("USD"))
                .andExpect(jsonPath("$.rate").value(1.085))
                .andExpect(jsonPath("$.pair").doesNotExist())
                .andExpect(jsonPath("$.bid").doesNotExist())
                .andExpect(jsonPath("$.ask").doesNotExist())
                .andExpect(jsonPath("$.timestamp").doesNotExist());
    }

    @Test
    void mapsUpstreamFailureToBadGateway() throws Exception {
        when(quoteService.quote("EUR", "USD"))
                .thenThrow(new AcmeHttpException(503, "Rate provider unavailable"));

        mockMvc.perform(get("/v1/rates/EUR/USD"))
                .andExpect(status().isBadGateway())
                .andExpect(jsonPath("$.message").value("Rate provider request failed"));
    }
}
