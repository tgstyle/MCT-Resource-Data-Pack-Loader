package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.def.CityMapDef;
import mctmods.resourcedatapackloader.content.worldgen.CityGrowth.City;
import mctmods.resourcedatapackloader.content.worldgen.CityGrowth.Growth;
import mctmods.resourcedatapackloader.content.worldgen.CityPlan.End;
import mctmods.resourcedatapackloader.content.worldgen.CityPlan.Line;
import mctmods.resourcedatapackloader.content.worldgen.CityPlan.Plot;
import mctmods.resourcedatapackloader.content.worldgen.CityPlan.Town;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Hashes;

import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nullable;

final class CitySeams {
    static final Map<List<Long>, Seam> TIES = new ConcurrentHashMap<>();
    private static final long SEAM_SALT = 0x5EA3E5L;
    private static final Seam NONE = new Seam(List.of(), List.of());

    record Leg(int districtX, int districtZ, Line line) {}

    record Seam(List<Leg> legs, List<Reach> reached) {}

    record Reach(long town, Line line, int end) {}

    private record Arrival(int along, int across, Line line, @Nullable Reach reach) {}

    private record Tie(List<Leg> legs, Arrival mine, Arrival theirs, int seam, int length) {}

    private CitySeams() {}

    static List<Line> through(CityGround ground, List<Town> towns, int districtX, int districtZ) {
        List<Line> found = new ArrayList<>();
        if (CityPlan.spacing() <= 1) { return found; }
        for (int first = 0; first < towns.size(); first++) {
            for (int second = first + 1; second < towns.size(); second++) {
                for (Leg leg : seam(ground, towns.get(first), towns.get(second)).legs()) {
                    if (leg.districtX() == districtX && leg.districtZ() == districtZ) { joined(found, leg.line()); }
                }
            }
        }
        return found;
    }

    private static void joined(List<Line> found, Line line) {
        Line held = line;
        for (int index = found.size() - 1; index >= 0; index--) {
            Line other = found.get(index);
            if (other.alongX() != held.alongX() || other.at() != held.at() || other.width() != held.width() || other.to() + 1 < held.from() || held.to() + 1 < other.from()) { continue; }
            held = reach(held, Math.min(held.from(), other.from()), Math.max(held.to(), other.to()));
            found.remove(index);
        }
        found.add(held);
    }

    static List<Reach> reaches(CityGround ground, @Nullable Town town) {
        List<Reach> found = new ArrayList<>();
        CityMapDef map = ContentCity.layout();
        if (town == null || map == null || CityPlan.spacing() <= 1) { return found; }
        for (int[] center : centers(ground, town)) {
            Town site = CityLayout.mapTown(ground, map, center[0], center[1], true);
            if (site == null || site.key() == town.key() || CityGrowth.tooSmall(ground, map, center[0], center[1], true, site)) { continue; }
            for (Reach reach : seam(ground, town, site).reached()) {
                if (reach.town() == town.key()) { found.add(reach); }
            }
        }
        return found;
    }

    static void reached(List<Reach> reaches, List<Line> rows, List<Line> columns) {
        for (Reach reach : reaches) {
            Line way = reach.line();
            List<Line> lines = way.alongX() ? rows : columns;
            int index = lines.size() - 1;
            while (index >= 0 && !reaching(lines.get(index), reach)) { index--; }
            if (index < 0) {
                lines.add(way);
                continue;
            }
            Line line = lines.get(index);
            if (line.to() == reach.end()) { lines.set(index, line.reaching(line.from(), way.to()).ending(line.endsLow(), End.MET)); }
            else { lines.set(index, line.reaching(way.from(), line.to()).ending(End.MET, line.endsHigh())); }
        }
    }

    private static boolean reaching(Line line, Reach reach) { return !line.alley() && line.at() == reach.line().at() && (line.to() == reach.end() || line.from() == reach.end()); }

