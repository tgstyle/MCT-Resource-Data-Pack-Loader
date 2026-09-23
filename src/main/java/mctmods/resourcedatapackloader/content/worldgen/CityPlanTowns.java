package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.def.CityMapDef;
import mctmods.resourcedatapackloader.content.def.VillageDef;
import mctmods.resourcedatapackloader.content.worldgen.CityGrowth.City;
import mctmods.resourcedatapackloader.content.worldgen.CityPlan.End;
import mctmods.resourcedatapackloader.content.worldgen.CityPlan.Junction;
import mctmods.resourcedatapackloader.content.worldgen.CityPlan.Line;
import mctmods.resourcedatapackloader.content.worldgen.CityPlan.Plot;
import mctmods.resourcedatapackloader.content.worldgen.CityPlan.Rail;
import mctmods.resourcedatapackloader.content.worldgen.CityPlan.Town;
import mctmods.resourcedatapackloader.content.worldgen.CityPlanPlots.Tally;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.util.RandomSource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import javax.annotation.Nullable;

final class CityPlanTowns {
    static final Map<List<Long>, Optional<CityPlan>> PLANS = new ConcurrentHashMap<>();
    private static final int DEFAULT_BLOCK = 32;
    private static final int NEAR_DISTRICTS = 2;
    private static final int SHORT_LEAST = 7;
    static final Map<Long, Optional<CityPlan>> RAW = new ConcurrentHashMap<>();

    private CityPlanTowns() {}

    @Nullable static CityPlan railed(CityGround ground, int districtX, int districtZ) {
        CityPlan raw = raw(ground, districtX, districtZ);
        if (raw != null && raw.town() != null) {
            CityDistricts.siteAhead(ground, CityRails.reaches(ground, raw.town()));
            Function<Rail, CityRails.Bed> beds = rail -> CityRails.bed(ground, rail);
            return shortOfLinks(raw.withRails(withLinks(ground, raw.town(), CityRails.fitted(ground, raw.town()), districtX, districtZ)).clearOfRails(ground, beds), beds);
        }
        List<Town> towns = townsNear(ground, districtX, districtZ);
        List<int[]> reaches = new ArrayList<>();
        for (Town near : towns) { reaches.addAll(CityRails.reaches(ground, near)); }
        CityDistricts.siteAhead(ground, reaches);
        List<Rail> passing = new ArrayList<>();
        for (Town near : towns) {
            for (Rail rail : railsThrough(CityRails.fitted(ground, near), districtX, districtZ)) {
                if (!passing.contains(rail)) { passing.add(rail); }
            }
        }
        List<Line> tied = CitySeams.through(ground, towns, districtX, districtZ);
        if (passing.isEmpty() && tied.isEmpty()) { return raw; }
        int size = CityPlan.district();
        if (!passing.isEmpty()) { ContentLog.LOGGER.debug("The district at {}, {} is no city's, but {} railway line(s) of the cities around it run through it", CityPlan.windowOf(districtX, true), CityPlan.windowOf(districtZ, false), passing.size()); }
        List<Line> rows = new ArrayList<>();
        List<Line> columns = new ArrayList<>();
        for (Line tie : tied) { (tie.alongX() ? rows : columns).add(tie); }
        if (!tied.isEmpty()) { ContentLog.LOGGER.debug("The district at {}, {} is no city's, but {} tie street(s) between the cities around it run through it", CityPlan.windowOf(districtX, true), CityPlan.windowOf(districtZ, false), tied.size()); }
        String type = tied.isEmpty() ? CityGround.VILLAGE_TYPES.get(0) : ground.villageStyle(CityPlan.windowOf(districtX, true) + size / 2, CityPlan.windowOf(districtZ, false) + size / 2);
        CityPlan made = new CityPlan(CityPlan.windowOf(districtX, true), CityPlan.windowOf(districtZ, false), DEFAULT_BLOCK, List.copyOf(rows), List.copyOf(columns), List.of(), List.copyOf(passing), CityPlan.windowOf(districtX, true), CityPlan.windowOf(districtZ, false), false, List.of(), List.of(), type, null);
        return tied.isEmpty() ? made : made.clearOfRails(ground, rail -> CityRails.bed(ground, rail));
    }

