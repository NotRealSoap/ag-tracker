/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.channel.ChannelDuplexHandler
 *  io.netty.channel.ChannelHandler
 *  io.netty.channel.ChannelHandlerContext
 *  io.netty.channel.ChannelPipeline
 *  io.netty.channel.ChannelPromise
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.entity.EntityPlayerSP
 *  net.minecraft.client.gui.Gui
 *  net.minecraft.client.gui.ScaledResolution
 *  net.minecraft.client.multiplayer.WorldClient
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.entity.boss.BossStatus
 *  net.minecraft.network.play.client.C01PacketChatMessage
 *  net.minecraft.network.play.client.C03PacketPlayer
 *  net.minecraft.network.play.server.S08PacketPlayerPosLook
 *  net.minecraft.util.ChatComponentText
 *  net.minecraft.util.IChatComponent
 *  net.minecraftforge.client.event.ClientChatReceivedEvent
 *  net.minecraftforge.client.event.RenderGameOverlayEvent$ElementType
 *  net.minecraftforge.client.event.RenderGameOverlayEvent$Post
 *  net.minecraftforge.common.MinecraftForge
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 *  net.minecraftforge.fml.common.gameevent.TickEvent$ClientTickEvent
 *  net.minecraftforge.fml.common.gameevent.TickEvent$Phase
 *  net.minecraftforge.fml.common.network.FMLNetworkEvent$ClientConnectedToServerEvent
 */
package org.polyfrost.example.sensor;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import java.util.Locale;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.boss.BossStatus;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import org.polyfrost.example.AutoGrinderMod;
import org.polyfrost.example.module.ModuleManager;
import org.polyfrost.example.module.modules.PitMap;
import org.polyfrost.example.sensor.CheckMath;
import org.polyfrost.example.utils.ScoreboardUtils;

public final class MacroCheckSensor {
    static final float ROTATION_THRESHOLD_DEG = 1.0f;
    static final double TELEPORT_THRESHOLD_BLOCKS = 1.0;
    static final int ALERT_ARGB = 0x33FF0000;
    static final long ALERT_DURATION_MS = 10000L;
    static final boolean VERBOSE = true;
    static final long OWN_TELEPORT_COMMAND_MS = 5000L;
    static final long CONNECTION_MESSAGE_MS = 10000L;
    static final long AFK_WARNING_MS = 15000L;
    static final long DEATH_MS = 3000L;
    static final long WORLD_CHANGE_MS = 3000L;
    static final long EVENT_CHANGE_MS = 3000L;
    static final long CONNECTION_RETRO_MS = 5000L;
    static final long DEATH_RETRO_MS = 2000L;
    static final long EVENT_RETRO_MS = 3000L;
    private static final String HANDLER_NAME = "autogrinder_macro_check_sensor";
    private static final String[] TELEPORT_COMMANDS = new String[]{"/spawn", "/hub", "/play", "/respawn", "/warp", "/l", "/rejoin", "/oof"};
    private static final String[] CONNECTION_MESSAGES = new String[]{"your connection timed out to the server, so you have been routed to limbo!", "an exception occurred in your connection, so you were put in the pit lobby!", "out of sync, check your internet connection.", "you were spawned in limbo.", "you were kicked for inactivity!"};
    private static final Minecraft mc = Minecraft.func_71410_x();
    private static volatile long lastOwnTeleportCommandMs;
    private static volatile boolean hasSentRotation;
    private static volatile float lastSentYaw;
    private static volatile float lastSentPitch;
    private static long lastConnectionMessageMs;
    private static long lastAfkWarningMs;
    private static long lastDeathMs;
    private static long lastWorldChangeMs;
    private static long lastEventChangeMs;
    private static EntityPlayerSP trackedPlayer;
    private static WorldClient trackedWorld;
    private static String eventKey;
    private static String eventType;
    private static long eventStartMs;
    private static long alertStartMs;
    private static long alertUntilMs;
    private static int checksDetected;

    public static void register() {
        MinecraftForge.EVENT_BUS.register((Object)new MacroCheckSensor());
    }

    public static int getChecksDetected() {
        return checksDetected;
    }

    public static boolean isAlertActive() {
        return System.currentTimeMillis() < alertUntilMs;
    }

