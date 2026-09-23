package mctmods.resourcedatapackloader.content.worldgen;


import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.Direction;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;

import javax.annotation.Nonnull;

public final class ContentCityWellPiece extends StructurePiece {
    public static final StructurePieceType TYPE = (StructurePieceType.ContextlessType) ContentCityWellPiece::new;
    public static final int SIZE = 6;
    private static final int RIM = 11;
    private static final int CLEAR_LEAST = 4;
    private static final int FILL = 8;
    private static final String LEVEL = "Level";
    private static final String TALL = "Tall";
    private static final String KIND = "Kind";
    private static final String FIRST = "First";
    private final int level;
    private final int tall;
    private final String kind;
    private final boolean first;

    public ContentCityWellPiece(int minX, int minZ, int level, int tall, String kind, boolean first) {
        super(TYPE, 0, new BoundingBox(minX, level - RIM, minZ, minX + SIZE - 1, level + Math.max(CLEAR_LEAST, tall), minZ + SIZE - 1));
        this.level = level;
        this.tall = tall;
        this.kind = kind;
        this.first = first;
    }

    public ContentCityWellPiece(CompoundTag tag) {
        super(TYPE, tag);
        this.level = tag.getInt(LEVEL);
        this.tall = tag.getInt(TALL);
        this.kind = tag.getString(KIND);
        this.first = tag.getBoolean(FIRST);
    }

    @Override protected void addAdditionalSaveData(@Nonnull StructurePieceSerializationContext context, @Nonnull CompoundTag tag) {
        tag.putInt(LEVEL, level);
        tag.putInt(TALL, tall);
        tag.putString(KIND, kind);
        tag.putBoolean(FIRST, first);
    }

    boolean crowned() { return !first; }

    private void set(WorldGenLevel world, BoundingBox box, int localX, int localY, int localZ, BlockState state) {
        BlockPos at = new BlockPos(boundingBox.minX() + localX, level - RIM + localY, boundingBox.minZ() + localZ);
        if (box.isInside(at)) { world.setBlock(at, state, 2); }
    }

    private BlockState typed(BlockState plain) {
        if ("desert".equals(kind)) {
            if (plain.is(Blocks.COBBLESTONE)) { return Blocks.SANDSTONE.defaultBlockState(); }
            return plain;
        }
        if ("savanna".equals(kind)) {
            if (plain.is(Blocks.COBBLESTONE)) { return Blocks.ACACIA_LOG.defaultBlockState().setValue(RotatedPillarBlock.AXIS, Direction.Axis.Y); }
            if (plain.is(Blocks.OAK_FENCE)) { return Blocks.ACACIA_FENCE.defaultBlockState(); }
            return plain;
        }
        if ("taiga".equals(kind) && plain.is(Blocks.OAK_FENCE)) { return Blocks.SPRUCE_FENCE.defaultBlockState(); }
        return plain;
    }

    private void vanilla(WorldGenLevel world, BoundingBox box) {
        BlockState wall = typed(Blocks.COBBLESTONE.defaultBlockState());
        BlockState fence = typed(Blocks.OAK_FENCE.defaultBlockState());
        BlockState water = Blocks.WATER.defaultBlockState();
        BlockState air = Blocks.AIR.defaultBlockState();
        for (int x = 1; x <= 4; x++) {
            for (int y = 0; y <= 12; y++) {
                for (int z = 1; z <= 4; z++) {
                    boolean edge = x == 1 || x == 4 || y == 0 || y == 12 || z == 1 || z == 4;
                    set(world, box, x, y, z, edge ? wall : water);
                }
            }
        }
        for (int x = 2; x <= 3; x++) {
            for (int z = 2; z <= 3; z++) { set(world, box, x, 12, z, air); }
        }
        for (int x : new int[] {1, 4}) {
            for (int z : new int[] {1, 4}) {
                set(world, box, x, 13, z, fence);
                set(world, box, x, 14, z, fence);
            }
        }
        for (int x = 1; x <= 4; x++) {
            for (int z = 1; z <= 4; z++) { set(world, box, x, 15, z, wall); }
        }
        BlockPos.MutableBlockPos at = new BlockPos.MutableBlockPos();
        for (int x = 0; x < SIZE; x++) {
            for (int z = 0; z < SIZE; z++) {
                if (x != 0 && x != SIZE - 1 && z != 0 && z != SIZE - 1) { continue; }
                set(world, box, x, RIM, z, wall);
                for (int y = level + 1; y < world.getMaxBuildHeight(); y++) {
                    at.set(boundingBox.minX() + x, y, boundingBox.minZ() + z);
                    if (!box.isInside(at) || world.getBlockState(at).isAir()) { break; }
                    world.setBlock(at, air, 2);
                }
            }
        }
    }

    @SuppressWarnings("deprecation") private void seat(WorldGenLevel world, BoundingBox box) {
        BlockState floor = CityPalette.stateOr(ContentCity.paving(), Blocks.DIRT_PATH.defaultBlockState());
        BlockState air = Blocks.AIR.defaultBlockState();
        BlockPos.MutableBlockPos at = new BlockPos.MutableBlockPos();
        for (int x = boundingBox.minX(); x <= boundingBox.maxX(); x++) {
            for (int z = boundingBox.minZ(); z <= boundingBox.maxZ(); z++) {
                at.set(x, level, z);
                if (!box.isInside(at)) { continue; }
                for (int y = level + 1; y <= level + Math.max(CLEAR_LEAST, tall); y++) {
                    at.set(x, y, z);
                    if (!world.getBlockState(at).isAir()) { world.setBlock(at, air, 2); }
                }
                for (int y = level - 1; y >= level - FILL; y--) {
                    at.set(x, y, z);
                    BlockState held = world.getBlockState(at);
                    if (held.isSolid()) { break; }
                    world.setBlock(at, Blocks.DIRT.defaultBlockState(), 2);
                }
                world.setBlock(at.set(x, level, z), floor, 2);
            }
        }
    }

    @Override public void postProcess(@Nonnull WorldGenLevel world, @Nonnull StructureManager manager, @Nonnull ChunkGenerator generator, @Nonnull RandomSource random, @Nonnull BoundingBox box, @Nonnull ChunkPos chunk, @Nonnull BlockPos pos) {
        CityBiome.enter(world, (box.minX() + box.maxX()) / 2, (box.minZ() + box.maxZ()) / 2);
        try {
            if (tall < 0) { vanilla(world, box); }
            else { seat(world, box); }
        }
        finally { CityBiome.leave(); }
    }
}
