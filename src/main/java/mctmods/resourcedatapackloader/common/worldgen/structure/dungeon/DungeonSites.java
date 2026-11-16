package mctmods.resourcedatapackloader.common.worldgen.structure.dungeon;

import mctmods.resourcedatapackloader.common.pack.definitions.games.PickDef;
import mctmods.resourcedatapackloader.common.pack.definitions.worlds.DungeonDef;
import mctmods.resourcedatapackloader.common.pack.definitions.worlds.DungeonSizeDef;
import mctmods.resourcedatapackloader.common.pack.definitions.worlds.DungeonThemeDef;
import mctmods.resourcedatapackloader.common.pack.definitions.worlds.WorldgenDef;
import mctmods.resourcedatapackloader.common.util.worldgen.BiomeFilter;
import mctmods.resourcedatapackloader.common.util.worldgen.Biomes;
import mctmods.resourcedatapackloader.common.util.worldgen.GenHeights;
import mctmods.resourcedatapackloader.common.util.worldgen.SeedStreams;
import mctmods.resourcedatapackloader.common.worldgen.structure.city.layout.BeardSurface;
import mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.plan.PlanRecords;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.math.MathUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.WeakHashMap;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraftforge.common.BiomeDictionary;

public final class DungeonSites {
    private static final int CLEAR = 16;
    private static final int GAP = 8;
    private static final int TABLE_CELLS = 64;
    private static final int CANDIDATE_CELLS = 8192;
    private static final int[] CORNER_X = {0, -1, 1, -1, 1};
    private static final int[] CORNER_Z = {0, -1, -1, 1, 1};
    private static final Map<String, Shape> SHAPES = new TreeMap<>();
    private static final Map<String, Group> GROUPS = new TreeMap<>();
    private static final Map<World, State> STATES = new WeakHashMap<>();
    private static boolean prepared = false;

    private DungeonSites() {}

    public static final class Site {
        public final DungeonDef def;
        public final int originX;
        public final int originZ;
        public final DungeonSizeDef size;
        public final List<PickDef> themes;
        public final int surfaceY;
        public final Biome biome;
        public final long priority;
        public final int radius;

        private Site(DungeonDef def, int originX, int originZ, DungeonSizeDef size, List<PickDef> themes, int surfaceY, Biome biome, long priority, int radius) {
            this.def = def;
            this.originX = originX;
            this.originZ = originZ;
            this.size = size;
            this.themes = themes;
            this.surfaceY = surfaceY;
            this.biome = biome;
            this.priority = priority;
            this.radius = radius;
        }

        public boolean reaches(StructureBoundingBox window) { return originX + radius >= window.minX && originX - radius <= window.maxX && originZ + radius >= window.minZ && originZ - radius <= window.maxZ; }
    }

    private static final class Cell {
        private final String id;
        private final int x;
        private final int z;

        private Cell(String id, int x, int z) {
            this.id = id;
            this.x = x;
            this.z = z;
        }

        @Override public boolean equals(Object other) { return other instanceof Cell && ((Cell) other).x == x && ((Cell) other).z == z && ((Cell) other).id.equals(id); }

        @Override public int hashCode() { return Objects.hash(id, x, z); }
    }

    private static final class Lru<V> extends LinkedHashMap<Cell, V> {
        private static final long serialVersionUID = 1L;
        private final int capacity;

        private Lru(int capacity) {
            super(16, 0.75F, true);
            this.capacity = capacity;
        }

        @Override protected boolean removeEldestEntry(Map.Entry<Cell, V> eldest) { return size() > capacity; }
    }

    private static final class State {
        private final Lru<Site> candidates = new Lru<>(CANDIDATE_CELLS);
        private final Lru<List<Site>> table = new Lru<>(TABLE_CELLS);
        private final Set<String> logged = new HashSet<>();
    }

    private static final class Shape {
        private final DungeonDef def;
        private final String id;
        private final int margin;
        private final int rMax;
        private final int pitch;
        private Set<Biome> named;
        private List<BiomeDictionary.Type> types;

        private Shape(DungeonDef def) {
            this.def = def;
            this.id = def.key.toString();
            this.margin = (def.corridor.width + 1) / 2 + 2;
            int widest = 0;
            for (DungeonSizeDef size : def.sizes) { widest = Math.max(widest, size.footprint / 2); }
            this.rMax = widest + def.roomSize[1] + 2 * margin;
            this.pitch = Math.max(def.minDistance, 2 * rMax + 2 * CLEAR);
        }

