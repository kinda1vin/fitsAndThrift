package com.sp.fitsandthrift.model;

public class Usermodel {
    private String email;
    private String phoneNumber;
    private String name;
    private String gender;

    // Default constructor required for calls to DataSnapshot.getValue(Usermodel.class)
    public Usermodel() {
    }

    // Constructor with all fields
    public Usermodel(String email) {
        this.email = email;
    }

    // Getter and setter for email

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


}
