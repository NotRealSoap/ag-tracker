/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonSyntaxException
 *  net.minecraftforge.client.event.ClientChatReceivedEvent
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package org.polyfrost.example.module.modules;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.polyfrost.example.module.modules.PitMap;
import org.polyfrost.example.module.modules.PitMapType;

public class MapDetector {
    public static PitMapType currentMapType;

    @SubscribeEvent
    public void onChat(ClientChatReceivedEvent event) {
        String message = event.message.func_150260_c();
        try {
            JsonObject obj = (JsonObject)new Gson().fromJson(message, JsonObject.class);
            if (obj != null && obj.has("map")) {
                String mapName = obj.get("map").getAsString();
                currentMapType = PitMapType.fromName(mapName);
                PitMap.updateMapType(currentMapType);
            }
        }
        catch (JsonSyntaxException jsonSyntaxException) {
            // empty catch block
        }
    }
}

