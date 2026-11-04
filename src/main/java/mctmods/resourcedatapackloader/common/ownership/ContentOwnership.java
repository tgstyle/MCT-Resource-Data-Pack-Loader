package mctmods.resourcedatapackloader.common.ownership;

import mctmods.resourcedatapackloader.common.ownership.claims.ClaimDissolve;
import mctmods.resourcedatapackloader.common.util.world.PlacePositions;

import com.mojang.brigadier.context.CommandContextBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.level.ChunkEvent;
import net.minecraftforge.event.level.ExplosionEvent;
import net.minecraftforge.event.level.LevelEvent;

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
        OwnedBlocks store = OwnedBlocksProvider.of(chunk);
        if (store != null && !store.entries().isEmpty()) { DissolvePass.onLoad(level, store, chunk); }
    }

    public static void onTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) { return; }
        MinecraftServer server = event.getServer();
        TeamDissolve.tick(server);
        DissolvePass.run(server);
        OwnershipSweep.tick(server);
    }

    public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) { TeamDissolve.onLogin(player); }
    }

    public static void onCommand(CommandEvent event) {
        CommandContextBuilder<CommandSourceStack> context = event.getParseResults().getContext();
        if (context.getNodes().isEmpty()) { return; }
        String name = context.getNodes().get(0).getNode().getName();
        if (!"team".equals(name) && !"scoreboard".equals(name)) { return; }
        TeamDissolve.check(context.getSource().getServer());
    }

    public static void onLevelLoad(LevelEvent.Load event) {
        if (event.getLevel() instanceof ServerLevel level && level.dimension() == Level.OVERWORLD) { TeamDissolve.check(level.getServer()); }
    }
}
