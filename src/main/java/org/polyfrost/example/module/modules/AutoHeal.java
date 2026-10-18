/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.init.Items
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemAxe
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.ItemSword
 *  net.minecraft.network.Packet
 *  net.minecraft.network.play.client.C08PacketPlayerBlockPlacement
 */
package org.polyfrost.example.module.modules;

import java.util.function.Predicate;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;

public class AutoHeal {
    private static final Minecraft mc = Minecraft.func_71410_x();

    private static boolean swapSlot(Predicate<ItemStack> condition) {
        for (int slot = 8; slot >= 0; --slot) {
            ItemStack itemStack = AutoHeal.mc.field_71439_g.field_71071_by.func_70301_a(slot);
            if (itemStack == null || !condition.test(itemStack)) continue;
            AutoHeal.mc.field_71439_g.field_71071_by.field_70461_c = slot;
            return true;
        }
        return false;
    }

    private static boolean shouldHeal() {
        return AutoHeal.mc.field_71439_g.func_110143_aJ() < AutoHeal.mc.field_71439_g.func_110138_aP();
    }

    private static boolean isSword(ItemStack is) {
        return is != null && is.func_77973_b() instanceof ItemSword || is.func_77973_b() instanceof ItemAxe;
    }

    private static boolean isHealItem(ItemStack is) {
        if (is == null) {
            return false;
        }
        Item item = is.func_77973_b();
        return item == Items.field_151144_bL || item == Items.field_151168_bH;
    }

    public static void healIfNeeded() {
        ItemStack heldItem = AutoHeal.mc.field_71439_g.func_70694_bm();
        if (AutoHeal.shouldHeal()) {
            if (AutoHeal.isHealItem(heldItem)) {
                AutoHeal.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new C08PacketPlayerBlockPlacement(heldItem));
            } else if (!AutoHeal.swapSlot(AutoHeal::isHealItem)) {
                AutoHeal.swapSlot(AutoHeal::isSword);
            }
        } else {
            AutoHeal.swapSlot(AutoHeal::isSword);
        }
    }
}

