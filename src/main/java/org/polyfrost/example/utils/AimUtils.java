/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.entity.EntityPlayerSP
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.ItemArmor
 *  net.minecraft.item.ItemArmor$ArmorMaterial
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.BlockPos
 *  net.minecraft.util.MathHelper
 *  net.minecraft.util.MovingObjectPosition
 *  net.minecraft.util.MovingObjectPosition$MovingObjectType
 *  net.minecraft.util.Vec3
 */
package org.polyfrost.example.utils;

import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import org.polyfrost.example.config.AutoGrinderConfig;
import org.polyfrost.example.module.modules.PitMap;

public class AimUtils {
    private static final Minecraft mc = Minecraft.func_71410_x();
    public static final double MAX_HEIGHT_ABOVE = 1.5;
    public static final double ALWAYS_TARGET_DISTANCE = 4.5;
    public static final double HEALTH_PRIORITY_RANGE = 5.0;
    public static float goalYaw;
    public static float goalPitch;
    public static double goalYawSpeed;
    public static double goalPitchSpeed;
    public static long goalTimeNanos;

    public static boolean isValidTarget(EntityPlayer entityPlayer, double d, double d2, double d3, double d4, EntityPlayer entityPlayer2) {
        boolean bl;
        EntityPlayerSP entityPlayerSP = AimUtils.mc.field_71439_g;
        if (entityPlayer == null || entityPlayerSP == null || entityPlayer == entityPlayer2 || entityPlayer == entityPlayerSP) {
            return false;
        }
        if (entityPlayer.field_70128_L || entityPlayer.func_82150_aj()) {
            return false;
        }
        if (AutoGrinderConfig.excludeDiamonds && AimUtils.hasDiamondChestplate(entityPlayer)) {
            return false;
        }
        double d5 = entityPlayer.field_70163_u;
        if (d5 > entityPlayerSP.field_70163_u + 1.5 || d5 < entityPlayerSP.field_70163_u - 14.0) {
            return false;
        }
        if (d5 > (double)PitMap.SPAWN_Y_COORDINATE || d5 >= (double)(PitMap.MID_Y_COORDINATE + 5)) {
            return false;
        }
        boolean bl2 = entityPlayer.func_180425_c().func_177954_c(d2, d3, d4) <= d * d;
        boolean bl3 = bl = (double)entityPlayerSP.func_70032_d((Entity)entityPlayer) <= 4.5;
        if (!bl2 && !bl) {
            return false;
        }
        return AimUtils.isVisible((EntityPlayer)entityPlayerSP, entityPlayer);
    }

    public static boolean useHealthPriority() {
        return AutoGrinderConfig.targetPriority == 0;
    }

    public static boolean isBetterTarget(EntityPlayer entityPlayer, EntityPlayer entityPlayer2, double d) {
        EntityPlayerSP entityPlayerSP = AimUtils.mc.field_71439_g;
        double d2 = entityPlayerSP.func_70032_d((Entity)entityPlayer);
        double d3 = entityPlayerSP.func_70032_d((Entity)entityPlayer2);
        if (AimUtils.useHealthPriority()) {
            boolean bl;
            boolean bl2 = d2 <= 5.0;
            boolean bl3 = bl = d3 <= 5.0;
            if (bl2 != bl) {
                return bl2;
            }
            if (bl2) {
                float f;
                float f2 = entityPlayer.func_110143_aJ();
                if ((double)Math.abs(f2 - (f = entityPlayer2.func_110143_aJ())) > d) {
                    return f2 < f;
                }
                if (d > 0.0) {
                    return false;
                }
            }
        }
        return d2 + d < d3;
    }

    public static boolean isInFov(EntityPlayer entityPlayer) {
        int n = AutoGrinderConfig.fov;
        if (n >= 360) {
            return true;
        }
        float f = AimUtils.getTargetRotations(entityPlayer.field_70165_t + 0.5, entityPlayer.field_70163_u, entityPlayer.field_70161_v + 0.5)[0];
        float f2 = Math.abs(MathHelper.func_76142_g((float)(f - AimUtils.mc.field_71439_g.field_70177_z)));
        return f2 <= (float)n / 2.0f;
    }

    public static EntityPlayer getTarget(double d, double d2, EntityPlayer entityPlayer, double d3, double d4, double d5) {
        EntityPlayerSP entityPlayerSP = AimUtils.mc.field_71439_g;
        if (entityPlayerSP == null || AimUtils.mc.field_71441_e == null) {
            return null;
        }
        List list = AimUtils.mc.field_71441_e.field_73010_i;
        EntityPlayer entityPlayer2 = null;
        EntityPlayer entityPlayer3 = null;
        for (Object e : list) {
            EntityPlayer entityPlayer4;
            if (!(e instanceof EntityPlayer) || !AimUtils.isValidTarget(entityPlayer4 = (EntityPlayer)e, d, d3, d4, d5, entityPlayer)) continue;
            if (AimUtils.isInFov(entityPlayer4)) {
                if (entityPlayer2 != null && !AimUtils.isBetterTarget(entityPlayer4, entityPlayer2, 0.0)) continue;
                entityPlayer2 = entityPlayer4;
                continue;
            }
            if (entityPlayer3 != null && !AimUtils.isBetterTarget(entityPlayer4, entityPlayer3, 0.0)) continue;
            entityPlayer3 = entityPlayer4;
        }
        return entityPlayer2 != null ? entityPlayer2 : entityPlayer3;
    }

