package com.floodrescue.mobile.data.model.response;

public class LoginResponse {
    private String token;
    private String tokenType;
    private Long userId;
    private String fullName;
    private String role;

    public String getToken() {
        return token;
    }

    public String getTokenType() {
        return tokenType;
    }

    public Long getUserId() {
        return userId;
    }

    public String getFullName() {
        return fullName;
    }

    public String getRole() {
        return role;
    }
}
