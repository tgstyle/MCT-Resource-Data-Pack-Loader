package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.ContentStates;

import net.neoforged.neoforge.common.world.PieceBeardifierModifier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;

import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class ContentCityPierPiece extends StructurePiece implements PieceBeardifierModifier, ContentCityTrees.Felling {
    public static final StructurePieceType TYPE = (StructurePieceType.ContextlessType) ContentCityPierPiece::new;
    public static final String RAILED = "railed";
    public static final String PILINGS = "pilings";
    public static final String BOARDWALK = "boardwalk";
    private static final int CLEAR = 4;
    private static final int PILING_RUN = 4;
    private static final int CARGO_RUN = 2;
    private static final int PILING_REACH = 24;
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
        BlockState deck = stateOr(ContentCity.bridgeBlock(), ContentCity.planks(plan));
        BlockState rail = block(ContentCity.bridgeBarrierBlock());
        BlockState piling = stateOr(ContentCity.supportBlock(), Blocks.OAK_LOG.defaultBlockState());
        BlockState air = Blocks.AIR.defaultBlockState();
        int half = BOARDWALK.equals(style) ? Math.min(CityPlan.extraWidth() + 2, cross.curb()) : cross.curb();
        int rise = ContentCity.bridgeBarrierHeight();
        BoundingBox held = getBoundingBox();
        ContentCityTrees.fellAround(level, manager, chunk, this, box);
        List<CityRails.Laid> bores = CityRails.subways(CityGround.of(level), held.minX(), held.minZ(), held.maxX(), held.maxZ());
        int first = alongX ? Math.max(held.minX(), box.minX()) : Math.max(held.minZ(), box.minZ());
        int last = alongX ? Math.min(held.maxX(), box.maxX()) : Math.min(held.maxZ(), box.maxZ());
        BlockPos.MutableBlockPos at = new BlockPos.MutableBlockPos();
        for (int along = first; along <= last; along++) {
            for (int across = middle - half; across <= middle + half; across++) {
                at.set(alongX ? along : across, this.level, alongX ? across : along);
                if (!box.isInside(at) || ContentCityPiece.deckBlocked(level, at, alongX ? along : across, this.level, alongX ? across : along)) { continue; }
                level.setBlock(at, deck, 2);
                for (int up = 1; up <= CLEAR; up++) {
                    at.set(alongX ? along : across, this.level + up, alongX ? across : along);
                    BlockState over = level.getBlockState(at);
                    if (CityPlotGround.solid(over)) { break; }
                    if (!over.isAir()) { level.setBlock(at, air, 2); }
                }
            }
            boolean posted = Math.floorMod(along - head, PILING_RUN) == 0;
            for (int side = -1; side <= 1; side += 2) {
                int across = middle + side * half;
                if (rail != null && (!PILINGS.equals(style) || posted)) { raise(level, box, rail, alongX ? along : across, alongX ? across : along, rise, at); }
                if (!posted) { continue; }
                int roof = CityRails.boreRoof(bores, alongX ? along : across, alongX ? across : along);
                int floor = Math.max(this.level - 1 - PILING_REACH, roof == Integer.MIN_VALUE ? level.getMinBuildHeight() + 1 : roof + 1);
                for (int down = this.level - 1; down >= floor; down--) {
                    at.set(alongX ? along : across, down, alongX ? across : along);
                    if (!box.isInside(at)) { break; }
                    BlockState under = level.getBlockState(at);
                    if (!under.isAir() && under.getFluidState().isEmpty()) { break; }
                    level.setBlock(at, piling, 2);
                }
            }
            if (rail != null && along == head) {
                for (int across = middle - half; across <= middle + half; across++) { raise(level, box, rail, alongX ? along : across, alongX ? across : along, rise, at); }
            }
            cargo(level, box, along, half, at);
        }
    }

    private void raise(WorldGenLevel level, BoundingBox box, BlockState rail, int x, int z, int rise, BlockPos.MutableBlockPos at) {
        for (int up = 1; up <= rise; up++) {
            at.set(x, this.level + up, z);
            if (!box.isInside(at) || CityPlotGround.solid(level.getBlockState(at))) { break; }
            level.setBlock(at, rail, 2);
        }
    }

    private void cargo(WorldGenLevel level, BoundingBox box, int along, int half, BlockPos.MutableBlockPos at) {
        if (Math.floorMod(along, CARGO_RUN) != 0 || along == head || half < 2) { return; }
        for (int side = -1; side <= 1; side += 2) {
            int across = middle + side * (half - 1);
            RandomSource roll = RandomSource.create(ContentCityBlocks.spot(level.getSeed(), alongX ? along : across, alongX ? across : along));
            ContentCity.Cargo stood = ContentCity.pierCargo(roll);
            if (stood == null) { continue; }
            BlockState parsed = ContentStates.known(stood.name(), "villagePathPierCargo");
            if (parsed == null) { continue; }
            Direction inward = alongX ? (side < 0 ? Direction.SOUTH : Direction.NORTH) : (side < 0 ? Direction.EAST : Direction.WEST);
            BlockState laid = parsed.hasProperty(BlockStateProperties.HORIZONTAL_FACING) ? parsed.setValue(BlockStateProperties.HORIZONTAL_FACING, inward) : parsed.hasProperty(BlockStateProperties.FACING) ? parsed.setValue(BlockStateProperties.FACING, inward) : parsed;
            if (blocked(level, box, across, along, stood.height(), at)) { continue; }
            for (int up = 1; up <= stood.height(); up++) {
                at.set(alongX ? along : across, this.level + up, alongX ? across : along);
                if (!box.isInside(at)) { break; }
                level.setBlock(at, laid, 2);
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
        BlockEntity found = level.getBlockEntity(at);
        if (!(found instanceof RandomizableContainerBlockEntity container)) { return; }
        container.setLootTable(ResourceKey.create(Registries.LOOT_TABLE, table));
        container.setLootTableSeed(roll.nextLong());
    }

    @Nullable private static BlockState block(String named) { return CityPalette.state(named); }

    private static BlockState stateOr(String named, BlockState fallback) { return CityPalette.stateOr(named, fallback); }

    @Override @Nonnull public BoundingBox getBeardifierBox() { return CityPlotGround.layer(getBoundingBox(), level); }

    @Override @Nonnull public TerrainAdjustment getTerrainAdjustment() { return TerrainAdjustment.NONE; }

    @Override public int getGroundLevelDelta() { return 0; }

    @Override public void postProcess(@Nonnull WorldGenLevel level, @Nonnull StructureManager manager, @Nonnull ChunkGenerator generator, @Nonnull RandomSource random, @Nonnull BoundingBox box, @Nonnull ChunkPos chunk, @Nonnull BlockPos pos) {
        CityBiome.within(level, box, () -> laid(level, manager, chunk, box));
    }
}
