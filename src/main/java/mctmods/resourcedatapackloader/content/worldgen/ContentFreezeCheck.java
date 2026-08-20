package mctmods.resourcedatapackloader.content.worldgen;

import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;

public final class ContentFreezeCheck {
    private static final float FREEZES_BELOW = 0.15F;
    private static final float COLDEST_HEIGHT_GAIN = ((4.0F + 255.0F) - 64.0F) * 0.05F / 30.0F;

    private ContentFreezeCheck() {}

    public static boolean couldFreeze(World world, int chunkX, int chunkZ) {
        Chunk chunk = world.getChunkProvider().getLoadedChunk(chunkX, chunkZ);
        if (chunk == null) { return true; }
        byte[] biomes = chunk.getBiomeArray();
        boolean[] asked = new boolean[256];
        for (byte id : biomes) {
            int index = id & 255;
            if (asked[index]) { continue; }
            asked[index] = true;
            Biome biome = Biome.getBiome(index);
            if (biome == null || biome.getDefaultTemperature() - COLDEST_HEIGHT_GAIN < FREEZES_BELOW) { return true; }
        }
        return false;
    }
}
