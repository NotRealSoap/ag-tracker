/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.command.ICommand
 *  net.minecraftforge.client.ClientCommandHandler
 *  net.minecraftforge.common.MinecraftForge
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 *  net.minecraftforge.fml.common.gameevent.TickEvent$ClientTickEvent
 *  net.minecraftforge.fml.common.gameevent.TickEvent$Phase
 *  net.minecraftforge.fml.common.gameevent.TickEvent$RenderTickEvent
 *  org.lwjgl.input.Keyboard
 */
package org.polyfrost.example.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import net.minecraft.client.Minecraft;
import net.minecraft.command.ICommand;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;
import org.polyfrost.example.AutoGrinderMod;
import org.polyfrost.example.config.AutoGrinderCommand;
import org.polyfrost.example.module.modules.AutoGrindModule;
import org.polyfrost.example.module.modules.PitMap;
import org.polyfrost.example.module.modules.PitMapType;
import org.polyfrost.example.utils.Chat;
import org.polyfrost.example.utils.ScoreboardUtils;

public class AutoGrinderConfig {
    public static int selectedMap = 0;
    public static int targetPriority = 0;
    public static int fov = 360;
    public static boolean stickToTarget = true;
    public static float stickRange = 6.0f;
    public static int jumpChance = 50;
    public static float horizontalAimSpeed = 2.9f;
    public static float verticalAimSpeed = 2.0f;
    public static float spawnCooldown = 1.0f;
    public static float stuckDetectionTime = 5.0f;
    public static boolean pickUpMystics = true;
    public static boolean excludeDiamonds = false;
    public static boolean toggleAura = false;
    public static boolean toggleAutoheal = false;
    public static boolean isAutoPerk = false;
    public static String toggleKeys = "LSHIFT+S";
    public static int midY = 0;
    public static int spawnY = 0;
    static final String[] MAPS = new String[]{"Abyss", "Genesis", "Seasons", "Elements", "Castle"};
    static final String[] PRIORITIES = new String[]{"Lowest Health", "Closest Distance"};
    private static final String FILE_NAME = "autogrinder.properties";
    static final List<Setting> SETTINGS = new ArrayList<Setting>();
    private static boolean pauseAtSpawn;
    private boolean toggleKeysWereDown;

    static PitMapType preset() {
        PitMapType[] pitMapTypeArray = PitMapType.values();
        return pitMapTypeArray[selectedMap >= 0 && selectedMap < pitMapTypeArray.length ? selectedMap : 0];
    }

    public static void applyHeights() {
        PitMap.MID_Y_COORDINATE = midY > 0 ? midY : AutoGrinderConfig.preset().getFloorY();
        PitMap.SPAWN_Y_COORDINATE = spawnY > 0 ? spawnY : AutoGrinderConfig.preset().getSpawnY();
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent clientTickEvent) {
        if (clientTickEvent.phase != TickEvent.Phase.START) {
            return;
        }
        AutoGrinderConfig.applyHeights();
        if (pauseAtSpawn) {
            AutoGrinderConfig.checkPauseAtSpawn();
        }
    }

    static boolean isPauseArmed() {
        return pauseAtSpawn;
    }

    static boolean armPauseAtSpawn() {
        if (AutoGrinderMod.moduleManager == null || !AutoGrinderMod.moduleManager.toggleSprintModule.isEnabled()) {
            return false;
        }
        pauseAtSpawn = true;
        return true;
    }

    static void cancelPauseAtSpawn() {
        pauseAtSpawn = false;
    }

    private static void checkPauseAtSpawn() {
        Minecraft minecraft = Minecraft.func_71410_x();
        if (minecraft.field_71439_g == null || AutoGrinderMod.moduleManager == null) {
            return;
        }
        AutoGrindModule autoGrindModule = AutoGrinderMod.moduleManager.toggleSprintModule;
        if (!autoGrindModule.isEnabled()) {
            pauseAtSpawn = false;
            return;
        }
        if (!ScoreboardUtils.getScoreboardDisplayName().contains("PIT")) {
            return;
        }
        if (minecraft.field_71439_g.func_180425_c().func_177956_o() < PitMap.SPAWN_Y_COORDINATE - 1) {
            return;
        }
        pauseAtSpawn = false;
        autoGrindModule.setEnabled(false);
        Chat.chat("\u00a7b[AutoGrinder]\u00a7r Paused in spawn. Resume with \u00a7f" + toggleKeys + "\u00a7r or \u00a7f/ag toggle");
    }