    @SubscribeEvent
    public void onConnect(FMLNetworkEvent.ClientConnectedToServerEvent clientConnectedToServerEvent) {
        hasSentRotation = false;
        ChannelPipeline channelPipeline = clientConnectedToServerEvent.manager.channel().pipeline();
        if (channelPipeline.get(HANDLER_NAME) != null) {
            channelPipeline.remove(HANDLER_NAME);
        }
        channelPipeline.addBefore("packet_handler", HANDLER_NAME, (ChannelHandler)new ChannelDuplexHandler(){

            public void channelRead(ChannelHandlerContext channelHandlerContext, Object object) throws Exception {
                if (object instanceof S08PacketPlayerPosLook) {
                    S08PacketPlayerPosLook s08PacketPlayerPosLook = (S08PacketPlayerPosLook)object;
                    try {
                        mc.func_152344_a(() -> MacroCheckSensor.evaluateSafely(s08PacketPlayerPosLook));
                    }
                    catch (Throwable throwable) {
                        // empty catch block
                    }
                }
                super.channelRead(channelHandlerContext, object);
            }

            public void write(ChannelHandlerContext channelHandlerContext, Object object, ChannelPromise channelPromise) throws Exception {
                try {
                    C03PacketPlayer c03PacketPlayer;
                    if (object instanceof C01PacketChatMessage) {
                        MacroCheckSensor.onOutgoingChat(((C01PacketChatMessage)object).func_149439_c());
                    } else if (object instanceof C03PacketPlayer && (c03PacketPlayer = (C03PacketPlayer)object).func_149463_k()) {
                        lastSentYaw = c03PacketPlayer.func_149462_g();
                        lastSentPitch = c03PacketPlayer.func_149470_h();
                        hasSentRotation = true;
                    }
                }
                catch (Throwable throwable) {
                    // empty catch block
                }
                super.write(channelHandlerContext, object, channelPromise);
            }
        });
    }

    private static void onOutgoingChat(String string) {
        if (string == null) {
            return;
        }
        String string2 = string.trim().toLowerCase(Locale.ROOT);
        for (String string3 : TELEPORT_COMMANDS) {
            if (!string2.startsWith(string3)) continue;
            lastOwnTeleportCommandMs = System.currentTimeMillis();
            return;
        }
    }

