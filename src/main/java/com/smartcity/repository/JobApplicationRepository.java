package com.smartcity.repository;

import com.smartcity.entity.JobApplication;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {
    @Query("select count(a) > 0 from JobApplication a where a.cV.id = :cvId")
    boolean existsByCV_Id(Long cvId);

    List<JobApplication> findByUserIdAndSampleSubmissionTrueOrderByIdDesc(Long userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select application from JobApplication application where application.id = :id")
    Optional<JobApplication> lockById(Long id);

}
