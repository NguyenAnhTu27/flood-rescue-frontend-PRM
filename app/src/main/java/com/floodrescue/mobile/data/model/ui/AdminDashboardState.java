package com.floodrescue.mobile.data.model.ui;

import java.util.List;

public class AdminDashboardState {

    private final long totalUsers;
    private final long activeUsers;
    private final long lockedUsers;
    private final int roleCount;
    private final int settingCount;
    private final List<RecentActivityItem> recentActivities;

    public AdminDashboardState(
            long totalUsers,
            long activeUsers,
            long lockedUsers,
            int roleCount,
            int settingCount,
            List<RecentActivityItem> recentActivities
    ) {
        this.totalUsers = totalUsers;
        this.activeUsers = activeUsers;
        this.lockedUsers = lockedUsers;
        this.roleCount = roleCount;
        this.settingCount = settingCount;
        this.recentActivities = recentActivities;
    }

    public long getTotalUsers() {
        return totalUsers;
    }

    public long getActiveUsers() {
        return activeUsers;
    }

    public long getLockedUsers() {
        return lockedUsers;
    }

    public int getRoleCount() {
        return roleCount;
    }

    public int getSettingCount() {
        return settingCount;
    }

    public List<RecentActivityItem> getRecentActivities() {
        return recentActivities;
    }

    public static class RecentActivityItem {
        private final long id;
        private final String action;
        private final String actor;
        private final String target;
        private final String level;
        private final String detail;
        private final String createdAt;

        public RecentActivityItem(long id, String action, String actor, String target, String level, String detail, String createdAt) {
            this.id = id;
            this.action = action;
            this.actor = actor;
            this.target = target;
            this.level = level;
            this.detail = detail;
            this.createdAt = createdAt;
        }

        public long getId() {
            return id;
        }

        public String getAction() {
            return action;
        }

        public String getActor() {
            return actor;
        }

        public String getTarget() {
            return target;
        }

        public String getLevel() {
            return level;
        }

        public String getDetail() {
            return detail;
        }

        public String getCreatedAt() {
            return createdAt;
        }
    }
}
