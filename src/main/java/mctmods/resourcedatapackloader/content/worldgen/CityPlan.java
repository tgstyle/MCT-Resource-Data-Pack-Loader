package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.content.def.CityMapDef;
import mctmods.resourcedatapackloader.content.def.PickDef;
import mctmods.resourcedatapackloader.content.def.VillageDef;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Hashes;

import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.util.RandomSource;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Set;
import java.util.TreeMap;
import javax.annotation.Nullable;

public final class CityPlan {
    private static final int DISTRICT_LEAST = 96;
    public static final int WELL_HALF = 3;
    public static final int ALLEY_LEAST = 16;
    public static final int ALLEY_APART = 12;
    private static int district = DISTRICT_LEAST;
    private static final Map<Long, Optional<CityPlan>> PLANS = new ConcurrentHashMap<>();
    private static final Map<Long, City> CITIES = new ConcurrentHashMap<>();
    public static final int ALLEY_WIDTH = 3;
    private static final int VANILLA_WIDTH = 3;
    private static final int DEFAULT_BLOCK = 32;
    private static final int LEAST_BLOCK = 8;
    private static final long SALT = 0x1F0D3F5B7C9E1A37L;
    private static final int GAP = 1;
    private static final int TAIL_MOST = 48;
    private static final int DEAD_END_ODDS = 3;
    private static final Set<String> WARNED = new LinkedHashSet<>();
    private static final Rotation[] TURNS = {Rotation.NONE, Rotation.CLOCKWISE_90, Rotation.CLOCKWISE_180, Rotation.COUNTERCLOCKWISE_90};

    public record Line(int at, int width, boolean alley, boolean alongX, int from, int to, boolean endsLow, boolean endsHigh) {
        public int last() { return at + width - 1; }

        public int middle() { return at + width / 2; }

        public boolean covers(int along) { return along >= from && along <= to; }

        public int rank() { return (alley ? 500 : 1000) + width; }

        public boolean beats(Line other) { return rank() != other.rank() ? rank() > other.rank() : alongX; }
    }

    public record Junction(Line alongX, Line alongZ) {
        public int fromX() { return alongZ.at(); }

        public int toX() { return alongZ.last(); }

        public int fromZ() { return alongX.at(); }

        public int toZ() { return alongX.last(); }
    }

    public record Plot(int fromX, int fromZ, int toX, int toZ, Line street, boolean lower, VillageDef def) {
        public int middleAlong() { return street.alongX() ? (fromX + toX) / 2 : (fromZ + toZ) / 2; }
    }

    public record Rail(int at, int width, boolean alongX, boolean subway) {
        public int last() { return at + width - 1; }

        public int middle() { return at + width / 2; }
    }

    private final int originX;
    private final int originZ;
    private final int block;
    private final List<Line> alongX;
    private final List<Line> alongZ;
    private final List<Plot> plots;
    private final List<Rail> rails;
    private final int windowX;
    private final int windowZ;
    private final boolean mapped;
    @Nullable private final Line plazaRow;
    @Nullable private final Line plazaColumn;
    private final boolean[] edges;

    private CityPlan(int originX, int originZ, int block, List<Line> alongX, List<Line> alongZ, List<Plot> plots, List<Rail> rails, int windowX, int windowZ, boolean mapped, @Nullable Line plazaRow, @Nullable Line plazaColumn) {
        this(originX, originZ, block, alongX, alongZ, plots, rails, windowX, windowZ, mapped, plazaRow, plazaColumn, new boolean[] {mapped, mapped, mapped, mapped});
    }

    private CityPlan(int originX, int originZ, int block, List<Line> alongX, List<Line> alongZ, List<Plot> plots, List<Rail> rails, int windowX, int windowZ, boolean mapped, @Nullable Line plazaRow, @Nullable Line plazaColumn, boolean[] edges) {
        this.edges = edges;
        this.plazaRow = plazaRow;
        this.plazaColumn = plazaColumn;
        this.windowX = windowX;
        this.windowZ = windowZ;
        this.mapped = mapped;
        this.originX = originX;
        this.originZ = originZ;
        this.block = block;
        this.alongX = alongX;
        this.alongZ = alongZ;
        this.plots = plots;
        this.rails = rails;
    }

    public int originX() { return originX; }

    public CityPlan withEdges(boolean lowX, boolean highX, boolean lowZ, boolean highZ) {
        return new CityPlan(originX, originZ, block, alongX, alongZ, plots, rails, windowX, windowZ, mapped, plazaRow, plazaColumn, new boolean[] {lowX, highX, lowZ, highZ});
    }

    public int tail(Rail rail, boolean low) {
        boolean edge = rail.alongX() ? edges[low ? 0 : 1] : edges[low ? 2 : 3];
        return edge ? railTail(rail.subway()) : 0;
    }

    public int originZ() { return originZ; }

    public int block() { return block; }

    public int windowX() { return windowX; }

    public int windowZ() { return windowZ; }

    public int spanStart(Line line) {
        if (!mapped) { return line.alongX() ? originX : originZ; }
        return line.from();
    }

    public int spanLength(Line line) {
        if (!mapped) { return district(); }
        return line.to() - line.from() + 1;
    }

    public boolean emits(int x, int z) {
        if (!mapped) { return true; }
        return x >= windowX && x < windowX + district() && z >= windowZ && z < windowZ + district();
    }

    public boolean emitsAlong(Line line, int along) {
        if (!mapped) { return true; }
        return line.alongX() ? emits(along, line.middle()) : emits(line.middle(), along);
    }

    public List<Line> alongX() { return alongX; }

    public List<Line> alongZ() { return alongZ; }

    public List<Line> crossing(Line line) { return line.alongX() ? alongZ : alongX; }

    public Line plazaRow() { return plazaRow != null ? plazaRow : alongX.get(alongX.size() / 2); }

    public Line plazaColumn() { return plazaColumn != null ? plazaColumn : alongZ.get(alongZ.size() / 2); }

    public boolean plazaAt(Line line) { return line.equals(plazaRow()) || line.equals(plazaColumn()); }

    public List<Junction> junctions() {
        List<Junction> found = new ArrayList<>();
        for (Line row : alongX) {
            for (Line column : alongZ) { found.add(new Junction(row, column)); }
        }
        return found;
    }

