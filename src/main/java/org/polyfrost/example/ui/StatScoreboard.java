package org.polyfrost.example.ui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.polyfrost.example.module.modules.StatTracker;
import org.lwjgl.opengl.GL11;

public class StatScoreboard {
    private static final Minecraft mc = Minecraft.func_71410_x();
    private StatTracker tracker;
    private boolean visible = true;

    public StatScoreboard(StatTracker tracker) {
        this.tracker = tracker;
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onRenderGameOverlay(RenderGameOverlayEvent event) {
        if (!this.visible || tracker == null || !tracker.isEnabled()) return;
        if (event.phase != RenderGameOverlayEvent.ElementType.CHAT.ordinal()) return;

        renderScoreboard();
    }

    private void renderScoreboard() {
        FontRenderer fontRenderer = mc.field_71466_p;
        if (fontRenderer == null) return;

        GlStateManager.func_179140_f();
        GlStateManager.func_179124_n();

        int screenHeight = mc.field_71440_d;
        int baseX = 15;
        int baseY = screenHeight / 2 - 50;

        // Title
        String title = "§6§lStats";
        fontRenderer.func_175063_a(title, baseX, baseY, 0xFFFFFF);

        // Deaths
        String deathsText = "§cDeaths§r: " + tracker.getDeaths();
        fontRenderer.func_175063_a(deathsText, baseX + 5, baseY + 15, 0xFFFFFF);

        // Uberstreaks
        String uberText = "§aUberstreaks§r: " + tracker.getUberstreaks();
        fontRenderer.func_175063_a(uberText, baseX + 5, baseY + 25, 0xFFFFFF);

        // Mystic Drops
        String mysticText = "§5Mystic Drops§r: " + tracker.getMysticDrops();
        fontRenderer.func_175063_a(mysticText, baseX + 5, baseY + 35, 0xFFFFFF);

        // Session Duration
        String sessionText = "§eSession§r: " + tracker.getSessionTimeFormatted();
        fontRenderer.func_175063_a(sessionText, baseX + 5, baseY + 45, 0xFFFFFF);

        // Average Uberstreak Time
        String avgText = "§dAvg Streak§r: " + tracker.getAverageUberstreakTimeFormatted();
        fontRenderer.func_175063_a(avgText, baseX + 5, baseY + 55, 0xFFFFFF);

        GlStateManager.func_179098_w();
        GlStateManager.func_179141_d();
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isVisible() {
        return this.visible;
    }

    public void toggleVisible() {
        this.visible = !this.visible;
    }
}
