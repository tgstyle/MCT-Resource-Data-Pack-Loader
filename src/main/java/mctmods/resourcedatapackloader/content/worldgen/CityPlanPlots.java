package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.content.def.CityMapDef;
import mctmods.resourcedatapackloader.content.def.PickDef;
import mctmods.resourcedatapackloader.content.def.VillageDef;
import mctmods.resourcedatapackloader.content.worldgen.CityPlan.Junction;
import mctmods.resourcedatapackloader.content.worldgen.CityPlan.Line;
import mctmods.resourcedatapackloader.content.worldgen.CityPlan.Plot;
import mctmods.resourcedatapackloader.content.worldgen.CityPlan.Rail;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Hashes;

import net.minecraft.util.RandomSource;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import javax.annotation.Nullable;

final class CityPlanPlots {
    static final int GAP = 1;
    static final int SETBACK = 1;
    private static final Set<String> WARNED = new LinkedHashSet<>();

    static final class Tally {
        private final int limit;
        private int fronts;

        Tally(int limit) { this.limit = limit; }

        boolean full() { return fronts >= limit; }

        void front() { fronts++; }
    }

    private record Road(List<Run> runs, List<Road> branches) {
        static Road arm() { return new Road(new ArrayList<>(), new ArrayList<>()); }
    }

    private record Quarter(int fromX, int toX, int fromZ, int toZ, List<Run> lanes) {
        List<Plot> fronting(List<Plot> fronts) {
            List<Plot> held = new ArrayList<>();
            for (Plot plot : fronts) {
                int x = (plot.fromX() + plot.toX()) / 2;
                int z = (plot.fromZ() + plot.toZ()) / 2;
                if (x >= fromX && x <= toX && z >= fromZ && z <= toZ) { held.add(plot); }
            }
            return held;
        }
    }

    private record Run(Line street, boolean lower, int fromX, int toX, int fromZ, int toZ) {}

    private CityPlanPlots() {}

    static int townBlock(long seed, CityPlan.Town town) { return districtBlock(RandomSource.create(seed ^ CityPlan.SALT ^ 0xB10C5L ^ town.hash())); }

    static int districtBlock(RandomSource roll) {
        List<PickDef> picks = new ArrayList<>();
        for (String entry : ContentControl.list(ContentControl.VILLAGES, "villageBlockSizes", Config.worldgen.villageBlockSizes())) {
            String text = entry.trim().toLowerCase(Locale.ROOT);
            if (text.isEmpty()) { continue; }
            int at = text.indexOf('=');
            if (at <= 0 || at == text.length() - 1) {
                if (WARNED.add(entry)) { ContentLog.LOGGER.error("villageBlockSizes entry '{}' is not written as size=weight, ignoring it", entry); }
                continue;
            }
            int weight;
            try { weight = Integer.parseInt(text.substring(at + 1).trim()); }
            catch (NumberFormatException notNumber) {
                if (WARNED.add(entry)) { ContentLog.LOGGER.error("villageBlockSizes entry '{}' gives a weight that is not a whole number, ignoring the entry", entry); }
                continue;
            }
            if (weight < 1) {
                if (WARNED.add(entry)) { ContentLog.LOGGER.error("villageBlockSizes entry '{}' asks for a weight below 1, ignoring the entry", entry); }
                continue;
            }
            picks.add(new PickDef(text.substring(0, at).trim(), weight));
        }
        String picked = PickDef.pick(picks, roll);
        if (picked == null) { return ContentVillages.largestPlot(); }
        try { return Math.max(CityGrowth.BLOCK_LEAST, Integer.parseInt(picked)); }
        catch (NumberFormatException notNumber) {
            if (WARNED.add(picked)) { ContentLog.LOGGER.error("villageBlockSizes entry '{}' is not a whole number of blocks, so the district keeps the largest plot as its block", picked); }
            return ContentVillages.largestPlot();
        }
    }

    static List<VillageDef> choices(int block, String type) {
        List<VillageDef> found = new ArrayList<>();
        for (VillageDef def : ContentVillages.allowed()) {
            if (Math.max(def.width(), def.depth()) > block || ContentVillages.vanillaHouse(def) && !def.structure().contains("village/" + type + "/")) { continue; }
            found.add(def);
        }
        return found;
    }

    static boolean backRow() { return ContentControl.flag(ContentControl.VILLAGES, "villagePlotsBackRow", Config.worldgen.villagePlotsBackRow()); }

