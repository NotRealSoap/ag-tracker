# AutoGrinder - Extended with Stat Tracker

This is your personal editable version of AutoGrinder with the new stat tracking features added!

## New Features

- **Stat Tracker** - Tracks deaths, uberstreaks, mystic drops, and session time
- **Minecraft-style Scoreboard** - Shows stats in a left-center overlay that matches Minecraft's native scoreboard style
- **Session Statistics** - Tracks session duration, average uberstreak completion time, and more
- **Tracker Commands** - `/tracker` command for full control

## Commands

```
/tracker                    - Toggle tracker on/off
/tracker toggle             - Toggle tracker on/off
/tracker stats              - Show detailed stats in chat
/tracker show               - Show the scoreboard overlay
/tracker hide               - Hide the scoreboard overlay
/tracker help               - Show all commands
```

## What It Tracks

- **Deaths** - Total death count (excludes deaths after /oof usage)
- **Uberstreaks** - Number of completed uberstreaks
- **Mystic Drops** - Mystic drops detected from chat
- **Session Duration** - How long your current session has been running
- **Average Uberstreak Time** - Average time to complete an uberstreak

## Building the Mod

### Prerequisites
- Java 8 JDK (get it if you don't have it)
- Gradle (will be downloaded automatically)

### Build Steps

**Windows (Command Prompt or PowerShell):**
```
cd C:\Users\kaden\Desktop\autogrinder-source
gradlew.bat build
```

**Mac/Linux:**
```
cd ~/Desktop/autogrinder-source
./gradlew build
```

The compiled JAR will be in: `build/libs/AutoGrinder-1.8.9.jar`

### Installation

Copy the JAR from `build/libs/` to your mods folder:
```
%APPDATA%\.minecraft\mods\1.8.9\
```

Then restart Minecraft!

## Editing the Code

The source code is organized as:
- `src/main/java/org/polyfrost/example/module/modules/StatTracker.java` - The stat tracking logic
- `src/main/java/org/polyfrost/example/ui/StatScoreboard.java` - The overlay rendering
- `src/main/java/org/polyfrost/example/config/TrackerCommand.java` - Commands for the tracker

Feel free to edit these files and rebuild whenever you want!

## Quick Tips

- **Want to change scoreboard position?** Edit `StatScoreboard.java`, look for `baseX` and `baseY` values
- **Want to change colors?** Edit the `Chat.chat()` calls in `StatTracker.java` or the hex values in `StatScoreboard.java`
- **Want to add more stats?** Add new variables to `StatTracker.java` and update both the chat detection and scoreboard rendering
- **Want to modify chat detection?** Edit the `onChatMessage()` method in `StatTracker.java`

## Troubleshooting

If you get compilation errors:
1. Make sure you have Java 8 JDK installed (not just JRE)
2. Try deleting the `build/` folder and rebuilding
3. Make sure you're in the correct directory when running the build command

Enjoy your customized AutoGrinder! 🎮
