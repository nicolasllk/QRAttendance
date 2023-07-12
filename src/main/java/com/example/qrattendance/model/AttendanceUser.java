package com.example.qrattendance.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;


@Entity
public class AttendanceUser {
    private String username;
    private String password;
    private String currentTokenSession;
    @Id @GeneratedValue private Long id;

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(final String hashedPassword) {
        this.password = hashedPassword;
    }
    
    void setId(final Long id) {
        this.id = id;
    }

    Long getId() {
        return id;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    public String getCurrentTokenSession() {
        return currentTokenSession;
    }

    public void setCurrentTokenSession(final String currentTokenSession) {
        this.currentTokenSession = currentTokenSession;
    }

}
