/*
 * Decompiled with CFR 0.152.
 */
package org.polyfrost.example.module.modules;

import org.polyfrost.example.module.modules.PitMapType;
import org.polyfrost.example.utils.Chat;

public class PitMap {
    public static int MID_Y_COORDINATE;
    public static int SPAWN_Y_COORDINATE;

    public static void updateMapType(PitMapType type) {
        if (type == null) {
            return;
        }
        MID_Y_COORDINATE = type.getFloorY();
        SPAWN_Y_COORDINATE = type.getSpawnY();
        Chat.chat("Map type updated to: " + type.getName());
    }
}

