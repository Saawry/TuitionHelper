package com.gadware.tution.models;

public class User {
    private String ImageUri,uid,email, password,address, mobile,name;

    public User() {
    }

    public User(String uid, String email, String password, String address, String mobile, String name ) {
        this.uid = uid;
        this.email = email;
        this.password = password;
        this.address = address;
        this.mobile = mobile;
        this.name = name;
    }

    public User(String ImageUri, String uid, String email, String password, String address, String mobile, String name) {
        this.ImageUri = ImageUri;
        this.uid = uid;
        this.email = email;
        this.password = password;
        this.address = address;
        this.mobile = mobile;
        this.name = name;
    }


    public String getImageUrl() {
        return ImageUri;
    }

    public void setImageUrl(String imageUrl) {
        this.ImageUri = imageUrl;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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
