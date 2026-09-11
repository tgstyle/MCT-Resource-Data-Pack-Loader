package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.util.ContentLog;

import net.neoforged.neoforge.common.world.PieceBeardifierModifier;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.TemplateStructurePiece;
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

import javax.annotation.Nonnull;

public final class ContentCityEntrancePiece extends TemplateStructurePiece implements PieceBeardifierModifier {
    public static final StructurePieceType TYPE = (StructurePieceType.StructureTemplateType) ContentCityEntrancePiece::new;
    private static final String ROW = "Row";
    private static final String NEAR = "Near";
    private static final String TOP = "Top";
    private static final String ALONG_X = "AlongX";
    private final int row;
    private final int near;
    private final int top;
    private final boolean alongX;

    public ContentCityEntrancePiece(StructureTemplateManager manager, ResourceLocation template, BlockPos corner, int row, int near, int top, boolean alongX) {
        super(TYPE, 0, manager, template, template.toString(), settings(), corner);
        this.row = row;
        this.near = near;
        this.top = top;
        this.alongX = alongX;
    }

    public ContentCityEntrancePiece(StructureTemplateManager manager, CompoundTag tag) {
        super(TYPE, tag, manager, held -> settings());
        this.row = tag.getInt(ROW);
        this.near = tag.getInt(NEAR);
        this.top = tag.getInt(TOP);
        this.alongX = tag.getBoolean(ALONG_X);
    }

    private static StructurePlaceSettings settings() {
        StructurePlaceSettings settings = new StructurePlaceSettings().setRotation(Rotation.NONE).setMirror(Mirror.NONE);
        settings.addProcessor(new ContentCityBlocks(100));
        return settings;
    }

    @Override protected void addAdditionalSaveData(@Nonnull StructurePieceSerializationContext context, @Nonnull CompoundTag tag) {
        super.addAdditionalSaveData(context, tag);
        tag.putInt(ROW, row);
        tag.putInt(NEAR, near);
        tag.putInt(TOP, top);
        tag.putBoolean(ALONG_X, alongX);
    }

    private void laid(@Nonnull WorldGenLevel level, @Nonnull StructureManager manager, @Nonnull ChunkGenerator generator, @Nonnull RandomSource random, @Nonnull BoundingBox box, @Nonnull ChunkPos chunk, @Nonnull BlockPos pos) {
        BoundingBox held = getBoundingBox();
        int felled = ContentCityTrees.fellAround(level, held, box, held.minY() - 1, held.maxY(), 2);
        if (felled > 0) { ContentLog.LOGGER.debug("Felled {} tree block(s) before the subway entrance at {}, {} was stamped", felled, held.minX(), held.minZ()); }
        super.postProcess(level, manager, generator, random, box, chunk, pos);
        BlockState air = Blocks.AIR.defaultBlockState();
        BlockPos.MutableBlockPos at = new BlockPos.MutableBlockPos();
        int cut = 0;
        for (int along = 0; along <= ContentCityStairsPiece.RUN; along++) {
            for (int lane = 0; lane < ContentCityStairsPiece.WIDE; lane++) {
                if (lane == ContentCityStairsPiece.WIDE / 2) { continue; }
                int x = alongX ? row + along : near + lane;
                int z = alongX ? near + lane : row + along;
                if (x <= held.minX() || x >= held.maxX() || z <= held.minZ() || z >= held.maxZ()) { continue; }
                for (int y = top + 1; y <= held.maxY(); y++) {
                    if (!box.isInside(at.set(x, y, z))) { continue; }
                    level.setBlock(at, air, 2);
                    cut++;
                }
            }
        }
        if (cut > 0) { ContentLog.LOGGER.info("A subway entrance at {}, {} opened {} block(s) of its floor over the stairs under it, keeping its outer ring and the stairwell divider", held.minX(), held.minZ(), cut); }
    }

    @Override protected void handleDataMarker(@Nonnull String name, @Nonnull BlockPos pos, @Nonnull ServerLevelAccessor level, @Nonnull RandomSource random, @Nonnull BoundingBox box) {}

    @Override @Nonnull public BoundingBox getBeardifierBox() { return getBoundingBox(); }

    @Override @Nonnull public TerrainAdjustment getTerrainAdjustment() { return TerrainAdjustment.BEARD_THIN; }

    @Override public int getGroundLevelDelta() { return 0; }

    @Override public void postProcess(@Nonnull WorldGenLevel level, @Nonnull StructureManager manager, @Nonnull ChunkGenerator generator, @Nonnull RandomSource random, @Nonnull BoundingBox box, @Nonnull ChunkPos chunk, @Nonnull BlockPos pos) {
        CityBiome.enter(level, (box.minX() + box.maxX()) / 2, (box.minZ() + box.maxZ()) / 2);
        try { laid(level, manager, generator, random, box, chunk, pos); }
        finally { CityBiome.leave(); }
    }
}