    static List<Plot> radialPlots(RandomSource roll, List<VillageDef> choices, Tally tally, int originX, int originZ, int size, Line row, Line column, List<Line> alongX, List<Line> alongZ, List<Rail> rails, List<Junction> plazas) {
        List<Plot> found = new ArrayList<>();
        if (choices.isEmpty()) { return found; }
        int most = ContentVillages.plotsMost();
        List<Line> lines = new ArrayList<>(alongX);
        lines.addAll(alongZ);
        int keep = CityPlan.plazaReach() + CityPlan.WELL_HALF;
        int centerX = column.middle();
        int centerZ = row.middle();
        Road[] rowArms = {Road.arm(), Road.arm()};
        Road[] columnArms = {Road.arm(), Road.arm()};
        List<Quarter> quarters = new ArrayList<>();
        for (int qx = -1; qx <= 1; qx += 2) {
            for (int qz = -1; qz <= 1; qz += 2) {
                int fromX = qx < 0 ? originX : column.last() + 1;
                int toX = qx < 0 ? column.at() - 1 : originX + size - 1;
                int fromZ = qz < 0 ? originZ : row.last() + 1;
                int toZ = qz < 0 ? row.at() - 1 : originZ + size - 1;
                Road rowArm = rowArms[qx < 0 ? 0 : 1];
                Road columnArm = columnArms[qz < 0 ? 0 : 1];
                int rowFromX = qx < 0 ? fromX : Math.max(fromX, centerX + keep + 1);
                int rowToX = qx < 0 ? Math.min(toX, centerX - keep - 1) : toX;
                if (rowToX >= rowFromX) { rowArm.runs().add(new Run(row, qz > 0, rowFromX, rowToX, fromZ, toZ)); }
                int columnFromZ = qz < 0 ? fromZ : Math.max(fromZ, centerZ + keep + 1);
                int columnToZ = qz < 0 ? Math.min(toZ, centerZ - keep - 1) : toZ;
                if (columnToZ >= columnFromZ) { columnArm.runs().add(new Run(column, qx > 0, fromX, toX, columnFromZ, columnToZ)); }
                for (Line street : alongX) {
                    if (street.alley() || street.equals(row) || street.at() < fromZ || street.last() > toZ) { continue; }
                    columnArm.branches().add(new Road(List.of(new Run(street, false, fromX, toX, fromZ, street.at() - 1), new Run(street, true, fromX, toX, street.last() + 1, toZ)), List.of()));
                }
                for (Line street : alongZ) {
                    if (street.alley() || street.equals(column) || street.at() < fromX || street.last() > toX) { continue; }
                    rowArm.branches().add(new Road(List.of(new Run(street, false, fromX, street.at() - 1, fromZ, toZ), new Run(street, true, street.last() + 1, toX, fromZ, toZ)), List.of()));
                }
                List<Run> lanes = new ArrayList<>();
                for (Line alley : alongZ) {
                    if (!alley.alley() || alley.at() < fromX || alley.last() > toX || alley.from() < fromZ || alley.to() > toZ) { continue; }
                    lanes.add(new Run(alley, false, fromX, alley.at() - 1, alley.from(), alley.to()));
                    lanes.add(new Run(alley, true, alley.last() + 1, toX, alley.from(), alley.to()));
                }
                for (Line alley : alongX) {
                    if (!alley.alley() || alley.at() < fromZ || alley.last() > toZ || alley.from() < fromX || alley.to() > toX) { continue; }
                    lanes.add(new Run(alley, false, alley.from(), alley.to(), fromZ, alley.at() - 1));
                    lanes.add(new Run(alley, true, alley.from(), alley.to(), alley.last() + 1, toZ));
                }
                quarters.add(new Quarter(fromX, toX, fromZ, toZ, lanes));
            }
        }
        List<Road> pending = new ArrayList<>(List.of(rowArms[0], rowArms[1], columnArms[0], columnArms[1]));
        while (!pending.isEmpty()) {
            Road road = pending.remove(roll.nextInt(pending.size()));
            seat(roll, choices, tally, found, rails, lines, plazas, road.runs(), area(road.runs()), most);
            pending.addAll(road.branches());
        }
        for (Quarter quarter : quarters) { seat(roll, choices, tally, found, rails, lines, plazas, quarter.lanes(), area(quarter.lanes()), most); }
        int backs = 0;
        if (backRow()) {
            List<Plot> fronts = List.copyOf(found);
            for (Quarter quarter : quarters) { backs += backRows(roll, choices, found, rails, lines, plazas, quarter.fronting(fronts), quarter.fromX(), quarter.toX(), quarter.fromZ(), quarter.toZ(), most); }
        }
        Map<String, Integer> sizes = new TreeMap<>();
        for (Plot plot : found) { sizes.merge(plot.def().width() + "x" + plot.def().depth(), 1, Integer::sum); }
        ContentLog.LOGGER.debug("The district at {}, {} seats {} plot(s) along its streets and alleys, {} of them behind a plot that fronts one, by size {}", originX, originZ, found.size(), backs, sizes);
        return List.copyOf(found);
    }

