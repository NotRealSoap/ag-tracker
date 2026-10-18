package org.polyfrost.example.module.modules;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.polyfrost.example.module.Module;
import org.polyfrost.example.utils.Chat;

public class StatTracker extends Module {
    private static final Minecraft mc = Minecraft.func_71410_x();

    private int deaths = 0;
    private int uberstreaks = 0;
    private int mysticDrops = 0;
    private long sessionStartTime = 0;
    private int killsInSession = 0;
    private int oofCount = 0;

    private long lastUberstreakTime = 0;
    private long uberstreakStartTime = 0;
    private int totalUberstreakTime = 0;
    private int completedUberstreaks = 0;

    public StatTracker() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public void onEnable() {
        sessionStartTime = System.currentTimeMillis();
        deaths = 0;
        uberstreaks = 0;
        mysticDrops = 0;
        killsInSession = 0;
        oofCount = 0;
        completedUberstreaks = 0;
        totalUberstreakTime = 0;
        Chat.chat("§6[Tracker] Session started!");
    }

    @Override
    public void onDisable() {
        Chat.chat("§6[Tracker] Session ended! Stats saved.");
    }

    @SubscribeEvent
    public void onChatMessage(ClientChatReceivedEvent event) {
        if (!this.isEnabled()) return;

        String message = event.message.func_150260_c();

        // Detect mystic drops
        if (message.contains("Mystic Drop") || message.contains("MYSTIC")) {
            mysticDrops++;
        }

        // Detect uberstreak completion
        if (message.contains("Uberstreak") && message.contains("completed")) {
            uberstreaks++;
            completedUberstreaks++;

            if (uberstreakStartTime > 0) {
                long uberstreakDuration = System.currentTimeMillis() - uberstreakStartTime;
                totalUberstreakTime += (int)(uberstreakDuration / 1000);
                uberstreakStartTime = 0;
            }
        }

        // Detect uberstreak start
        if (message.contains("Uberstreak") && message.contains("started")) {
            uberstreakStartTime = System.currentTimeMillis();
        }

        // Detect /oof usage
        if (message.contains("/oof") || message.contains("oof used")) {
            oofCount++;
            // Oofs can only be used after 400 kills, so don't count deaths after this
        }

        // Detect deaths - but exclude deaths after 400+ kills with /oof
        if (message.contains("You died") || message.contains("was slain")) {
            if (killsInSession < 400 || oofCount == 0) {
                deaths++;
            }
        }

        // Detect kills
        if (message.contains("Kill") && message.contains("Streak")) {
            // Extract kill count from message
            try {
                String[] parts = message.split("Kill");
                if (parts.length > 1) {
                    String numPart = parts[1].replaceAll("[^0-9]", "").trim();
                    if (!numPart.isEmpty()) {
                        int killCount = Integer.parseInt(numPart);
                        killsInSession = Math.max(killsInSession, killCount);
                    }
                }
            } catch (Exception e) {
                // Ignore parse errors
            }
        }
    }

    public int getDeaths() {
        return deaths;
    }

    public int getUberstreaks() {
        return uberstreaks;
    }

    public int getMysticDrops() {
        return mysticDrops;
    }

    public long getSessionDuration() {
        if (sessionStartTime == 0) return 0;
        return (System.currentTimeMillis() - sessionStartTime) / 1000;
    }

    public int getAverageUberstreakTime() {
        if (completedUberstreaks == 0) return 0;
        return totalUberstreakTime / completedUberstreaks;
    }

    public String getSessionTimeFormatted() {
        long seconds = getSessionDuration();
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        long secs = seconds % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, secs);
    }

    public String getAverageUberstreakTimeFormatted() {
        int avgSeconds = getAverageUberstreakTime();
        if (avgSeconds == 0) return "N/A";
        long minutes = avgSeconds / 60;
        long seconds = avgSeconds % 60;
        return String.format("%d:%02d", minutes, seconds);
    }
}