    @Nullable public static CityPlan of(long seed, int districtX, int districtZ) {
        CityMapDef map = ContentCity.layout();
        if (map != null) { return mapped(seed, districtX, districtZ, map); }
        int spacing = spacing();
        if (spacing <= 0) { return null; }
        if (spacing == 1) { return plan(seed, districtX, districtZ, null); }
        City city = cityOf(seed, districtX, districtZ, spacing);
        if (city == null) { return null; }
        CityPlan plan = plan(seed, districtX, districtZ, new int[] {city.centerX(), city.centerZ()});
        return plan == null ? null : plan.withEdges(!city.has(districtX - 1, districtZ), !city.has(districtX + 1, districtZ), !city.has(districtX, districtZ - 1), !city.has(districtX, districtZ + 1));
    }

    @Nullable private static City cityOf(long seed, int districtX, int districtZ, int spacing) {
        int regionX = Math.floorDiv(districtX, spacing);
        int regionZ = Math.floorDiv(districtZ, spacing);
        for (int aroundX = -1; aroundX <= 1; aroundX++) {
            for (int aroundZ = -1; aroundZ <= 1; aroundZ++) {
                int[] center = center(seed, (regionX + aroundX) * spacing, (regionZ + aroundZ) * spacing);
                City city = CITIES.computeIfAbsent(key(seed, center[0], center[1]), key -> grow(seed, center[0], center[1], spacing));
                if (city.has(districtX, districtZ)) { return city; }
            }
        }
        return null;
    }

    private record City(int centerX, int centerZ, Set<Long> districts) {
        boolean has(int districtX, int districtZ) { return districts.contains(((long) districtX << 32) ^ (districtZ & 0xFFFFFFFFL)); }
    }

    private static City grow(long seed, int centerX, int centerZ, int spacing) {
        int least = Math.max(0, ContentControl.number(ContentControl.VILLAGES, "villagePlotsLeast", Config.worldgen.villagePlotsLeast()));
        int most = plotsMost();
        int reach = (spacing / 2 - 1) / 2;
        Set<Long> held = new LinkedHashSet<>();
        int total = 0;
        int districts = 0;
        rings:
        for (int ring = 0; ring <= reach; ring++) {
            for (int[] cell : ring(centerX, centerZ, ring)) {
                if (ring > 0 && least == 0) { break rings; }
                CityPlan plan = plan(seed, cell[0], cell[1], new int[] {centerX, centerZ});
                int plots = plan == null ? 0 : plan.plots().size();
                if (most > 0 && total + plots > most) { break rings; }
                held.add(((long) cell[0] << 32) ^ (cell[1] & 0xFFFFFFFFL));
                total += plots;
                districts++;
                if (least > 0 && total >= least) { break rings; }
            }
        }
        ContentLog.LOGGER.info("The city around district {}, {} grows {} district(s) to {} plot(s) against the asked minimum of {}{}", centerX, centerZ, districts, total, least, most > 0 ? " and the ceiling of " + most : "");
        return new City(centerX, centerZ, held);
    }

    private static List<int[]> ring(int centerX, int centerZ, int ring) {
        List<int[]> cells = new ArrayList<>();
        if (ring == 0) {
            cells.add(new int[] {centerX, centerZ});
            return cells;
        }
        for (int x = centerX - ring; x <= centerX + ring; x++) { cells.add(new int[] {x, centerZ - ring}); }
        for (int z = centerZ - ring + 1; z <= centerZ + ring; z++) { cells.add(new int[] {centerX + ring, z}); }
        for (int x = centerX + ring - 1; x >= centerX - ring; x--) { cells.add(new int[] {x, centerZ + ring}); }
        for (int z = centerZ + ring - 1; z > centerZ - ring; z--) { cells.add(new int[] {centerX - ring, z}); }
        return cells;
    }

    private static int[] center(long seed, int districtX, int districtZ) {
        int spacing = spacing();
        int regionX = Math.floorDiv(districtX, spacing);
        int regionZ = Math.floorDiv(districtZ, spacing);
        for (String entry : ContentControl.list(ContentControl.STRUCTURES, "structureAt", Config.worldgen.structureAt())) {
            String[] parts = entry.split("=", 2);
            if (parts.length != 2 || !parts[0].trim().equalsIgnoreCase(ContentCity.STRUCTURE)) { continue; }
            String[] xz = parts[1].split(",");
            if (xz.length != 2) { continue; }
            try {
                int pinnedX = Math.floorDiv(Integer.parseInt(xz[0].trim()), district());
                int pinnedZ = Math.floorDiv(Integer.parseInt(xz[1].trim()), district());
                if (Math.floorDiv(pinnedX, spacing) == regionX && Math.floorDiv(pinnedZ, spacing) == regionZ) { return new int[] {pinnedX, pinnedZ}; }
            }
            catch (NumberFormatException notNumbers) { ContentLog.LOGGER.debug("structureAt entry '{}' is not city=x,z, so it does not seat a city", entry); }
        }
        long mixed = Hashes.mix(seed ^ SALT, regionX, 0, regionZ);
        int inner = Math.max(1, spacing / 2);
        return new int[] {regionX * spacing + spacing / 4 + Math.floorMod(mixed, inner), regionZ * spacing + spacing / 4 + Math.floorMod(mixed >> 21, inner)};
    }

    private static long key(long seed, int districtX, int districtZ) { return seed ^ (districtX * 341873128712L) ^ (districtZ * 132897987541L); }

    @Nullable private static CityPlan plan(long seed, int districtX, int districtZ, @Nullable int[] city) {
        long held = key(seed, districtX, districtZ) * 31 + (city == null ? 0 : key(0, city[0], city[1]));
        return PLANS.computeIfAbsent(held, key -> build(seed, districtX, districtZ, city)).orElse(null);
    }

