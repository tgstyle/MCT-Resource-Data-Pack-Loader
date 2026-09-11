package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Registered;

import net.neoforged.neoforge.common.world.PieceBeardifierModifier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class ContentCityRailPiece extends StructurePiece implements PieceBeardifierModifier {
    public static final StructurePieceType TYPE = (StructurePieceType.ContextlessType) ContentCityRailPiece::new;
    public static final int CLEAR = 4;
    private static final int POST_RUN = 6;
    private static final String LEVEL = "Level";
    private static final String MIDDLE = "Mid";
    private static final String ALONG_X = "AlongX";
    private static final String WIDTH = "Wide";
    private static final String BRIDGED = "Trestle";
    private static final String BORED = "Bore";
    private static final String SUBWAY = "Sub";
    private final int level;
    private final int middle;
    private final boolean alongX;
    private final int width;
    private final boolean bridged;
    private final boolean bored;
    private final boolean subway;

    public ContentCityRailPiece(int fromX, int fromZ, int toX, int toZ, int level, int middle, boolean alongX, int width, boolean bridged, boolean bored, boolean subway) {
        super(TYPE, 0, new BoundingBox(fromX - (bored && !alongX ? 1 : 0), level, fromZ - (bored && alongX ? 1 : 0),
                toX + (bored && !alongX ? 1 : 0), level + CLEAR + 1, toZ + (bored && alongX ? 1 : 0)));
        this.level = level;
        this.middle = middle;
        this.alongX = alongX;
        this.width = width;
        this.bridged = bridged;
        this.bored = bored;
        this.subway = subway;
    }

    public ContentCityRailPiece(CompoundTag tag) {
        super(TYPE, tag);
        this.level = tag.getInt(LEVEL);
        this.middle = tag.getInt(MIDDLE);
        this.alongX = tag.getBoolean(ALONG_X);
        this.width = tag.getInt(WIDTH);
        this.bridged = tag.getBoolean(BRIDGED);
        this.bored = tag.getBoolean(BORED);
        this.subway = tag.getBoolean(SUBWAY);
    }

    @Override protected void addAdditionalSaveData(@Nonnull StructurePieceSerializationContext context, @Nonnull CompoundTag tag) {
        tag.putInt(LEVEL, level);
        tag.putInt(MIDDLE, middle);
        tag.putBoolean(ALONG_X, alongX);
        tag.putInt(WIDTH, width);
        tag.putBoolean(BRIDGED, bridged);
        tag.putBoolean(BORED, bored);
        tag.putBoolean(SUBWAY, subway);
    }

    private void laid(@Nonnull WorldGenLevel level, @Nonnull BoundingBox box) {
        BlockState bed = bridged ? stateOr(ContentCity.railDeckBlock(), Blocks.OAK_PLANKS.defaultBlockState()) : stateOr(ContentCity.railBedBlock(subway), Blocks.GRAVEL.defaultBlockState());
        BlockState tie = stateOr(ContentCity.railTieBlock(subway), Blocks.OAK_PLANKS.defaultBlockState());
        BlockState shoulder = stateOr(ContentCity.railShoulderBlock(subway), bed);
        BlockState air = Blocks.AIR.defaultBlockState();
        int shoulderWide = ContentCity.railShoulderWidth(subway);
        int tieRun = ContentCity.railTieRun(subway);
        int half = (width - 1) / 2;
        BoundingBox held = getBoundingBox();
        int felled = ContentCityTrees.fellAround(level, held, box, this.level - 2, this.level + CLEAR, 2);
        if (felled > 0) { ContentLog.LOGGER.debug("Felled {} tree block(s) before the railway at {}, {} was laid", felled, held.minX(), held.minZ()); }
        BlockPos.MutableBlockPos at = new BlockPos.MutableBlockPos();
        int columns = 0;
        for (int x = Math.max(held.minX(), box.minX()); x <= Math.min(held.maxX(), box.maxX()); x++) {
            for (int z = Math.max(held.minZ(), box.minZ()); z <= Math.min(held.maxZ(), box.maxZ()); z++) {
                columns++;
                if (CityBiome.moved(level, x, z)) {
                    bed = bridged ? stateOr(ContentCity.railDeckBlock(), Blocks.OAK_PLANKS.defaultBlockState()) : stateOr(ContentCity.railBedBlock(subway), Blocks.GRAVEL.defaultBlockState());
                    tie = stateOr(ContentCity.railTieBlock(subway), Blocks.OAK_PLANKS.defaultBlockState());
                    shoulder = stateOr(ContentCity.railShoulderBlock(subway), bed);
                }
                int across = alongX ? z : x;
                int along = alongX ? x : z;
                int offset = Math.abs(across - middle);
                BlockState laid = offset > half - shoulderWide ? shoulder : Math.floorMod(along, tieRun) == 0 ? tie : bed;
                at.set(x, this.level, z);
                level.setBlock(at, laid, 2);
                for (int up = 1; up <= CLEAR; up++) {
                    at.set(x, this.level + up, z);
                    BlockState over = level.getBlockState(at);
                    if (ContentCityTrees.clears(level, at, over)) { level.setBlock(at, air, 2); }
                }
            }
        }
        if (bridged) { trestle(level, box, half); }
        if (bored) { bore(level, box, half); }
        tracks(level, box, half - shoulderWide);
        if (columns > 0) { ContentLog.LOGGER.debug("The {} at {}, {} laid {} column(s) of {} at y {} in this chunk{}{}", subway ? "subway" : "railway", held.minX(), held.minZ(), columns, bed, this.level, bridged ? ", on a trestle" : "", bored ? ", bored" : ""); }
    }

    private void trestle(WorldGenLevel level, BoundingBox box, int half) {
        BoundingBox held = getBoundingBox();
        int first = alongX ? Math.max(held.minX(), box.minX()) : Math.max(held.minZ(), box.minZ());
        int last = alongX ? Math.min(held.maxX(), box.maxX()) : Math.min(held.maxZ(), box.maxZ());
        BlockState rail = block(ContentCity.railBarrierBlock());
        BlockState post = stateOr(ContentCity.railSupportBlock(), Blocks.OAK_LOG.defaultBlockState());
        BlockPos.MutableBlockPos at = new BlockPos.MutableBlockPos();
        for (int along = first; along <= last; along++) {
            for (int side = -1; side <= 1; side += 2) {
                int across = middle + side * half;
                if (rail != null) {
                    at.set(alongX ? along : across, this.level + 1, alongX ? across : along);
                    if (box.isInside(at)) { level.setBlock(at, rail, 2); }
                }
                if (post == null || Math.floorMod(along, POST_RUN) != 0) { continue; }
                for (int down = this.level - 1; down > level.getMinBuildHeight(); down--) {
                    at.set(alongX ? along : across, down, alongX ? across : along);
                    if (!box.isInside(at)) { break; }
                    BlockState under = level.getBlockState(at);
                    if (!under.isAir() && under.getFluidState().isEmpty()) { break; }
                    level.setBlock(at, post, 2);
                }
            }
        }
        frames(level, box, half);
    }

    private void frames(WorldGenLevel level, BoundingBox box, int half) {
        BlockState post = block(ContentCity.railFrameBlock());
        if (post == null) { return; }
        BoundingBox held = getBoundingBox();
        int first = alongX ? held.minX() : held.minZ();
        int last = alongX ? held.maxX() : held.maxZ();
        int span = last - first + 1;
        if (span < ContentCity.railFrameLeast()) { return; }
        BlockState beam = stateOr(ContentCity.railFrameTopBlock(), post);
        int clear = ContentCity.railFrameHeight();
        int run = ContentCity.railFrameRun();
        int center = (first + last) / 2;
        int count = 1 + (span - 1) / run;
        BlockPos.MutableBlockPos at = new BlockPos.MutableBlockPos();
        for (int index = 0; index < count; index++) {
            int along = center + (index - (count - 1) / 2) * run;
            if (along < first || along > last) { continue; }
            for (int side = -1; side <= 1; side += 2) {
                int across = middle + side * half;
                for (int up = 1; up <= clear; up++) {
                    at.set(alongX ? along : across, this.level + up, alongX ? across : along);
                    if (box.isInside(at)) { level.setBlock(at, post, 2); }
                }
            }
            for (int across = middle - half; across <= middle + half; across++) {
                at.set(alongX ? along : across, this.level + clear + 1, alongX ? across : along);
                if (box.isInside(at)) { level.setBlock(at, beam, 2); }
            }
        }
    }

    private void bore(WorldGenLevel level, BoundingBox box, int half) {
        BlockState lining = block(ContentCity.railTunnelBlock(subway));
        if (lining == null) { return; }
        BlockState lamp = stateOr(ContentCity.railTunnelLightBlock(subway), lining);
        BlockState air = Blocks.AIR.defaultBlockState();
        int run = ContentCity.railTunnelLightRun(subway);
        int wall = half + 1;
        int roof = this.level + CLEAR + 1;
        BoundingBox held = getBoundingBox();
        int first = alongX ? Math.max(held.minX(), box.minX()) : Math.max(held.minZ(), box.minZ());
        int last = alongX ? Math.min(held.maxX(), box.maxX()) : Math.min(held.maxZ(), box.maxZ());
        BlockPos.MutableBlockPos at = new BlockPos.MutableBlockPos();
        for (int along = first; along <= last; along++) {
            for (int side = -1; side <= 1; side += 2) {
                for (int up = 0; up <= CLEAR; up++) {
                    at.set(alongX ? along : middle + side * wall, this.level + up, alongX ? middle + side * wall : along);
                    if (box.isInside(at)) { level.setBlock(at, lining, 2); }
                }
            }
            for (int across = middle - wall; across <= middle + wall; across++) {
                at.set(alongX ? along : across, roof, alongX ? across : along);
                if (!box.isInside(at)) { continue; }
                level.setBlock(at, across == middle && Math.floorMod(along, run) == 0 ? lamp : lining, 2);
            }
            for (int across = middle - half; across <= middle + half; across++) {
                for (int up = 1; up <= CLEAR; up++) {
                    at.set(alongX ? along : across, this.level + up, alongX ? across : along);
                    if (box.isInside(at)) { level.setBlock(at, air, 2); }
                }
            }
        }
    }

    private void tracks(WorldGenLevel level, BoundingBox box, int bedHalf) {
        BlockState track = stateOr(ContentCity.railBlock(subway), rail(Blocks.RAIL.defaultBlockState()));
        int count = ContentCity.railTracks(subway);
        if (count <= 0) { count = bedHalf * 2 + 1 < 5 ? 1 : 2; }
        int gap = ContentCity.railTrackGap(subway);
        boolean sits = seated(track);
        BlockState power = stateOr(ContentCity.railPowerBlock(subway), rail(Blocks.POWERED_RAIL.defaultBlockState()));
        BlockState base = stateOr(ContentCity.railPowerBase(subway), Blocks.REDSTONE_BLOCK.defaultBlockState());
        int powerRun = ContentCity.railPowerRun(subway);
        BoundingBox held = getBoundingBox();
        BlockPos.MutableBlockPos at = new BlockPos.MutableBlockPos();
        int first = alongX ? Math.max(held.minX(), box.minX()) : Math.max(held.minZ(), box.minZ());
        int last = alongX ? Math.min(held.maxX(), box.maxX()) : Math.min(held.maxZ(), box.maxZ());
        for (int line = 0; line < count; line++) {
            int across = middle + (2 * line - (count - 1)) * gap / 2;
            if (Math.abs(across - middle) > bedHalf) { continue; }
            for (int along = first; along <= last; along++) {
                if (CityBiome.moved(level, alongX ? along : across, alongX ? across : along)) {
                    track = stateOr(ContentCity.railBlock(subway), rail(Blocks.RAIL.defaultBlockState()));
                    power = stateOr(ContentCity.railPowerBlock(subway), rail(Blocks.POWERED_RAIL.defaultBlockState()));
                    base = stateOr(ContentCity.railPowerBase(subway), Blocks.REDSTONE_BLOCK.defaultBlockState());
                }
                boolean powered = powerRun > 0 && Math.floorMod(along, powerRun) == 0;
                BlockState laid = powered ? power : track;
                int y = sits ? this.level + 1 : this.level;
                at.set(alongX ? along : across, y, alongX ? across : along);
                if (!box.isInside(at)) { continue; }
                level.setBlock(at, laid, 2);
                if (!powered || !sits) { continue; }
                at.set(alongX ? along : across, this.level, alongX ? across : along);
                if (box.isInside(at)) { level.setBlock(at, base, 2); }
            }
        }
    }

    private boolean seated(BlockState track) {
        String seat = ContentCity.railTrackSeat(subway);
        if ("on".equals(seat)) { return true; }
        if ("in".equals(seat)) { return false; }
        return track.getBlock() instanceof BaseRailBlock;
    }

    private BlockState rail(BlockState laid) {
        if (!laid.hasProperty(BlockStateProperties.RAIL_SHAPE) && !laid.hasProperty(BlockStateProperties.RAIL_SHAPE_STRAIGHT)) { return laid; }
        RailShape shape = alongX ? RailShape.EAST_WEST : RailShape.NORTH_SOUTH;
        if (laid.hasProperty(BlockStateProperties.RAIL_SHAPE)) { return laid.setValue(BlockStateProperties.RAIL_SHAPE, shape); }
        return laid.setValue(BlockStateProperties.RAIL_SHAPE_STRAIGHT, shape);
    }

    @Nullable private BlockState block(String named) {
        if (named.isEmpty()) { return null; }
        Block found = Registered.find(BuiltInRegistries.BLOCK, ResourceLocation.tryParse(named));
        return found == null ? null : rail(found.defaultBlockState());
    }

    private BlockState stateOr(String named, BlockState fallback) {
        BlockState found = block(named);
        return found == null ? fallback : found;
    }

    @Override @Nonnull public BoundingBox getBeardifierBox() {
        BoundingBox held = getBoundingBox();
        return new BoundingBox(held.minX(), level, held.minZ(), held.maxX(), level, held.maxZ());
    }

    @Override @Nonnull public TerrainAdjustment getTerrainAdjustment() { return bridged || bored ? TerrainAdjustment.NONE : TerrainAdjustment.BEARD_THIN; }

    @Override public int getGroundLevelDelta() { return 0; }

    @Override public void postProcess(@Nonnull WorldGenLevel level, @Nonnull StructureManager manager, @Nonnull ChunkGenerator generator, @Nonnull RandomSource random, @Nonnull BoundingBox box, @Nonnull ChunkPos chunk, @Nonnull BlockPos pos) {
        CityBiome.enter(level, (box.minX() + box.maxX()) / 2, (box.minZ() + box.maxZ()) / 2);
        try { laid(level, box); }
        finally { CityBiome.leave(); }
    }
}
