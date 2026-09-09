package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Registered;

import net.minecraftforge.common.world.PieceBeardifierModifier;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;

public final class ContentCityPiece extends StructurePiece implements PieceBeardifierModifier {
    public static final StructurePieceType TYPE = (StructurePieceType.ContextlessType) ContentCityPiece::new;
    private static final int CLEAR = 4;
    private static final String PAVING = "Paving";
    private static final String LEVEL = "Level";
    private static final String MIDDLE = "Mid";
    private static final String ALONG_X = "AlongX";
    private static final String ALLEY = "Alley";
    private static final String WIDTH = "Wide";
    private static final String BRIDGED = "Bridge";
    private static final String BORED = "Bore";
    private final String paving;
    private final int level;
    private final int middle;
    private final boolean alongX;
    private final boolean alley;
    private final int width;
    private final boolean bridged;
    private final boolean bored;

    public ContentCityPiece(int fromX, int fromZ, int toX, int toZ, int level, String paving, int middle, boolean alongX, boolean alley, int width, boolean bridged, boolean bored) {
        super(TYPE, 0, new BoundingBox(fromX - (bored && !alongX ? 1 : 0), level, fromZ - (bored && alongX ? 1 : 0),
                toX + (bored && !alongX ? 1 : 0), level + CLEAR + 1, toZ + (bored && alongX ? 1 : 0)));
        this.paving = paving;
        this.level = level;
        this.middle = middle;
        this.alongX = alongX;
        this.alley = alley;
        this.width = width;
        this.bridged = bridged;
        this.bored = bored;
    }

    public ContentCityPiece(CompoundTag tag) {
        super(TYPE, tag);
        this.paving = tag.getString(PAVING);
        this.level = tag.getInt(LEVEL);
        this.middle = tag.getInt(MIDDLE);
        this.alongX = tag.getBoolean(ALONG_X);
        this.alley = tag.getBoolean(ALLEY);
        this.width = tag.getInt(WIDTH);
        this.bridged = tag.getBoolean(BRIDGED);
        this.bored = tag.getBoolean(BORED);
    }

    @Override protected void addAdditionalSaveData(@Nonnull StructurePieceSerializationContext context, @Nonnull CompoundTag tag) {
        tag.putString(PAVING, paving);
        tag.putInt(LEVEL, level);
        tag.putInt(MIDDLE, middle);
        tag.putBoolean(ALONG_X, alongX);
        tag.putBoolean(ALLEY, alley);
        tag.putInt(WIDTH, width);
        tag.putBoolean(BRIDGED, bridged);
        tag.putBoolean(BORED, bored);
    }

    @Override public void postProcess(@Nonnull WorldGenLevel level, @Nonnull StructureManager manager, @Nonnull ChunkGenerator generator, @Nonnull RandomSource random, @Nonnull BoundingBox box, @Nonnull ChunkPos chunk, @Nonnull BlockPos pos) {
        BlockState road = stateOr(paving, Blocks.DIRT_PATH.defaultBlockState());
        if (bridged) { road = stateOr(ContentCity.bridgeBlock(), road); }
        BlockState edge = stateOr(ContentCity.lineBlock(), road);
        BlockState walk = stateOr(ContentCity.sidewalkBlock(), road);
        if (bridged) { walk = stateOr(ContentCity.bridgeSidewalkBlock(), walk); }
        BlockState centre = stateOr(ContentCity.centerBlock(), road);
        BlockState under = bridged ? null : block(ContentCity.supportBlock());
        BlockState air = Blocks.AIR.defaultBlockState();
        CityCross cross = CityCross.of(width, alley);
        int dash = ContentCity.centerDash();
        BoundingBox held = getBoundingBox();
        int felled = ContentCityTrees.fellAround(level, held, box, this.level - 1, this.level + CLEAR, 2);
        if (felled > 0) { ContentLog.LOGGER.debug("Felled {} tree block(s) before the street at {}, {} was laid", felled, held.minX(), held.minZ()); }
        BlockPos.MutableBlockPos at = new BlockPos.MutableBlockPos();
        for (int x = Math.max(held.minX(), box.minX()); x <= Math.min(held.maxX(), box.maxX()); x++) {
            for (int z = Math.max(held.minZ(), box.minZ()); z <= Math.min(held.maxZ(), box.maxZ()); z++) {
                int across = alongX ? z : x;
                int along = alongX ? x : z;
                int offset = Math.abs(across - middle);
                BlockState laid = switch (cross.role(offset)) {
                    case CityCross.WALK -> walk;
                    case CityCross.LINE -> edge;
                    case CityCross.CORE -> offset == 0 && dashed(along, dash) ? centre : road;
                    default -> road;
                };
                at.set(x, this.level, z);
                level.setBlock(at, laid, 2);
                if (under != null) {
                    at.set(x, this.level - 1, z);
                    level.setBlock(at, under, 2);
                }
                for (int up = 1; up <= CLEAR; up++) {
                    at.set(x, this.level + up, z);
                    if (!level.getBlockState(at).isAir()) { level.setBlock(at, air, 2); }
                }
            }
        }
        if (bridged) { barriers(level, box, cross); }
        if (bridged) { frames(level, box, cross); }
        if (bored) { bore(level, box, cross); }
    }

