package com.example.qrattendance.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.qrattendance.model.ActivityReservation;


@Repository
public interface ActivityReservationRepository extends JpaRepository<ActivityReservation, Long> {
    
    Optional<ActivityReservation> findByQrCode(String qrCode);
    
    Optional<ActivityReservation> findByAttendanceUser_CurrentTokenSession(String token);
}