    public AutoGrinderConfig() {
        AutoGrinderConfig.load();
        AutoGrindModule.applySelectedMap(false);
        AutoGrinderConfig.applyHeights();
        MinecraftForge.EVENT_BUS.register((Object)this);
        ClientCommandHandler.instance.func_71560_a((ICommand)new AutoGrinderCommand());
    }

    @SubscribeEvent
    public void onRenderTick(TickEvent.RenderTickEvent renderTickEvent) {
        boolean bl;
        if (renderTickEvent.phase != TickEvent.Phase.START) {
            return;
        }
        Minecraft minecraft = Minecraft.func_71410_x();
        boolean bl2 = bl = minecraft.field_71439_g != null && minecraft.field_71462_r == null && AutoGrinderConfig.allKeysDown(toggleKeys);
        if (bl && !this.toggleKeysWereDown) {
            AutoGrinderConfig.toggleAutoGrinder();
        }
        this.toggleKeysWereDown = bl;
    }

    static void toggleAutoGrinder() {
        AutoGrindModule autoGrindModule;
        if (AutoGrinderMod.moduleManager == null || Minecraft.func_71410_x().field_71439_g == null) {
            return;
        }
        autoGrindModule.setEnabled(!(autoGrindModule = AutoGrinderMod.moduleManager.toggleSprintModule).isEnabled());
    }

    private static boolean allKeysDown(String string) {
        int[] nArray = AutoGrinderConfig.parseKeys(string);
        if (nArray == null) {
            return false;
        }
        for (int n : nArray) {
            if (Keyboard.isKeyDown((int)n)) continue;
            return false;
        }
        return true;
    }

    static int[] parseKeys(String string) {
        if (string == null || string.trim().isEmpty()) {
            return null;
        }
        String[] stringArray = string.split("\\+");
        int[] nArray = new int[stringArray.length];
        for (int i = 0; i < stringArray.length; ++i) {
            int n = AutoGrinderConfig.keyCode(stringArray[i].trim());
            if (n <= 0) {
                return null;
            }
            nArray[i] = n;
        }
        return nArray;
    }

    private static int keyCode(String string) {
        if (string.isEmpty()) {
            return 0;
        }
        for (int i = 1; i < 256; ++i) {
            String string2 = Keyboard.getKeyName((int)i);
            if (string2 == null || !string2.equalsIgnoreCase(string)) continue;
            return i;
        }
        return 0;
    }

    static File file() {
        Minecraft minecraft = Minecraft.func_71410_x();
        File file = minecraft != null && minecraft.field_71412_D != null ? minecraft.field_71412_D : new File(".");
        return new File(new File(file, "config"), FILE_NAME);
    }

    static void load() {
        File file = AutoGrinderConfig.file();
        if (!file.isFile()) {
            AutoGrinderConfig.save();
            return;
        }
        Properties properties = new Properties();
        try (FileInputStream fileInputStream = new FileInputStream(file);){
            properties.load(fileInputStream);
        }
        catch (Throwable throwable) {
            return;
        }
        for (Setting setting : SETTINGS) {
            String string = properties.getProperty(setting.key);
            if (string == null) continue;
            setting.set(string);
        }
    }

