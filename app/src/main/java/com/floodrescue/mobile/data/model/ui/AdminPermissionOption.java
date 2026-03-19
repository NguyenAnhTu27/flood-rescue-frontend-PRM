package com.floodrescue.mobile.data.model.ui;

public class AdminPermissionOption {

    private final int id;
    private final String code;
    private final String name;
    private final String module;

    public AdminPermissionOption(int id, String code, String name, String module) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.module = module;
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

    public String getModule() {
        return module;
    }
}
