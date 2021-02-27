package com.gadware.tution.models;

import androidx.room.Entity;
import androidx.room.Index;

@Entity(tableName = "StudentList",primaryKeys = {"batchId","studentId"}, indices = {@Index(value = {"batchId","studentId"}, unique = true)})
public class StudentList {
    private String batchId;
    private String studentId;

    public StudentList(String batchId, String studentId) {
        this.batchId = batchId;
        this.studentId = studentId;
    }

    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }
}