    private void bore(WorldGenLevel level, BoundingBox box, CityCross cross) {
        BlockState lining = block(ContentCity.tunnelBlock());
        if (lining == null) { return; }
        BlockState lamp = stateOr(ContentCity.tunnelLightBlock(), lining);
        BlockState air = Blocks.AIR.defaultBlockState();
        int run = ContentCity.tunnelLightRun();
        int wall = cross.curb() + 1;
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
                boolean lit = across == middle && Math.floorMod(along, run) == 0;
                level.setBlock(at, lit ? lamp : lining, 2);
            }
            for (int across = middle - cross.curb(); across <= middle + cross.curb(); across++) {
                for (int up = 1; up <= CLEAR; up++) {
                    at.set(alongX ? along : across, this.level + up, alongX ? across : along);
                    if (box.isInside(at)) { level.setBlock(at, air, 2); }
                }
            }
        }
    }

    private void barriers(WorldGenLevel level, BoundingBox box, CityCross cross) {
        BlockState rail = block(ContentCity.bridgeBarrierBlock());
        if (rail == null) { return; }
        int height = ContentCity.bridgeBarrierHeight();
        BoundingBox held = getBoundingBox();
        BlockPos.MutableBlockPos at = new BlockPos.MutableBlockPos();
        for (int side = -1; side <= 1; side += 2) {
            int across = middle + side * cross.curb();
            int from = alongX ? Math.max(held.minX(), box.minX()) : Math.max(held.minZ(), box.minZ());
            int to = alongX ? Math.min(held.maxX(), box.maxX()) : Math.min(held.maxZ(), box.maxZ());
            for (int along = from; along <= to; along++) {
                for (int up = 1; up <= height; up++) {
                    at.set(alongX ? along : across, this.level + up, alongX ? across : along);
                    if (box.isInside(at)) { level.setBlock(at, rail, 2); }
                }
            }
        }
    }

    private void frames(WorldGenLevel level, BoundingBox box, CityCross cross) {
        BlockState post = block(ContentCity.frameBlock());
        if (post == null) { return; }
        BoundingBox held = getBoundingBox();
        int first = alongX ? held.minX() : held.minZ();
        int last = alongX ? held.maxX() : held.maxZ();
        int span = last - first + 1;
        if (span < ContentCity.frameLeast()) { return; }
        BlockState beam = stateOr(ContentCity.frameTopBlock(), post);
        int clear = ContentCity.frameHeight();
        int run = ContentCity.frameRun();
        int middleAlong = (first + last) / 2;
        int frames = 1 + (span - 1) / run;
        BlockPos.MutableBlockPos at = new BlockPos.MutableBlockPos();
        for (int index = 0; index < frames; index++) {
            int along = middleAlong + (index - (frames - 1) / 2) * run;
            if (along < first || along > last) { continue; }
            for (int side = -1; side <= 1; side += 2) {
                int across = middle + side * cross.curb();
                for (int up = 1; up <= clear; up++) {
                    at.set(alongX ? along : across, this.level + up, alongX ? across : along);
                    if (box.isInside(at)) { level.setBlock(at, post, 2); }
                }
            }
            for (int across = middle - cross.curb(); across <= middle + cross.curb(); across++) {
                at.set(alongX ? along : across, this.level + clear + 1, alongX ? across : along);
                if (box.isInside(at)) { level.setBlock(at, beam, 2); }
            }
        }
    }

    private static boolean dashed(int along, int dash) { return dash <= 0 || Math.floorMod(along, dash + 1) != dash; }

    @javax.annotation.Nullable private static BlockState block(String named) {
        if (named.isEmpty()) { return null; }
        Block found = Registered.find(ForgeRegistries.BLOCKS, ResourceLocation.tryParse(named));
        return found == null ? null : found.defaultBlockState();
    }

    private static BlockState stateOr(String named, BlockState fallback) {
        BlockState found = block(named);
        return found == null ? fallback : found;
    }

    @Override @Nonnull public BoundingBox getBeardifierBox() {
        BoundingBox held = getBoundingBox();
        return new BoundingBox(held.minX(), level, held.minZ(), held.maxX(), level, held.maxZ());
    }

    @Override @Nonnull public TerrainAdjustment getTerrainAdjustment() { return bridged || bored ? TerrainAdjustment.NONE : TerrainAdjustment.BEARD_THIN; }

    @Override public int getGroundLevelDelta() { return 0; }
}