    static void madeWay(List<Reach> reaches, List<Plot> plots) {
        for (Reach reach : reaches) {
            BoundingBox way = CityMapDistricts.box(reach.line());
            plots.removeIf(plot -> plot.fromX() <= way.maxX() && plot.toX() >= way.minX() && plot.fromZ() <= way.maxZ() && plot.toZ() >= way.minZ());
        }
    }

    private static Seam seam(CityGround ground, Town one, Town two) {
        Town low = one.hash() < two.hash() ? one : two;
        Town high = low == one ? two : one;
        List<Long> held = List.of(low.key(), high.key());
        Seam known = TIES.get(held);
        if (known != null) { return known; }
        Seam made = ContentLog.LOGGER.aloud(() -> tie(ground, low, high));
        Seam raced = TIES.putIfAbsent(held, made);
        return raced == null ? made : raced;
    }

    private static int reach() { return CityGrowth.march() * 2 + 64; }

    private static Seam tie(CityGround ground, Town town, Town site) {
        int wx = town.wellX();
        int wz = town.wellZ();
        int bx = site.wellX();
        int bz = site.wellZ();
        if (Math.abs(bx - wx) > reach() || Math.abs(bz - wz) > reach()) { return NONE; }
        Set<Long> mine = districts(ground, town);
        Set<Long> theirs = districts(ground, site);
        if (mine.isEmpty() || theirs.isEmpty()) { return NONE; }
        List<Reach> stubbed = new ArrayList<>();
        if (ContentCity.layout() != null) { stubs(ground, site, town, stubbed); }
        if (ContentCity.layout() == null ? meets(mine, theirs) : !stubbed.isEmpty() || streetsMeet(town.drawn(), site.drawn())) {
            ContentLog.LOGGER.debug("The village at {}, {} already meets the village at {}, {} at a street, so no seam is tied between them", wx, wz, bx, bz);
            return new Seam(List.of(), List.copyOf(stubbed));
        }
        Set<Long> taken = taken(ground, town, site, mine, theirs);
        boolean alongX = Math.abs(bx - wx) >= Math.abs(bz - wz);
        boolean onward = alongX ? bx > wx : bz > wz;
        Town west = onward ? town : site;
        Town east = onward ? site : town;
        Set<Long> westHeld = onward ? mine : theirs;
        Set<Long> eastHeld = onward ? theirs : mine;
        int full = CityPlan.fullWidth();
        int keepOff = CityPlan.plazaReach() + full + 8;
        int westAt = alongX ? west.wellX() : west.wellZ();
        int eastAt = alongX ? east.wellX() : east.wellZ();
        int apart = eastAt - westAt;
        if (apart < 2 * keepOff + full) { return NONE; }
        int lowSeam = westAt + keepOff;
        int highSeam = eastAt - keepOff;
        RandomSource roll = RandomSource.create(Hashes.mix(ground.seed() ^ CityPlan.SALT ^ SEAM_SALT, wx + bx, 0, wz + bz));
        int seeded = Mth.clamp((westAt + eastAt) / 2 + roll.nextInt(apart / 2 + 1) - apart / 4, lowSeam, highSeam);
        List<Arrival> leaving = arrivals(ground, west, westHeld, alongX, true, westAt, eastAt);
        List<Arrival> coming = arrivals(ground, east, eastHeld, alongX, false, westAt, eastAt);
        if (leaving.isEmpty() || coming.isEmpty()) {
            ContentLog.LOGGER.debug("The village at {}, {} has no street heading toward the village site at {}, {}", wx, wz, bx, bz);
            return NONE;
        }
        Tie collinear = null;
        for (Arrival w : leaving) {
            for (Arrival e : coming) {
                if (w.across() != e.across() || w.line().at() != e.line().at() || w.line().width() != e.line().width() || e.along() - w.along() < 1) { continue; }
                List<Leg> legs = new ArrayList<>();
                for (int along = w.along() + 1; along < e.along(); along++) { legs.add(leg(alongX, along, w.across(), row(w.line(), along))); }
                Tie found = new Tie(legs, w, e, 0, e.along() - w.along());
                if (clear(ground, legs, taken) && (collinear == null || found.length() < collinear.length())) { collinear = found; }
            }
        }
        if (collinear != null) {
            Arrival m = onward ? collinear.mine() : collinear.theirs();
            Arrival t = onward ? collinear.theirs() : collinear.mine();
            ContentLog.LOGGER.debug("The street at {}, {} of the village at {}, {} runs to the street at {}, {} of the village at {}, {} on the same line, so the two villages join there and no tie road is needed", endX(m, alongX, onward), endZ(m, alongX, onward), wx, wz, endX(t, alongX, !onward), endZ(t, alongX, !onward), bx, bz);
            return laid(collinear);
        }
        int cross = CityDistricts.crossAt();
        List<Integer> seams = new ArrayList<>();
        for (int column = CityPlan.districtOf(lowSeam, alongX); column <= CityPlan.districtOf(highSeam, alongX); column++) {
            int middle = CityPlan.windowOf(column, alongX) + cross + full / 2;
            if (middle >= lowSeam && middle <= highSeam) { seams.add(column); }
        }
        seams.sort(Comparator.comparingInt((Integer column) -> Math.abs(CityPlan.windowOf(column, alongX) + cross + full / 2 - seeded)).thenComparingInt(column -> column));
        for (int column : seams) {
            Tie best = null;
            for (Arrival w : leaving) {
                for (Arrival e : coming) {
                    if (w.along() >= column || e.along() <= column) { continue; }
                    List<Leg> legs = bent(alongX, w, e, column);
                    Tie found = new Tie(legs, w, e, CityPlan.windowOf(column, alongX) + cross + full / 2, legs.size());
                    if (clear(ground, legs, taken) && (best == null || found.length() < best.length())) { best = found; }
                }
            }
            if (best == null) { continue; }
            Arrival m = onward ? best.mine() : best.theirs();
            Arrival t = onward ? best.theirs() : best.mine();
            String axis = alongX ? "x" : "z";
            ContentLog.LOGGER.debug("The street at {}, {} of the village at {}, {} reaches the seam at {} {} toward the site at {}, {}, and the tie road {} is laid along it", endX(m, alongX, onward), endZ(m, alongX, onward), wx, wz, axis, best.seam(), bx, bz, span(best.legs(), !alongX));
            ContentLog.LOGGER.debug("The street at {}, {} of the neighboring village at {}, {} is brought to the tie road as well, so the two villages join at {} {}", endX(t, alongX, !onward), endZ(t, alongX, !onward), bx, bz, axis, best.seam());
            return laid(best);
        }
        ContentLog.LOGGER.debug("The village at {}, {} finds no seam between {} and {} along {} at which it can tie toward the village site at {}, {}", wx, wz, lowSeam, highSeam, alongX ? "x" : "z", bx, bz);
        return NONE;
    }

