package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.def.CityMapDef;
import mctmods.resourcedatapackloader.content.worldgen.CityGrowth.Growth;
import mctmods.resourcedatapackloader.content.worldgen.CityPlan.End;
import mctmods.resourcedatapackloader.content.worldgen.CityPlan.Junction;
import mctmods.resourcedatapackloader.content.worldgen.CityPlan.Lift;
import mctmods.resourcedatapackloader.content.worldgen.CityPlan.Line;
import mctmods.resourcedatapackloader.content.worldgen.CityPlan.Plot;
import mctmods.resourcedatapackloader.content.worldgen.CityPlan.Rail;
import mctmods.resourcedatapackloader.content.worldgen.CityPlan.Town;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Hashes;

import com.google.gson.JsonObject;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.util.RandomSource;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Set;
import javax.annotation.Nullable;

final class CityLayout {
    private static final long MAP_SALT = 0x3A9F00D5L;
    private static final int COURT_ODDS = 4;
    private static final int PIER_LEAST = 8;
    private static final int PIER_ROLL = 17;
    private static final int SHORE_REACH = 64;
    static final Map<Long, Optional<Town>> MAP_TOWNS = new ConcurrentHashMap<>();
    static final Map<Long, List<BoundingBox>> MAP_WELLS = new ConcurrentHashMap<>();
    private static final int VILLAGE_START = 2;
    private static final Rotation[] TURNS = {Rotation.NONE, Rotation.CLOCKWISE_90, Rotation.CLOCKWISE_180, Rotation.COUNTERCLOCKWISE_90};

    record Drawing(CityMapDef map, CityMapDef.Kind[][] grid, char[][] marks, int[][] groups, int originX, int originZ, boolean hinted, boolean[][] rows, boolean[][] columns) {
        Drawing(CityMapDef map, CityMapDef.Kind[][] grid, char[][] marks, int[][] groups, int originX, int originZ, boolean hinted) { this(map, grid, marks, groups, originX, originZ, hinted, new boolean[grid.length][grid[0].length], new boolean[grid.length][grid[0].length]); }

        CityMapDef.Kind kind(int fixed, int at, boolean alongX) { return alongX ? grid[fixed][at] : grid[at][fixed]; }

        char mark(int fixed, int at, boolean alongX) { return alongX ? marks[fixed][at] : marks[at][fixed]; }

        int group(int fixed, int at, boolean alongX) { return alongX ? groups[fixed][at] : groups[at][fixed]; }

        void cover(int fixed, int at, boolean alongX) {
            if (alongX) { rows[fixed][at] = true; }
            else { columns[at][fixed] = true; }
        }
    }

    private CityLayout() {}

    @Nullable static CityPlan mapped(CityGround ground, int districtX, int districtZ, CityMapDef map) {
        int spacing = CityPlan.spacing();
        if (spacing <= 0) { return null; }
        int reachX = (map.blocksWide() + CityPlan.district() - 1) / CityPlan.district() + 1 + pinSlack(map);
        int reachZ = (map.blocksDeep() + CityPlan.district() - 1) / CityPlan.district() + 1 + pinSlack(map);
        CityPlan grown = CityGrowth.grownDistrict(ground, districtX, districtZ, map, reachX, reachZ);
        if (grown != null) { return grown; }
        for (int offZ = -reachZ; offZ <= reachZ; offZ++) {
            for (int offX = -reachX; offX <= reachX; offX++) {
                int cityX = districtX + offX;
                int cityZ = districtZ + offZ;
                if (spacing > 1 && elsewhere(ground, cityX, cityZ)) { continue; }
                CityPlan built = window(ground, map, cityX, cityZ, districtX, districtZ, spacing > 1);
                if (built != null) { return built; }
            }
        }
        return null;
    }

    static boolean elsewhere(CityGround ground, int cityX, int cityZ) {
        int[] held = CityDistricts.center(ground, cityX, cityZ);
        return held == null || held[0] != cityX || held[1] != cityZ;
    }

