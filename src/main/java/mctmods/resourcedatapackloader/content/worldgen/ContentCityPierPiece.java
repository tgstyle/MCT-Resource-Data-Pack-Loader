package mctmods.resourcedatapackloader.content.worldgen;

import static mctmods.resourcedatapackloader.content.worldgen.CityRails.CLEAR;

import mctmods.resourcedatapackloader.content.ContentStates;
import mctmods.resourcedatapackloader.util.world.SeededRandom;
import mctmods.resourcedatapackloader.util.world.StructureLoot;

import net.minecraftforge.common.world.PieceBeardifierModifier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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

import java.util.List;
import javax.annotation.Nonnull;

public final class ContentCityPierPiece extends StructurePiece implements PieceBeardifierModifier, ContentCityTrees.IFelling {
    public static final StructurePieceType TYPE = (StructurePieceType.ContextlessType) ContentCityPierPiece::new;
    public static final String RAILED = "railed";
    public static final String PILINGS = "pilings";
    public static final String BOARDWALK = "boardwalk";
    private static final int PILING_RUN = 4;
    private static final int CARGO_RUN = 2;
    private static final String LEVEL = "Level";
    private static final String MIDDLE = "Mid";
    private static final String ALONG_X = "AlongX";
    private static final String WIDTH = "Wide";
    private static final String STYLE = "Style";
    private static final String HEAD = "Head";
    private final int level;
    private final int middle;
    private final boolean alongX;
    private final int width;
    private final String style;
    private final int head;

    public ContentCityPierPiece(int fromX, int fromZ, int toX, int toZ, int level, int middle, boolean alongX, int width, String style, int head) {
        super(TYPE, 0, new BoundingBox(fromX, level, fromZ, toX, level + Math.max(CLEAR, ContentCity.bridgeBarrierHeight()), toZ));
        this.level = level;
        this.middle = middle;
        this.alongX = alongX;
        this.width = width;
        this.style = style;
        this.head = head;
    }

    public ContentCityPierPiece(CompoundTag tag) {
        super(TYPE, tag);
        this.level = tag.getInt(LEVEL);
        this.middle = tag.getInt(MIDDLE);
        this.alongX = tag.getBoolean(ALONG_X);
        this.width = tag.getInt(WIDTH);
        this.style = tag.getString(STYLE);
        this.head = tag.getInt(HEAD);
    }

    @Override protected void addAdditionalSaveData(@Nonnull StructurePieceSerializationContext context, @Nonnull CompoundTag tag) {
        tag.putInt(LEVEL, level);
        tag.putInt(MIDDLE, middle);
        tag.putBoolean(ALONG_X, alongX);
        tag.putInt(WIDTH, width);
        tag.putString(STYLE, style);
        tag.putInt(HEAD, head);
    }

    @Override @Nonnull public BoundingBox stood() { return getBoundingBox(); }

    @Override @Nonnull public BoundingBox owned() { return ContentCityTrees.bridge(getBoundingBox(), level, 0); }

    @Override public int fellFloor() { return level - 1; }

