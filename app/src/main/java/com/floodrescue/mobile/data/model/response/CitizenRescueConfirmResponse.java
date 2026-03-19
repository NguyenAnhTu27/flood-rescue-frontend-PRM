package com.floodrescue.mobile.data.model.response;

public class CitizenRescueConfirmResponse {

    private boolean rescued;
    private Long originalRequestId;
    private Long followUpRequestId;
    private String message;

    public boolean isRescued() {
        return rescued;
    }

    public Long getOriginalRequestId() {
        return originalRequestId;
    }

    public Long getFollowUpRequestId() {
        return followUpRequestId;
    }

    public String getMessage() {
        return message == null ? "" : message;
    }
}