    private static Seam laid(Tie tie) {
        List<Reach> reached = new ArrayList<>();
        if (tie.mine().reach() != null) { reached.add(tie.mine().reach()); }
        if (tie.theirs().reach() != null) { reached.add(tie.theirs().reach()); }
        return new Seam(List.copyOf(tie.legs()), List.copyOf(reached));
    }

    private static int endX(Arrival arrival, boolean alongX, boolean onward) {
        int size = CityPlan.district();
        return alongX ? CityPlan.windowOf(arrival.along(), true) + (onward ? size - 1 : 0) : arrival.line().middle();
    }

    private static int endZ(Arrival arrival, boolean alongX, boolean onward) {
        int size = CityPlan.district();
        return alongX ? arrival.line().middle() : CityPlan.windowOf(arrival.along(), false) + (onward ? size - 1 : 0);
    }

    private static String span(List<Leg> legs, boolean alongX) {
        int from = Integer.MAX_VALUE;
        int to = Integer.MIN_VALUE;
        int at = 0;
        for (Leg leg : legs) {
            if (leg.line().alongX() != alongX) { continue; }
            from = Math.min(from, leg.line().from());
            to = Math.max(to, leg.line().to());
            at = leg.line().at();
        }
        return alongX ? "x " + from + " to " + to + " at z " + at : "z " + from + " to " + to + " at x " + at;
    }

