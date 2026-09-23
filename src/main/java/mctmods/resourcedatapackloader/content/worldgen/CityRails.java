package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nullable;

public final class CityRails {
    public static final int CLEAR = 4;
    private static final int COURSES = 2;
    static final int MARGIN = 7;
    static final int LEAST_OPEN = 8;
    static final int OVER = 6;
    static final int FILL = 3;
    private static final int SLIDE = 48;
    static final int NEAR = 8;
    private static final long SALT = 0x1F0D3F5B7C9E1A37L ^ 0x5EED5L;
    private static final Map<Long, List<CityPlan.Rail>> PLACED = new ConcurrentHashMap<>();
    private static final Map<Long, List<CityPlan.Rail>> FITTED = new ConcurrentHashMap<>();
    static final Map<Long, CityPlan.Town> TOWNS = new ConcurrentHashMap<>();
    private static final Map<CityPlan.Rail, Laid> LAID = new ConcurrentHashMap<>();
    private static final Map<CityPlan.Rail, Bed> BEDS = new ConcurrentHashMap<>();
    private static final Map<Long, List<CityPlan.Rail>> STAGED = new ConcurrentHashMap<>();

    private CityRails() {}

    public record Station(int row, int near, int way) {
        public int heart() { return row + ContentCityStairsPiece.RUN / 2; }

        public int first() { return row - 1; }

        public int last() { return row + ContentCityStairsPiece.RUN + 1; }
    }

    @Nullable public static Station openingAt(Collection<Laid> tracks, CityPlan.Line street, int row, boolean high) {
        for (Laid track : tracks) {
            CityPlan.Rail rail = track.rail();
            if (rail.alongX() != street.alongX() || rail.middle() < street.at() || rail.middle() > street.last()) { continue; }
            for (Station station : track.stations()) {
                if (row <= station.first() || row >= station.last()) { continue; }
                if (street.from() > station.first() || street.to() < station.last()) { continue; }
                if (high ? station.near() - 1 > street.last() : station.near() + ContentCityStairsPiece.WIDE < street.at()) { return station; }
            }
        }
        return null;
    }

    public record Laid(CityPlan.Rail rail, int start, int[] profile, int[] ground, boolean[] bridged, boolean[] tunnel, boolean[] bored, boolean[] crossed, boolean[] frames, @Nullable int[] rising, List<Station> stations, List<Station> claimed) {
        public int level(int row) { return profile[Mth.clamp(row - start, 0, profile.length - 1)]; }

        public boolean within(int row) { return row >= start && row < start + profile.length; }

        public boolean tunnelAt(int row) { return within(row) && tunnel[row - start]; }

        public boolean boredAt(int row) { return within(row) && bored[row - start]; }
    }

    record Bed(int start, int[] profile, boolean[] bored) {
        int level(int row) { return profile[Mth.clamp(row - start, 0, profile.length - 1)]; }

        boolean within(int row) { return row >= start && row < start + profile.length; }

        boolean boredAt(int row) { return within(row) && bored[row - start]; }
    }

    public static void forget() {
        PLACED.clear();
        STAGED.clear();
        FITTED.clear();
        TOWNS.clear();
        LAID.clear();
        BEDS.clear();
    }

    public static List<CityPlan.Rail> placed(long seed, CityPlan.Town town) {
        List<CityPlan.Rail> held = PLACED.get(town.key());
        if (held != null) { return held; }
        RandomSource roll = RandomSource.create(seed ^ SALT ^ town.salt());
        List<CityPlan.Rail> found = new ArrayList<>();
        CityRailsFit.found(roll, town, false, found);
        CityRailsFit.found(roll, town, true, found);
        List<CityPlan.Rail> made = List.copyOf(found);
        PLACED.putIfAbsent(town.key(), made);
        return made;
    }

    static int spacing(boolean sub) { return Math.max(1, ContentControl.number(ContentControl.VILLAGES, sub ? "villageSubwaySpacing" : "villageRailSpacing", sub ? Config.worldgen.villageSubwaySpacing() : Config.worldgen.villageRailSpacing())); }

