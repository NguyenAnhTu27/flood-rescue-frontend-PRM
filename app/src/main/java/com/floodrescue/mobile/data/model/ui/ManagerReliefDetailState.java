package com.floodrescue.mobile.data.model.ui;

import java.util.ArrayList;
import java.util.List;

public class ManagerReliefDetailState {

    private final long id;
    private final String code;
    private final String status;
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
    private final long assignedTeamId;
    private final long assignedIssueId;
    private final String createdAt;
    private final String updatedAt;
    private final List<LineItem> lines;

    public ManagerReliefDetailState(long id, String code, String status, String deliveryStatus, String targetArea, String createdByName, String createdByPhone, long rescueRequestId, String address, Double latitude, Double longitude, String locationDescription, String note, String deliveryNote, long assignedTeamId, long assignedIssueId, String createdAt, String updatedAt, List<LineItem> lines) {
        this.id = id;
        this.code = code;
        this.status = status;
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
        this.assignedTeamId = assignedTeamId;
        this.assignedIssueId = assignedIssueId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.lines = lines == null ? new ArrayList<>() : lines;
    }

    public long getId() { return id; }
    public String getCode() { return code; }
    public String getStatus() { return status; }
    public String getDeliveryStatus() { return deliveryStatus; }
    public String getTargetArea() { return targetArea; }
    public String getCreatedByName() { return createdByName; }
    public String getCreatedByPhone() { return createdByPhone; }
    public long getRescueRequestId() { return rescueRequestId; }
    public String getAddress() { return address; }
    public Double getLatitude() { return latitude; }
    public Double getLongitude() { return longitude; }
    public String getLocationDescription() { return locationDescription; }
    public String getNote() { return note; }
    public String getDeliveryNote() { return deliveryNote; }
    public long getAssignedTeamId() { return assignedTeamId; }
    public long getAssignedIssueId() { return assignedIssueId; }
    public String getCreatedAt() { return createdAt; }
    public String getUpdatedAt() { return updatedAt; }
    public List<LineItem> getLines() { return lines; }

    public static class LineItem {
        private final int itemCategoryId;
        private final String itemCode;
        private final String itemName;
        private final String qty;
        private final String unit;

        public LineItem(int itemCategoryId, String itemCode, String itemName, String qty, String unit) {
            this.itemCategoryId = itemCategoryId;
            this.itemCode = itemCode;
            this.itemName = itemName;
            this.qty = qty;
            this.unit = unit;
        }

        public int getItemCategoryId() { return itemCategoryId; }
        public String getItemCode() { return itemCode; }
        public String getItemName() { return itemName; }
        public String getQty() { return qty; }
        public String getUnit() { return unit; }
    }
}
