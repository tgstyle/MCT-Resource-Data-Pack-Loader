package mctmods.resourcedatapackloader.content.extra;

import mctmods.resourcedatapackloader.network.RDPLNetwork;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class ContentIntroPlay {
    private static final String SEEN = "rdplIntroSeen";
    private static final Set<UUID> PLAYING = new HashSet<>();

    private ContentIntroPlay() {}

    public static boolean enabled() { return !ContentWorldIntro.pages().isEmpty(); }

    @SubscribeEvent public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.player instanceof EntityPlayerMP)) { return; }
        EntityPlayerMP player = (EntityPlayerMP) event.player;
        if (!enabled()) { return; }
        if (ContentWorldIntro.once() && persisted(player).getBoolean(SEEN)) { return; }
        PLAYING.add(player.getUniqueID());
        RDPLNetwork.playIntro(player);
    }

    @SubscribeEvent public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) { PLAYING.remove(event.player.getUniqueID()); }

    public static void finished(EntityPlayerMP player) {
        if (!PLAYING.remove(player.getUniqueID())) { return; }
        if (ContentWorldIntro.once()) { persisted(player).setBoolean(SEEN, true); }
    }

    public static void replay(EntityPlayerMP player) { persisted(player).removeTag(SEEN); }

    private static NBTTagCompound persisted(EntityPlayerMP player) {
        NBTTagCompound data = player.getEntityData();
        if (!data.hasKey(EntityPlayer.PERSISTED_NBT_TAG)) { data.setTag(EntityPlayer.PERSISTED_NBT_TAG, new NBTTagCompound()); }
        return data.getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
    }
}