    static int lines(boolean sub) { return Math.max(0, ContentControl.number(ContentControl.VILLAGES, sub ? "villageSubwayLines" : "villageRailLines", sub ? Config.worldgen.villageSubwayLines() : Config.worldgen.villageRailLines())); }

    static int clear(boolean sub) { return sub ? 0 : CityPlan.plazaReach() + Math.max(13, ContentVillages.largestPlot()) + CityPlan.streetFullWidth() + (CityPlan.railWidth(false) - 1) / 2 + 2; }

    static List<CityPlan.Rail> staged(CityGround ground, CityPlan.Town town) {
        List<CityPlan.Rail> held = STAGED.get(town.key());
        if (held != null) { return held; }
        TOWNS.putIfAbsent(town.key(), town);
        List<CityPlan.Rail> found = new ArrayList<>();
        for (CityPlan.Rail rolled : placed(ground.seed(), town)) {
            int linked = CityLinks.linkCenter(ground, town, rolled);
            if (linked == Integer.MIN_VALUE && rolled.subway()) { continue; }
            CityPlan.Rail rail = CityRailsFit.fit(ground, town, rolled, linked == Integer.MIN_VALUE ? rolled.middle() : linked);
            if (rail != null) { found.add(rail); }
        }
        List<CityPlan.Rail> made = List.copyOf(CityLinks.joined(ground, town, found));
        STAGED.putIfAbsent(town.key(), made);
        return made;
    }

    public static List<CityPlan.Rail> fitted(CityGround ground, CityPlan.Town town) {
        List<CityPlan.Rail> held = FITTED.get(town.key());
        if (held != null) { return held; }
        List<CityPlan.Rail> lines = new ArrayList<>();
        List<CityPlan.Rail> halves = new ArrayList<>();
        for (CityPlan.Rail rail : staged(ground, town)) {
            if (CityLinks.trunkData(rail) == null) { lines.add(rail); }
            else { halves.add(rail); }
        }
        for (CityPlan.Rail rolled : placed(ground.seed(), town)) {
            if (!rolled.subway() || CityLinks.linkCenter(ground, town, rolled) != Integer.MIN_VALUE) { continue; }
            List<CityPlan.Rail> mine = new ArrayList<>(lines);
            mine.addAll(halves);
            CityPlan.Rail rail = CityRailsFit.fit(ground, town, rolled, CityRailsFit.seekRoad(ground, town, mine, rolled, spacing(true)));
            if (rail != null) { lines.add(rail); }
        }
        lines.addAll(halves);
        List<CityPlan.Rail> made = List.copyOf(lines);
        FITTED.putIfAbsent(town.key(), made);
        return made;
    }

    static List<int[]> reaches(CityGround ground, CityPlan.Town town) {
        List<int[]> found = new ArrayList<>();
        if (FITTED.containsKey(town.key())) { return found; }
        for (CityPlan.Rail rail : placed(ground.seed(), town)) {
            int[] rows = CityRailsFit.rows(town, rail);
            int half = halfOf(rail) + (rail.subway() ? spacing(true) : 0);
            int alongLeast = CityPlan.districtOf(rows[0], rail.alongX());
            int alongMost = CityPlan.districtOf(rows[1], rail.alongX());
            int acrossLeast = CityPlan.districtOf(rail.middle() - half, !rail.alongX());
            int acrossMost = CityPlan.districtOf(rail.middle() + half, !rail.alongX());
            found.add(rail.alongX() ? new int[] {alongLeast, acrossLeast, alongMost, acrossMost} : new int[] {acrossLeast, alongLeast, acrossMost, alongMost});
        }
        return found;
    }

    @Nullable public static Laid laid(CityGround ground, CityPlan.Rail rail) {
        Laid held = LAID.get(rail);
        if (held != null) { return held; }
        Laid made = CityLinks.trunkLaid(ground, rail);
        if (made == null) {
            CityPlan.Town town = TOWNS.get(rail.town());
            if (town == null) { return null; }
            made = ContentLog.LOGGER.aloud(() -> CityRailsGrade.lay(ground, town, rail));
        }
        Laid kept = LAID.putIfAbsent(rail, made);
        return kept == null ? made : kept;
    }

