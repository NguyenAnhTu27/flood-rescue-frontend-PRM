package com.floodrescue.mobile.data.model.ui;

import java.util.List;

public class RescuerTeamLocationState {

    private final long teamId;
    private final String teamName;
    private final String teamStatus;
    private final Double latitude;
    private final Double longitude;
    private final String locationText;
    private final String updatedAt;
    private final List<AssetItem> heldAssets;

    public RescuerTeamLocationState(
            long teamId,
            String teamName,
            String teamStatus,
            Double latitude,
            Double longitude,
            String locationText,
            String updatedAt,
            List<AssetItem> heldAssets
    ) {
        this.teamId = teamId;
        this.teamName = teamName;
        this.teamStatus = teamStatus;
        this.latitude = latitude;
        this.longitude = longitude;
        this.locationText = locationText;
        this.updatedAt = updatedAt;
        this.heldAssets = heldAssets;
    }

    public long getTeamId() {
        return teamId;
    }

    public String getTeamName() {
        return teamName;
    }

    public String getTeamStatus() {
        return teamStatus;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public String getLocationText() {
        return locationText;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public List<AssetItem> getHeldAssets() {
        return heldAssets;
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
}
