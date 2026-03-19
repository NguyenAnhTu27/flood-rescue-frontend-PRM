package com.floodrescue.mobile.data.model.ui;

public class BlockedCitizenItem {
    private final long id;
    private final String fullName;
    private final String phone;
    private final String email;
    private final String blockedReason;

    public BlockedCitizenItem(long id, String fullName, String phone, String email, String blockedReason) {
        this.id = id;
        this.fullName = fullName;
        this.phone = phone;
        this.email = email;
        this.blockedReason = blockedReason;
    }

    public long getId() {
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

    public String getBlockedReason() {
        return blockedReason;
    }
}