    public static int halfOf(CityPlan.Rail rail) { return (rail.width() - 1) / 2 + (rail.subway() ? ContentCity.stationReach() : 0); }

    @Nullable static Bed bed(CityGround ground, CityPlan.Rail rail) {
        Bed held = BEDS.get(rail);
        if (held != null) { return held; }
        Bed made = CityLinks.trunkBed(ground, rail);
        if (made == null) {
            CityPlan.Town town = TOWNS.get(rail.town());
            if (town == null || rail.subway()) { return null; }
            made = CityRailsGrade.graded(ground, rail, fitted(ground, town));
        }
        Bed kept = BEDS.putIfAbsent(rail, made);
        return kept == null ? made : kept;
    }

    static int[] floorRows(CityGround ground, boolean alongX, int rowLeast, int rowMost, int acrossLeast, int acrossMost) {
        if (alongX) { ground.warm(rowLeast, acrossLeast, rowMost, acrossMost); }
        else { ground.warm(acrossLeast, rowLeast, acrossMost, rowMost); }
        int rows = rowMost - rowLeast + 1;
        int sea = ground.sea();
        int[] floor = new int[rows];
        int wide = acrossMost - acrossLeast + 1;
        int[] taken = new int[wide];
        for (int at = 0; at < rows; at++) {
            int count = 0;
            for (int across = acrossLeast; across <= acrossMost; across++) {
                int sampled = alongX ? ground.floor(rowLeast + at, across) : ground.floor(across, rowLeast + at);
                if (sampled < sea - 1) { continue; }
                taken[count++] = sampled;
            }
            if (count * 2 <= wide) {
                floor[at] = Integer.MIN_VALUE;
                continue;
            }
            Arrays.sort(taken, 0, count);
            floor[at] = taken[count / 2];
        }
        return floor;
    }

    static void gather(CityGround ground, @Nullable CityPlan.Town town, boolean alongX, int alongLeast, int alongMost, int acrossLeast, int acrossMost, List<CityPlan.Line> lines, List<BoundingBox> wells) {
        Set<CityPlan.Line> seen = new LinkedHashSet<>();
        for (int along = CityPlan.districtOf(alongLeast, alongX); along <= CityPlan.districtOf(alongMost, alongX); along++) {
            for (int across = CityPlan.districtOf(acrossLeast, !alongX); across <= CityPlan.districtOf(acrossMost, !alongX); across++) {
                CityPlan plan = CityPlan.of(ground, alongX ? along : across, alongX ? across : along);
                if (plan == null || plan.town() == null || (town != null && plan.town().key() != town.key())) { continue; }
                seen.addAll(plan.alongX());
                seen.addAll(plan.alongZ());
                for (BoundingBox box : plan.wellBoxes()) {
                    if (!wells.contains(box)) { wells.add(box); }
                }
            }
        }
        lines.addAll(seen);
    }

    static List<CityPlan.Line> streetsAcross(CityGround ground, CityPlan.Rail rail) {
        int half = halfOf(rail);
        List<CityPlan.Line> lines = new ArrayList<>();
        gather(ground, null, rail.alongX(), rail.from() - NEAR, rail.to() + NEAR, rail.middle() - half - NEAR, rail.middle() + half + NEAR, lines, new ArrayList<>());
        return lines;
    }

    static boolean surfaced(@Nullable int[] rising, int row) {
        if (rising == null) { return false; }
        return rising[1] > 0 ? row >= rising[0] : row <= rising[0];
    }

    @Nullable private static int[] streetOver(List<CityPlan.Line> lines, CityPlan.Rail rail) {
        int center = rail.middle();
        int least = Integer.MAX_VALUE;
        int most = Integer.MIN_VALUE;
        for (CityPlan.Line line : lines) {
            if (line.alongX() != rail.alongX() || center < line.at() || center > line.last()) { continue; }
            least = Math.min(least, line.from());
            most = Math.max(most, line.to());
        }
        return least > most ? null : new int[] {least, most};
    }

