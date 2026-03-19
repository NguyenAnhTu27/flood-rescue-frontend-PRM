package com.floodrescue.mobile.data.model.ui;

public class AdminRoleOption {

    private final int id;
    private final String code;
    private final String name;
    private final long userCount;

    public AdminRoleOption(int id, String code, String name, long userCount) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.userCount = userCount;
    }

    public int getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public long getUserCount() {
        return userCount;
    }
}
