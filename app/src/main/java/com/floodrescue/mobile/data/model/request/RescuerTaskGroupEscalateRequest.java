package com.floodrescue.mobile.data.model.request;

public class RescuerTaskGroupEscalateRequest {

    private final String severity;
    private final String reason;

    public RescuerTaskGroupEscalateRequest(String severity, String reason) {
        this.severity = severity;
        this.reason = reason;
    }

    public String getSeverity() {
        return severity;
    }

    public String getReason() {
        return reason;
    }
}
