package com.floodrescue.mobile.data.model.request;

public class CitizenRescueReopenRequest {

    private final String reason;

    public CitizenRescueReopenRequest(String reason) {
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }
}
