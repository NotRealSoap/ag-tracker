/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.entity.player.EntityPlayer
 */
package org.polyfrost.example.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import org.polyfrost.example.config.AutoGrinderConfig;
import org.polyfrost.example.module.modules.PitMap;
import org.polyfrost.example.utils.AimUtils;

public class PitUtils {
    private static final Minecraft mc = Minecraft.func_71410_x();

    public static int getMidPlayerCount() {
        int targetCount = 0;
        for (EntityPlayer player : PitUtils.mc.field_71441_e.field_73010_i) {
            if (player == null) continue;
            double x = player.field_70165_t;
            double y = player.field_70163_u;
            double z = player.field_70161_v;
            double distFromOrigin = Math.sqrt(x * x + z * z);
            if (!(distFromOrigin <= 14.0) || !(y <= (double)(PitMap.MID_Y_COORDINATE + 5)) || AutoGrinderConfig.excludeDiamonds && AimUtils.hasDiamondChestplate(player)) continue;
            ++targetCount;
        }
        return targetCount;
    }
}

