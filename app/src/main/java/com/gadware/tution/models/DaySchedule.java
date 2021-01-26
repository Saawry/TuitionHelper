package com.gadware.tution.models;

public class DaySchedule {
    private String dayName;
    private String time;

    public DaySchedule() {
    }

    public DaySchedule(String dayName, String time) {
        this.dayName = dayName;
        this.time = time;
    }

    public String getDayName() {
        return dayName;
    }

    public void setDayName(String dayName) {
        this.dayName = dayName;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