    @Nullable private static CityPlan window(CityGround ground, CityMapDef map, int cityX, int cityZ, int districtX, int districtZ, boolean centered) {
        long seed = ground.seed();
        Rotation turn = turnOf(seed, cityX, cityZ);
        char[][] marks = map.marks(turn);
        CityMapDef.Kind[][] grid = map.kinds(marks);
        int deep = grid.length;
        int wide = grid[0].length;
        int[] origin = mapOrigin(map, grid, cityX, cityZ, centered);
        int originX = origin[0];
        int originZ = origin[1];
        int windowX = CityPlan.windowOf(districtX, true);
        int windowZ = CityPlan.windowOf(districtZ, false);
        if (windowX + CityPlan.district() <= originX || windowX >= originX + wide * map.cell()) { return null; }
        if (windowZ + CityPlan.district() <= originZ || windowZ >= originZ + deep * map.cell()) { return null; }
        int full = CityPlan.fullWidth();
        int[][] groups = new int[deep][wide];
        List<Lift> lifts = lifts(grid, marks, map, originX, originZ, groups);
        Drawing drawing = new Drawing(map, grid, marks, groups, originX, originZ, map.bulbHinted());
        List<Line> alongX = new ArrayList<>();
        List<Line> alongZ = new ArrayList<>();
        for (int z = 0; z < deep; z++) { runs(drawing, z, wide, deep, true, alongX); }
        for (int x = 0; x < wide; x++) { runs(drawing, x, deep, wide, false, alongZ); }
        junctions(drawing, alongX, alongZ);
        if (alongX.isEmpty() && alongZ.isEmpty()) { return null; }
        Town town = mapTown(ground, map, cityX, cityZ, centered);
        settle(ground, town, alongX, alongX.size(), alongZ, drawing.hinted());
        settle(ground, town, alongZ, alongZ.size(), alongX, drawing.hinted());
        List<CitySeams.Reach> reaches = CitySeams.reaches(ground, town);
        CitySeams.reached(reaches, alongX, alongZ);
        CityEnds.closeEnds(ground, town, alongX, alongZ, drawing.hinted(), false);
        int drawnX = alongX.size();
        int drawnZ = alongZ.size();
        pierOut(ground, drawing, alongX, alongZ, false);
        List<Line> drawnRows = alongX.subList(0, drawnX);
        List<Line> drawnColumns = alongZ.subList(0, drawnZ);
        List<Junction> plazas = mapPlazas(map, grid, originX, originZ, windowX, windowZ, drawnRows, drawnColumns, full);
        String type = ground.villageStyle(windowX + CityPlan.district() / 2, windowZ + CityPlan.district() / 2);
        List<Plot> found = new ArrayList<>(CityPlanPlots.mapPlots(seed, map, marks, grid, originX, originZ, drawnRows, drawnColumns));
        if (town != null && CityGrowth.tooSmall(ground, map, cityX, cityZ, centered, town)) { return null; }
        List<Rail> rails = town == null ? List.of() : CityPlanTowns.across(CityRails.placed(seed, town), windowX, windowZ);
        if (town != null) {
            Growth growth = CityGrowth.mapGrowth(ground, map, cityX, cityZ, centered, town);
            for (Line alley : growth.alleys()) { (alley.alongX() ? alongX : alongZ).add(alley); }
            for (Line tie : growth.ties()) { (tie.alongX() ? alongX : alongZ).add(tie); }
            found.clear();
            found.addAll(growth.plots());
            CitySeams.madeWay(reaches, found);
        }
        return new CityPlan(originX, originZ, map.cell(), List.copyOf(alongX), List.copyOf(alongZ), List.copyOf(found), List.copyOf(rails), windowX, windowZ, true, plazas, List.copyOf(lifts), type, town);
    }

