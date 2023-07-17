package com.example.qrattendance.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import com.example.qrattendance.dto.ActivityDetailsDTO;
import com.example.qrattendance.model.ActivityReservation;
import com.example.qrattendance.model.AttendanceClass;
import com.example.qrattendance.model.AttendanceUser;
import com.example.qrattendance.repository.ActivityReservationRepository;
import com.example.qrattendance.repository.AttendanceClassRepository;
import com.example.qrattendance.repository.AttendanceUserRepository;


@RunWith(MockitoJUnitRunner.class)
public class AttendanceServiceTest {
    @Mock
    private AttendanceUserRepository attendanceUserRepository;
    @Mock
    private AttendanceClassRepository attendanceClassRepository;
    @Mock
    private ActivityReservationRepository activityReservationRepository;
    
    @InjectMocks
    private AttendanceService attendanceService;
    
    @Test
    public void testGetActivity() {
        final AttendanceClass attendanceClass = new AttendanceClass();
        attendanceClass.setName("name");
        attendanceClass.setClassRoomId("roomId");
        final Optional<AttendanceClass> attendanceClassItem = Optional.of(attendanceClass);
        when(attendanceClassRepository.findByQrCode(any())).thenReturn(attendanceClassItem);
        final ActivityDetailsDTO activity = attendanceService.getActivity("qrcode");
        Assert.assertNotNull(activity);
        Assert.assertEquals("should be equal", "name", activity.getName());
        Assert.assertEquals("should be equal", "roomId", activity.getClassRoomId());

    }
    
    @Test
    public void testCheckin() {
        final ActivityReservation activityReservation = new ActivityReservation();
        activityReservation.setQrCode("reservationCode");
        final Optional<ActivityReservation> activityReservationOptional = Optional.of(activityReservation);
        final AttendanceUser attendanceUser = new AttendanceUser();
        final Optional<AttendanceUser> optionalAttendanceUser = Optional.of(attendanceUser);
        final AttendanceClass attendanceClass = new AttendanceClass();
        attendanceClass.setEnd(LocalDateTime.now().plusHours(2));
        attendanceClass.setStart(LocalDateTime.now().minusMinutes(2));
        final Optional<AttendanceClass> optionalAttendanceClass = Optional.of(attendanceClass);
        
        when(activityReservationRepository.findByAttendanceUser_CurrentTokenSession(any())).thenReturn(activityReservationOptional);
        when(attendanceClassRepository.findByQrCode(any())).thenReturn(optionalAttendanceClass);
        when(attendanceUserRepository.findByCurrentTokenSession(any())).thenReturn(optionalAttendanceUser);
        
        Assert.assertTrue("should be true",attendanceService.checkIn("classQrCode", "token"));
    }
    
}