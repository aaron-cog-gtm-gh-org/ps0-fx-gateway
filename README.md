# FX Gateway

The Acme Bank FX gateway provides foreign exchange pricing to internal payments and treasury workflows. It retrieves currency-pair quotes from the bank's rate provider and applies the quoted ask rate when calculating customer-side conversions.

## Rate provider integration

For a conversion from euros to US dollars, the gateway requests `GET /v1/quotes/EURUSD` from the configured rate provider. The quote response contains the pair, bid, ask, and pricing timestamp. Amounts returned by the conversion operation are rounded to two decimal places using half-even rounding.

An unavailable quote is reported as an unsupported currency pair when the provider returns HTTP 404. Other provider failures are surfaced to the caller.

## Configuration

| Key | Description |
| --- | --- |
| `acme.rate-provider.base-url` | Internal rate provider API base URL |
| `acme.rate-provider.timeout` | Connection and response timeout |

The default configuration targets `https://rates.internal.acmebank.example` with a five-second timeout. Override these values for the target environment.

## Build

Set `ARTIFACTORY_URL` to the Acme Artifactory `maven-virtual` repository and run:

```sh
ARTIFACTORY_URL=https://artifactory.internal.acmebank.example/artifactory/maven-virtual mvn -B -s .mvn/settings.xml verify
```
