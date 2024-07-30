package com.sp.fitsandthrift.model;

public class Notification {
    String heading;
    int titleImage;

    public Notification(String heading, int titleImage) {
        this.heading = heading;
        this.titleImage = titleImage;
    }

    public String getHeading() {
        return heading;
    }

    public int getTitleImage() {
        return titleImage;
    }
}