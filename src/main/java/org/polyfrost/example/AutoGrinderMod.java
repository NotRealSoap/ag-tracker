/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.fml.common.Mod
 *  net.minecraftforge.fml.common.Mod$EventHandler
 *  net.minecraftforge.fml.common.Mod$Instance
 *  net.minecraftforge.fml.common.event.FMLInitializationEvent
 */
package org.polyfrost.example;

import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import org.polyfrost.example.autooof.AutoOof;
import org.polyfrost.example.config.AutoGrinderConfig;
import org.polyfrost.example.config.TrackerCommand;
import org.polyfrost.example.module.ModuleManager;
import org.polyfrost.example.module.modules.StatTracker;
import org.polyfrost.example.sensor.MacroCheckSensor;
import org.polyfrost.example.ui.StatScoreboard;

@Mod(modid="examplemod", name="AutoGrinder", version="1.0.0")
public class AutoGrinderMod {
    public static final String MODID = "examplemod";
    public static final String NAME = "AutoGrinder";
    public static final String VERSION = "1.0.0";
    @Mod.Instance(value="examplemod")
    public static AutoGrinderMod INSTANCE;
    public static AutoGrinderConfig config;
    public static ModuleManager moduleManager;

    @Mod.EventHandler
    public void onInit(FMLInitializationEvent fMLInitializationEvent) {
        config = new AutoGrinderConfig();
        moduleManager = new ModuleManager();
        MacroCheckSensor.register();
        AutoOof.register();

        // Initialize tracker and scoreboard
        StatTracker tracker = moduleManager.statTracker;
        StatScoreboard scoreboard = new StatScoreboard(tracker);
        ClientCommandHandler.instance.registerCommand(new TrackerCommand(tracker, scoreboard));
    }
}

