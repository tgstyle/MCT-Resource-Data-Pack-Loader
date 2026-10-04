package mctmods.resourcedatapackloader.content.def;

import net.minecraft.resources.ResourceLocation;
import java.util.List;
import java.util.Map;
import java.util.Set;

public record ExposureDef(ResourceLocation key, int scanInterval, int range, boolean skipsCreative, int sourcesForNextLevel, String immunity, Map<ResourceLocation, Integer> blocks, Map<ResourceLocation, Integer> items, Map<ResourceLocation, Integer> dimensions, List<ExposureLevelDef> levels, Map<ResourceLocation, Integer> carriers, boolean contagious, Set<ResourceLocation> catchers, int contagionRange, float contagionChance, int contagionDuration, Map<String, Integer> weather, Set<ResourceLocation> weatherDimensions) {
    public String name() { return key.getPath(); }

    public boolean spreads() { return contagionRange > 0 && contagionChance > 0.0F && (contagious || !carriers.isEmpty()); }
}
