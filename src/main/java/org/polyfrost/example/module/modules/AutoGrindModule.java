/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.init.Blocks
 *  net.minecraft.util.BlockPos
 *  net.minecraft.util.ChatComponentText
 *  net.minecraft.util.IChatComponent
 *  net.minecraftforge.common.MinecraftForge
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 *  net.minecraftforge.fml.common.gameevent.TickEvent$ClientTickEvent
 *  net.minecraftforge.fml.common.gameevent.TickEvent$Phase
 */
package org.polyfrost.example.module.modules;

import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.polyfrost.example.config.AutoGrinderConfig;
import org.polyfrost.example.module.Module;
import org.polyfrost.example.module.modules.AutoClicker;
import org.polyfrost.example.module.modules.AutoHeal;
import org.polyfrost.example.module.modules.InventoryManager;
import org.polyfrost.example.module.modules.MidMoveAction;
import org.polyfrost.example.module.modules.PitMap;
import org.polyfrost.example.module.modules.PitMapType;
import org.polyfrost.example.module.modules.ResetAllMovements;
import org.polyfrost.example.module.modules.SpawnMoveAction;
import org.polyfrost.example.utils.AimSmoother;
import org.polyfrost.example.utils.AimUtils;
import org.polyfrost.example.utils.Chat;
import org.polyfrost.example.utils.MysticFinder;
import org.polyfrost.example.utils.ScoreboardUtils;
import org.polyfrost.example.utils.Timer;

