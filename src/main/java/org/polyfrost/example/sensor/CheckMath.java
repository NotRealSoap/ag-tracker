/*
 * Decompiled with CFR 0.152.
 */
package org.polyfrost.example.sensor;

import java.util.regex.Pattern;

final class CheckMath {
    private static final Pattern FORMATTING = Pattern.compile("(?i)\u00a7[0-9A-FK-OR]");

    private CheckMath() {
    }

    static float wrapDegrees(float f) {
        if ((f %= 360.0f) >= 180.0f) {
            f -= 360.0f;
        }
        if (f < -180.0f) {
            f += 360.0f;
        }
        return f;
    }

    static float[] rotationDelta(boolean bl, boolean bl2, float f, float f2, float f3, float f4) {
        float f5 = bl ? f : CheckMath.wrapDegrees(f - f3);
        float f6 = bl2 ? f2 : f2 - f4;
        return new float[]{f5, f6};
    }

    static double[] positionDelta(boolean bl, boolean bl2, boolean bl3, double d, double d2, double d3, double d4, double d5, double d6) {
        return new double[]{bl ? d : d - d4, bl2 ? d2 : d2 - d5, bl3 ? d3 : d3 - d6};
    }

    static float totalRotation(float[] fArray) {
        return Math.abs(fArray[0]) + Math.abs(fArray[1]);
    }

    static double length(double[] dArray) {
        return Math.sqrt(dArray[0] * dArray[0] + dArray[1] * dArray[1] + dArray[2] * dArray[2]);
    }

    static boolean recent(long l, long l2, long l3) {
        return l2 > 0L && l >= l2 && l - l2 <= l3;
    }

    static boolean isPitSpawn(double d, double d2, double d3, int n) {
        if (n <= 0) {
            return false;
        }
        return d2 >= (double)(n - 3) && d * d + d3 * d3 <= 900.0;
    }

    static String stripFormatting(String string) {
        return string == null ? "" : FORMATTING.matcher(string).replaceAll("");
    }

    static int parseClock(String string) {
        int n;
        if (string == null) {
            return -1;
        }
        string = string.trim();
        for (n = 0; n < string.length() && (Character.isDigit(string.charAt(n)) || string.charAt(n) == ':'); ++n) {
        }
        if ((string = string.substring(0, n)).isEmpty()) {
            return -1;
        }
        try {
            String[] stringArray = string.split(":");
            if (stringArray.length == 2) {
                return Integer.parseInt(stringArray[0]) * 60 + Integer.parseInt(stringArray[1]);
            }
            if (stringArray.length == 1) {
                return Integer.parseInt(stringArray[0]);
            }
        }
        catch (NumberFormatException numberFormatException) {
            // empty catch block
        }
        return -1;
    }

    static String eventType(String string) {
        String string2 = string;
        for (String string3 : new String[]{"Starting in ", "Ending in ", "Go to "}) {
            int n = string2.indexOf(string3);
            if (n < 0) continue;
            string2 = string2.substring(0, n);
        }
        return string2.replace("!", "").trim();
    }
}

