/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.settings.KeyBinding
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayer
 */
package org.polyfrost.example.module.modules;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import org.polyfrost.example.module.modules.PitMap;
import org.polyfrost.example.utils.AimUtils;

public class AutoClicker {
    private static final Minecraft mc = Minecraft.func_71410_x();

    public static void click() {
        Entity entityHit = AutoClicker.mc.field_71476_x.field_72308_g;
        if (Math.random() < 0.75) {
            if (entityHit != null) {
                AutoClicker.mc.field_71439_g.func_71038_i();
                AutoClicker.mc.field_71442_b.func_78764_a((EntityPlayer)AutoClicker.mc.field_71439_g, entityHit);
            } else {
                EntityPlayer target = AimUtils.getTarget(13.0, 3.0, (EntityPlayer)AutoClicker.mc.field_71439_g, 0.0, PitMap.MID_Y_COORDINATE, 0.0);
                if (target == null) {
                    return;
                }
                if (AutoClicker.mc.field_71439_g.func_70032_d((Entity)target) < 5.0f) {
                    AutoClicker.mc.field_71439_g.func_71038_i();
                }
            }
        }
    }

    public static void press() {
        KeyBinding.func_74510_a((int)AutoClicker.mc.field_71474_y.field_74312_F.func_151463_i(), (boolean)true);
    }
}

