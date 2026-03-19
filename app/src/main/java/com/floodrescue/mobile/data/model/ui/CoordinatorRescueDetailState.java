package com.floodrescue.mobile.data.model.ui;

import java.util.List;

public class CoordinatorRescueDetailState {
    private final long id;
    private final String code;
    private final String citizenName;
    private final String citizenPhone;
    private final String status;
    private final String priority;
    private final int peopleCount;
    private final String description;
    private final String address;
    private final String locationDescription;
    private final Double latitude;
    private final Double longitude;
    private final boolean locationVerified;
    private final boolean waitingForTeam;
    private final String masterRequestCode;
    private final String cancelNote;
    private final List<AttachmentItem> attachments;
    private final List<TimelineItem> timeline;

    public CoordinatorRescueDetailState(long id, String code, String citizenName, String citizenPhone, String status, String priority, int peopleCount, String description, String address, String locationDescription, Double latitude, Double longitude, boolean locationVerified, boolean waitingForTeam, String masterRequestCode, String cancelNote, List<AttachmentItem> attachments, List<TimelineItem> timeline) {
        this.id = id;
        this.code = code;
        this.citizenName = citizenName;
        this.citizenPhone = citizenPhone;
        this.status = status;
        this.priority = priority;
        this.peopleCount = peopleCount;
        this.description = description;
        this.address = address;
        this.locationDescription = locationDescription;
        this.latitude = latitude;
        this.longitude = longitude;
        this.locationVerified = locationVerified;
        this.waitingForTeam = waitingForTeam;
        this.masterRequestCode = masterRequestCode;
        this.cancelNote = cancelNote;
        this.attachments = attachments;
        this.timeline = timeline;
    }

    public long getId() { return id; }
    public String getCode() { return code; }
    public String getCitizenName() { return citizenName; }
    public String getCitizenPhone() { return citizenPhone; }
    public String getStatus() { return status; }
    public String getPriority() { return priority; }
    public int getPeopleCount() { return peopleCount; }
    public String getDescription() { return description; }
    public String getAddress() { return address; }
    public String getLocationDescription() { return locationDescription; }
    public Double getLatitude() { return latitude; }
    public Double getLongitude() { return longitude; }
    public boolean isLocationVerified() { return locationVerified; }
    public boolean isWaitingForTeam() { return waitingForTeam; }
    public String getMasterRequestCode() { return masterRequestCode; }
    public String getCancelNote() { return cancelNote; }
    public List<AttachmentItem> getAttachments() { return attachments; }
    public List<TimelineItem> getTimeline() { return timeline; }

    public static class AttachmentItem {
        private final String fileUrl;

        public AttachmentItem(String fileUrl) {
            this.fileUrl = fileUrl;
        }

        public String getFileUrl() {
            return fileUrl;
        }
    }

    public static class TimelineItem {
        private final String eventType;
        private final String actorName;
        private final String note;
        private final String createdAt;

        public TimelineItem(String eventType, String actorName, String note, String createdAt) {
            this.eventType = eventType;
            this.actorName = actorName;
            this.note = note;
            this.createdAt = createdAt;
        }

        public String getEventType() { return eventType; }
        public String getActorName() { return actorName; }
        public String getNote() { return note; }
        public String getCreatedAt() { return createdAt; }
    }
}
