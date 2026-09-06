package mctmods.resourcedatapackloader.content.def;

import net.minecraft.resources.ResourceLocation;
import java.util.List;

public record WorldgenDef(ResourceLocation key, ResourceLocation block, List<BlockWeightDef> blocks, AmountDef size, AmountDef attempts,
        int minHeight, int maxHeight, List<BlockMatchDef> replaces, List<BlockMatchDef> adjacent, boolean replacesGiven, boolean sparse,
        List<String> dimensions, boolean dimensionsAreBlacklist, List<String> biomes, List<String> biomeTypes, boolean biomesAreBlacklist,
        float leastTemperature, float mostTemperature, float leastRainfall, float mostRainfall, int minDistanceFromSpawn, List<String> requires,
        boolean retrogen, String retrogenKey, List<ResourceLocation> caveRegions, String snap, int snapDepth, SpreadDef spread, ShapeDef shape) {
    public static final String FLOOR = "floor";
    public static final String CEILING = "ceiling";

    public boolean hasBiomeFilter() { return !biomes.isEmpty() || !biomeTypes.isEmpty(); }

    public boolean climateAllows(float temperature, float rainfall) {
        return temperature >= leastTemperature && temperature <= mostTemperature && rainfall >= leastRainfall && rainfall <= mostRainfall;
    }
}
