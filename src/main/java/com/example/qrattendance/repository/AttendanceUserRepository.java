package com.example.qrattendance.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.qrattendance.model.AttendanceUser;

@Repository
public interface AttendanceUserRepository extends JpaRepository<AttendanceUser, Long> {
    
    Optional<AttendanceUser> findByUsername(String username);
    
    Optional<AttendanceUser> findByCurrentTokenSession(String currentTokenSession);
}
