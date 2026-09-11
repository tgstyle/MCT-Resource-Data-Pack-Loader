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

public final class
ContentCityBulbPiece extends StructurePiece implements PieceBeardifierModifier {
    public static final StructurePieceType TYPE = (StructurePieceType.ContextlessType) ContentCityBulbPiece::new;
    private static final int CLEAR = 4;
    private static final String LEVEL = "Level";
    private static final String REACH = "Reach";
    private static final String PAVING = "Paving";
    private static final String WALK = "Walk";
    private final int level;
    private final int reach;
    private final String paving;
    private final String walk;

    public ContentCityBulbPiece(int centerX, int centerZ, int level, int reach, String paving, String walk) {
        super(TYPE, 0, new BoundingBox(centerX - reach, level, centerZ - reach, centerX + reach, level + CLEAR, centerZ + reach));
        this.level = level;
        this.reach = reach;
        this.paving = paving;
        this.walk = walk;
    }

    public ContentCityBulbPiece(CompoundTag tag) {
        super(TYPE, tag);
        this.level = tag.getInt(LEVEL);
        this.reach = tag.getInt(REACH);
        this.paving = tag.getString(PAVING);
        this.walk = tag.getString(WALK);
    }

    @Override protected void addAdditionalSaveData(@Nonnull StructurePieceSerializationContext context, @Nonnull CompoundTag tag) {
        tag.putInt(LEVEL, level);
        tag.putInt(REACH, reach);
        tag.putString(PAVING, paving);
        tag.putString(WALK, walk);
    }

    private void laid(@Nonnull WorldGenLevel level, @Nonnull BoundingBox box) {
        BlockState road = stateOr(paving, Blocks.DIRT_PATH.defaultBlockState());
        BlockState curb = stateOr(walk, road);
        BlockState air = Blocks.AIR.defaultBlockState();
        BoundingBox held = getBoundingBox();
        ContentCityTrees.fellAround(level, held, box, this.level - 1, this.level + CLEAR, 2);
        int centerX = (held.minX() + held.maxX()) / 2;
        int centerZ = (held.minZ() + held.maxZ()) / 2;
        int inner = Math.max(1, reach - 1);
        BlockPos.MutableBlockPos at = new BlockPos.MutableBlockPos();
        for (int x = Math.max(held.minX(), box.minX()); x <= Math.min(held.maxX(), box.maxX()); x++) {
            for (int z = Math.max(held.minZ(), box.minZ()); z <= Math.min(held.maxZ(), box.maxZ()); z++) {
                int away = (x - centerX) * (x - centerX) + (z - centerZ) * (z - centerZ);
                if (away > reach * reach + reach) { continue; }
                at.set(x, this.level, z);
                level.setBlock(at, away > inner * inner + inner ? curb : road, 2);
                for (int up = 1; up <= CLEAR; up++) {
                    at.set(x, this.level + up, z);
                    if (!level.getBlockState(at).isAir()) { level.setBlock(at, air, 2); }
                }
            }
        }
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

    @Override @Nonnull public TerrainAdjustment getTerrainAdjustment() { return TerrainAdjustment.BEARD_THIN; }

    @Override public int getGroundLevelDelta() { return 0; }

    @Override public void postProcess(@Nonnull WorldGenLevel level, @Nonnull StructureManager manager, @Nonnull ChunkGenerator generator, @Nonnull RandomSource random, @Nonnull BoundingBox box, @Nonnull ChunkPos chunk, @Nonnull BlockPos pos) {
        CityBiome.enter(level, (box.minX() + box.maxX()) / 2, (box.minZ() + box.maxZ()) / 2);
        try { laid(level, box); }
        finally { CityBiome.leave(); }
    }
}
