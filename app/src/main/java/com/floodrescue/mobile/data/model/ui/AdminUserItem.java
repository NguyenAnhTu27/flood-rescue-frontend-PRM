package com.floodrescue.mobile.data.model.ui;

public class AdminUserItem {

    private final long id;
    private final String fullName;
    private final String email;
    private final String phone;
    private final String status;
    private final int roleId;
    private final String roleCode;
    private final String createdAt;

    public AdminUserItem(long id, String fullName, String email, String phone, String status, int roleId, String roleCode, String createdAt) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.status = status;
        this.roleId = roleId;
        this.roleCode = roleCode;
        this.createdAt = createdAt;
    }

    public long getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getStatus() {
        return status;
    }

    public int getRoleId() {
        return roleId;
    }

    public String getRoleCode() {
        return roleCode;
    }

    public String getCreatedAt() {
        return createdAt;
    }
}
