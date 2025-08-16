package com.featherwaypoints.manager;

import com.featherwaypoints.data.Waypoint;
import com.featherwaypoints.data.WaypointDimension;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class WaypointManager {
    private static WaypointManager instance;
    private final Map<String, Waypoint> waypoints = new ConcurrentHashMap<>();
    private final Path waypointsFile;
    private final Gson gson;

    private WaypointManager() {
        Path configDir = FabricLoader.getInstance().getConfigDir().resolve("featherwaypoints");
        try {
            Files.createDirectories(configDir);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create config directory", e);
        }
        
        this.waypointsFile = configDir.resolve("waypoints.json");
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
        
        loadWaypoints();
    }

    public static WaypointManager getInstance() {
        if (instance == null) {
            instance = new WaypointManager();
        }
        return instance;
    }

    // CRUD Operations
    public void addWaypoint(Waypoint waypoint) {
        waypoints.put(waypoint.getId(), waypoint);
        saveWaypoints();
    }

    public void updateWaypoint(Waypoint waypoint) {
        waypoints.put(waypoint.getId(), waypoint);
        saveWaypoints();
    }

    public void removeWaypoint(String id) {
        waypoints.remove(id);
        saveWaypoints();
    }

    public void removeWaypoint(Waypoint waypoint) {
        removeWaypoint(waypoint.getId());
    }

    public Waypoint getWaypoint(String id) {
        return waypoints.get(id);
    }

    public List<Waypoint> getAllWaypoints() {
        return new ArrayList<>(waypoints.values());
    }

    public List<Waypoint> getWaypointsInDimension(String dimension) {
        return waypoints.values().stream()
                .filter(waypoint -> waypoint.getDimension().equals(dimension))
                .collect(Collectors.toList());
    }

    public List<Waypoint> getVisibleWaypoints() {
        return waypoints.values().stream()
                .filter(Waypoint::isVisible)
                .collect(Collectors.toList());
    }

    public List<Waypoint> getVisibleWaypointsInDimension(String dimension) {
        return waypoints.values().stream()
                .filter(waypoint -> waypoint.getDimension().equals(dimension) && waypoint.isVisible())
                .collect(Collectors.toList());
    }

    // Search functionality
    public List<Waypoint> searchWaypoints(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getAllWaypoints();
        }
        
        String lowerQuery = query.toLowerCase().trim();
        return waypoints.values().stream()
                .filter(waypoint -> 
                    waypoint.getName().toLowerCase().contains(lowerQuery) ||
                    (waypoint.getGroup() != null && waypoint.getGroup().toLowerCase().contains(lowerQuery)) ||
                    waypoint.getDimension().toLowerCase().contains(lowerQuery)
                )
                .collect(Collectors.toList());
    }

    public List<Waypoint> searchWaypointsInDimension(String query, String dimension) {
        return searchWaypoints(query).stream()
                .filter(waypoint -> waypoint.getDimension().equals(dimension))
                .collect(Collectors.toList());
    }

    // Group management
    public Set<String> getAllGroups() {
        return waypoints.values().stream()
                .map(Waypoint::getGroup)
                .filter(Objects::nonNull)
                .filter(group -> !group.trim().isEmpty())
                .collect(Collectors.toSet());
    }

    public List<Waypoint> getWaypointsInGroup(String group) {
        return waypoints.values().stream()
                .filter(waypoint -> Objects.equals(waypoint.getGroup(), group))
                .collect(Collectors.toList());
    }

    // Utility methods
    public Waypoint createWaypoint(String name, double x, double y, double z, String dimension) {
        WaypointDimension dim = WaypointDimension.fromId(dimension);
        return new Waypoint(name, x, y, z, dimension, dim.getDefaultColor(), dim.getDefaultIcon(), null);
    }

    public Waypoint createWaypoint(String name, double x, double y, double z, String dimension, int color, String icon, String group) {
        return new Waypoint(name, x, y, z, dimension, color, icon, group);
    }

    public List<Waypoint> getNearestWaypoints(double x, double y, double z, String dimension, int limit) {
        return waypoints.values().stream()
                .filter(waypoint -> waypoint.getDimension().equals(dimension))
                .sorted((w1, w2) -> Double.compare(w1.getDistanceTo(x, y, z), w2.getDistanceTo(x, y, z)))
                .limit(limit)
                .collect(Collectors.toList());
    }

    // File operations
    private void loadWaypoints() {
        if (!Files.exists(waypointsFile)) {
            return;
        }

        try (FileReader reader = new FileReader(waypointsFile.toFile())) {
            Type listType = new TypeToken<List<Waypoint>>(){}.getType();
            List<Waypoint> loadedWaypoints = gson.fromJson(reader, listType);
            
            if (loadedWaypoints != null) {
                waypoints.clear();
                for (Waypoint waypoint : loadedWaypoints) {
                    waypoints.put(waypoint.getId(), waypoint);
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to load waypoints: " + e.getMessage());
        } catch (JsonSyntaxException e) {
            System.err.println("Invalid waypoints JSON format: " + e.getMessage());
        }
    }

    private void saveWaypoints() {
        try (FileWriter writer = new FileWriter(waypointsFile.toFile())) {
            List<Waypoint> waypointList = new ArrayList<>(waypoints.values());
            gson.toJson(waypointList, writer);
        } catch (IOException e) {
            System.err.println("Failed to save waypoints: " + e.getMessage());
        }
    }

    // Import/Export functionality
    public void importWaypoints(String jsonData) throws JsonSyntaxException {
        Type listType = new TypeToken<List<Waypoint>>(){}.getType();
        List<Waypoint> importedWaypoints = gson.fromJson(jsonData, listType);
        
        if (importedWaypoints != null) {
            for (Waypoint waypoint : importedWaypoints) {
                waypoints.put(waypoint.getId(), waypoint);
            }
            saveWaypoints();
        }
    }

    public String exportWaypoints() {
        List<Waypoint> waypointList = new ArrayList<>(waypoints.values());
        return gson.toJson(waypointList);
    }

    public void clearAllWaypoints() {
        waypoints.clear();
        saveWaypoints();
    }

    public int getWaypointCount() {
        return waypoints.size();
    }

    public int getWaypointCountInDimension(String dimension) {
        return (int) waypoints.values().stream()
                .filter(waypoint -> waypoint.getDimension().equals(dimension))
                .count();
    }
}
