package com.featherwaypoints.data;

public enum WaypointDimension {
    OVERWORLD("minecraft:overworld", "Overworld", "grass_block.png", 0x4CAF50),
    NETHER("minecraft:the_nether", "Nether", "netherrack.png", 0xF44336),
    END("minecraft:the_end", "End", "end_stone.png", 0x9C27B0);

    private final String id;
    private final String displayName;
    private final String defaultIcon;
    private final int defaultColor;

    WaypointDimension(String id, String displayName, String defaultIcon, int defaultColor) {
        this.id = id;
        this.displayName = displayName;
        this.defaultIcon = defaultIcon;
        this.defaultColor = defaultColor;
    }

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDefaultIcon() {
        return defaultIcon;
    }

    public int getDefaultColor() {
        return defaultColor;
    }

    public static WaypointDimension fromId(String id) {
        for (WaypointDimension dimension : values()) {
            if (dimension.id.equals(id)) {
                return dimension;
            }
        }
        return OVERWORLD; // Default fallback
    }

    public static WaypointDimension fromDisplayName(String displayName) {
        for (WaypointDimension dimension : values()) {
            if (dimension.displayName.equals(displayName)) {
                return dimension;
            }
        }
        return OVERWORLD; // Default fallback
    }

    @Override
    public String toString() {
        return displayName;
    }
}
