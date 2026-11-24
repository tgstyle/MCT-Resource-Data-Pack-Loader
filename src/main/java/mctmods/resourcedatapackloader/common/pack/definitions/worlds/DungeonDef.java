package mctmods.resourcedatapackloader.common.pack.definitions.worlds;

import mctmods.resourcedatapackloader.common.pack.definitions.games.PickDef;
import mctmods.resourcedatapackloader.common.util.worldgen.BiomeFilter;
import mctmods.resourcedatapackloader.common.util.worldgen.SeedStreams;
import mctmods.resourcedatapackloader.common.util.worldgen.WeightedPicks;
import mctmods.resourcedatapackloader.core.util.Fallbacks;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.util.ResourceLocation;

public final class DungeonDef {
    public static final int DEFAULT_CHANCE = 75;
    public static final int DEFAULT_MIN_DISTANCE = 600;
    public static final int DEFAULT_SURFACE_BUFFER = 7;
    public static final int DEFAULT_LOOPS = 25;
    public static final int DEFAULT_SUPPORT_DEPTH = 8;
    public static final int[] DEFAULT_LEVEL_HEIGHT = {5, 8};
    public static final int[] DEFAULT_ROOM_SIZE = {5, 15};
    public static final String DEFAULT_DIFFICULTY = "norm";
    public static final String CAVE = "cave";
    public static final List<String> SHAFTS = Collections.unmodifiableList(Arrays.asList("ladder", "spiral", "stairs"));
    public static final List<String> ROLLS = Collections.unmodifiableList(Arrays.asList("outside", "liquids", "subrooms", "islands", "pillars", "symmetry", "variability", "degeneracy", "complexity", "vertical", "entrances", "fences", "naturals"));
    public static int rollIndex(String name) { return ROLLS.indexOf(name); }
    public final ResourceLocation key;
    public final String name;
    public final List<PickDef> themes;
    public final Set<Integer> dimensions;
    public final boolean dimensionsAreBlacklist;
    public final int chance;
    public final int minDistance;
    public final String group;
    public final int minDistanceFromSpawn;
    public final boolean noOceans;
    public final List<String> biomes;
    public final List<String> biomeTypes;
    public final boolean biomesAreBlacklist;
    @Nullable public final int[][] at;
    @Nullable public final String locateAs;
    public final List<DungeonSizeDef> sizes;
    public final int shrink;
    public final int surfaceBuffer;
    @Nullable public final Integer minY;
    public final int[] levelHeight;
    public final int[] roomSize;
    public final Corridor corridor;
    public final int loops;
    public final Map<String, Degree> rolls;
    public final List<RoomKindDef> roomKinds;
    public final Entrance entrance;
    @Nullable public final Boss boss;
    public final List<PickDef> shafts;
    @Nullable public final Difficulty difficulty;
    public final int supportDepth;
    @Nullable public final Decay decay;
    public final List<LevelRule> levelRules;
    @Nullable public final RespawnDef respawn;

    public DungeonDef(ResourceLocation key, String name, List<PickDef> themes, Set<Integer> dimensions, boolean dimensionsAreBlacklist, int chance, int minDistance, String group, int minDistanceFromSpawn, boolean noOceans, List<String> biomes, List<String> biomeTypes, boolean biomesAreBlacklist, @Nullable int[][] at, @Nullable String locateAs, List<DungeonSizeDef> sizes, int shrink, int surfaceBuffer, @Nullable Integer minY, int[] levelHeight, int[] roomSize, Corridor corridor, int loops, Map<String, Degree> rolls, List<RoomKindDef> roomKinds, Entrance entrance, @Nullable Boss boss, List<PickDef> shafts, @Nullable Difficulty difficulty, int supportDepth, @Nullable Decay decay, List<LevelRule> levelRules, @Nullable RespawnDef respawn) {
        this.key = key;
        this.name = name;
        this.themes = themes;
        this.dimensions = dimensions;
        this.dimensionsAreBlacklist = dimensionsAreBlacklist;
        this.chance = chance;
        this.minDistance = minDistance;
        this.group = group;
        this.minDistanceFromSpawn = minDistanceFromSpawn;
        this.noOceans = noOceans;
        this.biomes = biomes;
        this.biomeTypes = biomeTypes;
        this.biomesAreBlacklist = biomesAreBlacklist;
        this.at = at;
        this.locateAs = locateAs;
        this.sizes = sizes;
        this.shrink = shrink;
        this.surfaceBuffer = surfaceBuffer;
        this.minY = minY;
        this.levelHeight = levelHeight;
        this.roomSize = roomSize;
        this.corridor = corridor;
        this.loops = loops;
        this.rolls = rolls;
        this.roomKinds = roomKinds;
        this.entrance = entrance;
        this.boss = boss;
        this.shafts = shafts;
        this.difficulty = difficulty;
        this.supportDepth = supportDepth;
        this.decay = decay;
        this.levelRules = levelRules;
        this.respawn = respawn;
    }