    @Nullable static Town mapTown(CityGround ground, CityMapDef map, int cityX, int cityZ, boolean centered) {
        long held = CityPlan.packed(cityX, cityZ);
        Optional<Town> known = MAP_TOWNS.get(held);
        if (known != null) { return known.orElse(null); }
        Rotation turn = turnOf(ground.seed(), cityX, cityZ);
        char[][] marks = map.marks(turn);
        CityMapDef.Kind[][] grid = map.kinds(marks);
        int deep = grid.length;
        int wide = grid[0].length;
        int cell = map.cell();
        int[] origin = mapOrigin(map, grid, cityX, cityZ, centered);
        int originX = origin[0];
        int originZ = origin[1];
        Drawing drawing = new Drawing(map, grid, marks, new int[deep][wide], originX, originZ, map.bulbHinted());
        List<Line> drawn = new ArrayList<>();
        List<Line> columns = new ArrayList<>();
        for (int z = 0; z < deep; z++) { runs(drawing, z, wide, deep, true, drawn); }
        for (int x = 0; x < wide; x++) { runs(drawing, x, deep, wide, false, columns); }
        junctions(drawing, drawn, columns);
        List<BoundingBox> wells = new ArrayList<>();
        int full = CityPlan.fullWidth();
        for (int z = 0; z < deep; z++) {
            for (int x = 0; x < wide; x++) {
                if (grid[z][x] != CityMapDef.Kind.PLAZA) { continue; }
                int midX = originX + x * cell + cell / 2;
                int midZ = originZ + z * cell + cell / 2;
                Line row = through(drawn, midZ, midX);
                Line column = through(columns, midX, midZ);
                int wellX = (column == null ? midX - full / 2 + (full - 1) / 2 : (column.at() + column.last()) / 2) - ContentCityWellPiece.SIZE / 2;
                int wellZ = (row == null ? midZ - full / 2 + (full - 1) / 2 : (row.at() + row.last()) / 2) - ContentCityWellPiece.SIZE / 2;
                wells.add(new BoundingBox(wellX, 0, wellZ, wellX + ContentCityWellPiece.SIZE - 1, 0, wellZ + ContentCityWellPiece.SIZE - 1));
            }
        }
        if (MAP_WELLS.putIfAbsent(held, List.copyOf(wells)) == null) { ContentLog.LOGGER.info("The drawn city of district {}, {} lays its map turned {}, rolled from the seed and the village start at chunk {}, {}", cityX, cityZ, turn, startChunk(cityX, cityZ, 0), startChunk(cityX, cityZ, 1)); }
        drawn.addAll(columns);
        Town made = null;
        if (!drawn.isEmpty()) {
            int wellX = originX + wide * cell / 2;
            int wellZ = originZ + deep * cell / 2;
            boolean plaza = false;
            for (int z = 0; z < deep && !plaza; z++) {
                for (int x = 0; x < wide; x++) {
                    if (grid[z][x] != CityMapDef.Kind.PLAZA) { continue; }
                    wellX = originX + x * cell + cell / 2;
                    wellZ = originZ + z * cell + cell / 2;
                    plaza = true;
                    break;
                }
            }
            made = new Town(held, CityPlan.key(ground.seed() ^ MAP_SALT, cityX, cityZ), MAP_SALT ^ CityPlanTowns.citySalt(cityX, cityZ), wellX, wellZ, originX, originX + wide * cell - 1, originZ, originZ + deep * cell - 1, List.copyOf(drawn), CityDistricts.crossAt(), CityDistricts.crossAt());
        }
        MAP_TOWNS.putIfAbsent(held, Optional.ofNullable(made));
        return made;
    }

    static List<BoundingBox> mapWells(Town town) { return MAP_WELLS.getOrDefault(town.key(), List.of()); }

    static Rotation turnOf(long seed, int cityX, int cityZ) { return TURNS[Math.floorMod(Hashes.mix(seed, startChunk(cityX, cityZ, 0) * 16 + VILLAGE_START, 7, startChunk(cityX, cityZ, 1) * 16 + VILLAGE_START), TURNS.length)]; }

    private static int startChunk(int cityX, int cityZ, int axis) {
        int[] pin = CityDistricts.pinnedAt().get(CityPlan.packed(cityX, cityZ));
        if (pin != null) { return Math.floorDiv(pin[axis], 16); }
        return Math.floorDiv(CityPlan.windowOf(axis == 0 ? cityX : cityZ, axis == 0), 16) + CityPlan.chunks() / 2;
    }