    private static List<Leg> bent(boolean alongX, Arrival w, Arrival e, int column) {
        int size = CityPlan.district();
        int full = CityPlan.fullWidth();
        int at = CityPlan.windowOf(column, alongX) + CityDistricts.crossAt();
        int last = at + full - 1;
        Line westRow = w.line();
        Line eastRow = e.line();
        List<Leg> legs = new ArrayList<>();
        for (int along = w.along() + 1; along < column; along++) { legs.add(leg(alongX, along, w.across(), row(westRow, along))); }
        legs.add(leg(alongX, column, w.across(), reach(westRow, CityPlan.windowOf(column, alongX), last)));
        if (w.across() == e.across()) {
            int from = Math.min(westRow.at(), eastRow.at());
            int to = Math.max(westRow.last(), eastRow.last());
            legs.add(leg(alongX, column, w.across(), new Line(at, full, false, !alongX, from, to, End.MET, End.MET)));
        }
        else {
            int dir = e.across() > w.across() ? 1 : -1;
            int start = CityPlan.windowOf(w.across(), !alongX);
            legs.add(leg(alongX, column, w.across(), new Line(at, full, false, !alongX, dir > 0 ? westRow.at() : start, dir > 0 ? start + size - 1 : westRow.last(), End.MET, End.MET)));
            for (int across = w.across() + dir; across != e.across(); across += dir) { legs.add(leg(alongX, column, across, new Line(at, full, false, !alongX, CityPlan.windowOf(across, !alongX), CityPlan.windowOf(across, !alongX) + size - 1, End.MET, End.MET))); }
            int end = CityPlan.windowOf(e.across(), !alongX);
            legs.add(leg(alongX, column, e.across(), new Line(at, full, false, !alongX, dir > 0 ? end : eastRow.at(), dir > 0 ? eastRow.last() : end + size - 1, End.MET, End.MET)));
        }
        legs.add(leg(alongX, column, e.across(), reach(eastRow, at, CityPlan.windowOf(column, alongX) + size - 1)));
        for (int along = column + 1; along < e.along(); along++) { legs.add(leg(alongX, along, e.across(), row(eastRow, along))); }
        return legs;
    }

    private static Leg leg(boolean alongX, int along, int across, Line line) { return alongX ? new Leg(along, across, line) : new Leg(across, along, line); }

    private static Line row(Line street, int along) {
        int size = CityPlan.district();
        int from = CityPlan.windowOf(along, street.alongX());
        return reach(street, from, from + size - 1);
    }

    private static Line reach(Line street, int from, int to) { return new Line(street.at(), street.width(), false, street.alongX(), from, to, End.MET, End.MET); }

    private static boolean clear(CityGround ground, List<Leg> legs, @Nullable Set<Long> taken) {
        for (Leg leg : legs) {
            if (taken != null) {
                if (taken.contains(CityPlan.packed(leg.districtX(), leg.districtZ()))) { return false; }
                continue;
            }
            CityPlan held = CityPlanTowns.raw(ground, leg.districtX(), leg.districtZ());
            if (held != null && held.town() != null) { return false; }
        }
        return true;
    }

    @Nullable private static Set<Long> taken(CityGround ground, Town town, Town site, Set<Long> mine, Set<Long> theirs) {
        if (ContentCity.layout() == null) { return null; }
        Set<Long> found = new HashSet<>(mine);
        found.addAll(theirs);
        for (Town one : List.of(town, site)) {
            for (Town near : sites(ground, one)) {
                if (near.key() != town.key() && near.key() != site.key()) { found.addAll(districts(ground, near)); }
            }
        }
        return found;
    }

