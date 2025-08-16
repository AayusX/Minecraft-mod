package com.featherwaypoints.render;

import com.featherwaypoints.data.Waypoint;
import com.featherwaypoints.manager.WaypointManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import com.mojang.blaze3d.systems.RenderSystem;

import java.util.List;

public class WaypointRenderer {
    private static final int MAX_RENDER_DISTANCE = 1000;
    private static final float WAYPOINT_SIZE = 0.03f;
    private static final float TEXT_SCALE = 0.02f;

    public static void renderWaypoints(MatrixStack matrices, Camera camera) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.world == null) {
            return;
        }

        String currentDimension = client.world.getRegistryKey().getValue().toString();
        WaypointManager waypointManager = WaypointManager.getInstance();
        List<Waypoint> waypoints = waypointManager.getVisibleWaypointsInDimension(currentDimension);

        if (waypoints.isEmpty()) {
            return;
        }

        Vec3d cameraPos = camera.getPos();
        
        // Set up rendering state
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest();
        RenderSystem.disableCull();

        matrices.push();
        
        for (Waypoint waypoint : waypoints) {
            Vec3d waypointPos = new Vec3d(waypoint.getX() + 0.5, waypoint.getY() + 0.5, waypoint.getZ() + 0.5);
            double distance = cameraPos.distanceTo(waypointPos);
            
            if (distance > MAX_RENDER_DISTANCE) {
                continue;
            }

            renderWaypoint(matrices, waypoint, waypointPos, cameraPos, camera, distance);
        }

        matrices.pop();
        
        // Reset rendering state
        RenderSystem.enableDepthTest();
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
    }

    private static void renderWaypoint(MatrixStack matrices, Waypoint waypoint, Vec3d waypointPos, 
                                     Vec3d cameraPos, Camera camera, double distance) {
        MinecraftClient client = MinecraftClient.getInstance();
        TextRenderer textRenderer = client.textRenderer;

        matrices.push();

        // Translate to waypoint position
        matrices.translate(waypointPos.x - cameraPos.x, waypointPos.y - cameraPos.y, waypointPos.z - cameraPos.z);

        // Face the camera
        matrices.multiply(camera.getRotation());
        matrices.scale(-1.0f, -1.0f, 1.0f);

        // Scale based on distance for consistent size
        float scale = (float) Math.max(0.1, Math.min(1.0, distance * WAYPOINT_SIZE));
        matrices.scale(scale, scale, scale);

        Matrix4f matrix = matrices.peek().getPositionMatrix();

        // Draw waypoint marker (diamond shape)
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        
        float size = 8.0f;
        int color = waypoint.getColor();
        float r = ((color >> 16) & 0xFF) / 255.0f;
        float g = ((color >> 8) & 0xFF) / 255.0f;
        float b = (color & 0xFF) / 255.0f;
        float a = 0.8f;

        // Draw diamond background
        buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        buffer.vertex(matrix, 0, size, 0).color(r, g, b, a).next();
        buffer.vertex(matrix, -size, 0, 0).color(r, g, b, a).next();
        buffer.vertex(matrix, 0, -size, 0).color(r, g, b, a).next();
        buffer.vertex(matrix, size, 0, 0).color(r, g, b, a).next();
        tessellator.draw();

        // Draw diamond outline
        buffer.begin(VertexFormat.DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION_COLOR);
        buffer.vertex(matrix, 0, size, 0).color(1.0f, 1.0f, 1.0f, 1.0f).next();
        buffer.vertex(matrix, -size, 0, 0).color(1.0f, 1.0f, 1.0f, 1.0f).next();
        buffer.vertex(matrix, 0, -size, 0).color(1.0f, 1.0f, 1.0f, 1.0f).next();
        buffer.vertex(matrix, size, 0, 0).color(1.0f, 1.0f, 1.0f, 1.0f).next();
        buffer.vertex(matrix, 0, size, 0).color(1.0f, 1.0f, 1.0f, 1.0f).next();
        tessellator.draw();

        // Draw text
        matrices.push();
        matrices.scale(TEXT_SCALE, TEXT_SCALE, TEXT_SCALE);
        
        String name = waypoint.getName();
        String distanceText = String.format("%.0fm", distance);
        
        int nameWidth = textRenderer.getWidth(name);
        int distanceWidth = textRenderer.getWidth(distanceText);
        
        // Draw name
        matrices.push();
        matrices.translate(-nameWidth / 2.0f, -size / TEXT_SCALE - 30, 0);
        drawTextWithBackground(matrices, textRenderer, name, 0, 0, 0xFFFFFF);
        matrices.pop();
        
        // Draw distance
        matrices.push();
        matrices.translate(-distanceWidth / 2.0f, -size / TEXT_SCALE - 15, 0);
        drawTextWithBackground(matrices, textRenderer, distanceText, 0, 0, 0xCCCCCC);
        matrices.pop();
        
        matrices.pop(); // Text scaling

        matrices.pop(); // Waypoint transform
    }

    private static void drawTextWithBackground(MatrixStack matrices, TextRenderer textRenderer, 
                                             String text, int x, int y, int color) {
        int width = textRenderer.getWidth(text);
        int height = textRenderer.fontHeight;
        
        Matrix4f matrix = matrices.peek().getPositionMatrix();
        
        // Draw background
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        buffer.vertex(matrix, x - 2, y - 1, 0).color(0, 0, 0, 128).next();
        buffer.vertex(matrix, x - 2, y + height + 1, 0).color(0, 0, 0, 128).next();
        buffer.vertex(matrix, x + width + 2, y + height + 1, 0).color(0, 0, 0, 128).next();
        buffer.vertex(matrix, x + width + 2, y - 1, 0).color(0, 0, 0, 128).next();
        tessellator.draw();
        
        // Draw text
        textRenderer.draw(text, x, y, color, false, matrices.peek().getPositionMatrix(), 
                         MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers(), 
                         TextRenderer.TextLayerType.NORMAL, 0, 15728880);
    }

    public static void renderWaypointBeam(MatrixStack matrices, Camera camera, Waypoint waypoint) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.world == null) {
            return;
        }

        Vec3d cameraPos = camera.getPos();
        Vec3d waypointPos = new Vec3d(waypoint.getX() + 0.5, waypoint.getY(), waypoint.getZ() + 0.5);
        
        double distance = cameraPos.distanceTo(waypointPos);
        if (distance > MAX_RENDER_DISTANCE) {
            return;
        }

        matrices.push();

        // Translate to waypoint position
        matrices.translate(waypointPos.x - cameraPos.x, waypointPos.y - cameraPos.y, waypointPos.z - cameraPos.z);

        Matrix4f matrix = matrices.peek().getPositionMatrix();
        int color = waypoint.getColor();
        float r = ((color >> 16) & 0xFF) / 255.0f;
        float g = ((color >> 8) & 0xFF) / 255.0f;
        float b = (color & 0xFF) / 255.0f;
        float a = 0.4f;

        // Draw beam from bedrock to build limit
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest();
        
        buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        
        float beamWidth = 0.2f;
        float yMin = (float)(-64 - waypointPos.y);  // From bedrock
        float yMax = (float)(320 - waypointPos.y);  // To build limit
        
        // Four sides of the beam
        // North face
        buffer.vertex(matrix, -beamWidth, yMin, -beamWidth).color(r, g, b, a).next();
        buffer.vertex(matrix, -beamWidth, yMax, -beamWidth).color(r, g, b, 0).next();
        buffer.vertex(matrix, beamWidth, yMax, -beamWidth).color(r, g, b, 0).next();
        buffer.vertex(matrix, beamWidth, yMin, -beamWidth).color(r, g, b, a).next();
        
        // South face
        buffer.vertex(matrix, beamWidth, yMin, beamWidth).color(r, g, b, a).next();
        buffer.vertex(matrix, beamWidth, yMax, beamWidth).color(r, g, b, 0).next();
        buffer.vertex(matrix, -beamWidth, yMax, beamWidth).color(r, g, b, 0).next();
        buffer.vertex(matrix, -beamWidth, yMin, beamWidth).color(r, g, b, a).next();
        
        // East face
        buffer.vertex(matrix, beamWidth, yMin, -beamWidth).color(r, g, b, a).next();
        buffer.vertex(matrix, beamWidth, yMax, -beamWidth).color(r, g, b, 0).next();
        buffer.vertex(matrix, beamWidth, yMax, beamWidth).color(r, g, b, 0).next();
        buffer.vertex(matrix, beamWidth, yMin, beamWidth).color(r, g, b, a).next();
        
        // West face
        buffer.vertex(matrix, -beamWidth, yMin, beamWidth).color(r, g, b, a).next();
        buffer.vertex(matrix, -beamWidth, yMax, beamWidth).color(r, g, b, 0).next();
        buffer.vertex(matrix, -beamWidth, yMax, -beamWidth).color(r, g, b, 0).next();
        buffer.vertex(matrix, -beamWidth, yMin, -beamWidth).color(r, g, b, a).next();
        
        tessellator.draw();
        
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();

        matrices.pop();
    }
}
