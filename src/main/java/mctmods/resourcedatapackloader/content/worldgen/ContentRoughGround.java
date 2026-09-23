package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import java.util.Map;

public final class ContentRoughGround {
    private static final int TOLERANCE = 6;
    private static final int MANSION_OFFSET = 40;
    private static final int MANSION_REACH = 32;
    private static final ResourceLocation MANSION = ResourceLocation.fromNamespaceAndPath("minecraft", "mansion");
    private static final Map<ResourceLocation, Integer> TEMPLE_REACH = Map.of(
            ResourceLocation.fromNamespaceAndPath("minecraft", "desert_pyramid"), 10,
            ResourceLocation.fromNamespaceAndPath("minecraft", "jungle_pyramid"), 7,
            ResourceLocation.fromNamespaceAndPath("minecraft", "swamp_hut"), 4,
            ResourceLocation.fromNamespaceAndPath("minecraft", "igloo"), 4);

    private ContentRoughGround() {}

    public static boolean refuses(StructureStart start, long seed, ChunkGenerator generator, RandomState random, RegistryAccess registries) {
        if (!start.isValid()) { return false; }
        ResourceLocation id = registries.registryOrThrow(Registries.STRUCTURE).getKey(start.getStructure());
        if (id == null) { return false; }
        boolean mansion = MANSION.equals(id);
        Integer reach = TEMPLE_REACH.get(id);
        if (!mansion && reach == null) { return false; }
        ChunkPos chunk = start.getChunkPos();
        if (ContentStructureSpread.pinned(chunk) || !ContentCity.wanted()) { return false; }
        CityGround ground = CityGround.of(seed, generator, random, registries);
        boolean rough = mansion ? rough(ground, chunk.getMinBlockX() + MANSION_OFFSET, chunk.getMinBlockZ() + MANSION_OFFSET, MANSION_REACH) : rough(ground, chunk.getMiddleBlockX(), chunk.getMiddleBlockZ(), reach);
        if (rough) { ContentLog.LOGGER.debug("Structure {} at chunk {} stands on ground more than {} blocks uneven, so it is not founded", id, chunk, TOLERANCE); }
        return rough;
    }

    private static boolean rough(CityGround ground, int x, int z, int halfWidth) {
        int middle = ground.floor(x, z);
        int lowest = middle;
        int highest = middle;
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                int sampled = ground.floor(x + dx * halfWidth, z + dz * halfWidth);
                lowest = Math.min(lowest, sampled);
                highest = Math.max(highest, sampled);
                if (highest - lowest > TOLERANCE) { return true; }
            }
        }
        return false;
    }
}
