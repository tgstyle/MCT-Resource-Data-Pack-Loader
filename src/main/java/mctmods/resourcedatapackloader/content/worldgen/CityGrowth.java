package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.def.CityMapDef;
import mctmods.resourcedatapackloader.content.def.VillageDef;
import mctmods.resourcedatapackloader.content.worldgen.CityCulDeSacs.Held;
import mctmods.resourcedatapackloader.content.worldgen.CityCulDeSacs.Hold;
import mctmods.resourcedatapackloader.content.worldgen.CityCulDeSacs.StreetGrade;
import mctmods.resourcedatapackloader.content.worldgen.CityLayout.Drawing;
import mctmods.resourcedatapackloader.content.worldgen.CityPlan.Court;
import mctmods.resourcedatapackloader.content.worldgen.CityPlan.End;
import mctmods.resourcedatapackloader.content.worldgen.CityPlan.Junction;
import mctmods.resourcedatapackloader.content.worldgen.CityPlan.Line;
import mctmods.resourcedatapackloader.content.worldgen.CityPlan.Plot;
import mctmods.resourcedatapackloader.content.worldgen.CityPlan.Rail;
import mctmods.resourcedatapackloader.content.worldgen.CityPlan.Town;
import mctmods.resourcedatapackloader.content.worldgen.CityPlanPlots.Tally;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.util.RandomSource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Set;
import javax.annotation.Nullable;

final class CityGrowth {
    static final Map<Long, City> CITIES = new ConcurrentHashMap<>();
    private static final int RELIEF = 6;
    private static final int MARCH = 112;
    private static final int PER_MARCH = 96;
    static final int BLOCK_LEAST = 13;
    static final int FEWEST_PIECES = 2;
    static final Map<Long, Growth> MAP_GROWTH = new ConcurrentHashMap<>();
    static final Map<Long, Boolean> MAP_SMALL = new ConcurrentHashMap<>();

    record City(int centerX, int centerZ, Set<Long> districts) {
        boolean has(int districtX, int districtZ) { return districts.contains(CityPlan.packed(districtX, districtZ)); }
    }

    record Growth(List<Line> alleys, List<Plot> plots, List<Court> courts, List<Line> ties, Map<Long, CityPlan> districts, int districtPlots) {}

    private CityGrowth() {}

    static int pieces(int plots, int wells) { return plots + wells; }

    static City grow(CityGround ground, int centerX, int centerZ, int spacing) {
        int least = ContentVillages.plotsLeast();
        int most = ContentVillages.plotsMost();
        int reach = (spacing / 2 - 1) / 2;
        int march = march();
        int[] cross = CityDistricts.crossOf(centerX, centerZ);
        int[] first = CityDistricts.plazaCenter(centerX, centerZ, cross);
        Set<Long> held = new LinkedHashSet<>();
        int total = 0;
        int districts = 0;
        int far = 0;
        int rough = 0;
        int railed = 0;
        List<Rail> lines = CityRails.placed(ground.seed(), CityPlanTowns.protoTown(ground, centerX, centerZ));
        rings:
        for (int ring = 0; ring <= reach; ring++) {
            for (int[] cell : ring(centerX, centerZ, ring)) {
                if (ring > 0 && least == 0) { break rings; }
                if (ring > 0) {
                    int[] at = CityDistricts.plazaCenter(cell[0], cell[1], cross);
                    long awayX = at[0] - first[0];
                    long awayZ = at[1] - first[1];
                    if (awayX * awayX + awayZ * awayZ > (long) march * march) {
                        far++;
                        continue;
                    }
                    if (rough(ground, at[0], at[1])) {
                        rough++;
                        continue;
                    }
                    Rail met = railedPlaza(lines, at);
                    if (met != null) {
                        ContentLog.LOGGER.debug("The district at {}, {} of the city around district {}, {} gives way to the railway line at {} {}, which crosses its plaza at {}, {}, so no well is settled there", CityPlan.windowOf(cell[0], true), CityPlan.windowOf(cell[1], false), centerX, centerZ, met.alongX() ? "z" : "x", met.middle(), at[0], at[1]);
                        railed++;
                        continue;
                    }
                }
                CityPlan plan = CityPlanTowns.plan(ground, cell[0], cell[1], new int[] {centerX, centerZ});
                int plots = plan == null ? 0 : plan.plots().size();
                if (most > 0 && total + plots > most) { break rings; }
                held.add(CityPlan.packed(cell[0], cell[1]));
                total += plots;
                districts++;
                if (least > 0 && total >= least) { break rings; }
            }
        }
        if (far + rough + railed > 0) { ContentLog.LOGGER.debug("The city around district {}, {} passes over {} district(s) farther than {} blocks from its first well, {} on ground with more than {} blocks of relief or under the water line and {} whose plaza a railway line of the city crosses", centerX, centerZ, far, march, rough, RELIEF, railed); }
        if (pieces(total, districts) <= FEWEST_PIECES) {
            ContentLog.LOGGER.info("The city around district {}, {} stands at {} piece(s) beside its streets once grown, two or fewer, so it is dropped as too small", centerX, centerZ, pieces(total, districts));
            return new City(centerX, centerZ, Set.of());
        }
        ContentLog.LOGGER.info("The city around district {}, {} grows {} district(s) to {} plot(s) against the asked minimum of {}{}", centerX, centerZ, districts, total, least, most > 0 ? " and the ceiling of " + most : "");
        return new City(centerX, centerZ, held);
    }

