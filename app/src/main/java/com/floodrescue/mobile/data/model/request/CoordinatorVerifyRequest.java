package com.floodrescue.mobile.data.model.request;

public class CoordinatorVerifyRequest {
    private final Boolean locationVerified;
    private final String note;
    private final Boolean cancelRequest;
    private final String cancelAction;
    private final String cancelReason;

    public CoordinatorVerifyRequest(Boolean locationVerified, String note, Boolean cancelRequest, String cancelAction, String cancelReason) {
        this.locationVerified = locationVerified;
        this.note = note;
        this.cancelRequest = cancelRequest;
        this.cancelAction = cancelAction;
        this.cancelReason = cancelReason;
    }
}
