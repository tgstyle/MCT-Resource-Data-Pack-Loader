package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;

import javax.annotation.Nonnull;

public final class ContentCitySewerHatchPiece extends StructurePiece {
    public static final StructurePieceType TYPE = (StructurePieceType.ContextlessType) ContentCitySewerHatchPiece::new;
    private static final String LEVEL = "Level";
    private static final String SHAFT_X = "ShaftX";
    private static final String SHAFT_Z = "ShaftZ";
    private final int level;
    private final int shaftX;
    private final int shaftZ;

    public ContentCitySewerHatchPiece(int shaftX, int shaftZ, int level) {
        super(TYPE, 0, new BoundingBox(shaftX - 1, ContentCitySewerPiece.floorAt(level), shaftZ - 1, shaftX + 1, level, shaftZ + 1));
        this.level = level;
        this.shaftX = shaftX;
        this.shaftZ = shaftZ;
    }

    public ContentCitySewerHatchPiece(CompoundTag tag) {
        super(TYPE, tag);
        this.level = tag.getIntOr(LEVEL, 0);
        this.shaftX = tag.getIntOr(SHAFT_X, 0);
        this.shaftZ = tag.getIntOr(SHAFT_Z, 0);
    }

    @Override protected void addAdditionalSaveData(@Nonnull StructurePieceSerializationContext context, @Nonnull CompoundTag tag) {
        tag.putInt(LEVEL, level);
        tag.putInt(SHAFT_X, shaftX);
        tag.putInt(SHAFT_Z, shaftZ);
    }

    @Override public void postProcess(@Nonnull WorldGenLevel level, @Nonnull StructureManager manager, @Nonnull ChunkGenerator generator, @Nonnull RandomSource random, @Nonnull BoundingBox box, @Nonnull ChunkPos chunk, @Nonnull BlockPos pos) {
        CityBiome.within(level, shaftX, shaftZ, () -> cut(level, box));
    }

    private void cut(WorldGenLevel world, BoundingBox box) {
        BlockState lining = CityPalette.state(ContentCity.sewerBlock());
        if (lining == null) { return; }
        int floor = ContentCitySewerPiece.floorAt(this.level);
        int height = ContentCity.sewerHeight();
        if (ContentCitySewerPiece.cramped(world.getMinY(), this.level)) { return; }
        if (ContentCitySewerPiece.shaft(world, box, CityRails.subways(CityGround.of(world), shaftX - 1, shaftZ - 1, shaftX + 1, shaftZ + 1), shaftX, shaftZ, floor, floor + 2 + height, this.level, lining, new BlockPos.MutableBlockPos())) { ContentLog.LOGGER.debug("A manhole is cut at {}, {} from the street at y {} down to the sewer walk", shaftX, shaftZ, this.level); }
    }
}