    public static boolean hasDiamondChestplate(EntityPlayer entityPlayer) {
        for (ItemStack itemStack : entityPlayer.field_71071_by.field_70460_b) {
            ItemArmor itemArmor;
            if (itemStack == null || !(itemStack.func_77973_b() instanceof ItemArmor) || (itemArmor = (ItemArmor)itemStack.func_77973_b()).func_82812_d() != ItemArmor.ArmorMaterial.DIAMOND || itemArmor.field_77881_a != 1) continue;
            return true;
        }
        return false;
    }

    public static void aimTo(BlockPos blockPos, double[] dArray) {
        AimUtils.aimTo((double)blockPos.func_177958_n() + 0.5, (double)blockPos.func_177956_o() + 0.5, (double)blockPos.func_177952_p() + 0.5, dArray);
    }

    public static void aimTo(Entity entity, double[] dArray) {
        double d;
        if (entity instanceof EntityLivingBase) {
            EntityLivingBase entityLivingBase = (EntityLivingBase)entity;
            d = entityLivingBase.field_70163_u + (double)entityLivingBase.func_70047_e() * 0.9;
        } else {
            d = (entity.func_174813_aQ().field_72338_b + entity.func_174813_aQ().field_72337_e) / 2.0;
        }
        AimUtils.aimTo(entity.field_70165_t + 0.5, d, entity.field_70161_v + 0.5, dArray);
    }

    public static void aimTo(double d, double d2, double d3, double[] dArray) {
        float[] fArray = AimUtils.getTargetRotations(d, d2, d3);
        goalYaw = fArray[0];
        goalPitch = AimUtils.clamp(fArray[1], -65.0f, 65.0f);
        goalYawSpeed = dArray[0] / 10.0;
        goalPitchSpeed = dArray[1] / 10.0;
        goalTimeNanos = System.nanoTime();
    }

    public static void clearGoal() {
        goalTimeNanos = 0L;
    }

    public static float clamp(float f, float f2, float f3) {
        return Math.max(f2, Math.min(f3, f));
    }

    public static float[] getTargetRotations(double d, double d2, double d3) {
        double d4 = d - (AimUtils.mc.field_71439_g.field_70165_t + 0.5);
        double d5 = d2 - (AimUtils.mc.field_71439_g.field_70163_u + (double)AimUtils.mc.field_71439_g.func_70047_e());
        double d6 = d3 - (AimUtils.mc.field_71439_g.field_70161_v + 0.5);
        double d7 = MathHelper.func_76133_a((double)(d4 * d4 + d6 * d6));
        float f = (float)(Math.atan2(d6, d4) * 180.0 / Math.PI) - 90.0f;
        float f2 = (float)(-(Math.atan2(d5, d7) * 180.0 / Math.PI));
        return new float[]{f, f2 + 11.0f};
    }

    public static float[] smoothRotations(float[] fArray, double d, double d2) {
        float f = AimUtils.angledifference(fArray[0], AimUtils.mc.field_71439_g.field_70177_z);
        float f2 = AimUtils.angledifference(fArray[1], AimUtils.mc.field_71439_g.field_70125_A);
        double d3 = (1.0 - d) * (1.0 + 0.1 * (double)Math.abs(f) / 180.0);
        double d4 = (1.0 - d2) * (1.0 + 0.1 * (double)Math.abs(f2) / 180.0);
        return new float[]{(float)((double)AimUtils.mc.field_71439_g.field_70177_z + (double)MathHelper.func_76142_g((float)(fArray[0] - AimUtils.mc.field_71439_g.field_70177_z)) * d3), (float)((double)AimUtils.mc.field_71439_g.field_70125_A + (double)MathHelper.func_76142_g((float)(fArray[1] - AimUtils.mc.field_71439_g.field_70125_A)) * d4)};
    }

    public static float angledifference(float f, float f2) {
        return MathHelper.func_76142_g((float)(f - f2));
    }

    public static float[] getFixedRotation(float[] fArray, float[] fArray2) {
        Minecraft minecraft = Minecraft.func_71410_x();
        float f = fArray[0];
        float f2 = fArray[1];
        float f3 = fArray2[0];
        float f4 = fArray2[1];
        float f5 = minecraft.field_71474_y.field_74341_c * 0.6f + 0.2f;
        float f6 = f5 * f5 * f5 * 1.2f;
        float f7 = f - f3;
        float f8 = f2 - f4;
        float f9 = f7 - f7 % f6;
        float f10 = f8 - f8 % f6;
        return new float[]{f3 + f9, f4 + f10};
    }

    public static boolean isVisible(EntityPlayer entityPlayer, EntityPlayer entityPlayer2) {
        Vec3 vec3;
        Vec3 vec32 = new Vec3(entityPlayer.field_70165_t, entityPlayer.field_70163_u + (double)entityPlayer.func_70047_e(), entityPlayer.field_70161_v);
        MovingObjectPosition movingObjectPosition = entityPlayer.field_70170_p.func_147447_a(vec32, vec3 = new Vec3(entityPlayer2.field_70165_t, entityPlayer2.field_70163_u + (double)entityPlayer2.func_70047_e(), entityPlayer2.field_70161_v), false, true, false);
        return movingObjectPosition == null || movingObjectPosition.field_72313_a == MovingObjectPosition.MovingObjectType.MISS;
    }

    static {
        goalTimeNanos = 0L;
    }
}

