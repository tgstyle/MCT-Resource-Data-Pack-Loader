package mctmods.resourcedatapackloader.content.def;

import net.minecraft.util.ResourceLocation;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class ExposureDef {
    public final ResourceLocation registryName;
    public final String name;
    public final int scanInterval;
    public final int range;
    public final boolean skipsCreative;
    public final int sourcesForNextLevel;
    public final String immunity;
    public final Map<ResourceLocation, Integer> blocks;
    public final Map<ResourceLocation, Integer> items;
    public final Map<Integer, Integer> dimensions;
    public final List<ExposureLevelDef> levels;
    public final Map<ResourceLocation, Integer> carriers;
    public final boolean contagious;
    public final Set<ResourceLocation> catchers;
    public final int contagionRange;
    public final float contagionChance;
    public final int contagionDuration;
    public final Map<String, Integer> weather;
    public final Set<Integer> weatherDimensions;

    public ExposureDef(ResourceLocation registryName, int scanInterval, int range, boolean skipsCreative, int sourcesForNextLevel, String immunity, Map<ResourceLocation, Integer> blocks, Map<ResourceLocation, Integer> items, Map<Integer, Integer> dimensions, List<ExposureLevelDef> levels, Map<ResourceLocation, Integer> carriers, boolean contagious, Set<ResourceLocation> catchers, int contagionRange, float contagionChance, int contagionDuration, Map<String, Integer> weather, Set<Integer> weatherDimensions) {
        this.registryName = registryName;
        this.name = registryName.getPath();
        this.scanInterval = scanInterval;
        this.range = range;
        this.skipsCreative = skipsCreative;
        this.sourcesForNextLevel = sourcesForNextLevel;
        this.immunity = immunity;
        this.blocks = blocks;
        this.items = items;
        this.dimensions = dimensions;
        this.levels = levels;
        this.carriers = carriers;
        this.contagious = contagious;
        this.catchers = catchers;
        this.contagionRange = contagionRange;
        this.contagionChance = contagionChance;
        this.contagionDuration = contagionDuration;
        this.weather = weather;
        this.weatherDimensions = weatherDimensions;
    }

    public boolean spreads() { return contagionRange > 0 && contagionChance > 0.0F && (contagious || !carriers.isEmpty()); }
}
