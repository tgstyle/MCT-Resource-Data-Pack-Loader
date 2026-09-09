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
import java.util.Set;
import javax.annotation.Nullable;

public final class CityPlan {
    public static final int DISTRICT = 96;
    public static final int CHUNKS = DISTRICT / 16;
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

    public record Rail(int at, int width, boolean alongX) {
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

    private CityPlan(int originX, int originZ, int block, List<Line> alongX, List<Line> alongZ, List<Plot> plots, List<Rail> rails) {
        this(originX, originZ, block, alongX, alongZ, plots, rails, originX, originZ, false);
    }

    private CityPlan(int originX, int originZ, int block, List<Line> alongX, List<Line> alongZ, List<Plot> plots, List<Rail> rails, int windowX, int windowZ, boolean mapped) {
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

    public int originZ() { return originZ; }

    public int block() { return block; }

    public int windowX() { return windowX; }

    public int windowZ() { return windowZ; }

    public int spanStart(Line line) {
        if (!mapped) { return line.alongX() ? originX : originZ; }
        return line.from();
    }

    public int spanLength(Line line) {
        if (!mapped) { return DISTRICT; }
        return line.to() - line.from() + 1;
    }

    public boolean emits(int x, int z) {
        if (!mapped) { return true; }
        return x >= windowX && x < windowX + DISTRICT && z >= windowZ && z < windowZ + DISTRICT;
    }

    public boolean emitsAlong(Line line, int along) {
        if (!mapped) { return true; }
        return line.alongX() ? emits(along, line.middle()) : emits(line.middle(), along);
    }

    public List<Line> alongX() { return alongX; }

    public List<Line> alongZ() { return alongZ; }

    public List<Line> crossing(Line line) { return line.alongX() ? alongZ : alongX; }

    public Line plazaRow() { return alongX.get(alongX.size() / 2); }

    public Line plazaColumn() { return alongZ.get(alongZ.size() / 2); }

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
        if (skipped(seed, districtX, districtZ)) { return null; }
        RandomSource roll = RandomSource.create(seed ^ SALT ^ (districtX * 341873128712L + districtZ * 132897987541L));
        int full = fullWidth();
        int block = blockSize(roll, full);
        int originX = districtX * DISTRICT;
        int originZ = districtZ * DISTRICT;
        List<Line> alongX = lines(roll, originZ, originX, block, full, true);
        List<Line> alongZ = lines(roll, originX, originZ, block, full, false);
        if (alongX.isEmpty() || alongZ.isEmpty()) { return null; }
        List<Plot> plots = plots(roll, alongX, alongZ);
        int least = Math.max(0, ContentControl.number(ContentControl.VILLAGES, "villagePlotsLeast", Config.worldgen.villagePlotsLeast()));
        if (plots.size() < least) { return null; }
        return new CityPlan(originX, originZ, block, alongX, alongZ, plots, rails(roll, originX, originZ));
    }

    private static List<Line> lines(RandomSource roll, int origin, int across, int block, int full, boolean alongX) {
        int chance = Mth.clamp(ContentControl.number(ContentControl.VILLAGES, "villagePathAlleyChance", Config.worldgen.villagePathAlleyChance()), 0, 100);
        int least = Math.max(0, ContentControl.number(ContentControl.VILLAGES, "villagePathMinimumWidth", Config.worldgen.villagePathMinimumWidth()));
        int stub = Math.max(ALLEY_WIDTH, block / 3);
        List<Line> found = new ArrayList<>();
        int at = 0;
        while (at + ALLEY_WIDTH <= DISTRICT) {
            boolean alley = chance > 0 && roll.nextInt(100) < chance;
            int width = alley ? ALLEY_WIDTH : full;
            if (at + width > DISTRICT) { break; }
            if (width >= least) {
                boolean endsLow = !found.isEmpty() && roll.nextInt(DEAD_END_ODDS) == 0;
                boolean endsHigh = !found.isEmpty() && roll.nextInt(DEAD_END_ODDS) == 0;
                int from = across + (endsLow ? Math.min(DISTRICT / 2, block + stub) : 0);
                int to = across + DISTRICT - 1 - (endsHigh ? Math.min(DISTRICT / 2, block + stub) : 0);
                if (to - from + 1 >= ALLEY_WIDTH) { found.add(new Line(origin + at, width, alley, alongX, from, to, endsLow, endsHigh)); }
            }
            at += width + block;
        }
        return List.copyOf(found);
    }

