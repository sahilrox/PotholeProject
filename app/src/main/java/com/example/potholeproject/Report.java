package com.example.potholeproject;

public class Report {
    private String latitude;
    private String longitude;
    private String imageURL;
    private String userID;
    private boolean serviced;
    private int noOfComplaints;
    private String reportID;
    private String description;


    public Report(String latitude, String longitude, String imageURL, String userID, String reportID,
                  String description) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.imageURL = imageURL;
        this.userID = userID;
        this.serviced = false;
        this.noOfComplaints = 1;
        this.reportID = reportID;
        this.description = description;

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

    public String getUserID() {
        return userID;
    }

    public boolean isServiced() {
        return serviced;
    }

    public int getNoOfComplaints() {
        return noOfComplaints;
    }

    public String getReportID() {
        return reportID;
    }

    public String getDescription() {
        return description;
    }
}
