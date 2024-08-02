package com.sp.fitsandthrift.model;

public class Review {
    private String senderId;
    private String receiverId;
    private String reviewText;
    private int rating;

    public Review() {
        // Default constructor required for calls to DataSnapshot.getValue(Review.class)
    }

    public Review(String senderId, String receiverId, String reviewText, int rating) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.reviewText = reviewText;
        this.rating = rating;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getReviewText() {
        return reviewText;
    }

    public void setReviewText(String reviewText) {
        this.reviewText = reviewText;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
}