    private static List<Rail> withLinks(CityGround ground, Town town, List<Rail> own, int districtX, int districtZ) {
        List<Rail> held = new ArrayList<>(railsThrough(own, districtX, districtZ));
        if (!CityLinks.on()) { return List.copyOf(held); }
        for (Town near : townsNear(ground, districtX, districtZ)) {
            if (near.key() == town.key()) { continue; }
            for (Rail rail : railsThrough(CityLinks.linkRails(CityRails.staged(ground, near)), districtX, districtZ)) {
                if (held.contains(rail)) { continue; }
                held.add(rail);
                ContentLog.LOGGER.debug("The district at {}, {} of the city at {}, {} keeps clear of the railway link of the city at {}, {} that runs through it", CityPlan.windowOf(districtX, true), CityPlan.windowOf(districtZ, false), town.wellX(), town.wellZ(), near.wellX(), near.wellZ());
            }
        }
        return List.copyOf(held);
    }

    private static CityPlan shortOfLinks(CityPlan plan, Function<Rail, CityRails.Bed> beds) {
        Town town = plan.town();
        if (town == null || plan.mapped()) { return plan; }
        Map<Line, Line> cut = new HashMap<>();
        List<Line> dropped = new ArrayList<>();
        for (List<Line> lines : List.of(plan.alongX, plan.alongZ)) {
            for (Line line : lines) {
                Line kept = line;
                for (Rail rail : plan.rails()) {
                    if (kept == null || rail.subway() || rail.town() == town.key() || rail.alongX() == line.alongX()) { continue; }
                    if (kept.to() < rail.at() || kept.from() > rail.last() || kept.last() < rail.from() || kept.at() > rail.to()) { continue; }
                    CityRails.Bed track = beds.apply(rail);
                    if (track != null && track.boredAt(kept.middle())) { continue; }
                    kept = shortOf(plan, town, kept, rail);
                }
                if (kept == null) { dropped.add(line); }
                else if (!kept.equals(line)) { cut.put(line, kept); }
                if (kept != line) { ContentLog.LOGGER.debug("The {} along {} at {} of the district at {}, {} runs into the railway link of another city, so it {}", line.alley() ? "alley" : "street", line.alongX() ? "x" : "z", line.at(), plan.originX(), plan.originZ(), kept == null ? "is not laid" : "stops short of it at rows " + kept.from() + " to " + kept.to()); }
            }
        }
        if (cut.isEmpty() && dropped.isEmpty()) { return plan; }
        List<Plot> kept = new ArrayList<>();
        for (Plot plot : plan.plots()) {
            Line street = cut.get(plot.street());
            if (dropped.contains(plot.street())) { continue; }
            if (street == null || (street.alongX() ? plot.fromX() >= street.from() && plot.toX() <= street.to() : plot.fromZ() >= street.from() && plot.toZ() <= street.to())) { kept.add(plot); }
        }
        return plan.keeping(kept).dropping(dropped).pulledBack(cut);
    }

    @Nullable private static Line shortOf(CityPlan plan, Town town, Line line, Rail rail) {
        Line low = rail.at() > line.from() ? line.reaching(line.from(), rail.at() - 1).ending(line.endsLow(), End.BARE) : null;
        Line high = rail.last() < line.to() ? line.reaching(rail.last() + 1, line.to()).ending(End.BARE, line.endsHigh()) : null;
        int lowMet = low == null ? -1 : junctions(plan, line, low);
        int highMet = high == null ? -1 : junctions(plan, line, high);
        int well = line.alongX() ? town.wellX() : town.wellZ();
        boolean keepLow = lowMet != highMet ? lowMet > highMet : high == null || low != null && Math.abs(well - (low.from() + low.to()) / 2) <= Math.abs(well - (high.from() + high.to()) / 2);
        Line kept = keepLow ? low : high;
        if (kept == null) { return null; }
        boolean plaza = plan.plazas().stream().anyMatch(held -> held.alongX().equals(line) || held.alongZ().equals(line));
        return kept.to() - kept.from() + 1 < SHORT_LEAST && !plaza ? null : kept;
    }

