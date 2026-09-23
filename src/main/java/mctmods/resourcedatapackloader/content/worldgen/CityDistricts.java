package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.content.def.CityMapDef;
import mctmods.resourcedatapackloader.content.def.VillageDef;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Parallel;

import net.minecraft.util.Mth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Set;
import javax.annotation.Nullable;

final class CityDistricts {
    private static final int SITE_MARGIN = 4;
    private static final int SITE_SAMPLES = 10;
    private static final int SITE_STEP = 8;
    private static final int SITE_TOLERANCE = 10;
    private static final String VILLAGES = "villages";
    static final Map<Long, Optional<int[]>> SITES = new ConcurrentHashMap<>();
    static final Map<Long, Optional<int[]>> PLACES = new ConcurrentHashMap<>();
    static final Set<Long> SHIFTED = ConcurrentHashMap.newKeySet();
    @Nullable static volatile Map<Long, int[]> pinnedAt;
    @Nullable static volatile int[] offset;

    private CityDistricts() {}

    public static int separation() { return villageNumber("structureSeparation"); }

    static int[] plazaCenter(int districtX, int districtZ, int[] cross) {
        int half = CityPlan.fullWidth() / 2;
        return new int[] {CityPlan.windowOf(districtX, true) + cross[0] + half, CityPlan.windowOf(districtZ, false) + cross[1] + half};
    }

    static int[] crossOf(int districtX, int districtZ) {
        int[] pin = pinnedAt().get(CityPlan.packed(districtX, districtZ));
        int standard = crossAt();
        if (pin == null) { return new int[] {standard, standard}; }
        int half = CityPlan.fullWidth() / 2;
        int room = CityPlan.district() - CityPlan.fullWidth();
        int wantedX = pin[0] + 1 - CityPlan.windowOf(districtX, true) - half;
        int wantedZ = pin[1] + 1 - CityPlan.windowOf(districtZ, false) - half;
        int[] cross = {Mth.clamp(wantedX, 0, room), Mth.clamp(wantedZ, 0, room)};
        if ((cross[0] != wantedX || cross[1] != wantedZ) && SHIFTED.add(CityPlan.packed(pin[0], pin[1]))) { ContentLog.LOGGER.info("The well of the village pinned at {}, {} stands {} block(s) off the pin in x and {} in z: the pin lies so near its district's edge that the plaza street would cross into the next district, which is planned apart from it", pin[0], pin[1], cross[0] - wantedX, cross[1] - wantedZ); }
        return cross;
    }

    static int offset(boolean alongX) {
        int[] held = offset;
        if (held == null) {
            held = gridOffset();
            offset = held;
        }
        return held[alongX ? 0 : 1];
    }

    private static int[] gridOffset() {
        List<int[]> pinned = pins(ContentCity.STRUCTURE);
        if (pinned.isEmpty()) { pinned = pins(VILLAGES); }
        if (pinned.isEmpty()) { return new int[] {0, 0}; }
        int lead = crossAt() + CityPlan.fullWidth() / 2 - 1;
        int[] pin = pinned.get(0);
        return new int[] {Math.floorMod(Math.floorDiv(pin[0] - lead, 16) * 16, CityPlan.district()), Math.floorMod(Math.floorDiv(pin[1] - lead, 16) * 16, CityPlan.district())};
    }

    static Map<Long, int[]> pinnedAt() {
        Map<Long, int[]> held = pinnedAt;
        if (held != null) { return held; }
        Map<Long, int[]> found = new LinkedHashMap<>();
        for (String named : List.of(ContentCity.STRUCTURE, VILLAGES)) {
            for (int[] pin : pins(named)) { found.putIfAbsent(CityPlan.packed(CityPlan.districtOf(pin[0], true), CityPlan.districtOf(pin[1], false)), pin); }
        }
        pinnedAt = Map.copyOf(found);
        return pinnedAt;
    }