public class AutoGrindModule
extends Module {
    private static final Minecraft mc = Minecraft.func_71410_x();
    private static int appliedMapIndex = -1;
    private final Timer limboTimer = new Timer();
    private final Timer spawnWaitTimer = new Timer();
    private final Timer enterPitTimer = new Timer();
    private final Timer playPitCooldownTimer = new Timer();
    private PlayAction prevAction;
    private double prevPosY = 999.0;
    private double invPosY;
    private int tempCount = 0;
    private boolean justJoinedHypixel = false;
    private boolean justEnteredPit = false;
    private boolean coordinateviewer = false;
    private boolean allowAutoGrind = false;

    public static void applySelectedMap(boolean bl) {
        PitMapType[] pitMapTypeArray = PitMapType.values();
        int n = AutoGrinderConfig.selectedMap;
        if (n < 0 || n >= pitMapTypeArray.length) {
            n = 0;
        }
        if (n == appliedMapIndex) {
            return;
        }
        PitMapType pitMapType = pitMapTypeArray[n];
        PitMap.MID_Y_COORDINATE = pitMapType.getFloorY();
        PitMap.SPAWN_Y_COORDINATE = pitMapType.getSpawnY();
        appliedMapIndex = n;
        if (bl) {
            Chat.chat("Map set to: " + pitMapType.getName() + " (mid " + PitMap.MID_Y_COORDINATE + ", spawn " + PitMap.SPAWN_Y_COORDINATE + ")");
        }
    }

    @Override
    public void onEnable() {
        Minecraft.func_71410_x().field_71439_g.func_145747_a((IChatComponent)new ChatComponentText("AutoGrinder: \u00a7bON"));
        AutoGrindModule.applySelectedMap(false);
        this.enterPitTimer.reset();
        this.coordinateviewer = true;
    }

    @Override
    public void onDisable() {
        Minecraft.func_71410_x().field_71439_g.func_145747_a((IChatComponent)new ChatComponentText("AutoGrinder: \u00a7cOFF"));
        SpawnMoveAction.initialize();
        AimUtils.clearGoal();
    }

    public AutoGrindModule() {
        MinecraftForge.EVENT_BUS.register((Object)this);
        MinecraftForge.EVENT_BUS.register((Object)new AimSmoother());
    }

    private boolean isInLimbo() {
        return AutoGrindModule.mc.field_71439_g.func_180425_c().func_177956_o() == 31;
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent clientTickEvent) {
        boolean bl;
        if (clientTickEvent.phase != TickEvent.Phase.START || !this.isEnabled() || AutoGrindModule.mc.field_71439_g == null) {
            return;
        }
        AutoGrindModule.applySelectedMap(true);
        if (InventoryManager.isMoving()) {
            InventoryManager.inventoryManage();
            return;
        }
        this.invPosY = AutoGrindModule.mc.field_71439_g.func_180425_c().func_177956_o();
        if (InventoryManager.isHotbarFull() && !InventoryManager.isMoving() && this.invPosY >= (double)PitMap.SPAWN_Y_COORDINATE) {
            InventoryManager.startSwapToInventory();
        }
        if (this.isInLimbo()) {
            if (this.limboTimer.passed(10000.0)) {
                Chat.say("/lobby");
                Chat.chat("IN LIMBO - RETURNING TO LOBBY");
                this.limboTimer.reset();
            }
            return;
        }
        if (!ScoreboardUtils.getScoreboardDisplayName().contains("PIT")) {
            this.justEnteredPit = false;
            if (AutoGrinderConfig.toggleAura && this.tempCount == 0) {
                Chat.say(".c load ag");
                Chat.chat("RESET MYAU CONFIG");
                Chat.chat("RESET PREV POSY");
                this.prevPosY = 999.0;
                this.tempCount = 1;
            }
            ResetAllMovements.immaResetAl();
        }
        if ((bl = ScoreboardUtils.getScoreboardDisplayName().equals("HYPIXEL")) && !this.justJoinedHypixel && this.playPitCooldownTimer.passed(10000.0)) {
            this.justJoinedHypixel = true;
            if (AutoGrinderConfig.toggleAura) {
                Chat.say(".c load ag");
                Chat.chat("RESET MYAU CONFIG");
                Chat.chat("RESET PREV POSY");
                this.prevPosY = 999.0;
                this.tempCount = 0;
            }
            Chat.say("/play pit");
            Chat.chat("JOINING THE PIT");
            this.playPitCooldownTimer.reset();
            this.spawnWaitTimer.reset();
            this.enterPitTimer.reset();
            this.allowAutoGrind = false;
        }
        if (!bl) {
            this.justJoinedHypixel = false;
            this.playPitCooldownTimer.reset();
        }
        if (!ScoreboardUtils.getScoreboardDisplayName().contains("PIT")) {
            return;
        }
        if (!this.justEnteredPit && this.enterPitTimer.passed(2000.0)) {
            this.justEnteredPit = true;
            this.coordinateviewer = true;
            this.allowAutoGrind = true;
        }
        if (this.coordinateviewer) {
            Chat.chat("Using map: " + PitMapType.values()[appliedMapIndex].getName() + " (mid " + PitMap.MID_Y_COORDINATE + ", spawn " + PitMap.SPAWN_Y_COORDINATE + ")");
            this.coordinateviewer = false;
        }
        double d = PitMap.SPAWN_Y_COORDINATE - (PitMap.SPAWN_Y_COORDINATE - PitMap.MID_Y_COORDINATE) / 2;
        if (AutoGrinderConfig.toggleAura) {
            double d2 = AutoGrindModule.mc.field_71439_g.field_70163_u;
            if ((d2 - d) * (this.prevPosY - d) < 0.0) {
                Chat.say(".t killaura");
            }
            this.prevPosY = d2;
        }
        if (this.allowAutoGrind) {
            PlayAction playAction = PitMap.SPAWN_Y_COORDINATE - 1 <= AutoGrindModule.mc.field_71439_g.func_180425_c().func_177956_o() ? PlayAction.SPAWN_MOVE : PlayAction.MID_MOVE;
            PlayAction playAction2 = this.prevAction;
            this.prevAction = playAction;
            if (playAction == PlayAction.SPAWN_MOVE) {
                if (playAction != playAction2) {
                    SpawnMoveAction.initialize();
                    this.spawnWaitTimer.reset();
                }
                if (ScoreboardUtils.getScoreboardLines().size() < 3) {
                    return;
                }
                String string = ScoreboardUtils.getScoreboardLines().get(2);
                if (AutoGrindModule.mc.field_71441_e.func_180495_p(new BlockPos(0, AutoGrindModule.mc.field_71439_g.func_180425_c().func_177956_o() - 1, 0)).func_177230_c() != Blocks.field_150350_a || string.contains("PIZZA") || string.contains("SPIRE") || string.contains("SQUADS") || string.contains("RAFFLE")) {
                    return;
                }
                if (this.spawnWaitTimer.passed(AutoGrinderConfig.spawnCooldown * 1000.0f)) {
                    SpawnMoveAction.moveFromSpawn();
                }
            } else {
                if (!AutoGrinderConfig.pickUpMystics || !MysticFinder.mysticFinder()) {
                    MidMoveAction.processFollowingMove();
                    if (!AutoGrinderConfig.toggleAutoheal) {
                        AutoHeal.healIfNeeded();
                    }
                }
                if (!AutoGrinderConfig.toggleAura) {
                    AutoClicker.click();
                }
            }
        }
    }

    private static enum PlayAction {
        SPAWN_MOVE,
        MID_MOVE;

    }
}

