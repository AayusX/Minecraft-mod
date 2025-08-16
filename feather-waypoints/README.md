# ATPoint

A lightweight Minecraft Fabric mod for version 1.20.1 that provides an advanced waypoint system for enhanced navigation and location management. Created by **Mr.Aayush Bhandari**, this mod allows players to easily manage waypoints with a clean, intuitive interface and visual indicators in the world.

## Features

### Core Functionality
- **Add, edit, delete, and search waypoints**
- **JSON-based persistence** - waypoints are saved in `config/featherwaypoints/waypoints.json`
- **Dimension support** - separate waypoints for Overworld, Nether, and End
- **Visual waypoint rendering** in the world with distance indicators
- **Customizable colors** for each waypoint
- **Group organization** for better waypoint management

### User Interface
- **Intuitive GUI** for waypoint management
- **Search functionality** to quickly find specific waypoints
- **In-world HUD** showing waypoint names and distances
- **Easy-to-use forms** for adding and editing waypoints

### Keybindings
- **M** - Open waypoints list for current dimension
- **B** - Add waypoint at current location

## Technical Specifications

- **Minecraft Version**: 1.20.1
- **Fabric Loader**: 0.14.21+
- **Java Version**: 17+
- **Dependencies**: Fabric API

## Installation

1. Install [Fabric Loader](https://fabricmc.net/use/installer/) for Minecraft 1.20.1
2. Download [Fabric API](https://modrinth.com/mod/fabric-api) for 1.20.1
3. Build this mod (see Building section below)
4. Place the compiled JAR file in your `.minecraft/mods/` folder
5. Launch Minecraft with the Fabric profile

## Building

### Prerequisites
- Java 17 or higher
- Git (optional)

### Build Steps
```bash
# Navigate to project directory
cd atpoint

# Build the mod
./gradlew build

# The compiled JAR will be in build/libs/
```

### Development Setup
```bash
# Generate IDE run configurations
./gradlew genEclipseRuns  # For Eclipse
./gradlew genIntellijRuns # For IntelliJ IDEA

# Run the client in development
./gradlew runClient
```

## Usage

### Adding Waypoints
1. Press **B** to add a waypoint at your current location, or
2. Press **M** to open the waypoints GUI and click "Add"
3. Fill in the waypoint details:
   - **Name**: Display name for the waypoint
   - **Coordinates**: X, Y, Z position (auto-filled with current position)
   - **Color**: Hex color code (e.g., #FF0000 for red)
   - **Group**: Optional group name for organization

### Managing Waypoints
1. Press **M** to open the waypoints list
2. **Search**: Type in the search box to filter waypoints
3. **Edit**: Click the "Edit" button on any waypoint entry
4. **Delete**: Right-click on a waypoint entry, or use the delete button in edit mode
5. **Clear All**: Remove all waypoints in the current dimension

### Viewing Waypoints
- Waypoints appear as colored diamond markers in the world
- Names and distances are displayed above each waypoint
- Only waypoints in your current dimension are shown
- Maximum render distance: 1000 blocks

## File Structure

```
feather-waypoints/
├── src/main/java/com/featherwaypoints/
│   ├── FeatherWaypointsClient.java       # Main mod class
│   ├── data/
│   │   ├── Waypoint.java                 # Waypoint data class
│   │   └── WaypointDimension.java        # Dimension handling
│   ├── gui/
│   │   ├── WaypointListScreen.java       # Waypoint list GUI
│   │   └── WaypointEditScreen.java       # Waypoint edit form
│   ├── manager/
│   │   └── WaypointManager.java          # Waypoint CRUD operations
│   └── render/
│       └── WaypointRenderer.java         # World rendering
├── src/main/resources/
│   ├── fabric.mod.json                   # Mod metadata
│   ├── featherwaypoints.mixins.json      # Mixin configuration
│   └── assets/featherwaypoints/
│       ├── lang/en_us.json               # English translations
│       └── textures/icons/               # Waypoint icons
├── build.gradle                          # Build configuration
├── gradle.properties                     # Project properties
└── README.md                            # This file
```

## Configuration

Waypoints are stored in JSON format at:
```
.minecraft/config/featherwaypoints/waypoints.json
```

Example waypoint data:
```json
[
  {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "name": "Home Base",
    "x": 100.0,
    "y": 64.0,
    "z": 200.0,
    "dimension": "minecraft:overworld",
    "color": 65280,
    "icon": "grass_block.png",
    "group": "Bases",
    "visible": true,
    "created_at": 1640995200000
  }
]
```

## Compatibility

This mod is designed to be lightweight and should be compatible with most other Fabric mods. It only affects the client-side and doesn't require server installation.

## License

MIT License - See LICENSE file for details.

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Test thoroughly
5. Submit a pull request

## Author

**ATPoint** is created and maintained by **Mr.Aayush Bhandari**.

- A passionate Minecraft modder dedicated to enhancing the player experience
- Focused on creating lightweight, efficient, and user-friendly modifications
- Committed to providing high-quality tools for navigation and world management

## Support

For issues, feature requests, or questions, please create an issue on the project repository.

---

*ATPoint - Precision Navigation for Minecraft* 🎯  
*Created with ❤️ by Mr.Aayush Bhandari*
