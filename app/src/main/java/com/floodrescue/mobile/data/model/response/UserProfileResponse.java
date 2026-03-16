package com.floodrescue.mobile.data.model.response;

public class UserProfileResponse {
    private Long id;
    private String fullName;
    private String phone;
    private String email;
    private String role;

    public Long getId() {
        return id;
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

    public String getRole() {
        return role;
    }
}
