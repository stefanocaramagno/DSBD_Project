package flights.datacollector.client;

import flights.datacollector.client.dto.OpenSkyFlightDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

@Component
public class OpenSkyClient {

    private final RestTemplate restTemplate;
    private final String baseUrl;
    private final String username;
    private final String password;

    public OpenSkyClient(RestTemplateBuilder restTemplateBuilder,
                         @Value("${opensky.base-url:https://opensky-network.org/api}") String baseUrl,
                         @Value("${opensky.username:}") String username,
                         @Value("${opensky.password:}") String password) {

        this.restTemplate = restTemplateBuilder
                .setConnectTimeout(Duration.ofSeconds(10))
                .setReadTimeout(Duration.ofSeconds(30))
                .build();

        this.baseUrl = baseUrl;
        this.username = username;
        this.password = password;
    }

    /**
     * Recupera i voli in arrivo su un aeroporto nel periodo [begin, end].
     *
     * @param airportIcao Codice ICAO dell'aeroporto (es. "EDDF").
     * @param begin       Inizio intervallo (Instant, UTC).
     * @param end         Fine intervallo (Instant, UTC).
     * @return Lista di voli (eventualmente vuota se non ci sono voli).
     */
    public List<OpenSkyFlightDto> getArrivals(String airportIcao, Instant begin, Instant end) {
        return fetchFlights("/flights/arrival", airportIcao, begin, end);
    }

    /**
     * Recupera i voli in partenza da un aeroporto nel periodo [begin, end].
     *
     * @param airportIcao Codice ICAO dell'aeroporto.
     * @param begin       Inizio intervallo (Instant, UTC).
     * @param end         Fine intervallo (Instant, UTC).
     * @return Lista di voli (eventualmente vuota se non ci sono voli).
     */
    public List<OpenSkyFlightDto> getDepartures(String airportIcao, Instant begin, Instant end) {
        return fetchFlights("/flights/departure", airportIcao, begin, end);
    }

    private List<OpenSkyFlightDto> fetchFlights(String path,
                                                String airportIcao,
                                                Instant begin,
                                                Instant end) {

        String url = baseUrl + path + "?airport={airport}&begin={begin}&end={end}";

        long beginEpoch = begin.getEpochSecond();
        long endEpoch = end.getEpochSecond();

        try {
            ResponseEntity<OpenSkyFlightDto[]> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    buildAuthEntity(),
                    OpenSkyFlightDto[].class,
                    airportIcao,
                    beginEpoch,
                    endEpoch
            );

            OpenSkyFlightDto[] body = response.getBody();
            if (body == null || body.length == 0) {
                return Collections.emptyList();
            }

            return Arrays.asList(body);

        } catch (HttpClientErrorException.NotFound e) {
            // Caso documentato da OpenSky: 404 -> nessun volo nel periodo richiesto
            return Collections.emptyList();
        }
        // Le altre eccezioni (401, 429, 5xx, problemi di rete) per ora le
        // lasciamo propagare: potremo gestirle in modo mirato nella fase di scheduling.
    }

    /**
     * Crea l'HttpEntity con eventuale header Authorization Basic.
     * Se username è vuoto, non inserisce alcuna autenticazione.
     */
    private HttpEntity<Void> buildAuthEntity() {
        HttpHeaders headers = new HttpHeaders();

        if (username != null && !username.isBlank()) {
            String credentials = username + ":" + password;
            String encoded = Base64.getEncoder()
                    .encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
            headers.add(HttpHeaders.AUTHORIZATION, "Basic " + encoded);
        }

        // Restituiamo sempre un HttpEntity<Void> tipizzato in modo coerente,
        // evitando l'uso di HttpEntity.EMPTY (che è HttpEntity<?>)
        return new HttpEntity<>(headers);
    }
}