    private void laid(@Nonnull WorldGenLevel level, @Nonnull StructureManager manager, @Nonnull ChunkPos chunk, @Nonnull BoundingBox box) {
        CityCross cross = CityCross.of(width, false);
        CityPlan plan = CityPlan.of(CityGround.of(level), CityPlan.districtOf(alongX ? getBoundingBox().minX() : middle, true), CityPlan.districtOf(alongX ? middle : getBoundingBox().minZ(), false));
        BlockState deck = CityPalette.stateOr(ContentCity.bridgeBlock(), ContentCity.planks(plan));
        BlockState rail = CityPalette.state(ContentCity.bridgeBarrierBlock());
        BlockState piling = CityPalette.stateOr(ContentCity.supportBlock(), Blocks.OAK_LOG.defaultBlockState());
        BlockState air = Blocks.AIR.defaultBlockState();
        int half = BOARDWALK.equals(style) ? Math.min(CityPlan.extraWidth() + 2, cross.curb()) : cross.curb();
        int rise = ContentCity.bridgeBarrierHeight();
        BoundingBox held = getBoundingBox();
        ContentCityTrees.fellAround(level, manager, chunk, this, box);
        List<CityRails.Laid> bores = CityRails.subways(CityGround.of(level), held.minX(), held.minZ(), held.maxX(), held.maxZ());
        int first = CityClip.first(box, held, alongX);
        int last = CityClip.last(box, held, alongX);
        BlockPos.MutableBlockPos at = new BlockPos.MutableBlockPos();
        for (int along = first; along <= last; along++) {
            for (int across = middle - half; across <= middle + half; across++) {
                at.set(alongX ? along : across, this.level, alongX ? across : along);
                if (!box.isInside(at) || ContentCityPiece.deckBlocked(level, at, alongX ? along : across, this.level, alongX ? across : along)) { continue; }
                level.setBlock(at, deck, Block.UPDATE_CLIENTS);
                for (int up = 1; up <= CLEAR; up++) {
                    at.set(alongX ? along : across, this.level + up, alongX ? across : along);
                    BlockState over = level.getBlockState(at);
                    if (CityPlotGround.solid(over)) { break; }
                    if (!over.isAir()) { level.setBlock(at, air, Block.UPDATE_CLIENTS); }
                }
            }
            boolean posted = Math.floorMod(along - head, PILING_RUN) == 0;
            for (int side = -1; side <= 1; side += 2) {
                int across = middle + side * half;
                if (rail != null && (!PILINGS.equals(style) || posted)) { raise(level, box, rail, alongX ? along : across, alongX ? across : along, rise, at); }
                if (!posted) { continue; }
                int postX = alongX ? along : across;
                int postZ = alongX ? across : along;
                if (box.isInside(at.set(postX, this.level - 1, postZ))) { CityPlotGround.piling(level, postX, postZ, this.level - 1, piling, CityRails.boreRoof(bores, postX, postZ), at); }
            }
            if (rail != null && along == head) {
                for (int across = middle - half; across <= middle + half; across++) { raise(level, box, rail, alongX ? along : across, alongX ? across : along, rise, at); }
            }
            cargo(level, box, along, half, at);
        }
    }

    private void raise(WorldGenLevel level, BoundingBox box, BlockState rail, int x, int z, int rise, BlockPos.MutableBlockPos at) {
        CityPlotGround.raise(level, box, at, rail, rise, x, this.level, z);
    }

    private void cargo(WorldGenLevel level, BoundingBox box, int along, int half, BlockPos.MutableBlockPos at) {
        if (Math.floorMod(along, CARGO_RUN) != 0 || along == head || half < 2) { return; }
        for (int side = -1; side <= 1; side += 2) {
            int across = middle + side * (half - 1);
            RandomSource roll = RandomSource.create(SeededRandom.spot(level.getSeed(), alongX ? along : across, alongX ? across : along));
            ContentCity.Cargo stood = ContentCity.pierCargo(roll);
            if (stood == null) { continue; }
            BlockState parsed = ContentStates.known(stood.name(), "villagePathPierCargo");
            if (parsed == null) { continue; }
            Direction inward = alongX ? (side < 0 ? Direction.SOUTH : Direction.NORTH) : (side < 0 ? Direction.EAST : Direction.WEST);
            BlockState laid = CityPalette.faced(parsed, inward);
            if (blocked(level, box, across, along, stood.height(), at)) { continue; }
            for (int up = 1; up <= stood.height(); up++) {
                at.set(alongX ? along : across, this.level + up, alongX ? across : along);
                if (!box.isInside(at)) { break; }
                level.setBlock(at, laid, Block.UPDATE_CLIENTS);
                fill(level, at, roll);
            }
        }
    }

    private boolean blocked(WorldGenLevel level, BoundingBox box, int across, int along, int height, BlockPos.MutableBlockPos at) {
        for (int up = 1; up <= height; up++) {
            at.set(alongX ? along : across, this.level + up, alongX ? across : along);
            if (!box.isInside(at) || !level.getBlockState(at).canBeReplaced()) { return true; }
        }
        return false;
    }

    private static void fill(WorldGenLevel level, BlockPos at, RandomSource roll) {
        String named = ContentCity.pierLoot();
        if (named.isEmpty()) { return; }
        ResourceLocation table = ResourceLocation.tryParse(named);
        if (table == null) { return; }
        StructureLoot.stock(level, roll, at, table);
    }

    @Override @Nonnull public BoundingBox getBeardifierBox() { return CityPlotGround.layer(getBoundingBox(), level); }

    @Override @Nonnull public TerrainAdjustment getTerrainAdjustment() { return TerrainAdjustment.NONE; }

    @Override public int getGroundLevelDelta() { return 0; }

    @Override public void postProcess(@Nonnull WorldGenLevel level, @Nonnull StructureManager manager, @Nonnull ChunkGenerator generator, @Nonnull RandomSource random, @Nonnull BoundingBox box, @Nonnull ChunkPos chunk, @Nonnull BlockPos pos) {
        CityBiome.within(level, box, () -> laid(level, manager, chunk, box));
    }
}
