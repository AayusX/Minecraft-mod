package com.featherwaypoints.data;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;
import java.util.UUID;

public class Waypoint {
    @SerializedName("id")
    private final String id;
    
    @SerializedName("name")
    private String name;
    
    @SerializedName("x")
    private double x;
    
    @SerializedName("y")
    private double y;
    
    @SerializedName("z")
    private double z;
    
    @SerializedName("dimension")
    private String dimension;
    
    @SerializedName("color")
    private int color;
    
    @SerializedName("icon")
    private String icon;
    
    @SerializedName("group")
    private String group;
    
    @SerializedName("visible")
    private boolean visible;
    
    @SerializedName("created_at")
    private long createdAt;

    // Constructor for creating new waypoints
    public Waypoint(String name, double x, double y, double z, String dimension, int color, String icon, String group) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.x = x;
        this.y = y;
        this.z = z;
        this.dimension = dimension;
        this.color = color;
        this.icon = icon;
        this.group = group;
        this.visible = true;
        this.createdAt = System.currentTimeMillis();
    }

    // Constructor for JSON deserialization
    public Waypoint(String id, String name, double x, double y, double z, String dimension, 
                   int color, String icon, String group, boolean visible, long createdAt) {
        this.id = id;
        this.name = name;
        this.x = x;
        this.y = y;
        this.z = z;
        this.dimension = dimension;
        this.color = color;
        this.icon = icon;
        this.group = group;
        this.visible = visible;
        this.createdAt = createdAt;
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public double getX() { return x; }
    public double getY() { return y; }
    public double getZ() { return z; }
    public String getDimension() { return dimension; }
    public int getColor() { return color; }
    public String getIcon() { return icon; }
    public String getGroup() { return group; }
    public boolean isVisible() { return visible; }
    public long getCreatedAt() { return createdAt; }

    // Setters
    public void setName(String name) { this.name = name; }
    public void setX(double x) { this.x = x; }
    public void setY(double y) { this.y = y; }
    public void setZ(double z) { this.z = z; }
    public void setDimension(String dimension) { this.dimension = dimension; }
    public void setColor(int color) { this.color = color; }
    public void setIcon(String icon) { this.icon = icon; }
    public void setGroup(String group) { this.group = group; }
    public void setVisible(boolean visible) { this.visible = visible; }

    // Utility methods
    public double getDistanceTo(double x, double y, double z) {
        return Math.sqrt(Math.pow(this.x - x, 2) + Math.pow(this.y - y, 2) + Math.pow(this.z - z, 2));
    }

    public String getColorAsHex() {
        return String.format("#%06X", color & 0xFFFFFF);
    }

    public static int colorFromHex(String hex) {
        if (hex.startsWith("#")) {
            hex = hex.substring(1);
        }
        return Integer.parseInt(hex, 16);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Waypoint waypoint = (Waypoint) o;
        return Objects.equals(id, waypoint.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("Waypoint{name='%s', x=%.1f, y=%.1f, z=%.1f, dimension='%s'}", 
                           name, x, y, z, dimension);
    }
}