    private static int junctions(CityPlan plan, Line line, Line side) {
        int met = 0;
        for (Line crossing : plan.crossing(line)) {
            if (crossing.covers(line.middle()) && side.covers(crossing.middle())) { met++; }
        }
        return met;
    }

    static boolean laysStreet(CityGround ground, Town town, List<Rail> mine, Function<Rail, CityRails.Bed> beds, int districtX, int districtZ, boolean alongX, int center) {
        CityPlan raw = raw(ground, districtX, districtZ);
        if (raw == null || raw.town() == null || raw.town().key() != town.key()) { return false; }
        CityPlan plan = shortOfLinks(raw.withRails(withLinks(ground, town, mine, districtX, districtZ)).clearOfRails(ground, beds), beds);
        for (Line line : alongX ? plan.alongX : plan.alongZ) {
            if (!line.alley() && line.at() <= center && center <= line.last()) { return true; }
        }
        return false;
    }

    private static List<Rail> railsThrough(List<Rail> rails, int districtX, int districtZ) {
        int size = CityPlan.district();
        List<Rail> found = new ArrayList<>();
        for (Rail rail : rails) {
            int along = CityPlan.windowOf(rail.alongX() ? districtX : districtZ, rail.alongX());
            int across = CityPlan.windowOf(rail.alongX() ? districtZ : districtX, !rail.alongX());
            int half = CityRails.halfOf(rail);
            if (rail.to() < along || rail.from() > along + size - 1 || rail.middle() + half < across || rail.middle() - half > across + size - 1) { continue; }
            found.add(rail);
        }
        return List.copyOf(found);
    }

    private static List<Town> townsNear(CityGround ground, int districtX, int districtZ) {
        List<Town> found = new ArrayList<>();
        CityMapDef map = ContentCity.layout();
        int spacing = CityPlan.spacing();
        if (spacing <= 0) { return found; }
        if (map != null) {
            int reachX = (map.blocksWide() + CityPlan.district() - 1) / CityPlan.district() + 2 + CityLayout.pinSlack(map);
            int reachZ = (map.blocksDeep() + CityPlan.district() - 1) / CityPlan.district() + 2 + CityLayout.pinSlack(map);
            for (int offZ = -reachZ; offZ <= reachZ; offZ++) {
                for (int offX = -reachX; offX <= reachX; offX++) {
                    int cityX = districtX + offX;
                    int cityZ = districtZ + offZ;
                    if (spacing > 1 && CityLayout.elsewhere(ground, cityX, cityZ)) { continue; }
                    Town town = CityLayout.mapTown(ground, map, cityX, cityZ, spacing > 1);
                    if (town != null && !found.contains(town) && !CityGrowth.tooSmall(ground, map, cityX, cityZ, spacing > 1, town)) { found.add(town); }
                }
            }
            return found;
        }
        if (spacing == 1) {
            for (int offX = -NEAR_DISTRICTS; offX <= NEAR_DISTRICTS; offX++) {
                for (int offZ = -NEAR_DISTRICTS; offZ <= NEAR_DISTRICTS; offZ++) {
                    CityPlan near = raw(ground, districtX + offX, districtZ + offZ);
                    if (near != null && near.town() != null) { found.add(near.town()); }
                }
            }
            return found;
        }
        int regionX = Math.floorDiv(districtX, spacing);
        int regionZ = Math.floorDiv(districtZ, spacing);
        for (int aroundX = -1; aroundX <= 1; aroundX++) {
            for (int aroundZ = -1; aroundZ <= 1; aroundZ++) {
                int[] center = CityDistricts.center(ground, (regionX + aroundX) * spacing, (regionZ + aroundZ) * spacing);
                if (center == null) { continue; }
                City city = CityGrowth.CITIES.computeIfAbsent(CityPlan.packed(center[0], center[1]), key -> CityGrowth.grow(ground, center[0], center[1], spacing));
                if (!city.districts().isEmpty()) { found.add(town(ground, city)); }
            }
        }
        return found;
    }

