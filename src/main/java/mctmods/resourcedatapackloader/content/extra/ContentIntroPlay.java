package mctmods.resourcedatapackloader.content.extra;

import mctmods.resourcedatapackloader.content.worldgen.ContentPregen;
import mctmods.resourcedatapackloader.network.RDPLNetwork;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class ContentIntroPlay {
    private static final String SEEN = "rdplIntroSeen";
    private static final Set<UUID> PLAYING = new HashSet<>();

    private ContentIntroPlay() {}

    public static boolean enabled() { return !ContentWorldIntro.pages().isEmpty(); }

    public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player) || !enabled()) { return; }
        if (ContentWorldIntro.once() && player.getPersistentData().getBoolean(SEEN)) { return; }
        if (!RDPLNetwork.reaches(player)) { return; }
        PLAYING.add(player.getUUID());
        RDPLNetwork.playIntro(player, ContentPregen.busy());
    }

    public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) { PLAYING.remove(event.getEntity().getUUID()); }

    public static boolean reading(UUID player) { return PLAYING.contains(player); }

    public static void finished(ServerPlayer player) {
        if (!PLAYING.remove(player.getUUID())) { return; }
        if (ContentWorldIntro.once()) { player.getPersistentData().putBoolean(SEEN, true); }
        ContentPregen.releaseAfterIntro(player);
    }

    public static void replay(ServerPlayer player) { player.getPersistentData().remove(SEEN); }
}
