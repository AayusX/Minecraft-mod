# ATPoint — Minecraft Waypoint Mod

> An advanced **waypoint system for Minecraft 1.20.1 (Fabric)** — add, edit,
> search, and color-code waypoints rendered in the world with distance labels.

Cross-dimensional waypoint tracking with groups, JSON persistence, and
draft-feather-first: it's fully client-side, so no server install needed.

## Features

- ➕ Add / ✏️ edit / 🗑️ delete / 🔎 search waypoints
- 🎨 Customizable waypoint colors
- 🌍 **Dimension-aware** — separate lists for Overworld, Nether, and End
- 🏷️ **Group organization**
- 🪧 **In-world rendering** — colored diamond markers + distance labels (up to
  1000 blocks render distance)
- ⌨️ **Keybindings**: `M` — waypoint list, `B` — add at current position
- 💾 JSON persistence (`config/featherwaypoints/waypoints.json`)
- 📦 **Client-side only** — no server mod required

## Installation

1. Install **Fabric Loader** for Minecraft **1.20.1** + **Fabric API**.
2. Download the mod JAR from **Releases** (or build it, below).
3. Drop it into `.minecraft/mods/`.

## Building

```bash
cd feather-waypoints
./gradlew build
# JAR → build/libs/

# dev client:
./gradlew runClient
```

## Tech Stack

- **Java 17+**, **Fabric Loader 0.14.21+**, **Fabric API**
- **Gradle** with **fabric-loom** 1.2+
- **Yarn mappings**, **Gson** 2.10.1

## Project Structure

```
└── feather-waypoints/
    ├── src/main/            # Java source + resources
    ├── build.gradle, settings.gradle, gradle.properties
    └── README.md            # full documentation (175 lines)
```

See [feather-waypoints/README.md](feather-waypoints/README.md) for the
complete feature docs, config reference, and usage guide.

## License

[MIT](feather-waypoints/LICENSE)