    public boolean allowsDimension(int dimension) { return BiomeFilter.allowsDimension(dimensions, dimensionsAreBlacklist, dimension); }

    public static double depthOf(int index, int levels) { return index / (double) Math.max(1, levels - 1); }

    public List<RoomKindDef> kindsAt(double depth) {
        List<RoomKindDef> kinds = roomKinds;
        for (LevelRule rule : levelRules) {
            if (rule.fromDepth <= depth && rule.roomKinds != null) { kinds = rule.roomKinds; }
        }
        return kinds;
    }

    public static final class Degree {
        public static final String[] NAMES = {"none", "few", "some", "plenty", "heaps", "always"};
        public static final int[] CHANCES = {0, 10, 30, 50, 90, 100};
        public final int[] weights;

        public Degree(int[] weights) { this.weights = weights; }

        public static boolean chance(Random random, int percent) { return random.nextInt(100) < percent; }

        public static Degree named(int index) {
            int[] weights = new int[NAMES.length];
            weights[index] = 1;
            return new Degree(weights);
        }

        public static int indexOf(String name) {
            for (int i = 0; i < NAMES.length; i++) {
                if (NAMES[i].equals(name)) { return i; }
            }
            return -1;
        }

        public boolean valid() {
            for (int weight : weights) {
                if (weight > 0) { return true; }
            }
            return false;
        }

        public static void apply(int[] into, Map<String, Degree> source, long seed, String stream, int base) {
            for (int i = 0; i < into.length; i++) {
                Degree degree = source.get(ROLLS.get(i));
                if (degree != null && degree.valid()) { into[i] = WeightedPicks.index(degree.weights, SeedStreams.random(seed, stream, base + i)); }
            }
        }
    }

    public static final class Corridor {
        public static final int DEFAULT_WIDTH = 3;
        public static final int DEFAULT_HEIGHT = 3;
        public static final int MIN_WIDTH = 1;
        public static final int MAX_WIDTH = 5;
        public static final int MIN_HEIGHT = 2;
        public final int width;
        public final int height;

        public Corridor(int width, int height) {
            this.width = width;
            this.height = height;
        }
    }

    public static final class Entrance {
        public final List<PickDef> types;
        public final List<PickDef> topRoomTemplates;
        @Nullable public final Degree extra;

        public Entrance(List<PickDef> types, List<PickDef> topRoomTemplates, @Nullable Degree extra) {
            this.types = types;
            this.topRoomTemplates = topRoomTemplates;
            this.extra = extra;
        }

        public static Entrance simple() { return new Entrance(Collections.singletonList(new PickDef("simple", 1)), Collections.emptyList(), null); }
    }

    public static final class Boss {
        public static final String DEFAULT_MOB = "boss";
        public static final String DEFAULT_LOOT = "boss";
        public static final int DEFAULT_COUNT = 1;
        public final List<PickDef> templates;
        @Nullable public final int[] size;
        @Nullable public final int[] height;
        public final String mob;
        public final String loot;
        public final int count;

