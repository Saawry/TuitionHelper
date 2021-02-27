package com.gadware.tution.models;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "UserInfo")
public class User {
    @PrimaryKey
    private String uid;
    private String email;
    private String address;
    private String mobile;
    private String name;
    private String status;

    public User(String uid, String name,String email, String address, String mobile, String status) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.address = address;
        this.mobile = mobile;
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
}
