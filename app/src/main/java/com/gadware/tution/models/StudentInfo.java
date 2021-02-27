package com.gadware.tution.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class StudentInfo {
    @PrimaryKey
    private String id;
    private String name;
    private String className;
    private String institute;
    private String fatherName;
    private String address;
    private String phone;
    private String email;
    private String reference;

    public StudentInfo(String id, String name, String className, String institute, String fatherName, String address, String phone, String email, String reference) {
        this.id = id;
        this.name = name;
        this.className = className;
        this.institute = institute;
        this.fatherName = fatherName;
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.reference = reference;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getInstitute() {
        return institute;
    }

    public void setInstitute(String institute) {
        this.institute = institute;
    }

    public String getFatherName() {
        return fatherName;
    }

    public void setFatherName(String fatherName) {
        this.fatherName = fatherName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
