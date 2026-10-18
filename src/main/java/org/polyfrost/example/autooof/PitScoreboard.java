/*
 * Decompiled with CFR 0.152.
 */
package org.polyfrost.example.autooof;

import java.util.List;
import java.util.Locale;

final class PitScoreboard {
    private PitScoreboard() {
    }

    static String value(List<String> list, String string) {
        if (list == null) {
            return null;
        }
        String string2 = string.toLowerCase(Locale.ROOT) + ":";
        for (String string3 : list) {
            String string4;
            if (string3 == null || !(string4 = string3.trim()).toLowerCase(Locale.ROOT).startsWith(string2)) continue;
            return string4.substring(string2.length()).trim();
        }
        return null;
    }

    static String status(List<String> list) {
        return PitScoreboard.value(list, "Status");
    }

    static double streak(List<String> list) {
        return PitScoreboard.parseNumber(PitScoreboard.value(list, "Streak"));
    }

    static double parseNumber(String string) {
        int n;
        if (string == null) {
            return Double.NaN;
        }
        String string2 = string.replace(",", "").trim();
        for (n = 0; n < string2.length() && (Character.isDigit(string2.charAt(n)) || string2.charAt(n) == '.'); ++n) {
        }
        if (n == 0) {
            return Double.NaN;
        }
        try {
            return Double.parseDouble(string2.substring(0, n));
        }
        catch (NumberFormatException numberFormatException) {
            return Double.NaN;
        }
    }

    static boolean isUberstreak(String string) {
        return string != null && string.toLowerCase(Locale.ROOT).startsWith("uberstreak");
    }

    static boolean shouldOof(String string, double d, double d2) {
        return PitScoreboard.isUberstreak(string) && !Double.isNaN(d) && d > d2;
    }

    static String fmt(double d) {
        if (Double.isNaN(d)) {
            return "?";
        }
        return d == Math.rint(d) ? String.valueOf((long)d) : String.valueOf(d);
    }
}

