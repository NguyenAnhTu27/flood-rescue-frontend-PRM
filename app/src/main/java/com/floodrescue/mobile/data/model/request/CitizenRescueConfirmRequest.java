package com.floodrescue.mobile.data.model.request;

public class CitizenRescueConfirmRequest {

    private final Boolean rescued;
    private final String reason;

    public CitizenRescueConfirmRequest(Boolean rescued, String reason) {
        this.rescued = rescued;
        this.reason = reason;
    }

    public Boolean getRescued() {
        return rescued;
    }

    public String getReason() {
        return reason;
    }
}