    private static List<Arrival> arrivals(CityGround ground, Town town, Set<Long> held, boolean alongX, boolean onward, int westAt, int eastAt) {
        int size = CityPlan.district();
        Growth growth = ContentCity.layout() == null ? null : growth(ground, town);
        List<Arrival> found = new ArrayList<>();
        for (long packed : held) {
            int districtX = (int) (packed >> 32);
            int districtZ = (int) packed;
            int along = alongX ? districtX : districtZ;
            int across = alongX ? districtZ : districtX;
            int next = along + (onward ? 1 : -1);
            if (held.contains(alongX ? CityPlan.packed(next, across) : CityPlan.packed(across, next))) { continue; }
            int end = CityPlan.windowOf(along, alongX) + (onward ? size - 1 : 0);
            if (end <= westAt || end >= eastAt) { continue; }
            if (ContentCity.layout() != null && (growth == null || !growth.districts().containsKey(packed))) {
                drawnArrivals(town, alongX, onward, along, across, end, found);
                continue;
            }
            if (growth == null && townless(ground, town, districtX, districtZ)) { continue; }
            CityPlan plan = growth == null ? CityPlan.of(ground, districtX, districtZ) : growth.districts().get(packed);
            if (plan == null) { continue; }
            for (Line line : alongX ? plan.alongX() : plan.alongZ()) {
                if (line.alley() || (onward ? line.to() != end || line.endsHigh() != End.MET : line.from() != end || line.endsLow() != End.MET)) { continue; }
                found.add(new Arrival(along, across, line, null));
            }
        }
        found.sort(Comparator.comparingInt(Arrival::across).thenComparingInt(arrival -> arrival.line().at()));
        return found;
    }

    private static boolean townless(CityGround ground, Town town, int districtX, int districtZ) {
        CityPlan raw = CityPlanTowns.raw(ground, districtX, districtZ);
        if (raw != null && raw.town() != null) { return false; }
        ContentLog.LOGGER.debug("The district at {}, {} is counted to the city at {}, {} but plans no town of its own, so no street of it heads toward a neighboring city", CityPlan.windowOf(districtX, true), CityPlan.windowOf(districtZ, false), town.wellX(), town.wellZ());
        return true;
    }

    private static void drawnArrivals(Town town, boolean alongX, boolean onward, int along, int across, int end, List<Arrival> found) {
        int edge = onward ? (alongX ? town.toX() : town.toZ()) : (alongX ? town.fromX() : town.fromZ());
        if (CityPlan.districtOf(edge, alongX) != along) { return; }
        for (Line line : town.drawn()) {
            if (line.alley() || line.alongX() != alongX || CityPlan.districtOf(line.at(), !alongX) != across || CityPlan.districtOf(line.last(), !alongX) != across) { continue; }
            if ((onward ? line.to() : line.from()) != edge) { continue; }
            Line reached = onward ? line.reaching(line.from(), end) : line.reaching(end, line.to());
            Reach reach = edge == end ? null : new Reach(town.key(), reach(line, onward ? edge + 1 : end, onward ? end : edge - 1), edge);
            found.add(new Arrival(along, across, reached, reach));
        }
    }

    private static boolean meets(Set<Long> mine, Set<Long> theirs) {
        for (long packed : mine) {
            int x = (int) (packed >> 32);
            int z = (int) packed;
            if (theirs.contains(packed) || theirs.contains(CityPlan.packed(x + 1, z)) || theirs.contains(CityPlan.packed(x - 1, z)) || theirs.contains(CityPlan.packed(x, z + 1)) || theirs.contains(CityPlan.packed(x, z - 1))) { return true; }
        }
        return false;
    }

