package mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.plan;

import mctmods.resourcedatapackloader.common.pack.definitions.games.PickDef;
import mctmods.resourcedatapackloader.common.pack.definitions.worlds.DungeonDef;
import mctmods.resourcedatapackloader.common.pack.definitions.worlds.DungeonDef.Degree;
import mctmods.resourcedatapackloader.common.pack.definitions.worlds.DungeonDef.LevelRule;
import mctmods.resourcedatapackloader.common.pack.definitions.worlds.DungeonSizeDef;
import mctmods.resourcedatapackloader.common.pack.definitions.worlds.DungeonThemeDef;
import mctmods.resourcedatapackloader.common.pack.definitions.worlds.RoomKindDef;
import mctmods.resourcedatapackloader.common.util.BoundedLru;
import mctmods.resourcedatapackloader.common.util.worldgen.SeedStreams;
import mctmods.resourcedatapackloader.common.util.worldgen.WeightedPicks;
import mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.ContentDungeonThemes;
import mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.DungeonSites;
import mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.DungeonWorld;
import mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.plan.DungeonPlan.Level;
import mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.write.DungeonEntrance;
import mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.plan.DungeonSlots.Tally;
import mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.plan.DungeonSlots.TemplateSizes;
import mctmods.resourcedatapackloader.core.util.ContentLog;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;

public final class DungeonPlanner {
    private static final int CACHED = 8;
    private static final int RULE_STREAM = 1;
    private static final int VERTICAL = DungeonDef.ROLLS.indexOf("vertical");
    private static final int ENTRANCES = DungeonDef.ROLLS.indexOf("entrances");
    private static final BoundedLru<String, DungeonPlan> CACHE = new BoundedLru<>(CACHED);

    private DungeonPlanner() {}

    public static synchronized DungeonPlan get(DungeonWorld world, DungeonDef def, DungeonSites.Site site) {
        String key = def.key() + "@" + site.originX + "," + site.originZ + "#" + siteSeedOf(world, def, site);
        DungeonPlan held = CACHE.get(key);
        if (held != null) { return held; }
        DungeonPlan made = plan(world, def, site);
        CACHE.put(key, made);
        return made;
    }

    public static DungeonPlan plan(DungeonWorld world, DungeonDef def, DungeonSites.Site site) {
        long siteSeed = siteSeedOf(world, def, site);
        DungeonPlan.Site where = new DungeonPlan.Site(site.originX, site.originZ, siteSeed, site.size.name(), site.surfaceY);
        String themeId = ResourceLocation.parse(PickDef.pick(site.themes, SeedStreams.random(siteSeed, "theme", 0), site.themes.getFirst().name())).toString();
        DungeonThemeDef theme = ContentDungeonThemes.byName(themeId);
        int[] rolls = new int[DungeonDef.ROLLS.size()];
        if (theme != null && theme.rolls() != null) { applyRolls(rolls, theme.rolls(), siteSeed, 0); }
        applyRolls(rolls, def.rolls(), siteSeed, 0);
        DungeonPlan.Builder plan = DungeonPlan.builder(where, themeId);
        for (int i = 0; i < rolls.length; i++) {
            plan.roll(i, rolls[i]);
        }
        List<int[]> slabs = slabs(def, site.size, siteSeed, site.surfaceY - def.surfaceBuffer(), lowestFloor(world, def, theme));
        int levels = slabs.size();
        int[] field = {site.originX - site.radius, site.originZ - site.radius, site.originX + site.radius, site.originZ + site.radius};
        Tally tally = new Tally();
        TemplateSizes sizes = new TemplateSizes(world);
        int anchorX = site.originX;
        int anchorZ = site.originZ;
        for (int i = 0; i < levels; i++) {
            Step step = stepAt(def, themeId, rolls, siteSeed, DungeonDef.depthOf(i, levels));
            int[] slab = slabs.get(i);
            Level.Builder level = DungeonSlots.layout(plan, def, step.kinds, site.size, i, levels, slab[0], slab[1], step.themeId, step.vertical, anchorX, anchorZ, site.originX, site.originZ, field, SeedStreams.seed(siteSeed, "level", i), tally, sizes);
            level.rolls(step.rolls);
            DungeonLinks.Result links = DungeonLinks.link(level, def, SeedStreams.seed(siteSeed, "links", i), i == levels - 1, i < levels - 1 ? slabs.get(i + 1)[0] : 0);
            if (links.bossRoom >= 0) { plan.bossRoom(links.bossRoom); }
            DungeonFeatures.features(level, step.kinds, SeedStreams.seed(siteSeed, "features", i), i == 0, site.surfaceY);
            DungeonPopulation.populate(plan, level, def, step.kinds, DungeonDef.depthOf(i, levels), SeedStreams.seed(siteSeed, "population", i));
            if (links.anchor != null) {
                anchorX = links.anchor[0];
                anchorZ = links.anchor[1];
            }
        }
        if (levels > 0) { plan.entrance(DungeonEntrance.plan(def, siteSeed, entranceDegree(def, rolls, siteSeed), site.originX, site.originZ, roomsOf(plan.level(0)), sizes)); }
        DungeonPlan built = plan.build();
        if (ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("dungeon plan key={} site={},{} levels={} rooms={} theme={}", def.key(), site.originX, site.originZ, built.levels.size(), built.roomCount(), themeId); }
        return built;
    }

