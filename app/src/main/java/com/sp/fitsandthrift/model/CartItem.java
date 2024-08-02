package com.sp.fitsandthrift.model;

public class CartItem {
    private String imageUri;
    private String itemDescription;
    private String itemID;
    private String userID;

    public CartItem() {
    }

    public CartItem(String imageUri, String itemDescription, String itemID, String userID) {
        this.imageUri = imageUri;
        this.itemDescription = itemDescription;
        this.itemID = itemID;
        this.userID = userID;
    }

    public String getImageUri() {
        return imageUri;
    }
    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }
    public String getItemID() {
        return itemID;
    }
    public void setItemID(String itemID) {
        this.itemID = itemID;
    }
    public String getUserID() {
        return userID;
    }
    public void setUserID(String userID) {
        this.userID = userID;
    }
}