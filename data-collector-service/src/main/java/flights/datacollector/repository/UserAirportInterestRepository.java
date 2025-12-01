package flights.datacollector.repository;

import flights.datacollector.domain.Airport;
import flights.datacollector.domain.UserAirportInterest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserAirportInterestRepository extends JpaRepository<UserAirportInterest, Long> {

    Optional<UserAirportInterest> findByUserEmailAndAirport(String userEmail, Airport airport);

    boolean existsByUserEmailAndAirport(String userEmail, Airport airport);

    List<UserAirportInterest> findAllByUserEmail(String userEmail);
}