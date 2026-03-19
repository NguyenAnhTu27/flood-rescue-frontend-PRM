package com.floodrescue.mobile.data.model.ui;

import java.util.List;

public class CitizenRescueDetailState {

    private final long id;
    private final String code;
    private final String status;
    private final String priority;
    private final int affectedPeopleCount;
    private final String description;
    private final String addressText;
    private final Double latitude;
    private final Double longitude;
    private final String locationDescription;
    private final boolean locationVerified;
    private final boolean waitingForTeam;
    private final String coordinatorCancelNote;
    private final boolean waitingCitizenRescueConfirmation;
    private final String rescueResultConfirmationStatus;
    private final String rescueResultConfirmationNote;
    private final String createdAt;
    private final String updatedAt;
    private final List<AttachmentItem> attachments;
    private final List<TimelineItem> timeline;

    public CitizenRescueDetailState(
            long id,
            String code,
            String status,
            String priority,
            int affectedPeopleCount,
            String description,
            String addressText,
            Double latitude,
            Double longitude,
            String locationDescription,
            boolean locationVerified,
            boolean waitingForTeam,
            String coordinatorCancelNote,
            boolean waitingCitizenRescueConfirmation,
            String rescueResultConfirmationStatus,
            String rescueResultConfirmationNote,
            String createdAt,
            String updatedAt,
            List<AttachmentItem> attachments,
            List<TimelineItem> timeline
    ) {
        this.id = id;
        this.code = code;
        this.status = status;
        this.priority = priority;
        this.affectedPeopleCount = affectedPeopleCount;
        this.description = description;
        this.addressText = addressText;
        this.latitude = latitude;
        this.longitude = longitude;
        this.locationDescription = locationDescription;
        this.locationVerified = locationVerified;
        this.waitingForTeam = waitingForTeam;
        this.coordinatorCancelNote = coordinatorCancelNote;
        this.waitingCitizenRescueConfirmation = waitingCitizenRescueConfirmation;
        this.rescueResultConfirmationStatus = rescueResultConfirmationStatus;
        this.rescueResultConfirmationNote = rescueResultConfirmationNote;
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

    public String getStatus() {
        return status;
    }

    public String getPriority() {
        return priority;
    }

    public int getAffectedPeopleCount() {
        return affectedPeopleCount;
    }

    public String getDescription() {
        return description;
    }

    public String getAddressText() {
        return addressText;
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

    public boolean isWaitingForTeam() {
        return waitingForTeam;
    }

    public String getCoordinatorCancelNote() {
        return coordinatorCancelNote;
    }

    public boolean isWaitingCitizenRescueConfirmation() {
        return waitingCitizenRescueConfirmation;
    }

    public String getRescueResultConfirmationStatus() {
        return rescueResultConfirmationStatus;
    }

    public String getRescueResultConfirmationNote() {
        return rescueResultConfirmationNote;
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
        private final String createdAt;

        public AttachmentItem(long id, String fileUrl, String fileType, String createdAt) {
            this.id = id;
            this.fileUrl = fileUrl;
            this.fileType = fileType;
            this.createdAt = createdAt;
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

        public String getCreatedAt() {
            return createdAt;
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

        public TimelineItem(
                long id,
                String actorName,
                String eventType,
                String fromStatus,
                String toStatus,
                String note,
                String createdAt
        ) {
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
