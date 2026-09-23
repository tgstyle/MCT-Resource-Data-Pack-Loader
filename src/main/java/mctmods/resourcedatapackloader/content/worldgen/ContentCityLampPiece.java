package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.ContentStates;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class ContentCityLampPiece extends StructurePiece {
    public static final StructurePieceType TYPE = (StructurePieceType.ContextlessType) ContentCityLampPiece::new;
    private static final String FOOT = "Foot";
    private static final Set<String> WARNED = ConcurrentHashMap.newKeySet();
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

    private static void place(WorldGenLevel level, BlockPos at, ContentStates.Spec spec, BlockState state) {
        level.setBlock(at, state, 2);
        if (spec.tag() == null) { return; }
        BlockEntity entity = level.getBlockEntity(at);
        if (entity == null) { return; }
        CompoundTag merged = entity.saveWithoutMetadata(level.registryAccess());
        merged.merge(spec.tag());
        entity.loadWithComponents(merged, level.registryAccess());
        entity.setChanged();
    }

    @Override public void postProcess(@Nonnull WorldGenLevel level, @Nonnull StructureManager manager, @Nonnull ChunkGenerator generator, @Nonnull RandomSource random, @Nonnull BoundingBox box, @Nonnull ChunkPos chunk, @Nonnull BlockPos pos) {
        ContentStates.Spec post = spec("villagePathLampBlock", ContentCity.lampBlock());
        if (post == null) { return; }
        BoundingBox held = getBoundingBox();
        int x = (held.minX() + held.maxX()) / 2;
        int z = (held.minZ() + held.maxZ()) / 2;
        int head = held.maxY();
        BlockPos.MutableBlockPos at = new BlockPos.MutableBlockPos();
        for (int y = foot; y < head; y++) {
            at.set(x, y, z);
            if (box.isInside(at)) { place(level, at.immutable(), post, post.state()); }
        }
        ContentStates.Spec top = spec("villagePathLampTopBlock", ContentCity.lampTopBlock());
        at.set(x, head, z);
        if (top != null && box.isInside(at)) { place(level, at.immutable(), top, top.state()); }
        ContentStates.Spec side = spec("villagePathLampSideBlock", ContentCity.lampSideBlock());
        if (side == null) { return; }
        for (Direction facing : Direction.Plane.HORIZONTAL) {
            at.set(x + facing.getStepX(), head, z + facing.getStepZ());
            if (!box.isInside(at) || !level.getBlockState(at).isAir()) { continue; }
            BlockState hung = CityPalette.faced(side.state(), facing);
            place(level, at.immutable(), side, hung);
        }
    }

    @Nullable private static ContentStates.Spec spec(String key, String named) {
        if (named.isEmpty()) { return null; }
        ContentStates.Spec found = ContentStates.spec(named, "a street lamp");
        if (found == null && WARNED.add(key + "|" + named)) { ContentLog.LOGGER.error("{} '{}' is not a registered block, nothing is placed", key, named); }
        return found == null || found.state().isAir() ? null : found;
    }
}