    @Nullable static int[] surfacing(CityGround ground, CityPlan.Rail rail, List<CityPlan.Line> lines) {
        int[] ends = CityLinks.ends(rail);
        if (ends.length > 0) {
            int[] linked = CityLinks.climbOut(ends);
            ContentLog.LOGGER.debug("Subway line at {} {} toward its railway link", rail.middle(), linked == null ? "stays buried to meet a trunk underground" : "climbs out at row " + linked[0]);
            return linked;
        }
        int chance = ContentCity.subwaySurfaces();
        if (chance <= 0) { return null; }
        RandomSource roll = RandomSource.create(ground.seed() ^ SALT ^ (rail.at() * 341873128712L + (rail.alongX() ? 11L : 13L)));
        if (roll.nextInt(100) >= chance) {
            ContentLog.LOGGER.debug("Subway line at {} rolled to stay buried at {} in a hundred", rail.middle(), chance);
            return null;
        }
        int least = rail.from();
        int most = rail.to();
        int ramp = ContentCity.subwayDepth() * ContentCity.railClimb(true);
        int shortest = ramp + LEAST_OPEN;
        if (most - least < shortest) { return null; }
        int run = ramp + Math.max(LEAST_OPEN, CityPlan.railTail(true));
        int[] street = streetOver(lines, rail);
        int lowRow = street == null ? least + run : Math.min(least + run, street[0] - 1);
        int highRow = street == null ? most - run : Math.max(most - run, street[1] + 1);
        boolean canLow = lowRow - least >= shortest;
        boolean canHigh = most - highRow >= shortest;
        ContentLog.LOGGER.debug("Subway line at {} weighs a climb-out: rows {} to {}, ramp {} and run {} (shortest {}), streets over it {}, low end at row {} {}, high end at row {} {}", rail.middle(), least, most, ramp, run, shortest, street == null ? "none" : street[0] + " to " + street[1], lowRow, canLow ? "can" : "cannot", highRow, canHigh ? "can" : "cannot");
        if (!canHigh && !canLow) { return null; }
        boolean high = canHigh && (!canLow || roll.nextBoolean());
        return high ? new int[] {highRow, 1} : new int[] {lowRow, -1};
    }

    static List<Station> claim(CityPlan.Rail rail, CityPlan.Town town, List<CityPlan.Line> lines, List<BoundingBox> wells, @Nullable int[] rising) {
        List<Station> found = new ArrayList<>();
        int[] street = streetOver(lines, rail);
        if (street == null) {
            ContentLog.LOGGER.debug("Subway line at {} has no street over it, so it gets no stations", rail.middle());
            return found;
        }
        int least = Math.max(rail.from(), street[0]);
        int most = Math.min(rail.to(), street[1]);
        if (rising != null) {
            int ramp = ContentCity.subwayDepth() * ContentCity.railClimb(true);
            if (rising[1] > 0) { most = Math.min(most, rising[0] - ramp - 1); }
            else { least = Math.max(least, rising[0] + ramp + 1); }
            ContentLog.LOGGER.debug("Subway line at {} climbs out past row {}, so its stations keep to rows {} to {}", rail.middle(), rising[0], least, most);
        }
        int wellRow = town.wellAlong(rail.alongX());
        claimAt(rail, lines, wells, found, wellRow, least, most);
        int run = ContentCity.stationRun();
        if (run <= 0) { return found; }
        for (int heart = wellRow - run; heart >= least; heart -= run) { claimAt(rail, lines, wells, found, heart, least, most); }
        for (int heart = wellRow + run; heart <= most; heart += run) { claimAt(rail, lines, wells, found, heart, least, most); }
        if (found.size() > 1) { ContentLog.LOGGER.debug("Subway line at {} carries {} station(s), one at the well and the rest every {} block(s) along it where the ground allowed one", rail.middle(), found.size(), run); }
        return found;
    }