    private static Optional<CityPlan> build(long seed, int districtX, int districtZ, @Nullable int[] city) {
        RandomSource roll = RandomSource.create(seed ^ SALT ^ (districtX * 341873128712L + districtZ * 132897987541L));
        int size = district();
        int full = fullWidth();
        int half = full / 2;
        int originX = districtX * size;
        int originZ = districtZ * size;
        int cross = crossAt();
        int centerX = originX + cross + half;
        int centerZ = originZ + cross + half;
        Line row = new Line(centerZ - half, full, false, true, originX, originX + size - 1, false, false);
        Line column = new Line(centerX - half, full, false, false, originZ, originZ + size - 1, false, false);
        List<Line> alongX = new ArrayList<>(List.of(row));
        List<Line> alongZ = new ArrayList<>(List.of(column));
        int keep = plazaReach() + WELL_HALF;
        List<Line> rows = new ArrayList<>(alongX);
        rows.add(new Line(centerZ - keep, 2 * keep + 1, false, true, originX, originX + size - 1, false, false));
        List<Line> columns = new ArrayList<>(alongZ);
        columns.add(new Line(centerX - keep, 2 * keep + 1, false, false, originZ, originZ + size - 1, false, false));
        RandomSource lines = RandomSource.create(seed ^ SALT ^ 0x5EED5L ^ (city == null ? 0 : city[0] * 341873128712L + city[1] * 132897987541L));
        List<Rail> rails = new ArrayList<>(rails(lines, originX, originZ, rows, columns, false, city));
        rails.addAll(rails(lines, originX, originZ, rows, columns, true, city));
        if (city == null) {
            int under = 0;
            for (int at = 0; at < rails.size(); at++) {
                Rail rail = rails.get(at);
                if (rail.subway() && under++ == 0) { rails.set(at, new Rail((rail.alongX() ? row : column).middle() - rail.width() / 2, rail.width(), rail.alongX(), true)); }
            }
        }
        alleys(roll, originX, originZ, size, row, column, alongX, alongZ, rails);
        List<Plot> plots = radialPlots(roll, originX, originZ, size, row, column, alongX, alongZ, rails);
        int alleyCount = alongX.size() + alongZ.size() - 2;
        ContentLog.LOGGER.debug("The district at {}, {} has its plaza at {}, {}, {} alley(s) and {} line(s)", originX, originZ, centerX, centerZ, alleyCount, rails.size());
        return Optional.of(new CityPlan(originX, originZ, DEFAULT_BLOCK, List.copyOf(alongX), List.copyOf(alongZ), plots, List.copyOf(rails), originX, originZ, false, row, column));
    }

    private static void alleys(RandomSource roll, int originX, int originZ, int size, Line row, Line column, List<Line> alongX, List<Line> alongZ, List<Rail> rails) {
        int chance = Mth.clamp(ContentControl.number(ContentControl.VILLAGES, "villagePathAlleyChance", Config.worldgen.villagePathAlleyChance()), 0, 100);
        int least = Math.max(0, ContentControl.number(ContentControl.VILLAGES, "villagePathMinimumWidth", Config.worldgen.villagePathMinimumWidth()));
        if (chance <= 0 || ALLEY_WIDTH < least) { return; }
        int clear = plazaReach() + WELL_HALF + ALLEY_WIDTH;
        int centerX = column.middle();
        int centerZ = row.middle();
        for (int side = -1; side <= 1; side += 2) {
            for (int end = -1; end <= 1; end += 2) {
                if (roll.nextInt(100) < chance) {
                    int lowX = end < 0 ? originX + ALLEY_APART : centerX + clear;
                    int highX = end < 0 ? centerX - clear - ALLEY_WIDTH : originX + size - ALLEY_APART - ALLEY_WIDTH;
                    int depth = side < 0 ? row.at() - originZ : originZ + size - 1 - row.last();
                    Line alley = branch(roll, lowX, highX, depth, side < 0 ? row.at() - 1 : row.last() + 1, side, false);
                    if (alley != null && clear(alongZ, alley) && offRail(rails, alley)) {
                        alongZ.add(alley);
                        ContentLog.LOGGER.debug("An alley off the row street at x {} to {} runs z {} to {}", alley.at(), alley.last(), alley.from(), alley.to());
                    }
                }
                if (roll.nextInt(100) < chance) {
                    int lowZ = end < 0 ? originZ + ALLEY_APART : centerZ + clear;
                    int highZ = end < 0 ? centerZ - clear - ALLEY_WIDTH : originZ + size - ALLEY_APART - ALLEY_WIDTH;
                    int depth = side < 0 ? column.at() - originX : originX + size - 1 - column.last();
                    Line alley = branch(roll, lowZ, highZ, depth, side < 0 ? column.at() - 1 : column.last() + 1, side, true);
                    if (alley != null && clear(alongX, alley) && offRail(rails, alley)) {
                        alongX.add(alley);
                        ContentLog.LOGGER.debug("An alley off the column street at z {} to {} runs x {} to {}", alley.at(), alley.last(), alley.from(), alley.to());
                    }
                }
            }
        }
    }

    private static boolean offRail(List<Rail> rails, Line alley) {
        for (Rail rail : rails) {
            if (rail.subway() || rail.alongX() != alley.alongX()) { continue; }
            if (alley.last() >= rail.at() - GAP && alley.at() <= rail.last() + GAP) { return false; }
        }
        return true;
    }

    @Nullable private static Line branch(RandomSource roll, int low, int high, int depth, int edge, int side, boolean alongX) {
        if (high < low || depth - ALLEY_APART < ALLEY_LEAST) { return null; }
        int at = low + roll.nextInt(high - low + 1);
        int length = ALLEY_LEAST + roll.nextInt(depth - ALLEY_APART - ALLEY_LEAST + 1);
        int from = side < 0 ? edge - length + 1 : edge;
        int to = side < 0 ? edge : edge + length - 1;
        return new Line(at, ALLEY_WIDTH, true, alongX, from, to, side < 0, side > 0);
    }

    private static List<Line> lines(RandomSource roll, int origin, int across, int block, int full, boolean alongX) {
        int chance = Mth.clamp(ContentControl.number(ContentControl.VILLAGES, "villagePathAlleyChance", Config.worldgen.villagePathAlleyChance()), 0, 100);
        int least = Math.max(0, ContentControl.number(ContentControl.VILLAGES, "villagePathMinimumWidth", Config.worldgen.villagePathMinimumWidth()));
        int stub = Math.max(ALLEY_WIDTH, block / 3);
        List<Line> found = new ArrayList<>();
        int at = 0;
        while (at + ALLEY_WIDTH <= district()) {
            boolean alley = chance > 0 && roll.nextInt(100) < chance;
            int width = alley ? ALLEY_WIDTH : full;
            if (at + width > district()) { break; }
            if (width >= least) {
                boolean endsLow = !found.isEmpty() && roll.nextInt(DEAD_END_ODDS) == 0;
                boolean endsHigh = !found.isEmpty() && roll.nextInt(DEAD_END_ODDS) == 0;
                int from = across + (endsLow ? Math.min(district() / 2, block + stub) : 0);
                int to = across + district() - 1 - (endsHigh ? Math.min(district() / 2, block + stub) : 0);
                if (to - from + 1 >= ALLEY_WIDTH) { found.add(new Line(origin + at, width, alley, alongX, from, to, endsLow, endsHigh)); }
            }
            at += width + block;
        }
        return List.copyOf(found);
    }

