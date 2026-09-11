package mctmods.resourcedatapackloader.content.interfaces;

import mctmods.resourcedatapackloader.content.worldgen.ContentPlacer;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import java.util.List;
import java.util.function.Predicate;

public interface IContentChunkShape extends IContentShape {
    void generateChunk(ContentPlacer placer, ChunkPos chunk, Predicate<BlockPos> valid);

    default List<BlockPos> originsIn(ContentPlacer placer, ChunkPos chunk, Predicate<BlockPos> valid) { return List.of(); }
}
