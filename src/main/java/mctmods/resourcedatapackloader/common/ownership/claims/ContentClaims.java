package mctmods.resourcedatapackloader.common.ownership.claims;

import mctmods.resourcedatapackloader.common.util.world.Coords;
import mctmods.resourcedatapackloader.common.util.worldgen.GenHeights;

import net.minecraft.block.state.IBlockState;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public final class ContentClaims {
    private ContentClaims() {}

    public static boolean enabled() { return ClaimSettings.any(); }

    @SubscribeEvent public static void onChunkLoad(ChunkEvent.Load event) {
        World world = event.getWorld();
        if (!enabled() || world.isRemote || GenHeights.rubic(world)) { return; }
        ClaimIndex index = ClaimIndex.obtain(world);
        if (index == null) { return; }
        ClaimDissolve.onLoad(world, event.getChunk().x, event.getChunk().z);
        for (ClaimIndex.Anchor anchor : index.anchorList()) {
            if (Coords.blockToCube(anchor.x) == event.getChunk().x && Coords.blockToCube(anchor.z) == event.getChunk().z) { verify(index, anchor, event.getChunk().getBlockState(new BlockPos(anchor.x, anchor.y, anchor.z))); }
        }
    }

    @SubscribeEvent public static void onTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !enabled()) { return; }
        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        if (server == null) { return; }
        ClaimDissolve.drain();
        if (server.getTickCounter() % ClaimSettings.checkTicks() != 0) { return; }
        for (WorldServer world : DimensionManager.getWorlds()) { sweep(world); }
    }

    private static void sweep(World world) {
        ClaimIndex index = ClaimIndex.obtain(world);
        if (index == null) { return; }
        for (ClaimIndex.Anchor anchor : index.anchorList()) {
            BlockPos pos = new BlockPos(anchor.x, anchor.y, anchor.z);
            if (world.isBlockLoaded(pos)) { verify(index, anchor, world.getBlockState(pos)); }
        }
    }

    private static void verify(ClaimIndex index, ClaimIndex.Anchor anchor, IBlockState state) {
        if (!ClaimSettings.isClaimBlock(state.getBlock())) { ClaimTools.dissolve(index, anchor.id); }
    }
}
