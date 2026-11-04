package mctmods.resourcedatapackloader.common.ownership.claims;

import mctmods.resourcedatapackloader.common.util.TellThrottle;
import mctmods.resourcedatapackloader.common.util.world.Coords;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public final class ClaimMessages {
    private static final int DENY_EVERY = 40;
    private static final int WATCH_EVERY = 5;
    private static final TellThrottle DENIED = new TellThrottle(DENY_EVERY);
    private static final Map<UUID, int[]> SEEN = new HashMap<>();

    private ClaimMessages() {}

    public static void deny(EntityPlayer player, int claimHandle) {
        if (!ClaimSettings.denyMessage()) { return; }
        long now = player.world.getTotalWorldTime();
        if (!DENIED.ready(player.getName(), now)) { return; }
        DENIED.mark(player.getName(), now);
        String label = ClaimOwners.labelOf(player.getServer(), claimHandle);
        player.sendStatusMessage(label.isEmpty() ? new TextComponentTranslation("rdpl.claim.protected.orphan") : new TextComponentTranslation("rdpl.claim.protected", label), true);
    }

    @SubscribeEvent public static void onTick(TickEvent.PlayerTickEvent event) {
        EntityPlayer player = event.player;
        if (event.phase != TickEvent.Phase.END || player.world.isRemote || player.ticksExisted % WATCH_EVERY != 0 || !ClaimSettings.any() || !ClaimSettings.message()) { return; }
        ClaimIndex index = ClaimIndex.obtain(player.world);
        if (index == null) { return; }
        int chunkX = Coords.getCubeXForEntity(player);
        int chunkZ = Coords.getCubeZForEntity(player);
        int owner = index.ownerAt(chunkX, chunkZ);
        int[] last = SEEN.put(player.getUniqueID(), new int[] {player.dimension, chunkX, chunkZ, owner});
        if (last == null || last[3] == owner || (last[0] == player.dimension && last[1] == chunkX && last[2] == chunkZ)) { return; }
        if (owner != 0) { enter(player, owner); }
        else if (ClaimSettings.messageWilderness()) { leave(player, last[3]); }
    }

    @SubscribeEvent public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) { SEEN.remove(event.player.getUniqueID()); }

    private static void enter(EntityPlayer player, int owner) {
        String label = ClaimOwners.labelOf(player.getServer(), owner);
        ITextComponent line = label.isEmpty() ? new TextComponentTranslation("rdpl.claim.enter.orphan") : new TextComponentTranslation("rdpl.claim.enter", label);
        TextFormatting color = ClaimOwners.isMember(player, owner) ? TextFormatting.GREEN : label.isEmpty() ? TextFormatting.YELLOW : TextFormatting.RED;
        player.sendStatusMessage(line.setStyle(line.getStyle().createDeepCopy().setColor(color)), true);
    }

    private static void leave(EntityPlayer player, int owner) {
        String label = ClaimOwners.labelOf(player.getServer(), owner);
        ITextComponent line = label.isEmpty() ? new TextComponentTranslation("rdpl.claim.leave.orphan") : new TextComponentTranslation("rdpl.claim.leave", label);
        player.sendStatusMessage(line.appendText(" - ").appendSibling(new TextComponentTranslation("rdpl.claim.wilderness")), true);
    }
}
