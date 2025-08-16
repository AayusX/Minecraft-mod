package com.featherwaypoints.gui;

import com.featherwaypoints.data.Waypoint;
import com.featherwaypoints.data.WaypointDimension;
import com.featherwaypoints.manager.WaypointManager;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

public class WaypointEditScreen extends Screen {
    private static final int FIELD_WIDTH = 200;
    private static final int FIELD_HEIGHT = 20;
    private static final int BUTTON_WIDTH = 80;
    private static final int BUTTON_HEIGHT = 20;

    private final WaypointManager waypointManager;
    private final Waypoint editingWaypoint;
    private final String defaultDimension;
    private final WaypointListScreen parentScreen;

    private TextFieldWidget nameField;
    private TextFieldWidget xField;
    private TextFieldWidget yField;
    private TextFieldWidget zField;
    private TextFieldWidget colorField;
    private TextFieldWidget groupField;
    
    private boolean isEditing;

    public WaypointEditScreen(Waypoint waypoint, String defaultDimension, WaypointListScreen parent) {
        super(Text.literal(waypoint == null ? "Add Waypoint" : "Edit Waypoint"));
        this.waypointManager = WaypointManager.getInstance();
        this.editingWaypoint = waypoint;
        this.defaultDimension = defaultDimension;
        this.parentScreen = parent;
        this.isEditing = waypoint != null;
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int startY = this.height / 2 - 120;
        int fieldSpacing = 35;

        // Name field
        this.nameField = new TextFieldWidget(this.textRenderer, 
            centerX - FIELD_WIDTH / 2, startY, FIELD_WIDTH, FIELD_HEIGHT, Text.literal("Name"));
        this.nameField.setMaxLength(50);
        if (isEditing) {
            this.nameField.setText(editingWaypoint.getName());
        }
        this.addSelectableChild(this.nameField);

        // Coordinate fields
        startY += fieldSpacing;
        this.xField = new TextFieldWidget(this.textRenderer,
            centerX - FIELD_WIDTH / 2, startY, FIELD_WIDTH / 3 - 5, FIELD_HEIGHT, Text.literal("X"));
        this.yField = new TextFieldWidget(this.textRenderer,
            centerX - FIELD_WIDTH / 6, startY, FIELD_WIDTH / 3 - 5, FIELD_HEIGHT, Text.literal("Y"));
        this.zField = new TextFieldWidget(this.textRenderer,
            centerX + FIELD_WIDTH / 6 + 5, startY, FIELD_WIDTH / 3 - 5, FIELD_HEIGHT, Text.literal("Z"));

        if (isEditing) {
            this.xField.setText(String.valueOf((int) editingWaypoint.getX()));
            this.yField.setText(String.valueOf((int) editingWaypoint.getY()));
            this.zField.setText(String.valueOf((int) editingWaypoint.getZ()));
        } else if (client != null && client.player != null) {
            // Auto-fill current position
            this.xField.setText(String.valueOf((int) client.player.getX()));
            this.yField.setText(String.valueOf((int) client.player.getY()));
            this.zField.setText(String.valueOf((int) client.player.getZ()));
        }

        this.addSelectableChild(this.xField);
        this.addSelectableChild(this.yField);
        this.addSelectableChild(this.zField);

        // Color field
        startY += fieldSpacing;
        this.colorField = new TextFieldWidget(this.textRenderer,
            centerX - FIELD_WIDTH / 2, startY, FIELD_WIDTH, FIELD_HEIGHT, Text.literal("Color (hex)"));
        this.colorField.setMaxLength(7);
        if (isEditing) {
            this.colorField.setText(editingWaypoint.getColorAsHex());
        } else {
            WaypointDimension dim = WaypointDimension.fromId(defaultDimension);
            this.colorField.setText(String.format("#%06X", dim.getDefaultColor()));
        }
        this.addSelectableChild(this.colorField);

        // Group field
        startY += fieldSpacing;
        this.groupField = new TextFieldWidget(this.textRenderer,
            centerX - FIELD_WIDTH / 2, startY, FIELD_WIDTH, FIELD_HEIGHT, Text.literal("Group (optional)"));
        this.groupField.setMaxLength(30);
        if (isEditing && editingWaypoint.getGroup() != null) {
            this.groupField.setText(editingWaypoint.getGroup());
        }
        this.addSelectableChild(this.groupField);

        // Buttons
        startY += fieldSpacing + 20;
        
        // Current position button
        if (!isEditing) {
            this.addDrawableChild(ButtonWidget.builder(Text.literal("Current Pos"), button -> {
                if (client != null && client.player != null) {
                    this.xField.setText(String.valueOf((int) client.player.getX()));
                    this.yField.setText(String.valueOf((int) client.player.getY()));
                    this.zField.setText(String.valueOf((int) client.player.getZ()));
                }
            }).dimensions(centerX - 120, startY, 100, BUTTON_HEIGHT).build());
        }

        // Save button
        this.addDrawableChild(ButtonWidget.builder(Text.literal(isEditing ? "Save" : "Add"), button -> {
            saveWaypoint();
        }).dimensions(centerX - (isEditing ? 40 : 10), startY, BUTTON_WIDTH, BUTTON_HEIGHT).build());

        // Cancel button
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Cancel"), button -> {
            this.close();
        }).dimensions(centerX + (isEditing ? 40 : 80), startY, BUTTON_WIDTH, BUTTON_HEIGHT).build());

        // Delete button (only for editing)
        if (isEditing) {
            this.addDrawableChild(ButtonWidget.builder(Text.literal("Delete"), button -> {
                waypointManager.removeWaypoint(editingWaypoint);
                this.close();
            }).dimensions(centerX - 120, startY, BUTTON_WIDTH, BUTTON_HEIGHT).build());
        }

        this.setInitialFocus(this.nameField);
    }

    private void saveWaypoint() {
        String name = this.nameField.getText().trim();
        if (name.isEmpty()) {
            return; // Could show error message
        }

        try {
            double x = Double.parseDouble(this.xField.getText());
            double y = Double.parseDouble(this.yField.getText());
            double z = Double.parseDouble(this.zField.getText());
            
            int color;
            String colorText = this.colorField.getText().trim();
            try {
                color = Waypoint.colorFromHex(colorText);
            } catch (NumberFormatException e) {
                WaypointDimension dim = WaypointDimension.fromId(defaultDimension);
                color = dim.getDefaultColor();
            }

            String group = this.groupField.getText().trim();
            if (group.isEmpty()) {
                group = null;
            }

            if (isEditing) {
                // Update existing waypoint
                editingWaypoint.setName(name);
                editingWaypoint.setX(x);
                editingWaypoint.setY(y);
                editingWaypoint.setZ(z);
                editingWaypoint.setColor(color);
                editingWaypoint.setGroup(group);
                waypointManager.updateWaypoint(editingWaypoint);
            } else {
                // Create new waypoint
                WaypointDimension dim = WaypointDimension.fromId(defaultDimension);
                Waypoint newWaypoint = new Waypoint(name, x, y, z, defaultDimension, color, dim.getDefaultIcon(), group);
                waypointManager.addWaypoint(newWaypoint);
            }

            this.close();
        } catch (NumberFormatException e) {
            // Could show error message for invalid coordinates
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);

        // Title
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 20, 0xFFFFFF);

        // Field labels
        int centerX = this.width / 2;
        int startY = this.height / 2 - 120;
        int fieldSpacing = 35;

        context.drawTextWithShadow(this.textRenderer, "Name:", centerX - FIELD_WIDTH / 2, startY - 12, 0xFFFFFF);
        
        startY += fieldSpacing;
        context.drawTextWithShadow(this.textRenderer, "Coordinates:", centerX - FIELD_WIDTH / 2, startY - 12, 0xFFFFFF);
        context.drawTextWithShadow(this.textRenderer, "X", centerX - FIELD_WIDTH / 2, startY + FIELD_HEIGHT + 2, 0xCCCCCC);
        context.drawTextWithShadow(this.textRenderer, "Y", centerX - FIELD_WIDTH / 6, startY + FIELD_HEIGHT + 2, 0xCCCCCC);
        context.drawTextWithShadow(this.textRenderer, "Z", centerX + FIELD_WIDTH / 6 + 5, startY + FIELD_HEIGHT + 2, 0xCCCCCC);

        startY += fieldSpacing;
        context.drawTextWithShadow(this.textRenderer, "Color:", centerX - FIELD_WIDTH / 2, startY - 12, 0xFFFFFF);
        
        // Color preview
        try {
            int color = Waypoint.colorFromHex(this.colorField.getText());
            context.fill(centerX + FIELD_WIDTH / 2 + 10, startY, centerX + FIELD_WIDTH / 2 + 30, startY + FIELD_HEIGHT, color | 0xFF000000);
        } catch (NumberFormatException ignored) {}

        startY += fieldSpacing;
        context.drawTextWithShadow(this.textRenderer, "Group:", centerX - FIELD_WIDTH / 2, startY - 12, 0xFFFFFF);

        // Render text fields
        this.nameField.render(context, mouseX, mouseY, delta);
        this.xField.render(context, mouseX, mouseY, delta);
        this.yField.render(context, mouseX, mouseY, delta);
        this.zField.render(context, mouseX, mouseY, delta);
        this.colorField.render(context, mouseX, mouseY, delta);
        this.groupField.render(context, mouseX, mouseY, delta);

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public void close() {
        if (parentScreen != null) {
            parentScreen.refreshList();
            this.client.setScreen(parentScreen);
        } else {
            super.close();
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 257) { // Enter key
            saveWaypoint();
            return true;
        }
        
        if (this.nameField.keyPressed(keyCode, scanCode, modifiers) ||
            this.xField.keyPressed(keyCode, scanCode, modifiers) ||
            this.yField.keyPressed(keyCode, scanCode, modifiers) ||
            this.zField.keyPressed(keyCode, scanCode, modifiers) ||
            this.colorField.keyPressed(keyCode, scanCode, modifiers) ||
            this.groupField.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (this.nameField.charTyped(chr, modifiers) ||
            this.xField.charTyped(chr, modifiers) ||
            this.yField.charTyped(chr, modifiers) ||
            this.zField.charTyped(chr, modifiers) ||
            this.colorField.charTyped(chr, modifiers) ||
            this.groupField.charTyped(chr, modifiers)) {
            return true;
        }
        
        return super.charTyped(chr, modifiers);
    }
}
