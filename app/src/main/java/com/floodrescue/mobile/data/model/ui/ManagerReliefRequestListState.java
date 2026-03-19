package com.floodrescue.mobile.data.model.ui;

import java.util.ArrayList;
import java.util.List;

public class ManagerReliefRequestListState {

    private final List<ManagerReliefRequestItem> items;
    private final int totalElements;

    public ManagerReliefRequestListState(List<ManagerReliefRequestItem> items, int totalElements) {
        this.items = items == null ? new ArrayList<>() : items;
        this.totalElements = totalElements;
    }

    public List<ManagerReliefRequestItem> getItems() {
        return items;
    }

    public int getTotalElements() {
        return totalElements;
    }
}
