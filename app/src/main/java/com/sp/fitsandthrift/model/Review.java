package com.sp.fitsandthrift.model;

public class Review {
    String user;
    String reviewuserpic;
    String reviewtext;

    public Review(String user, String reviewuserpic, String reviewtext) {
        this.user = user;
        this.reviewuserpic = reviewuserpic;
        this.reviewtext = reviewtext;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getReviewuserpic() {
        return reviewuserpic;
    }

    public void setReviewuserpic(String reviewuserpic) {
        this.reviewuserpic = reviewuserpic;
    }

    public String getReviewtext() {
        return reviewtext;
    }

    public void setReviewtext(String reviewtext) {
        this.reviewtext = reviewtext;
    }
}