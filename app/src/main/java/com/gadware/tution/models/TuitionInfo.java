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
    private String Status;
    private String ImageUri;

    public TuitionInfo() {
    }

    public TuitionInfo(String id, String studentName, String location, String mobile, String totalDays, String completedDays, String weeklyDays, String remuneration, String status, String imageUri) {
        this.id = id;
        this.studentName = studentName;
        this.location = location;
        this.mobile = mobile;
        this.totalDays = totalDays;
        this.completedDays = completedDays;
        this.weeklyDays = weeklyDays;
        this.remuneration = remuneration;
        Status = status;
        ImageUri = imageUri;
    }

    public TuitionInfo(String id, String studentName, String location, String mobile, String totalDays, String completedDays, String weeklyDays, String remuneration, String status) {
        this.id = id;
        this.studentName = studentName;
        this.location = location;
        this.mobile = mobile;
        this.totalDays = totalDays;
        this.completedDays = completedDays;
        this.weeklyDays = weeklyDays;
        this.remuneration = remuneration;
        Status = status;
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

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getImageUri() {
        return ImageUri;
    }

    public void setImageUri(String imageUri) {
        ImageUri = imageUri;
    }
}
