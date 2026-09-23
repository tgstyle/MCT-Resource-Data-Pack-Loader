package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.mixin.rdpl.common.IStructureManager;
import mctmods.resourcedatapackloader.util.Config;

import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.structure.Structure;
import java.util.List;
import javax.annotation.Nullable;

public final class ContentVoidWorld {
    @Nullable private static volatile VoidMemory remembered;
    private ContentVoidWorld() {}

    public static boolean voidEmpties(StructureManager structures) { return ((IStructureManager) structures).rdpl$level() instanceof WorldGenLevel level && voidApplies(level.getLevel()); }

    static boolean voidAsked() {
        if (ContentControl.off(ContentControl.VOID)) { return false; }
        return ContentWorldShape.blockedToVoid || ContentControl.flag(ContentControl.VOID, "voidWorld", Config.worldgen.voidWorld());
    }

    static List<String> voidDimensions() { return ContentControl.list(ContentControl.VOID, "voidWorldDimensions", Config.worldgen.voidWorldDimensions()); }

    private static boolean voidBlacklist() { return ContentControl.flag(ContentControl.VOID, "voidWorldDimensionsAreBlacklist", Config.worldgen.voidWorldDimensionsAreBlacklist()); }

    static boolean voidApplies(String dimension) { return voidApplies(dimension, voidAsked(), voidDimensions(), voidBlacklist()); }

    static boolean voidApplies(String dimension, boolean asked, List<String> wanted, boolean blacklist) {
        if (!asked) { return false; }
        return wanted.isEmpty() ? blacklist : ContentWorldShape.listed(dimension, wanted, blacklist);
    }

    public static boolean voidApplies(ServerLevel level) {
        String dimension = level.dimension().location().toString();
        VoidMemory memory = remembered;
        if (memory != null && memory.recorded()) { return voidApplies(dimension, memory.enabled(), memory.dimensions(), memory.areBlacklist()); }
        return voidApplies(dimension);
    }

    static void remember(ServerLevel overworld) {
        MinecraftServer server = overworld.getServer();
        VoidMemory memory = VoidMemory.of(server);
        if (!memory.recorded() && !server.getWorldData().overworldData().isInitialized()) { memory.record(voidAsked(), voidDimensions(), voidBlacklist()); }
        remembered = memory;
    }

    public static boolean voidRefuses(LevelAccessor level, Structure structure) { return level instanceof ServerLevel server && !(structure instanceof ContentMapStructure) && voidApplies(server); }

    public static boolean voidRefuses(WorldGenLevel level, PlacedFeature feature) {
        Feature<?> type = feature.feature().value().feature();
        return !(type instanceof ContentShapeFeature || type instanceof ContentCoverFeature || type instanceof ContentCaveStructureFeature) && voidApplies(level.getLevel());
    }

    static void standOn(ServerPlayer player) {
        ServerLevel level = player.serverLevel();
        if (!voidApplies(level)) { return; }
        BlockPos center = platformCenter(level);
        if (level.isEmptyBlock(center)) { platform(level, center); }
        if (player.getY() >= center.getY() + 1 && player.getY() < center.getY() + 2 && Math.abs(player.getX() - (center.getX() + 0.5D)) <= 0.5D && Math.abs(player.getZ() - (center.getZ() + 0.5D)) <= 0.5D) { return; }
        player.teleportTo(center.getX() + 0.5D, center.getY() + 1, center.getZ() + 0.5D);
        player.fallDistance = 0.0F;
        ContentPregenHold.anchored(player);
    }

    static BlockPos platformCenter(ServerLevel level) {
        int asked = ContentControl.number(ContentControl.VOID, "voidPlatformHeight", Config.worldgen.voidPlatformHeight());
        return new BlockPos(0, Mth.clamp(asked, level.getMinBuildHeight() + 1, level.getMaxBuildHeight() - 2), 0);
    }

    static void platform(ServerLevel level, BlockPos center) {
        String name = ContentControl.text(ContentControl.VOID, "voidPlatformBlock", Config.worldgen.voidPlatformBlock());
        Block found = ContentWorldShape.block(name, "voidPlatformBlock");
        BlockState state = found == null ? Blocks.STONE.defaultBlockState() : found.defaultBlockState();
        int reach = Math.max(0, (ContentControl.number(ContentControl.VOID, "voidPlatformSize", Config.worldgen.voidPlatformSize()) - 1) / 2);
        for (int x = -reach; x <= reach; x++) {
            for (int z = -reach; z <= reach; z++) { level.setBlock(center.offset(x, 0, z), state, 2); }
        }
    }
}
