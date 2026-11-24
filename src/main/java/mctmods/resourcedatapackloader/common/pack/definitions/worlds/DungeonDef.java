package mctmods.resourcedatapackloader.common.pack.definitions.worlds;

import mctmods.resourcedatapackloader.common.pack.definitions.games.PickDef;
import mctmods.resourcedatapackloader.common.util.worldgen.SeedStreams;
import mctmods.resourcedatapackloader.common.util.worldgen.WeightedPicks;
import mctmods.resourcedatapackloader.core.util.Fallbacks;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;

public record DungeonDef(ResourceLocation key, String name, List<PickDef> themes, List<String> dimensions, boolean dimensionsAreBlacklist, int chance, int minDistance, String group, int minDistanceFromSpawn, boolean noOceans, List<String> biomes, List<String> biomeTypes, boolean biomesAreBlacklist, @Nullable int[][] at, @Nullable String locateAs, List<DungeonSizeDef> sizes, int shrink, int surfaceBuffer, @Nullable Integer minY, int[] levelHeight, int[] roomSize, Corridor corridor, int loops, Map<String, Degree> rolls, List<RoomKindDef> roomKinds, Entrance entrance, @Nullable Boss boss, List<PickDef> shafts, @Nullable Difficulty difficulty, int supportDepth, @Nullable Decay decay, List<LevelRule> levelRules, @Nullable RespawnDef respawn) {
    public static final int DEFAULT_CHANCE = 75;
    public static final int DEFAULT_MIN_DISTANCE = 600;
    public static final int DEFAULT_SURFACE_BUFFER = 7;
    public static final int DEFAULT_LOOPS = 25;
    public static final int DEFAULT_SUPPORT_DEPTH = 8;
    public static final int[] DEFAULT_LEVEL_HEIGHT = {5, 8};
    public static final int[] DEFAULT_ROOM_SIZE = {5, 15};
    public static final String DEFAULT_DIFFICULTY = "norm";
    public static final List<String> SHAFTS = List.of("ladder", "spiral", "stairs");
    public static final List<String> ROLLS = List.of("outside", "liquids", "subrooms", "islands", "pillars", "symmetry", "variability", "degeneracy", "complexity", "vertical", "entrances", "fences", "naturals");

    public static int rollIndex(String name) { return ROLLS.indexOf(name); }

    public static double depthOf(int index, int levels) { return index / (double) Math.max(1, levels - 1); }

    public boolean allowsDimension(String dimension) { return dimensions.isEmpty() || dimensions.contains(dimension) != dimensionsAreBlacklist; }

    public List<RoomKindDef> kindsAt(double depth) {
        List<RoomKindDef> kinds = roomKinds;
        for (LevelRule rule : levelRules) {
            if (rule.fromDepth <= depth && rule.roomKinds != null) { kinds = rule.roomKinds; }
        }
        return kinds;
    }

    public record Degree(int[] weights) {
        public static final String[] NAMES = {"none", "few", "some", "plenty", "heaps", "always"};
        public static final int[] CHANCES = {0, 10, 30, 50, 90, 100};

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
                if (degree != null && degree.valid()) { into[i] = WeightedPicks.index(degree.weights(), SeedStreams.random(seed, stream, base + i)); }
            }
        }
    }

    public record Corridor(int width, int height) {
        public static final int DEFAULT_WIDTH = 3;
        public static final int DEFAULT_HEIGHT = 3;
        public static final int MIN_WIDTH = 1;
        public static final int MAX_WIDTH = 5;
        public static final int MIN_HEIGHT = 2;
    }

    public record Entrance(List<PickDef> types, List<PickDef> topRoomTemplates, @Nullable Degree extra) {
        public static Entrance simple() { return new Entrance(List.of(new PickDef("simple", 1)), List.of(), null); }
    }

    public record Boss(List<PickDef> templates, @Nullable int[] size, @Nullable int[] height, String mob, String loot, int count) {
        public static final String DEFAULT_MOB = "boss";
        public static final String DEFAULT_LOOT = "boss";
        public static final int DEFAULT_COUNT = 1;
    }

    public record Difficulty(String preset, @Nullable Integer spawners, @Nullable Integer promote, @Nullable Integer maxTier, @Nullable Integer nodeTier, @Nullable Integer bossTier, @Nullable Integer blocksPerSpawner, @Nullable Boolean entranceMobs) {
        public static final Map<String, Difficulty> PRESETS = presets();

        private static Map<String, Difficulty> presets() {
            Map<String, Difficulty> table = new LinkedHashMap<>();
            table.put("baby", preset("baby", 30, 0, 0, 0, 0, 371, false));
            table.put("noob", preset("noob", 40, 10, 1, 1, 1, 248, false));
            table.put(DEFAULT_DIFFICULTY, preset(DEFAULT_DIFFICULTY, 50, 20, 1, 1, 2, 180, false));
            table.put("hard", preset("hard", 60, 30, 2, 2, 3, 136, true));
            table.put("nuts", preset("nuts", 70, 50, 2, 2, 4, 50, true));
            return Map.copyOf(table);
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

    public record Decay(int base, int perDepth) {
        public static final int DEFAULT_BASE = 15;
        public static final int DEFAULT_PER_DEPTH = 20;
    }

    public record LevelRule(double fromDepth, @Nullable ResourceLocation theme, @Nullable Map<String, Degree> rolls, @Nullable Difficulty difficulty, @Nullable Decay decay, @Nullable Integer lootBonus, @Nullable List<RoomKindDef> roomKinds) {}
}