    private static Set<Long> districts(CityGround ground, Town town) {
        int size = CityPlan.district();
        if (ContentCity.layout() != null) {
            Set<Long> found = new HashSet<>();
            for (int districtX = CityPlan.districtOf(town.fromX(), true); districtX <= CityPlan.districtOf(town.toX(), true); districtX++) {
                for (int districtZ = CityPlan.districtOf(town.fromZ(), false); districtZ <= CityPlan.districtOf(town.toZ(), false); districtZ++) { found.add(CityPlan.packed(districtX, districtZ)); }
            }
            Growth growth = growth(ground, town);
            if (growth != null) { found.addAll(growth.districts().keySet()); }
            return found;
        }
        City city = CityGrowth.CITIES.get(town.key());
        if (city != null) { return city.districts(); }
        int grown = CityGrowth.march() / size + 2;
        Set<Long> found = new HashSet<>();
        for (int districtX = CityPlan.districtOf(town.fromX(), true) - grown; districtX <= CityPlan.districtOf(town.toX(), true) + grown; districtX++) {
            for (int districtZ = CityPlan.districtOf(town.fromZ(), false) - grown; districtZ <= CityPlan.districtOf(town.toZ(), false) + grown; districtZ++) {
                CityPlan held = CityPlanTowns.raw(ground, districtX, districtZ);
                if (held != null && held.town() != null && held.town().key() == town.key()) { found.add(CityPlan.packed(districtX, districtZ)); }
            }
        }
        return found;
    }

    @Nullable private static Growth growth(CityGround ground, Town town) {
        CityMapDef map = ContentCity.layout();
        if (map == null) { return null; }
        for (int[] center : centers(ground, town)) {
            Town held = CityLayout.mapTown(ground, map, center[0], center[1], true);
            if (held != null && held.key() == town.key()) { return CityGrowth.mapGrowth(ground, map, center[0], center[1], true, held); }
        }
        return null;
    }

    static boolean facesNeighbor(CityGround ground, @Nullable Town town, boolean alongX, int end, int dir, int row) {
        if (town == null || CityPlan.spacing() <= 1) { return false; }
        int reach = CityGrowth.march();
        for (Town site : sites(ground, town)) {
            int siteAt = alongX ? site.wellX() : site.wellZ();
            int siteRow = alongX ? site.wellZ() : site.wellX();
            if ((siteAt - end) * dir <= 0 || Math.abs(row - siteRow) > reach) { continue; }
            if (touches(town, site)) { continue; }
            return true;
        }
        return false;
    }

    private static boolean touches(Town town, Town site) {
        City mine = CityGrowth.CITIES.get(town.key());
        City theirs = CityGrowth.CITIES.get(site.key());
        if (mine != null && theirs != null && ContentCity.layout() == null) { return meets(mine.districts(), theirs.districts()); }
        if (ContentCity.layout() != null) { return streetsMeet(town.drawn(), site.drawn()); }
        return site.fromX() <= town.toX() + 1 && site.toX() >= town.fromX() - 1 && site.fromZ() <= town.toZ() + 1 && site.toZ() >= town.fromZ() - 1;
    }

    private static boolean streetsMeet(List<Line> own, List<Line> theirs) {
        for (Line mine : own) {
            if (mine.alley()) { continue; }
            BoundingBox a = CityMapDistricts.box(mine);
            for (Line other : theirs) {
                if (other.alley()) { continue; }
                BoundingBox b = CityMapDistricts.box(other);
                boolean overX = b.maxX() >= a.minX() && b.minX() <= a.maxX();
                boolean overZ = b.maxZ() >= a.minZ() && b.minZ() <= a.maxZ();
                boolean nearX = b.maxX() >= a.minX() - 1 && b.minX() <= a.maxX() + 1;
                boolean nearZ = b.maxZ() >= a.minZ() - 1 && b.minZ() <= a.maxZ() + 1;
                if ((overX && nearZ) || (overZ && nearX)) { return true; }
            }
        }
        return false;
    }

