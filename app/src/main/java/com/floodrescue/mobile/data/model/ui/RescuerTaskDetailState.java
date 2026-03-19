package com.floodrescue.mobile.data.model.ui;

import java.util.List;

public class RescuerTaskDetailState {

    private final long id;
    private final String code;
    private final String citizenName;
    private final String citizenPhone;
    private final String status;
    private final String priority;
    private final int peopleCount;
    private final String description;
    private final String address;
    private final Double latitude;
    private final Double longitude;
    private final String locationDescription;
    private final boolean locationVerified;
    private final String createdAt;
    private final String updatedAt;
    private final List<AttachmentItem> attachments;
    private final List<TimelineItem> timeline;

    public RescuerTaskDetailState(
            long id,
            String code,
            String citizenName,
            String citizenPhone,
            String status,
            String priority,
            int peopleCount,
            String description,
            String address,
            Double latitude,
            Double longitude,
            String locationDescription,
            boolean locationVerified,
            String createdAt,
            String updatedAt,
            List<AttachmentItem> attachments,
            List<TimelineItem> timeline
    ) {
        this.id = id;
        this.code = code;
        this.citizenName = citizenName;
        this.citizenPhone = citizenPhone;
        this.status = status;
        this.priority = priority;
        this.peopleCount = peopleCount;
        this.description = description;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.locationDescription = locationDescription;
        this.locationVerified = locationVerified;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.attachments = attachments;
        this.timeline = timeline;
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

    public String getCitizenPhone() {
        return citizenPhone;
    }

    public String getStatus() {
        return status;
    }

    public String getPriority() {
        return priority;
    }

    public int getPeopleCount() {
        return peopleCount;
    }

    public String getDescription() {
        return description;
    }

    public String getAddress() {
        return address;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public String getLocationDescription() {
        return locationDescription;
    }

    public boolean isLocationVerified() {
        return locationVerified;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public List<AttachmentItem> getAttachments() {
        return attachments;
    }

    public List<TimelineItem> getTimeline() {
        return timeline;
    }

    public static class AttachmentItem {
        private final long id;
        private final String fileUrl;
        private final String fileType;

        public AttachmentItem(long id, String fileUrl, String fileType) {
            this.id = id;
            this.fileUrl = fileUrl;
            this.fileType = fileType;
        }

        public long getId() {
            return id;
        }

        public String getFileUrl() {
            return fileUrl;
        }

        public String getFileType() {
            return fileType;
        }
    }

    public static class TimelineItem {
        private final long id;
        private final String actorName;
        private final String eventType;
        private final String fromStatus;
        private final String toStatus;
        private final String note;
        private final String createdAt;

        public TimelineItem(long id, String actorName, String eventType, String fromStatus, String toStatus, String note, String createdAt) {
            this.id = id;
            this.actorName = actorName;
            this.eventType = eventType;
            this.fromStatus = fromStatus;
            this.toStatus = toStatus;
            this.note = note;
            this.createdAt = createdAt;
        }

        public long getId() {
            return id;
        }

        public String getActorName() {
            return actorName;
        }

        public String getEventType() {
            return eventType;
        }

        public String getFromStatus() {
            return fromStatus;
        }

        public String getToStatus() {
            return toStatus;
        }

        public String getNote() {
            return note;
        }

        public String getCreatedAt() {
            return createdAt;
        }
    }
}
