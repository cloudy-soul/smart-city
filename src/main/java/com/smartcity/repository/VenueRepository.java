package com.smartcity.repository;

import com.smartcity.entity.Venue;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

public interface VenueRepository extends JpaRepository<Venue, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select entity from Venue entity where entity.id = :id")
    Optional<Venue> lockById(Long id);

}