    private static void stubs(CityGround ground, Town neighbor, Town town, List<Reach> found) {
        int reach = CityEnds.attachGap() * 2;
        List<Line> everyone = new ArrayList<>(neighbor.drawn());
        everyone.addAll(town.drawn());
        Growth growth = growth(ground, town);
        for (Line line : neighbor.drawn()) {
            if (line.alley()) { continue; }
            boolean alongX = line.alongX();
            for (int dir = -1; dir <= 1; dir += 2) {
                int end = dir > 0 ? line.to() : line.from();
                int endX = alongX ? end : line.middle();
                int endZ = alongX ? line.middle() : end;
                if (CityEnds.metBeyond(everyone, line, alongX, end + dir, line.at(), line.last())) { continue; }
                Line best = null;
                int bestAhead = Integer.MAX_VALUE;
                for (Line met : town.drawn()) {
                    if (met.alley() || met.alongX() == alongX) { continue; }
                    int ahead = dir > 0 ? met.at() - end : end - met.last();
                    if (ahead < 2 || ahead > reach || ahead >= bestAhead) { continue; }
                    if (met.to() < line.at() || met.from() > line.last()) { continue; }
                    bestAhead = ahead;
                    best = met;
                }
                if (best == null) { continue; }
                Line stub = reach(line, dir > 0 ? end + 1 : best.last() + 1, dir > 0 ? best.at() - 1 : end - 1);
                BoundingBox strip = CityMapDistricts.box(stub);
                int bestX = best.alongX() ? best.from() : best.at();
                int bestZ = best.alongX() ? best.at() : best.from();
                if (CityEnds.stubHeld(ground, town, neighbor, line, strip)) {
                    ContentLog.LOGGER.debug("The dead end at {}, {} of the neighboring village cannot be reached from the street at {}, {}: the strip is held", endX, endZ, bestX, bestZ);
                    continue;
                }
                if (growth != null) {
                    for (Plot plot : growth.plots()) {
                        if (plot.fromX() <= strip.maxX() && plot.toX() >= strip.minX() && plot.fromZ() <= strip.maxZ() && plot.toZ() >= strip.minZ()) { ContentLog.LOGGER.debug("{} at {}, {} makes way for the stub reaching a neighboring village's dead end", plot.def().key(), plot.fromX(), plot.fromZ()); }
                    }
                }
                found.add(new Reach(neighbor.key(), stub, end));
                found.add(new Reach(town.key(), stub, Integer.MIN_VALUE));
                ContentLog.LOGGER.debug("The dead end at {}, {} of the neighboring village at {}, {} is reached by the stub {} from the street at {}, {}, so the two villages join there", endX, endZ, neighbor.wellX(), neighbor.wellZ(), strip, bestX, bestZ);
                break;
            }
        }
    }

    private static List<Town> sites(CityGround ground, Town town) {
        List<Town> found = new ArrayList<>();
        CityMapDef map = ContentCity.layout();
        for (int[] center : centers(ground, town)) {
            Town site = site(ground, map, center, CityPlan.spacing());
            if (site == null || site.key() == town.key() || Math.abs(site.wellX() - town.wellX()) > reach() || Math.abs(site.wellZ() - town.wellZ()) > reach()) { continue; }
            found.add(site);
        }
        return found;
    }

    private static List<int[]> centers(CityGround ground, Town town) {
        List<int[]> found = new ArrayList<>();
        int spacing = CityPlan.spacing();
        int regionX = Math.floorDiv(CityPlan.districtOf(town.wellX(), true), spacing);
        int regionZ = Math.floorDiv(CityPlan.districtOf(town.wellZ(), false), spacing);
        for (int aroundX = -1; aroundX <= 1; aroundX++) {
            for (int aroundZ = -1; aroundZ <= 1; aroundZ++) {
                int[] center = CityDistricts.center(ground, (regionX + aroundX) * spacing, (regionZ + aroundZ) * spacing);
                if (center != null) { found.add(center); }
            }
        }
        return found;
    }

    @Nullable private static Town site(CityGround ground, @Nullable CityMapDef map, int[] center, int spacing) {
        if (map != null) { return CityLayout.mapTown(ground, map, center[0], center[1], true); }
        City city = CityGrowth.CITIES.computeIfAbsent(CityPlan.packed(center[0], center[1]), key -> CityGrowth.grow(ground, center[0], center[1], spacing));
        return city.districts().isEmpty() ? null : CityPlanTowns.town(ground, city);
    }
}