    static int[] mapOrigin(CityMapDef map, CityMapDef.Kind[][] grid, int cityX, int cityZ, boolean centered) {
        int cell = map.cell();
        int[] pin = CityDistricts.pinnedAt().get(CityPlan.packed(cityX, cityZ));
        if (pin != null) {
            int[] anchor = anchor(grid);
            return new int[] {pin[0] + 1 - anchor[0] * cell - cell / 2, pin[1] + 1 - anchor[1] * cell - cell / 2};
        }
        return new int[] {CityPlan.windowOf(cityX, true) + (centered ? CityPlan.district() / 2 - grid[0].length * cell / 2 : 0), CityPlan.windowOf(cityZ, false) + (centered ? CityPlan.district() / 2 - grid.length * cell / 2 : 0)};
    }

    private static int[] anchor(CityMapDef.Kind[][] grid) {
        for (int z = 0; z < grid.length; z++) {
            for (int x = 0; x < grid[z].length; x++) {
                if (grid[z][x] == CityMapDef.Kind.PLAZA) { return new int[] {x, z}; }
            }
        }
        return new int[] {grid[0].length / 2, grid.length / 2};
    }

    static int pinSlack(CityMapDef map) { return CityDistricts.pinnedAt().isEmpty() ? 0 : (Math.max(map.blocksWide(), map.blocksDeep()) + CityPlan.district() - 1) / CityPlan.district() + 1; }

    static List<Junction> mapPlazas(CityMapDef map, CityMapDef.Kind[][] grid, int originX, int originZ, int windowX, int windowZ, List<Line> rows, List<Line> columns, int full) {
        List<Junction> found = new ArrayList<>();
        for (Junction plaza : mapPlazas(map, grid, originX, originZ, rows, columns, full)) {
            int midX = plaza.alongZ().middle();
            int midZ = plaza.alongX().middle();
            if (midX >= windowX && midX < windowX + CityPlan.district() && midZ >= windowZ && midZ < windowZ + CityPlan.district()) { found.add(plaza); }
        }
        return List.copyOf(found);
    }

    static List<Junction> mapPlazas(CityMapDef map, CityMapDef.Kind[][] grid, int originX, int originZ, List<Line> rows, List<Line> columns, int full) {
        List<Junction> found = new ArrayList<>();
        int cell = map.cell();
        for (int z = 0; z < grid.length; z++) {
            for (int x = 0; x < grid[z].length; x++) {
                if (grid[z][x] != CityMapDef.Kind.PLAZA) { continue; }
                int midX = originX + x * cell + cell / 2;
                int midZ = originZ + z * cell + cell / 2;
                Line row = through(rows, midZ, midX);
                Line column = through(columns, midX, midZ);
                if (row == null) { row = new Line(midZ - full / 2, full, false, true, originX + x * cell, originX + (x + 1) * cell - 1, End.MET, End.MET); }
                if (column == null) { column = new Line(midX - full / 2, full, false, false, originZ + z * cell, originZ + (z + 1) * cell - 1, End.MET, End.MET); }
                found.add(new Junction(row, column));
            }
        }
        return List.copyOf(found);
    }

    @Nullable private static Line through(List<Line> lines, int across, int along) {
        for (Line line : lines) {
            if (!line.alley() && across >= line.at() && across <= line.last() && line.covers(along)) { return line; }
        }
        return null;
    }