    private static int area(List<Run> runs) {
        int area = 0;
        for (Run run : runs) { area += Math.max(0, run.toX() - run.fromX() + 1) * Math.max(0, run.toZ() - run.fromZ() + 1); }
        return area;
    }

    static int backRows(RandomSource roll, List<VillageDef> choices, List<Plot> found, List<Rail> rails, List<Line> lines, List<Junction> plazas, List<Plot> fronts, int fromX, int toX, int fromZ, int toZ, int most) {
        List<VillageDef> packs = choices.stream().filter(def -> !ContentVillages.vanillaHouse(def)).toList();
        if (packs.isEmpty()) { return 0; }
        int seated = 0;
        int noRoom = 0;
        int[] refused = new int[4];
        for (Plot front : fronts) {
            if (most > 0 && found.size() >= most) { break; }
            if (ContentVillages.vanillaHouse(front.def())) { continue; }
            boolean alongX = front.street().alongX();
            int wide = alongX ? front.toX() - front.fromX() + 1 : front.toZ() - front.fromZ() + 1;
            int lowAlong = alongX ? fromX : fromZ;
            int highAlong = alongX ? toX : toZ;
            int edge = front.lower() ? (alongX ? front.toZ() : front.toX()) + 1 : (alongX ? front.fromZ() : front.fromX()) - 1;
            int room = front.lower() ? (alongX ? toZ : toX) - edge + 1 : edge - (alongX ? fromZ : fromX) + 1;
            List<VillageDef> left = new ArrayList<>(packs);
            Plot back = null;
            boolean any = false;
            while (back == null) {
                VillageDef def = room <= 0 ? null : ContentVillages.pick(left, roll, highAlong - lowAlong + 1, room);
                if (def == null) { break; }
                any = true;
                int least = (alongX ? front.fromX() : front.fromZ()) + (wide - def.width()) / 2;
                int near = front.lower() ? edge : edge - def.depth() + 1;
                int over = near + def.depth() - 1;
                Plot plot = alongX ? new Plot(least, near, least + def.width() - 1, over, front.street(), front.lower(), def, true)
                                   : new Plot(near, least, over, least + def.width() - 1, front.street(), front.lower(), def, true);
                int why = least < lowAlong || least + def.width() - 1 > highAlong ? 0 : refusal(found, rails, lines, plazas, plot);
                if (why < 0) { back = plot; }
                else {
                    refused[why]++;
                    left.remove(def);
                }
            }
            if (back != null) {
                found.add(back);
                seated++;
            }
            else if (!any) { noRoom++; }
        }
        if (!fronts.isEmpty()) { ContentLog.LOGGER.debug("Behind {} front plot(s): {} seated, {} with no room behind, tries refused {} on a plot, {} on a railway, {} on a street, {} beside a well", fronts.size(), seated, noRoom, refused[0], refused[1], refused[2], refused[3]); }
        return seated;
    }