    @Nullable public static Town townIn(CityGround ground, int cellX, int cellZ) {
        int spacing = CityPlan.spacing();
        if (spacing <= 0) { return null; }
        if (spacing == 1) {
            CityPlan plan = raw(ground, cellX, cellZ);
            return plan == null ? null : plan.town();
        }
        CityDistricts.siteAhead(ground, List.of(new int[] {cellX * spacing, cellZ * spacing, (cellX + 1) * spacing - 1, (cellZ + 1) * spacing - 1}));
        int[] center = CityDistricts.center(ground, cellX * spacing, cellZ * spacing);
        if (center == null) { return null; }
        CityMapDef map = ContentCity.layout();
        if (map != null) {
            Town town = CityLayout.mapTown(ground, map, center[0], center[1], true);
            return town == null || CityGrowth.tooSmall(ground, map, center[0], center[1], true, town) ? null : town;
        }
        City city = CityGrowth.CITIES.computeIfAbsent(CityPlan.packed(center[0], center[1]), key -> CityGrowth.grow(ground, center[0], center[1], spacing));
        return city.districts().isEmpty() ? null : town(ground, city);
    }

    @Nullable static CityPlan raw(CityGround ground, int districtX, int districtZ) {
        long held = CityPlan.packed(districtX, districtZ);
        Optional<CityPlan> found = RAW.get(held);
        if (found != null) { return found.orElse(null); }
        CityPlan made = unrailed(ground, districtX, districtZ);
        RAW.putIfAbsent(held, Optional.ofNullable(made));
        return made;
    }

    @Nullable private static CityPlan unrailed(CityGround ground, int districtX, int districtZ) {
        CityMapDef map = ContentCity.layout();
        if (map != null) { return CityLayout.mapped(ground, districtX, districtZ, map); }
        int spacing = CityPlan.spacing();
        if (spacing <= 0) { return null; }
        if (spacing == 1) {
            CityPlan plan = plan(ground, districtX, districtZ, null);
            return plan == null || CityGrowth.pieces(plan.plots().size(), 1) <= CityGrowth.FEWEST_PIECES ? null : plan.withTown(districtTown(ground, districtX, districtZ));
        }
        City city = cityOf(ground, districtX, districtZ, spacing);
        if (city == null) { return null; }
        CityPlan plan = plan(ground, districtX, districtZ, new int[] {city.centerX(), city.centerZ()});
        return plan == null ? null : plan.withTown(town(ground, city));
    }

    private static Town districtTown(CityGround ground, int districtX, int districtZ) {
        int size = CityPlan.district();
        int[] cross = CityDistricts.crossOf(districtX, districtZ);
        int[] well = CityDistricts.plazaCenter(districtX, districtZ, cross);
        return new Town(CityPlan.packed(districtX, districtZ), CityPlan.key(ground.seed(), districtX, districtZ), 0L, well[0], well[1], CityPlan.windowOf(districtX, true), CityPlan.windowOf(districtX, true) + size - 1, CityPlan.windowOf(districtZ, false), CityPlan.windowOf(districtZ, false) + size - 1, List.of(), cross[0], cross[1]);
    }

