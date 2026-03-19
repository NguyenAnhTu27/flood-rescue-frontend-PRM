package com.floodrescue.mobile.data.model.ui;

import java.util.List;

public class AdminUserPage {

    private final List<AdminUserItem> users;
    private final long totalUsers;
    private final int totalPages;
    private final int page;

    public AdminUserPage(List<AdminUserItem> users, long totalUsers, int totalPages, int page) {
        this.users = users;
        this.totalUsers = totalUsers;
        this.totalPages = totalPages;
        this.page = page;
    }

    public List<AdminUserItem> getUsers() {
        return users;
    }

    public long getTotalUsers() {
        return totalUsers;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public int getPage() {
        return page;
    }
}
