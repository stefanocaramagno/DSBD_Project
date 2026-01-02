package flights.datacollector.client;

import flights.datacollector.client.dto.OpenSkyFlightDto;
import flights.datacollector.observability.OpenSkyClientMetrics;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Client per il servizio REST di OpenSky.
 * Incapsula le chiamate HTTP e applica un Circuit Breaker (Resilience4j)
 * per proteggere il microservizio nel caso di malfunzionamenti esterni.
 */
@Component
public class OpenSkyClient {

    private static final Logger log = LoggerFactory.getLogger(OpenSkyClient.class);

    private final RestTemplate restTemplate;
    private final String apiBaseUrl;

    private final OpenSkyClientMetrics metrics;

    public OpenSkyClient(RestTemplateBuilder restTemplateBuilder,
                         @Value("${opensky.api-base-url}") String apiBaseUrl,
                         @Value("${opensky.timeout-seconds:5}") long timeoutSeconds,
                         OpenSkyClientMetrics metrics) {
        this.restTemplate = restTemplateBuilder
                .setConnectTimeout(Duration.ofSeconds(timeoutSeconds))
                .setReadTimeout(Duration.ofSeconds(timeoutSeconds))
                .build();
        this.apiBaseUrl = apiBaseUrl;
        this.metrics = metrics;
    }

    /**
     * Recupera i voli in arrivo per un aeroporto e una finestra temporale.
     */
    public List<OpenSkyFlightDto> getArrivals(String airportIcao, Instant begin, Instant end) {
        return fetchFlights("/flights/arrival", airportIcao, begin, end);
    }

    /**
     * Recupera i voli in partenza per un aeroporto e una finestra temporale.
     */
    public List<OpenSkyFlightDto> getDepartures(String airportIcao, Instant begin, Instant end) {
        return fetchFlights("/flights/departure", airportIcao, begin, end);
    }

    /**
     * Metodo centrale protetto da Circuit Breaker.
     * Se OpenSky non risponde o restituisce errori, Resilience4j può aprire il circuito
     * e le chiamate successive verranno cortocircuitate, cadendo nel fallback.
     */
    @CircuitBreaker(name = "opensky", fallbackMethod = "fallbackFetchFlights")
    protected List<OpenSkyFlightDto> fetchFlights(String path,
                                                  String airportIcao,
                                                  Instant begin,
                                                  Instant end) {
        URI uri = UriComponentsBuilder.fromHttpUrl(apiBaseUrl)
                .path(path)
                .queryParam("airport", airportIcao)
                .queryParam("begin", begin.getEpochSecond())
                .queryParam("end", end.getEpochSecond())
                .build(true)
                .toUri();

        // Metriche: conta la richiesta e misura la latenza dell'operazione.
        metrics.incrementRequests();
        final long startNs = System.nanoTime();

        try {
            log.debug("Chiamata OpenSky: uri={} (airport={}, begin={}, end={})",
                    uri, airportIcao, begin, end);

            ResponseEntity<OpenSkyFlightDto[]> response =
                    restTemplate.getForEntity(uri, OpenSkyFlightDto[].class);

            OpenSkyFlightDto[] body = response.getBody();
            if (body == null || body.length == 0) {
                log.debug("OpenSky ha restituito una lista vuota (airport={}, path={}, status={})",
                        airportIcao, path, response.getStatusCode());
                return Collections.emptyList();
            }

            return Arrays.asList(body);
        }
        // Caso speciale: 404 Not Found → per OpenSky su questi endpoint significa "nessun volo"
        catch (HttpClientErrorException.NotFound ex) {
            log.info("Nessun volo trovato su OpenSky (404) per airport={} path={} window=[{}, {}]. " +
                            "Tratto il caso come '0 voli' e ritorno lista vuota.",
                    airportIcao, path, begin, end);
            return Collections.emptyList();
        }
        // Altri 4xx / 5xx → errori reali, da far gestire al Circuit Breaker
        catch (HttpStatusCodeException ex) {
            log.warn("Errore HTTP da OpenSky per airport={} path={} window=[{}, {}]. " +
                            "Status={} Body={}",
                    airportIcao, path, begin, end, ex.getStatusCode(), ex.getResponseBodyAsString());
            // L’eccezione viene rilanciata per essere vista da Resilience4j
            metrics.incrementRequestErrors();
            throw ex;
        }
        // Errori generici di client (timeout, I/O, ecc.)
        catch (RestClientException ex) {
            log.warn("Errore di comunicazione con OpenSky per airport={} path={} window=[{}, {}]. " +
                            "Dettagli={}",
                    airportIcao, path, begin, end, ex.getMessage());
            metrics.incrementRequestErrors();
            throw ex;
        }
        finally {
            long durationMs = (System.nanoTime() - startNs) / 1_000_000L;
            metrics.setLastFetchDurationMs(durationMs);
        }
    }

    /**
     * Fallback invocato quando il Circuit Breaker è in stato OPEN
     * o quando la chiamata fallisce e Resilience4j applica il fallback.
     */
    protected List<OpenSkyFlightDto> fallbackFetchFlights(String path,
                                                          String airportIcao,
                                                          Instant begin,
                                                          Instant end,
                                                          Throwable ex) {
        metrics.incrementFallbacks();
        log.warn("Fallback OpenSky attivato (circuit breaker o errore) per airport={}, path={}. " +
                        "Motivo: {}. Ritorno lista vuota.",
                airportIcao, path, ex.toString());
        return Collections.emptyList();
    }
}