    static Town town(CityGround ground, City city) {
        int[] cross = CityDistricts.crossOf(city.centerX(), city.centerZ());
        int[] well = CityDistricts.plazaCenter(city.centerX(), city.centerZ(), cross);
        int lowX = Integer.MAX_VALUE;
        int highX = Integer.MIN_VALUE;
        int lowZ = Integer.MAX_VALUE;
        int highZ = Integer.MIN_VALUE;
        for (long packed : city.districts()) {
            int x = (int) (packed >> 32);
            int z = (int) packed;
            lowX = Math.min(lowX, x);
            highX = Math.max(highX, x);
            lowZ = Math.min(lowZ, z);
            highZ = Math.max(highZ, z);
        }
        return new Town(CityPlan.packed(city.centerX(), city.centerZ()), CityPlan.key(ground.seed(), city.centerX(), city.centerZ()), citySalt(city.centerX(), city.centerZ()), well[0], well[1], CityPlan.windowOf(lowX, true), CityPlan.windowOf(highX + 1, true) - 1, CityPlan.windowOf(lowZ, false), CityPlan.windowOf(highZ + 1, false) - 1, List.of(), cross[0], cross[1]);
    }

    static long citySalt(int centerX, int centerZ) { return centerX * 341873128712L + centerZ * 132897987541L; }

    @Nullable private static City cityOf(CityGround ground, int districtX, int districtZ, int spacing) {
        int regionX = Math.floorDiv(districtX, spacing);
        int regionZ = Math.floorDiv(districtZ, spacing);
        for (int aroundX = -1; aroundX <= 1; aroundX++) {
            for (int aroundZ = -1; aroundZ <= 1; aroundZ++) {
                int[] center = CityDistricts.center(ground, (regionX + aroundX) * spacing, (regionZ + aroundZ) * spacing);
                if (center == null) { continue; }
                City city = CityGrowth.CITIES.computeIfAbsent(CityPlan.packed(center[0], center[1]), key -> CityGrowth.grow(ground, center[0], center[1], spacing));
                if (city.has(districtX, districtZ)) { return city; }
            }
        }
        return null;
    }

    @Nullable public static int[] cityCenter(CityGround ground, int districtX, int districtZ) {
        int spacing = CityPlan.spacing();
        if (ContentCity.layout() != null) {
            CityPlan plan = raw(ground, districtX, districtZ);
            return plan == null || plan.town() == null ? new int[] {districtX, districtZ} : new int[] {plan.town().wellX(), plan.town().wellZ()};
        }
        if (spacing <= 1) { return new int[] {districtX, districtZ}; }
        City city = cityOf(ground, districtX, districtZ, spacing);
        return city == null ? null : new int[] {city.centerX(), city.centerZ()};
    }

    @Nullable static CityPlan plan(CityGround ground, int districtX, int districtZ, @Nullable int[] city) {
        List<Long> held = city == null ? List.of(CityPlan.packed(districtX, districtZ)) : List.of(CityPlan.packed(districtX, districtZ), CityPlan.packed(city[0], city[1]));
        return PLANS.computeIfAbsent(held, key -> build(ground, districtX, districtZ, city)).orElse(null);
    }

    private static Optional<CityPlan> build(CityGround ground, int districtX, int districtZ, @Nullable int[] city) { return Optional.of(laid(ground, districtX, districtZ, city == null ? districtTown(ground, districtX, districtZ) : protoTown(ground, city[0], city[1]))); }

