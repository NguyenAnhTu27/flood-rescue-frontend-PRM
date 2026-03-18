package com.floodrescue.mobile.data.model.request;

public class CitizenFeedbackRequest {
    private final Integer rating;
    private final String feedbackContent;
    private final Boolean rescuedConfirmed;
    private final Boolean reliefConfirmed;

    public CitizenFeedbackRequest(Integer rating, String feedbackContent, Boolean rescuedConfirmed, Boolean reliefConfirmed) {
        this.rating = rating;
        this.feedbackContent = feedbackContent;
        this.rescuedConfirmed = rescuedConfirmed;
        this.reliefConfirmed = reliefConfirmed;
    }
}
