package mctmods.resourcedatapackloader.common.pack.definitions.entities;

import net.minecraft.resources.ResourceLocation;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

public record RankDef(ResourceLocation key, List<String> entities, int weight, String label, float health, float damage, Map<String, Integer> effects, float loot, List<String> dimensions, Map<String, String> equipment, float dropChance, @Nullable Boolean picksUpLoot, boolean digs, String digItem, boolean breaksDoors, boolean steals, RankRandomDef random, @Nullable RankScalingDef scaling) {}
