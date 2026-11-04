package mctmods.resourcedatapackloader.common.ownership;

import mctmods.resourcedatapackloader.common.ownership.claims.ClaimDissolve;
import mctmods.resourcedatapackloader.common.rubic.world.interfaces.ICube;
import mctmods.resourcedatapackloader.common.util.world.PlacePositions;
import mctmods.resourcedatapackloader.common.util.worldgen.GenHeights;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public final class ContentOwnership {
    private ContentOwnership() {}

    @SubscribeEvent(priority = EventPriority.LOWEST) public static void onPlace(BlockEvent.EntityPlaceEvent event) {
        World world = event.getWorld();
        if (!OwnershipSettings.any() || world.isRemote) { return; }
        for (BlockPos pos : PlacePositions.of(event)) { place(world, pos, event.getEntity()); }
    }

    private static void place(World world, BlockPos pos, Entity entity) {
        if (entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entity;
            Block block = world.getBlockState(pos).getBlock();
            if (OwnershipSettings.tags(block)) {
                int handle = OwnerTable.handleOf(player, OwnershipSettings.who());
                if (handle != 0) {
                    OwnedBlockLookup.tag(world, pos, OwnedBlockLookup.nameOf(block), handle);
                    return;
                }
            }
        }
        OwnedBlockLookup.clear(world, pos);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST) public static void onBreak(BlockEvent.BreakEvent event) {
        if (event.isCanceled() || !OwnershipSettings.any()) { return; }
        OwnedBlockLookup.clear(event.getWorld(), event.getPos());
    }

    @SubscribeEvent(priority = EventPriority.LOWEST) public static void onDetonate(ExplosionEvent.Detonate event) {
        if (!OwnershipSettings.any()) { return; }
        for (BlockPos pos : event.getAffectedBlocks()) { OwnedBlockLookup.clear(event.getWorld(), pos); }
    }

    @SubscribeEvent public static void onChunkLoad(ChunkEvent.Load event) {
        World world = event.getWorld();
        if (world.isRemote || GenHeights.rubic(world)) { return; }
        Chunk chunk = event.getChunk();
        OwnedBlocks store = OwnedBlocksProvider.of(chunk);
        if (store != null && !store.entries().isEmpty()) { DissolvePass.onLoad(world, store, chunk, null); }
    }

    public static void onCubeLoad(ICube cube) {
        ClaimDissolve.onLoad(cube.getWorld(), cube.getCoords().getX(), cube.getCoords().getZ());
        OwnedBlocks store = OwnedBlocksProvider.of(cube);
        if (store != null && !store.entries().isEmpty()) { DissolvePass.onLoad(cube.getWorld(), store, null, cube); }
    }

    @SubscribeEvent public static void onTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) { return; }
        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        TeamDissolve.tick(server);
        DissolvePass.run(server);
        OwnershipSweep.tick(server);
    }

    @SubscribeEvent public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) { TeamDissolve.onLogin((EntityPlayerMP) event.player); }

    @SubscribeEvent public static void onCommand(CommandEvent event) {
        String name = event.getCommand().getName();
        if (!"team".equals(name) && !"scoreboard".equals(name)) { return; }
        MinecraftServer server = event.getSender().getServer();
        if (server != null) { TeamDissolve.check(server); }
    }

    @SubscribeEvent public static void onWorldLoad(WorldEvent.Load event) {
        World world = event.getWorld();
        if (world.isRemote || world.provider.getDimension() != 0) { return; }
        MinecraftServer server = world.getMinecraftServer();
        if (server != null) { TeamDissolve.check(server); }
    }
}
