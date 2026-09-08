package mctmods.resourcedatapackloader.content.def;

import net.minecraft.resources.ResourceLocation;
import java.util.List;

public record WorldgenDef(ResourceLocation key, ResourceLocation block, List<BlockWeightDef> blocks, AmountDef size, AmountDef attempts,
        int minHeight, int maxHeight, List<BlockMatchDef> replaces, List<BlockMatchDef> adjacent, boolean replacesGiven, boolean sparse,
        List<String> dimensions, boolean dimensionsAreBlacklist, List<String> biomes, List<String> biomeTypes, boolean biomesAreBlacklist,
        float leastTemperature, float mostTemperature, float leastRainfall, float mostRainfall, int minDistanceFromSpawn, List<String> requires,
        boolean retrogen, String retrogenKey, List<ResourceLocation> caveRegions, String snap, int snapDepth, SpreadDef spread, ShapeDef shape,
        List<PickDef> indicators, AmountDef indicatorCount, int indicatorSpread, List<FollowDef> then, AmountDef thenCount, int thenSpread, AmountDef thenDepth, String prospectAs) {
    public static final String FLOOR = "floor";
    public static final String CEILING = "ceiling";
    public static final float NO_LIMIT = 100.0F;

    public boolean hasBiomeFilter() { return !biomes.isEmpty() || !biomeTypes.isEmpty(); }

    public boolean hasClimateFilter() { return leastTemperature > -NO_LIMIT || mostTemperature < NO_LIMIT || leastRainfall > -NO_LIMIT || mostRainfall < NO_LIMIT; }

    public boolean follows() { return !indicators.isEmpty() || !then.isEmpty(); }

    public boolean needsBiome() { return !caveRegions.isEmpty() || hasBiomeFilter() || hasClimateFilter(); }

    public boolean climateAllows(float temperature, float rainfall) {
        return temperature >= leastTemperature && temperature <= mostTemperature && rainfall >= leastRainfall && rainfall <= mostRainfall;
    }
}
