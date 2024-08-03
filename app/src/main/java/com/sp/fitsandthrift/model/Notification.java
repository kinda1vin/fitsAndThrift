package com.sp.fitsandthrift.model;

import java.util.ArrayList;

public class Notification {
    private String senderUsername;
    private String message;
    private String senderProfilePicUrl;
    private ArrayList<String> selectedItems;
    private String desiredItemId;
    private String ownerId;

    // Default constructor for Firestore
    public Notification() {}

    public Notification(String senderUsername, String message, String senderProfilePicUrl, String ownerId, ArrayList<String> selectedItems, String desiredItemId) {
        this.senderUsername = senderUsername;
        this.message = message;
        this.senderProfilePicUrl = senderProfilePicUrl;
        this.ownerId = ownerId;
        this.selectedItems = selectedItems;
        this.desiredItemId = desiredItemId;
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
    public ArrayList<String> getSelectedItems() {
        return selectedItems;
    }

    public void setSelectedItems(ArrayList<String> selectedItems) {
        this.selectedItems = selectedItems;
    }

    public String getDesiredItemId() {
        return desiredItemId;
    }

    public void setDesiredItemId(String desiredItemId) {
        this.desiredItemId = desiredItemId;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }
}
