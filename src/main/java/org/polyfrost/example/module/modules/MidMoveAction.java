/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.entity.EntityPlayerSP
 *  net.minecraft.client.settings.GameSettings
 *  net.minecraft.client.settings.KeyBinding
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.util.MathHelper
 */
package org.polyfrost.example.module.modules;

import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import org.polyfrost.example.config.AutoGrinderConfig;
import org.polyfrost.example.module.modules.PitMap;
import org.polyfrost.example.utils.AimUtils;
import org.polyfrost.example.utils.Chat;

public class MidMoveAction {
    private static final Minecraft mc = Minecraft.func_71410_x();
    private static final Random rand = new Random();
    private static final long IGNORE_STUCK_TARGET_MS = 5000L;
    private static EntityPlayer cachedTarget = null;
    private static int invalidTicks = 0;
    private static int stuckTicks = 0;
    private static EntityPlayer stuckTarget = null;
    private static EntityPlayer ignoredTarget = null;
    private static long ignoredUntil = 0L;

    private static boolean isStillLocked(EntityPlayer entityPlayer) {
        if (entityPlayer == null || entityPlayer.field_70128_L || entityPlayer.func_82150_aj() || entityPlayer.func_110143_aJ() <= 0.0f) {
            return false;
        }
        if (!MidMoveAction.mc.field_71441_e.field_73010_i.contains(entityPlayer)) {
            return false;
        }
        if ((double)MidMoveAction.mc.field_71439_g.func_70032_d((Entity)entityPlayer) > (double)AutoGrinderConfig.stickRange) {
            return false;
        }
        if (AimUtils.isVisible((EntityPlayer)MidMoveAction.mc.field_71439_g, entityPlayer)) {
            invalidTicks = 0;
            return true;
        }
        return ++invalidTicks < 10;
    }

    private static boolean isIgnored(EntityPlayer entityPlayer) {
        return entityPlayer != null && entityPlayer == ignoredTarget && System.currentTimeMillis() < ignoredUntil;
    }

    private static EntityPlayer pickTarget() {
        EntityPlayerSP entityPlayerSP = MidMoveAction.mc.field_71439_g;
        if (entityPlayerSP == null || MidMoveAction.mc.field_71441_e == null) {
            return null;
        }
        EntityPlayer entityPlayer = null;
        EntityPlayer entityPlayer2 = null;
        for (Object e : MidMoveAction.mc.field_71441_e.field_73010_i) {
            EntityPlayer entityPlayer3;
            if (!(e instanceof EntityPlayer) || MidMoveAction.isIgnored(entityPlayer3 = (EntityPlayer)e) || !AimUtils.isValidTarget(entityPlayer3, 13.0, 0.0, PitMap.MID_Y_COORDINATE, 0.0, (EntityPlayer)entityPlayerSP)) continue;
            if (AimUtils.isInFov(entityPlayer3)) {
                if (entityPlayer != null && !AimUtils.isBetterTarget(entityPlayer3, entityPlayer, 0.0)) continue;
                entityPlayer = entityPlayer3;
                continue;
            }
            if (entityPlayer2 != null && !AimUtils.isBetterTarget(entityPlayer3, entityPlayer2, 0.0)) continue;
            entityPlayer2 = entityPlayer3;
        }
        return entityPlayer != null ? entityPlayer : entityPlayer2;
    }

    private static void releaseMovementKeys() {
        GameSettings gameSettings = MidMoveAction.mc.field_71474_y;
        KeyBinding.func_74510_a((int)gameSettings.field_74351_w.func_151463_i(), (boolean)false);
        KeyBinding.func_74510_a((int)gameSettings.field_74370_x.func_151463_i(), (boolean)false);
        KeyBinding.func_74510_a((int)gameSettings.field_74366_z.func_151463_i(), (boolean)false);
        KeyBinding.func_74510_a((int)gameSettings.field_74314_A.func_151463_i(), (boolean)false);
        KeyBinding.func_74510_a((int)gameSettings.field_151444_V.func_151463_i(), (boolean)false);
    }

