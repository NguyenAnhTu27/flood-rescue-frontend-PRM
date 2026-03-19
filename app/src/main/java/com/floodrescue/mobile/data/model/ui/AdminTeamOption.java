package com.floodrescue.mobile.data.model.ui;

public class AdminTeamOption {

    private final long id;
    private final String code;
    private final String name;
    private final String status;

    public AdminTeamOption(long id, String code, String name, String status) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.status = status;
    }

    public long getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getStatus() {
        return status;
    }
}
