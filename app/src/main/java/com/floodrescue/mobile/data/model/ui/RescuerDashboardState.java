package com.floodrescue.mobile.data.model.ui;

import java.util.List;

public class RescuerDashboardState {
    private final long teamId;
    private final String teamName;
    private final String teamLocationText;
    private final String teamLocationUpdatedAt;
    private final long activeTaskGroups;
    private final long activeAssignments;
    private final List<AssetItem> heldAssets;
    private final List<TaskGroupItem> taskGroups;

    public RescuerDashboardState(
            long teamId,
            String teamName,
            String teamLocationText,
            String teamLocationUpdatedAt,
            long activeTaskGroups,
            long activeAssignments,
            List<AssetItem> heldAssets,
            List<TaskGroupItem> taskGroups
    ) {
        this.teamId = teamId;
        this.teamName = teamName;
        this.teamLocationText = teamLocationText;
        this.teamLocationUpdatedAt = teamLocationUpdatedAt;
        this.activeTaskGroups = activeTaskGroups;
        this.activeAssignments = activeAssignments;
        this.heldAssets = heldAssets;
        this.taskGroups = taskGroups;
    }

    public long getTeamId() {
        return teamId;
    }

    public String getTeamName() {
        return teamName;
    }

    public String getTeamLocationText() {
        return teamLocationText;
    }

    public String getTeamLocationUpdatedAt() {
        return teamLocationUpdatedAt;
    }

    public long getActiveTaskGroups() {
        return activeTaskGroups;
    }

    public long getActiveAssignments() {
        return activeAssignments;
    }

    public List<AssetItem> getHeldAssets() {
        return heldAssets;
    }

    public List<TaskGroupItem> getTaskGroups() {
        return taskGroups;
    }

    public static class AssetItem {
        private final long id;
        private final String code;
        private final String name;
        private final String assetType;
        private final String status;

        public AssetItem(long id, String code, String name, String assetType, String status) {
            this.id = id;
            this.code = code;
            this.name = name;
            this.assetType = assetType;
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

        public String getAssetType() {
            return assetType;
        }

        public String getStatus() {
            return status;
        }
    }

    public static class TaskGroupItem {
        private final long id;
        private final String code;
        private final String status;
        private final String note;
        private final String updatedAt;

        public TaskGroupItem(long id, String code, String status, String note, String updatedAt) {
            this.id = id;
            this.code = code;
            this.status = status;
            this.note = note;
            this.updatedAt = updatedAt;
        }

        public long getId() {
            return id;
        }

        public String getCode() {
            return code;
        }

        public String getStatus() {
            return status;
        }

        public String getNote() {
            return note;
        }

        public String getUpdatedAt() {
            return updatedAt;
        }
    }
}