        private int radius(DungeonSizeDef size) { return size.footprint / 2 + def.roomSize[1] + 2 * margin; }

        private boolean blocked(Biome biome) {
            if (def.biomes.isEmpty() && def.biomeTypes.isEmpty()) { return false; }
            if (named == null) { resolve(); }
            return BiomeFilter.blocked(named, types, def.biomesAreBlacklist, biome);
        }

        private void resolve() {
            Set<Biome> biomes = new HashSet<>();
            for (String name : def.biomes) {
                Biome found = Biomes.byName(name, def.key.getNamespace());
                if (found != null) { biomes.add(found); }
                else { ContentLog.LOGGER.onceWarn("Dungeon {} names biome {}, which is not registered, leaving it out", id, name); }
            }
            List<BiomeDictionary.Type> kinds = new ArrayList<>();
            for (String name : def.biomeTypes) {
                BiomeDictionary.Type type = WorldgenDef.knownType(name);
                if (type != null) { kinds.add(type); }
                else { ContentLog.LOGGER.onceWarn("Dungeon {} names biome type '{}', which no biome type answers to", id, name); }
            }
            types = kinds;
            named = biomes;
        }
    }

    private static final class Group {
        private final String id;
        private final List<Shape> members = new ArrayList<>();
        private int minDistance;
        private int pitch;
        private int rMax;

        private Group(String id) { this.id = id; }
    }

    private static void prepare() {
        if (prepared) { return; }
        prepared = true;
        for (DungeonDef def : ContentDungeons.all()) {
            Shape shape = new Shape(def);
            SHAPES.put(shape.id, shape);
            Group group = GROUPS.computeIfAbsent(def.group, Group::new);
            group.members.add(shape);
            group.minDistance = Math.max(group.minDistance, def.minDistance);
            group.pitch = Math.max(group.pitch, shape.pitch);
            group.rMax = Math.max(group.rMax, shape.rMax);
        }
    }

    private static State stateOf(World world) { return STATES.computeIfAbsent(world, held -> new State()); }

    private static List<Shape> shapesFor(Group group, int dimension) {
        List<Shape> found = new ArrayList<>();
        for (Shape shape : group.members) {
            if (shape.def.allowsDimension(dimension)) { found.add(shape); }
        }
        return found;
    }

    public static synchronized List<Site> sitesTouching(World world, StructureBoundingBox window) { return collect(world, window.minX, window.minZ, window.maxX, window.maxZ, site -> site.reaches(window)); }

    public static synchronized List<Site> sitesNear(World world, BlockPos pos, int radius) {
        long limit = (long) radius * radius;
        return collect(world, pos.getX() - radius, pos.getZ() - radius, pos.getX() + radius, pos.getZ() + radius, site -> {
            long dx = site.originX - pos.getX();
            long dz = site.originZ - pos.getZ();
            return dx * dx + dz * dz <= limit;
        });
    }

    private static List<Site> collect(World world, int minX, int minZ, int maxX, int maxZ, Predicate<Site> wanted) {
        if (world.isRemote) { return Collections.emptyList(); }
        prepare();
        int dimension = world.provider.getDimension();
        State state = stateOf(world);
        List<Site> found = new ArrayList<>();
        for (Group group : GROUPS.values()) {
            List<Shape> members = shapesFor(group, dimension);
            if (members.isEmpty()) { continue; }
            for (int cellX = Math.floorDiv(minX - group.rMax, group.pitch); cellX <= Math.floorDiv(maxX + group.rMax, group.pitch); cellX++) {
                for (int cellZ = Math.floorDiv(minZ - group.rMax, group.pitch); cellZ <= Math.floorDiv(maxZ + group.rMax, group.pitch); cellZ++) {
                    for (Site site : groupSites(world, state, group, members, cellX, cellZ)) {
                        if (wanted.test(site)) { found.add(site); }
                    }
                }
            }
        }
        return found;
    }

