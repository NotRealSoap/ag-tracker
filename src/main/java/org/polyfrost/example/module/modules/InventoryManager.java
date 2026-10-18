/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.client.gui.inventory.GuiInventory
 *  net.minecraft.client.settings.KeyBinding
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.ItemStack
 */
package org.polyfrost.example.module.modules;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class InventoryManager {
    private static final Minecraft mc = Minecraft.func_71410_x();
    private static boolean moving = false;
    private static int slotToMove = 1;
    private static int delayTicks = 0;
    private static final int delayBetweenClicks = 5;

    public static boolean isHotbarFull() {
        for (int i = 0; i < 9; ++i) {
            ItemStack stack = InventoryManager.mc.field_71439_g.field_71071_by.func_70301_a(i);
            if (stack != null) continue;
            return false;
        }
        return true;
    }

    public static void openInventoryIfNeeded() {
        if (!(InventoryManager.mc.field_71462_r instanceof GuiInventory)) {
            mc.func_147108_a((GuiScreen)new GuiInventory((EntityPlayer)InventoryManager.mc.field_71439_g));
        }
    }

    public static void resetState() {
        moving = false;
        slotToMove = 1;
        delayTicks = 0;
    }

    public static void startSwapToInventory() {
        if (InventoryManager.isInventoryFull()) {
            return;
        }
        if (InventoryManager.isHotbarFull()) {
            InventoryManager.openInventoryIfNeeded();
            moving = true;
            slotToMove = 1;
            delayTicks = 0;
        }
    }

    public static void inventoryManage() {
        if (!moving) {
            return;
        }
        if (!(InventoryManager.mc.field_71462_r instanceof GuiInventory)) {
            InventoryManager.resetState();
            return;
        }
        InventoryManager.mc.field_71439_g.func_70637_d(false);
        KeyBinding.func_74510_a((int)InventoryManager.mc.field_71474_y.field_74351_w.func_151463_i(), (boolean)false);
        KeyBinding.func_74510_a((int)InventoryManager.mc.field_71474_y.field_74368_y.func_151463_i(), (boolean)false);
        KeyBinding.func_74510_a((int)InventoryManager.mc.field_71474_y.field_74370_x.func_151463_i(), (boolean)false);
        KeyBinding.func_74510_a((int)InventoryManager.mc.field_71474_y.field_74366_z.func_151463_i(), (boolean)false);
        KeyBinding.func_74510_a((int)InventoryManager.mc.field_71474_y.field_74314_A.func_151463_i(), (boolean)false);
        if (delayTicks > 0) {
            --delayTicks;
            return;
        }
        if (slotToMove >= 9) {
            InventoryManager.resetState();
            InventoryManager.mc.field_71439_g.func_71053_j();
            return;
        }
        ItemStack stack = InventoryManager.mc.field_71439_g.field_71071_by.func_70301_a(slotToMove);
        if (stack != null) {
            int guiSlot = 36 + slotToMove;
            InventoryManager.mc.field_71442_b.func_78753_a(0, guiSlot, 0, 1, (EntityPlayer)InventoryManager.mc.field_71439_g);
        }
        ++slotToMove;
        delayTicks = 5;
    }

    public static boolean isMoving() {
        return moving;
    }

    public static boolean isInventoryFull() {
        for (int i = 9; i < 36; ++i) {
            ItemStack stack = InventoryManager.mc.field_71439_g.field_71071_by.func_70301_a(i);
            if (stack != null) continue;
            return false;
        }
        return true;
    }
}