    @Nullable static int[] center(CityGround ground, int districtX, int districtZ) {
        int spacing = CityPlan.spacing();
        int regionX = Math.floorDiv(districtX, spacing);
        int regionZ = Math.floorDiv(districtZ, spacing);
        int[] pinned = pinnedIn(ContentCity.STRUCTURE, regionX, regionZ, spacing);
        if (pinned != null) { return pinned; }
        if (villagesPinned()) { return pinnedIn(VILLAGES, regionX, regionZ, spacing); }
        return SITES.computeIfAbsent(CityPlan.packed(regionX, regionZ), key -> Optional.ofNullable(founded(ground, regionX, regionZ, spacing))).orElse(null);
    }

    private static boolean villagesPinned() {
        for (String entry : ContentControl.list(ContentControl.STRUCTURES, "structureAt", Config.worldgen.structureAt())) {
            String[] parts = entry.split("=", 2);
            if (parts.length == 2 && parts[0].trim().equalsIgnoreCase(VILLAGES)) { return true; }
        }
        return false;
    }

    @Nullable private static int[] pinnedIn(String named, int regionX, int regionZ, int spacing) {
        for (int[] pinned : pinnedDistricts(named)) {
            if (Math.floorDiv(pinned[0], spacing) == regionX && Math.floorDiv(pinned[1], spacing) == regionZ) { return pinned; }
        }
        return null;
    }

    public static List<int[]> pinnedWells() {
        List<int[]> wells = new ArrayList<>();
        List<int[]> pinned = pinnedDistricts(ContentCity.STRUCTURE);
        if (pinned.isEmpty()) { pinned = pinnedDistricts(VILLAGES); }
        for (int[] district : pinned) { wells.add(plazaCenter(district[0], district[1], crossOf(district[0], district[1]))); }
        return wells;
    }

    static int pinsIn(int regionX, int regionZ) {
        int spacing = Math.max(1, CityPlan.spacing());
        for (String named : List.of(ContentCity.STRUCTURE, VILLAGES)) {
            int held = 0;
            for (int[] pinned : pinnedDistricts(named)) {
                if (Math.floorDiv(pinned[0], spacing) == regionX && Math.floorDiv(pinned[1], spacing) == regionZ) { held++; }
            }
            if (held > 0) { return held; }
        }
        return 0;
    }

    private static List<int[]> pinnedDistricts(String named) {
        List<int[]> pinned = new ArrayList<>();
        for (int[] pin : pins(named)) { pinned.add(new int[] {CityPlan.districtOf(pin[0], true), CityPlan.districtOf(pin[1], false)}); }
        return pinned;
    }

    private static List<int[]> pins(String named) {
        List<int[]> pinned = new ArrayList<>();
        for (String entry : ContentControl.list(ContentControl.STRUCTURES, "structureAt", Config.worldgen.structureAt())) {
            String[] parts = entry.split("=", 2);
            if (parts.length != 2 || !parts[0].trim().equalsIgnoreCase(named)) { continue; }
            String[] xz = parts[1].split(",");
            if (xz.length != 2) { continue; }
            try { pinned.add(new int[] {Integer.parseInt(xz[0].trim()), Integer.parseInt(xz[1].trim())}); }
            catch (NumberFormatException notNumbers) { ContentLog.LOGGER.debug("structureAt entry '{}' is not {}=x,z, so it does not seat a city", entry, named); }
        }
        return pinned;
    }