    public List<Plot> plots() { return plots; }

    private static List<Plot> plots(RandomSource roll, List<Line> alongX, List<Line> alongZ) {
        List<Plot> found = new ArrayList<>();
        plots(roll, alongX, alongZ, found);
        return List.copyOf(found);
    }

    private static void plots(RandomSource roll, List<Line> alongX, List<Line> alongZ, List<Plot> found) {
        List<VillageDef> choices = ContentVillages.allowed();
        if (choices.isEmpty()) { return; }
        int most = Math.max(0, ContentControl.number(ContentControl.VILLAGES, "villagePlotsMost", Config.worldgen.villagePlotsMost()));
        for (int row = 0; row + 1 < alongX.size(); row++) {
            for (int column = 0; column + 1 < alongZ.size(); column++) {
                int fromX = alongZ.get(column).last() + 1;
                int toX = alongZ.get(column + 1).at() - 1;
                int fromZ = alongX.get(row).last() + 1;
                int toZ = alongX.get(row + 1).at() - 1;
                if (toX < fromX || toZ < fromZ) { continue; }
                frontage(roll, choices, found, alongX.get(row), true, fromX, toX, fromZ, toZ, most);
                frontage(roll, choices, found, alongX.get(row + 1), false, fromX, toX, fromZ, toZ, most);
                frontage(roll, choices, found, alongZ.get(column), true, fromX, toX, fromZ, toZ, most);
                frontage(roll, choices, found, alongZ.get(column + 1), false, fromX, toX, fromZ, toZ, most);
            }
        }
    }

    private static void frontage(RandomSource roll, List<VillageDef> choices, List<Plot> found, Line street, boolean lower, int fromX, int toX, int fromZ, int toZ, int most) {
        boolean alongX = street.alongX();
        int least = alongX ? fromX : fromZ;
        int last = alongX ? toX : toZ;
        int deep = alongX ? toZ - fromZ + 1 : toX - fromX + 1;
        int at = least;
        while (at <= last) {
            if (most > 0 && found.size() >= most) { return; }
            VillageDef def = ContentVillages.pick(choices, roll, last - at + 1, deep);
            if (def == null) { return; }
            int back = lower ? (alongX ? fromZ : fromX) + def.apron() : (alongX ? toZ : toX) - def.apron() - def.depth() + 1;
            int over = back + def.depth() - 1;
            Plot plot = alongX ? new Plot(at, back, at + def.width() - 1, over, street, lower, def)
                               : new Plot(back, at, over, at + def.width() - 1, street, lower, def);
            if (fits(found, plot)) { found.add(plot); }
            at += def.width() + GAP;
        }
    }

    private static boolean fits(List<Plot> found, Plot plot) {
        for (Plot held : found) {
            if (plot.fromX() <= held.toX() && plot.toX() >= held.fromX() && plot.fromZ() <= held.toZ() && plot.toZ() >= held.fromZ()) { return false; }
        }
        return true;
    }

    public List<Rail> rails() { return rails; }

    public static int railTail() { return Math.clamp(ContentControl.number(ContentControl.VILLAGES, "villageRailTail", Config.worldgen.villageRailTail()), 0, TAIL_MOST); }