    private static long siteSeedOf(DungeonWorld world, DungeonDef def, DungeonSites.Site site) { return SeedStreams.keyed(world.seed(), def.key(), site.originX, 3, site.originZ); }

    private static int entranceDegree(DungeonDef def, int[] rolls, long siteSeed) {
        Degree extra = def.entrance().extra();
        return extra != null && extra.valid() ? WeightedPicks.index(extra.weights(), SeedStreams.random(siteSeed, "entranceExtra", 1)) : rolls[ENTRANCES];
    }

    private static List<DungeonPlan.Room> roomsOf(Level.Builder level) {
        List<DungeonPlan.Room> rooms = new ArrayList<>();
        for (int i = 0; i < level.roomCount(); i++) {
            rooms.add(level.room(i));
        }
        return rooms;
    }

    private static int lowestFloor(DungeonWorld world, DungeonDef def, DungeonThemeDef theme) {
        int lowest = DungeonSites.lowest(world, def);
        return theme == null || theme.minY() == null ? lowest : Math.max(lowest, theme.minY());
    }

    private static List<int[]> slabs(DungeonDef def, DungeonSizeDef size, long siteSeed, int top, int bottom) {
        int fewest = size.levels()[0];
        int wanted = fewest + SeedStreams.random(siteSeed, "levels", 0).nextInt(Math.max(fewest, size.levels()[1]) - fewest + 1);
        int lowHeight = def.levelHeight()[0];
        int heightSpan = Math.max(lowHeight, def.levelHeight()[1]) - lowHeight + 1;
        List<int[]> slabs = new ArrayList<>();
        int ceiling = top;
        for (int i = 0; i < wanted; i++) {
            int height = lowHeight + SeedStreams.random(siteSeed, "slab", i).nextInt(heightSpan);
            int floor = ceiling - height + 1;
            if (floor < bottom) { break; }
            slabs.add(new int[] {floor, height});
            ceiling = floor - 1 - PlanRecords.SLAB_GAP;
        }
        return slabs;
    }

    private static void applyRolls(int[] into, Map<String, Degree> source, long siteSeed, int stream) {
        Degree.apply(into, source, siteSeed, "rolls", stream);
    }

    private static Step stepAt(DungeonDef def, String themeId, int[] rolls, long siteSeed, double depth) {
        Step step = new Step(themeId, def.kindsAt(depth), rolls.clone());
        for (int r = 0; r < def.levelRules().size(); r++) {
            LevelRule rule = def.levelRules().get(r);
            if (rule.fromDepth() > depth) { continue; }
            if (rule.theme() != null) { step.themeId = rule.theme().toString(); }
            if (rule.rolls() != null) { applyRolls(step.rolls, rule.rolls(), siteSeed, RULE_STREAM * DungeonDef.ROLLS.size() * (r + 1)); }
        }
        step.vertical = step.rolls[VERTICAL];
        return step;
    }

    private static final class Step {
        private String themeId;
        private final List<RoomKindDef> kinds;
        private final int[] rolls;
        private int vertical;

        private Step(String themeId, List<RoomKindDef> kinds, int[] rolls) {
            this.themeId = themeId;
            this.kinds = kinds;
            this.rolls = rolls;
        }
    }

}
