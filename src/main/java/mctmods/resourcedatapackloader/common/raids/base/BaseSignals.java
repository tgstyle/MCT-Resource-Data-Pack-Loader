package mctmods.resourcedatapackloader.common.raids.base;

import mctmods.resourcedatapackloader.common.ownership.claims.ClaimOwners;
import mctmods.resourcedatapackloader.common.ownership.claims.ClaimSettings;
import mctmods.resourcedatapackloader.common.pack.definitions.villages.RaidDef;
import mctmods.resourcedatapackloader.common.raids.RaidTarget;

import java.util.Collection;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.CompoundContainer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerSetSpawnEvent;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.event.level.BlockEvent;

public final class BaseSignals {
    private static final int SAMPLE_EVERY = 200;
    private static final int MIN_SLOTS = 9;
    private static boolean armed;
    private static boolean creative;

    private BaseSignals() {}

    public static void arm(Collection<RaidDef> defs) {
        armed = false;
        creative = false;
        for (RaidDef def : defs) {
            if (!def.target().contains(RaidTarget.Kind.BASE) && !def.target().contains(RaidTarget.Kind.PLAYER)) { continue; }
            armed = true;
            creative |= def.base() != null && def.base().creative();
        }
    }

    public static void sample(ServerPlayer player) {
        if (!armed || player.tickCount % SAMPLE_EVERY != 0 || !(player.level() instanceof ServerLevel level) || player.isSpectator() || player.isCreative() && !creative) { return; }
        BaseLedger.get(level).ticks(player, player.blockPosition(), SAMPLE_EVERY);
    }

    public static void onSpawn(PlayerSetSpawnEvent event) {
        if (!armed || !(event.getEntity() instanceof ServerPlayer player) || !(player.level() instanceof ServerLevel level)) { return; }
        BlockPos spawn = event.getNewSpawn();
        BaseLedger ledger = BaseLedger.get(level);
        if (spawn == null) { ledger.clearBed(player.getUUID()); }
        else { ledger.setBed(player.getUUID(), spawn); }
    }

    public static void onWake(PlayerWakeUpEvent event) {
        if (!armed || !(event.getEntity() instanceof ServerPlayer player) || !(player.level() instanceof ServerLevel level)) { return; }
        BlockPos bed = player.getRespawnPosition();
        if (bed != null && player.getRespawnDimension().equals(level.dimension())) { BaseLedger.get(level).setBed(player.getUUID(), bed); }
    }

    public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!armed || !(event.getEntity() instanceof ServerPlayer player) || !(player.level() instanceof ServerLevel level)) { return; }
        BaseLedger ledger = BaseLedger.get(level);
        if (ledger.bed(player.getUUID()) != null) { return; }
        BlockPos bed = player.getRespawnDimension().equals(level.dimension()) ? player.getRespawnPosition() : null;
        if (bed != null) { ledger.setBed(player.getUUID(), bed); }
    }

    public static void onPlace(BlockEvent.EntityPlaceEvent event) {
        if (!armed || !(event.getLevel() instanceof ServerLevel level) || event.isCanceled()) { return; }
        ServerPlayer player = counted(event.getEntity());
        if (player != null) { BaseLedger.get(level).placed(player, event.getPos(), 1); }
    }

    public static void onBreak(BlockEvent.BreakEvent event) {
        if (!armed || !(event.getLevel() instanceof ServerLevel level) || event.isCanceled()) { return; }
        ServerPlayer player = counted(event.getPlayer());
        if (player != null) { BaseLedger.get(level).placed(player, event.getPos(), -1); }
    }

    public static void onOpen(PlayerContainerEvent.Open event) {
        if (!armed || !(event.getEntity() instanceof ServerPlayer player) || !(player.level() instanceof ServerLevel level)) { return; }
        BlockPos container = containerAt(player, level, event);
        if (container != null) { BaseLedger.get(level).opened(player, container); }
    }

    @Nullable private static ServerPlayer counted(@Nullable Entity entity) {
        if (!(entity instanceof ServerPlayer player)) { return null; }
        return ClaimOwners.isFake(player) && ClaimSettings.fakePlayerBlocksNone() ? null : player;
    }

    @Nullable private static BlockPos containerAt(ServerPlayer player, ServerLevel level, PlayerContainerEvent.Open event) {
        for (Slot slot : event.getContainer().slots) {
            Container inventory = slot.container;
            if (inventory.getContainerSize() < MIN_SLOTS || inventory instanceof Inventory) { continue; }
            if (inventory instanceof BlockEntity tile) { return tile.getBlockPos(); }
            return inventory instanceof CompoundContainer large ? pairAt(player, level, large) : null;
        }
        return null;
    }

    @Nullable private static BlockPos pairAt(Player player, ServerLevel level, CompoundContainer large) {
        double reach = player.getBlockReach() + 2.0D;
        int fromX = Mth.floor(player.getX() - reach) >> 4;
        int toX = Mth.floor(player.getX() + reach) >> 4;
        int fromZ = Mth.floor(player.getZ() - reach) >> 4;
        int toZ = Mth.floor(player.getZ() + reach) >> 4;
        BlockPos lower = null;
        for (int chunkX = fromX; chunkX <= toX; chunkX++) {
            for (int chunkZ = fromZ; chunkZ <= toZ; chunkZ++) {
                LevelChunk chunk = level.getChunkSource().getChunkNow(chunkX, chunkZ);
                if (chunk == null) { continue; }
                for (BlockEntity tile : chunk.getBlockEntities().values()) {
                    BlockPos at = tile.getBlockPos();
                    if (!(tile instanceof ChestBlockEntity chest) || player.distanceToSqr(Vec3.atCenterOf(at)) > reach * reach || !large.contains(chest)) { continue; }
                    if (lower == null || at.asLong() < lower.asLong()) { lower = at; }
                }
            }
        }
        return lower;
    }
}
