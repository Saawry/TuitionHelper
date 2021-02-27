package com.gadware.tution.models;

import androidx.room.Entity;
import androidx.room.Index;

@Entity(tableName = "DaySchedule",primaryKeys = {"bTId","dayName", "time"}, indices = {@Index(value = {"bTId","dayName", "time"}, unique = true)})
public class DaySchedule {
    private String bTId;
    private String dayName;
    private String aTime;
    private String time;


    public DaySchedule(String bTId, String dayName, String aTime, String time) {
        this.bTId = bTId;
        this.dayName = dayName;
        this.aTime = aTime;
        this.time = time;
    }

    public String getaTime() {
        return aTime;
    }

    public void setaTime(String aTime) {
        this.aTime = aTime;
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

    public String getbTId() {
        return bTId;
    }

    public void setbTId(String bTId) {
        this.bTId = bTId;
    }

}
