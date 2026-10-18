/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.entity.EntityPlayerSP
 *  net.minecraft.util.ChatComponentText
 *  net.minecraft.util.IChatComponent
 */
package org.polyfrost.example.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;

public final class Chat {
    private Chat() {
    }

    public static void chat(Object object) {
        String string = String.valueOf(object);
        EntityPlayerSP entityPlayerSP = Minecraft.func_71410_x().field_71439_g;
        if (entityPlayerSP != null) {
            entityPlayerSP.func_145747_a((IChatComponent)new ChatComponentText(string));
        } else {
            System.out.println("[AutoGrinder] " + string);
        }
    }

    public static void say(String string) {
        EntityPlayerSP entityPlayerSP = Minecraft.func_71410_x().field_71439_g;
        if (entityPlayerSP != null) {
            entityPlayerSP.func_71165_d(string);
        }
    }
}

