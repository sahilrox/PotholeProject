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
        if(imageURL == null) this.imageURL = "no url";
        else this.imageURL = imageURL;
        this.userID = userID;
        this.serviced = false;
        this.noOfComplaints = 1;
        this.reportID = reportID;
        this.description = description;

    }

    public Report(){

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

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public void setServiced(boolean serviced) {
        this.serviced = serviced;
    }

    public void setNoOfComplaints(int noOfComplaints) {
        this.noOfComplaints = noOfComplaints;
    }

    public void setReportID(String reportID) {
        this.reportID = reportID;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