    static int march() {
        int least = ContentVillages.plotsLeast();
        int wide = Math.max(BLOCK_LEAST, ContentVillages.largestPlot());
        double filled = (double) least / PER_MARCH * ((double) (wide * wide) / (BLOCK_LEAST * BLOCK_LEAST));
        if (filled <= 1.0) { return MARCH; }
        return (int) (MARCH * Math.sqrt(filled));
    }

    @Nullable private static Rail railedPlaza(List<Rail> rails, int[] at) {
        int keep = CityPlan.plazaReach() + CityPlan.WELL_HALF;
        for (Rail rail : rails) {
            if (rail.subway()) { continue; }
            int across = rail.alongX() ? at[1] : at[0];
            if (rail.last() >= across - keep && rail.at() <= across + keep) { return rail; }
        }
        return null;
    }

    static boolean rough(CityGround ground, int x, int z) {
        int reach = CityPlan.plazaReach();
        int sea = ground.sea();
        int lowest = Integer.MAX_VALUE;
        int highest = Integer.MIN_VALUE;
        int[] marks = {-2 - reach, 0, 3 + reach};
        for (int dx : marks) {
            for (int dz : marks) {
                int found = ground.surface(x + dx, z + dz);
                if (found < sea - 1) { return true; }
                lowest = Math.min(lowest, found);
                highest = Math.max(highest, found);
            }
        }
        return highest - lowest > RELIEF;
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

    @Nullable static CityPlan grownDistrict(CityGround ground, int districtX, int districtZ, CityMapDef map, int reachX, int reachZ) {
        int spacing = CityPlan.spacing();
        if (ContentVillages.plotsLeast() <= 0 || CityLayout.growthless(map.kinds(map.marks(Rotation.NONE)))) { return null; }
        int grown = march() / CityPlan.district() + 2;
        for (int offZ = -reachZ - grown; offZ <= reachZ + grown; offZ++) {
            for (int offX = -reachX - grown; offX <= reachX + grown; offX++) {
                int cityX = districtX + offX;
                int cityZ = districtZ + offZ;
                if (spacing > 1 && CityLayout.elsewhere(ground, cityX, cityZ)) { continue; }
                Town town = CityLayout.mapTown(ground, map, cityX, cityZ, spacing > 1);
                if (town == null || tooSmall(ground, map, cityX, cityZ, spacing > 1, town)) { continue; }
                Growth growth = mapGrowth(ground, map, cityX, cityZ, spacing > 1, town);
                CityPlan district = growth.districts().get(CityPlan.packed(districtX, districtZ));
                if (district != null) { return district.grownFrom(town, growth.ties()); }
            }
        }
        return null;
    }

    static boolean tooSmall(CityGround ground, CityMapDef map, int cityX, int cityZ, boolean centered, Town town) {
        Boolean known = MAP_SMALL.get(town.key());
        if (known != null) { return known; }
        Growth growth = mapGrowth(ground, map, cityX, cityZ, centered, town);
        int pieces = pieces(growth.plots().size() + growth.districtPlots(), Math.max(1, CityLayout.mapWells(town).size()) + growth.districts().size());
        boolean small = pieces <= FEWEST_PIECES;
        if (MAP_SMALL.putIfAbsent(town.key(), small) == null && small) { ContentLog.LOGGER.info("The drawn city at {}, {} stands at {} piece(s) beside its streets once grown, two or fewer, so it is dropped as too small", town.wellX(), town.wellZ(), pieces); }
        return small;
    }

    static Growth mapGrowth(CityGround ground, CityMapDef map, int cityX, int cityZ, boolean centered, Town town) {
        Growth known = MAP_GROWTH.get(town.key());
        if (known != null) { return known; }
        Growth made = grown(ground, map, cityX, cityZ, centered, town);
        Growth raced = MAP_GROWTH.putIfAbsent(town.key(), made);
        return raced == null ? made : raced;
    }

    private static Growth grown(CityGround ground, CityMapDef map, int cityX, int cityZ, boolean centered, Town town) {
        long seed = ground.seed();
        Rotation turn = CityLayout.turnOf(seed, cityX, cityZ);
        char[][] marks = map.marks(turn);
        CityMapDef.Kind[][] grid = map.kinds(marks);
        int deep = grid.length;
        int wide = grid[0].length;
        int[] origin = CityLayout.mapOrigin(map, grid, cityX, cityZ, centered);
        int[][] groups = new int[deep][wide];
        CityLayout.lifts(grid, marks, map, origin[0], origin[1], groups);
        Drawing drawing = new Drawing(map, grid, marks, groups, origin[0], origin[1], map.bulbHinted());
        List<Line> rows = new ArrayList<>();
        List<Line> columns = new ArrayList<>();
        for (int z = 0; z < deep; z++) { CityLayout.runs(drawing, z, wide, deep, true, rows); }
        for (int x = 0; x < wide; x++) { CityLayout.runs(drawing, x, deep, wide, false, columns); }
        int runRows = rows.size();
        int runColumns = columns.size();
        CityLayout.junctions(drawing, rows, columns);
        CityLayout.settle(ground, town, rows, rows.size(), columns, drawing.hinted());
        CityLayout.settle(ground, town, columns, columns.size(), rows, drawing.hinted());
        CityEnds.closeEnds(ground, town, rows, columns, drawing.hinted(), true);
        CityLayout.pierOut(ground, drawing, rows, columns, true);
        List<Line> order = new ArrayList<>();
        for (Line line : rows.subList(0, runRows)) { if (!line.alley()) { order.add(line); } }
        for (Line line : columns.subList(0, runColumns)) { if (!line.alley()) { order.add(line); } }
        order.addAll(rows.subList(runRows, rows.size()));
        order.addAll(columns.subList(runColumns, columns.size()));
        List<Line> lines = new ArrayList<>(rows);
        lines.addAll(columns);
        List<Plot> drawnPlots = CityPlanPlots.mapPlots(seed, map, marks, grid, origin[0], origin[1], rows, columns);
        List<Junction> plazas = CityLayout.mapPlazas(map, grid, origin[0], origin[1], rows, columns, CityPlan.fullWidth());
        List<Rail> rails = CityRails.placed(seed, town);
        int block = CityPlanPlots.townBlock(seed, town);
        List<VillageDef> choices = CityPlanPlots.choices(block, ground.villageStyle(town.wellX(), town.wellZ()));
        RandomSource roll = RandomSource.create(seed ^ CityPlan.SALT ^ 0x6C0D5L ^ town.hash());
        int most = ContentVillages.plotsMost();
        List<Plot> found = new ArrayList<>(drawnPlots);
        if (CityLayout.growthless(grid)) {
            if (CityPlanPlots.backRow() && !choices.isEmpty()) { CityPlanPlots.backRows(roll, choices, found, rails, lines, plazas, List.copyOf(found), origin[0], origin[0] + wide * map.cell() - 1, origin[1], origin[1] + deep * map.cell() - 1, most); }
            return new Growth(List.of(), List.copyOf(found), List.of(), List.of(), Map.of(), 0);
        }
        List<Held> held = new ArrayList<>();
        for (Line line : lines) { held.add(CityCulDeSacs.held(line)); }
        for (Plot plot : drawnPlots) { held.add(new Held(plot.fromX(), plot.fromZ(), plot.toX(), plot.toZ(), Hold.PLOT, true)); }
        for (BoundingBox well : CityLayout.mapWells(town)) {
            held.add(new Held(well.minX(), well.minZ(), well.maxX(), well.maxZ(), Hold.FIXED, true));
        }
        for (Rail rail : rails) {
            if (rail.subway()) { continue; }
            held.add(rail.alongX() ? new Held(rail.from(), rail.at(), rail.to(), rail.last(), Hold.FIXED, true) : new Held(rail.at(), rail.from(), rail.last(), rail.to(), Hold.FIXED, false));
        }
        List<Line> refusing = new ArrayList<>(lines);
        List<Court> courts = new ArrayList<>();
        Map<Line, StreetGrade> grades = new HashMap<>();
        int seated = 0;
        for (Line street : order) {
            for (boolean low : new boolean[] {true, false}) {
                if ((low ? street.endsLow() : street.endsHigh()) != End.COURT) { continue; }
                int end = low ? street.from() : street.to();
                StreetGrade grade = grades.computeIfAbsent(street, line -> CityCulDeSacs.streetGrade(ground, line, lines));
                Court court = CityCulDeSacs.court(ground, street, low, held, grade.profile()[end - grade.start()]);
                courts.add(court);
                if (court.room() <= 0) { continue; }
                Held box = CityCulDeSacs.courtBox(street, end, court.dir(), court.room(), court.stem());
                CityCulDeSacs.makeWay(found, held, box);
                held.add(box);
                Line way = CityCulDeSacs.courtLine(street, end, court.dir(), court.room(), court.stem());
                refusing.add(way);
                seated++;
            }
        }
        if (CityPlanPlots.backRow() && !choices.isEmpty()) { CityPlanPlots.backRows(roll, choices, found, rails, refusing, plazas, List.copyOf(found), origin[0], origin[0] + wide * map.cell() - 1, origin[1], origin[1] + deep * map.cell() - 1, most); }
        int behind = found.size() - drawnPlots.size();
        List<BoundingBox> standing = new ArrayList<>();
        for (Plot plot : found) { standing.add(new BoundingBox(plot.fromX(), 0, plot.fromZ(), plot.toX(), 0, plot.toZ())); }
        standing.addAll(CityLayout.mapWells(town));
        for (Rail rail : rails) {
            if (!rail.subway()) { standing.add(rail.alongX() ? new BoundingBox(rail.from(), 0, rail.at(), rail.to(), 0, rail.last()) : new BoundingBox(rail.at(), 0, rail.from(), rail.last(), 0, rail.to())); }
        }
        for (Court court : courts) {
            if (court.room() <= 0) { continue; }
            Held box = CityCulDeSacs.courtBox(court.street(), court.end(), court.dir(), court.room(), court.stem());
            standing.add(new BoundingBox(box.minX(), 0, box.minZ(), box.maxX(), 0, box.maxZ()));
        }
        CityMapDistricts.Grown districts = CityMapDistricts.grow(ground, town, lines, standing, rails, found.size(), CityMapDistricts.others(ground, map, cityX, cityZ, town));
        List<Line> beyond = new ArrayList<>(districts.ties());
        for (CityPlan district : districts.districts().values()) {
            beyond.addAll(district.alongX());
            beyond.addAll(district.alongZ());
        }
        refusing.addAll(beyond);
        found.addAll(districts.plots());
        Tally open = new Tally(Integer.MAX_VALUE);
        if (!choices.isEmpty()) {
            for (Court court : courts) {
                if (court.room() > 0) { CityPlanPlots.flank(roll, choices, open, found, rails, refusing, plazas, court.line(), block, most); }
            }
        }
        int housed = found.size() - drawnPlots.size() - behind - districts.plots().size();
        List<Line> alleys = CityAlleys.alleyFill(ground, roll, choices, open, found, rails, refusing, plazas, lines, beyond, courts, grades, town, block, most);
        found.removeAll(districts.plots());
        ContentLog.LOGGER.debug("The drawn city at {}, {} grows {} plot(s) behind its front plots, {} district(s) with {} plot(s) and {} tie street(s) beside it, lays {} cul-de-sac(s) with {} house(s) seated off them and {} alley(s) with {} house(s) along them", origin[0], origin[1], behind, districts.districts().size(), districts.plots().size(), districts.ties().size(), seated, housed, alleys.size(), found.size() - drawnPlots.size() - behind - housed);
        return new Growth(List.copyOf(alleys), List.copyOf(found), List.copyOf(courts), districts.ties(), districts.districts(), districts.plots().size());
    }
}
