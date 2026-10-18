/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.entity.EntityPlayerSP
 *  net.minecraft.util.MathHelper
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 *  net.minecraftforge.fml.common.gameevent.TickEvent$Phase
 *  net.minecraftforge.fml.common.gameevent.TickEvent$RenderTickEvent
 */
package org.polyfrost.example.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.MathHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.polyfrost.example.utils.AimUtils;

public class AimSmoother {
    private static final Minecraft mc = Minecraft.func_71410_x();
    public static final float DEADZONE = 2.0f;
    private static final long GOAL_TIMEOUT_NANOS = 150000000L;
    private long lastFrameNanos = 0L;

    @SubscribeEvent
    public void onRenderTick(TickEvent.RenderTickEvent renderTickEvent) {
        if (renderTickEvent.phase != TickEvent.Phase.START) {
            return;
        }
        long l = System.nanoTime();
        long l2 = this.lastFrameNanos;
        this.lastFrameNanos = l;
        EntityPlayerSP entityPlayerSP = AimSmoother.mc.field_71439_g;
        if (entityPlayerSP == null || AimUtils.goalTimeNanos == 0L || l - AimUtils.goalTimeNanos > 150000000L || l2 == 0L) {
            return;
        }
        double d = Math.min((double)(l - l2) / 5.0E7, 2.0);
        float f = MathHelper.func_76142_g((float)(AimUtils.goalYaw - entityPlayerSP.field_70177_z));
        float f2 = AimUtils.goalPitch - entityPlayerSP.field_70125_A;
        float f3 = Math.abs(f) < 2.0f ? 0.0f : (float)((double)f * AimSmoother.frameFraction(AimUtils.goalYawSpeed, f, d));
        float f4 = Math.abs(f2) < 2.0f ? 0.0f : (float)((double)f2 * AimSmoother.frameFraction(AimUtils.goalPitchSpeed, f2, d));
        float f5 = AimSmoother.mc.field_71474_y.field_74341_c * 0.6f + 0.2f;
        float f6 = f5 * f5 * f5 * 1.2f;
        f3 -= f3 % f6;
        f4 -= f4 % f6;
        float f7 = AimUtils.clamp(entityPlayerSP.field_70125_A + f4, -65.0f, 65.0f);
        f4 = f7 - entityPlayerSP.field_70125_A;
        if (f3 == 0.0f && f4 == 0.0f) {
            return;
        }
        entityPlayerSP.field_70177_z += f3;
        entityPlayerSP.field_70126_B += f3;
        entityPlayerSP.field_70125_A += f4;
        entityPlayerSP.field_70127_C += f4;
    }

    private static double frameFraction(double d, float f, double d2) {
        double d3 = (1.0 - d) * (1.0 + 0.1 * (double)Math.abs(f) / 180.0);
        d3 = Math.max(0.0, Math.min(1.0, d3));
        return 1.0 - Math.pow(1.0 - d3, d2);
    }
}

