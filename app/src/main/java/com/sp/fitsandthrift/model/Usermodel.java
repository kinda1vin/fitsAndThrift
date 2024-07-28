package com.sp.fitsandthrift.model;

import com.google.firebase.Firebase;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sp.fitsandthrift.Firebase.Util;

public class Usermodel {
    private Timestamp createdTimestamp;
    private String email;
    private String username;
    private String phoneNumber;
    private String profilePicUrl; // Add this field

    private String currentUserId = Util.currentUserId();


    public Usermodel() {

    }

    public Usermodel(String email) {
        this.email = email;
    }

    public String getCurrentUserId() {
        return currentUserId;
    }

    public void setCurrentUserId(String currentUserId) {
        this.currentUserId = currentUserId;
    }

    public Usermodel(String username, String email, String phoneNumber, String currentUserId) {
        this.username = username;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }


    // Getters and Setters

    public Timestamp getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(Timestamp createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getProfilePicUrl() {
        return profilePicUrl;
    }

    public void setProfilePicUrl(String profilePicUrl) {
        this.profilePicUrl = profilePicUrl;
    }
}

