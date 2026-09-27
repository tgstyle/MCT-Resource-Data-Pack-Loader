package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.interfaces.IContentChunkShape;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import java.util.ArrayList;
import java.util.function.Predicate;

public final class ContentShapeFeature {
    private ContentShapeFeature() {}

    public static boolean place(WorldGenLevel level, RandomSource random, BlockPos origin, Identifier id) {
        ContentWorldgen.Entry entry = ContentWorldgen.entry(id);
        if (entry == null) { return false; }
        ChunkPos center = ChunkPos.containing(origin);
        return run(entry, new ContentPlacer(level, entry.palette(), center), random, center, origin);
    }

    public static boolean run(ContentWorldgen.Entry entry, ContentPlacer placer, RandomSource random, ChunkPos center, BlockPos origin) {
        if (entry.shape() instanceof IContentChunkShape chunked) {
            Predicate<BlockPos> valid = pos -> ContentWorldgen.allows(entry, placer.level(), pos);
            chunked.generateChunk(placer, center, valid);
            if (entry.def().follows()) {
                for (BlockPos led : chunked.originsIn(placer, center, valid)) { ContentWorldgen.after(entry, placer, random, led, new ArrayList<>()); }
            }
            return true;
        }
        if (entry.shape() instanceof ContentImprint imprint && imprint.pinned()) { return imprint.placePinned(placer, center, origin); }
        boolean placed = entry.shape().generate(placer, random, origin);
        if (placed && entry.def().follows()) { ContentWorldgen.after(entry, placer, random, origin, new ArrayList<>()); }
        return placed;
    }
}
