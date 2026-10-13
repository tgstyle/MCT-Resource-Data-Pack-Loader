package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.util.ContentLog;
import net.minecraft.init.Biomes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.GenLayerSmooth;
import net.minecraft.world.gen.layer.GenLayerVoronoiZoom;
import net.minecraft.world.gen.layer.GenLayerZoom;
import net.minecraft.world.gen.layer.IntCache;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;

public final class ContentListBiomes {
    private static final int ZOOMS = 6;

    private ContentListBiomes() {}

    public static BiomeProvider provider(WorldInfo info, List<String> names) {
        int[] ids = ids(names);
        return new BiomeProvider(info) {
            @Override @Nonnull public GenLayer[] getModdedBiomeGenerators(@Nonnull WorldType type, long seed, @Nonnull GenLayer[] original) { return layers(seed, ids); }
        };
    }

    private static int[] ids(List<String> names) {
        List<Integer> found = new ArrayList<>();
        for (String name : names) {
            Biome biome = ForgeRegistries.BIOMES.getValue(new ResourceLocation(name));
            if (biome == null) { ContentLog.LOGGER.error("A dimension's biome list names '{}', which is not a registered biome, so it is left out", name); }
            else { found.add(Biome.getIdForBiome(biome)); }
        }
        if (found.isEmpty()) {
            ContentLog.LOGGER.error("A dimension's biome list holds no registered biome, using minecraft:plains");
            found.add(Biome.getIdForBiome(Biomes.PLAINS));
        }
        int[] ids = new int[found.size()];
        for (int i = 0; i < ids.length; i++) { ids[i] = found.get(i); }
        return ids;
    }

    private static GenLayer[] layers(long seed, int[] ids) {
        GenLayer zoomed = GenLayerZoom.magnify(1000L, new ListLayer(200L, ids), ZOOMS);
        GenLayer smooth = new GenLayerSmooth(1000L, zoomed);
        GenLayer voronoi = new GenLayerVoronoiZoom(10L, smooth);
        smooth.initWorldGenSeed(seed);
        voronoi.initWorldGenSeed(seed);
        return new GenLayer[]{smooth, voronoi, smooth};
    }

    private static final class ListLayer extends GenLayer {
        private final int[] ids;

        ListLayer(long seed, int[] ids) {
            super(seed);
            this.ids = ids;
        }

        @Override @Nonnull public int[] getInts(int areaX, int areaY, int areaWidth, int areaHeight) {
            int[] out = IntCache.getIntCache(areaWidth * areaHeight);
            for (int y = 0; y < areaHeight; y++) {
                for (int x = 0; x < areaWidth; x++) {
                    initChunkSeed(areaX + x, areaY + y);
                    out[x + y * areaWidth] = ids[nextInt(ids.length)];
                }
            }
            return out;
        }
    }
}
