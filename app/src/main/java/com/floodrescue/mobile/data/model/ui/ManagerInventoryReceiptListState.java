package com.floodrescue.mobile.data.model.ui;

import java.util.ArrayList;
import java.util.List;

public class ManagerInventoryReceiptListState {

    private final List<ManagerInventoryReceiptItem> items;
    private final int totalElements;

    public ManagerInventoryReceiptListState(List<ManagerInventoryReceiptItem> items, int totalElements) {
        this.items = items == null ? new ArrayList<>() : items;
        this.totalElements = totalElements;
    }

    public List<ManagerInventoryReceiptItem> getItems() {
        return items;
    }

    public int getTotalElements() {
        return totalElements;
    }
}
