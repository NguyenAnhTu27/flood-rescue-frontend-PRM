package com.floodrescue.mobile.data.model.request;

public class RescueNoteRequest {

    private final String note;

    public RescueNoteRequest(String note) {
        this.note = note;
    }

    public String getNote() {
        return note;
    }
}