    private static void claimAt(CityPlan.Rail rail, List<CityPlan.Line> lines, List<BoundingBox> wells, List<Station> found, int wanted, int least, int most) {
        boolean alongX = rail.alongX();
        int center = rail.middle();
        int out = ContentCity.outFromLine(ContentCity.railBedHalf(true));
        int half = ContentCity.stationLength() / 2;
        int run = ContentCityStairsPiece.RUN;
        int wide = ContentCityStairsPiece.WIDE;
        for (int slide = 0; slide <= SLIDE; slide++) {
            for (int way = 0; way < (slide == 0 ? 1 : 2); way++) {
                int row = wanted + (way == 0 ? slide : -slide);
                if (row - 1 < least + half || row + run + 1 > most - half) { continue; }
                if (crowded(found, row) || streetAlong(lines, alongX, center, row - 1, row + run + 1) == null) { continue; }
                for (int side = 0; side < 2; side++) {
                    int near = side == 0 ? center + out : center - out - wide + 1;
                    BoundingBox box = alongX ? new BoundingBox(row - 1, 0, near - 1, row + run + 1, 0, near + wide) : new BoundingBox(near - 1, 0, row - 1, near + wide, 0, row + run + 1);
                    if (onRoadOrWell(lines, wells, box) || onPlaza(wells, box)) { continue; }
                    found.add(new Station(row, near, near > center ? 1 : -1));
                    ContentLog.LOGGER.debug("A subway station plot is claimed at {}, {} to {}, {}, {} block(s) from the well along the line", box.minX(), box.minZ(), box.maxX(), box.maxZ(), Math.abs(row - wanted));
                    return;
                }
            }
        }
    }

    static List<Station> reachable(CityGround ground, CityPlan.Rail rail, List<CityPlan.Line> lines, int start, int[] profile, List<Station> stations) {
        Vec3i size = ContentCity.stationSpan();
        if (size == null) { return List.of(); }
        boolean alongX = rail.alongX();
        int bedHalf = ContentCity.railBedHalf(true);
        List<Station> kept = new ArrayList<>();
        for (Station station : stations) {
            int level = profile[Mth.clamp(station.heart() - start, 0, profile.length - 1)];
            int top = streetTop(ground, lines, alongX, rail.middle(), station);
            String why = ContentCityStampPiece.unreachable(size, level, top, station.row(), station.near(), station.way(), rail.middle(), alongX, bedHalf);
            if (why == null) {
                kept.add(station);
                continue;
            }
            ContentLog.LOGGER.info("The subway station claimed at {}, {} on the line at {} is not built, since {}", alongX ? station.row() - 1 : station.near() - 1, alongX ? station.near() - 1 : station.row() - 1, rail.middle(), why);
        }
        return kept;
    }

    private static int streetTop(CityGround ground, List<CityPlan.Line> lines, boolean alongX, int center, Station station) {
        CityPlan.Line line = streetAlong(lines, alongX, center, station.first(), station.last());
        return line == null ? Integer.MIN_VALUE : roadAt(ground, null, line, station.heart());
    }

    private static boolean crowded(List<Station> found, int row) {
        int least = ContentCity.stationLength() + ContentCityStairsPiece.RUN;
        for (Station station : found) {
            if (Math.abs(row - station.heart()) < least) { return true; }
        }
        return false;
    }

    @Nullable public static CityPlan.Line streetAlong(Collection<CityPlan.Line> lines, boolean alongX, int center, int from, int to) {
        for (CityPlan.Line line : lines) {
            if (line.alongX() != alongX || line.alley() || center < line.at() || center > line.last()) { continue; }
            if (line.from() <= from && line.to() >= to) { return line; }
        }
        return null;
    }

    public static boolean onRoadOrWell(List<CityPlan.Line> lines, List<BoundingBox> wells, BoundingBox box) {
        for (CityPlan.Line line : lines) {
            if (box.intersects(lineBox(line))) { return true; }
        }
        for (BoundingBox well : wells) {
            if (box.intersects(well)) { return true; }
        }
        return false;
    }

