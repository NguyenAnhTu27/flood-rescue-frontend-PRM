package com.floodrescue.mobile.data.model.response;

public class RescueChatMessageResponse {
    private Long id;
    private Long rescueRequestId;
    private Long senderId;
    private String senderName;
    private String senderRole;
    private String message;
    private String createdAt;

    public Long getId() {
        return id;
    }

    public Long getRescueRequestId() {
        return rescueRequestId;
    }

    public Long getSenderId() {
        return senderId;
    }

    public String getSenderName() {
        return senderName;
    }

    public String getSenderRole() {
        return senderRole;
    }

    public String getMessage() {
        return message;
    }

    public String getCreatedAt() {
        return createdAt;
    }
}