    public static int railWidth() {
        int bed = Math.max(3, ContentControl.number(ContentControl.VILLAGES, "villageRailWidth", Config.worldgen.villageRailWidth()));
        int tracks = Math.max(0, ContentControl.number(ContentControl.VILLAGES, "villageRailTracks", Config.worldgen.villageRailTracks()));
        int gap = Math.max(2, ContentControl.number(ContentControl.VILLAGES, "villageRailTrackGap", Config.worldgen.villageRailTrackGap()));
        if (tracks > 1) { bed = Math.max(bed, (tracks - 1) * gap + 3); }
        int shoulder = ContentCity.railShoulderBlock().isEmpty() ? 0 : Math.max(0, ContentControl.number(ContentControl.VILLAGES, "villageRailShoulderWidth", Config.worldgen.villageRailShoulderWidth()));
        return bed + shoulder * 2;
    }

    private static List<Rail> rails(RandomSource roll, int originX, int originZ) {
        int lines = Math.max(0, ContentControl.number(ContentControl.VILLAGES, "villageRailLines", Config.worldgen.villageRailLines()));
        if (lines == 0) { return List.of(); }
        String wanted = ContentControl.text(ContentControl.VILLAGES, "villageRailDirection", Config.worldgen.villageRailDirection()).trim().toLowerCase(Locale.ROOT);
        boolean alongX = "ew".equals(wanted) || (!"ns".equals(wanted) && roll.nextBoolean());
        int width = railWidth();
        int spacing = Math.max(1, ContentControl.number(ContentControl.VILLAGES, "villageRailSpacing", Config.worldgen.villageRailSpacing()));
        int pitch = width + spacing;
        int room = DISTRICT - width;
        if (room < 0) { return List.of(); }
        int first = lines * pitch - spacing > DISTRICT ? 0 : roll.nextInt(Math.max(1, room - (lines - 1) * pitch + 1));
        List<Rail> found = new ArrayList<>();
        for (int line = 0; line < lines; line++) {
            int at = first + line * pitch;
            if (at + width > DISTRICT) { break; }
            found.add(new Rail((alongX ? originZ : originX) + at, width, alongX));
        }
        return List.copyOf(found);
    }


    @Nullable private static CityPlan mapped(long seed, int districtX, int districtZ, CityMapDef map) {
        int spacing = spacing();
        if (spacing <= 0) { return null; }
        int reachX = (map.blocksWide() + DISTRICT - 1) / DISTRICT + 1;
        int reachZ = (map.blocksDeep() + DISTRICT - 1) / DISTRICT + 1;
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
        int originX = cityX * DISTRICT;
        int originZ = cityZ * DISTRICT;
        int windowX = districtX * DISTRICT;
        int windowZ = districtZ * DISTRICT;
        Rotation turn = TURNS[Math.floorMod(Hashes.mix(seed ^ SALT, cityX, 3, cityZ), TURNS.length)];
        char[][] marks = map.marks(turn);
        CityMapDef.Kind[][] grid = map.kinds(marks);
        int deep = grid.length;
        int wide = grid[0].length;
        if (windowX + DISTRICT <= originX || windowX >= originX + wide * map.cell()) { return null; }
        if (windowZ + DISTRICT <= originZ || windowZ >= originZ + deep * map.cell()) { return null; }
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
            plots(roll, alongX, alongZ, found);
            rails = rails(roll, windowX, windowZ);
        }
        return new CityPlan(originX, originZ, map.cell(), List.copyOf(alongX), List.copyOf(alongZ), List.copyOf(found), rails, windowX, windowZ, true);
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
            int centre = (alongX ? originZ : originX) + fixed * cell + cell / 2;
            int from = (alongX ? originX : originZ) + at * cell;
            int to = (alongX ? originX : originZ) + (end + 1) * cell - 1;
            if (end > at || solitary(grid, fixed, at, other, alongX)) {
                found.add(new Line(centre - width / 2, width, alley, alongX, from, to, false, false));
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

    private static int fullWidth() {
        int extra = Math.max(0, ContentControl.number(ContentControl.VILLAGES, "villagePathExtraWidth", Config.worldgen.villagePathExtraWidth()));
        return VANILLA_WIDTH + extra * 2;
    }

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
        return Mth.clamp(picked == null ? DEFAULT_BLOCK : Integer.parseInt(picked), LEAST_BLOCK, DISTRICT - full);
    }
}
