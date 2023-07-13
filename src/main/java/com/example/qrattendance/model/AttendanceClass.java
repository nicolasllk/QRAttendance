package com.example.qrattendance.model;

import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;


/**
 * Represents an activity/class into which a user can enroll/check-in
 */
@Entity
public class AttendanceClass {
    @Id 
    @GeneratedValue
    private Long id;
    private String name;
    //not sure if this object is the best for db storing
    private LocalDateTime start;
    private LocalDateTime end;
    //if we want the code to cycle for security reasons I'd say this can be changed to a Set<String>
    private String qrCode;
    private String classRoomId;
    
    void setId(final Long id) {
        this.id = id;
    }

    Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public void setStart(final LocalDateTime start) {
        this.start = start;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public void setEnd(final LocalDateTime end) {
        this.end = end;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(final String qrCode) {
        this.qrCode = qrCode;
    }

    public String getClassRoomId() {
        return classRoomId;
    }

    public void setClassRoomId(final String classRoomId) {
        this.classRoomId = classRoomId;
    }
}
