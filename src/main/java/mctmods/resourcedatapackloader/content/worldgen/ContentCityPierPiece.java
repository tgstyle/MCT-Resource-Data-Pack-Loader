package mctmods.resourcedatapackloader.content.worldgen;

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
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class ContentCityPierPiece extends StructurePiece implements PieceBeardifierModifier {
    public static final StructurePieceType TYPE = (StructurePieceType.ContextlessType) ContentCityPierPiece::new;
    public static final String RAILED = "railed";
    public static final String PILINGS = "pilings";
    public static final String BOARDWALK = "boardwalk";
    private static final int CLEAR = 4;
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
        super(TYPE, 0, new BoundingBox(fromX, level, fromZ, toX, level + CLEAR, toZ));
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

    private void laid(@Nonnull WorldGenLevel level, @Nonnull BoundingBox box) {
        CityCross cross = CityCross.of(width, false);
        BlockState deck = stateOr(ContentCity.bridgeBlock(), stateOr(ContentCity.paving(), Blocks.OAK_PLANKS.defaultBlockState()));
        BlockState rail = block(ContentCity.bridgeBarrierBlock());
        BlockState piling = stateOr(ContentCity.supportBlock(), Blocks.OAK_LOG.defaultBlockState());
        BlockState air = Blocks.AIR.defaultBlockState();
        int half = BOARDWALK.equals(style) ? cross.core() : cross.curb();
        BoundingBox held = getBoundingBox();
        ContentCityTrees.fellAround(level, held, box, this.level - 1, this.level + CLEAR, 2);
        int first = alongX ? Math.max(held.minX(), box.minX()) : Math.max(held.minZ(), box.minZ());
        int last = alongX ? Math.min(held.maxX(), box.maxX()) : Math.min(held.maxZ(), box.maxZ());
        BlockPos.MutableBlockPos at = new BlockPos.MutableBlockPos();
        for (int along = first; along <= last; along++) {
            for (int across = middle - half; across <= middle + half; across++) {
                at.set(alongX ? along : across, this.level, alongX ? across : along);
                if (box.isInside(at)) { level.setBlock(at, deck, 2); }
                for (int up = 1; up <= CLEAR; up++) {
                    at.set(alongX ? along : across, this.level + up, alongX ? across : along);
                    if (box.isInside(at) && !level.getBlockState(at).isAir()) { level.setBlock(at, air, 2); }
                }
            }
            boolean posted = Math.floorMod(along, PILING_RUN) == 0;
            for (int side = -1; side <= 1; side += 2) {
                int across = middle + side * half;
                if (rail != null && (!PILINGS.equals(style) || posted)) {
                    at.set(alongX ? along : across, this.level + 1, alongX ? across : along);
                    if (box.isInside(at)) { level.setBlock(at, rail, 2); }
                }
                if (!posted) { continue; }
                for (int down = this.level - 1; down > level.getMinBuildHeight(); down--) {
                    at.set(alongX ? along : across, down, alongX ? across : along);
                    if (!box.isInside(at)) { break; }
                    BlockState under = level.getBlockState(at);
                    if (!under.isAir() && under.getFluidState().isEmpty()) { break; }
                    level.setBlock(at, piling, 2);
                }
            }
            if (rail != null && RAILED.equals(style) && along == head) {
                for (int across = middle - half; across <= middle + half; across++) {
                    at.set(alongX ? along : across, this.level + 1, alongX ? across : along);
                    if (box.isInside(at)) { level.setBlock(at, rail, 2); }
                }
            }
            cargo(level, box, along, half, at);
        }
    }

    private void cargo(WorldGenLevel level, BoundingBox box, int along, int half, BlockPos.MutableBlockPos at) {
        if (Math.floorMod(along, CARGO_RUN) != 0 || along == head || half < 2) { return; }
        for (int side = -1; side <= 1; side += 2) {
            int across = middle + side * (half - 1);
            RandomSource roll = RandomSource.create(ContentCityBlocks.spot(alongX ? along : across, alongX ? across : along));
            ContentCity.Cargo stood = ContentCity.pierCargo(roll);
            if (stood == null) { continue; }
            BlockState laid = block(stood.name());
            if (laid == null) { continue; }
            for (int up = 1; up <= stood.height(); up++) {
                at.set(alongX ? along : across, this.level + up, alongX ? across : along);
                if (!box.isInside(at) || !level.getBlockState(at).isAir()) { return; }
            }
            for (int up = 1; up <= stood.height(); up++) {
                at.set(alongX ? along : across, this.level + up, alongX ? across : along);
                if (!box.isInside(at)) { break; }
                level.setBlock(at, laid, 2);
                fill(level, at, roll);
            }
        }
    }

    private static void fill(WorldGenLevel level, BlockPos at, RandomSource roll) {
        String named = ContentCity.pierLoot();
        if (named.isEmpty()) { return; }
        ResourceLocation table = ResourceLocation.tryParse(named);
        if (table == null) { return; }
        BlockEntity found = level.getBlockEntity(at);
        if (found instanceof RandomizableContainerBlockEntity container) { container.setLootTable(table, roll.nextLong()); }
    }

    @Nullable private static BlockState block(String named) {
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

    @Override @Nonnull public TerrainAdjustment getTerrainAdjustment() { return TerrainAdjustment.NONE; }

    @Override public int getGroundLevelDelta() { return 0; }

    @Override public void postProcess(@Nonnull WorldGenLevel level, @Nonnull StructureManager manager, @Nonnull ChunkGenerator generator, @Nonnull RandomSource random, @Nonnull BoundingBox box, @Nonnull ChunkPos chunk, @Nonnull BlockPos pos) {
        CityBiome.enter(level, (box.minX() + box.maxX()) / 2, (box.minZ() + box.maxZ()) / 2);
        try { laid(level, box); }
        finally { CityBiome.leave(); }
    }
}
