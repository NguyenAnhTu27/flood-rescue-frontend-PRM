package com.floodrescue.mobile.data.model.ui;

import java.util.List;

public class RescuerReliefDetailState {

    private final long id;
    private final String code;
    private final String requestStatus;
    private final String deliveryStatus;
    private final String targetArea;
    private final String createdByName;
    private final String createdByPhone;
    private final long rescueRequestId;
    private final String address;
    private final Double latitude;
    private final Double longitude;
    private final String locationDescription;
    private final String note;
    private final String deliveryNote;
    private final long assignedIssueId;
    private final String createdAt;
    private final String updatedAt;
    private final List<LineItem> lines;

    public RescuerReliefDetailState(
            long id,
            String code,
            String requestStatus,
            String deliveryStatus,
            String targetArea,
            String createdByName,
            String createdByPhone,
            long rescueRequestId,
            String address,
            Double latitude,
            Double longitude,
            String locationDescription,
            String note,
            String deliveryNote,
            long assignedIssueId,
            String createdAt,
            String updatedAt,
            List<LineItem> lines
    ) {
        this.id = id;
        this.code = code;
        this.requestStatus = requestStatus;
        this.deliveryStatus = deliveryStatus;
        this.targetArea = targetArea;
        this.createdByName = createdByName;
        this.createdByPhone = createdByPhone;
        this.rescueRequestId = rescueRequestId;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.locationDescription = locationDescription;
        this.note = note;
        this.deliveryNote = deliveryNote;
        this.assignedIssueId = assignedIssueId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.lines = lines;
    }

    public long getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getRequestStatus() {
        return requestStatus;
    }

    public String getDeliveryStatus() {
        return deliveryStatus;
    }

    public String getTargetArea() {
        return targetArea;
    }

    public String getCreatedByName() {
        return createdByName;
    }

    public String getCreatedByPhone() {
        return createdByPhone;
    }

    public long getRescueRequestId() {
        return rescueRequestId;
    }

    public String getAddress() {
        return address;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public String getLocationDescription() {
        return locationDescription;
    }

    public String getNote() {
        return note;
    }

    public String getDeliveryNote() {
        return deliveryNote;
    }

    public long getAssignedIssueId() {
        return assignedIssueId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public List<LineItem> getLines() {
        return lines;
    }

    public static class LineItem {
        private final long id;
        private final String itemCode;
        private final String itemName;
        private final String quantityLabel;
        private final String unit;

        public LineItem(long id, String itemCode, String itemName, String quantityLabel, String unit) {
            this.id = id;
            this.itemCode = itemCode;
            this.itemName = itemName;
            this.quantityLabel = quantityLabel;
            this.unit = unit;
        }

        public long getId() {
            return id;
        }

        public String getItemCode() {
            return itemCode;
        }

        public String getItemName() {
            return itemName;
        }

        public String getQuantityLabel() {
            return quantityLabel;
        }

        public String getUnit() {
            return unit;
        }
    }
}
