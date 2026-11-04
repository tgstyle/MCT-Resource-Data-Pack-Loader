package mctmods.resourcedatapackloader.common.ownership;

import mctmods.resourcedatapackloader.common.ownership.claims.ClaimDissolve;
import mctmods.resourcedatapackloader.common.util.world.PlacePositions;

import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.context.ParsedCommandNode;
import java.util.List;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.neoforge.event.CommandEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.level.ChunkEvent;
import net.neoforged.neoforge.event.level.ExplosionEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

public final class ContentOwnership {
    private ContentOwnership() {}

    public static void onPlace(BlockEvent.EntityPlaceEvent event) {
        if (!OwnershipSettings.any() || !(event.getLevel() instanceof ServerLevel level)) { return; }
        for (BlockPos pos : PlacePositions.of(event)) { place(level, pos, event.getEntity()); }
    }

    private static void place(ServerLevel level, BlockPos pos, Entity entity) {
        if (entity instanceof ServerPlayer player) {
            Block block = level.getBlockState(pos).getBlock();
            if (OwnershipSettings.tags(block)) {
                int handle = OwnerTable.handleOf(player, OwnershipSettings.who());
                if (handle != 0) {
                    OwnedBlockLookup.tag(level, pos, OwnedBlockLookup.nameOf(block), handle);
                    return;
                }
            }
        }
        OwnedBlockLookup.clear(level, pos);
    }

    public static void onBreak(BlockEvent.BreakEvent event) {
        if (event.isCanceled() || !OwnershipSettings.any() || !(event.getLevel() instanceof ServerLevel level)) { return; }
        OwnedBlockLookup.clear(level, event.getPos());
    }

    public static void onDetonate(ExplosionEvent.Detonate event) {
        if (!OwnershipSettings.any() || !(event.getLevel() instanceof ServerLevel level)) { return; }
        for (BlockPos pos : event.getAffectedBlocks()) { OwnedBlockLookup.clear(level, pos); }
    }

    public static void onChunkLoad(ChunkEvent.Load event) {
        if (!(event.getLevel() instanceof ServerLevel level) || !(event.getChunk() instanceof LevelChunk chunk)) { return; }
        ClaimDissolve.onLoad(level, chunk.getPos().x, chunk.getPos().z);
        OwnedBlocks store = OwnedBlocksStore.of(chunk);
        if (!store.entries().isEmpty()) { DissolvePass.onLoad(level, store, chunk); }
    }

    public static void onTick(ServerTickEvent.Post event) {
        TeamDissolve.tick(event.getServer());
        DissolvePass.run(event.getServer());
        OwnershipSweep.tick(event.getServer());
    }

    public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) { TeamDissolve.onLogin(player); }
    }

    public static void onCommand(CommandEvent event) {
        CommandContextBuilder<CommandSourceStack> context = event.getParseResults().getContext();
        List<ParsedCommandNode<CommandSourceStack>> nodes = context.getNodes();
        if (nodes.isEmpty()) { return; }
        String name = nodes.getFirst().getNode().getName();
        if ("team".equals(name) || "scoreboard".equals(name)) { TeamDissolve.check(context.getSource().getServer()); }
    }

    public static void onLevelLoad(LevelEvent.Load event) {
        if (event.getLevel() instanceof ServerLevel level && level.dimension() == Level.OVERWORLD) { TeamDissolve.check(level.getServer()); }
    }
}
