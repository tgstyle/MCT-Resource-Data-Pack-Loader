package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.def.SpreadDef;
import mctmods.resourcedatapackloader.content.def.WorldgenDef;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import javax.annotation.Nullable;

public final class ContentSpread {
    private ContentSpread() {}

    public static RandomSource regionRandom(WorldGenLevel level, ChunkPos chunk) {
        RandomSource random = RandomSource.create(level.getSeed());
        long alongX = (random.nextLong() / 2L) * 2L + 1L;
        long alongZ = (random.nextLong() / 2L) * 2L + 1L;
        return RandomSource.create(chunk.x * alongX + chunk.z * alongZ ^ level.getSeed());
    }

    @Nullable public static BlockPos position(WorldgenDef def, PlacementContext context, RandomSource random, RandomSource region, BlockPos origin) {
        SpreadDef spread = def.spread();
        return switch (spread.type()) {
            case SpreadDef.SPRAWL -> sprawl(def, spread, region, origin);
            case SpreadDef.CENTERED -> centered(def, spread, random, origin);
            case SpreadDef.TERRAIN -> terrain(spread, context, random, origin);
            case SpreadDef.CAVERN -> cavern(def, spread, context, random, origin);
            case SpreadDef.SUBMERGED -> submerged(def, context, random, origin);
            default -> even(def, random, origin);
        };
    }

    private static BlockPos even(WorldgenDef def, RandomSource random, BlockPos origin) {
        int span = def.maxHeight() - def.minHeight() + 1;
        return new BlockPos(origin.getX() + random.nextInt(16), def.minHeight() + random.nextInt(span), origin.getZ() + random.nextInt(16));
    }

    private static BlockPos centered(WorldgenDef def, SpreadDef spread, RandomSource random, BlockPos origin) {
        int y = spread.center();
        if (spread.range() > 1) {
            int rolls = Math.max(1, spread.smoothness());
            for (int roll = 0; roll < rolls; roll++) { y += random.nextInt(spread.range()); }
            y = Math.round(y - spread.range() * (rolls * 0.5F));
        }
        return new BlockPos(origin.getX() + random.nextInt(16), Mth.clamp(y, def.minHeight(), def.maxHeight()), origin.getZ() + random.nextInt(16));
    }

    private static BlockPos sprawl(WorldgenDef def, SpreadDef spread, RandomSource region, BlockPos origin) {
        int x = origin.getX() + density(region, spread.veinDiameter(), spread.horizontalDensity());
        int y = def.minHeight() + density(region, spread.veinHeight(), spread.verticalDensity());
        int z = origin.getZ() + density(region, spread.veinDiameter(), spread.horizontalDensity());
        return new BlockPos(x, y, z);
    }

    @Nullable private static BlockPos terrain(SpreadDef spread, PlacementContext context, RandomSource random, BlockPos origin) {
        int x = origin.getX() + random.nextInt(16);
        int z = origin.getZ() + random.nextInt(16);
        int top = context.getHeight(Heightmap.Types.MOTION_BLOCKING, x, z);
        int span = Math.max(1, spread.offsetMax() - spread.offsetMin() + 1);
        int y = top + spread.offsetMin() + random.nextInt(span);
        return y <= context.getMinBuildHeight() ? null : new BlockPos(x, y, z);
    }

    @Nullable private static BlockPos cavern(WorldgenDef def, SpreadDef spread, PlacementContext context, RandomSource random, BlockPos origin) {
        WorldGenLevel level = context.getLevel();
        int x = origin.getX() + random.nextInt(16);
        int z = origin.getZ() + random.nextInt(16);
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int y = highest(def, level); y > def.minHeight(); y--) {
            pos.set(x, y, z);
            if (!level.isEmptyBlock(pos)) { continue; }
            int against = spread.ceiling() ? y + 1 : y - 1;
            pos.set(x, against, z);
            if (level.isEmptyBlock(pos)) { continue; }
            return new BlockPos(x, against, z);
        }
        return null;
    }

    @Nullable private static BlockPos submerged(WorldgenDef def, PlacementContext context, RandomSource random, BlockPos origin) {
        WorldGenLevel level = context.getLevel();
        int x = origin.getX() + random.nextInt(16);
        int z = origin.getZ() + random.nextInt(16);
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int y = highest(def, level); y > def.minHeight(); y--) {
            pos.set(x, y, z);
            if (level.getBlockState(pos).getFluidState().isEmpty()) { continue; }
            pos.set(x, y - 1, z);
            if (!level.getBlockState(pos).getFluidState().isEmpty() || level.isEmptyBlock(pos)) { continue; }
            return new BlockPos(x, y - 1, z);
        }
        return null;
    }

    private static int highest(WorldgenDef def, WorldGenLevel level) { return Math.min(def.maxHeight(), level.getMaxBuildHeight() - 1); }

    private static int density(RandomSource random, int distance, int percent) {
        float scaled = percent * 0.01F * (distance >> 1);
        int rolls = Math.max(1, (int) scaled);
        int bound = Math.max(1, distance / rolls);
        int total = 0;
        for (int roll = 0; roll < rolls; roll++) { total += random.nextInt(bound); }
        return total;
    }
}
