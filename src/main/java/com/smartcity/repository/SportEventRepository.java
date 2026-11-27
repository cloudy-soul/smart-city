package com.smartcity.repository;

import com.smartcity.entity.SportEvent;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

public interface SportEventRepository extends JpaRepository<SportEvent, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select entity from SportEvent entity where entity.id = :id")
    Optional<SportEvent> lockById(Long id);

    boolean existsBySlotId(Long slotId);
}
