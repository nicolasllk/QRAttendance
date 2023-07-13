package com.example.qrattendance.service;

import java.time.LocalDateTime;
import java.util.Optional;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.qrattendance.dto.ActivityDetailsDTO;
import com.example.qrattendance.model.ActivityReservation;
import com.example.qrattendance.model.AttendanceClass;
import com.example.qrattendance.model.AttendanceUser;
import com.example.qrattendance.repository.ActivityReservationRepository;
import com.example.qrattendance.repository.AttendanceClassRepository;
import com.example.qrattendance.repository.AttendanceUserRepository;


@Service
public class AttendanceService {

    public static final String IV_PARAM_SPEC = "random1234567890";
    public static final String SECRET_KEY_SPEC = "secretKey1234567";
    public static final int TOLERANCE_TIME = 15;
    public static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";
    public static final String AES = "AES";
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

    /**
     * Does the specified user token corresponds to a logged-in user?
     * @param token
     * @return
     */
    public boolean isValidToken(final String token) {
        return attendanceUserRepository.findByCurrentTokenSession(token).isPresent();
    }

    /**
     * Check-in a logged-in user in the specified class
     * @param classQrCode the class qr code
     * @param token logged in user 
     * @return true if user was registered in the specified class, false otherwise
     */
    public boolean checkin(final String classQrCode, final String token) {
        if(canCheckIn(classQrCode, token)){
            return doCheckIn(classQrCode, token);
        }
        return false;
    }

    /**
     * validate qr code belongs to an active classroom
     * @param decryptedQrCode classroom QR code
     * @return true if code is valid and active, false otherwise
     */
    public boolean isValidQrCode(final String decryptedQrCode) {
        return Optional.ofNullable(decryptedQrCode)
                .map(qrCode -> attendanceClassRepository.findByQrCode(decryptedQrCode))
                .map(optionalAttendanceClass -> optionalAttendanceClass.filter(this::isAttendanceClassActive))
                .isPresent();
    }

    /**
     * decrypt the class qr code
     * @param qrCode encrypted class qr code
     * @return decrypted class qr code
     */
    public String decryptCode(final String qrCode) {
        try {
            final Cipher qrCipher = Cipher.getInstance(TRANSFORMATION);
            final IvParameterSpec ivParameterSpec = new IvParameterSpec(IV_PARAM_SPEC.getBytes());
            final SecretKeySpec secretKeySpec = new SecretKeySpec(SECRET_KEY_SPEC.getBytes(), AES);
            qrCipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);

            //this wont work unless the qrcode has been properly encrypted. decryption of a random string will almos 
            // always fail commenting this for now
            //return new String(qrCipher.doFinal(qrCode.getBytes()));
            return "1234567890";
        }catch (Exception e) {
            //log error
            return null;
        }
    }

    /**
     * Gets the QR's corresponding activity/classroom information 
     * @param decryptedQrCode decripted classroom qr code
     * @return the activity/classroom information if present/loaded in the system
     */
    public ActivityDetailsDTO getActivity(final String decryptedQrCode) {
        return Optional.ofNullable(decryptedQrCode)
                .flatMap(attendanceClassRepository::findByQrCode)
                .map(attendanceClass -> new ActivityDetailsDTO(attendanceClass.getName(),
                        attendanceClass.getClassRoomId()))
                .orElse(null);
    }
    
    private boolean doCheckIn(final String classQrCode, final String token) {
        final Optional<AttendanceUser> optionalAttendanceUser = attendanceUserRepository.findByCurrentTokenSession(
                token);

        if (!optionalAttendanceUser.isPresent()){
            //user is no longer logged, cant check-in
            return false;
        }

        activityReservationRepository.saveAndFlush(createReservation(optionalAttendanceUser.get(), classQrCode));
        return true;
    }
    
    private ActivityReservation createReservation(final AttendanceUser attendanceUser, final String classQrCode) {
        ActivityReservation activityReservation = new ActivityReservation();
        activityReservation.setAttendanceUser(attendanceUser);
        activityReservation.setQrCode(classQrCode);
        return activityReservation;
    }
    

    private boolean canCheckIn(final String classId, final String token) {
        //check if user is not already enrolled
        final Optional<ActivityReservation> reservation =
                activityReservationRepository.findByAttendanceUser_CurrentTokenSession(token);
        return reservation.map(activityReservation -> 
                        isOngoingActivity(activityReservation.getQrCode()) && isValidQrCode(classId))
                .orElseGet(() -> isValidQrCode(classId));
        
    }

    private boolean isOngoingActivity(final String qrCode) {
        return Optional.ofNullable(qrCode)
                .map(attendanceClassRepository::findByQrCode)
                .map(optionalAttendanceClass -> optionalAttendanceClass.filter(this::isAttendanceClassFinished))
                .isPresent();
    }

    private boolean isAttendanceClassFinished(final AttendanceClass attendanceClass) {
        return LocalDateTime.now().isBefore(attendanceClass.getEnd());
    }

    private boolean isAttendanceClassActive(final AttendanceClass attendanceClass) {
        final LocalDateTime now = LocalDateTime.now();
        return now.isAfter(attendanceClass.getStart().plusMinutes(TOLERANCE_TIME)) 
                && now.isBefore(attendanceClass.getEnd());
    }
}
