package com.gadware.tution.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Batch {
    @PrimaryKey
    private String id;
    private String className;
    private String subject;
    private String studentsAmount;
    private String remuneration;
    private String weeklyDays;

    public Batch(String id, String className, String subject, String studentsAmount, String remuneration, String weeklyDays) {
        this.id = id;
        this.className = className;
        this.subject = subject;
        this.studentsAmount = studentsAmount;
        this.remuneration = remuneration;
        this.weeklyDays = weeklyDays;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getStudentsAmount() {
        return studentsAmount;
    }

    public void setStudentsAmount(String studentsAmount) {
        this.studentsAmount = studentsAmount;
    }

    public String getRemuneration() {
        return remuneration;
    }

    public void setRemuneration(String remuneration) {
        this.remuneration = remuneration;
    }

    public String getWeeklyDays() {
        return weeklyDays;
    }

    public void setWeeklyDays(String weeklyDays) {
        this.weeklyDays = weeklyDays;
    }
}