    public static void processFollowingMove() {
        EntityPlayer entityPlayer;
        if (MidMoveAction.isIgnored(cachedTarget)) {
            cachedTarget = null;
        }
        EntityPlayer entityPlayer2 = MidMoveAction.pickTarget();
        if (AutoGrinderConfig.stickToTarget) {
            if (!MidMoveAction.isStillLocked(cachedTarget)) {
                cachedTarget = entityPlayer2;
                invalidTicks = 0;
            }
        } else {
            boolean bl;
            boolean bl2 = bl = cachedTarget != null && MidMoveAction.mc.field_71441_e.field_73010_i.contains(cachedTarget) && AimUtils.isValidTarget(cachedTarget, 13.0, 0.0, PitMap.MID_Y_COORDINATE, 0.0, (EntityPlayer)MidMoveAction.mc.field_71439_g);
            if (bl) {
                invalidTicks = 0;
            } else if (cachedTarget != null && !MidMoveAction.cachedTarget.field_70128_L && MidMoveAction.mc.field_71441_e.field_73010_i.contains(cachedTarget) && ++invalidTicks < 10) {
                bl = true;
            }
            if (!bl) {
                cachedTarget = entityPlayer2;
                invalidTicks = 0;
            } else if (entityPlayer2 != null && entityPlayer2 != cachedTarget && (AimUtils.isBetterTarget(entityPlayer2, cachedTarget, 1.0) || AimUtils.isInFov(entityPlayer2) && !AimUtils.isInFov(cachedTarget))) {
                cachedTarget = entityPlayer2;
            }
        }
        if ((entityPlayer = cachedTarget) == null) {
            stuckTicks = 0;
            stuckTarget = null;
            MidMoveAction.releaseMovementKeys();
            return;
        }
        if (entityPlayer != stuckTarget) {
            stuckTarget = entityPlayer;
            stuckTicks = 0;
        }
        if (MidMoveAction.mc.field_71439_g.field_70123_F) {
            int n;
            ++stuckTicks;
            if ((float)n >= AutoGrinderConfig.stuckDetectionTime * 20.0f) {
                Chat.chat("\u00a77[AutoGrinder] Blocked by a wall for " + MidMoveAction.trim(AutoGrinderConfig.stuckDetectionTime) + "s chasing " + entityPlayer.func_70005_c_() + " - trying another target");
                ignoredTarget = entityPlayer;
                ignoredUntil = System.currentTimeMillis() + 5000L;
                cachedTarget = null;
                stuckTicks = 0;
                stuckTarget = null;
                MidMoveAction.releaseMovementKeys();
                return;
            }
        } else {
            stuckTicks = 0;
        }
        AimUtils.aimTo((Entity)entityPlayer, new double[]{10.0f - AutoGrinderConfig.horizontalAimSpeed, 10.0f - AutoGrinderConfig.verticalAimSpeed});
        float f = AimUtils.getTargetRotations(entityPlayer.field_70165_t, entityPlayer.field_70163_u, entityPlayer.field_70161_v)[0];
        float f2 = MathHelper.func_76142_g((float)(f - MidMoveAction.mc.field_71439_g.field_70177_z));
        double d = MidMoveAction.mc.field_71439_g.field_70161_v - entityPlayer.field_70161_v;
        double d2 = MidMoveAction.mc.field_71439_g.field_70165_t - entityPlayer.field_70165_t;
        double d3 = d2 * d2 + d * d;
        KeyBinding.func_74510_a((int)MidMoveAction.mc.field_71474_y.field_74370_x.func_151463_i(), (boolean)false);
        KeyBinding.func_74510_a((int)MidMoveAction.mc.field_71474_y.field_74366_z.func_151463_i(), (boolean)false);
        KeyBinding.func_74510_a((int)MidMoveAction.mc.field_71474_y.field_74314_A.func_151463_i(), (boolean)false);
        KeyBinding.func_74510_a((int)MidMoveAction.mc.field_71474_y.field_74351_w.func_151463_i(), (boolean)true);
        if (f2 < -15.0f) {
            KeyBinding.func_74510_a((int)MidMoveAction.mc.field_71474_y.field_74370_x.func_151463_i(), (boolean)true);
        } else if (f2 > 15.0f) {
            KeyBinding.func_74510_a((int)MidMoveAction.mc.field_71474_y.field_74366_z.func_151463_i(), (boolean)true);
        }
        if (rand.nextInt(100) < AutoGrinderConfig.jumpChance) {
            KeyBinding.func_74510_a((int)MidMoveAction.mc.field_71474_y.field_74314_A.func_151463_i(), (boolean)true);
        } else {
            KeyBinding.func_74510_a((int)MidMoveAction.mc.field_71474_y.field_74314_A.func_151463_i(), (boolean)false);
        }
        KeyBinding.func_74510_a((int)MidMoveAction.mc.field_71474_y.field_151444_V.func_151463_i(), (boolean)true);
        if (d3 <= 1.0) {
            KeyBinding.func_74510_a((int)MidMoveAction.mc.field_71474_y.field_74351_w.func_151463_i(), (boolean)false);
            KeyBinding.func_74510_a((int)MidMoveAction.mc.field_71474_y.field_151444_V.func_151463_i(), (boolean)false);
        }
    }

    private static String trim(float f) {
        return (double)f == Math.rint(f) ? String.valueOf((int)f) : String.valueOf(f);
    }
}

