package com.featherwaypoints.gui;

import com.featherwaypoints.data.Waypoint;
import com.featherwaypoints.data.WaypointDimension;
import com.featherwaypoints.manager.WaypointManager;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

public class WaypointListScreen extends Screen {
    private static final int ENTRY_HEIGHT = 20;
    private static final int MARGIN = 10;
    private static final int BUTTON_WIDTH = 80;
    private static final int BUTTON_HEIGHT = 20;

    private final WaypointManager waypointManager;
    private TextFieldWidget searchField;
    private List<Waypoint> displayedWaypoints;
    private int scrollOffset = 0;
    private String currentDimension;

    public WaypointListScreen(String dimension) {
        super(Text.literal("Waypoints"));
        this.waypointManager = WaypointManager.getInstance();
        this.currentDimension = dimension;
        updateDisplayedWaypoints();
    }

    @Override
    protected void init() {
        // Search field
        this.searchField = new TextFieldWidget(this.textRenderer, 
            this.width / 2 - 100, 30, 200, 20, Text.literal("Search waypoints..."));
        this.searchField.setChangedListener(this::onSearchChanged);
        this.addSelectableChild(this.searchField);

        // Add waypoint button
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Add"), button -> {
            this.client.setScreen(new WaypointEditScreen(null, this.currentDimension, this));
        }).dimensions(this.width - 200, 30, BUTTON_WIDTH, BUTTON_HEIGHT).build());

        // Clear all button
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Clear All"), button -> {
            waypointManager.clearAllWaypoints();
            updateDisplayedWaypoints();
        }).dimensions(this.width - 110, 30, BUTTON_WIDTH, BUTTON_HEIGHT).build());

        // Done button
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Done"), button -> {
            this.close();
        }).dimensions(this.width / 2 - 40, this.height - 30, BUTTON_WIDTH, BUTTON_HEIGHT).build());
    }

    private void onSearchChanged(String query) {
        if (query.isEmpty()) {
            this.displayedWaypoints = waypointManager.getWaypointsInDimension(currentDimension);
        } else {
            this.displayedWaypoints = waypointManager.searchWaypointsInDimension(query, currentDimension);
        }
        this.scrollOffset = 0;
    }

    private void updateDisplayedWaypoints() {
        this.displayedWaypoints = waypointManager.getWaypointsInDimension(currentDimension);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);
        
        // Title
        WaypointDimension dim = WaypointDimension.fromId(currentDimension);
        String title = dim.getDisplayName() + " Waypoints (" + displayedWaypoints.size() + ")";
        context.drawCenteredTextWithShadow(this.textRenderer, title, this.width / 2, 10, 0xFFFFFF);

        // Search field
        this.searchField.render(context, mouseX, mouseY, delta);

        // Waypoint list
        renderWaypointList(context, mouseX, mouseY);

        super.render(context, mouseX, mouseY, delta);
    }

    private void renderWaypointList(DrawContext context, int mouseX, int mouseY) {
        int startY = 60;
        int endY = this.height - 60;
        int listHeight = endY - startY;
        int maxEntries = listHeight / ENTRY_HEIGHT;

        // Scrolling
        if (displayedWaypoints.size() > maxEntries) {
            int maxScroll = displayedWaypoints.size() - maxEntries;
            this.scrollOffset = Math.max(0, Math.min(scrollOffset, maxScroll));
        } else {
            this.scrollOffset = 0;
        }

        // Draw waypoint entries
        for (int i = 0; i < Math.min(maxEntries, displayedWaypoints.size()); i++) {
            int index = i + scrollOffset;
            if (index >= displayedWaypoints.size()) break;

            Waypoint waypoint = displayedWaypoints.get(index);
            int y = startY + i * ENTRY_HEIGHT;
            
            // Background
            boolean isHovered = mouseX >= MARGIN && mouseX <= this.width - MARGIN && 
                               mouseY >= y && mouseY <= y + ENTRY_HEIGHT;
            int bgColor = isHovered ? 0x80FFFFFF : 0x40000000;
            context.fill(MARGIN, y, this.width - MARGIN, y + ENTRY_HEIGHT, bgColor);

            // Waypoint info
            String name = waypoint.getName();
            String coords = String.format("%.0f, %.0f, %.0f", waypoint.getX(), waypoint.getY(), waypoint.getZ());
            String distance = "";
            
            if (client != null && client.player != null) {
                double dist = waypoint.getDistanceTo(client.player.getX(), client.player.getY(), client.player.getZ());
                distance = String.format("%.0fm", dist);
            }

            // Color indicator
            context.fill(MARGIN + 2, y + 2, MARGIN + 8, y + ENTRY_HEIGHT - 2, waypoint.getColor() | 0xFF000000);

            // Text
            context.drawTextWithShadow(this.textRenderer, name, MARGIN + 15, y + 2, 0xFFFFFF);
            context.drawTextWithShadow(this.textRenderer, coords, MARGIN + 15, y + 11, 0xCCCCCC);
            
            if (!distance.isEmpty()) {
                context.drawTextWithShadow(this.textRenderer, distance, 
                    this.width - MARGIN - textRenderer.getWidth(distance) - 50, y + 6, 0xAAAAA);
            }

            // Edit button
            if (isHovered) {
                int editX = this.width - MARGIN - 40;
                context.fill(editX, y + 2, editX + 38, y + ENTRY_HEIGHT - 2, 0xFF4CAF50);
                context.drawCenteredTextWithShadow(this.textRenderer, "Edit", editX + 19, y + 6, 0xFFFFFF);
            }
        }

        // Scroll indicator
        if (displayedWaypoints.size() > maxEntries) {
            int scrollbarHeight = Math.max(20, (listHeight * maxEntries) / displayedWaypoints.size());
            int scrollbarY = startY + (scrollOffset * (listHeight - scrollbarHeight)) / 
                           (displayedWaypoints.size() - maxEntries);
            context.fill(this.width - 8, scrollbarY, this.width - 4, scrollbarY + scrollbarHeight, 0xFFFFFFFF);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.searchField.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }

        // Check waypoint list clicks
        int startY = 60;
        int endY = this.height - 60;
        int listHeight = endY - startY;
        int maxEntries = listHeight / ENTRY_HEIGHT;

        if (mouseX >= MARGIN && mouseX <= this.width - MARGIN && mouseY >= startY && mouseY <= endY) {
            int clickedIndex = ((int) mouseY - startY) / ENTRY_HEIGHT + scrollOffset;
            
            if (clickedIndex < displayedWaypoints.size()) {
                Waypoint waypoint = displayedWaypoints.get(clickedIndex);
                
                // Check if clicked on edit button area
                if (mouseX >= this.width - MARGIN - 40) {
                    this.client.setScreen(new WaypointEditScreen(waypoint, currentDimension, this));
                    return true;
                }
                
                // Otherwise, teleport (if creative/cheats enabled)
                if (button == 1) { // Right click to delete
                    waypointManager.removeWaypoint(waypoint);
                    updateDisplayedWaypoints();
                    return true;
                }
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        int startY = 60;
        int endY = this.height - 60;
        
        if (mouseY >= startY && mouseY <= endY) {
            this.scrollOffset -= (int) amount;
            return true;
        }
        
        return super.mouseScrolled(mouseX, mouseY, amount);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.searchField.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (this.searchField.charTyped(chr, modifiers)) {
            return true;
        }
        return super.charTyped(chr, modifiers);
    }

    public void refreshList() {
        updateDisplayedWaypoints();
    }
}
