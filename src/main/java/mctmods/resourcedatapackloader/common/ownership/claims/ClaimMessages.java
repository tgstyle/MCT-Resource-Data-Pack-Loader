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
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

public final class ClaimMessages {
    private static final int DENY_EVERY = 40;
    private static final int WATCH_EVERY = 5;
    private static final TellThrottle DENIED = new TellThrottle(DENY_EVERY);
    private static final Map<UUID, Seen> SEEN = new HashMap<>();

    private record Seen(ResourceKey<Level> dimension, int chunkX, int chunkZ, int owner) {}

    private ClaimMessages() {}

    public static void deny(ServerPlayer player, int claimHandle) {
        if (!ClaimSettings.denyMessage()) { return; }
        long now = player.level().getGameTime();
        String name = player.getGameProfile().getName();
        if (!DENIED.ready(name, now)) { return; }
        DENIED.mark(name, now);
        String label = ClaimOwners.labelOf(player.server, claimHandle);
        player.displayClientMessage(label.isEmpty() ? Component.translatable("rdpl.claim.protected.orphan") : Component.translatable("rdpl.claim.protected", label), true);
    }

    @SubscribeEvent public static void onTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player) || player.tickCount % WATCH_EVERY != 0 || !ClaimSettings.any() || !ClaimSettings.message()) { return; }
        ClaimIndex index = ClaimIndex.obtain(player.level());
        if (index == null) { return; }
        int chunkX = player.blockPosition().getX() >> 4;
        int chunkZ = player.blockPosition().getZ() >> 4;
        int owner = index.ownerAt(chunkX, chunkZ);
        ResourceKey<Level> dimension = player.level().dimension();
        Seen last = SEEN.put(player.getUUID(), new Seen(dimension, chunkX, chunkZ, owner));
        if (last == null || last.owner == owner || (last.dimension.equals(dimension) && last.chunkX == chunkX && last.chunkZ == chunkZ)) { return; }
        if (owner != 0) { enter(player, owner); }
        else if (ClaimSettings.messageWilderness()) { leave(player, last.owner); }
    }

    @SubscribeEvent public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) { SEEN.remove(event.getEntity().getUUID()); }

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
