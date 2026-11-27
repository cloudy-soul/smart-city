package com.smartcity.repository;

import com.smartcity.entity.Users;
import java.util.Optional;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsersRepository extends JpaRepository<Users, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select user from Users user where user.id = :id")
    Optional<Users> lockById(Long id);

    Optional<Users> findFirstByAccountUsername(String username);
    Optional<Users> findFirstByUserCodeOrderById(String userCode);
}
