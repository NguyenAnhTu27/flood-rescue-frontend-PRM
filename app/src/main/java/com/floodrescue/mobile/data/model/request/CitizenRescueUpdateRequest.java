package com.floodrescue.mobile.data.model.request;

public class CitizenRescueUpdateRequest {

    private final Integer affectedPeopleCount;
    private final String description;
    private final String addressText;
    private final String priority;
    private final java.util.List<AttachmentPayload> attachments;

    public CitizenRescueUpdateRequest(
            Integer affectedPeopleCount,
            String description,
            String addressText,
            String priority,
            java.util.List<AttachmentPayload> attachments
    ) {
        this.affectedPeopleCount = affectedPeopleCount;
        this.description = description;
        this.addressText = addressText;
        this.priority = priority;
        this.attachments = attachments;
    }

    public Integer getAffectedPeopleCount() {
        return affectedPeopleCount;
    }

    public String getDescription() {
        return description;
    }

    public String getAddressText() {
        return addressText;
    }

    public String getPriority() {
        return priority;
    }

    public java.util.List<AttachmentPayload> getAttachments() {
        return attachments;
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
