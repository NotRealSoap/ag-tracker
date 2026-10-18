/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.entity.EntityPlayerSP
 *  net.minecraft.command.ICommand
 *  net.minecraft.util.ChatComponentText
 *  net.minecraft.util.IChatComponent
 *  net.minecraftforge.client.ClientCommandHandler
 *  net.minecraftforge.common.MinecraftForge
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 *  net.minecraftforge.fml.common.gameevent.TickEvent$ClientTickEvent
 *  net.minecraftforge.fml.common.gameevent.TickEvent$Phase
 */
package org.polyfrost.example.autooof;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Properties;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.command.ICommand;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.polyfrost.example.autooof.AutoOofCommand;
import org.polyfrost.example.autooof.PitScoreboard;
import org.polyfrost.example.utils.ScoreboardUtils;

public final class AutoOof {
    static final double DEFAULT_THRESHOLD = 401.0;
    static final long RETRY_MS = 5000L;
    static final int CHECK_EVERY_TICKS = 5;
    private static final String FILE_NAME = "autogrinder-autooof.properties";
    private static final Minecraft mc = Minecraft.func_71410_x();
    private static boolean enabled = false;
    private static double threshold = 401.0;
    private static long lastOofMs;
    private static int ticks;

    public static void register() {
        AutoOof.load();
        MinecraftForge.EVENT_BUS.register((Object)new AutoOof());
        ClientCommandHandler.instance.func_71560_a((ICommand)new AutoOofCommand());
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent clientTickEvent) {
        if (clientTickEvent.phase != TickEvent.Phase.START || !enabled) {
            return;
        }
        if (++ticks % 5 != 0) {
            return;
        }
        EntityPlayerSP entityPlayerSP = AutoOof.mc.field_71439_g;
        if (entityPlayerSP == null || AutoOof.mc.field_71441_e == null) {
            return;
        }
        try {
            double d;
            if (!ScoreboardUtils.getScoreboardDisplayName().contains("PIT")) {
                return;
            }
            List<String> list = ScoreboardUtils.getScoreboardLines();
            String string = PitScoreboard.status(list);
            if (!PitScoreboard.shouldOof(string, d = PitScoreboard.streak(list), threshold)) {
                return;
            }
            long l = System.currentTimeMillis();
            if (l - lastOofMs < 5000L) {
                return;
            }
            lastOofMs = l;
            entityPlayerSP.func_145747_a((IChatComponent)new ChatComponentText("\u00a76[AutoOof]\u00a7r Uberstreak, streak " + PitScoreboard.fmt(d) + " > " + PitScoreboard.fmt(threshold) + " \u00a77- sending /oof"));
            entityPlayerSP.func_71165_d("/oof");
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    static boolean isEnabled() {
        return enabled;
    }

    static double getThreshold() {
        return threshold;
    }

    static void setEnabled(boolean bl) {
        enabled = bl;
        AutoOof.save();
    }

    static void setThreshold(double d) {
        threshold = d;
        AutoOof.save();
    }

    static String readNow() {
        if (AutoOof.mc.field_71441_e == null) {
            return "no world loaded";
        }
        List<String> list = ScoreboardUtils.getScoreboardLines();
        String string = PitScoreboard.status(list);
        double d = PitScoreboard.streak(list);
        return "Status: " + (string == null ? "(not on board)" : string) + ", Streak: " + (Double.isNaN(d) ? "(not on board)" : PitScoreboard.fmt(d));
    }

    private static File file() {
        File file = mc != null && AutoOof.mc.field_71412_D != null ? AutoOof.mc.field_71412_D : new File(".");
        return new File(new File(file, "config"), FILE_NAME);
    }

    private static void load() {
        File file = AutoOof.file();
        if (!file.isFile()) {
            return;
        }
        Properties properties = new Properties();
        try (FileInputStream fileInputStream = new FileInputStream(file);){
            properties.load(fileInputStream);
            enabled = Boolean.parseBoolean(properties.getProperty("enabled", "false"));
            double d = Double.parseDouble(properties.getProperty("threshold", String.valueOf(401.0)));
            if (!Double.isNaN(d) && d >= 0.0) {
                threshold = d;
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    private static void save() {
        File file = AutoOof.file();
        Properties properties = new Properties();
        properties.setProperty("enabled", String.valueOf(enabled));
        properties.setProperty("threshold", PitScoreboard.fmt(threshold));
        try {
            File file2 = file.getParentFile();
            if (file2 != null) {
                file2.mkdirs();
            }
            try (FileOutputStream fileOutputStream = new FileOutputStream(file);){
                properties.store(fileOutputStream, "AutoGrinder /autooof");
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }
}

