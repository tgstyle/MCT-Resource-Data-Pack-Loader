package mctmods.resourcedatapackloader.common.pack.definitions.worlds;

import mctmods.resourcedatapackloader.common.pack.definitions.blocks.BlockWeightDef;
import mctmods.resourcedatapackloader.common.pack.definitions.games.PickDef;
import mctmods.resourcedatapackloader.core.util.Fallbacks;

import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;

public record DungeonThemeDef(ResourceLocation key, String name, @Nullable Integer weight, List<String> dimensions, boolean dimensionsAreBlacklist, List<PickDef> biomes, List<PickDef> biomeTypes, List<PickDef> notBiomes, List<PickDef> notBiomeTypes, @Nullable Integer minY, @Nullable Integer maxY, Map<String, List<BlockWeightDef>> blocks, Map<String, List<Mob>> mobs, List<SpawnGroup> spawnGroups, Map<String, List<LootEntry>> loot, Map<String, Decorator> decorators, @Nullable Map<String, DungeonDef.Degree> rolls, @Nullable DungeonDef.Difficulty difficulty, @Nullable DungeonDef.Decay decay, @Nullable Integer lootLevels, @Nullable Integer lootBonus, List<ResourceLocation> append, Map<String, List<String>> removals) {
    public static final int DEFAULT_WEIGHT = 1;
    public static final int DEFAULT_LOOT_LEVELS = 6;
    public static final int MAX_LOOT_LEVELS = 12;
    public static final int DEFAULT_LOOT_BONUS = 0;
    public static final String REMOVE_PREFIX = "remove:";

    public boolean appends() { return !append.isEmpty(); }

    public int weightOrDefault() { return Fallbacks.orDefault(weight, DEFAULT_WEIGHT); }

    public int lootLevelsOrDefault() { return Fallbacks.orDefault(lootLevels, DEFAULT_LOOT_LEVELS); }

    public int lootBonusOrDefault() { return Fallbacks.orDefault(lootBonus, DEFAULT_LOOT_BONUS); }

    public boolean allowsDimension(String dimension) { return dimensions.isEmpty() || dimensions.contains(dimension) != dimensionsAreBlacklist; }

    public record Mob(String id, int weight, @Nullable String nbt) {}

    public record SpawnGroup(String name, List<Mob> mobs, int weight, double minDepth, double maxDepth, List<String> kinds, @Nullable String tier) {}

    public record LootEntry(ResourceLocation table, int weight, int minLevel, int maxLevel) {
        public static final int ANY_LEVEL = Integer.MAX_VALUE;
    }

    public record Decorator(String type, List<String> blocks, Map<String, Integer> numbers, @Nullable String on, @Nullable String loot) {}
}
