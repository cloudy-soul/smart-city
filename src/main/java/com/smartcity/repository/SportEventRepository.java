package com.smartcity.repository;

import com.smartcity.entity.SportEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SportEventRepository extends JpaRepository<SportEvent, Long> {
}