    public static boolean onPlaza(List<BoundingBox> wells, BoundingBox box) {
        int reach = CityPlan.plazaReach();
        for (BoundingBox well : wells) {
            if (box.maxX() < well.minX() - reach || box.minX() > well.maxX() + reach || box.maxZ() < well.minZ() - reach || box.minZ() > well.maxZ() + reach) { continue; }
            return true;
        }
        return false;
    }

    public static BoundingBox lineBox(CityPlan.Line line) { return line.alongX() ? new BoundingBox(line.from(), 0, line.at(), line.to(), 0, line.last()) : new BoundingBox(line.at(), 0, line.from(), line.last(), 0, line.to()); }

    static boolean roadOver(List<CityPlan.Line> lines, boolean alongX, int row, int center) {
        for (CityPlan.Line line : lines) {
            if (line.alongX() == alongX || row < line.at() || row > line.last() || center < line.from() || center > line.to()) { continue; }
            return true;
        }
        return false;
    }

    static boolean uncrossed(CityGround ground, List<CityPlan.Line> lines, boolean alongX, int row, int center, int level) {
        for (CityPlan.Line line : lines) {
            if (line.alongX() == alongX || row < line.at() - 1 || row > line.last() + 1 || center < line.from() || center > line.to()) { continue; }
            return roadAt(ground, null, line, center) >= level + OVER;
        }
        return true;
    }

    static int roadAt(CityGround ground, @Nullable CityPlan plan, CityPlan.Line line, int along) {
        CityPlan owner = plan != null ? plan : CityPlan.of(ground, CityPlan.districtOf(line.alongX() ? along : line.at(), true), CityPlan.districtOf(line.alongX() ? line.at() : along, false));
        int start = owner == null ? line.from() : owner.spanStart(line);
        int[] profile = new int[owner == null ? line.to() - line.from() + 1 : owner.spanLength(line)];
        for (int at = 0; at < profile.length; at++) { profile[at] = alongLine(ground, line, start + at); }
        CityGrade.flatRuns(profile, start, row -> flatAt(ground, line, row, CityPlan.flatRun()));
        CityGrade.smoothRoad(profile);
        if (owner != null) { owner.apron(ground, line, start, profile); }
        return profile[Mth.clamp(along - start, 0, profile.length - 1)];
    }

    static int flatAt(CityGround ground, CityPlan.Line line, int row, int run) {
        if (run <= 1 || CityPlan.underwater(ground, line, row)) { return Integer.MIN_VALUE; }
        int grid = row - Math.floorMod(row, run);
        int center = line.at() + (line.width() - 1) / 2;
        int[] taken = new int[3];
        int count = 0;
        for (int across = center - 1; across <= center + 1; across++) {
            int found = line.alongX() ? ground.floor(grid, across) : ground.floor(across, grid);
            if (found < ground.sea() - 1) { continue; }
            taken[count++] = found;
        }
        if (count == 0) { return Integer.MIN_VALUE; }
        Arrays.sort(taken, 0, count);
        return taken[count / 2];
    }

    static int alongLine(CityGround ground, CityPlan.Line line, int row) {
        int[] taken = new int[line.width()];
        for (int at = 0; at < line.width(); at++) {
            int across = line.at() + at;
            taken[at] = line.alongX() ? ground.surface(row, across) : ground.surface(across, row);
        }
        Arrays.sort(taken);
        return taken[taken.length / 2];
    }

    public static List<Laid> subways(CityGround ground, int leastX, int leastZ, int mostX, int mostZ) { return ContentCity.subways() ? near(ground, leastX, leastZ, mostX, mostZ, true) : new ArrayList<>(); }

    public static List<Laid> railways(CityGround ground, int leastX, int leastZ, int mostX, int mostZ) { return near(ground, leastX, leastZ, mostX, mostZ, false); }

