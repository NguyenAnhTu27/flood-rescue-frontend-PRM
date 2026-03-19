package com.floodrescue.mobile.data.model.ui;

public class CoordinatorRequestItem {
    private final long id;
    private final String code;
    private final String title;
    private final String priority;
    private final int people;
    private final String address;
    private final String updatedTime;
    private final String status;

    public CoordinatorRequestItem(long id, String code, String title, String priority, int people, String address, String updatedTime, String status) {
        this.id = id;
        this.code = code;
        this.title = title;
        this.priority = priority;
        this.people = people;
        this.address = address;
        this.updatedTime = updatedTime;
        this.status = status;
    }

    public long getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getTitle() {
        return title;
    }

    public String getPriority() {
        return priority;
    }

    public int getPeople() {
        return people;
    }

    public String getAddress() {
        return address;
    }

    public String getUpdatedTime() {
        return updatedTime;
    }

    public String getStatus() {
        return status;
    }
}
