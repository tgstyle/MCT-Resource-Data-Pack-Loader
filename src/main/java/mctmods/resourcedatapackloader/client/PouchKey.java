package mctmods.resourcedatapackloader.client;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;
import mctmods.resourcedatapackloader.client.screen.ContentContainerScreen;
import mctmods.resourcedatapackloader.compat.ClientCompat;
import mctmods.resourcedatapackloader.compat.LineClientCompat;
import mctmods.resourcedatapackloader.content.compat.ContentCurios;
import mctmods.resourcedatapackloader.network.RDPLNetwork;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import javax.annotation.Nullable;

public final class PouchKey {
    public static final KeyMapping.Category CATEGORY = new KeyMapping.Category(Identifier.fromNamespaceAndPath(ResourceDataPackLoader.MOD_ID, "main"));
    public static final String NAME = "key." + ResourceDataPackLoader.MOD_ID + ".pouch";
    @Nullable private static KeyMapping key;

    private PouchKey() {}

    public static void register(RegisterKeyMappingsEvent event) {
        if (ContentCurios.missing()) { return; }
        key = new KeyMapping(NAME, KeyConflictContext.IN_GAME, LineClientCompat.keyV(), CATEGORY);
        event.registerCategory(CATEGORY);
        event.register(key);
    }

    public static void screen(ScreenEvent.KeyPressed.Pre event) {
        if (key == null || !(event.getScreen() instanceof ContentContainerScreen screen) || screen.getMenu().worn() < 0 || !key.matches(event.getKeyEvent())) { return; }
        RDPLNetwork.openWorn();
        event.setCanceled(true);
    }

    public static void tick(ClientTickEvent.Post ignoredEvent) {
        if (key == null) { return; }
        Minecraft client = Minecraft.getInstance();
        if (client.player == null || ClientCompat.screen(client) != null) { return; }
        while (key.consumeClick()) { RDPLNetwork.openWorn(); }
    }
}
