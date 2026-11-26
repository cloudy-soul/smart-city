package com.smartcity.repository;

import com.smartcity.entity.OnlineSession;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OnlineSessionRepository extends JpaRepository<OnlineSession, Long> {
}
