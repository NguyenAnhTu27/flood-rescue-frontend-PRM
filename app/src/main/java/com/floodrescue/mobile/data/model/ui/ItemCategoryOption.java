package com.floodrescue.mobile.data.model.ui;

public class ItemCategoryOption {

    private final int id;
    private final String code;
    private final String name;
    private final String unit;
    private final String classificationName;

    public ItemCategoryOption(int id, String code, String name, String unit, String classificationName) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.unit = unit;
        this.classificationName = classificationName;
    }

    public int getId() { return id; }
    public String getCode() { return code; }
    public String getName() { return name; }
    public String getUnit() { return unit; }
    public String getClassificationName() { return classificationName; }

    @Override
    public String toString() {
        if (code == null || code.trim().isEmpty()) {
            return name;
        }
        return code + " - " + name;
    }
}
