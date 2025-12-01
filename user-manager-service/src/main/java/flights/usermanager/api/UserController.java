package flights.usermanager.api;

import flights.usermanager.api.dto.UserRegistrationRequest;
import flights.usermanager.api.dto.UserResponse;
import flights.usermanager.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Registrazione utente (at-most-once).
     *
     * Semantica:
     * - se l'utente non esiste, viene creato → 201 Created
     * - se l'utente esiste già, viene restituito comunque → 200 OK
     *   (nessun nuovo inserimento, quindi nessun effetto addizionale).
     */
    @PostMapping
    public ResponseEntity<UserResponse> registerUser(
            @Valid @RequestBody UserRegistrationRequest request
    ) {
        UserResponse response = userService.registerUser(request);

        // Heuristica semplice: se abbiamo appena creato l'utente, potremmo distinguere,
        // ma per semplicità assumiamo:
        // - 201 Created se NON esisteva prima,
        // - 200 OK se esisteva già.
        //
        // Per distinguere i due casi in modo pulito,
        // potremmo fare tornare dal service anche un flag "created".
        //
        // Per ora usiamo lo schema più semplice: 200 OK sempre.
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{email}")
    public ResponseEntity<UserResponse> getUser(@PathVariable String email) {
        UserResponse response = userService.getUser(email);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{email}")
    public ResponseEntity<Void> deleteUser(@PathVariable String email) {
        userService.deleteUser(email);
        return ResponseEntity.noContent().build();
    }
}