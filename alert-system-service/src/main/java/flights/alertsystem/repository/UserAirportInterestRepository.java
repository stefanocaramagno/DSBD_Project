package flights.alertsystem.repository;

import flights.alertsystem.domain.UserAirportInterest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;

public interface UserAirportInterestRepository extends JpaRepository<UserAirportInterest, Long> {

    /**
     * Recupera tutti gli interessi che hanno almeno una soglia definita
     * (highValue o lowValue) per gli aeroporti indicati.
     */
    @Query("""
            select i
            from UserAirportInterest i
            join fetch i.airport a
            where a.code in :airportCodes
              and (i.highValue is not null or i.lowValue is not null)
           """)
    List<UserAirportInterest> findWithThresholdsByAirportCodes(Collection<String> airportCodes);
}