/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.command.CommandBase
 *  net.minecraft.command.CommandException
 *  net.minecraft.command.ICommandSender
 *  net.minecraft.util.BlockPos
 *  net.minecraft.util.ChatComponentText
 *  net.minecraft.util.IChatComponent
 */
package org.polyfrost.example.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import org.polyfrost.example.config.AutoGrinderConfig;

public class AutoGrinderCommand
extends CommandBase {
    private static final String PREFIX = "\u00a7b[AutoGrinder]\u00a7r ";

    public String func_71517_b() {
        return "autogrinder";
    }

    public List<String> func_71514_a() {
        return Arrays.asList("ag");
    }

    public String func_71518_a(ICommandSender iCommandSender) {
        return "/autogrinder [toggle | pause [cancel] | reload | <setting> [value]]";
    }

    public void func_71515_b(ICommandSender iCommandSender, String[] stringArray) throws CommandException {
        if (stringArray.length == 0) {
            AutoGrinderCommand.say(iCommandSender, "Settings (change with /ag <setting> <value>):");
            for (AutoGrinderConfig.Setting setting : AutoGrinderConfig.SETTINGS) {
                AutoGrinderCommand.say(iCommandSender, "\u00a77" + setting.key + "\u00a7r = \u00a7e" + setting.display() + "\u00a78  (" + setting.label + ")");
            }
            AutoGrinderCommand.say(iCommandSender, "\u00a77Toggle with \u00a7f" + AutoGrinderConfig.toggleKeys + "\u00a77 or \u00a7f/ag toggle\u00a77; \u00a7f/ag pause\u00a77 stops it next time you're in spawn");
            return;
        }
        String string = stringArray[0].toLowerCase(Locale.ROOT);
        if (string.equals("toggle")) {
            AutoGrinderConfig.toggleAutoGrinder();
            return;
        }
        if (string.equals("pause")) {
            if (stringArray.length > 1 && stringArray[1].equalsIgnoreCase("cancel")) {
                boolean bl = AutoGrinderConfig.isPauseArmed();
                AutoGrinderConfig.cancelPauseAtSpawn();
                AutoGrinderCommand.say(iCommandSender, bl ? "Pause cancelled - AutoGrinder keeps running." : "No pause was set.");
            } else if (AutoGrinderConfig.isPauseArmed()) {
                AutoGrinderCommand.say(iCommandSender, "Already set to pause next time you're in spawn. \u00a77(/ag pause cancel to undo)");
            } else if (AutoGrinderConfig.armPauseAtSpawn()) {
                AutoGrinderCommand.say(iCommandSender, "Will pause next time you're in spawn. \u00a77(/ag pause cancel to undo)");
            } else {
                AutoGrinderCommand.say(iCommandSender, "AutoGrinder isn't running, so there's nothing to pause.");
            }
            return;
        }
        if (string.equals("reload")) {
            AutoGrinderConfig.load();
            AutoGrinderConfig.applyHeights();
            AutoGrinderCommand.say(iCommandSender, "Reloaded " + AutoGrinderConfig.file().getName());
            return;
        }
        AutoGrinderConfig.Setting setting = AutoGrinderConfig.find(stringArray[0]);
        if (setting == null) {
            AutoGrinderCommand.say(iCommandSender, "\u00a7cUnknown setting '" + stringArray[0] + "'. \u00a77Type /ag to list them.");
            return;
        }
        if (stringArray.length == 1) {
            AutoGrinderCommand.say(iCommandSender, setting.label + " (\u00a77" + setting.key + "\u00a7r) = \u00a7e" + setting.display() + "\u00a77  allowed: " + setting.allowed());
            return;
        }
        String string2 = String.join((CharSequence)" ", Arrays.asList(stringArray).subList(1, stringArray.length));
        String string3 = setting.set(string2);
        if (string3 != null) {
            AutoGrinderCommand.say(iCommandSender, "\u00a7c" + setting.key + " " + string3);
            return;
        }
        AutoGrinderConfig.save();
        AutoGrinderConfig.applyHeights();
        AutoGrinderCommand.say(iCommandSender, setting.label + " set to \u00a7e" + setting.display());
    }

    public List<String> func_180525_a(ICommandSender iCommandSender, String[] stringArray, BlockPos blockPos) {
        Object object;
        ArrayList<String> arrayList = new ArrayList<String>();
        if (stringArray.length == 1) {
            arrayList.add("toggle");
            arrayList.add("pause");
            arrayList.add("reload");
            for (AutoGrinderConfig.Setting object22 : AutoGrinderConfig.SETTINGS) {
                arrayList.add(object22.key);
            }
        } else if (stringArray.length == 2) {
            if (stringArray[0].equalsIgnoreCase("pause")) {
                arrayList.add("cancel");
            } else {
                object = AutoGrinderConfig.find(stringArray[0]);
                if (object != null) {
                    arrayList.addAll(((AutoGrinderConfig.Setting)object).suggestions());
                }
            }
        }
        object = stringArray.length == 0 ? "" : stringArray[stringArray.length - 1].toLowerCase(Locale.ROOT);
        ArrayList<String> arrayList2 = new ArrayList<String>();
        for (String string : arrayList) {
            if (!string.toLowerCase(Locale.ROOT).startsWith((String)object)) continue;
            arrayList2.add(string);
        }
        return arrayList2;
    }

    private static void say(ICommandSender iCommandSender, String string) {
        iCommandSender.func_145747_a((IChatComponent)new ChatComponentText(PREFIX + string));
    }

    public int func_82362_a() {
        return 0;
    }

    public boolean func_71519_b(ICommandSender iCommandSender) {
        return true;
    }
}

