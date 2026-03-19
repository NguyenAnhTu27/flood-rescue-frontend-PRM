package com.floodrescue.mobile.data.model.request;

public class RescuerTeamLocationUpdateRequest {

    private final Double latitude;
    private final Double longitude;
    private final String locationText;

    public RescuerTeamLocationUpdateRequest(Double latitude, Double longitude, String locationText) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.locationText = locationText;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public String getLocationText() {
        return locationText;
    }
}
