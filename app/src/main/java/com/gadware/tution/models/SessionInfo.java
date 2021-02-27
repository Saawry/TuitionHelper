package com.gadware.tution.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class SessionInfo {
    @PrimaryKey
    private String id;
    private String btId;
    private String type;
    private String date;
    private String day;
    private String sTime;
    private String eTime;
    private String topic;
    private String tutorId;
    private String counter;

    public SessionInfo(String id, String btId, String type, String date, String day, String sTime, String eTime, String topic, String tutorId, String counter) {
        this.id = id;
        this.btId = btId;
        this.type = type;
        this.date = date;
        this.day = day;
        this.sTime = sTime;
        this.eTime = eTime;
        this.topic = topic;
        this.tutorId = tutorId;
        this.counter = counter;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBtId() {
        return btId;
    }

    public void setBtId(String btId) {
        this.btId = btId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getsTime() {
        return sTime;
    }

    public void setsTime(String sTime) {
        this.sTime = sTime;
    }

    public String geteTime() {
        return eTime;
    }

    public void seteTime(String eTime) {
        this.eTime = eTime;
    }

    public String getTutorId() {
        return tutorId;
    }

    public void setTutorId(String tutorId) {
        this.tutorId = tutorId;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getCounter() {
        return counter;
    }

    public void setCounter(String counter) {
        this.counter = counter;
    }

}