    static List<Lift> lifts(CityMapDef.Kind[][] grid, char[][] marks, CityMapDef map, int originX, int originZ, int[][] groups) {
        int deep = grid.length;
        int wide = grid[0].length;
        List<Lift> found = new ArrayList<>();
        int[][] steps = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        for (int z = 0; z < deep; z++) {
            for (int x = 0; x < wide; x++) {
                if (grid[z][x] != CityMapDef.Kind.ELEVATED || groups[z][x] != 0) { continue; }
                int id = found.size() + 1;
                int[] box = {Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, 0};
                Deque<int[]> open = new ArrayDeque<>();
                open.push(new int[] {x, z});
                groups[z][x] = id;
                while (!open.isEmpty()) {
                    int[] at = open.pop();
                    box[0] = Math.min(box[0], originX + at[0] * map.cell());
                    box[1] = Math.min(box[1], originZ + at[1] * map.cell());
                    box[2] = Math.max(box[2], originX + (at[0] + 1) * map.cell() - 1);
                    box[3] = Math.max(box[3], originZ + (at[1] + 1) * map.cell() - 1);
                    box[4] = Math.max(box[4], map.heightOf(marks[at[1]][at[0]]));
                    for (int[] step : steps) {
                        int nextX = at[0] + step[0];
                        int nextZ = at[1] + step[1];
                        if (nextX < 0 || nextZ < 0 || nextX >= wide || nextZ >= deep || grid[nextZ][nextX] != CityMapDef.Kind.ELEVATED || groups[nextZ][nextX] != 0) { continue; }
                        groups[nextZ][nextX] = id;
                        open.push(new int[] {nextX, nextZ});
                    }
                }
                found.add(new Lift(box[0], box[1], box[2], box[3], box[4]));
            }
        }
        return found;
    }

    static void runs(Drawing drawing, int fixed, int span, int other, boolean alongX, List<Line> found) {
        int at = 0;
        while (at < span) {
            CityMapDef.Kind kind = drawing.kind(fixed, at, alongX);
            if (bare(kind) && kind != CityMapDef.Kind.ALLEY) {
                at++;
                continue;
            }
            boolean alley = kind == CityMapDef.Kind.ALLEY;
            int end = at;
            while (end + 1 < span) {
                CityMapDef.Kind next = drawing.kind(fixed, end + 1, alongX);
                if (alley ? next != CityMapDef.Kind.ALLEY : bare(next)) { break; }
                end++;
            }
            boolean street = false;
            for (int index = at; index <= end; index++) { street |= streetLike(drawing.kind(fixed, index, alongX)); }
            if ((end > at || alongX && (alley ? alleyAlone(drawing.grid(), fixed, at, other) : solitary(drawing.grid(), fixed, at, other))) && (alley || street)) {
                JsonObject keys = alley ? null : keysAlong(drawing, fixed, at, end, other, alongX);
                int width = alley ? CityAlleys.ALLEY_WIDTH : CityPlan.fullWidthFor(keys);
                End low = drawing.hinted() && !alley && drawing.kind(fixed, at, alongX) == CityMapDef.Kind.BULB ? End.COURT : End.BARE;
                End high = drawing.hinted() && !alley && drawing.kind(fixed, end, alongX) == CityMapDef.Kind.BULB ? End.COURT : End.BARE;
                found.add(drawn(drawing, fixed, at, end, width, alley, alongX, low, high, keys));
                for (int index = at; index <= end; index++) { drawing.cover(fixed, index, alongX); }
            }
            at = end + 1;
        }
    }

    private static Line drawn(Drawing drawing, int fixed, int at, int end, int width, boolean alley, boolean alongX, End low, End high, @Nullable JsonObject keys) {
        int cell = drawing.map().cell();
        int center = (alongX ? drawing.originZ() : drawing.originX()) + fixed * cell + cell / 2;
        int origin = alongX ? drawing.originX() : drawing.originZ();
        int from = origin + at * cell;
        int to = origin + (end + 1) * cell - 1;
        Set<Integer> touched = new LinkedHashSet<>();
        for (int index = at; index <= end; index++) {
            int group = drawing.group(fixed, index, alongX);
            if (group > 0) { touched.add(group - 1); }
        }
        return new Line(center - width / 2, width, alley, alongX, from, to, low, high, keys, List.copyOf(touched));
    }

