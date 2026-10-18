/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.multiplayer.WorldClient
 *  net.minecraft.scoreboard.ScoreObjective
 *  net.minecraft.scoreboard.ScorePlayerTeam
 *  net.minecraft.scoreboard.Scoreboard
 *  net.minecraft.scoreboard.Team
 */
package org.polyfrost.example.utils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;

public class ScoreboardUtils {
    public static List<String> getScoreboardLines() {
        WorldClient world = Minecraft.func_71410_x().field_71441_e;
        if (world == null) {
            return Collections.emptyList();
        }
        Scoreboard scoreboard = world.func_96441_U();
        if (scoreboard == null) {
            return Collections.emptyList();
        }
        ScoreObjective obj = scoreboard.func_96539_a(1);
        if (obj == null) {
            return Collections.emptyList();
        }
        List lines = scoreboard.func_96534_i(obj).stream().map(it -> ScorePlayerTeam.func_96667_a((Team)scoreboard.func_96509_i(it.func_96653_e()), (String)it.func_96653_e())).map(it -> it.replaceAll("[^\\x00-\\x7F|\u00a7]", "").replaceAll("\u00a7[0123456789abcdefklmnor]", "")).collect(Collectors.toList());
        Collections.reverse(lines);
        return lines.subList(0, Math.min(lines.size(), 15));
    }

    public static String getScoreboardDisplayName() {
        WorldClient world = Minecraft.func_71410_x().field_71441_e;
        if (world == null) {
            return "";
        }
        Scoreboard scoreboard = world.func_96441_U();
        if (scoreboard == null) {
            return "";
        }
        ScoreObjective obj = scoreboard.func_96539_a(1);
        if (obj == null) {
            return "";
        }
        return obj.func_96678_d().replaceAll("[^\\x00-\\x7F|\u00a7]", "").replaceAll("\u00a7[0123456789abcdefklmnor]", "");
    }
}

