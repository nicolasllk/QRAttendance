package com.example.qrattendance.controller;

import java.util.Optional;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
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

    public static final String TOKEN_ID_FIELD = "token-id";
    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<String> login(HttpSession session, @RequestBody LoginRequest loginRequest) {
        //Validate loginRequest contents
        String sessionToken = userService.login(loginRequest.getUsername(), loginRequest.getPassword(),
                session.getId());

        if (sessionToken != null) {
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set(TOKEN_ID_FIELD, sessionToken);
            return ResponseEntity.ok()
                    .headers(responseHeaders)
                    .build();
        }
        
        return ResponseEntity.badRequest().build();
    }
    
    @PostMapping("/create")
    public ResponseEntity<AttendanceUser> createUser(@RequestBody AttendanceUser attendanceUser) {
        //apply request input validations and password encryption before persist
        
        try {
            //do not return the same object that is persisted in the DB! I should use a DTO or simply return HTTP
            // .CREATED and implement a GET endpoint
            AttendanceUser attendanceUserEntity = userService.createUser(attendanceUser);
            return new ResponseEntity<>(attendanceUserEntity, HttpStatus.CREATED);
        }catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        
    }
    
}
