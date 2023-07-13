package com.example.qrattendance.service;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.qrattendance.model.AttendanceUser;
import com.example.qrattendance.repository.AttendanceUserRepository;


@Service
public class UserService {
    
    private final AttendanceUserRepository attendanceUserRepository;
    
    @Autowired
    public UserService(AttendanceUserRepository attendanceUserRepository){
        this.attendanceUserRepository = attendanceUserRepository;
    }
        
    public AttendanceUser createUser(AttendanceUser attendanceUser) {
        Optional<AttendanceUser> optionalUser = attendanceUserRepository.findByUsername(attendanceUser.getUsername());
        if (optionalUser.isPresent()) {
            throw new IllegalArgumentException("Username already taken");
        }

        // This is WRONG, BAD and LAME! dont!
        attendanceUser.setPassword(attendanceUser.getPassword());
        
        return attendanceUserRepository.saveAndFlush(attendanceUser);
    }

    public String login(String username, String password, String sessionId) {
       
        Optional<AttendanceUser> optionalUser = attendanceUserRepository.findByUsername(username);
        if (!optionalUser.isPresent()) {
            return null;
        }
       
        //here goes a user password check/decrypt/encrypt.
        
        optionalUser.ifPresent(attendanceUser -> {
            attendanceUser.setCurrentTokenSession(sessionId);
            attendanceUserRepository.saveAndFlush(attendanceUser);
        });
        return optionalUser.get().getCurrentTokenSession();
    }
    
}