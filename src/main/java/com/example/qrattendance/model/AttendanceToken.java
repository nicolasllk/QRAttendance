package com.example.qrattendance.model;

import javax.persistence.Entity;
import javax.persistence.Id;


@Entity
public class AttendanceToken {
    @Id private Long id;

    void setId(final Long id) {
        this.id = id;
    }

    Long getId() {
        return id;
    }
}
