package mctmods.resourcedatapackloader.util;

import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.network.RDPLNetwork;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import java.util.Locale;

public final class Toasts {
    public static final int ADVANCEMENTS = 1;
    public static final int RECIPES = 2;
    public static final int TUTORIAL = 4;
    public static final int SYSTEM = 8;
    public static final int OTHER = 16;
    private static final int ALL = ADVANCEMENTS | RECIPES | TUTORIAL | SYSTEM | OTHER;
    private static final String KEY = "toasts";
    private static volatile int shown;

    private Toasts() {}

    public static boolean hides(int kind) { return (shown & kind) == 0; }

    public static void show(int kinds) { shown = kinds; }

    @SubscribeEvent public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.player instanceof EntityPlayerMP) { RDPLNetwork.sendToasts((EntityPlayerMP) event.player, allowed()); }
    }

    @SubscribeEvent public static void onDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) { shown = 0; }

    private static int allowed() {
        if (!ContentControl.listed(ContentControl.CHUNKS, KEY)) { return ContentControl.flag(ContentControl.CHUNKS, KEY, Config.chunks.toasts) ? ALL : 0; }
        int kinds = 0;
        for (String name : ContentControl.list(ContentControl.CHUNKS, KEY, new String[0])) {
            int kind = kind(name);
            if (kind == 0) { ContentLog.LOGGER.error("The active world template lists '{}' under toasts, which is not advancements, recipes, tutorial, system or other, so it is skipped", name); }
            kinds |= kind;
        }
        return kinds;
    }

    private static int kind(String name) {
        switch (name.trim().toLowerCase(Locale.ROOT)) {
            case "advancements": return ADVANCEMENTS;
            case "recipes": return RECIPES;
            case "tutorial": return TUTORIAL;
            case "system": return SYSTEM;
            case "other": return OTHER;
            default: return 0;
        }
    }
}
