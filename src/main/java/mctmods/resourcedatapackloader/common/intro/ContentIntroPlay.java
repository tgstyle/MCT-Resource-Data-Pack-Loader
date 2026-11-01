package mctmods.resourcedatapackloader.common.intro;

import mctmods.resourcedatapackloader.common.cards.CardFire;
import mctmods.resourcedatapackloader.common.pregen.ContentPregenHold;
import mctmods.resourcedatapackloader.common.util.entity.PlayerPersisted;
import mctmods.resourcedatapackloader.core.network.RDPLNetwork;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

public final class ContentIntroPlay {
    private static final String SEEN = "rdplIntroSeen";
    private static final Set<UUID> PLAYING = new HashSet<>();

    private ContentIntroPlay() {}

    public static boolean enabled() { return !ContentWorldIntro.pages().isEmpty(); }

    @SubscribeEvent public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.player instanceof EntityPlayerMP)) { return; }
        EntityPlayerMP player = (EntityPlayerMP) event.player;
        if (willPlay(player)) {
            PLAYING.add(player.getUniqueID());
            RDPLNetwork.playIntro(player);
        }
    }

    @SubscribeEvent public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) { PLAYING.remove(event.player.getUniqueID()); }

    public static boolean willPlay(EntityPlayerMP player) { return enabled() && !RDPLNetwork.vanilla(player) && !(ContentWorldIntro.once() && PlayerPersisted.of(player).getBoolean(SEEN)); }

    public static boolean reading(UUID player) { return PLAYING.contains(player); }

    public static void finished(EntityPlayerMP player) {
        if (!PLAYING.remove(player.getUniqueID())) { return; }
        if (ContentWorldIntro.once()) { PlayerPersisted.of(player).setBoolean(SEEN, true); }
        ContentPregenHold.releaseAfterIntro(player);
        CardFire.afterIntro(player);
    }

    public static void replay(EntityPlayerMP player) { PlayerPersisted.of(player).removeTag(SEEN); }
}
