package com.example.qrattendance.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.qrattendance.dto.ActivityDetailsDTO;
import com.example.qrattendance.service.AttendanceService;


@RestController
@RequestMapping("/attendance")
public class AttendanceController {

    @Autowired
    private AttendanceService attendanceService;
    
    @PostMapping("/class/checkin/{class-id}")
    public ResponseEntity<String> checkIn(@PathVariable("class-id") String classId, 
            @RequestHeader("token-id") String token) {
        
        //since token was assigned to a user we check based on that token whether the user can check in for this or 
        // any class at this time
        if (!attendanceService.isValidToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); 
        }
        
        if (attendanceService.checkin(classId, token)) {
            return ResponseEntity.ok("signed in!");
        }
       
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
    
    @GetMapping("/class/{qr-code}")
    public ResponseEntity<ActivityDetailsDTO> getActivity(@PathVariable("qr-code") String qrCode) {
        final String decryptedQrCode = attendanceService.decryptCode(qrCode);
        if (!attendanceService.isValidQrCode(decryptedQrCode) ) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        ActivityDetailsDTO classDetails = attendanceService.getActivity(decryptedQrCode);
        
        return ResponseEntity.ok(classDetails);
        
    }
}
