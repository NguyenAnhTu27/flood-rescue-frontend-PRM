package com.floodrescue.mobile.data.model.ui;

public class CoordinatorTaskGroupItem {
    private final long id;
    private final String name;
    private final String status;
    private final String description;

    public CoordinatorTaskGroupItem(long id, String name, String status, String description) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.description = description;
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

    public String getDescription() {
        return description;
    }
}
