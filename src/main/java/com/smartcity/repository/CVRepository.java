package com.smartcity.repository;

import com.smartcity.entity.CV;
import java.util.List;
import java.util.Optional;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CVRepository extends JpaRepository<CV, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select cv from CV cv where cv.id = :id")
    Optional<CV> lockById(Long id);

    List<CV> findByOwnerIsNull();

    List<CV> findByOwnerIdOrderByIdDesc(Long ownerId);
}
