package com.floodrescue.mobile.data.model.ui;

import java.util.ArrayList;
import java.util.List;

public class ManagerDashboardState {

    private final List<OverviewItem> overviewItems;
    private final List<TransactionItem> recentTransactions;
    private final List<InventorySummaryItem> inventorySummary;
    private final List<InventoryItem> inventoryItems;

    public ManagerDashboardState(
            List<OverviewItem> overviewItems,
            List<TransactionItem> recentTransactions,
            List<InventorySummaryItem> inventorySummary,
            List<InventoryItem> inventoryItems
    ) {
        this.overviewItems = overviewItems == null ? new ArrayList<>() : overviewItems;
        this.recentTransactions = recentTransactions == null ? new ArrayList<>() : recentTransactions;
        this.inventorySummary = inventorySummary == null ? new ArrayList<>() : inventorySummary;
        this.inventoryItems = inventoryItems == null ? new ArrayList<>() : inventoryItems;
    }

    public List<OverviewItem> getOverviewItems() {
        return overviewItems;
    }

    public List<TransactionItem> getRecentTransactions() {
        return recentTransactions;
    }

    public List<InventorySummaryItem> getInventorySummary() {
        return inventorySummary;
    }

    public List<InventoryItem> getInventoryItems() {
        return inventoryItems;
    }

    public static class OverviewItem {
        private final String id;
        private final String label;
        private final String value;
        private final String unit;
        private final String sub;
        private final String color;
        private final boolean highlighted;

        public OverviewItem(String id, String label, String value, String unit, String sub, String color, boolean highlighted) {
            this.id = id;
            this.label = label;
            this.value = value;
            this.unit = unit;
            this.sub = sub;
            this.color = color;
            this.highlighted = highlighted;
        }

        public String getId() { return id; }
        public String getLabel() { return label; }
        public String getValue() { return value; }
        public String getUnit() { return unit; }
        public String getSub() { return sub; }
        public String getColor() { return color; }
        public boolean isHighlighted() { return highlighted; }
    }

    public static class TransactionItem {
        private final String id;
        private final String code;
        private final String typeLabel;
        private final String typeColor;
        private final String destination;
        private final String statusLabel;
        private final String statusColor;
        private final String time;

        public TransactionItem(String id, String code, String typeLabel, String typeColor, String destination, String statusLabel, String statusColor, String time) {
            this.id = id;
            this.code = code;
            this.typeLabel = typeLabel;
            this.typeColor = typeColor;
            this.destination = destination;
            this.statusLabel = statusLabel;
            this.statusColor = statusColor;
            this.time = time;
        }

        public String getId() { return id; }
        public String getCode() { return code; }
        public String getTypeLabel() { return typeLabel; }
        public String getTypeColor() { return typeColor; }
        public String getDestination() { return destination; }
        public String getStatusLabel() { return statusLabel; }
        public String getStatusColor() { return statusColor; }
        public String getTime() { return time; }
    }

    public static class InventorySummaryItem {
        private final String id;
        private final String label;
        private final String value;
        private final String color;

        public InventorySummaryItem(String id, String label, String value, String color) {
            this.id = id;
            this.label = label;
            this.value = value;
            this.color = color;
        }

        public String getId() { return id; }
        public String getLabel() { return label; }
        public String getValue() { return value; }
        public String getColor() { return color; }
    }

    public static class InventoryItem {
        private final String code;
        private final String name;
        private final String categoryName;
        private final String unit;
        private final String qty;
        private final String statusLabel;
        private final String statusColor;

        public InventoryItem(String code, String name, String categoryName, String unit, String qty, String statusLabel, String statusColor) {
            this.code = code;
            this.name = name;
            this.categoryName = categoryName;
            this.unit = unit;
            this.qty = qty;
            this.statusLabel = statusLabel;
            this.statusColor = statusColor;
        }

        public String getCode() { return code; }
        public String getName() { return name; }
        public String getCategoryName() { return categoryName; }
        public String getUnit() { return unit; }
        public String getQty() { return qty; }
        public String getStatusLabel() { return statusLabel; }
        public String getStatusColor() { return statusColor; }
    }
}
