package mctmods.resourcedatapackloader.content.worldgen;


import net.minecraftforge.common.world.PieceBeardifierModifier;
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
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;

import javax.annotation.Nonnull;

public final class ContentCityCapPiece extends StructurePiece implements PieceBeardifierModifier {
    public static final StructurePieceType TYPE = (StructurePieceType.ContextlessType) ContentCityCapPiece::new;
    private static final String LEVEL = "Level";
    private static final String BLOCK = "Block";
    private static final String RISE = "Rise";
    private final int level;
    private final String block;
    private final int rise;

    public ContentCityCapPiece(int fromX, int fromZ, int toX, int toZ, int level, String block, int rise) {
        super(TYPE, 0, new BoundingBox(fromX, level, fromZ, toX, level + rise, toZ));
        this.level = level;
        this.block = block;
        this.rise = rise;
    }

    public ContentCityCapPiece(CompoundTag tag) {
        super(TYPE, tag);
        this.level = tag.getInt(LEVEL);
        this.block = tag.getString(BLOCK);
        this.rise = tag.getInt(RISE);
    }

    @Override protected void addAdditionalSaveData(@Nonnull StructurePieceSerializationContext context, @Nonnull CompoundTag tag) {
        tag.putInt(LEVEL, level);
        tag.putString(BLOCK, block);
        tag.putInt(RISE, rise);
    }

    @Override @Nonnull public BoundingBox getBeardifierBox() { return CityPlotGround.layer(getBoundingBox(), level); }

    @Override @Nonnull public TerrainAdjustment getTerrainAdjustment() { return ContentCity.adaptation(); }

    @Override public int getGroundLevelDelta() { return 1; }

    @Override public void postProcess(@Nonnull WorldGenLevel level, @Nonnull StructureManager manager, @Nonnull ChunkGenerator generator, @Nonnull RandomSource random, @Nonnull BoundingBox box, @Nonnull ChunkPos chunk, @Nonnull BlockPos pos) {
        BlockState state = CityPalette.state(block);
        if (state == null) { return; }
        BoundingBox held = getBoundingBox();
        BlockPos.MutableBlockPos at = new BlockPos.MutableBlockPos();
        for (int x = Math.max(held.minX(), box.minX()); x <= Math.min(held.maxX(), box.maxX()); x++) {
            for (int z = Math.max(held.minZ(), box.minZ()); z <= Math.min(held.maxZ(), box.maxZ()); z++) {
                if (rise == 0) {
                    level.setBlock(at.set(x, this.level, z), state, 2);
                    continue;
                }
                for (int up = 1; up <= rise; up++) {
                    at.set(x, this.level + up, z);
                    if (!level.getBlockState(at).canBeReplaced()) { break; }
                    level.setBlock(at, state, 2);
                }
            }
        }
    }
}
