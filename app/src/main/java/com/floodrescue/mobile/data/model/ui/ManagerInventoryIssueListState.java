package com.floodrescue.mobile.data.model.ui;

import java.util.ArrayList;
import java.util.List;

public class ManagerInventoryIssueListState {

    private final List<ManagerInventoryIssueItem> items;
    private final List<ManagerInventoryIssueItem> temporaryItems;
    private final int totalElements;

    public ManagerInventoryIssueListState(List<ManagerInventoryIssueItem> items, List<ManagerInventoryIssueItem> temporaryItems, int totalElements) {
        this.items = items == null ? new ArrayList<>() : items;
        this.temporaryItems = temporaryItems == null ? new ArrayList<>() : temporaryItems;
        this.totalElements = totalElements;
    }

    public List<ManagerInventoryIssueItem> getItems() { return items; }
    public List<ManagerInventoryIssueItem> getTemporaryItems() { return temporaryItems; }
    public int getTotalElements() { return totalElements; }
}