    static CityPlan laid(CityGround ground, int districtX, int districtZ, Town proto) {
        long seed = ground.seed();
        RandomSource roll = RandomSource.create(seed ^ CityPlan.SALT ^ (districtX * 341873128712L + districtZ * 132897987541L));
        int size = CityPlan.district();
        int full = CityPlan.fullWidth();
        int half = full / 2;
        int originX = CityPlan.windowOf(districtX, true);
        int originZ = CityPlan.windowOf(districtZ, false);
        int centerX = originX + proto.crossX() + half;
        int centerZ = originZ + proto.crossZ() + half;
        Line row = new Line(centerZ - half, full, false, true, originX, originX + size - 1, End.MET, End.MET);
        Line column = new Line(centerX - half, full, false, false, originZ, originZ + size - 1, End.MET, End.MET);
        List<Line> alongX = new ArrayList<>(List.of(row));
        List<Line> alongZ = new ArrayList<>(List.of(column));
        List<Rail> placed = CityRails.placed(seed, proto);
        List<Rail> rails = across(placed, originX, originZ);
        for (boolean acrossZ : new boolean[] {true, false}) {
            int at = (acrossZ ? originZ : originX) + CityAlleys.branchAt(proto.crossAcross(acrossZ));
            int from = acrossZ ? originX : originZ;
            if (CityAlleys.branches(proto, placed, acrossZ ? districtZ : districtX, acrossZ)) { (acrossZ ? alongX : alongZ).add(new Line(at, full, false, acrossZ, from, from + size - 1, End.MET, End.MET)); }
            else { ContentLog.LOGGER.debug("The district at {}, {} lays no side street {} at {} {}: it did not roll, as a village road branches two times in three, or it would run on a railway line of its city", originX, originZ, acrossZ ? "east to west" : "north to south", acrossZ ? "z" : "x", at); }
        }
        int block = blockAt(seed, districtX, districtZ);
        CityAlleys.alleys(roll, originX, originZ, size, row, column, alongX, alongZ, rails, block);
        String type = ground.villageStyle(centerX, centerZ);
        List<VillageDef> choices = CityPlanPlots.choices(block, type);
        Tally tally = new Tally(ContentVillages.frontLimit(RandomSource.create(seed ^ CityPlan.SALT ^ 0x11A17L ^ (districtX * 341873128712L + districtZ * 132897987541L))));
        Junction plaza = new Junction(row, column);
        List<Plot> plots = CityPlanPlots.radialPlots(roll, choices, tally, originX, originZ, size, row, column, alongX, alongZ, rails, List.of(plaza));
        CityAlleys.idleAlleys(plots, alongX, alongZ);
        int alleyCount = 0;
        for (List<Line> lines : List.of(alongX, alongZ)) {
            for (Line line : lines) { alleyCount += line.alley() ? 1 : 0; }
        }
        int sideStreets = alongX.size() + alongZ.size() - 2 - alleyCount;
        ContentLog.LOGGER.debug("The district at {}, {} has its plaza at {}, {}, {} side street(s), {} alley(s) and {} line(s), its blocks {} deep and its plots built as {} villages", originX, originZ, centerX, centerZ, sideStreets, alleyCount, rails.size(), block, type);
        return new CityPlan(originX, originZ, DEFAULT_BLOCK, List.copyOf(alongX), List.copyOf(alongZ), plots, List.copyOf(rails), originX, originZ, false, List.of(plaza), List.of(), type, null);
    }

    static int blockAt(long seed, int districtX, int districtZ) { return CityPlanPlots.districtBlock(RandomSource.create(seed ^ CityPlan.SALT ^ 0xB10C5L ^ (districtX * 341873128712L + districtZ * 132897987541L))); }

    static Town protoTown(CityGround ground, int centerX, int centerZ) {
        int[] cross = CityDistricts.crossOf(centerX, centerZ);
        int[] well = CityDistricts.plazaCenter(centerX, centerZ, cross);
        return new Town(CityPlan.packed(centerX, centerZ), CityPlan.key(ground.seed(), centerX, centerZ), citySalt(centerX, centerZ), well[0], well[1], 0, 0, 0, 0, List.of(), cross[0], cross[1]);
    }

    static List<Rail> across(List<Rail> placed, int windowX, int windowZ) {
        int size = CityPlan.district();
        List<Rail> found = new ArrayList<>();
        for (Rail rail : placed) {
            int base = rail.alongX() ? windowZ : windowX;
            if (rail.last() >= base && rail.at() <= base + size - 1) { found.add(rail); }
        }
        return found;
    }
}
