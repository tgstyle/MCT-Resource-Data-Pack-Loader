package mctmods.resourcedatapackloader.common.worldgen.feature.shape;

import mctmods.resourcedatapackloader.common.pack.definitions.worlds.CaveRegionDef;
import mctmods.resourcedatapackloader.common.worldgen.carver.ContentCaveRegions;
import mctmods.resourcedatapackloader.common.worldgen.placement.ContentPlacer;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;

public final class ContentCoverFeature {
    private ContentCoverFeature() {}

    public static boolean place(WorldGenLevel level, BlockPos origin, Identifier id) {
        ContentCover cover = ContentCaveRegions.cover(id);
        CaveRegionDef region = ContentCaveRegions.def(id);
        if (cover == null || region == null) { return false; }
        ChunkPos center = ChunkPos.containing(origin);
        cover.generateChunk(new ContentPlacer(level, ContentCaveRegions.palette(id), center, ContentPlacer.CHUNK_ONLY), center, pos -> ContentCaveRegions.holds(level, region, pos));
        return true;
    }
}
