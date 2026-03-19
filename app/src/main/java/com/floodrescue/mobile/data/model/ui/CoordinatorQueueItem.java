package com.floodrescue.mobile.data.model.ui;

public class CoordinatorQueueItem {

    private final long id;
    private final String code;
    private final String citizenName;
    private final String phoneNumber;
    private final int peopleCount;
    private final String address;
    private final String priority;
    private final String status;
    private final boolean locationVerified;
    private final String teamName;
    private final String updatedAt;

    public CoordinatorQueueItem(
            long id,
            String code,
            String citizenName,
            String phoneNumber,
            int peopleCount,
            String address,
            String priority,
            String status,
            boolean locationVerified,
            String teamName,
            String updatedAt
    ) {
        this.id = id;
        this.code = code;
        this.citizenName = citizenName;
        this.phoneNumber = phoneNumber;
        this.peopleCount = peopleCount;
        this.address = address;
        this.priority = priority;
        this.status = status;
        this.locationVerified = locationVerified;
        this.teamName = teamName;
        this.updatedAt = updatedAt;
    }

    public long getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getCitizenName() {
        return citizenName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public int getPeopleCount() {
        return peopleCount;
    }

    public String getAddress() {
        return address;
    }

    public String getPriority() {
        return priority;
    }

    public String getStatus() {
        return status;
    }

    public boolean isLocationVerified() {
        return locationVerified;
    }

    public String getTeamName() {
        return teamName;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }
}