    public List<Plot> plots() { return plots; }

    private static void plots(RandomSource roll, List<Line> alongX, List<Line> alongZ, List<Rail> rails, List<Plot> found) {
        List<VillageDef> choices = ContentVillages.allowed();
        if (choices.isEmpty()) { return; }
        int most = plotsMost();
        boolean backRow = backRow();
        List<Line> lines = new ArrayList<>(alongX);
        lines.addAll(alongZ);
        int before = found.size();
        int backs = 0;
        for (int row = 0; row + 1 < alongX.size(); row++) {
            for (int column = 0; column + 1 < alongZ.size(); column++) {
                int fromX = alongZ.get(column).last() + 1;
                int toX = alongZ.get(column + 1).at() - 1;
                int fromZ = alongX.get(row).last() + 1;
                int toZ = alongX.get(row + 1).at() - 1;
                if (toX < fromX || toZ < fromZ) { continue; }
                int fronts = found.size();
                List<Run> runs = List.of(new Run(alongX.get(row), true, fromX, toX, fromZ, toZ), new Run(alongX.get(row + 1), false, fromX, toX, fromZ, toZ),
                        new Run(alongZ.get(column), true, fromX, toX, fromZ, toZ), new Run(alongZ.get(column + 1), false, fromX, toX, fromZ, toZ));
                seat(roll, choices, found, rails, lines, runs, (toX - fromX + 1) * (toZ - fromZ + 1), most);
                if (backRow) { backs += backRows(roll, choices, found, rails, lines, fronts, fromX, toX, fromZ, toZ, most); }
            }
        }
        ContentLog.LOGGER.debug("A city plan seats {} plot(s) along its streets, {} of them behind a plot that fronts one", found.size() - before, backs);
    }

    private static int plotsMost() { return Math.max(0, ContentControl.number(ContentControl.VILLAGES, "villagePlotsMost", Config.worldgen.villagePlotsMost())); }

    private static boolean backRow() { return ContentControl.flag(ContentControl.VILLAGES, "villagePlotsBackRow", Config.worldgen.villagePlotsBackRow()); }

    private static List<Plot> radialPlots(RandomSource roll, int originX, int originZ, int size, Line row, Line column, List<Line> alongX, List<Line> alongZ, List<Rail> rails) {
        List<Plot> found = new ArrayList<>();
        List<VillageDef> choices = ContentVillages.allowed();
        if (choices.isEmpty()) { return found; }
        int most = plotsMost();
        boolean backRow = backRow();
        List<Line> lines = new ArrayList<>(alongX);
        lines.addAll(alongZ);
        int keep = plazaReach() + WELL_HALF;
        int centerX = column.middle();
        int centerZ = row.middle();
        int backs = 0;
        for (int qx = -1; qx <= 1; qx += 2) {
            for (int qz = -1; qz <= 1; qz += 2) {
                int fromX = qx < 0 ? originX : column.last() + 1;
                int toX = qx < 0 ? column.at() - 1 : originX + size - 1;
                int fromZ = qz < 0 ? originZ : row.last() + 1;
                int toZ = qz < 0 ? row.at() - 1 : originZ + size - 1;
                int before = found.size();
                List<Run> runs = new ArrayList<>();
                int rowFromX = qx < 0 ? fromX : Math.max(fromX, centerX + keep + 1);
                int rowToX = qx < 0 ? Math.min(toX, centerX - keep - 1) : toX;
                if (rowToX >= rowFromX) { runs.add(new Run(row, qz > 0, rowFromX, rowToX, fromZ, toZ)); }
                int columnFromZ = qz < 0 ? fromZ : Math.max(fromZ, centerZ + keep + 1);
                int columnToZ = qz < 0 ? Math.min(toZ, centerZ - keep - 1) : toZ;
                if (columnToZ >= columnFromZ) { runs.add(new Run(column, qx > 0, fromX, toX, columnFromZ, columnToZ)); }
                for (Line alley : alongZ) {
                    if (!alley.alley() || alley.at() < fromX || alley.last() > toX || alley.from() < fromZ || alley.to() > toZ) { continue; }
                    runs.add(new Run(alley, false, fromX, alley.at() - 1, alley.from(), alley.to()));
                    runs.add(new Run(alley, true, alley.last() + 1, toX, alley.from(), alley.to()));
                }
                for (Line alley : alongX) {
                    if (!alley.alley() || alley.at() < fromZ || alley.last() > toZ || alley.from() < fromX || alley.to() > toX) { continue; }
                    runs.add(new Run(alley, false, alley.from(), alley.to(), fromZ, alley.at() - 1));
                    runs.add(new Run(alley, true, alley.from(), alley.to(), alley.last() + 1, toZ));
                }
                seat(roll, choices, found, rails, lines, runs, (toX - fromX + 1) * (toZ - fromZ + 1), most);
                if (backRow) { backs += backRows(roll, choices, found, rails, lines, before, fromX, toX, fromZ, toZ, most); }
            }
        }
        Map<String, Integer> sizes = new TreeMap<>();
        for (Plot plot : found) { sizes.merge(plot.def().width() + "x" + plot.def().depth(), 1, Integer::sum); }
        ContentLog.LOGGER.debug("The district at {}, {} seats {} plot(s) along its streets and alleys, {} of them behind a plot that fronts one, by size {}", originX, originZ, found.size(), backs, sizes);
        return List.copyOf(found);
    }

