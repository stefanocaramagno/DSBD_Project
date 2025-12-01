package flights.datacollector.api;

import flights.datacollector.api.dto.UserInterestRequest;
import flights.datacollector.api.dto.UserInterestResponse;
import flights.datacollector.service.UserInterestService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/interests")
public class UserInterestController {

    private final UserInterestService userInterestService;

    public UserInterestController(UserInterestService userInterestService) {
        this.userInterestService = userInterestService;
    }

    /**
     * Registra l'interesse di un utente per un aeroporto (at-most-once).
     */
    @PostMapping
    public ResponseEntity<UserInterestResponse> registerInterest(
            @Valid @RequestBody UserInterestRequest request
    ) {
        UserInterestResponse response = userInterestService.registerInterest(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Rimuove l'interesse di un utente per un aeroporto.
     */
    @DeleteMapping
    public ResponseEntity<Void> removeInterest(
            @RequestParam("userEmail") String userEmail,
            @RequestParam("airportCode") String airportCode
    ) {
        userInterestService.removeInterest(userEmail, airportCode);
        return ResponseEntity.noContent().build(); 
    }

    /**
     * Elenca tutti gli interessi per un dato utente.
     */
    @GetMapping
    public ResponseEntity<List<UserInterestResponse>> listInterests(
            @RequestParam("userEmail") String userEmail
    ) {
        List<UserInterestResponse> responses = userInterestService.listInterestsForUser(userEmail);
        return ResponseEntity.ok(responses);
    }
}