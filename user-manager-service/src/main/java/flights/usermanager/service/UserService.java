package flights.usermanager.service;

import flights.usermanager.api.dto.UserRegistrationRequest;
import flights.usermanager.api.dto.UserResponse;
import flights.usermanager.domain.User;
import flights.usermanager.exception.UserNotFoundException;
import flights.usermanager.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Registrazione utente con politica "at-most-once".
     *
     * Scelta implementativa:
     * - se l'utente NON esiste, viene creato e restituito.
     * - se l'utente ESISTE già, NON viene creato un duplicato; restituiamo comunque
     *   i dati dell'utente esistente.
     *
     * In questo modo, chiamate ripetute con lo stesso payload producono
     * al massimo un inserimento, e nessuna duplicazione → at-most-once.
     */
    @Transactional
    public UserResponse registerUser(UserRegistrationRequest request) {
        String email = request.getEmail();

        User existing = userRepository.findById(email).orElse(null);
        if (existing != null) {
            // Politica at-most-once: nessun effetto aggiuntivo, restituiamo l'utente esistente
            return toResponse(existing);
        }

        User user = new User(
                request.getEmail(),
                request.getName(),
                LocalDateTime.now()
        );
        User saved = userRepository.save(user);
        return toResponse(saved);
    }

    @Transactional
    public void deleteUser(String email) {
        boolean exists = userRepository.existsById(email);
        if (!exists) {
            throw new UserNotFoundException(email);
        }
        userRepository.deleteById(email);
    }

    @Transactional(readOnly = true)
    public UserResponse getUser(String email) {
        User user = userRepository.findById(email)
                .orElseThrow(() -> new UserNotFoundException(email));
        return toResponse(user);
    }

    private UserResponse toResponse(User user) {
        return new UserResponse(
                user.getEmail(),
                user.getName(),
                user.getCreatedAt()
        );
    }

    @Transactional(readOnly = true)
    public boolean userExists(String email) {
        return userRepository.existsById(email);
    }
}