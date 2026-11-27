package com.smartcity.repository;

import com.smartcity.entity.Booking;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select entity from Booking entity where entity.id = :id")
    Optional<Booking> lockById(Long id);

    List<Booking> findByUserIdAndEventIsNotNullOrderByIdDesc(Long userId);
}