    private static int backRows(RandomSource roll, List<VillageDef> choices, List<Plot> found, List<Rail> rails, List<Line> lines, int from, int fromX, int toX, int fromZ, int toZ, int most) {
        int fronts = found.size();
        int seated = 0;
        int noRoom = 0;
        int[] refused = new int[3];
        for (int at = from; at < fronts; at++) {
            if (most > 0 && found.size() >= most) { break; }
            Plot front = found.get(at);
            boolean alongX = front.street().alongX();
            int wide = alongX ? front.toX() - front.fromX() + 1 : front.toZ() - front.fromZ() + 1;
            int edge = front.lower() ? (alongX ? front.toZ() : front.toX()) + 1 : (alongX ? front.fromZ() : front.fromX()) - 1;
            int room = front.lower() ? (alongX ? toZ : toX) - edge + 1 : edge - (alongX ? fromZ : fromX) + 1;
            List<VillageDef> left = new ArrayList<>(choices);
            Plot back = null;
            boolean any = false;
            while (back == null) {
                VillageDef def = room <= 0 ? null : ContentVillages.pick(left, roll, wide, room);
                if (def == null) { break; }
                any = true;
                int least = (alongX ? front.fromX() : front.fromZ()) + (wide - def.width()) / 2;
                int near = front.lower() ? edge : edge - def.depth() + 1;
                int over = near + def.depth() - 1;
                Plot plot = alongX ? new Plot(least, near, least + def.width() - 1, over, front.street(), front.lower(), def)
                                   : new Plot(near, least, over, least + def.width() - 1, front.street(), front.lower(), def);
                int why = refusal(found, rails, lines, plot);
                if (why < 0) { back = plot; }
                else {
                    refused[why]++;
                    drop(left, def);
                }
            }
            if (back != null) {
                found.add(back);
                seated++;
            }
            else if (!any) { noRoom++; }
        }
        if (fronts > from) { ContentLog.LOGGER.debug("Behind {} front plot(s): {} seated, {} with no room behind, tries refused {} on a plot, {} on a railway, {} on a street", fronts - from, seated, noRoom, refused[0], refused[1], refused[2]); }
        return seated;
    }

    private record Run(Line street, boolean lower, int fromX, int toX, int fromZ, int toZ) {}

    private static void seat(RandomSource roll, List<VillageDef> choices, List<Plot> found, List<Rail> rails, List<Line> lines, List<Run> runs, int area, int most) {
        List<VillageDef> wanted = new ArrayList<>();
        int rolled = 0;
        while (rolled < area) {
            VillageDef def = ContentVillages.pick(choices, roll, Integer.MAX_VALUE, Integer.MAX_VALUE);
            if (def == null) { break; }
            wanted.add(def);
            rolled += Math.max(1, def.width() * def.depth());
        }
        wanted.sort(Comparator.comparingInt((VillageDef def) -> def.width() * def.depth()).reversed());
        int rolledCount = wanted.size();
        int bagged = 0;
        for (VillageDef def : wanted) {
            if (most > 0 && found.size() >= most) { break; }
            Plot plot = firstFit(def, runs, found, rails, lines);
            if (plot == null) { continue; }
            found.add(plot);
            bagged++;
        }
        int before = found.size();
        for (Run run : runs) { frontage(roll, choices, found, rails, lines, run, most); }
        ContentLog.LOGGER.debug("A block of {} square block(s) rolled {} plot(s) and seated {} of them, and the fill after seated {} more", area, rolledCount, bagged, found.size() - before);
    }

    @Nullable private static Plot firstFit(VillageDef def, List<Run> runs, List<Plot> found, List<Rail> rails, List<Line> lines) {
        for (Run run : runs) {
            boolean alongX = run.street().alongX();
            int least = alongX ? run.fromX() : run.fromZ();
            int last = alongX ? run.toX() : run.toZ();
            int deep = alongX ? run.toZ() - run.fromZ() + 1 : run.toX() - run.fromX() + 1;
            if (def.depth() + def.apron() > deep) { continue; }
            for (int at = least; at + def.width() - 1 <= last; at++) {
                Plot plot = front(run.street(), run.lower(), run.fromX(), run.toX(), run.fromZ(), run.toZ(), at, def);
                if (refusal(found, rails, lines, plot) < 0) { return plot; }
            }
        }
        return null;
    }

    private static void frontage(RandomSource roll, List<VillageDef> choices, List<Plot> found, List<Rail> rails, List<Line> lines, Run run, int most) {
        Line street = run.street();
        boolean lower = run.lower();
        int fromX = run.fromX();
        int toX = run.toX();
        int fromZ = run.fromZ();
        int toZ = run.toZ();
        boolean alongX = street.alongX();
        int least = alongX ? fromX : fromZ;
        int last = alongX ? toX : toZ;
        int deep = alongX ? toZ - fromZ + 1 : toX - fromX + 1;
        if (last < least || deep <= 0) { return; }
        int at = least;
        int seated = 0;
        int stepped = 0;
        int[] refused = new int[3];
        while (at <= last) {
            if (most > 0 && found.size() >= most) { break; }
            List<VillageDef> left = new ArrayList<>(choices);
            Plot plot = null;
            while (plot == null) {
                VillageDef def = ContentVillages.pick(left, roll, last - at + 1, deep);
                if (def == null) { break; }
                Plot tried = front(street, lower, fromX, toX, fromZ, toZ, at, def);
                int why = refusal(found, rails, lines, tried);
                if (why < 0) { plot = tried; }
                else {
                    refused[why]++;
                    drop(left, def);
                }
            }
            if (plot == null) {
                at++;
                stepped++;
                continue;
            }
            found.add(plot);
            seated++;
            at += plot.def().width() + GAP;
        }
        ContentLog.LOGGER.debug("Along the {} at {}: {} front plot(s) seated to fill, {} block(s) stepped over, tries refused {} on a plot, {} on a railway, {} on a street", street.alley() ? "alley" : "street", street.at(), seated, stepped, refused[0], refused[1], refused[2]);
    }

    private static Plot front(Line street, boolean lower, int fromX, int toX, int fromZ, int toZ, int at, VillageDef def) {
        boolean alongX = street.alongX();
        int near = lower ? (alongX ? fromZ : fromX) + def.apron() : (alongX ? toZ : toX) - def.apron() - def.depth() + 1;
        int over = near + def.depth() - 1;
        return alongX ? new Plot(at, near, at + def.width() - 1, over, street, lower, def) : new Plot(near, at, over, at + def.width() - 1, street, lower, def);
    }

