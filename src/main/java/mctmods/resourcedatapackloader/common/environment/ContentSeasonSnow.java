package mctmods.resourcedatapackloader.common.environment;

import mctmods.resourcedatapackloader.common.dimension.ContentEnvironments;
import mctmods.resourcedatapackloader.common.pack.definitions.sky.RuleSwitchDef;
import mctmods.resourcedatapackloader.common.pack.definitions.sky.SnowRuleDef;
import mctmods.resourcedatapackloader.core.util.ContentLog;

import java.util.Collection;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.IceBlock;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.registries.ForgeRegistries;

public final class ContentSeasonSnow {
    private static final int SAMPLES = 16;
    private static final int REACH = 128;

    private ContentSeasonSnow() {}

    public static boolean snowStopped(LevelReader reader, BlockPos pos) { return stopped(ContentEnvironments.snow(reader, pos).place(), ContentWeather.traits(reader).snow()); }

    public static boolean freezeStopped(LevelReader reader, BlockPos pos) { return stopped(ContentEnvironments.snow(reader, pos).freeze(), ContentWeather.traits(reader).freeze()); }

    public static boolean warmForSnow(Biome biome, LevelReader reader, BlockPos pos) { return biome.warmEnoughToRain(pos) && ContentEnvironments.snow(reader, pos).place() != SnowRuleDef.ANY; }

    public static boolean warmForFreeze(Biome biome, LevelReader reader, BlockPos pos) { return biome.warmEnoughToRain(pos) && ContentEnvironments.snow(reader, pos).freeze() != SnowRuleDef.ANY; }

    private static boolean stopped(int mode, boolean trait) {
        return switch (mode) {
            case SnowRuleDef.NEVER -> true;
            case SnowRuleDef.BIOME, SnowRuleDef.ANY -> false;
            default -> !trait;
        };
    }

    public static void check(Collection<RuleSwitchDef> rules) {
        for (RuleSwitchDef rule : rules) {
            for (ResourceLocation id : rule.snow().meltBlocks()) {
                if (!ForgeRegistries.BLOCKS.containsKey(id)) { ContentLog.LOGGER.error("An environment gives rules.snow.meltBlocks the entry {}, which is not a block, ignoring it", id); }
            }
        }
    }

    public static void onLevelTick(TickEvent.LevelTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !(event.level instanceof ServerLevel level) || level.players().isEmpty()) { return; }
        int reach = Math.min(REACH, level.getServer().getPlayerList().getViewDistance() * 16);
        for (ServerPlayer player : level.players()) {
            if (player.isSpectator()) { continue; }
            for (int sample = 0; sample < SAMPLES; sample++) { sample(level, player, reach); }
        }
    }

    private static void sample(ServerLevel level, ServerPlayer player, int reach) {
        int dx = level.getRandom().nextInt(reach * 2 + 1) - reach;
        int dz = level.getRandom().nextInt(reach * 2 + 1) - reach;
        if (dx * dx + dz * dz > reach * reach) { return; }
        BlockPos near = new BlockPos(player.getBlockX() + dx, level.getMinBuildHeight(), player.getBlockZ() + dz);
        if (!level.isLoaded(near)) { return; }
        BlockPos top = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, near);
        SnowRuleDef snow = ContentEnvironments.snow(level, top);
        if (snow.melt()) { melt(level, top, snow); }
        else if (snow.whenDry() && !level.isRaining()) { place(level, top); }
    }

    private static void place(ServerLevel level, BlockPos top) {
        if (level.getBlockState(top).isAir() && level.getBiome(top).value().shouldSnow(level, top)) { level.setBlockAndUpdate(top, Blocks.SNOW.defaultBlockState()); }
    }

    private static void melt(ServerLevel level, BlockPos top, SnowRuleDef snow) {
        if (level.getRandom().nextFloat() >= snow.meltChance() || meltAt(level, top, snow)) { return; }
        meltAt(level, top.below(), snow);
    }

    private static boolean meltAt(ServerLevel level, BlockPos pos, SnowRuleDef snow) {
        BlockState state = level.getBlockState(pos);
        if (!snow.meltBlocks().contains(ForgeRegistries.BLOCKS.getKey(state.getBlock()))) { return false; }
        if (state.hasProperty(SnowLayerBlock.LAYERS) && state.getValue(SnowLayerBlock.LAYERS) > 1) { level.setBlockAndUpdate(pos, state.setValue(SnowLayerBlock.LAYERS, state.getValue(SnowLayerBlock.LAYERS) - 1)); }
        else if (state.getBlock() instanceof IceBlock && !level.dimensionType().ultraWarm()) {
            level.setBlockAndUpdate(pos, Blocks.WATER.defaultBlockState());
            level.neighborChanged(pos, Blocks.WATER, pos);
        }
        else { level.removeBlock(pos, false); }
        return true;
    }
}
