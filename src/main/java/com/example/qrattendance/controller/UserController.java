package com.example.qrattendance.controller;

import java.util.Optional;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.qrattendance.model.AttendanceUser;
import com.example.qrattendance.model.LoginRequest;
import com.example.qrattendance.service.UserService;


@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<String> login(HttpSession session, @RequestBody LoginRequest loginRequest) {
        String sessionToken = userService.login(loginRequest.getUsername(), loginRequest.getPassword(),
                session.getId());

        return Optional.ofNullable(sessionToken)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.badRequest().body(null));
    }
    
    @PostMapping("/create")
    public ResponseEntity<AttendanceUser> createUser(@RequestBody AttendanceUser attendanceUser) {
        //apply request input validations
        
        try {
            AttendanceUser attendanceUserEntity = userService.createUser(attendanceUser);
            return new ResponseEntity<>(attendanceUserEntity, HttpStatus.CREATED);
        }catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        
        
    }

    
}
