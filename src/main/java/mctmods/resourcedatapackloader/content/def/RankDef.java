package mctmods.resourcedatapackloader.content.def;

import net.minecraft.resources.Identifier;
import java.util.List;
import java.util.Map;

public record RankDef(Identifier key, List<Identifier> ids, List<String> kinds, int weight, String label, float health, float damage, Map<String, Integer> effects, float loot, List<String> dimensions) {}
