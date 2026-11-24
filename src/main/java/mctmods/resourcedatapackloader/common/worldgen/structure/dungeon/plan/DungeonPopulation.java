package mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.plan;

import mctmods.resourcedatapackloader.common.pack.definitions.worlds.DungeonDef;
import mctmods.resourcedatapackloader.common.pack.definitions.worlds.DungeonDef.Difficulty;
import mctmods.resourcedatapackloader.common.pack.definitions.worlds.DungeonDef.LevelRule;
import mctmods.resourcedatapackloader.common.pack.definitions.worlds.DungeonThemeDef;
import mctmods.resourcedatapackloader.common.pack.definitions.worlds.DungeonThemeDef.LootEntry;
import mctmods.resourcedatapackloader.common.pack.definitions.worlds.DungeonThemeDef.Mob;
import mctmods.resourcedatapackloader.common.pack.definitions.worlds.DungeonThemeDef.SpawnGroup;
import mctmods.resourcedatapackloader.common.pack.definitions.worlds.RoomKindDef;
import mctmods.resourcedatapackloader.common.pack.definitions.worlds.RoomKindDef.Amount;
import mctmods.resourcedatapackloader.common.util.worldgen.SeedStreams;
import mctmods.resourcedatapackloader.common.util.worldgen.WeightedPicks;
import mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.ContentDungeonThemes;
import mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.plan.DungeonPlan.Link;
import mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.plan.DungeonPlan.Room;
import mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.plan.PlanRecords.Chest;
import mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.plan.PlanRecords.Feature;
import mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.plan.PlanRecords.Shaft;
import mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.plan.PlanRecords.Spawner;
import mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.write.DungeonShapes;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.math.MathUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public final class DungeonPopulation {
    public static final List<String> TIERS = Collections.unmodifiableList(Arrays.asList("common", "hard", "brute", "elite", "boss"));
    private static final Set<String> MISSING = new HashSet<>();
    private static final int JITTER = 20;
    private static final int CEILING_IN = 4;
    private static final int WEAK_IN = 5;
    private static final int LANDING_REACH = 1;

    private final DungeonPlan.Builder plan;
    private final DungeonPlan.Level.Builder level;
    private final DungeonDef def;
    private final List<RoomKindDef> kinds;
    private final DungeonThemeDef theme;
    private final int spawnPercent;
    private final int perSpawner;
    private final int promote;
    private final int maxTier;
    private final int nodeTier;
    private final int bossTier;
    private final boolean entranceMobs;
    private final double depth;
    private final long levelSeed;
    private final int lootLevels;
    private final int lootBonus;

    private DungeonPopulation(DungeonPlan.Builder plan, DungeonPlan.Level.Builder level, DungeonDef def, List<RoomKindDef> kinds, DungeonThemeDef theme, double depth, long levelSeed) {
        this.plan = plan;
        this.level = level;
        this.def = def;
        this.kinds = kinds;
        this.theme = theme;
        this.depth = depth;
        this.levelSeed = levelSeed;
        Difficulty ruled = def.difficulty != null ? def.difficulty : theme.difficulty != null ? theme.difficulty : new Difficulty(DungeonDef.DEFAULT_DIFFICULTY, null, null, null, null, null, null, null);
        int bonus = theme.lootBonusOrDefault();
        Integer ruleBonus = null;
        for (LevelRule rule : def.levelRules) {
            if (rule.fromDepth > depth) { continue; }
            if (rule.difficulty != null) { ruled = rule.difficulty; }
            if (rule.lootBonus != null) { ruleBonus = rule.lootBonus; }
        }
        Difficulty held = ruled.resolved();
        this.spawnPercent = orZero(held.spawners);
        this.perSpawner = Math.max(1, orZero(held.blocksPerSpawner));
        this.promote = orZero(held.promote);
        this.maxTier = orZero(held.maxTier);
        this.nodeTier = orZero(held.nodeTier);
        this.bossTier = orZero(held.bossTier);
        this.entranceMobs = Boolean.TRUE.equals(held.entranceMobs);
        this.lootBonus = bonus + (ruleBonus == null ? 0 : ruleBonus);
        this.lootLevels = theme.lootLevelsOrDefault();
    }

    private static int orZero(@Nullable Integer value) { return value == null ? 0 : value; }

    static void populate(DungeonPlan.Builder plan, DungeonPlan.Level.Builder level, DungeonDef def, List<RoomKindDef> kinds, double depth, long levelSeed) {
        DungeonThemeDef theme = ContentDungeonThemes.byName(level.themeId());
        if (theme == null) { return; }
        DungeonPopulation population = new DungeonPopulation(plan, level, def, kinds, theme, depth, levelSeed);
        for (int i = 0; i < level.roomCount(); i++) {
            Room room = level.room(i);
            if (room.templateId == null) { population.room(room); }
        }
    }

    @Nullable public static Draw draw(DungeonPlan plan, DungeonDef def, DungeonPlan.Level level, Room room) {
        DungeonThemeDef theme = ContentDungeonThemes.byName(level.themeId);
        if (theme == null) { return null; }
        double depth = DungeonDef.depthOf(level.index, plan.levels.size());
        DungeonPopulation population = new DungeonPopulation(null, null, def, def.kindsAt(depth), theme, depth, SeedStreams.seed(plan.site.siteSeed, "population", level.index));
        return new Draw(population, room, population.kindOf(room));
    }

    public static final class Draw {
        private final DungeonPopulation population;
        private final Room room;
        private final RoomKindDef kind;

        private Draw(DungeonPopulation population, Room room, RoomKindDef kind) {
            this.population = population;
            this.room = room;
            this.kind = kind;
        }

        @Nullable public String mob(long seed) {
            Random random = new Random(seed);
            DungeonDef.Boss boss = room.boss ? population.def.boss : null;
            Mob mob = boss != null ? population.pickMob(boss.mob, tierNamed(boss.mob, TIERS.size() - 1), room.kind, random) : population.pickMob(kind.mobs, population.rollTier(room, tierNamed(kind.mobs, 0), random), room.kind, random);
            if (mob == null) { return null; }
            return mob.nbt == null ? mob.id : mob.id + mob.nbt;
        }

        @Nullable public String loot(long seed) {
            DungeonDef.Boss boss = room.boss ? population.def.boss : null;
            return loot(boss != null ? boss.loot : kind.loot, seed);
        }

        @Nullable public String loot(String set, long seed) {
            Random random = new Random(seed);
            DungeonDef.Boss boss = room.boss ? population.def.boss : null;
            int top = boss != null ? tierNamed(boss.mob, TIERS.size() - 1) : tierNamed(kind.mobs, 0);
            LootEntry entry = population.pickLoot(set, population.chestLevel(room, top, 0), random);
            return entry == null ? null : entry.table.toString();
        }

        public List<String> decor() { return kind.decor; }
    }

    private void room(Room room) {
        RoomKindDef kind = kindOf(room);
        Random random = SeedStreams.random(levelSeed, "population", room.index);
        List<int[]> cells = cells(room);
        Collections.shuffle(cells, random);
        int[] top = {0};
        DungeonDef.Boss boss = room.boss ? def.boss : null;
        int count = boss != null ? bossSpawners(boss, room, random, cells, top) : mobSpawners(room, kind, random, cells, top);
        if (boss != null) { bossChest(boss, room, random, cells, top[0], count); }
        else { roomChests(room, kind, random, cells, top[0], count); }
    }

    private int bossSpawners(DungeonDef.Boss boss, Room room, Random random, List<int[]> cells, int[] top) {
        int tier = tierNamed(boss.mob, TIERS.size() - 1);
        int placed = 0;
        for (int n = 0; n < Math.max(1, boss.count); n++) {
            if (place(room, boss.mob, tier, random, cells, top)) { placed++; }
        }
        return placed;
    }

    private int mobSpawners(Room room, RoomKindDef kind, Random random, List<int[]> cells, int[] top) {
        int count = spawnerCount(room, kind, random);
        int base = tierNamed(kind.mobs, 0);
        int placed = 0;
        for (int n = 0; n < count; n++) {
            if (place(room, kind.mobs, rollTier(room, base, random), random, cells, top)) { placed++; }
        }
        return placed;
    }

    private int spawnerCount(Room room, RoomKindDef kind, Random random) {
        Amount amount = kind.spawners;
        if (amount != null && !amount.percent) { return MathUtil.randomIn(random, amount.min, amount.max); }
        int chance = amount != null ? amount.min : spawnPercent;
        boolean allowed = amount != null || !room.entrance || entranceMobs;
        boolean gets = room.node && amount == null || allowed && DungeonDef.Degree.chance(random, chance);
        double area = (room.x1 - room.x0 + 1) * (double) (room.z1 - room.z0 + 1);
        double expected = area / perSpawner * (1.0D + (random.nextInt(2 * JITTER + 1) - JITTER) / 100.0D);
        int count = (int) expected + (random.nextDouble() < expected - (int) expected ? 1 : 0);
        if (!gets) { return 0; }
        return room.node ? Math.max(1, count) : count;
    }

    private int rollTier(Room room, int base, Random random) {
        int cap = room.node ? bossTier : maxTier;
        int tier = room.node ? Math.max(base, nodeTier) : base;
        while (tier < cap && DungeonDef.Degree.chance(random, promote)) {
            tier++;
        }
        tier = Math.max(tier, (int) Math.floor(depth * maxTier / 2.0D));
        return Math.min(tier, TIERS.size() - 1);
    }

    private static int tierNamed(String name, int otherwise) {
        int at = TIERS.indexOf(name);
        return at < 0 ? otherwise : at;
    }

    private boolean place(Room room, String spec, int tier, Random random, List<int[]> cells, int[] top) {
        Mob mob = pickMob(spec, tier, room.kind, random);
        if (mob == null || cells.isEmpty()) { return false; }
        int[] cell = cells.remove(cells.size() - 1);
        boolean ceiling = random.nextInt(CEILING_IN) == 0;
        int y = ceiling ? room.y1 : room.floorY;
        String id = mob.nbt == null ? mob.id : mob.id + mob.nbt;
        plan.spawner(new Spawner(cell[0], y, cell[1], ceiling ? PlanRecords.SPAWNER_CEILING : PlanRecords.SPAWNER_FLOOR, tier, id, random.nextLong()));
        top[0] = Math.max(top[0], tier);
        return true;
    }

    @Nullable private Mob pickMob(String spec, int tier, String roomKind, Random random) {
        List<SpawnGroup> groups = new ArrayList<>();
        for (SpawnGroup group : theme.spawnGroups) {
            if (!group.name.equals(spec) || depth < group.minDepth || depth > group.maxDepth) { continue; }
            if (!group.kinds.isEmpty() && !group.kinds.contains(roomKind) || group.tier != null && !group.tier.equals(TIERS.get(tier))) { continue; }
            if (usable(group.mobs).isEmpty()) { continue; }
            groups.add(group);
        }
        SpawnGroup chosen = groups.isEmpty() ? null : WeightedPicks.raw(groups, group -> group.weight, random, null);
        if (chosen != null) { return weighted(usable(chosen.mobs), random); }
        for (int t = tier; t >= 0; t--) {
            List<Mob> held = usable(theme.mobs.getOrDefault(TIERS.get(t), Collections.emptyList()));
            if (!held.isEmpty()) { return weighted(held, random); }
        }
        return null;
    }

    private List<Mob> usable(List<Mob> mobs) {
        List<Mob> found = new ArrayList<>();
        for (Mob mob : mobs) {
            if (ForgeRegistries.ENTITIES.containsKey(new ResourceLocation(mob.id))) { found.add(mob); }
            else { ContentLog.LOGGER.errorOnce(MISSING, mob.id, "Dungeon theme {} gives mob '{}', which nothing registers, so it is left out of the roll", theme.key, mob.id); }
        }
        return found;
    }

    private static Mob weighted(List<Mob> mobs, Random random) {
        return WeightedPicks.raw(mobs, mob -> mob.weight, random, mobs.get(0));
    }

    private void bossChest(DungeonDef.Boss boss, Room room, Random random, List<int[]> cells, int top, int spawners) {
        int[] cell = wallCell(room, cells, new ArrayList<>());
        if (cell == null) { return; }
        int at = chestLevel(room, top, spawners);
        LootEntry entry = pickLoot(boss.loot, at, random);
        if (entry != null) { plan.chest(new Chest(cell[0], room.floorY, cell[1], PlanRecords.CHEST_BOSS, at, entry.table.toString(), random.nextLong())); }
    }

    private void roomChests(Room room, RoomKindDef kind, Random random, List<int[]> cells, int top, int spawners) {
        Amount amount = kind.chests;
        int count;
        if (amount == null) { count = room.entrance ? 0 : room.node || spawners > 0 && random.nextBoolean() || spawners == 0 && random.nextInt(WEAK_IN) == 0 ? 1 : 0; }
        else if (amount.percent) { count = DungeonDef.Degree.chance(random, amount.min) ? 1 : 0; }
        else { count = MathUtil.randomIn(random, amount.min, amount.max); }
        int chestKind = room.node ? PlanRecords.CHEST_TREASURE : spawners > 0 ? PlanRecords.CHEST_BASIC : PlanRecords.CHEST_WEAK;
        int at = chestLevel(room, top, spawners);
        List<int[]> placed = new ArrayList<>();
        for (int n = 0; n < count; n++) {
            int[] cell = wallCell(room, cells, placed);
            LootEntry entry = pickLoot(kind.loot, at, random);
            if (cell == null || entry == null) { return; }
            plan.chest(new Chest(cell[0], room.floorY, cell[1], chestKind, at, entry.table.toString(), random.nextLong()));
        }
    }

    private int chestLevel(Room room, int top, int spawners) {
        int at = top + (spawners > 1 ? 1 : 0) + (room.node ? 1 : 0) + lootBonus + (int) Math.round(depth * lootLevels);
        return MathHelper.clamp(at, 0, lootLevels);
    }

    @Nullable private LootEntry pickLoot(String set, int at, Random random) {
        List<LootEntry> fits = new ArrayList<>();
        for (LootEntry entry : theme.loot.getOrDefault(set, Collections.emptyList())) {
            if (at >= entry.minLevel && at <= entry.maxLevel) { fits.add(entry); }
        }
        if (fits.isEmpty()) { return null; }
        return WeightedPicks.raw(fits, entry -> entry.weight, random, fits.get(0));
    }

    @Nullable private static int[] wallCell(Room room, List<int[]> cells, List<int[]> chests) {
        for (int i = cells.size() - 1; i >= 0; i--) {
            int[] cell = cells.get(i);
            if ((cell[0] == room.x0 || cell[0] == room.x1 || cell[1] == room.z0 || cell[1] == room.z1) && !beside(cell, chests)) {
                chests.add(cell);
                return cells.remove(i);
            }
        }
        return null;
    }

    private static boolean beside(int[] cell, List<int[]> chests) {
        for (int[] chest : chests) {
            if (Math.abs(chest[0] - cell[0]) + Math.abs(chest[1] - cell[1]) <= 1) { return true; }
        }
        return false;
    }

    private RoomKindDef kindOf(Room room) {
        for (RoomKindDef kind : kinds) {
            if (kind.kind.equals(room.kind)) { return kind; }
        }
        return RoomKindDef.hall();
    }

    private List<int[]> cells(Room room) {
        List<int[]> open = new ArrayList<>();
        Shaft shaft = level.shaft();
        List<Feature> features = level.featuresOf(room.index);
        for (int x = room.x0; x <= room.x1; x++) {
            for (int z = room.z0; z <= room.z1; z++) {
                if (DungeonShapes.isFloorCell(room, features, x, z) && !blocked(room, shaft, x, z)) { open.add(new int[] {x, z}); }
            }
        }
        return open;
    }

    private boolean blocked(Room room, @Nullable Shaft shaft, int x, int z) {
        if (near(x, z, level.anchorX(), level.anchorZ(), LANDING_REACH)) { return true; }
        if (shaft != null && shaft.covers(x, z, 1)) { return true; }
        for (Link link : level.links()) {
            int reach = Math.max(1, link.width / 2);
            if (link.roomA == room.index && near(x, z, link.doorAX, link.doorAZ, reach) || link.roomB == room.index && near(x, z, link.doorBX, link.doorBZ, reach)) { return true; }
        }
        return false;
    }

    private static boolean near(int x, int z, int doorX, int doorZ, int reach) { return Math.abs(x - doorX) <= reach && Math.abs(z - doorZ) <= reach; }
}