    static void save() {
        Properties properties = new Properties();
        for (Setting object : SETTINGS) {
            properties.setProperty(object.key, object.raw());
        }
        File file = AutoGrinderConfig.file();
        try {
            File throwable = file.getParentFile();
            if (throwable != null) {
                throwable.mkdirs();
            }
            try (FileOutputStream fileOutputStream = new FileOutputStream(file);){
                properties.store(fileOutputStream, "AutoGrinder settings - edit here or use /autogrinder in game. map: " + String.join((CharSequence)", ", Arrays.asList(MAPS)) + " (0-4); targetPriority: 0 = Lowest Health, 1 = Closest Distance");
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    static Setting find(String string) {
        for (Setting setting : SETTINGS) {
            if (!setting.key.equalsIgnoreCase(string)) continue;
            return setting;
        }
        return null;
    }

    static {
        SETTINGS.add(Setting.choice("map", "Map", MAPS, () -> selectedMap, n -> {
            selectedMap = n;
        }));
        SETTINGS.add(Setting.choice("targetPriority", "Target Priority", PRIORITIES, () -> targetPriority, n -> {
            targetPriority = n;
        }));
        SETTINGS.add(Setting.integer("fov", "FOV (degrees)", 30, 360, () -> fov, n -> {
            fov = n;
        }));
        SETTINGS.add(Setting.bool("stickToTarget", "Stick To Target", () -> stickToTarget, bl -> {
            stickToTarget = bl;
        }));
        SETTINGS.add(Setting.decimal("stickRange", "Stick Range (blocks)", 3.0f, 10.0f, () -> stickRange, f -> {
            stickRange = f;
        }));
        SETTINGS.add(Setting.integer("jumpChance", "Jump chance (%)", 0, 100, () -> jumpChance, n -> {
            jumpChance = n;
        }));
        SETTINGS.add(Setting.decimal("horizontalAimSpeed", "Horizontal aim speed", 1.0f, 9.0f, () -> horizontalAimSpeed, f -> {
            horizontalAimSpeed = f;
        }));
        SETTINGS.add(Setting.decimal("verticalAimSpeed", "Vertical aim speed", 1.0f, 9.0f, () -> verticalAimSpeed, f -> {
            verticalAimSpeed = f;
        }));
        SETTINGS.add(Setting.decimal("spawnCooldown", "Spawn cooldown (s)", 0.0f, 25.0f, () -> spawnCooldown, f -> {
            spawnCooldown = f;
        }));
        SETTINGS.add(Setting.decimal("stuckDetectionTime", "Stuck detection time (s)", 1.0f, 10.0f, () -> stuckDetectionTime, f -> {
            stuckDetectionTime = f;
        }));
        SETTINGS.add(Setting.bool("pickUpMystics", "Pick Up Mystics", () -> pickUpMystics, bl -> {
            pickUpMystics = bl;
        }));
        SETTINGS.add(Setting.bool("excludeDiamonds", "Ignore Diamond Wearers", () -> excludeDiamonds, bl -> {
            excludeDiamonds = bl;
        }));
        SETTINGS.add(Setting.bool("toggleAura", "Myau KillAura", () -> toggleAura, bl -> {
            toggleAura = bl;
        }));
        SETTINGS.add(Setting.bool("toggleAutoheal", "Myau AutoHeal", () -> toggleAutoheal, bl -> {
            toggleAutoheal = bl;
        }));
        SETTINGS.add(Setting.bool("isAutoPerk", "AutoPerk (unimplemented)", () -> isAutoPerk, bl -> {
            isAutoPerk = bl;
        }));
        SETTINGS.add(Setting.keys("toggleKeys", "Toggle keys", () -> toggleKeys, string -> {
            toggleKeys = string;
        }));
        SETTINGS.add(Setting.height("midY", "Mid floor height", () -> AutoGrinderConfig.preset().getFloorY(), () -> midY, n -> {
            midY = n;
        }));
        SETTINGS.add(Setting.height("spawnY", "Spawn height", () -> AutoGrinderConfig.preset().getSpawnY(), () -> spawnY, n -> {
            spawnY = n;
        }));
        pauseAtSpawn = false;
    }

    static final class Setting {
        final String key;
        final String label;
        final Kind kind;
        final String[] options;
        final float min;
        final float max;
        private IntGet presetGet;
        private final IntGet intGet;
        private final IntSet intSet;
        private final FloatGet floatGet;
        private final FloatSet floatSet;
        private final BoolGet boolGet;
        private final BoolSet boolSet;
        private final StrGet strGet;
        private final StrSet strSet;

        private Setting(String string, String string2, Kind kind, String[] stringArray, float f, float f2, IntGet intGet, IntSet intSet, FloatGet floatGet, FloatSet floatSet, BoolGet boolGet, BoolSet boolSet, StrGet strGet, StrSet strSet) {
            this.key = string;
            this.label = string2;
            this.kind = kind;
            this.options = stringArray;
            this.min = f;
            this.max = f2;
            this.intGet = intGet;
            this.intSet = intSet;
            this.floatGet = floatGet;
            this.floatSet = floatSet;
            this.boolGet = boolGet;
            this.boolSet = boolSet;
            this.strGet = strGet;
            this.strSet = strSet;
        }

        static Setting choice(String string, String string2, String[] stringArray, IntGet intGet, IntSet intSet) {
            return new Setting(string, string2, Kind.CHOICE, stringArray, 0.0f, stringArray.length - 1, intGet, intSet, null, null, null, null, null, null);
        }

        static Setting integer(String string, String string2, int n, int n2, IntGet intGet, IntSet intSet) {
            return new Setting(string, string2, Kind.INT, null, n, n2, intGet, intSet, null, null, null, null, null, null);
        }

        static Setting decimal(String string, String string2, float f, float f2, FloatGet floatGet, FloatSet floatSet) {
            return new Setting(string, string2, Kind.DECIMAL, null, f, f2, null, null, floatGet, floatSet, null, null, null, null);
        }

        static Setting bool(String string, String string2, BoolGet boolGet, BoolSet boolSet) {
            return new Setting(string, string2, Kind.BOOL, null, 0.0f, 1.0f, null, null, null, null, boolGet, boolSet, null, null);
        }

        static Setting keys(String string, String string2, StrGet strGet, StrSet strSet) {
            return new Setting(string, string2, Kind.KEYS, null, 0.0f, 0.0f, null, null, null, null, null, null, strGet, strSet);
        }

        static Setting height(String string, String string2, IntGet intGet, IntGet intGet2, IntSet intSet) {
            Setting setting = new Setting(string, string2, Kind.HEIGHT, null, 0.0f, 255.0f, intGet2, intSet, null, null, null, null, null, null);
            setting.presetGet = intGet;
            return setting;
        }

        String raw() {
            switch (this.kind.ordinal()) {
                case 0: 
                case 1: 
                case 5: {
                    return String.valueOf(this.intGet.get());
                }
                case 2: {
                    return Setting.trim(this.floatGet.get());
                }
                case 3: {
                    return String.valueOf(this.boolGet.get());
                }
            }
            return this.strGet.get();
        }

        String display() {
            switch (this.kind.ordinal()) {
                case 0: {
                    int n = this.intGet.get();
                    return n >= 0 && n < this.options.length ? this.options[n] : String.valueOf(n);
                }
                case 3: {
                    return this.boolGet.get() ? "on" : "off";
                }
                case 5: {
                    int n = this.intGet.get();
                    return n > 0 ? String.valueOf(n) : "map preset (" + this.presetGet.get() + ")";
                }
            }
            return this.raw();
        }

        String allowed() {
            switch (this.kind.ordinal()) {
                case 0: {
                    return String.join((CharSequence)" | ", Arrays.asList(this.options));
                }
                case 1: {
                    return (int)this.min + " to " + (int)this.max;
                }
                case 2: {
                    return Setting.trim(this.min) + " to " + Setting.trim(this.max);
                }
                case 3: {
                    return "on | off";
                }
                case 5: {
                    return "'here' (where you're standing), a Y value, or 0 for the map preset";
                }
            }
            return "key names joined by +, e.g. LSHIFT+S";
        }

        List<String> suggestions() {
            switch (this.kind.ordinal()) {
                case 0: {
                    ArrayList<String> arrayList = new ArrayList<String>();
                    for (String string : this.options) {
                        arrayList.add(string.replace(" ", ""));
                    }
                    return arrayList;
                }
                case 3: {
                    return Arrays.asList("on", "off", "toggle");
                }
                case 4: {
                    return Arrays.asList("LSHIFT+S");
                }
                case 5: {
                    return Arrays.asList("here", "0");
                }
            }
            return new ArrayList<String>();
        }

        String set(String string) {
            String string2 = string == null ? "" : string.trim();
            switch (this.kind.ordinal()) {
                case 0: {
                    int n;
                    String string3 = string2.replace(" ", "").toLowerCase(Locale.ROOT);
                    for (n = 0; n < this.options.length; ++n) {
                        if (!this.options[n].replace(" ", "").toLowerCase(Locale.ROOT).equals(string3)) continue;
                        this.intSet.set(n);
                        return null;
                    }
                    for (n = 0; n < this.options.length; ++n) {
                        if (string3.isEmpty() || !this.options[n].replace(" ", "").toLowerCase(Locale.ROOT).startsWith(string3)) continue;
                        this.intSet.set(n);
                        return null;
                    }
                    try {
                        n = Integer.parseInt(string2);
                        if (n >= 0 && n < this.options.length) {
                            this.intSet.set(n);
                            return null;
                        }
                    }
                    catch (NumberFormatException numberFormatException) {
                        // empty catch block
                    }
                    return "must be one of: " + this.allowed();
                }
                case 1: {
                    try {
                        int n = (int)Math.round(Double.parseDouble(string2));
                        if ((float)n < this.min || (float)n > this.max) {
                            return "must be " + this.allowed();
                        }
                        this.intSet.set(n);
                        return null;
                    }
                    catch (NumberFormatException numberFormatException) {
                        return "must be a whole number, " + this.allowed();
                    }
                }
                case 2: {
                    try {
                        float f = Float.parseFloat(string2);
                        if (Float.isNaN(f) || f < this.min || f > this.max) {
                            return "must be " + this.allowed();
                        }
                        this.floatSet.set(f);
                        return null;
                    }
                    catch (NumberFormatException numberFormatException) {
                        return "must be a number, " + this.allowed();
                    }
                }
                case 3: {
                    String string4 = string2.toLowerCase(Locale.ROOT);
                    if (string4.equals("on") || string4.equals("true") || string4.equals("yes") || string4.equals("1")) {
                        this.boolSet.set(true);
                        return null;
                    }
                    if (string4.equals("off") || string4.equals("false") || string4.equals("no") || string4.equals("0")) {
                        this.boolSet.set(false);
                        return null;
                    }
                    if (string4.equals("toggle")) {
                        this.boolSet.set(!this.boolGet.get());
                        return null;
                    }
                    return "must be on or off";
                }
                case 5: {
                    String string5 = string2.toLowerCase(Locale.ROOT);
                    if (string5.equals("here")) {
                        Minecraft minecraft = Minecraft.func_71410_x();
                        if (minecraft.field_71439_g == null) {
                            return "- join a world first, then stand on the spot";
                        }
                        this.intSet.set((int)Math.floor(minecraft.field_71439_g.field_70163_u));
                        return null;
                    }
                    if (string5.equals("preset") || string5.equals("auto") || string5.equals("reset")) {
                        this.intSet.set(0);
                        return null;
                    }
                    try {
                        int n = Integer.parseInt(string2);
                        if (n < 0 || n > 255) {
                            return "must be " + this.allowed();
                        }
                        this.intSet.set(n);
                        return null;
                    }
                    catch (NumberFormatException numberFormatException) {
                        return "must be " + this.allowed();
                    }
                }
            }
            if (AutoGrinderConfig.parseKeys(string2) == null) {
                return "unknown key name in '" + string2 + "' (use names like LSHIFT, S, F6, LCONTROL)";
            }
            this.strSet.set(string2.toUpperCase(Locale.ROOT));
            return null;
        }

        private static String trim(float f) {
            return (double)f == Math.rint(f) ? String.valueOf((int)f) : String.valueOf(f);
        }

        static enum Kind {
            CHOICE,
            INT,
            DECIMAL,
            BOOL,
            KEYS,
            HEIGHT;

        }
    }

    static interface IntGet {
        public int get();
    }

    static interface IntSet {
        public void set(int var1);
    }

    static interface BoolGet {
        public boolean get();
    }

    static interface BoolSet {
        public void set(boolean var1);
    }

    static interface FloatGet {
        public float get();
    }

    static interface FloatSet {
        public void set(float var1);
    }

    static interface StrGet {
        public String get();
    }

    static interface StrSet {
        public void set(String var1);
    }
}

