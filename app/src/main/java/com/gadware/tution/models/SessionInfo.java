package com.gadware.tution.models;

public class SessionInfo {
    private String id;
    private String date;
    private String day;
    private String time;
    private String eTime;
    private String topic;
    private String counter;

    public SessionInfo() {
    }

    public SessionInfo(String id, String date, String day, String time, String eTime, String topic) {
        this.id = id;
        this.date = date;
        this.day = day;
        this.time = time;
        this.eTime = eTime;
        this.topic = topic;
    }

    public SessionInfo(String id, String date, String day, String time, String eTime, String topic, String counter) {
        this.id = id;
        this.date = date;
        this.day = day;
        this.time = time;
        this.eTime = eTime;
        this.topic = topic;
        this.counter = counter;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String geteTime() {
        return eTime;
    }

    public void seteTime(String eTime) {
        this.eTime = eTime;
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
