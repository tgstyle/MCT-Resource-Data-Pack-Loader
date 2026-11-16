package mctmods.resourcedatapackloader.common.pack.definitions.worlds;

import mctmods.resourcedatapackloader.common.pack.definitions.blocks.BlockWeightDef;
import mctmods.resourcedatapackloader.common.pack.definitions.games.PickDef;

import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.util.ResourceLocation;

public final class DungeonThemeDef {
    public static final int DEFAULT_WEIGHT = 1;
    public static final int DEFAULT_LOOT_LEVELS = 6;
    public static final int MAX_LOOT_LEVELS = 12;
    public static final int DEFAULT_LOOT_BONUS = 0;
    public static final String REMOVE_PREFIX = "remove:";
    public final ResourceLocation key;
    public final String name;
    @Nullable public final Integer weight;
    public final Set<Integer> dimensions;
    public final boolean dimensionsAreBlacklist;
    public final List<PickDef> biomes;
    public final List<PickDef> biomeTypes;
    public final List<PickDef> notBiomes;
    public final List<PickDef> notBiomeTypes;
    @Nullable public final Integer minY;
    @Nullable public final Integer maxY;
    public final Map<String, List<BlockWeightDef>> blocks;
    public final Map<String, List<Mob>> mobs;
    public final List<SpawnGroup> spawnGroups;
    public final Map<String, List<LootEntry>> loot;
    public final Map<String, Decorator> decorators;
    @Nullable public final Map<String, DungeonDef.Degree> rolls;
    @Nullable public final DungeonDef.Difficulty difficulty;
    @Nullable public final DungeonDef.Decay decay;
    @Nullable public final Integer lootLevels;
    @Nullable public final Integer lootBonus;
    public final List<ResourceLocation> append;
    public final Map<String, List<String>> removals;

    public DungeonThemeDef(ResourceLocation key, String name, @Nullable Integer weight, Set<Integer> dimensions, boolean dimensionsAreBlacklist, List<PickDef> biomes, List<PickDef> biomeTypes, List<PickDef> notBiomes, List<PickDef> notBiomeTypes, @Nullable Integer minY, @Nullable Integer maxY, Map<String, List<BlockWeightDef>> blocks, Map<String, List<Mob>> mobs, List<SpawnGroup> spawnGroups, Map<String, List<LootEntry>> loot, Map<String, Decorator> decorators, @Nullable Map<String, DungeonDef.Degree> rolls, @Nullable DungeonDef.Difficulty difficulty, @Nullable DungeonDef.Decay decay, @Nullable Integer lootLevels, @Nullable Integer lootBonus, List<ResourceLocation> append, Map<String, List<String>> removals) {
        this.key = key;
        this.name = name;
        this.weight = weight;
        this.dimensions = dimensions;
        this.dimensionsAreBlacklist = dimensionsAreBlacklist;
        this.biomes = biomes;
        this.biomeTypes = biomeTypes;
        this.notBiomes = notBiomes;
        this.notBiomeTypes = notBiomeTypes;
        this.minY = minY;
        this.maxY = maxY;
        this.blocks = blocks;
        this.mobs = mobs;
        this.spawnGroups = spawnGroups;
        this.loot = loot;
        this.decorators = decorators;
        this.rolls = rolls;
        this.difficulty = difficulty;
        this.decay = decay;
        this.lootLevels = lootLevels;
        this.lootBonus = lootBonus;
        this.append = append;
        this.removals = removals;
    }

    public boolean appends() { return !append.isEmpty(); }

    public int weightOrDefault() { return weight == null ? DEFAULT_WEIGHT : weight; }

    public int lootLevelsOrDefault() { return lootLevels == null ? DEFAULT_LOOT_LEVELS : lootLevels; }

    public int lootBonusOrDefault() { return lootBonus == null ? DEFAULT_LOOT_BONUS : lootBonus; }

    public boolean allowsDimension(int dimension) { return dimensions.isEmpty() || dimensions.contains(dimension) != dimensionsAreBlacklist; }

    public static final class Mob {
        public final String id;
        public final int weight;
        @Nullable public final String nbt;

        public Mob(String id, int weight, @Nullable String nbt) {
            this.id = id;
            this.weight = weight;
            this.nbt = nbt;
        }
    }

    public static final class SpawnGroup {
        public final String name;
        public final List<Mob> mobs;
        public final int weight;
        public final double minDepth;
        public final double maxDepth;
        public final List<String> kinds;
        @Nullable public final String tier;

        public SpawnGroup(String name, List<Mob> mobs, int weight, double minDepth, double maxDepth, List<String> kinds, @Nullable String tier) {
            this.name = name;
            this.mobs = mobs;
            this.weight = weight;
            this.minDepth = minDepth;
            this.maxDepth = maxDepth;
            this.kinds = kinds;
            this.tier = tier;
        }
    }

    public static final class LootEntry {
        public static final int ANY_LEVEL = Integer.MAX_VALUE;
        public final ResourceLocation table;
        public final int weight;
        public final int minLevel;
        public final int maxLevel;

        public LootEntry(ResourceLocation table, int weight, int minLevel, int maxLevel) {
            this.table = table;
            this.weight = weight;
            this.minLevel = minLevel;
            this.maxLevel = maxLevel;
        }
    }

    public static final class Decorator {
        public final String type;
        public final List<String> blocks;
        public final Map<String, Integer> numbers;
        @Nullable public final String on;
        @Nullable public final String loot;

        public Decorator(String type, List<String> blocks, Map<String, Integer> numbers, @Nullable String on, @Nullable String loot) {
            this.type = type;
            this.blocks = blocks;
            this.numbers = numbers;
            this.on = on;
            this.loot = loot;
        }
    }
}
