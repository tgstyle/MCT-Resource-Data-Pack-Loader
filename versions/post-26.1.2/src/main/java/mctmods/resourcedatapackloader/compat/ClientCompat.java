package mctmods.resourcedatapackloader.compat;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import javax.annotation.Nullable;

public final class ClientCompat {
    private ClientCompat() {}

    @Nullable public static Screen screen(Minecraft mc) { return mc.gui.screen(); }

    public static void setScreen(Minecraft mc, @Nullable Screen screen) { mc.gui.setScreen(screen); }

    public static boolean hudHidden(Minecraft mc) { return mc.gui.hud.isHidden(); }

    public static int leftHeight(Minecraft mc) { return mc.gui.hud.leftHeight; }

    public static void addLeftHeight(Minecraft mc, int amount) { mc.gui.hud.leftHeight += amount; }

    public static ChatComponent chat(Minecraft mc) { return mc.gui.hud.getChat(); }

    public static RenderType cutoutBlockSheet() { return Sheets.cutoutBlockItemSheet(); }

    public static RenderTarget mainTarget(Minecraft mc) { return mc.gameRenderer.mainRenderTarget(); }

    public static boolean depthZeroToOne() { return RenderSystem.getDevice().getDeviceInfo().isZZeroToOne(); }

    public static void allChanged(Minecraft mc) { mc.levelExtractor.allChanged(); }

    public static PreparableReloadListener levelListener(Minecraft mc) { return mc.levelExtractor; }
}
