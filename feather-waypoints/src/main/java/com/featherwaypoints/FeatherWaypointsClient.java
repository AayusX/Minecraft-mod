package com.featherwaypoints;

import com.featherwaypoints.gui.WaypointListScreen;
import com.featherwaypoints.manager.WaypointManager;
import com.featherwaypoints.render.WaypointRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class FeatherWaypointsClient implements ClientModInitializer {
    public static final String MOD_ID = "featherwaypoints";
    
    private static KeyBinding openWaypointsKey;
    private static KeyBinding addWaypointKey;
    
    @Override
    public void onInitializeClient() {
        // Initialize waypoint manager
        WaypointManager.getInstance();
        
        // Register key bindings
        openWaypointsKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.featherwaypoints.open_waypoints",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_M,
            "category.featherwaypoints.waypoints"
        ));
        
        addWaypointKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.featherwaypoints.add_waypoint",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_B,
            "category.featherwaypoints.waypoints"
        ));
        
        // Register client tick event for key handling
        ClientTickEvents.END_CLIENT_TICK.register(this::onClientTick);
        
        // Register world render event for waypoint rendering
        WorldRenderEvents.AFTER_TRANSLUCENT.register((context) -> {
            WaypointRenderer.renderWaypoints(context.matrixStack(), context.camera());
        });
        
        System.out.println("ATPoint mod initialized by Mr.Aayush Bhandari!");
    }
    
    private void onClientTick(MinecraftClient client) {
        if (client.player == null || client.world == null) {
            return;
        }
        
        // Handle open waypoints key
        if (openWaypointsKey.wasPressed()) {
            String currentDimension = client.world.getRegistryKey().getValue().toString();
            client.setScreen(new WaypointListScreen(currentDimension));
        }
        
        // Handle add waypoint key
        if (addWaypointKey.wasPressed()) {
            String currentDimension = client.world.getRegistryKey().getValue().toString();
            WaypointManager waypointManager = WaypointManager.getInstance();
            
            // Create waypoint at current position
            String waypointName = "Waypoint " + (waypointManager.getWaypointCountInDimension(currentDimension) + 1);
            waypointManager.addWaypoint(waypointManager.createWaypoint(
                waypointName,
                client.player.getX(),
                client.player.getY(),
                client.player.getZ(),
                currentDimension
            ));
            
            // Send feedback to player
            if (client.player != null) {
                client.player.sendMessage(
                    net.minecraft.text.Text.literal("§aWaypoint added: " + waypointName), 
                    true
                );
            }
        }
    }
    
    public static String getModId() {
        return MOD_ID;
    }
}
