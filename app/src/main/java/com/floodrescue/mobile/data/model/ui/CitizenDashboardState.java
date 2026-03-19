package com.floodrescue.mobile.data.model.ui;

public class CitizenDashboardState {

    private final String fullName;
    private final String role;
    private final boolean rescueRequestBlocked;
    private final String rescueRequestBlockedReason;
    private final int unreadCount;
    private final HighlightNotification highlightNotification;
    private final RescueSummary latestRequest;

    public CitizenDashboardState(
            String fullName,
            String role,
            boolean rescueRequestBlocked,
            String rescueRequestBlockedReason,
            int unreadCount,
            HighlightNotification highlightNotification,
            RescueSummary latestRequest
    ) {
        this.fullName = fullName;
        this.role = role;
        this.rescueRequestBlocked = rescueRequestBlocked;
        this.rescueRequestBlockedReason = rescueRequestBlockedReason;
        this.unreadCount = unreadCount;
        this.highlightNotification = highlightNotification;
        this.latestRequest = latestRequest;
    }

    public String getFullName() {
        return fullName;
    }

    public String getRole() {
        return role;
    }

    public boolean isRescueRequestBlocked() {
        return rescueRequestBlocked;
    }

    public String getRescueRequestBlockedReason() {
        return rescueRequestBlockedReason;
    }

    public int getUnreadCount() {
        return unreadCount;
    }

    public HighlightNotification getHighlightNotification() {
        return highlightNotification;
    }

    public RescueSummary getLatestRequest() {
        return latestRequest;
    }

    public static class HighlightNotification {
        private final String title;
        private final String content;
        private final boolean urgent;

        public HighlightNotification(String title, String content, boolean urgent) {
            this.title = title;
            this.content = content;
            this.urgent = urgent;
        }

        public String getTitle() {
            return title;
        }

        public String getContent() {
            return content;
        }

        public boolean isUrgent() {
            return urgent;
        }
    }

    public static class RescueSummary {
        private final long id;
        private final String code;
        private final String status;
        private final String priority;
        private final String addressText;
        private final String description;
        private final String updatedAt;
        private final boolean waitingForTeam;
        private final boolean locationVerified;

        public RescueSummary(
                long id,
                String code,
                String status,
                String priority,
                String addressText,
                String description,
                String updatedAt,
                boolean waitingForTeam,
                boolean locationVerified
        ) {
            this.id = id;
            this.code = code;
            this.status = status;
            this.priority = priority;
            this.addressText = addressText;
            this.description = description;
            this.updatedAt = updatedAt;
            this.waitingForTeam = waitingForTeam;
            this.locationVerified = locationVerified;
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

        public String getAddressText() {
            return addressText;
        }

        public String getDescription() {
            return description;
        }

        public String getUpdatedAt() {
            return updatedAt;
        }

        public boolean isWaitingForTeam() {
            return waitingForTeam;
        }

        public boolean isLocationVerified() {
            return locationVerified;
        }
    }
}
