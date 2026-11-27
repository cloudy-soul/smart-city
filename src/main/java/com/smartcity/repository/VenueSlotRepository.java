package com.smartcity.repository;

import com.smartcity.entity.VenueSlot;
import jakarta.persistence.LockModeType;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

public interface VenueSlotRepository extends JpaRepository<VenueSlot, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select slot from VenueSlot slot where slot.id = :id")
    Optional<VenueSlot> lockById(Long id);

    @Query("""
            select count(slot) > 0 from VenueSlot slot
            where slot.venue.id = :venueId and slot.id <> :excludedId
              and slot.startsAt < :end and slot.endsAt > :start
            """)
    boolean overlaps(Long venueId, Long excludedId, LocalDateTime start, LocalDateTime end);
}
