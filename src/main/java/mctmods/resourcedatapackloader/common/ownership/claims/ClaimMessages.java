package mctmods.resourcedatapackloader.common.ownership.claims;

import mctmods.resourcedatapackloader.common.util.TellThrottle;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;

public final class ClaimMessages {
    private static final int DENY_EVERY = 40;
    private static final int WATCH_EVERY = 5;
    private static final TellThrottle DENIED = new TellThrottle(DENY_EVERY);
    private static final Map<UUID, Seen> SEEN = new HashMap<>();

    private record Seen(ResourceKey<Level> dimension, int chunkX, int chunkZ, int owner) {}

    private ClaimMessages() {}

    public static void deny(Player player, int claimHandle) {
        if (!(player instanceof ServerPlayer target) || !ClaimSettings.denyMessage()) { return; }
        long now = target.level().getGameTime();
        if (!DENIED.ready(target.getScoreboardName(), now)) { return; }
        DENIED.mark(target.getScoreboardName(), now);
        String label = ClaimOwners.labelOf(target.server, claimHandle);
        target.displayClientMessage((label.isEmpty() ? Component.translatable("rdpl.claim.protected.orphan") : Component.translatable("rdpl.claim.protected", label)).withStyle(ChatFormatting.RED), true);
    }

    public static void onTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !(event.player instanceof ServerPlayer player) || player.tickCount % WATCH_EVERY != 0 || !ClaimSettings.any() || !ClaimSettings.message()) { return; }
        ChunkPos chunk = player.chunkPosition();
        int owner = ClaimIndex.get(player.serverLevel()).ownerAt(chunk.x, chunk.z);
        Seen last = SEEN.put(player.getUUID(), new Seen(player.level().dimension(), chunk.x, chunk.z, owner));
        if (last == null || last.owner == owner || (last.dimension == player.level().dimension() && last.chunkX == chunk.x && last.chunkZ == chunk.z)) { return; }
        if (owner != 0) { enter(player, owner); }
        else if (ClaimSettings.messageWilderness()) { leave(player, last.owner); }
    }

    public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) { SEEN.remove(event.getEntity().getUUID()); }

    private static void enter(ServerPlayer player, int owner) {
        String label = ClaimOwners.labelOf(player.server, owner);
        MutableComponent line = label.isEmpty() ? Component.translatable("rdpl.claim.enter.orphan") : Component.translatable("rdpl.claim.enter", label);
        ChatFormatting color = ClaimOwners.isMember(player, owner) ? ChatFormatting.GREEN : label.isEmpty() ? ChatFormatting.YELLOW : ChatFormatting.RED;
        player.displayClientMessage(line.withStyle(color), true);
    }

    private static void leave(ServerPlayer player, int owner) {
        String label = ClaimOwners.labelOf(player.server, owner);
        MutableComponent line = label.isEmpty() ? Component.translatable("rdpl.claim.leave.orphan") : Component.translatable("rdpl.claim.leave", label);
        player.displayClientMessage(line.append(" - ").append(Component.translatable("rdpl.claim.wilderness")), true);
    }
}
