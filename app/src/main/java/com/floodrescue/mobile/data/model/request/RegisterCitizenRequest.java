package com.floodrescue.mobile.data.model.request;

public class RegisterCitizenRequest {

    private final String fullName;
    private final String phone;
    private final String email;
    private final String password;

    public RegisterCitizenRequest(String fullName, String phone, String email, String password) {
        this.fullName = fullName;
        this.phone = phone;
        this.email = email;
        this.password = password;
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

    public String getPassword() {
        return password;
    }
}
