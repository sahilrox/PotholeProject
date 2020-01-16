package com.example.potholeproject;

public class Report {
    public String latitude, longitude, imageURL;

    public Report(String latitude, String longitude, String imageURL) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.imageURL = imageURL;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getImageURL() {
        return imageURL;
    }
}