    @Nullable private static int[] founded(CityGround ground, int regionX, int regionZ, int spacing) {
        int[] site = site(ground, regionX, regionZ, spacing);
        if (site == null) {
            ContentLog.LOGGER.debug("City region {}, {} has no district both flat within {} block(s) and in a village biome, so nothing is founded there", regionX, regionZ, SITE_TOLERANCE);
            return null;
        }
        int[] at = plazaCenter(site[0], site[1], crossOf(site[0], site[1]));
        int least = villageNumber("structureMinDistanceFromSpawn");
        if (least > 0) {
            int[] spawn = ContentStructureControl.spawnAt();
            long awayX = at[0] - spawn[0];
            long awayZ = at[1] - spawn[1];
            if (awayX * awayX + awayZ * awayZ < (long) least * least) {
                ContentLog.LOGGER.debug("City region {}, {} would found within {} blocks of the spawn, so nothing is founded there", regionX, regionZ, least);
                return null;
            }
        }
        if (ground.mansionNear(Math.floorDiv(at[0], 16), Math.floorDiv(at[1], 16))) {
            ContentLog.LOGGER.debug("City region {}, {} would found where a woodland mansion could start, so nothing is founded there", regionX, regionZ);
            return null;
        }
        int apart = villageNumber("structureSeparation");
        if (apart > 0) {
            for (int aroundX = -1; aroundX <= 1; aroundX++) {
                for (int aroundZ = -1; aroundZ <= 1; aroundZ++) {
                    boolean earlier = aroundX < 0 || (aroundX == 0 && aroundZ < 0);
                    if (!earlier) { continue; }
                    int[] other = site(ground, regionX + aroundX, regionZ + aroundZ, spacing);
                    if (other == null) { continue; }
                    int[] near = plazaCenter(other[0], other[1], crossOf(other[0], other[1]));
                    if (Math.abs(Math.floorDiv(near[0], 16) - Math.floorDiv(at[0], 16)) < apart && Math.abs(Math.floorDiv(near[1], 16) - Math.floorDiv(at[1], 16)) < apart) {
                        ContentLog.LOGGER.debug("City region {}, {} would found within {} chunks of the city in region {}, {}, so nothing is founded there", regionX, regionZ, apart, regionX + aroundX, regionZ + aroundZ);
                        return null;
                    }
                }
            }
        }
        return site;
    }

    @Nullable private static int[] site(CityGround ground, int regionX, int regionZ, int spacing) {
        long held = CityPlan.packed(regionX, regionZ);
        Optional<int[]> found = PLACES.get(held);
        if (found != null) { return found.orElse(null); }
        Optional<int[]> made = Optional.ofNullable(chooseSite(ground, regionX, regionZ, spacing));
        PLACES.putIfAbsent(held, made);
        return made.orElse(null);
    }

    static void siteAhead(CityGround ground, List<int[]> districts) {
        int spacing = CityPlan.spacing();
        if (spacing <= 1 || districts.isEmpty() || villagesPinned()) { return; }
        CityMapDef map = ContentCity.layout();
        int reachX = map == null ? 0 : (map.blocksWide() + CityPlan.district() - 1) / CityPlan.district() + 2;
        int reachZ = map == null ? 0 : (map.blocksDeep() + CityPlan.district() - 1) / CityPlan.district() + 2;
        int around = map == null ? 1 : 0;
        Set<Long> founding = new LinkedHashSet<>();
        for (int[] span : districts) {
            for (int regionX = Math.floorDiv(span[0] - reachX, spacing) - around; regionX <= Math.floorDiv(span[2] + reachX, spacing) + around; regionX++) {
                for (int regionZ = Math.floorDiv(span[1] - reachZ, spacing) - around; regionZ <= Math.floorDiv(span[3] + reachZ, spacing) + around; regionZ++) {
                    if (SITES.containsKey(CityPlan.packed(regionX, regionZ)) || pinnedIn(ContentCity.STRUCTURE, regionX, regionZ, spacing) != null) { continue; }
                    founding.add(CityPlan.packed(regionX, regionZ));
                }
            }
        }
        siteAll(ground, founding, spacing);
        if (villageNumber("structureSeparation") <= 0) { return; }
        Set<Long> earlier = new LinkedHashSet<>();
        for (long region : founding) {
            int regionX = (int) (region >> 32);
            int regionZ = (int) region;
            if (site(ground, regionX, regionZ, spacing) == null) { continue; }
            for (int aroundZ = -1; aroundZ <= 1; aroundZ++) { earlier.add(CityPlan.packed(regionX - 1, regionZ + aroundZ)); }
            earlier.add(CityPlan.packed(regionX, regionZ - 1));
        }
        siteAll(ground, earlier, spacing);
    }

