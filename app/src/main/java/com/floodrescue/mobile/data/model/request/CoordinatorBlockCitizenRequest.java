package com.floodrescue.mobile.data.model.request;

public class CoordinatorBlockCitizenRequest {
    private final Boolean blocked;
    private final String reason;

    public CoordinatorBlockCitizenRequest(Boolean blocked, String reason) {
        this.blocked = blocked;
        this.reason = reason;
    }
}
