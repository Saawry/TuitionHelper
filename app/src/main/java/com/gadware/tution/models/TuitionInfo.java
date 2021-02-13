package com.gadware.tution.models;

public class TuitionInfo {
    private String id;
    private String studentName;
    private String location;
    private String mobile;
    private String totalDays;
    private String completedDays;
    private String weeklyDays;
    private String remuneration;
    private String sDate;
    private String eDate;

    public TuitionInfo() {
    }

    public TuitionInfo(String id, String studentName, String location, String mobile, String totalDays, String completedDays, String weeklyDays, String remuneration, String sDate, String eDate) {
        this.id = id;
        this.studentName = studentName;
        this.location = location;
        this.mobile = mobile;
        this.totalDays = totalDays;
        this.completedDays = completedDays;
        this.weeklyDays = weeklyDays;
        this.remuneration = remuneration;
        this.sDate = sDate;
        this.eDate = eDate;
    }





    public TuitionInfo(String id, String studentName, String location, String mobile, String totalDays, String completedDays, String weeklyDays, String remuneration) {
        this.id = id;
        this.studentName = studentName;
        this.location = location;
        this.mobile = mobile;
        this.totalDays = totalDays;
        this.completedDays = completedDays;
        this.weeklyDays = weeklyDays;
        this.remuneration = remuneration;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getTotalDays() {
        return totalDays;
    }

    public void setTotalDays(String totalDays) {
        this.totalDays = totalDays;
    }

    public String getCompletedDays() {
        return completedDays;
    }

    public void setCompletedDays(String completedDays) {
        this.completedDays = completedDays;
    }

    public String getWeeklyDays() {
        return weeklyDays;
    }

    public void setWeeklyDays(String weeklyDays) {
        this.weeklyDays = weeklyDays;
    }

    public String getRemuneration() {
        return remuneration;
    }

    public void setRemuneration(String remuneration) {
        this.remuneration = remuneration;
    }


    public String getsDate() {
        return sDate;
    }

    public void setsDate(String sDate) {
        this.sDate = sDate;
    }

    public String geteDate() {
        return eDate;
    }

    public void seteDate(String eDate) {
        this.eDate = eDate;
    }
}
