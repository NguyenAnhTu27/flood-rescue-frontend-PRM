package com.floodrescue.mobile.data.model.request;

public class RescuerReliefStatusUpdateRequest {

    private final String status;
    private final String note;

    public RescuerReliefStatusUpdateRequest(String status, String note) {
        this.status = status;
        this.note = note;
    }

    public String getStatus() {
        return status;
    }

    public String getNote() {
        return note;
    }
}