    private static void seat(RandomSource roll, List<VillageDef> choices, Tally tally, List<Plot> found, List<Rail> rails, List<Line> lines, List<Junction> plazas, List<Run> runs, int area, int most) {
        if (runs.isEmpty()) { return; }
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
            if ((most > 0 && found.size() >= most) || tally.full()) { break; }
            Plot plot = firstFit(def, runs, found, rails, lines, plazas);
            if (plot == null) { continue; }
            found.add(plot);
            tally.front();
            bagged++;
        }
        int before = found.size();
        for (Run run : runs) { frontage(roll, choices, tally, found, rails, lines, plazas, run, most); }
        ContentLog.LOGGER.debug("A block of {} square block(s) rolled {} plot(s) and seated {} of them, and the fill after seated {} more", area, rolledCount, bagged, found.size() - before);
    }

    @Nullable private static Plot firstFit(VillageDef def, List<Run> runs, List<Plot> found, List<Rail> rails, List<Line> lines, List<Junction> plazas) {
        for (Run run : runs) {
            boolean alongX = run.street().alongX();
            int least = alongX ? run.fromX() : run.fromZ();
            int last = alongX ? run.toX() : run.toZ();
            int deep = alongX ? run.toZ() - run.fromZ() + 1 : run.toX() - run.fromX() + 1;
            if (def.depth() + SETBACK > deep) { continue; }
            for (int at = least; at + def.width() - 1 <= last; at++) {
                Plot plot = front(run.street(), run.lower(), run.fromX(), run.toX(), run.fromZ(), run.toZ(), at, def);
                if (refusal(found, rails, lines, plazas, plot) < 0) { return plot; }
            }
        }
        return null;
    }

    private static void frontage(RandomSource roll, List<VillageDef> choices, Tally tally, List<Plot> found, List<Rail> rails, List<Line> lines, List<Junction> plazas, Run run, int most) {
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
        int[] refused = new int[4];
        while (at <= last) {
            if ((most > 0 && found.size() >= most) || tally.full()) { break; }
            List<VillageDef> left = new ArrayList<>(choices);
            Plot plot = null;
            while (plot == null) {
                VillageDef def = ContentVillages.pick(left, roll, last - at + 1, deep);
                if (def == null) { break; }
                Plot tried = front(street, lower, fromX, toX, fromZ, toZ, at, def);
                int why = refusal(found, rails, lines, plazas, tried);
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
            tally.front();
            seated++;
            at += plot.def().width() + GAP;
        }
        ContentLog.LOGGER.debug("Along the {} at {}: {} front plot(s) seated to fill, {} block(s) stepped over, tries refused {} on a plot, {} on a railway, {} on a street, {} beside a well", street.alley() ? "alley" : "street", street.at(), seated, stepped, refused[0], refused[1], refused[2], refused[3]);
    }

    private static Plot front(Line street, boolean lower, int fromX, int toX, int fromZ, int toZ, int at, VillageDef def) {
        boolean alongX = street.alongX();
        int near = lower ? (alongX ? fromZ : fromX) + SETBACK : (alongX ? toZ : toX) - SETBACK - def.depth() + 1;
        int over = near + def.depth() - 1;
        return alongX ? new Plot(at, near, at + def.width() - 1, over, street, lower, def, false) : new Plot(near, at, over, at + def.width() - 1, street, lower, def, false);
    }

    private static int refusal(List<Plot> found, List<Rail> rails, List<Line> lines, List<Junction> plazas, Plot plot) {
        if (!fits(found, plot)) { return 0; }
        if (onRail(rails, plot)) { return 1; }
        if (onLine(lines, plot, GAP)) { return 2; }
        return besideWell(plazas, plot) ? 3 : -1;
    }

    static boolean besideWell(List<Junction> plazas, Plot plot) {
        int reach = CityPlan.WELL_HALF + CityPlan.plazaReach() + 1;
        for (Junction plaza : plazas) {
            int x = plaza.alongZ().middle();
            int z = plaza.alongX().middle();
            if (plot.toX() >= x - reach && plot.fromX() <= x + reach && plot.toZ() >= z - reach && plot.fromZ() <= z + reach) { return true; }
        }
        return false;
    }

    private static void drop(List<VillageDef> left, VillageDef failed) { left.removeIf(def -> def.width() >= failed.width() && def.depth() >= failed.depth()); }

    static boolean onRail(List<Rail> rails, Plot plot) {
        for (Rail rail : rails) {
            if (rail.subway()) { continue; }
            if (rail.alongX() ? plot.fromZ() <= rail.last() && plot.toZ() >= rail.at() : plot.fromX() <= rail.last() && plot.toX() >= rail.at()) { return true; }
        }
        return false;
    }

    static boolean onLine(List<Line> lines, Plot plot, int gap) {
        for (Line line : lines) {
            boolean across = line.alongX() ? plot.fromZ() <= line.last() + gap && plot.toZ() >= line.at() - gap : plot.fromX() <= line.last() + gap && plot.toX() >= line.at() - gap;
            boolean along = line.alongX() ? plot.fromX() <= line.to() + gap && plot.toX() >= line.from() - gap : plot.fromZ() <= line.to() + gap && plot.toZ() >= line.from() - gap;
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

    static void flank(RandomSource roll, List<VillageDef> choices, Tally tally, List<Plot> found, List<Rail> rails, List<Line> lines, List<Junction> plazas, Line way, int block, int most) {
        int deep = block + SETBACK;
        int length = way.to() - way.from() + 1;
        for (boolean lower : new boolean[] {false, true}) {
            int near = lower ? way.last() + 1 : way.at() - deep;
            int far = lower ? way.last() + deep : way.at() - 1;
            int fromX = way.alongX() ? way.from() : near;
            int toX = way.alongX() ? way.to() : far;
            int fromZ = way.alongX() ? near : way.from();
            int toZ = way.alongX() ? far : way.to();
            for (int step = roll.nextInt(5); step < length - 8; step += 2 + roll.nextInt(5)) {
                if ((most > 0 && found.size() >= most) || tally.full()) { return; }
                List<VillageDef> left = new ArrayList<>(choices);
                Plot plot = null;
                while (plot == null) {
                    VillageDef def = ContentVillages.pick(left, roll, Integer.MAX_VALUE, deep);
                    if (def == null) { break; }
                    Plot tried = front(way, lower, fromX, toX, fromZ, toZ, way.from() + step, def);
                    if (refusal(found, rails, lines, plazas, tried) < 0) { plot = tried; }
                    else { drop(left, def); }
                }
                if (plot == null) { continue; }
                found.add(plot);
                tally.front();
                step += Math.max(plot.def().width(), plot.def().depth());
            }
        }
    }

    static List<Plot> mapPlots(long seed, CityMapDef map, char[][] marks, CityMapDef.Kind[][] grid, int originX, int originZ, List<Line> alongX, List<Line> alongZ) {
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
                String picked = PickDef.pick(held.picks(), RandomSource.create(Hashes.mix(seed ^ CityPlan.SALT, cellX, 11, cellZ)));
                VillageDef def = picked == null ? null : ContentVillages.byKey(picked);
                if (def == null) {
                    if (picked != null && WARNED.add(picked)) { ContentLog.LOGGER.error("City map {} names plot '{}', which no pack provides, so that cell stays open", map.key(), picked); }
                    continue;
                }
                Plot facing = facingFor(grid, x, z, cellX, cellZ, cell, alongX, alongZ, def);
                if (facing == null) {
                    ContentLog.LOGGER.debug("City map {} cell {}, {} plot {} has no street beside it and no street along z to face east toward, so the cell stays open", map.key(), x, z, picked);
                    continue;
                }
                if (fits(found, facing) && !onLine(alongX, facing, 0) && !onLine(alongZ, facing, 0)) { found.add(facing); }
                else { ContentLog.LOGGER.debug("City map {} cell {}, {} plot {} would overlap what is already laid, so the cell stays open", map.key(), x, z, picked); }
            }
        }
        return List.copyOf(found);
    }

    @Nullable private static Plot facingFor(CityMapDef.Kind[][] grid, int x, int z, int cellX, int cellZ, int cell, List<Line> alongX, List<Line> alongZ, VillageDef def) {
        int[][] steps = {{0, -1}, {0, 1}, {1, 0}, {-1, 0}};
        int midX = cellX + cell / 2;
        int midZ = cellZ + cell / 2;
        for (int pass = 0; pass < 2; pass++) {
            for (int[] step : steps) {
                int nextX = x + step[0];
                int nextZ = z + step[1];
                if (nextZ < 0 || nextZ >= grid.length || nextX < 0 || nextX >= grid[nextZ].length) { continue; }
                CityMapDef.Kind kind = grid[nextZ][nextX];
                if (pass == 0 ? !CityLayout.carries(kind) : kind != CityMapDef.Kind.ALLEY) { continue; }
                boolean rows = step[1] != 0;
                int from = rows ? cellZ + step[1] * cell : cellX + step[0] * cell;
                Line street = null;
                for (Line line : rows ? alongX : alongZ) {
                    if (line.middle() >= from && line.middle() < from + cell && line.covers(rows ? midX : midZ)) {
                        street = line;
                        break;
                    }
                }
                if (street == null) { continue; }
                boolean lower = rows ? step[1] < 0 : step[0] < 0;
                int wide = rows ? def.width() : def.depth();
                int deep = rows ? def.depth() : def.width();
                int fromX = midX - wide / 2;
                int fromZ = midZ - deep / 2;
                return new Plot(fromX, fromZ, fromX + wide - 1, fromZ + deep - 1, street, lower, def, false);
            }
        }
        Line nearest = null;
        for (Line line : alongZ) {
            if (nearest == null || Math.abs(line.middle() - midX) < Math.abs(nearest.middle() - midX)) { nearest = line; }
        }
        if (nearest == null) { return null; }
        int fromX = midX - def.depth() / 2;
        int fromZ = midZ - def.width() / 2;
        return new Plot(fromX, fromZ, fromX + def.depth() - 1, fromZ + def.width() - 1, nearest, true, def, false);
    }

}
