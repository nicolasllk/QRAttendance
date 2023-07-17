package com.example.qrattendance.service;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.qrattendance.commons.CommonConstants;
import com.example.qrattendance.dto.ActivityDetailsDTO;
import com.example.qrattendance.model.ActivityReservation;
import com.example.qrattendance.model.AttendanceClass;
import com.example.qrattendance.model.AttendanceUser;
import com.example.qrattendance.repository.ActivityReservationRepository;
import com.example.qrattendance.repository.AttendanceClassRepository;
import com.example.qrattendance.repository.AttendanceUserRepository;


@Service
public class AttendanceService {
    private static final String IV_PARAM_SPEC = "random1234567890";
    private static final String SECRET_KEY_SPEC = "DkXbR21lnI0ChDsC4tnwRKNQSZ0lEhO8";
    private static final int TOLERANCE_TIME = 15;
    
    private final AttendanceUserRepository attendanceUserRepository;
    private final AttendanceClassRepository attendanceClassRepository;
    private final ActivityReservationRepository activityReservationRepository;

    @Autowired
    public AttendanceService(final AttendanceUserRepository attendanceUserRepository,
            final AttendanceClassRepository attendanceClassRepository,
            final ActivityReservationRepository activityReservationRepository) {
        this.attendanceUserRepository = attendanceUserRepository;
        this.attendanceClassRepository = attendanceClassRepository;
        this.activityReservationRepository = activityReservationRepository;
    }

    public boolean checkIn(final String classQrCode, final String token) {
        if (canCheckIn(classQrCode, token)) {
            return doCheckIn(classQrCode, token);
        }
        return false;
    }

    /**
     * Does the qrCode belongs to an actual classroom activity
     * @param decryptedQrCode classroom uniquely assigned qrcode
     * @return true if  valid false otherwise
     */
    public boolean isValidQrCode(final String decryptedQrCode) {
        return Optional.ofNullable(decryptedQrCode)
                .flatMap(attendanceClassRepository::findByQrCode)
                .filter(this::isAttendanceClassEnrollPeriodActive)
                .isPresent();
    }
    
    public ActivityDetailsDTO getActivity(final String decryptedQrCode) {
        return Optional.ofNullable(decryptedQrCode)
                .flatMap(attendanceClassRepository::findByQrCode)
                .map(attendanceClass -> new ActivityDetailsDTO(attendanceClass.getName(),
                        attendanceClass.getClassRoomId()))
                .orElse(null);
    }

    public String decryptQrToken(final String token) {
        try {
            final Cipher qrCipher = Cipher.getInstance(CommonConstants.TRANSFORMATION);
            final IvParameterSpec ivParameterSpec = new IvParameterSpec(IV_PARAM_SPEC.getBytes());
            final SecretKeySpec secretKeySpec = new SecretKeySpec(SECRET_KEY_SPEC.getBytes(),
                    CommonConstants.AES);
            qrCipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
            
            byte[] decryptedBytes = qrCipher.doFinal(Base64.getDecoder().decode(token));
            return new String(decryptedBytes);
        } catch (Exception e) {
            // Log the error
            return null;
        }
    }
    
    private boolean doCheckIn(final String classQrCode, final String token) {
        final Optional<AttendanceUser> optionalAttendanceUser = attendanceUserRepository.findByCurrentTokenSession(token);

        if (optionalAttendanceUser.isPresent()) {
            ActivityReservation reservation = createReservation(optionalAttendanceUser.get(), classQrCode);
            activityReservationRepository.saveAndFlush(reservation);
            return true;
        }
        // User is no longer logged in, can't check-in
        return false;
    }

    private ActivityReservation createReservation(final AttendanceUser attendanceUser, final String classQrCode) {
        ActivityReservation activityReservation = new ActivityReservation();
        activityReservation.setAttendanceUser(attendanceUser);
        activityReservation.setQrCode(classQrCode);
        return activityReservation;
    }

    private boolean canCheckIn(final String classId, final String token) {
        // Check if user is not already enrolled
        final Optional<ActivityReservation> reservation = activityReservationRepository.findByAttendanceUser_CurrentTokenSession(token);
        if (reservation.isPresent()) {
            //token already registered in classroom
            return false;
        }
        
        return isValidQrCode(classId) && isOngoingActivity(classId);
        
//        return reservation.map(activityReservation ->
//                        isOngoingActivity(activityReservation.getQrCode()) && isValidQrCode(classId))
//                .orElseGet(() -> isValidQrCode(classId));
    }

    private boolean isOngoingActivity(final String qrCode) {
        return Optional.ofNullable(qrCode)
                .flatMap(attendanceClassRepository::findByQrCode)
                .filter(this::isAttendanceClassFinished)
                .isPresent();
    }

    private boolean isAttendanceClassFinished(final AttendanceClass attendanceClass) {
        return LocalDateTime.now().isAfter(attendanceClass.getEnd());
    }

    private boolean isAttendanceClassEnrollPeriodActive(final AttendanceClass attendanceClass) {
        final LocalDateTime now = LocalDateTime.now();
        return now.isAfter(attendanceClass.getStart()) &&
                now.isBefore(attendanceClass.getStart().plusMinutes(TOLERANCE_TIME));
    }
}

