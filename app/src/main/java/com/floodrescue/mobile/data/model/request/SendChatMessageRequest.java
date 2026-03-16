package com.floodrescue.mobile.data.model.request;

public class SendChatMessageRequest {
    private final String message;

    public SendChatMessageRequest(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
