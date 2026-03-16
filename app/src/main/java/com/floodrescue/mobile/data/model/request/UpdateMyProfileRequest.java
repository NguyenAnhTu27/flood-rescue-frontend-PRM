package com.floodrescue.mobile.data.model.request;

public class UpdateMyProfileRequest {
    private final String fullName;
    private final String phone;
    private final String email;

    public UpdateMyProfileRequest(String fullName, String phone, String email) {
        this.fullName = fullName;
        this.phone = phone;
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }
}
