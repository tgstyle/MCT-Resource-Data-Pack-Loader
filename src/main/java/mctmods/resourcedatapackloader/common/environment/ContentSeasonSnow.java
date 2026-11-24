package mctmods.resourcedatapackloader.common.environment;

import mctmods.resourcedatapackloader.common.dimension.ContentEnvironments;
import mctmods.resourcedatapackloader.common.pack.definitions.sky.SnowRuleDef;

import net.minecraft.block.Block;
import net.minecraft.block.BlockIce;
import net.minecraft.block.BlockSnow;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import mctmods.resourcedatapackloader.core.util.SideUtils;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public final class ContentSeasonSnow {
    private static final int SAMPLES = 16;
    private static final int REACH = 128;

    private ContentSeasonSnow() {}

    @SubscribeEvent public static void onWorldTick(TickEvent.WorldTickEvent event) {
        World world = event.world;
        if (!SideUtils.serverEnd(event) || world.playerEntities.isEmpty()) { return; }
        int reach = reach(world);
        for (EntityPlayer player : world.playerEntities) {
            if (player.isSpectator()) { continue; }
            for (int sample = 0; sample < SAMPLES; sample++) { sample(world, player, reach); }
        }
    }

    private static int reach(World world) {
        MinecraftServer server = world.getMinecraftServer();
        return server == null ? REACH : Math.min(REACH, server.getPlayerList().getViewDistance() * 16);
    }

    private static void sample(World world, EntityPlayer player, int reach) {
        int dx = world.rand.nextInt(reach * 2 + 1) - reach;
        int dz = world.rand.nextInt(reach * 2 + 1) - reach;
        if (dx * dx + dz * dz > reach * reach) { return; }
        BlockPos near = new BlockPos(MathHelper.floor(player.posX) + dx, MathHelper.floor(player.posY), MathHelper.floor(player.posZ) + dz);
        if (!world.isBlockLoaded(near)) { return; }
        SnowRuleDef snow = ContentEnvironments.rules(world, near).snow;
        if (snow.melt) { melt(world, world.getPrecipitationHeight(near), snow); }
        else if (snow.whenDry && !world.isRaining()) { place(world, world.getPrecipitationHeight(near)); }
    }

    private static void place(World world, BlockPos top) {
        if (world.isBlockLoaded(top) && world.canSnowAt(top, true)) { world.setBlockState(top, Blocks.SNOW_LAYER.getDefaultState()); }
    }

    private static void melt(World world, BlockPos top, SnowRuleDef snow) {
        if (world.rand.nextFloat() >= snow.meltChance || meltAt(world, top, snow)) { return; }
        meltAt(world, top.down(), snow);
    }

    private static boolean meltAt(World world, BlockPos pos, SnowRuleDef snow) {
        if (!world.isBlockLoaded(pos)) { return false; }
        IBlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        if (!snow.meltBlocks.contains(block)) { return false; }
        if (state.getPropertyKeys().contains(BlockSnow.LAYERS) && state.getValue(BlockSnow.LAYERS) > 1) { world.setBlockState(pos, state.withProperty(BlockSnow.LAYERS, state.getValue(BlockSnow.LAYERS) - 1)); }
        else if (block instanceof BlockIce && !world.provider.doesWaterVaporize()) {
            world.setBlockState(pos, Blocks.WATER.getDefaultState());
            world.neighborChanged(pos, Blocks.WATER, pos);
        }
        else { world.setBlockToAir(pos); }
        return true;
    }
}
