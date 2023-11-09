package com.jeysidg.e_notify;

public class NotificationModel {

    private String VisitorsName, timestamp, CompanyClient, PurposeofVisit, sender;

    public NotificationModel() {
    }

    public NotificationModel(String visitorsName, String timestamp, String companyClient, String purposeofVisit, String sender) {
        VisitorsName = visitorsName;
        this.timestamp = timestamp;
        CompanyClient = companyClient;
        PurposeofVisit = purposeofVisit;
        this.sender = sender;
    }

    public String getVisitorsName() {
        return VisitorsName;
    }

    public void setVisitorsName(String visitorsName) {
        VisitorsName = visitorsName;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getCompanyClient() {
        return CompanyClient;
    }

    public void setCompanyClient(String companyClient) {
        CompanyClient = companyClient;
    }

    public String getPurposeofVisit() {
        return PurposeofVisit;
    }

    public void setPurposeofVisit(String purposeofVisit) {
        PurposeofVisit = purposeofVisit;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }
}

