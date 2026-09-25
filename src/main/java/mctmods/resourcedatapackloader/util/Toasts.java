package mctmods.resourcedatapackloader.util;

import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.network.RDPLNetwork;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import java.util.List;
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

    public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) { RDPLNetwork.sendToasts(player, allowed()); }
    }

    private static int allowed() {
        if (!ContentControl.listed(ContentControl.CHUNKS, KEY)) { return ContentControl.flag(ContentControl.CHUNKS, KEY, Config.chunks.toasts()) ? ALL : 0; }
        int kinds = 0;
        for (String name : ContentControl.list(ContentControl.CHUNKS, KEY, List.of())) {
            int kind = kind(name);
            if (kind == 0) { ContentLog.LOGGER.error("The active world template lists '{}' under toasts, which is not advancements, recipes, tutorial, system or other, so it is skipped", name); }
            kinds |= kind;
        }
        return kinds;
    }

    private static int kind(String name) {
        return switch (name.trim().toLowerCase(Locale.ROOT)) {
            case "advancements" -> ADVANCEMENTS;
            case "recipes" -> RECIPES;
            case "tutorial" -> TUTORIAL;
            case "system" -> SYSTEM;
            case "other" -> OTHER;
            default -> 0;
        };
    }
}
