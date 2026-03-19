package com.floodrescue.mobile.data.model.ui;

import java.util.List;

public class RescuerTaskGroupDetailState {

    private final long id;
    private final String code;
    private final String status;
    private final String note;
    private final String teamName;
    private final String createdByName;
    private final String createdAt;
    private final String updatedAt;
    private final List<RequestItem> requests;
    private final List<AssignmentItem> assignments;
    private final List<TimelineItem> timeline;
    private final List<EmergencyAckItem> emergencyAcks;

    public RescuerTaskGroupDetailState(
            long id,
            String code,
            String status,
            String note,
            String teamName,
            String createdByName,
            String createdAt,
            String updatedAt,
            List<RequestItem> requests,
            List<AssignmentItem> assignments,
            List<TimelineItem> timeline,
            List<EmergencyAckItem> emergencyAcks
    ) {
        this.id = id;
        this.code = code;
        this.status = status;
        this.note = note;
        this.teamName = teamName;
        this.createdByName = createdByName;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.requests = requests;
        this.assignments = assignments;
        this.timeline = timeline;
        this.emergencyAcks = emergencyAcks;
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

    public String getNote() {
        return note;
    }

    public String getTeamName() {
        return teamName;
    }

    public String getCreatedByName() {
        return createdByName;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public List<RequestItem> getRequests() {
        return requests;
    }

    public List<AssignmentItem> getAssignments() {
        return assignments;
    }

    public List<TimelineItem> getTimeline() {
        return timeline;
    }

    public List<EmergencyAckItem> getEmergencyAcks() {
        return emergencyAcks;
    }

    public static class RequestItem {
        private final long id;
        private final String code;
        private final String status;
        private final String priority;
        private final int peopleCount;
        private final String address;
        private final String locationDescription;
        private final String description;
        private final String citizenName;
        private final String citizenPhone;
        private final String createdAt;
        private final String updatedAt;
        private final boolean locationVerified;
        private final boolean emergency;

        public RequestItem(
                long id,
                String code,
                String status,
                String priority,
                int peopleCount,
                String address,
                String locationDescription,
                String description,
                String citizenName,
                String citizenPhone,
                String createdAt,
                String updatedAt,
                boolean locationVerified,
                boolean emergency
        ) {
            this.id = id;
            this.code = code;
            this.status = status;
            this.priority = priority;
            this.peopleCount = peopleCount;
            this.address = address;
            this.locationDescription = locationDescription;
            this.description = description;
            this.citizenName = citizenName;
            this.citizenPhone = citizenPhone;
            this.createdAt = createdAt;
            this.updatedAt = updatedAt;
            this.locationVerified = locationVerified;
            this.emergency = emergency;
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

        public int getPeopleCount() {
            return peopleCount;
        }

        public String getAddress() {
            return address;
        }

        public String getLocationDescription() {
            return locationDescription;
        }

        public String getDescription() {
            return description;
        }

        public String getCitizenName() {
            return citizenName;
        }

        public String getCitizenPhone() {
            return citizenPhone;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public String getUpdatedAt() {
            return updatedAt;
        }

        public boolean isLocationVerified() {
            return locationVerified;
        }

        public boolean isEmergency() {
            return emergency;
        }
    }

    public static class AssignmentItem {
        private final long id;
        private final String teamName;
        private final String assetCode;
        private final String assetName;
        private final String assignedByName;
        private final String assignedAt;
        private final boolean active;

        public AssignmentItem(
                long id,
                String teamName,
                String assetCode,
                String assetName,
                String assignedByName,
                String assignedAt,
                boolean active
        ) {
            this.id = id;
            this.teamName = teamName;
            this.assetCode = assetCode;
            this.assetName = assetName;
            this.assignedByName = assignedByName;
            this.assignedAt = assignedAt;
            this.active = active;
        }

        public long getId() {
            return id;
        }

        public String getTeamName() {
            return teamName;
        }

        public String getAssetCode() {
            return assetCode;
        }

        public String getAssetName() {
            return assetName;
        }

        public String getAssignedByName() {
            return assignedByName;
        }

        public String getAssignedAt() {
            return assignedAt;
        }

        public boolean isActive() {
            return active;
        }
    }

    public static class TimelineItem {
        private final long id;
        private final String actorName;
        private final String eventType;
        private final String note;
        private final String createdAt;

        public TimelineItem(long id, String actorName, String eventType, String note, String createdAt) {
            this.id = id;
            this.actorName = actorName;
            this.eventType = eventType;
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

        public String getNote() {
            return note;
        }

        public String getCreatedAt() {
            return createdAt;
        }
    }

    public static class EmergencyAckItem {
        private final long coordinatorId;
        private final String coordinatorName;
        private final boolean read;
        private final String actionStatus;
        private final String actionNote;
        private final long queueRequestId;
        private final String acknowledgedAt;

        public EmergencyAckItem(
                long coordinatorId,
                String coordinatorName,
                boolean read,
                String actionStatus,
                String actionNote,
                long queueRequestId,
                String acknowledgedAt
        ) {
            this.coordinatorId = coordinatorId;
            this.coordinatorName = coordinatorName;
            this.read = read;
            this.actionStatus = actionStatus;
            this.actionNote = actionNote;
            this.queueRequestId = queueRequestId;
            this.acknowledgedAt = acknowledgedAt;
        }

        public long getCoordinatorId() {
            return coordinatorId;
        }

        public String getCoordinatorName() {
            return coordinatorName;
        }

        public boolean isRead() {
            return read;
        }

        public String getActionStatus() {
            return actionStatus;
        }

        public String getActionNote() {
            return actionNote;
        }

        public long getQueueRequestId() {
            return queueRequestId;
        }

        public String getAcknowledgedAt() {
            return acknowledgedAt;
        }
    }
}
