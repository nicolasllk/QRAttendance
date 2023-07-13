package com.example.qrattendance.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.qrattendance.model.AttendanceClass;


@Repository
public interface AttendanceClassRepository extends JpaRepository<AttendanceClass, Long> {
    Optional<AttendanceClass> findByQrCode(String qrCode);
}