    private static List<Site> groupSites(World world, State state, Group group, List<Shape> members, int cellX, int cellZ) {
        Cell cell = new Cell(group.id, cellX, cellZ);
        List<Site> held = state.table.get(cell);
        if (held != null) { return held; }
        int minX = cellX * group.pitch;
        int minZ = cellZ * group.pitch;
        List<Site> survivors = new ArrayList<>();
        for (Shape shape : members) {
            for (Site site : near(world, state, shape, minX, minZ, minX + group.pitch - 1, minZ + group.pitch - 1)) {
                if (survives(world, state, site, group, members)) { survivors.add(site); }
            }
        }
        for (Site site : survivors) {
            if (ContentLog.LOGGER.debugEnabled() && state.logged.add(site.def.key + "@" + site.originX + "," + site.originZ)) { ContentLog.LOGGER.debug("dungeon candidate key={} site={},{} priority={} size={}", site.def.key, site.originX, site.originZ, site.priority, site.size.name); }
        }
        List<Site> kept = Collections.unmodifiableList(survivors);
        state.table.put(cell, kept);
        return kept;
    }

    private static boolean survives(World world, State state, Site site, Group group, List<Shape> members) {
        if (site.priority == Long.MAX_VALUE) { return true; }
        int apart = group.minDistance - 1;
        if (apart >= 0) {
            for (Shape shape : members) {
                for (Site other : near(world, state, shape, site.originX - apart, site.originZ - apart, site.originX + apart, site.originZ + apart)) {
                    if (beats(other, site)) { return false; }
                }
            }
        }
        int dimension = world.provider.getDimension();
        for (Shape shape : SHAPES.values()) {
            if (shape.def == site.def || !shape.def.allowsDimension(dimension)) { continue; }
            int reach = site.radius + CLEAR + shape.rMax;
            for (Site other : near(world, state, shape, site.originX - reach, site.originZ - reach, site.originX + reach, site.originZ + reach)) {
                int touch = site.radius + other.radius + 2 * GAP;
                if (Math.abs(other.originX - site.originX) <= touch && Math.abs(other.originZ - site.originZ) <= touch && beats(other, site)) { return false; }
            }
        }
        return true;
    }

    private static boolean beats(Site first, Site second) {
        if (first.priority != second.priority) { return first.priority > second.priority; }
        int byKey = first.def.key.toString().compareTo(second.def.key.toString());
        if (byKey != 0) { return byKey > 0; }
        if (first.originX != second.originX) { return first.originX > second.originX; }
        return first.originZ > second.originZ;
    }

    private static List<Site> near(World world, State state, Shape shape, int minX, int minZ, int maxX, int maxZ) {
        List<Site> found = new ArrayList<>();
        for (int cellX = Math.floorDiv(minX, shape.pitch); cellX <= Math.floorDiv(maxX, shape.pitch); cellX++) {
            for (int cellZ = Math.floorDiv(minZ, shape.pitch); cellZ <= Math.floorDiv(maxZ, shape.pitch); cellZ++) {
                Site site = candidate(world, state, shape, cellX, cellZ);
                if (site != null && site.originX >= minX && site.originX <= maxX && site.originZ >= minZ && site.originZ <= maxZ) { found.add(site); }
            }
        }
        if (shape.def.at == null) { return found; }
        for (int[] spot : shape.def.at) {
            if (spot[0] < minX || spot[0] > maxX || spot[1] < minZ || spot[1] > maxZ) { continue; }
            Cell cell = new Cell("@" + shape.id, spot[0], spot[1]);
            if (!state.candidates.containsKey(cell)) { state.candidates.put(cell, pinned(world, shape, spot[0], spot[1])); }
            Site site = state.candidates.get(cell);
            if (site != null) { found.add(site); }
        }
        return found;
    }

    @Nullable private static Site candidate(World world, State state, Shape shape, int cellX, int cellZ) {
        Cell cell = new Cell(shape.id, cellX, cellZ);
        if (state.candidates.containsKey(cell)) { return state.candidates.get(cell); }
        Site made = grid(world, shape, cellX, cellZ);
        state.candidates.put(cell, made);
        return made;
    }

