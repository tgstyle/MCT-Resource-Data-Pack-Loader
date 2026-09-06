package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.def.WorldgenDef;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class ContentSpreadPlacement extends PlacementModifier {
    public static final MapCodec<ContentSpreadPlacement> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("entry").forGetter(ContentSpreadPlacement::entry)).apply(instance, ContentSpreadPlacement::new));
    public static final PlacementModifierType<ContentSpreadPlacement> TYPE = () -> CODEC;
    private static final int SNAP_STEPS = 24;
    private final ResourceLocation entry;

    public ContentSpreadPlacement(ResourceLocation entry) { this.entry = entry; }

    public ResourceLocation entry() { return entry; }

    @Override @Nonnull public PlacementModifierType<?> type() { return TYPE; }

    @Override @Nonnull public Stream<BlockPos> getPositions(@Nonnull PlacementContext context, @Nonnull RandomSource random, @Nonnull BlockPos origin) {
        ContentWorldgen.Entry held = ContentWorldgen.entry(entry);
        return held == null ? Stream.empty() : positions(held, context, random, origin);
    }

    public static Stream<BlockPos> positions(ContentWorldgen.Entry held, PlacementContext context, RandomSource random, BlockPos origin) {
        WorldgenDef def = held.def();
        WorldGenLevel level = context.getLevel();
        if (!ContentWorldgen.dimensionAllows(held, level)) { return Stream.empty(); }
        if (!farEnoughFromSpawn(def, level, origin)) { return Stream.empty(); }
        if (def.shape().wholeChunk()) { return Stream.of(origin); }
        int[] pinned = def.shape().pinnedAt();
        if (pinned != null) { return pinnedPosition(context, origin, pinned); }
        int tries = def.attempts().pick(random);
        if (def.shape().rarity() > 0) {
            if (def.shape().perChunk()) { tries = def.shape().rarity(); }
            else if (random.nextInt(def.shape().rarity()) != 0) { return Stream.empty(); }
        }
        RandomSource region = def.spread().isSprawl() ? ContentSpread.regionRandom(level, new ChunkPos(origin)) : random;
        List<BlockPos> found = new ArrayList<>();
        for (int attempt = 0; attempt < tries; attempt++) {
            BlockPos pos = ContentSpread.position(def, context, random, region, origin);
            if (pos == null) { continue; }
            if (!def.snap().isEmpty()) {
                pos = snap(level, pos, WorldgenDef.CEILING.equals(def.snap()), def.snapDepth());
                if (pos == null) { continue; }
            }
            if (!ContentWorldgen.allows(held, level, pos)) { continue; }
            found.add(pos);
        }
        return found.stream();
    }

    private static Stream<BlockPos> pinnedPosition(PlacementContext context, BlockPos origin, int[] pinned) {
        ChunkPos chunk = new ChunkPos(origin);
        if (pinned[0] >> 4 != chunk.x || pinned[1] >> 4 != chunk.z) { return Stream.empty(); }
        return Stream.of(new BlockPos(pinned[0], context.getHeight(Heightmap.Types.MOTION_BLOCKING, pinned[0], pinned[1]), pinned[1]));
    }

    private static boolean farEnoughFromSpawn(WorldgenDef def, WorldGenLevel level, BlockPos origin) {
        if (def.minDistanceFromSpawn() <= 0) { return true; }
        BlockPos spawn = level.getLevel().getSharedSpawnPos();
        double offX = (origin.getX() + 8) - spawn.getX();
        double offZ = (origin.getZ() + 8) - spawn.getZ();
        return offX * offX + offZ * offZ >= (double) def.minDistanceFromSpawn() * def.minDistanceFromSpawn();
    }

    @Nullable private static BlockPos snap(WorldGenLevel level, BlockPos pos, boolean ceiling, int depth) {
        BlockPos at = pos;
        for (int step = 0; step < SNAP_STEPS; step++) {
            if (!ContentPlacer.loaded(level, at)) { return null; }
            boolean airHere = level.isEmptyBlock(at);
            BlockPos against = ceiling ? at.above() : at.below();
            if (airHere && !level.isEmptyBlock(against)) {
                if (!level.getBlockState(against).getFluidState().isEmpty()) { return null; }
                return depth <= 0 ? at : deeper(level, at, ceiling, depth);
            }
            if (ceiling) { at = airHere ? at.above() : at.below(); }
            else { at = airHere ? at.below() : at.above(); }
        }
        return null;
    }

    @Nullable private static BlockPos deeper(WorldGenLevel level, BlockPos at, boolean ceiling, int depth) {
        BlockPos moved = ceiling ? at.above(depth) : at.below(depth);
        if (moved.getY() < level.getMinBuildHeight() || moved.getY() >= level.getMaxBuildHeight() || !ContentPlacer.loaded(level, moved)) { return null; }
        return moved;
    }
}
