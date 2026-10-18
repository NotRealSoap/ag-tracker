/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.settings.KeyBinding
 */
package org.polyfrost.example.module.modules;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;

public class ResetAllMovements {
    private static final Minecraft mc = Minecraft.func_71410_x();

    public static void immaResetAl() {
        KeyBinding.func_74510_a((int)ResetAllMovements.mc.field_71474_y.field_74351_w.func_151463_i(), (boolean)false);
        KeyBinding.func_74510_a((int)ResetAllMovements.mc.field_71474_y.field_74314_A.func_151463_i(), (boolean)false);
        KeyBinding.func_74510_a((int)ResetAllMovements.mc.field_71474_y.field_74370_x.func_151463_i(), (boolean)false);
        KeyBinding.func_74510_a((int)ResetAllMovements.mc.field_71474_y.field_74366_z.func_151463_i(), (boolean)false);
        KeyBinding.func_74510_a((int)ResetAllMovements.mc.field_71474_y.field_151444_V.func_151463_i(), (boolean)false);
    }
}

