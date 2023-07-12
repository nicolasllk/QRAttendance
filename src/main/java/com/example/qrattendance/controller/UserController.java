package com.example.qrattendance.controller;

import java.util.Optional;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
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

    @PostMapping("/create")
    public ResponseEntity<AttendanceUser> createUser(@RequestBody AttendanceUser attendanceUser) {
        AttendanceUser createdAttendanceUser = userService.createUser(attendanceUser);
        return ResponseEntity.ok(createdAttendanceUser);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(HttpSession session, @RequestBody LoginRequest loginRequest) {
        String sessionToken = userService.login(loginRequest.getUsername(), loginRequest.getPassword(),
                session.getId());

        return Optional.ofNullable(sessionToken)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.badRequest().body(null));
    }
}