    private static int refusal(List<Plot> found, List<Rail> rails, List<Line> lines, Plot plot) {
        if (!fits(found, plot)) { return 0; }
        if (!offRail(rails, plot)) { return 1; }
        return onLine(lines, plot) ? 2 : -1;
    }

    private static void drop(List<VillageDef> left, VillageDef failed) { left.removeIf(def -> def.width() >= failed.width() && def.depth() >= failed.depth()); }

    private static boolean offRail(List<Rail> rails, Plot plot) {
        for (Rail rail : rails) {
            if (rail.subway()) { continue; }
            if (rail.alongX() ? plot.fromZ() <= rail.last() && plot.toZ() >= rail.at() : plot.fromX() <= rail.last() && plot.toX() >= rail.at()) { return false; }
        }
        return true;
    }

    private static boolean onLine(List<Line> lines, Plot plot) {
        for (Line line : lines) {
            boolean across = line.alongX() ? plot.fromZ() <= line.last() + GAP && plot.toZ() >= line.at() - GAP : plot.fromX() <= line.last() + GAP && plot.toX() >= line.at() - GAP;
            boolean along = line.alongX() ? plot.fromX() <= line.to() + GAP && plot.toX() >= line.from() - GAP : plot.fromZ() <= line.to() + GAP && plot.toZ() >= line.from() - GAP;
            if (across && along) { return true; }
        }
        return false;
    }

    private static boolean fits(List<Plot> found, Plot plot) {
        for (Plot held : found) {
            if (plot.fromX() <= held.toX() && plot.toX() >= held.fromX() && plot.fromZ() <= held.toZ() && plot.toZ() >= held.fromZ()) { return false; }
        }
        return true;
    }

    public List<Rail> rails() { return rails; }

    public static int railTail(boolean sub) { return Math.clamp(ContentControl.number(ContentControl.VILLAGES, sub ? "villageSubwayTail" : "villageRailTail", sub ? Config.worldgen.villageSubwayTail() : Config.worldgen.villageRailTail()), 0, TAIL_MOST); }


    public static int railWidth(boolean sub) {
        int bed = Math.max(3, ContentControl.number(ContentControl.VILLAGES, sub ? "villageSubwayWidth" : "villageRailWidth", sub ? Config.worldgen.villageSubwayWidth() : Config.worldgen.villageRailWidth()));
        int tracks = Math.max(0, ContentControl.number(ContentControl.VILLAGES, sub ? "villageSubwayTracks" : "villageRailTracks", sub ? Config.worldgen.villageSubwayTracks() : Config.worldgen.villageRailTracks()));
        int gap = Math.max(2, ContentControl.number(ContentControl.VILLAGES, sub ? "villageSubwayTrackGap" : "villageRailTrackGap", sub ? Config.worldgen.villageSubwayTrackGap() : Config.worldgen.villageRailTrackGap()));
        if (tracks > 1) { bed = Math.max(bed, (tracks - 1) * gap + 3); }
        int shoulder = ContentCity.railShoulderBlock(sub).isEmpty() ? 0 : Math.max(0, ContentControl.number(ContentControl.VILLAGES, sub ? "villageSubwayShoulderWidth" : "villageRailShoulderWidth", sub ? Config.worldgen.villageSubwayShoulderWidth() : Config.worldgen.villageRailShoulderWidth()));
        return bed + shoulder * 2;
    }

    private static List<Rail> rails(RandomSource roll, int originX, int originZ, List<Line> alongX, List<Line> alongZ) {
        List<Rail> found = new ArrayList<>(rails(roll, originX, originZ, alongX, alongZ, false, null));
        found.addAll(rails(roll, originX, originZ, alongX, alongZ, true, null));
        return List.copyOf(found);
    }

    private static int crossAt() {
        int room = district() - fullWidth();
        int shallow = 0;
        for (VillageDef def : ContentVillages.allowed()) { shallow = Math.max(shallow, def.depth() + def.apron()); }
        return shallow > 0 && shallow <= room / 2 ? shallow : room / 2;
    }

    private static int blockBand(int width, int lines, int pitch) {
        int size = district();
        int keep = plazaReach() + WELL_HALF + GAP;
        int need = (lines - 1) * pitch + width;
        int center = crossAt() + fullWidth() / 2;
        if (size - GAP - need > center + keep) { return size - GAP - need; }
        if (GAP + need - 1 < center - keep) { return GAP; }
        return center + keep + 1;
    }

    private static int clearOf(List<Line> beside, int at, int width, int base) {
        int size = district();
        for (int away = 0; away < size; away++) {
            for (int side = -1; side <= 1; side += 2) {
                int seat = at + side * away;
                if (seat < base + GAP || seat + width - 1 > base + size - 1 - GAP) { continue; }
                if (blocked(beside, seat, width)) { continue; }
                return seat;
            }
        }
        return Integer.MIN_VALUE;
    }

    private static boolean blocked(List<Line> beside, int seat, int width) {
        for (Line line : beside) {
            if (seat + width - 1 >= line.at() - GAP && seat <= line.last() + GAP) { return true; }
        }
        return false;
    }

