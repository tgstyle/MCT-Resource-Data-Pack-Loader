package mctmods.resourcedatapackloader.content.def;

import net.minecraft.resources.ResourceLocation;
import java.util.List;
import java.util.Map;

public record BiomeDef(ResourceLocation key, String name, float temperature, float rainfall, boolean rain, boolean snow, int waterColor, int grassColor, int foliageColor,
        ResourceLocation baseBiome, String topBlock, String fillerBlock, String stoneBlock, List<String> types, String climate, int weight, boolean playerSpawn,
        boolean villages, String villageType, boolean strongholds, Map<String, Integer> decoration, float spawnChance, float surfaceDayRate, float surfaceNightRate,
        float undergroundDayRate, float undergroundNightRate, boolean keepDefaultSpawns, List<BiomeSpawnDef> spawns, boolean banded, int minHeight, int maxHeight,
        List<String> replaces, List<String> requires) {
    public static final int NO_COLOR = -1;
    public static final float NO_RATE = -1.0F;

    public float rate(boolean sky, boolean day) {
        if (sky) { return day ? surfaceDayRate : surfaceNightRate; }
        return day ? undergroundDayRate : undergroundNightRate;
    }
}
