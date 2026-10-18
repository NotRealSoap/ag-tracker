package org.polyfrost.example.config;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import org.polyfrost.example.AutoGrinderMod;
import org.polyfrost.example.module.modules.StatTracker;
import org.polyfrost.example.ui.StatScoreboard;
import org.polyfrost.example.utils.Chat;

import java.util.ArrayList;
import java.util.List;

public class TrackerCommand extends CommandBase {
    private StatTracker tracker;
    private StatScoreboard scoreboard;

    public TrackerCommand(StatTracker tracker, StatScoreboard scoreboard) {
        this.tracker = tracker;
        this.scoreboard = scoreboard;
    }

    @Override
    public String func_71517_b() {
        return "tracker";
    }

    @Override
    public String func_71518_a(ICommandSender sender) {
        return "/tracker <toggle|stats|show|hide|help>";
    }

    @Override
    public void func_71515_b(ICommandSender sender, String[] args) {
        if (args.length == 0) {
            tracker.setEnabled(!tracker.isEnabled());
            Chat.chat("§6[Tracker] §r" + (tracker.isEnabled() ? "§aEnabled" : "§cDisabled"));
            return;
        }

        String subcommand = args[0].toLowerCase();

        switch (subcommand) {
            case "toggle":
                tracker.setEnabled(!tracker.isEnabled());
                Chat.chat("§6[Tracker] §r" + (tracker.isEnabled() ? "§aEnabled" : "§cDisabled"));
                break;

            case "stats":
                if (tracker.isEnabled()) {
                    Chat.chat("§6═══════════════════════════════");
                    Chat.chat("§6 Session Statistics");
                    Chat.chat("§6═══════════════════════════════");
                    Chat.chat("§cDeaths§r: " + tracker.getDeaths());
                    Chat.chat("§aUberstreaks§r: " + tracker.getUberstreaks());
                    Chat.chat("§5Mystic Drops§r: " + tracker.getMysticDrops());
                    Chat.chat("§eSession Duration§r: " + tracker.getSessionTimeFormatted());
                    Chat.chat("§dAvg Uberstreak Time§r: " + tracker.getAverageUberstreakTimeFormatted());
                    Chat.chat("§6═══════════════════════════════");
                } else {
                    Chat.chat("§cTracker is disabled!");
                }
                break;

            case "show":
                scoreboard.setVisible(true);
                Chat.chat("§6[Tracker] Scoreboard shown");
                break;

            case "hide":
                scoreboard.setVisible(false);
                Chat.chat("§6[Tracker] Scoreboard hidden");
                break;

            case "help":
                Chat.chat("§6[Tracker Commands]");
                Chat.chat("§6/tracker§r - Toggle tracker on/off");
                Chat.chat("§6/tracker toggle§r - Toggle tracker on/off");
                Chat.chat("§6/tracker stats§r - Show detailed stats in chat");
                Chat.chat("§6/tracker show§r - Show the scoreboard overlay");
                Chat.chat("§6/tracker hide§r - Hide the scoreboard overlay");
                Chat.chat("§6/tracker help§r - Show this help message");
                break;

            default:
                Chat.chat("§cUnknown command! Use /tracker help");
        }
    }

    @Override
    public int func_82362_a() {
        return 0;
    }

    @Override
    public boolean func_71519_b(ICommandSender sender) {
        return true;
    }

    @Override
    public List func_71514_a() {
        List<String> list = new ArrayList();
        list.add("toggle");
        list.add("stats");
        list.add("show");
        list.add("hide");
        list.add("help");
        return list;
    }
}
