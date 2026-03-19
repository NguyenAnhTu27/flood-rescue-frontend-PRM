package com.floodrescue.mobile.data.model.request;

import java.util.List;

public class RescueRequestCreatePayload {

    private final Integer affectedPeopleCount;
    private final String description;
    private final String addressText;
    private final Double latitude;
    private final Double longitude;
    private final String locationDescription;
    private final String priority;
    private final List<AttachmentPayload> attachments;

    public RescueRequestCreatePayload(
            Integer affectedPeopleCount,
            String description,
            String addressText,
            Double latitude,
            Double longitude,
            String locationDescription,
            String priority,
            List<AttachmentPayload> attachments
    ) {
        this.affectedPeopleCount = affectedPeopleCount;
        this.description = description;
        this.addressText = addressText;
        this.latitude = latitude;
        this.longitude = longitude;
        this.locationDescription = locationDescription;
        this.priority = priority;
        this.attachments = attachments;
    }

    public static class AttachmentPayload {
        private final String fileUrl;
        private final String fileType;

        public AttachmentPayload(String fileUrl, String fileType) {
            this.fileUrl = fileUrl;
            this.fileType = fileType;
        }

        public String getFileUrl() {
            return fileUrl;
        }

        public String getFileType() {
            return fileType;
        }
    }
}
