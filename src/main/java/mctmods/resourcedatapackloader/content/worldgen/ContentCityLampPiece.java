package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.util.Registered;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;

import javax.annotation.Nonnull;

public final class ContentCityLampPiece extends StructurePiece {
    public static final StructurePieceType TYPE = (StructurePieceType.ContextlessType) ContentCityLampPiece::new;
    private static final String FOOT = "Foot";
    private final int foot;

    public ContentCityLampPiece(int x, int foot, int z, int height) {
        super(TYPE, 0, new BoundingBox(x - 1, foot, z - 1, x + 1, foot + height, z + 1));
        this.foot = foot;
    }

    public ContentCityLampPiece(CompoundTag tag) {
        super(TYPE, tag);
        this.foot = tag.getInt(FOOT);
    }

    @Override protected void addAdditionalSaveData(@Nonnull StructurePieceSerializationContext context, @Nonnull CompoundTag tag) { tag.putInt(FOOT, foot); }

    @Override public void postProcess(@Nonnull WorldGenLevel level, @Nonnull StructureManager manager, @Nonnull ChunkGenerator generator, @Nonnull RandomSource random, @Nonnull BoundingBox box, @Nonnull ChunkPos chunk, @Nonnull BlockPos pos) {
        BlockState post = state(ContentCity.lampBlock());
        if (post == null) { return; }
        BoundingBox held = getBoundingBox();
        int x = (held.minX() + held.maxX()) / 2;
        int z = (held.minZ() + held.maxZ()) / 2;
        int head = held.maxY();
        BlockPos.MutableBlockPos at = new BlockPos.MutableBlockPos();
        for (int y = foot; y < head; y++) {
            at.set(x, y, z);
            if (box.isInside(at)) { level.setBlock(at, post, 2); }
        }
        BlockState top = state(ContentCity.lampTopBlock());
        at.set(x, head, z);
        if (top != null && box.isInside(at)) { level.setBlock(at, top, 2); }
        BlockState side = state(ContentCity.lampSideBlock());
        if (side == null) { return; }
        for (Direction facing : Direction.Plane.HORIZONTAL) {
            at.set(x + facing.getStepX(), head, z + facing.getStepZ());
            if (!box.isInside(at) || !level.getBlockState(at).isAir()) { continue; }
            BlockState hung = side.hasProperty(BlockStateProperties.HORIZONTAL_FACING) ? side.setValue(BlockStateProperties.HORIZONTAL_FACING, facing) : side;
            if (hung.canSurvive(level, at)) { level.setBlock(at, hung, 2); }
        }
    }

    @javax.annotation.Nullable private static BlockState state(String named) {
        if (named.isEmpty()) { return null; }
        Block found = Registered.find(BuiltInRegistries.BLOCK, ResourceLocation.tryParse(named));
        return found == null || found == Blocks.AIR ? null : found.defaultBlockState();
    }
}
