package com.example.qrattendance.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;


/**
 * Represents an activity/class that the user has checked in using the classroom qrCode
 */
@Entity
public class ActivityReservation {
    private String qrCode;
    @OneToOne
    @JoinColumn(name = "user_id")
    private AttendanceUser attendanceUser;
    @Id
    @GeneratedValue
    private Long id;

    void setId(final Long id) {
        this.id = id;
    }

    Long getId() {
        return id;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(final String qrCode) {
        this.qrCode = qrCode;
    }

    public AttendanceUser getAttendanceUser() {
        return attendanceUser;
    }

    public void setAttendanceUser(final AttendanceUser attendanceUser) {
        this.attendanceUser = attendanceUser;
    }
}
