package com.floodrescue.mobile.data.model.ui;

public class RescuerTaskItem {

    private final long requestId;
    private final long taskGroupId;
    private final String requestCode;
    private final String taskGroupCode;
    private final String citizenName;
    private final String citizenPhone;
    private final String address;
    private final String description;
    private final String priority;
    private final String status;
    private final String updatedAt;
    private final int peopleCount;
    private final boolean locationVerified;
    private final boolean emergency;

    public RescuerTaskItem(
            long requestId,
            long taskGroupId,
            String requestCode,
            String taskGroupCode,
            String citizenName,
            String citizenPhone,
            String address,
            String description,
            String priority,
            String status,
            String updatedAt,
            int peopleCount,
            boolean locationVerified,
            boolean emergency
    ) {
        this.requestId = requestId;
        this.taskGroupId = taskGroupId;
        this.requestCode = requestCode;
        this.taskGroupCode = taskGroupCode;
        this.citizenName = citizenName;
        this.citizenPhone = citizenPhone;
        this.address = address;
        this.description = description;
        this.priority = priority;
        this.status = status;
        this.updatedAt = updatedAt;
        this.peopleCount = peopleCount;
        this.locationVerified = locationVerified;
        this.emergency = emergency;
    }

    public long getRequestId() {
        return requestId;
    }

    public long getTaskGroupId() {
        return taskGroupId;
    }

    public String getRequestCode() {
        return requestCode;
    }

    public String getTaskGroupCode() {
        return taskGroupCode;
    }

    public String getCitizenName() {
        return citizenName;
    }

    public String getCitizenPhone() {
        return citizenPhone;
    }

    public String getAddress() {
        return address;
    }

    public String getDescription() {
        return description;
    }

    public String getPriority() {
        return priority;
    }

    public String getStatus() {
        return status;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public int getPeopleCount() {
        return peopleCount;
    }

    public boolean isLocationVerified() {
        return locationVerified;
    }

    public boolean isEmergency() {
        return emergency;
    }
}
