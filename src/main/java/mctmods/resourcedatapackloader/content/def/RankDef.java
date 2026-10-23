package mctmods.resourcedatapackloader.content.def;

import net.minecraft.resources.ResourceLocation;
import java.util.List;
import java.util.Map;

public record RankDef(ResourceLocation key, List<String> entities, int weight, String label, float health, float damage, Map<String, Integer> effects, float loot) {}
