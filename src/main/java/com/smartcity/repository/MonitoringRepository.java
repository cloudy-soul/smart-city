package com.smartcity.repository;

import com.smartcity.entity.Monitoring;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MonitoringRepository extends JpaRepository<Monitoring, Long> {
}
