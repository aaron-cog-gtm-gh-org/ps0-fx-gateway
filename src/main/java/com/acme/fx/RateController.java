package com.acme.fx;

import com.acme.commons.http.AcmeHttpException;
import com.acme.fx.model.FxQuote;
import com.acme.fx.model.FxRate;
import com.acme.fx.service.QuoteService;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/rates")
public class RateController {
    private final QuoteService quoteService;

    public RateController(QuoteService quoteService) {
        this.quoteService = quoteService;
    }

    @GetMapping("/{from}/{to}")
    public FxRate quote(@PathVariable String from, @PathVariable String to) {
        FxQuote quote = quoteService.quote(from, to);
        return new FxRate(from, to, quote.ask());
    }

    @ExceptionHandler(AcmeHttpException.class)
    public ResponseEntity<Map<String, String>> handleUpstreamFailure(AcmeHttpException exception) {
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body(Map.of("message", "Rate provider request failed"));
    }
}