    private static void evaluateSafely(S08PacketPlayerPosLook s08PacketPlayerPosLook) {
        try {
            MacroCheckSensor.evaluate(s08PacketPlayerPosLook);
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    private static void evaluate(S08PacketPlayerPosLook s08PacketPlayerPosLook) {
        EntityPlayerSP entityPlayerSP = MacroCheckSensor.mc.field_71439_g;
        WorldClient worldClient = MacroCheckSensor.mc.field_71441_e;
        if (entityPlayerSP == null || worldClient == null) {
            return;
        }
        long l = System.currentTimeMillis();
        if (entityPlayerSP != trackedPlayer || worldClient != trackedWorld) {
            trackedPlayer = entityPlayerSP;
            trackedWorld = worldClient;
            lastWorldChangeMs = l;
            return;
        }
        if (!MacroCheckSensor.isArmed()) {
            return;
        }
        Set set = s08PacketPlayerPosLook.func_179834_f();
        boolean bl = MacroCheckSensor.hasFlag(set, "X");
        boolean bl2 = MacroCheckSensor.hasFlag(set, "Y");
        boolean bl3 = MacroCheckSensor.hasFlag(set, "Z");
        boolean bl4 = MacroCheckSensor.hasFlag(set, "Y_ROT");
        boolean bl5 = MacroCheckSensor.hasFlag(set, "X_ROT");
        float f = entityPlayerSP.field_70177_z;
        float f2 = entityPlayerSP.field_70125_A;
        double d = entityPlayerSP.field_70165_t;
        double d2 = entityPlayerSP.field_70163_u;
        double d3 = entityPlayerSP.field_70161_v;
        float[] fArray = CheckMath.rotationDelta(bl4, bl5, s08PacketPlayerPosLook.func_148931_f(), s08PacketPlayerPosLook.func_148930_g(), f, f2);
        double[] dArray = CheckMath.positionDelta(bl, bl2, bl3, s08PacketPlayerPosLook.func_148932_c(), s08PacketPlayerPosLook.func_148928_d(), s08PacketPlayerPosLook.func_148933_e(), d, d2, d3);
        float f3 = CheckMath.totalRotation(fArray);
        double d4 = CheckMath.length(dArray);
        boolean bl6 = f3 >= 1.0f;
        boolean bl7 = d4 >= 1.0;
        String string = "";
        if (bl6 && hasSentRotation) {
            float f4 = f + fArray[0];
            float f5 = f2 + fArray[1];
            float f6 = Math.abs(CheckMath.wrapDegrees(f4 - lastSentYaw)) + Math.abs(f5 - lastSentPitch);
            if (f6 < 1.0f) {
                string = " \u00a78(snaps to your last sent rotation)";
            }
        }
        String string2 = String.format(Locale.ROOT, "S08: turn %.1f\u00b0 (yaw %s, pitch %s), move %.2f blocks", Float.valueOf(f3), bl4 ? "rel" : "abs", bl5 ? "rel" : "abs", d4);
        if (!bl6 && !bl7) {
            MacroCheckSensor.debug(string2 + " - ignored: no visible change");
            return;
        }
        String string3 = MacroCheckSensor.falsePositiveReason(l, bl7, d + dArray[0], d2 + dArray[1], d3 + dArray[2]);
        if (string3 != null) {
            MacroCheckSensor.debug(string2 + " - ignored: " + string3 + string);
            return;
        }
        MacroCheckSensor.debug(string2 + string);
        String string4 = bl6 && bl7 ? "ROTATION + TELEPORT" : (bl6 ? "ROTATION" : "TELEPORT");
        MacroCheckSensor.triggerAlert(string4, l);
    }

    private static String falsePositiveReason(long l, boolean bl, double d, double d2, double d3) {
        if (CheckMath.recent(l, lastOwnTeleportCommandMs, 5000L)) {
            return "you just sent a teleport command";
        }
        if (CheckMath.recent(l, lastWorldChangeMs, 3000L)) {
            return "world just changed";
        }
        if (CheckMath.recent(l, lastDeathMs, 3000L)) {
            return "you just died (respawn)";
        }
        if (bl && CheckMath.isPitSpawn(d, d2, d3, PitMap.SPAWN_Y_COORDINATE)) {
            return "landed in spawn (respawn)";
        }
        if (CheckMath.recent(l, lastConnectionMessageMs, 10000L)) {
            return "limbo/connection message";
        }
        if (CheckMath.recent(l, lastAfkWarningMs, 15000L)) {
            return "AFK warning";
        }
        if (CheckMath.recent(l, lastEventChangeMs, 3000L) || MacroCheckSensor.inEventTeleportWindow(l)) {
            return "Pit event teleport";
        }
        return null;
    }

    private static boolean isArmed() {
        ModuleManager moduleManager = AutoGrinderMod.moduleManager;
        if (moduleManager == null || moduleManager.toggleSprintModule == null || !moduleManager.toggleSprintModule.isEnabled()) {
            return false;
        }
        return ScoreboardUtils.getScoreboardDisplayName().contains("PIT");
    }

    private static boolean hasFlag(Set<?> set, String string) {
        if (set == null) {
            return false;
        }
        for (Object obj : set) {
            String string2;
            if (obj == null || !string.equals(string2 = obj instanceof Enum ? ((Enum)obj).name() : obj.toString())) continue;
            return true;
        }
        return false;
    }

    private static void triggerAlert(String string, long l) {
        boolean bl;
        boolean bl2 = bl = l < alertUntilMs;
        if (!bl) {
            ++checksDetected;
            alertStartMs = l;
        }
        alertUntilMs = l + 10000L;
        MacroCheckSensor.chat("\u00a7d\u00a7l[MACRO CHECK] Detected: " + string + (bl ? " (again)" : "") + " | Total: " + checksDetected);
    }

    private static void retroCancel(String string, long l, long l2) {
        if (l2 < alertUntilMs && l2 - alertStartMs <= l) {
            alertUntilMs = 0L;
            checksDetected = Math.max(0, checksDetected - 1);
            MacroCheckSensor.chat("\u00a77[MacroCheck] alert withdrawn: " + string);
        }
    }

    @SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent.Post post) {
        if (post.type != RenderGameOverlayEvent.ElementType.ALL) {
            return;
        }
        if (System.currentTimeMillis() >= alertUntilMs) {
            return;
        }
        if (MacroCheckSensor.mc.field_71439_g == null || MacroCheckSensor.mc.field_71441_e == null) {
            return;
        }
        ScaledResolution scaledResolution = new ScaledResolution(mc);
        GlStateManager.func_179097_i();
        GlStateManager.func_179147_l();
        GlStateManager.func_179120_a((int)770, (int)771, (int)1, (int)0);
        Gui.func_73734_a((int)0, (int)0, (int)scaledResolution.func_78326_a(), (int)scaledResolution.func_78328_b(), (int)0x33FF0000);
        GlStateManager.func_179084_k();
        GlStateManager.func_179126_j();
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent clientTickEvent) {
        if (clientTickEvent.phase != TickEvent.Phase.START) {
            return;
        }
        long l = System.currentTimeMillis();
        EntityPlayerSP entityPlayerSP = MacroCheckSensor.mc.field_71439_g;
        WorldClient worldClient = MacroCheckSensor.mc.field_71441_e;
        if (entityPlayerSP != trackedPlayer || worldClient != trackedWorld) {
            trackedPlayer = entityPlayerSP;
            trackedWorld = worldClient;
            lastWorldChangeMs = l;
        }
        MacroCheckSensor.updateEventState(l);
    }

    @SubscribeEvent
    public void onChat(ClientChatReceivedEvent clientChatReceivedEvent) {
        if (clientChatReceivedEvent.message == null) {
            return;
        }
        String string = CheckMath.stripFormatting(clientChatReceivedEvent.message.func_150260_c()).trim();
        String string2 = string.toLowerCase(Locale.ROOT);
        long l = System.currentTimeMillis();
        for (String string3 : CONNECTION_MESSAGES) {
            if (!string2.startsWith(string3)) continue;
            lastConnectionMessageMs = l;
            MacroCheckSensor.retroCancel("limbo/connection message", 5000L, l);
            break;
        }
        if (string2.contains("afk warning! kicking to lobby in 10 seconds")) {
            lastAfkWarningMs = l;
        }
        if (string.startsWith("DEATH!")) {
            lastDeathMs = l;
            MacroCheckSensor.retroCancel("you died (respawn)", 2000L, l);
        }
    }

    private static void updateEventState(long l) {
        String string = BossStatus.field_82826_b > 0 && BossStatus.field_82827_c != null ? CheckMath.stripFormatting(BossStatus.field_82827_c).trim() : "";
        String string2 = "";
        if (string.startsWith("MAJOR EVENT!")) {
            int n;
            String string3;
            String string4 = string.substring("MAJOR EVENT!".length()).trim();
            String string5 = CheckMath.eventType(string4);
            String string6 = string4.contains("Starting in ") ? "starting" : (string3 = string4.contains("Ending in ") ? "running" : "on");
            if (!string5.equals(eventType)) {
                eventType = string5;
                eventStartMs = 0L;
            }
            if ("starting".equals(string3) && (n = CheckMath.parseClock(string4.substring(string4.lastIndexOf("Starting in ") + "Starting in ".length()))) >= 0) {
                eventStartMs = l + (long)n * 1000L;
            }
            string2 = string5 + "|" + string3;
        } else {
            eventType = "";
            eventStartMs = 0L;
        }
        if (!string2.equals(eventKey)) {
            eventKey = string2;
            lastEventChangeMs = l;
            MacroCheckSensor.retroCancel("Pit event teleport", 3000L, l);
        }
    }

    private static boolean inEventTeleportWindow(long l) {
        if (eventStartMs <= 0L) {
            return false;
        }
        String string = eventType.toLowerCase(Locale.ROOT);
        if (string.contains("rage") && Math.abs(l - eventStartMs) <= 3000L) {
            return true;
        }
        return string.contains("spire") && Math.abs(l - (eventStartMs - 120000L)) <= 3000L;
    }

    private static void debug(String string) {
        MacroCheckSensor.chat("\u00a77[MacroCheck] " + string);
    }

    private static void chat(String string) {
        EntityPlayerSP entityPlayerSP = MacroCheckSensor.mc.field_71439_g;
        if (entityPlayerSP != null) {
            entityPlayerSP.func_145747_a((IChatComponent)new ChatComponentText(string));
        }
    }

    static {
        eventKey = "";
        eventType = "";
    }
}

