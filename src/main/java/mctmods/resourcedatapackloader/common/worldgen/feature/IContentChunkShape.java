package mctmods.resourcedatapackloader.common.worldgen.feature;

import mctmods.resourcedatapackloader.common.worldgen.placement.ContentPlacer;

import java.util.List;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;

public interface IContentChunkShape extends IContentShape {
    void generateChunk(ContentPlacer placer, ChunkPos chunk, Predicate<BlockPos> valid);

    default List<BlockPos> originsIn(ContentPlacer placer, ChunkPos chunk, Predicate<BlockPos> valid) { return List.of(); }
}
