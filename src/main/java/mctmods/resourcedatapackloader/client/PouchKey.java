package mctmods.resourcedatapackloader.client;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;
import mctmods.resourcedatapackloader.client.screen.ContentContainerScreen;
import mctmods.resourcedatapackloader.content.compat.ContentCurios;
import mctmods.resourcedatapackloader.network.RDPLNetwork;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nullable;

public final class PouchKey {
    public static final String CATEGORY = "key.categories." + ResourceDataPackLoader.MOD_ID;
    public static final String NAME = "key." + ResourceDataPackLoader.MOD_ID + ".pouch";
    @Nullable private static KeyMapping key;

    private PouchKey() {}

    public static void register(RegisterKeyMappingsEvent event) {
        if (ContentCurios.missing()) { return; }
        key = new KeyMapping(NAME, KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_V, CATEGORY);
        event.register(key);
    }

    public static void screen(ScreenEvent.KeyPressed.Pre event) {
        if (key == null || !(event.getScreen() instanceof ContentContainerScreen screen) || screen.getMenu().worn() < 0 || !key.matches(event.getKeyCode(), event.getScanCode())) { return; }
        RDPLNetwork.openWorn();
        event.setCanceled(true);
    }

    public static void tick(ClientTickEvent.Post ignoredEvent) {
        if (key == null) { return; }
        Minecraft client = Minecraft.getInstance();
        if (client.player == null || client.screen != null) { return; }
        while (key.consumeClick()) { RDPLNetwork.openWorn(); }
    }
}
