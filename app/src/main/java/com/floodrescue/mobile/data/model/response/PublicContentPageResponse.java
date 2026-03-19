package com.floodrescue.mobile.data.model.response;

public class PublicContentPageResponse {

    private String title;
    private String content;

    public String getTitle() {
        return title == null ? "" : title;
    }

    public String getContent() {
        return content == null ? "" : content;
    }
}
