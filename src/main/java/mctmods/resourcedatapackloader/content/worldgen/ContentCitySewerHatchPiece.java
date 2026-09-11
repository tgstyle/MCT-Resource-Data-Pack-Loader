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
        super(TYPE, 0, new BoundingBox(shaftX - 1, level - ContentCity.sewerDepth(), shaftZ - 1, shaftX + 1, level, shaftZ + 1));
        this.level = level;
        this.shaftX = shaftX;
        this.shaftZ = shaftZ;
    }

    public ContentCitySewerHatchPiece(CompoundTag tag) {
        super(TYPE, tag);
        this.level = tag.getInt(LEVEL);
        this.shaftX = tag.getInt(SHAFT_X);
        this.shaftZ = tag.getInt(SHAFT_Z);
    }

    @Override protected void addAdditionalSaveData(@Nonnull StructurePieceSerializationContext context, @Nonnull CompoundTag tag) {
        tag.putInt(LEVEL, level);
        tag.putInt(SHAFT_X, shaftX);
        tag.putInt(SHAFT_Z, shaftZ);
    }

    @Override public void postProcess(@Nonnull WorldGenLevel level, @Nonnull StructureManager manager, @Nonnull ChunkGenerator generator, @Nonnull RandomSource random, @Nonnull BoundingBox box, @Nonnull ChunkPos chunk, @Nonnull BlockPos pos) {
        CityBiome.enter(level, shaftX, shaftZ);
        try {
            BlockState lining = ContentCitySewerPiece.block(ContentCity.sewerBlock());
            if (lining == null) { return; }
            int floor = this.level - ContentCity.sewerDepth();
            int height = ContentCity.sewerHeight();
            if (floor < level.getMinBuildHeight() + ContentCitySewerPiece.FLOOR_LEAST || floor + height + ContentCitySewerPiece.CLEAR_UNDER >= this.level) { return; }
            if (ContentCitySewerPiece.shaft(level, box, shaftX, shaftZ, floor, floor + 2 + height, this.level, lining, new BlockPos.MutableBlockPos())) { ContentLog.LOGGER.debug("A manhole is cut at {}, {} from the street at y {} down to the sewer walk", shaftX, shaftZ, this.level); }
        }
        finally { CityBiome.leave(); }
    }
}
