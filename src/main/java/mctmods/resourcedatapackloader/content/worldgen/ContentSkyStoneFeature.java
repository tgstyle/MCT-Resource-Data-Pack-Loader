package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.def.CaveRegionDef;

import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import java.util.Map;

public final class ContentSkyStoneFeature {
    private ContentSkyStoneFeature() {}

    public static boolean place(WorldGenLevel level, ChunkGenerator generator, BlockPos origin, Identifier id) {
        ContentSkyLand.Land land = ContentSkyLand.landOf(generator);
        CaveRegionDef region = ContentCaveRegions.def(id);
        if (land == null || region == null) { return false; }
        Block named = ContentWorldShape.block(region.sky().stone(), "Cave region " + region.key() + " skyStone");
        if (named == null) { return false; }
        BlockState stone = named.defaultBlockState();
        Map<Identifier, Identifier> biomes = ContentBiomes.skyStones();
        ChunkPos chunk = ChunkPos.containing(origin);
        ChunkAccess held = level.getChunk(chunk.x(), chunk.z());
        int lowest = Math.max(land.floor(), region.minHeight());
        int highest = Math.min(land.ceiling(), region.maxHeight());
        BlockPos.MutableBlockPos at = new BlockPos.MutableBlockPos();
        boolean placed = false;
        int y = lowest;
        while (y <= highest) {
            int index = held.getSectionIndex(y);
            if (index < 0 || index >= held.getSectionsCount() || held.getSection(index).hasOnlyAir()) {
                y = SectionPos.sectionToBlockCoord(SectionPos.blockToSectionCoord(y) + 1);
                continue;
            }
            for (int x = chunk.getMinBlockX(); x <= chunk.getMaxBlockX(); x++) {
                for (int z = chunk.getMinBlockZ(); z <= chunk.getMaxBlockZ(); z++) {
                    at.set(x, y, z);
                    Identifier wanted = level.getBiome(at).unwrapKey().map(key -> biomes.getOrDefault(key.identifier(), land.stone())).orElse(land.stone());
                    if (!wanted.equals(BuiltInRegistries.BLOCK.getKey(held.getBlockState(at).getBlock())) || !ContentCaveRegions.holds(level, region, at)) { continue; }
                    level.setBlock(at, stone, Block.UPDATE_CLIENTS);
                    placed = true;
                }
            }
            y++;
        }
        return placed;
    }
}
