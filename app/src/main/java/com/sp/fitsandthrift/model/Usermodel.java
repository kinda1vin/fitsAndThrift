package com.sp.fitsandthrift.model;

public class Usermodel {
    private String email;
    private String username;
    private String gender;
    private String phoneNumber;
    private boolean detailsProvided;
    private String profilePicUrl; // Add this field

    public Usermodel() {
    }

    public Usermodel(String email) {
        this.email = email;
        this.detailsProvided = false;
    }

    public Usermodel(String email, String username, String gender, String phoneNumber, boolean detailsProvided) {
        this.email = email;
        this.username = username;
        this.gender = gender;
        this.phoneNumber = phoneNumber;
        this.detailsProvided = detailsProvided;
    }

    // Getters and Setters
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

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public boolean isDetailsProvided() {
        return detailsProvided;
    }

    public void setDetailsProvided(boolean detailsProvided) {
        this.detailsProvided = detailsProvided;
    }

    public String getProfilePicUrl() {
        return profilePicUrl;
    }

    public void setProfilePicUrl(String profilePicUrl) {
        this.profilePicUrl = profilePicUrl;
    }
}

