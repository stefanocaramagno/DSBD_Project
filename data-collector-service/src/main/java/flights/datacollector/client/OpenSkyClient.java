package flights.datacollector.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import flights.datacollector.client.dto.OpenSkyFlightDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
public class OpenSkyClient {

    private final RestTemplate restTemplate;

    // URL base delle API REST di OpenSky (es. https://opensky-network.org/api)
    private final String apiBaseUrl;

    // Endpoint OAuth2 per ottenere il token (es. https://auth.opensky-network.org/.../token)
    private final String authUrl;

    // Credenziali OAuth2 (client credentials)
    private final String clientId;
    private final String clientSecret;

    // Cache del token per evitare di richiederlo a ogni chiamata
    private String accessToken;
    private Instant accessTokenExpiry;

    public OpenSkyClient(RestTemplateBuilder restTemplateBuilder,
                         @Value("${opensky.api-base-url:https://opensky-network.org/api}") String apiBaseUrl,
                         @Value("${opensky.auth-url}") String authUrl,
                         @Value("${opensky.client-id}") String clientId,
                         @Value("${opensky.client-secret}") String clientSecret) {

        this.restTemplate = restTemplateBuilder
                .setConnectTimeout(Duration.ofSeconds(10))
                .setReadTimeout(Duration.ofSeconds(30))
                .build();

        this.apiBaseUrl = apiBaseUrl;
        this.authUrl = authUrl;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
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

        String url = apiBaseUrl + path + "?airport={airport}&begin={begin}&end={end}";

        long beginEpoch = begin.getEpochSecond();
        long endEpoch = end.getEpochSecond();

        try {
            ResponseEntity<OpenSkyFlightDto[]> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    buildAuthEntity(),       // Authorization: Bearer <token>
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
        // Le altre eccezioni (401, 429, 5xx, problemi di rete) al momento vengono propagate.
    }

    /**
     * Crea l'HttpEntity con l'header Authorization: Bearer <token>.
     */
    private HttpEntity<Void> buildAuthEntity() {
        HttpHeaders headers = new HttpHeaders();
        String token = getAccessToken();
        headers.setBearerAuth(token);
        return new HttpEntity<>(headers);
    }

    /**
     * Restituisce un access token valido. Se il token in cache è ancora valido lo riusa,
     * altrimenti ne richiede uno nuovo all'Authorization Server di OpenSky.
     */
    private synchronized String getAccessToken() {
        // Se ho già un token non scaduto (con un piccolo margine di 60s), lo riuso
        if (accessToken != null && accessTokenExpiry != null) {
            Instant now = Instant.now();
            if (now.isBefore(accessTokenExpiry.minusSeconds(60))) {
                return accessToken;
            }
        }

        // Altrimenti chiedo un nuovo token via OAuth2 Client Credentials
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "client_credentials");
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        ResponseEntity<OpenSkyTokenResponse> response =
                restTemplate.postForEntity(authUrl, request, OpenSkyTokenResponse.class);

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new IllegalStateException(
                    "Impossibile ottenere un access token da OpenSky. HTTP status: " + response.getStatusCode());
        }

        OpenSkyTokenResponse tokenResponse = response.getBody();
        if (tokenResponse.getAccessToken() == null || tokenResponse.getAccessToken().isBlank()) {
            throw new IllegalStateException("Risposta OAuth2 di OpenSky priva di access_token valido");
        }

        this.accessToken = tokenResponse.getAccessToken();

        // Se "expires_in" non è valorizzato, assumiamo 1800 secondi (30 minuti)
        long expiresIn = tokenResponse.getExpiresIn() != null ? tokenResponse.getExpiresIn() : 1800L;
        this.accessTokenExpiry = Instant.now().plusSeconds(expiresIn);

        return this.accessToken;
    }

    /**
     * DTO interno per mappare la risposta del token endpoint di OpenSky.
     *
     * Esempio di risposta:
     * {
     *   "access_token": "...",
     *   "expires_in": 1800,
     *   "token_type": "Bearer",
     *   ...
     * }
     */
    @SuppressWarnings("unused")
    private static class OpenSkyTokenResponse {

        @JsonProperty("access_token")
        private String accessToken;

        @JsonProperty("expires_in")
        private Long expiresIn;

        @JsonProperty("token_type")
        private String tokenType;

        public String getAccessToken() {
            return accessToken;
        }

        public Long getExpiresIn() {
            return expiresIn;
        }

        public String getTokenType() {
            return tokenType;
        }

        public void setAccessToken(String accessToken) {
            this.accessToken = accessToken;
        }

        public void setExpiresIn(Long expiresIn) {
            this.expiresIn = expiresIn;
        }

        public void setTokenType(String tokenType) {
            this.tokenType = tokenType;
        }
    }
}
