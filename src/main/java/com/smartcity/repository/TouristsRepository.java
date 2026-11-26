package com.smartcity.repository;

import com.smartcity.entity.Tourists;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TouristsRepository extends JpaRepository<Tourists, Long> {
}