    static void junctions(Drawing drawing, List<Line> alongX, List<Line> alongZ) {
        CityMapDef.Kind[][] grid = drawing.grid();
        for (int z = 0; z < grid.length; z++) {
            for (int x = 0; x < grid[z].length; x++) {
                if (grid[z][x] != CityMapDef.Kind.JUNCTION) { continue; }
                JsonObject keys = drawing.map().keysOf(drawing.marks()[z][x]);
                int width = CityPlan.fullWidthFor(keys);
                if (!drawing.rows()[z][x]) {
                    alongX.add(drawn(drawing, z, x, x, width, false, true, End.BARE, End.BARE, keys));
                    drawing.cover(z, x, true);
                }
                if (!drawing.columns()[z][x]) {
                    alongZ.add(drawn(drawing, x, z, z, width, false, false, End.BARE, End.BARE, keys));
                    drawing.cover(x, z, false);
                }
            }
        }
    }

    static void settle(CityGround ground, @Nullable Town town, List<Line> lines, int drawn, List<Line> crossing, boolean hinted) {
        for (int index = 0; index < drawn; index++) {
            Line line = lines.get(index);
            lines.set(index, line.ending(settled(ground, town, line, true, crossing, hinted), settled(ground, town, line, false, crossing, hinted)));
        }
    }

    private static End settled(CityGround ground, @Nullable Town town, Line line, boolean low, List<Line> crossing, boolean hinted) {
        int along = low ? line.from() : line.to();
        for (Line other : crossing) {
            if (along >= other.at() - 1 && along <= other.last() + 1 && other.to() + 1 >= line.at() && other.from() - 1 <= line.last()) { return End.MET; }
        }
        End marked = low ? line.endsLow() : line.endsHigh();
        if (hinted || line.alley()) { return marked; }
        if (CitySeams.facesNeighbor(ground, town, line.alongX(), along, low ? -1 : 1, line.middle())) { return End.BARE; }
        int x = line.alongX() ? along : line.middle();
        int z = line.alongX() ? line.middle() : along;
        return RandomSource.create(Hashes.mix(ground.seed() ^ CityPlan.SALT, x, 13, z)).nextInt(COURT_ODDS) < COURT_ODDS - 1 ? End.COURT : End.BARE;
    }

    static void pierOut(CityGround ground, Drawing drawing, List<Line> rows, List<Line> columns, boolean told) {
        if (ContentCity.pierStyles().isEmpty()) { return; }
        for (List<Line> lines : List.of(rows, columns)) {
            for (int index = 0; index < lines.size(); index++) {
                Line line = lines.get(index);
                if (!line.alley()) { lines.set(index, pierOut(ground, drawing, line, rows, columns, told)); }
            }
        }
    }

    static Line pierOut(CityGround ground, Drawing drawing, Line line, List<Line> rows, List<Line> columns, boolean told) {
        int from = line.from();
        int to = line.to();
        for (int side = 0; side < 2; side++) {
            boolean low = side == 0;
            if ((low ? line.endsLow() : line.endsHigh()) == End.MET) { continue; }
            int dir = low ? -1 : 1;
            int end = low ? from : to;
            int far = low ? to : from;
            int shore = Integer.MIN_VALUE;
            for (int row = end; dir > 0 ? row >= far : row <= far; row -= dir) {
                if (Math.abs(end - row) > SHORE_REACH) { break; }
                if (landAt(ground, line, row)) {
                    shore = row;
                    break;
                }
            }
            if (shore == Integer.MIN_VALUE) { continue; }
            int shoreX = line.alongX() ? shore : line.middle();
            int shoreZ = line.alongX() ? line.middle() : shore;
            int target = shore + dir * (PIER_LEAST + RandomSource.create(Hashes.mix(ground.seed() ^ CityPlan.SALT, shoreX, 29, shoreZ)).nextInt(PIER_ROLL));
            if (dir > 0 ? target <= end : target >= end) { continue; }
            int grown = end;
            for (int row = end + dir; dir > 0 ? row <= target : row >= target; row += dir) {
                if (!openAt(drawing, line, row) || crossedAt(rows, columns, line, row) || landAt(ground, line, row)) { break; }
                grown = row;
            }
            if (grown == end || Math.abs(grown - shore) < PIER_LEAST) { continue; }
            if (low) { from = grown; }
            else { to = grown; }
            if (told) { ContentLog.LOGGER.debug("The street at {} runs its {} end out {} row(s) over open water from {} to {} for a pier", line.at(), low ? "low" : "high", Math.abs(grown - end), end, grown); }
        }
        return from == line.from() && to == line.to() ? line : line.reaching(from, to);
    }