    private static void siteAll(CityGround ground, Set<Long> regions, int spacing) {
        List<int[]> open = new ArrayList<>();
        for (long region : regions) {
            int regionX = (int) (region >> 32);
            int regionZ = (int) region;
            if (!PLACES.containsKey(CityPlan.packed(regionX, regionZ))) { open.add(new int[] {regionX, regionZ}); }
        }
        int count = open.size();
        if (count == 0) { return; }
        SpawnProgress.queued(count);
        Parallel.each(count, at -> {
            try { site(ground, open.get(at)[0], open.get(at)[1], spacing); }
            finally { SpawnProgress.sited(); }
        });
    }

    @Nullable private static int[] chooseSite(CityGround ground, int regionX, int regionZ, int spacing) {
        int margin = (SITE_MARGIN * 16 + CityPlan.district() - 1) / CityPlan.district();
        if (spacing - 2 * margin <= 0) { margin = 0; }
        List<int[]> candidates = new ArrayList<>();
        for (int dx = margin; dx < spacing - margin; dx++) {
            for (int dz = margin; dz < spacing - margin; dz++) { candidates.add(new int[] {dx, dz, Math.abs(dx * 2 + 1 - spacing) + Math.abs(dz * 2 + 1 - spacing)}); }
        }
        candidates.sort(Comparator.comparingInt(candidate -> candidate[2]));
        int[] chosen = null;
        int bestSpread = SITE_TOLERANCE + 1;
        for (int[] candidate : candidates) {
            int districtX = regionX * spacing + candidate[0];
            int districtZ = regionZ * spacing + candidate[1];
            int[] at = plazaCenter(districtX, districtZ, crossOf(districtX, districtZ));
            if (ground.barren(at[0], at[1])) { continue; }
            int spread = spread(ground, at[0], at[1], bestSpread - 1);
            if (spread == Integer.MAX_VALUE && bestSpread > SITE_TOLERANCE) { ContentLog.LOGGER.debug("The district at {}, {} is passed over as a city site: the ground and sea bed under its sample square around {}, {} spread more than {} block(s)", districtX, districtZ, at[0], at[1], SITE_TOLERANCE); }
            if (spread >= bestSpread) { continue; }
            bestSpread = spread;
            chosen = new int[] {districtX, districtZ};
            if (bestSpread == 0) { break; }
        }
        return chosen;
    }

    private static int spread(CityGround ground, int x, int z, int limit) {
        int lowest = Integer.MAX_VALUE;
        int highest = Integer.MIN_VALUE;
        int half = (SITE_SAMPLES - 1) * SITE_STEP / 2;
        for (int stepX = 0; stepX < SITE_SAMPLES; stepX++) {
            for (int stepZ = 0; stepZ < SITE_SAMPLES; stepZ++) {
                int found = ground.floor(x - half + stepX * SITE_STEP, z - half + stepZ * SITE_STEP);
                lowest = Math.min(lowest, found);
                highest = Math.max(highest, found);
                if (highest - lowest > limit) { return Integer.MAX_VALUE; }
            }
        }
        return highest - lowest;
    }

    static int villageNumber(String key) {
        for (String entry : ContentControl.list(ContentControl.STRUCTURES, key, key.equals("structureSpacing") ? Config.worldgen.structureSpacing() : key.equals("structureSeparation") ? Config.worldgen.structureSeparation() : Config.worldgen.structureMinDistanceFromSpawn())) {
            int at = entry.indexOf('=');
            if (at <= 0 || !entry.substring(0, at).trim().equalsIgnoreCase(VILLAGES)) { continue; }
            try { return Integer.parseInt(entry.substring(at + 1).trim()); }
            catch (NumberFormatException notNumber) { return 0; }
        }
        return 0;
    }

    static int crossAt() {
        int room = CityPlan.district() - CityPlan.fullWidth();
        int shallow = 0;
        for (VillageDef def : ContentVillages.allowed()) { shallow = Math.max(shallow, def.depth() + CityPlanPlots.SETBACK); }
        return shallow > 0 && shallow <= room / 2 ? shallow : room / 2;
    }
}
