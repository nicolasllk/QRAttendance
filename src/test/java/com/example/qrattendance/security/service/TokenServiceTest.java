package com.example.qrattendance.security.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import java.util.Optional;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import com.example.qrattendance.model.AttendanceUser;
import com.example.qrattendance.repository.AttendanceUserRepository;


@RunWith(MockitoJUnitRunner.class)
public class TokenServiceTest {
    @Mock
    private AttendanceUserRepository attendanceUserRepository;
    
    @InjectMocks
    private TokenService tokenService;

    @Test
    public void testIsValidToken() {
        final AttendanceUser attendanceUser = new AttendanceUser();
        final Optional<AttendanceUser> optionalAttendanceUser = Optional.of(attendanceUser);
        when(attendanceUserRepository.findByCurrentTokenSession(any())).thenReturn(optionalAttendanceUser);
        Assert.assertTrue("should be true", tokenService.isValidToken("token"));
    }

}