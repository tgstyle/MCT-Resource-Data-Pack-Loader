package mctmods.resourcedatapackloader.content.def;

import net.minecraft.resources.Identifier;
import java.util.List;
import java.util.Map;
import java.util.Set;

public record ExposureDef(Identifier key, int scanInterval, int range, boolean skipsCreative, int sourcesForNextLevel, String immunity, Map<Identifier, Integer> blocks, Map<Identifier, Integer> items, Map<Identifier, Integer> dimensions, List<ExposureLevelDef> levels, Map<Identifier, Integer> carriers, boolean contagious, Set<Identifier> catchers, int contagionRange, float contagionChance, int contagionDuration, Map<String, Integer> weather, Set<Identifier> weatherDimensions) {
    public String name() { return key.getPath(); }

    public boolean spreads() { return contagionRange > 0 && contagionChance > 0.0F && (contagious || !carriers.isEmpty()); }
}
