package com.example.qrattendance.service;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.qrattendance.model.AttendanceToken;
import com.example.qrattendance.model.AttendanceUser;
import com.example.qrattendance.repository.UserRepository;


@Service
public class UserService {
    
    private final UserRepository userRepository;
    
    @Autowired
    public UserService(UserRepository userRepository){
        this.userRepository = userRepository;
    }
        
    public AttendanceUser createUser(AttendanceUser attendanceUser) {
        Optional<AttendanceUser> optionalUser = userRepository.findByUsername(attendanceUser.getUsername());
        if (optionalUser.isPresent()) {
            throw new IllegalArgumentException("Username already taken");
        }

        // This is WRONG, BAD and LAME! dont!
        attendanceUser.setPassword(attendanceUser.getPassword());
        
        return userRepository.saveAndFlush(attendanceUser);
    }

    public String login(String username, String password, String sessionId) {
       
        Optional<AttendanceUser> optionalUser = userRepository.findByUsername(username);
        if (!optionalUser.isPresent()) {
            return null;
        }
       
        //here goes a user password check.
        
        optionalUser.ifPresent(attendanceUser -> {
            attendanceUser.setCurrentTokenSession(sessionId);
            userRepository.saveAndFlush(attendanceUser);
        });
        return optionalUser.get().getCurrentTokenSession();
    }
    
//    public AttendanceToken generateToken() {
//        
//    }
}