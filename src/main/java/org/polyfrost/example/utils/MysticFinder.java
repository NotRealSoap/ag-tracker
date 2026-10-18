/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.settings.KeyBinding
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.item.EntityItem
 *  net.minecraft.item.ItemStack
 */
package org.polyfrost.example.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import org.polyfrost.example.utils.AimUtils;

public class MysticFinder {
    private static final Minecraft mc = Minecraft.func_71410_x();

    public static boolean mysticFinder() {
        Minecraft mc = Minecraft.func_71410_x();
        double maxRange = 14.0;
        for (Entity entity : mc.field_71441_e.field_72996_f) {
            double dz;
            double dx;
            double distanceXZ;
            EntityItem entityItem;
            ItemStack itemStack;
            String displayName;
            if (!(entity instanceof EntityItem) || !(displayName = (itemStack = (entityItem = (EntityItem)entity).func_92059_d()).func_82833_r()).contains("Fresh") && !displayName.contains("Mystic") || !((distanceXZ = Math.sqrt((dx = entityItem.field_70165_t - 0.0) * dx + (dz = entityItem.field_70161_v - 0.0) * dz)) <= maxRange)) continue;
            double[] smoothing = new double[]{5.0, 5.0};
            AimUtils.aimTo((Entity)entityItem, smoothing);
            KeyBinding.func_74510_a((int)mc.field_71474_y.field_74351_w.func_151463_i(), (boolean)true);
            KeyBinding.func_74510_a((int)mc.field_71474_y.field_151444_V.func_151463_i(), (boolean)true);
            KeyBinding.func_74510_a((int)mc.field_71474_y.field_74314_A.func_151463_i(), (boolean)false);
            return true;
        }
        return false;
    }
}

