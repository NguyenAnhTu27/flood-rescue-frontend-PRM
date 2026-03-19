package com.floodrescue.mobile.data.model.ui;

public class ManagerReliefRequestItem {

    private final long id;
    private final String code;
    private final String status;
    private final String deliveryStatus;
    private final String targetArea;
    private final String createdByName;
    private final String createdByPhone;
    private final String address;
    private final int lineCount;
    private final String updatedAt;

    public ManagerReliefRequestItem(long id, String code, String status, String deliveryStatus, String targetArea, String createdByName, String createdByPhone, String address, int lineCount, String updatedAt) {
        this.id = id;
        this.code = code;
        this.status = status;
        this.deliveryStatus = deliveryStatus;
        this.targetArea = targetArea;
        this.createdByName = createdByName;
        this.createdByPhone = createdByPhone;
        this.address = address;
        this.lineCount = lineCount;
        this.updatedAt = updatedAt;
    }

    public long getId() { return id; }
    public String getCode() { return code; }
    public String getStatus() { return status; }
    public String getDeliveryStatus() { return deliveryStatus; }
    public String getTargetArea() { return targetArea; }
    public String getCreatedByName() { return createdByName; }
    public String getCreatedByPhone() { return createdByPhone; }
    public String getAddress() { return address; }
    public int getLineCount() { return lineCount; }
    public String getUpdatedAt() { return updatedAt; }
}
