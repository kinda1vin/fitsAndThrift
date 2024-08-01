package com.sp.fitsandthrift.model;

public class Notification {
    private String senderUsername;
    private String message;
    private String senderProfilePicUrl;

    // Default constructor for Firestore
    public Notification() {}

    public Notification(String senderUsername, String message, String senderProfilePicUrl) {
        this.senderUsername = senderUsername;
        this.message = message;
        this.senderProfilePicUrl = senderProfilePicUrl;
    }

    // Getters and setters
    public String getSenderUsername() {
        return senderUsername;
    }

    public void setSenderUsername(String senderUsername) {
        this.senderUsername = senderUsername;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSenderProfilePicUrl() {
        return senderProfilePicUrl;
    }

    public void setSenderProfilePicUrl(String senderProfilePicUrl) {
        this.senderProfilePicUrl = senderProfilePicUrl;
    }
}
