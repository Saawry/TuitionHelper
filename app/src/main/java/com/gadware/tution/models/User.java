package com.gadware.tution.models;

public class User {
    private String uid,email,address, mobile,name;

    public User() {
    }

    public User(String uid, String email,  String address, String mobile, String name ) {
        this.uid = uid;
        this.email = email;
        this.address = address;
        this.mobile = mobile;
        this.name = name;
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