        public Boss(List<PickDef> templates, @Nullable int[] size, @Nullable int[] height, String mob, String loot, int count) {
            this.templates = templates;
            this.size = size;
            this.height = height;
            this.mob = mob;
            this.loot = loot;
            this.count = count;
        }
    }

    public static final class Difficulty {
        public static final Map<String, Difficulty> PRESETS = presets();
        public final String preset;
        @Nullable public final Integer spawners;
        @Nullable public final Integer promote;
        @Nullable public final Integer maxTier;
        @Nullable public final Integer nodeTier;
        @Nullable public final Integer bossTier;
        @Nullable public final Integer blocksPerSpawner;
        @Nullable public final Boolean entranceMobs;

        public Difficulty(String preset, @Nullable Integer spawners, @Nullable Integer promote, @Nullable Integer maxTier, @Nullable Integer nodeTier, @Nullable Integer bossTier, @Nullable Integer blocksPerSpawner, @Nullable Boolean entranceMobs) {
            this.preset = preset;
            this.spawners = spawners;
            this.promote = promote;
            this.maxTier = maxTier;
            this.nodeTier = nodeTier;
            this.bossTier = bossTier;
            this.blocksPerSpawner = blocksPerSpawner;
            this.entranceMobs = entranceMobs;
        }

        private static Map<String, Difficulty> presets() {
            Map<String, Difficulty> table = new LinkedHashMap<>();
            table.put("baby", preset("baby", 30, 0, 0, 0, 0, 371, false));
            table.put("noob", preset("noob", 40, 10, 1, 1, 1, 248, false));
            table.put(DEFAULT_DIFFICULTY, preset(DEFAULT_DIFFICULTY, 50, 20, 1, 1, 2, 180, false));
            table.put("hard", preset("hard", 60, 30, 2, 2, 3, 136, true));
            table.put("nuts", preset("nuts", 70, 50, 2, 2, 4, 50, true));
            return Collections.unmodifiableMap(table);
        }

        private static Difficulty preset(String name, int spawners, int promote, int maxTier, int nodeTier, int bossTier, int blocksPerSpawner, boolean entranceMobs) {
            return new Difficulty(name, spawners, promote, maxTier, nodeTier, bossTier, blocksPerSpawner, entranceMobs);
        }

        public Difficulty resolved() {
            Difficulty base = PRESETS.get(preset);
            if (base == null) { return this; }
            return new Difficulty(preset, Fallbacks.orDefault(spawners, base.spawners), Fallbacks.orDefault(promote, base.promote), Fallbacks.orDefault(maxTier, base.maxTier), Fallbacks.orDefault(nodeTier, base.nodeTier), Fallbacks.orDefault(bossTier, base.bossTier), Fallbacks.orDefault(blocksPerSpawner, base.blocksPerSpawner), Fallbacks.orDefault(entranceMobs, base.entranceMobs));
        }
    }

    public static final class Decay {
        public static final int DEFAULT_BASE = 15;
        public static final int DEFAULT_PER_DEPTH = 20;
        public final int base;
        public final int perDepth;

        public Decay(int base, int perDepth) {
            this.base = base;
            this.perDepth = perDepth;
        }
    }

    public static final class LevelRule {
        public final double fromDepth;
        @Nullable public final ResourceLocation theme;
        @Nullable public final Map<String, Degree> rolls;
        @Nullable public final Difficulty difficulty;
        @Nullable public final Decay decay;
        @Nullable public final Integer lootBonus;
        @Nullable public final List<RoomKindDef> roomKinds;

        public LevelRule(double fromDepth, @Nullable ResourceLocation theme, @Nullable Map<String, Degree> rolls, @Nullable Difficulty difficulty, @Nullable Decay decay, @Nullable Integer lootBonus, @Nullable List<RoomKindDef> roomKinds) {
            this.fromDepth = fromDepth;
            this.theme = theme;
            this.rolls = rolls;
            this.difficulty = difficulty;
            this.decay = decay;
            this.lootBonus = lootBonus;
            this.roomKinds = roomKinds;
        }
    }
}
