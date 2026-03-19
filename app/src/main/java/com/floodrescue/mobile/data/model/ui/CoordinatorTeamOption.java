package com.floodrescue.mobile.data.model.ui;

public class CoordinatorTeamOption {
    private final long id;
    private final String name;
    private final String status;
    private final String area;
    private final String lastUpdate;
    private final boolean online;

    public CoordinatorTeamOption(long id, String name, String status, String area, String lastUpdate, boolean online) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.area = area;
        this.lastUpdate = lastUpdate;
        this.online = online;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getStatus() {
        return status;
    }

    public String getArea() {
        return area;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public boolean isOnline() {
        return online;
    }
}
