package com.sp.fitsandthrift;

import android.net.Uri;

import java.io.Serializable;

public class Item implements Serializable {
    private static final long serialVersionUID = 1L;
    String itemDescription;
    String imageUri;
    String itemGender;
    String itemType;
    String color;
    String size;
    String itemCondition;
    String gender;
    String userID;
    String itemID;
    boolean trade_status;

    public String getItemID() {
        return itemID;
    }

    public void setItemID(String itemID) {
        this.itemID = itemID;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    // Assuming userID is the owner ID
    public String getOwnerId() {
        return userID;
    }

    public Item() {}

    public Item(String itemDescription, String imageUri) {
        this.itemDescription = itemDescription;
        this.imageUri = imageUri;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public String getItemGender() {
        return itemGender;
    }

    public void setItemGender(String gender) {
        this.itemGender = gender;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getItemCondition() {
        return itemCondition;
    }

    public void setItemCondition(String itemCondition) {
        this.itemCondition = itemCondition;
    }

    public Uri getImageUriAsUri() {
        if (imageUri != null) {
            return Uri.parse(imageUri);
        } else {
            // return null or a default Uri if imageUri is null
            return null;
        }
    }
    public boolean isTrade_status() {
        return trade_status;
    }

    public void setTrade_status(boolean trade_status) {
        this.trade_status = trade_status;
    }

}