    private static boolean landAt(CityGround ground, Line line, int row) { return ground.floor(line.alongX() ? row : line.middle(), line.alongX() ? line.middle() : row) >= ground.sea() - 1; }

    private static boolean openAt(Drawing drawing, Line line, int row) {
        int cell = drawing.map().cell();
        CityMapDef.Kind[][] grid = drawing.grid();
        int span = line.alongX() ? grid[0].length : grid.length;
        int origin = line.alongX() ? drawing.originX() : drawing.originZ();
        boolean alongX = line.alongX();
        if (row < CityPlan.windowOf(CityPlan.districtOf(origin, alongX), alongX) || row >= CityPlan.windowOf(CityPlan.districtOf(origin + span * cell - 1, alongX) + 1, alongX)) { return false; }
        int index = Math.floorDiv(row - origin, cell);
        if (index < 0 || index >= span) { return true; }
        int fixed = Math.floorDiv(line.middle() - (line.alongX() ? drawing.originZ() : drawing.originX()), cell);
        return fixed < 0 || fixed >= (line.alongX() ? grid.length : grid[0].length) || drawing.kind(fixed, index, line.alongX()) == CityMapDef.Kind.OPEN;
    }

    private static boolean crossedAt(List<Line> rows, List<Line> columns, Line line, int row) {
        for (List<Line> lines : List.of(rows, columns)) {
            for (Line other : lines) {
                if (other.equals(line)) { continue; }
                int minX = other.alongX() ? other.from() : other.at();
                int maxX = other.alongX() ? other.to() : other.last();
                int minZ = other.alongX() ? other.at() : other.from();
                int maxZ = other.alongX() ? other.last() : other.to();
                int fromX = line.alongX() ? row : line.at() - 1;
                int toX = line.alongX() ? row : line.last() + 1;
                int fromZ = line.alongX() ? line.at() - 1 : row;
                int toZ = line.alongX() ? line.last() + 1 : row;
                if (maxX >= fromX && minX <= toX && maxZ >= fromZ && minZ <= toZ) { return true; }
            }
        }
        return false;
    }

    @Nullable private static JsonObject keysAlong(Drawing drawing, int fixed, int at, int end, int other, boolean alongX) {
        for (int index = at; index <= end; index++) {
            boolean crossed = fixed > 0 && carries(drawing.kind(fixed - 1, index, alongX)) || fixed + 1 < other && carries(drawing.kind(fixed + 1, index, alongX));
            if (crossed) { continue; }
            JsonObject keys = drawing.map().keysOf(drawing.mark(fixed, index, alongX));
            if (keys != null) { return keys; }
        }
        return null;
    }

    private static boolean solitary(CityMapDef.Kind[][] grid, int z, int x, int deep) { return (z == 0 || bare(grid[z - 1][x])) && (z + 1 >= deep || bare(grid[z + 1][x])); }

    private static boolean alleyAlone(CityMapDef.Kind[][] grid, int z, int x, int deep) { return (z == 0 || grid[z - 1][x] != CityMapDef.Kind.ALLEY) && (z + 1 >= deep || grid[z + 1][x] != CityMapDef.Kind.ALLEY); }

    static boolean growthless(CityMapDef.Kind[][] grid) {
        for (CityMapDef.Kind[] row : grid) {
            for (CityMapDef.Kind kind : row) {
                if (kind == CityMapDef.Kind.GROW) { return false; }
            }
        }
        return true;
    }

    private static boolean bare(CityMapDef.Kind kind) { return !carries(kind); }

    static boolean carries(CityMapDef.Kind kind) { return kind == CityMapDef.Kind.PLAZA || streetLike(kind); }

    private static boolean streetLike(CityMapDef.Kind kind) { return kind == CityMapDef.Kind.STREET || kind == CityMapDef.Kind.JUNCTION || kind == CityMapDef.Kind.BULB || kind == CityMapDef.Kind.ELEVATED; }
}
