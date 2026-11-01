package mctmods.resourcedatapackloader.common.intro;

import mctmods.resourcedatapackloader.common.cards.CardFire;
import mctmods.resourcedatapackloader.common.pregen.ContentPregen;
import mctmods.resourcedatapackloader.common.pregen.ContentPregenHold;
import mctmods.resourcedatapackloader.common.util.entity.PlayerPersisted;
import mctmods.resourcedatapackloader.common.welcome.ContentWelcome;
import mctmods.resourcedatapackloader.core.network.RDPLNetwork;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

public final class ContentIntroPlay {
    private static final String SEEN = "rdplIntroSeen";
    private static final Set<UUID> PLAYING = new HashSet<>();

    private ContentIntroPlay() {}

    public static boolean disabled() { return ContentWorldIntro.pages().isEmpty(); }

    public static boolean skips(ServerPlayer player) {
        if (disabled() || !RDPLNetwork.reaches(player)) { return true; }
        return ContentWorldIntro.once() && PlayerPersisted.of(player, SEEN).getBooleanOr(SEEN, false);
    }

    public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player) || skips(player)) { return; }
        PLAYING.add(player.getUUID());
        RDPLNetwork.playIntro(player, ContentPregen.landBeingMade());
    }

    public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) { PLAYING.remove(event.getEntity().getUUID()); }

    public static boolean reading(UUID player) { return PLAYING.contains(player); }

    public static void finished(ServerPlayer player) {
        if (!PLAYING.remove(player.getUUID())) { return; }
        if (ContentWorldIntro.once()) { PlayerPersisted.of(player, SEEN).putBoolean(SEEN, true); }
        welcomeUnlessHeld(player);
        CardFire.afterIntro(player);
    }

    private static void welcomeUnlessHeld(ServerPlayer player) {
        if (ContentPregen.landBeingMade() || ContentPregenHold.releaseAfterIntro(player)) { return; }
        ContentWelcome.welcome(player);
    }

    public static void replay(ServerPlayer player) { PlayerPersisted.of(player, SEEN).remove(SEEN); }
}