    private static List<Rail> rails(RandomSource roll, int originX, int originZ, List<Line> rows, List<Line> columns, boolean sub, @Nullable int[] city) {
        int lines = Math.max(0, ContentControl.number(ContentControl.VILLAGES, sub ? "villageSubwayLines" : "villageRailLines", sub ? Config.worldgen.villageSubwayLines() : Config.worldgen.villageRailLines()));
        if (lines == 0) { return List.of(); }
        String wanted = ContentControl.text(ContentControl.VILLAGES, sub ? "villageSubwayDirection" : "villageRailDirection", sub ? Config.worldgen.villageSubwayDirection() : Config.worldgen.villageRailDirection()).trim().toLowerCase(Locale.ROOT);
        boolean alongX = "ew".equals(wanted) || (!"ns".equals(wanted) && roll.nextBoolean());
        int width = railWidth(sub);
        int spacing = Math.max(1, ContentControl.number(ContentControl.VILLAGES, sub ? "villageSubwaySpacing" : "villageRailSpacing", sub ? Config.worldgen.villageSubwaySpacing() : Config.worldgen.villageRailSpacing()));
        int pitch = width + spacing;
        int room = district() - width;
        if (room < 0) { return List.of(); }
        int first = lines * pitch - spacing > district() ? 0 : roll.nextInt(Math.max(1, room - (lines - 1) * pitch + 1));
        if (!sub && rows != null) { first = blockBand(width, lines, pitch); }
        List<Rail> found = new ArrayList<>();
        List<Line> beside = alongX ? rows : columns;
        int base = alongX ? originZ : originX;
        int size = district();
        if (city != null && sub && rows != null) { first = crossAt() + fullWidth() / 2 - width / 2; }
        for (int line = 0; line < lines; line++) {
            int at;
            if (city == null) {
                at = base + first + line * pitch;
                if (first + line * pitch + width > size) { break; }
            }
            else {
                int apart = Math.max(1, Math.floorDiv(pitch + size - 1, size)) * size;
                at = (alongX ? city[1] : city[0]) * size + first + (line + 1) / 2 * apart * (line % 2 == 1 ? 1 : -1);
                if (at < base || at > base + size - 1) { continue; }
            }
            int seat = sub ? at : clearOf(beside, at, width, base);
            if (seat == Integer.MIN_VALUE) {
                ContentLog.LOGGER.debug("A railway rolled at {} finds no run clear of the streets beside it, so the district goes without it", at);
                continue;
            }
            found.add(new Rail(seat, width, alongX, sub));
        }
        return List.copyOf(found);
    }


    @Nullable private static CityPlan mapped(long seed, int districtX, int districtZ, CityMapDef map) {
        int spacing = spacing();
        if (spacing <= 0) { return null; }
        int reachX = (map.blocksWide() + district() - 1) / district() + 1;
        int reachZ = (map.blocksDeep() + district() - 1) / district() + 1;
        for (int offZ = -reachZ; offZ <= reachZ; offZ++) {
            for (int offX = -reachX; offX <= reachX; offX++) {
                int cityX = districtX + offX;
                int cityZ = districtZ + offZ;
                if (skipped(seed, cityX, cityZ)) { continue; }
                CityPlan built = window(seed, map, cityX, cityZ, districtX, districtZ);
                if (built != null) { return built; }
            }
        }
        return null;
    }

    @Nullable private static CityPlan window(long seed, CityMapDef map, int cityX, int cityZ, int districtX, int districtZ) {
        int originX = cityX * district();
        int originZ = cityZ * district();
        int windowX = districtX * district();
        int windowZ = districtZ * district();
        Rotation turn = TURNS[Math.floorMod(Hashes.mix(seed ^ SALT, cityX, 3, cityZ), TURNS.length)];
        char[][] marks = map.marks(turn);
        CityMapDef.Kind[][] grid = map.kinds(marks);
        int deep = grid.length;
        int wide = grid[0].length;
        if (windowX + district() <= originX || windowX >= originX + wide * map.cell()) { return null; }
        if (windowZ + district() <= originZ || windowZ >= originZ + deep * map.cell()) { return null; }
        int full = fullWidth();
        List<Line> alongX = new ArrayList<>();
        List<Line> alongZ = new ArrayList<>();
        for (int z = 0; z < deep; z++) { runs(grid, map, originX, originZ, z, wide, deep, full, true, alongX); }
        for (int x = 0; x < wide; x++) { runs(grid, map, originX, originZ, x, deep, wide, full, false, alongZ); }
        if (alongX.isEmpty() && alongZ.isEmpty()) { return null; }
        List<Plot> found = new ArrayList<>(mapPlots(seed, map, marks, grid, originX, originZ, alongX, alongZ));
        List<Rail> rails = List.of();
        if (grows(grid)) {
            RandomSource roll = RandomSource.create(seed ^ SALT ^ (windowX * 341873128712L + windowZ * 132897987541L));
            int block = blockSize(roll, full);
            for (Line line : lines(roll, windowZ, windowX, block, full, true)) { if (clear(alongX, line)) { alongX.add(line); } }
            for (Line line : lines(roll, windowX, windowZ, block, full, false)) { if (clear(alongZ, line)) { alongZ.add(line); } }
            alongX.sort(Comparator.comparingInt(Line::at));
            alongZ.sort(Comparator.comparingInt(Line::at));
            rails = rails(roll, windowX, windowZ, alongX, alongZ);
            plots(roll, alongX, alongZ, rails, found);
        }
        return new CityPlan(originX, originZ, map.cell(), List.copyOf(alongX), List.copyOf(alongZ), List.copyOf(found), rails, windowX, windowZ, true, null, null);
    }

    private static void runs(CityMapDef.Kind[][] grid, CityMapDef map, int originX, int originZ, int fixed, int span, int other, int full, boolean alongX, List<Line> found) {
        int cell = map.cell();
        int at = 0;
        while (at < span) {
            CityMapDef.Kind kind = alongX ? grid[fixed][at] : grid[at][fixed];
            if (bare(kind) && kind != CityMapDef.Kind.ALLEY) {
                at++;
                continue;
            }
            boolean alley = kind == CityMapDef.Kind.ALLEY;
            int end = at;
            while (end + 1 < span) {
                CityMapDef.Kind next = alongX ? grid[fixed][end + 1] : grid[end + 1][fixed];
                if (alley ? next != CityMapDef.Kind.ALLEY : bare(next)) { break; }
                end++;
            }
            int width = alley ? ALLEY_WIDTH : full;
            int center = (alongX ? originZ : originX) + fixed * cell + cell / 2;
            int from = (alongX ? originX : originZ) + at * cell;
            int to = (alongX ? originX : originZ) + (end + 1) * cell - 1;
            if (end > at || solitary(grid, fixed, at, other, alongX)) {
                found.add(new Line(center - width / 2, width, alley, alongX, from, to, false, false));
            }
            at = end + 1;
        }
    }

    private static boolean solitary(CityMapDef.Kind[][] grid, int fixed, int at, int other, boolean alongX) {
        CityMapDef.Kind before = fixed > 0 ? (alongX ? grid[fixed - 1][at] : grid[at][fixed - 1]) : CityMapDef.Kind.OPEN;
        CityMapDef.Kind after = fixed + 1 < other ? (alongX ? grid[fixed + 1][at] : grid[at][fixed + 1]) : CityMapDef.Kind.OPEN;
        return bare(before) && bare(after);
    }