    @Nullable private static Site grid(World world, Shape shape, int cellX, int cellZ) {
        DungeonDef def = shape.def;
        long seed = MathUtil.mix(world.getSeed() ^ def.key.hashCode(), cellX, 1, cellZ);
        if (Math.floorMod(seed >>> 16, 100) >= def.chance) { return null; }
        DungeonSizeDef size = pickSize(def, seed);
        long give = shape.pitch - 2L * shape.rMax - 2 * CLEAR + 1;
        int originX = cellX * shape.pitch + shape.rMax + CLEAR + (int) Math.floorMod(seed, give);
        int originZ = cellZ * shape.pitch + shape.rMax + CLEAR + (int) Math.floorMod(seed >>> 40, give);
        BlockPos spawn = world.getSpawnPoint();
        if (def.minDistanceFromSpawn > 0 && Math.hypot(originX - spawn.getX(), originZ - spawn.getZ()) < def.minDistanceFromSpawn) { return null; }
        int radius = shape.radius(size);
        Biome centre = world.getBiome(new BlockPos(originX, 64, originZ));
        for (int i = 0; i < CORNER_X.length; i++) {
            Biome biome = i == 0 ? centre : world.getBiome(new BlockPos(originX + CORNER_X[i] * radius, 64, originZ + CORNER_Z[i] * radius));
            if (shape.blocked(biome) || (def.noOceans && BiomeDictionary.hasType(biome, BiomeDictionary.Type.OCEAN))) { return null; }
        }
        int surface = surfaceAt(world, originX, originZ);
        List<PickDef> fitting = fitting(world, shape, size, centre, surface);
        if (fitting.isEmpty()) { return null; }
        return new Site(def, originX, originZ, size, fitting, surface, centre, MathUtil.mix(world.getSeed() ^ def.key.hashCode(), cellX, 2, cellZ), radius);
    }

    @Nullable private static Site pinned(World world, Shape shape, int originX, int originZ) {
        DungeonDef def = shape.def;
        DungeonSizeDef size = pickSize(def, MathUtil.mix(world.getSeed() ^ def.key.hashCode(), originX, 0, originZ));
        Biome centre = world.getBiome(new BlockPos(originX, 64, originZ));
        int surface = surfaceAt(world, originX, originZ);
        List<PickDef> themes = ContentDungeonThemes.usable(def.themes, world.provider.getDimension(), centre, lowest(world, def), surface - def.surfaceBuffer);
        if (themes.isEmpty()) {
            ContentLog.LOGGER.onceWarn("Dungeon {} is pinned at {}, {} but none of its themes suits that place, so no dungeon is built there", shape.id, originX, originZ);
            return null;
        }
        return new Site(def, originX, originZ, size, themes, surface, centre, Long.MAX_VALUE, shape.radius(size));
    }

    private static DungeonSizeDef pickSize(DungeonDef def, long seed) {
        int total = 0;
        for (DungeonSizeDef size : def.sizes) { total += size.weight; }
        long roll = Math.floorMod(SeedStreams.seed(seed, "size", 0) >>> 8, total);
        for (DungeonSizeDef size : def.sizes) {
            roll -= size.weight;
            if (roll < 0) { return size; }
        }
        return def.sizes.get(def.sizes.size() - 1);
    }

    private static int surfaceAt(World world, int blockX, int blockZ) {
        int top = BeardSurface.surfaceAt(world, blockX, blockZ);
        return top < 0 ? world.getSeaLevel() - 1 : top;
    }

    public static int lowest(World world, DungeonDef def) {
        int floor = GenHeights.floor(world, 0);
        return def.minY == null ? floor : Math.max(floor, def.minY);
    }

    private static List<PickDef> fitting(World world, Shape shape, DungeonSizeDef size, Biome biome, int surface) {
        DungeonDef def = shape.def;
        int lowest = lowest(world, def);
        int highest = surface - def.surfaceBuffer;
        int needed = size.levels[0] * def.levelHeight[0] + (size.levels[0] - 1) * PlanRecords.SLAB_GAP;
        List<PickDef> fitting = new ArrayList<>();
        for (PickDef pick : ContentDungeonThemes.usable(def.themes, world.provider.getDimension(), biome, lowest, highest)) {
            DungeonThemeDef theme = ContentDungeonThemes.byName(pick.name);
            int bottom = theme == null || theme.minY == null ? lowest : Math.max(lowest, theme.minY);
            if (highest - bottom + 1 >= needed) { fitting.add(pick); }
        }
        return fitting;
    }
}
