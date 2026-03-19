package com.floodrescue.mobile.data.model.request;

public class CoordinatorDuplicateRequest {
    private final Long masterRequestId;
    private final String note;

    public CoordinatorDuplicateRequest(Long masterRequestId, String note) {
        this.masterRequestId = masterRequestId;
        this.note = note;
    }
}
