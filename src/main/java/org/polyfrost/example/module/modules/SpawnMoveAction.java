/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.settings.KeyBinding
 *  net.minecraft.util.BlockPos
 */
package org.polyfrost.example.module.modules;

import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.BlockPos;
import org.polyfrost.example.config.AutoGrinderConfig;
import org.polyfrost.example.module.modules.PitMap;
import org.polyfrost.example.utils.AimUtils;
import org.polyfrost.example.utils.Timer;

public class SpawnMoveAction {
    private static final Minecraft mc = Minecraft.func_71410_x();
    private static final Random rand = new Random();
    private static final Timer justTimer = new Timer();
    public static int midDropAimingY;
    public static int midDropAimingX;
    public static int midDropAimingZ;
    public static int spawnJumpChance;

    public static void initialize() {
        spawnJumpChance = 1;
        midDropAimingY = rand.nextInt(6);
        midDropAimingX = rand.nextInt(5) - 2;
        midDropAimingZ = rand.nextInt(5) - 2;
        midDropAimingY += PitMap.SPAWN_Y_COORDINATE - 8;
        KeyBinding.func_74510_a((int)SpawnMoveAction.mc.field_71474_y.field_74351_w.func_151463_i(), (boolean)false);
        KeyBinding.func_74510_a((int)SpawnMoveAction.mc.field_71474_y.field_74314_A.func_151463_i(), (boolean)false);
        KeyBinding.func_74510_a((int)SpawnMoveAction.mc.field_71474_y.field_74370_x.func_151463_i(), (boolean)false);
        KeyBinding.func_74510_a((int)SpawnMoveAction.mc.field_71474_y.field_74366_z.func_151463_i(), (boolean)false);
        KeyBinding.func_74510_a((int)SpawnMoveAction.mc.field_71474_y.field_151444_V.func_151463_i(), (boolean)false);
        justTimer.reset();
    }

    public static void moveFromSpawn() {
        double d = SpawnMoveAction.mc.field_71439_g.field_70165_t;
        double d2 = SpawnMoveAction.mc.field_71439_g.field_70161_v;
        AimUtils.aimTo(new BlockPos(midDropAimingX, midDropAimingY, midDropAimingZ), new double[]{6.3, 9.0});
        switch (spawnJumpChance) {
            case 0: {
                KeyBinding.func_74510_a((int)SpawnMoveAction.mc.field_71474_y.field_74351_w.func_151463_i(), (boolean)true);
                SpawnMoveAction.mc.field_71439_g.func_70031_b(true);
                break;
            }
            case 1: 
            case 2: {
                KeyBinding.func_74510_a((int)SpawnMoveAction.mc.field_71474_y.field_74351_w.func_151463_i(), (boolean)true);
                SpawnMoveAction.mc.field_71439_g.func_70031_b(true);
                if (!justTimer.passed(AutoGrinderConfig.spawnCooldown * 1000.0f + 250.0f)) break;
                KeyBinding.func_74510_a((int)SpawnMoveAction.mc.field_71474_y.field_74314_A.func_151463_i(), (boolean)true);
                justTimer.reset();
                break;
            }
            case 3: {
                KeyBinding.func_74510_a((int)SpawnMoveAction.mc.field_71474_y.field_74351_w.func_151463_i(), (boolean)true);
                if (Math.sqrt(d * d + d2 * d2) <= 7.0) {
                    KeyBinding.func_74510_a((int)SpawnMoveAction.mc.field_71474_y.field_74314_A.func_151463_i(), (boolean)true);
                }
                SpawnMoveAction.mc.field_71439_g.func_70031_b(true);
            }
        }
    }
}