    private static List<Laid> near(CityGround ground, int leastX, int leastZ, int mostX, int mostZ, boolean subwaysOnly) {
        List<Laid> found = new ArrayList<>();
        if (ContentCity.idle()) { return found; }
        Set<CityPlan.Rail> seen = new LinkedHashSet<>();
        for (int districtX = CityPlan.districtOf(leastX, true) - 1; districtX <= CityPlan.districtOf(mostX, true) + 1; districtX++) {
            for (int districtZ = CityPlan.districtOf(leastZ, false) - 1; districtZ <= CityPlan.districtOf(mostZ, false) + 1; districtZ++) {
                CityPlan plan = CityPlan.of(ground, districtX, districtZ);
                if (plan == null) { continue; }
                for (CityPlan.Rail rail : plan.rails()) {
                    if ((subwaysOnly && !rail.subway()) || !seen.add(rail)) { continue; }
                    int half = halfOf(rail);
                    int fromX = rail.alongX() ? rail.from() : rail.middle() - half;
                    int toX = rail.alongX() ? rail.to() : rail.middle() + half;
                    int fromZ = rail.alongX() ? rail.middle() - half : rail.from();
                    int toZ = rail.alongX() ? rail.middle() + half : rail.to();
                    if (toX < leastX || fromX > mostX || toZ < leastZ || fromZ > mostZ) { continue; }
                    Laid laid = laid(ground, rail);
                    if (laid != null) { found.add(laid); }
                }
            }
        }
        return found;
    }

    private static int boreLevel(Laid laid, int x, int z) {
        CityPlan.Rail rail = laid.rail();
        int along = rail.alongX() ? x : z;
        int across = rail.alongX() ? z : x;
        if (along < rail.from() || along > rail.to() || Math.abs(across - rail.middle()) > (CityPlan.railWidth(rail.subway()) - 1) / 2 + 1) { return Integer.MIN_VALUE; }
        return laid.level(along);
    }

    public static boolean insideBore(List<Laid> subways, int x, int y, int z) {
        for (Laid laid : subways) {
            int level = boreLevel(laid, x, z);
            if (level != Integer.MIN_VALUE && y >= level - 1 && y <= level + CLEAR + 1) { return true; }
        }
        return false;
    }

    public static boolean runsInto(List<Laid> lines, int x, int y, int z) {
        for (Laid laid : lines) {
            int level = boreLevel(laid, x, z);
            if (level == Integer.MIN_VALUE || y < level - 1) { continue; }
            if (!laid.rail().subway() || y <= level + CLEAR + 1) { return true; }
        }
        return false;
    }

    public static boolean underBed(List<Laid> lines, int x, int y, int z) {
        for (Laid laid : lines) {
            int level = boreLevel(laid, x, z);
            if (y <= level && y >= level - CityPlotGround.FILL_UNDER) { return true; }
        }
        return false;
    }

    public static int boreRoof(List<Laid> subways, int x, int z) {
        int roof = Integer.MIN_VALUE;
        for (Laid laid : subways) {
            int level = boreLevel(laid, x, z);
            if (level != Integer.MIN_VALUE) { roof = Math.max(roof, level + CLEAR + 1); }
        }
        return roof;
    }

    public static boolean givesWay(Collection<Laid> tracks, CityPlan.Plot plot, BoundingBox met, int floor) {
        for (Laid laid : tracks) {
            CityPlan.Rail rail = laid.rail();
            if (!rail.subway() || CityLinks.trunkData(rail) != null) { continue; }
            int roof = CityRailsFit.roofUnder(laid, met);
            if (roof == Integer.MIN_VALUE) { continue; }
            if (floor - COURSES > roof) {
                ContentLog.LOGGER.debug("Village plot {} at {}, {}, {} to {}, {}, {} stands over the subway line at {} and is kept: it would rest at y {} and the bore roof under it lies at y {}, so the bore clears its {} foundation course(s)", plot.def().key(), met.minX(), met.minY(), met.minZ(), met.maxX(), met.maxY(), met.maxZ(), rail.middle(), floor, roof, COURSES);
                continue;
            }
            ContentLog.LOGGER.debug("Village plot {} at {}, {}, {} to {}, {}, {} makes way for the subway line at {}: it would rest at y {}, and its {} foundation course(s) reach the bore roof at y {} under it", plot.def().key(), met.minX(), met.minY(), met.minZ(), met.maxX(), met.maxY(), met.maxZ(), rail.middle(), floor, COURSES, roof);
            return true;
        }
        return false;
    }
}