    private static boolean grows(CityMapDef.Kind[][] grid) {
        for (CityMapDef.Kind[] row : grid) {
            for (CityMapDef.Kind kind : row) {
                if (kind == CityMapDef.Kind.GROW) { return true; }
            }
        }
        return false;
    }

    private static boolean clear(List<Line> held, Line line) {
        for (Line other : held) {
            if (line.at() <= other.last() && line.last() >= other.at()) { return false; }
        }
        return true;
    }

    private static boolean bare(CityMapDef.Kind kind) { return kind != CityMapDef.Kind.STREET && kind != CityMapDef.Kind.PLAZA; }

    private static List<Plot> mapPlots(long seed, CityMapDef map, char[][] marks, CityMapDef.Kind[][] grid, int originX, int originZ, List<Line> alongX, List<Line> alongZ) {
        List<Plot> found = new ArrayList<>();
        int cell = map.cell();
        int deep = grid.length;
        int wide = grid[0].length;
        for (int z = 0; z < deep; z++) {
            for (int x = 0; x < wide; x++) {
                if (grid[z][x] != CityMapDef.Kind.PLOT) { continue; }
                CityMapDef.Cell held = map.palette().get(marks[z][x]);
                if (held == null || held.picks().isEmpty()) { continue; }
                int cellX = originX + x * cell;
                int cellZ = originZ + z * cell;
                String picked = PickDef.pick(held.picks(), RandomSource.create(Hashes.mix(seed ^ SALT, cellX, 11, cellZ)));
                VillageDef def = picked == null ? null : ContentVillages.byKey(picked);
                if (def == null) {
                    if (picked != null && WARNED.add(picked)) { ContentLog.LOGGER.error("City map {} names plot '{}', which no pack provides, so that cell stays open", map.key(), picked); }
                    continue;
                }
                Line street = nearest(alongX, alongZ, cellX, cellZ, cell);
                if (street == null) { continue; }
                boolean lower = street.alongX() ? cellZ + cell / 2 > street.middle() : cellX + cell / 2 > street.middle();
                int midX = cellX + cell / 2;
                int midZ = cellZ + cell / 2;
                int fromX = midX - def.width() / 2;
                int fromZ = midZ - def.depth() / 2;
                Plot plot = new Plot(fromX, fromZ, fromX + def.width() - 1, fromZ + def.depth() - 1, street, lower, def);
                if (fits(found, plot)) { found.add(plot); }
            }
        }
        return List.copyOf(found);
    }

    @Nullable private static Line nearest(List<Line> alongX, List<Line> alongZ, int cellX, int cellZ, int cell) {
        int midX = cellX + cell / 2;
        int midZ = cellZ + cell / 2;
        Line best = null;
        int closest = Integer.MAX_VALUE;
        for (Line line : alongX) {
            if (!line.covers(midX)) { continue; }
            int gap = Math.abs(line.middle() - midZ);
            if (gap < closest) { closest = gap; best = line; }
        }
        for (Line line : alongZ) {
            if (!line.covers(midZ)) { continue; }
            int gap = Math.abs(line.middle() - midX);
            if (gap < closest) { closest = gap; best = line; }
        }
        return best;
    }

    private static boolean skipped(long seed, int districtX, int districtZ) {
        int spacing = spacing();
        if (spacing <= 0) { return true; }
        if (spacing == 1) { return false; }
        int regionX = Math.floorDiv(districtX, spacing);
        int regionZ = Math.floorDiv(districtZ, spacing);
        long mixed = Hashes.mix(seed ^ SALT, regionX, 0, regionZ);
        return districtX != regionX * spacing + Math.floorMod(mixed, spacing) || districtZ != regionZ * spacing + Math.floorMod(mixed >> 21, spacing);
    }

    public static int spacing() { return ContentControl.number(ContentControl.VILLAGES, "villageCitySpacing", Config.worldgen.villageCitySpacing()); }

    public static int flatRun() { return Math.max(1, ContentControl.number(ContentControl.VILLAGES, "villagePathFlatRun", Config.worldgen.villagePathFlatRun())); }

    public static int streetFullWidth() { return fullWidth(); }

    private static int fullWidth() {
        int extra = Math.max(0, ContentControl.number(ContentControl.VILLAGES, "villagePathExtraWidth", Config.worldgen.villagePathExtraWidth()));
        return VANILLA_WIDTH + extra * 2 + lineWidth() * 2 + walkWidth() * 2;
    }

    public static int lineWidth() { return ContentCity.lineBlock().isEmpty() ? 0 : 1; }

    public static int walkWidth() { return ContentCity.sidewalkBlock().isEmpty() ? 0 : Math.max(0, ContentControl.number(ContentControl.VILLAGES, "villagePathSidewalkWidth", Config.worldgen.villagePathSidewalkWidth())); }

    public static int plazaReach() { return 3 + (fullWidth() - 3) / 2; }

    public static int plazaPaved() { return plazaReach() - walkWidth(); }

    public static void reset() {
        district = Math.max(DISTRICT_LEAST, (2 * ContentVillages.largestPlot() + 2 * plazaReach() + 2 * fullWidth() + 15) / 16 * 16);
        PLANS.clear();
        CITIES.clear();
    }

    public static int district() { return district; }

    public static int chunks() { return district / 16; }

    private static int blockSize(RandomSource roll, int full) {
        List<PickDef> picks = new ArrayList<>();
        for (String entry : ContentControl.list(ContentControl.VILLAGES, "villageBlockSizes", Config.worldgen.villageBlockSizes())) {
            String text = entry.trim().toLowerCase(Locale.ROOT);
            if (text.isEmpty()) { continue; }
            int at = text.lastIndexOf('=');
            try {
                int size = Integer.parseInt(at == -1 ? text : text.substring(0, at).trim());
                picks.add(new PickDef(Integer.toString(size), at == -1 ? 1 : Math.max(1, Integer.parseInt(text.substring(at + 1).trim()))));
            }
            catch (NumberFormatException notNumber) {
                if (WARNED.add(entry)) { ContentLog.LOGGER.error("villageBlockSizes entry '{}' is not a size or a size=weight, so it is left out", entry); }
            }
        }
        String picked = PickDef.pick(picks, roll);
        return Mth.clamp(picked == null ? DEFAULT_BLOCK : Integer.parseInt(picked), LEAST_BLOCK, district() - full);
    }
}
