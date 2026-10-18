/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.command.CommandBase
 *  net.minecraft.command.CommandException
 *  net.minecraft.command.ICommandSender
 *  net.minecraft.util.ChatComponentText
 *  net.minecraft.util.IChatComponent
 */
package org.polyfrost.example.autooof;

import java.util.Locale;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import org.polyfrost.example.autooof.AutoOof;
import org.polyfrost.example.autooof.PitScoreboard;

public class AutoOofCommand
extends CommandBase {
    private static final String PREFIX = "\u00a76[AutoOof]\u00a7r ";

    public String func_71517_b() {
        return "autooof";
    }

    public String func_71518_a(ICommandSender iCommandSender) {
        return "/autooof [on|off|status|<streak>]";
    }

    public void func_71515_b(ICommandSender iCommandSender, String[] stringArray) throws CommandException {
        String string;
        String string2 = string = stringArray.length > 0 ? stringArray[0].toLowerCase(Locale.ROOT) : "";
        if (string.isEmpty()) {
            AutoOof.setEnabled(!AutoOof.isEnabled());
            AutoOofCommand.say(iCommandSender, AutoOofCommand.describe());
        } else if (string.equals("on")) {
            AutoOof.setEnabled(true);
            AutoOofCommand.say(iCommandSender, AutoOofCommand.describe());
        } else if (string.equals("off")) {
            AutoOof.setEnabled(false);
            AutoOofCommand.say(iCommandSender, AutoOofCommand.describe());
        } else if (string.equals("status")) {
            AutoOofCommand.say(iCommandSender, AutoOofCommand.describe());
            AutoOofCommand.say(iCommandSender, "\u00a77Scoreboard now: " + AutoOof.readNow());
        } else {
            double d = PitScoreboard.parseNumber(string);
            if (Double.isNaN(d) || !string.matches("[0-9.,]+")) {
                AutoOofCommand.say(iCommandSender, "\u00a7cUsage: " + this.func_71518_a(iCommandSender));
                return;
            }
            AutoOof.setThreshold(d);
            AutoOofCommand.say(iCommandSender, "Threshold set to streak > " + PitScoreboard.fmt(d) + ". " + AutoOofCommand.describe());
        }
    }

    private static String describe() {
        return (AutoOof.isEnabled() ? "\u00a7aON" : "\u00a7cOFF") + "\u00a7r - /oof when Status is Uberstreak and Streak > " + PitScoreboard.fmt(AutoOof.getThreshold());
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

