package mctmods.resourcedatapackloader.common.util.messages;

import mctmods.resourcedatapackloader.common.pack.loading.ContentControl;
import mctmods.resourcedatapackloader.core.config.Config;
import mctmods.resourcedatapackloader.core.config.Settings;
import mctmods.resourcedatapackloader.core.network.RDPLNetwork;
import mctmods.resourcedatapackloader.core.util.ContentLog;

import java.util.List;
import java.util.Set;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;

public final class Toasts {
    public static final int ADVANCEMENTS = 1;
    public static final int RECIPES = 2;
    public static final int TUTORIAL = 4;
    public static final int SYSTEM = 8;
    public static final int OTHER = 16;
    private static final int ALL = ADVANCEMENTS | RECIPES | TUTORIAL | SYSTEM | OTHER;
    private static final String KEY = "toasts";
    private static final String HIDDEN_KEY = "hiddenToasts";
    private static final int OFF_SERVER = -1;
    private static volatile int shown = OFF_SERVER;
    private static volatile Set<String> hidden = Set.of();

    private Toasts() {}

    public static boolean hides(int kind, String name) {
        int kinds = shown == OFF_SERVER ? (Config.chunks.toasts() ? ALL : 0) : shown;
        return (kinds & kind) == 0 || (shown == OFF_SERVER ? Config.chunks.hiddenToasts().contains(name) : hidden.contains(name));
    }

    public static void show(int kinds, List<String> names) {
        hidden = Set.copyOf(names);
        shown = kinds;
    }

    public static void leave() {
        shown = OFF_SERVER;
        hidden = Set.of();
    }

    public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) { if (event.getEntity() instanceof ServerPlayer player) { RDPLNetwork.sendToasts(player, allowed(), ContentControl.list(ContentControl.CHUNKS, HIDDEN_KEY, Config.chunks.hiddenToasts())); } }

    private static int allowed() {
        if (ContentControl.notArray(ContentControl.CHUNKS, KEY)) { return ContentControl.flag(ContentControl.CHUNKS, KEY, Config.chunks.toasts()) ? ALL : 0; }
        int kinds = 0;
        for (String name : ContentControl.list(ContentControl.CHUNKS, KEY, List.of())) {
            int kind = kind(name);
            if (kind == 0) { ContentLog.LOGGER.error("The active world template lists '{}' under toasts, which is not advancements, recipes, tutorial, system or other, so it is skipped", name); }
            kinds |= kind;
        }
        return kinds;
    }

    private static int kind(String name) {
        return switch (Settings.lower(name)) {
            case "advancements" -> ADVANCEMENTS;
            case "recipes" -> RECIPES;
            case "tutorial" -> TUTORIAL;
            case "system" -> SYSTEM;
            case "other" -> OTHER;
            default -> 0;
        };
    }
}
