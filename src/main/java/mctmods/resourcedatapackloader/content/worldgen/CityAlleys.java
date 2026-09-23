package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.content.def.VillageDef;
import mctmods.resourcedatapackloader.content.worldgen.CityCulDeSacs.Held;
import mctmods.resourcedatapackloader.content.worldgen.CityCulDeSacs.Hold;
import mctmods.resourcedatapackloader.content.worldgen.CityCulDeSacs.StreetGrade;
import mctmods.resourcedatapackloader.content.worldgen.CityPlan.Court;
import mctmods.resourcedatapackloader.content.worldgen.CityPlan.End;
import mctmods.resourcedatapackloader.content.worldgen.CityPlan.Junction;
import mctmods.resourcedatapackloader.content.worldgen.CityPlan.Line;
import mctmods.resourcedatapackloader.content.worldgen.CityPlan.Plot;
import mctmods.resourcedatapackloader.content.worldgen.CityPlan.Rail;
import mctmods.resourcedatapackloader.content.worldgen.CityPlan.Town;
import mctmods.resourcedatapackloader.content.worldgen.CityPlanPlots.Tally;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Hashes;

import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.util.RandomSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

final class CityAlleys {
    public static final int ALLEY_LEAST = 16;
    public static final int ALLEY_APART = 12;
    private static final int ALLEY_SHORTEST = 14;
    private static final int ALLEY_LONGEST = 42;
    private static final int ALLEY_STEP = 7;
    private static final int ATTACH_GAP = 8;
    public static final int ALLEY_WIDTH = 3;
    private static final long BRANCH_SALT = 0xB4A7C5L;
    private static final int BRANCH_ODDS = 3;

    private CityAlleys() {}

    static void idleAlleys(List<Plot> plots, List<Line> alongX, List<Line> alongZ) {
        int most = ContentVillages.plotsMost();
        if (most <= 0 || plots.size() < most) { return; }
        for (List<Line> lines : List.of(alongX, alongZ)) {
            lines.removeIf(line -> line.alley() && plots.stream().noneMatch(plot -> plot.street().equals(line)));
        }
    }

    static void alleys(RandomSource roll, int originX, int originZ, int size, Line row, Line column, List<Line> alongX, List<Line> alongZ, List<Rail> rails, int block) {
        int chance = Mth.clamp(ContentControl.number(ContentControl.VILLAGES, "villagePathAlleyChance", Config.worldgen.villagePathAlleyChance()), 0, 100);
        int least = Math.max(0, ContentControl.number(ContentControl.VILLAGES, "villagePathMinimumWidth", Config.worldgen.villagePathMinimumWidth()));
        if (chance <= 0 || ALLEY_WIDTH < least) { return; }
        int clear = CityPlan.plazaReach() + CityPlan.WELL_HALF + ALLEY_WIDTH;
        int centerX = column.middle();
        int centerZ = row.middle();
        for (int side = -1; side <= 1; side += 2) {
            for (int end = -1; end <= 1; end += 2) {
                if (roll.nextInt(100) < chance) {
                    int lowX = end < 0 ? originX + ALLEY_APART : centerX + clear;
                    int highX = end < 0 ? centerX - clear - ALLEY_WIDTH : originX + size - ALLEY_APART - ALLEY_WIDTH;
                    int depth = reach(alongX, row, side, side < 0 ? originZ : originZ + size - 1);
                    Line alley = branch(roll, lowX, highX, depth, side < 0 ? row.at() - 1 : row.last() + 1, side, false);
                    if (alley != null && clear(alongZ, alley) && apart(alongZ, alley, column) && !hugsStreet(alongZ, alley, block) && offRail(rails, alley.alongX(), alley.at(), alley.last(), CityPlanPlots.GAP)) {
                        alongZ.add(alley);
                        ContentLog.LOGGER.debug("An alley off the row street at x {} to {} runs z {} to {}", alley.at(), alley.last(), alley.from(), alley.to());
                    }
                }
                if (roll.nextInt(100) < chance) {
                    int lowZ = end < 0 ? originZ + ALLEY_APART : centerZ + clear;
                    int highZ = end < 0 ? centerZ - clear - ALLEY_WIDTH : originZ + size - ALLEY_APART - ALLEY_WIDTH;
                    int depth = reach(alongZ, column, side, side < 0 ? originX : originX + size - 1);
                    Line alley = branch(roll, lowZ, highZ, depth, side < 0 ? column.at() - 1 : column.last() + 1, side, true);
                    if (alley != null && clear(alongX, alley) && apart(alongX, alley, row) && !hugsStreet(alongX, alley, block) && offRail(rails, alley.alongX(), alley.at(), alley.last(), CityPlanPlots.GAP)) {
                        alongX.add(alley);
                        ContentLog.LOGGER.debug("An alley off the column street at z {} to {} runs x {} to {}", alley.at(), alley.last(), alley.from(), alley.to());
                    }
                }
            }
        }
    }

    private static boolean offRail(List<Rail> rails, boolean alongX, int at, int last, int gap) {
        for (Rail rail : rails) {
            if (rail.subway() || rail.alongX() != alongX) { continue; }
            if (last >= rail.at() - gap && at <= rail.last() + gap) { return false; }
        }
        return true;
    }

    private static int reach(List<Line> held, Line street, int side, int edge) {
        int found = side < 0 ? street.at() - edge : edge - street.last();
        for (Line other : held) {
            if (other.alley() || other.equals(street)) { continue; }
            int gap = side < 0 ? street.at() - other.last() - 1 : other.at() - street.last() - 1;
            if (gap >= 0) { found = Math.min(found, gap); }
        }
        return found;
    }

    private static boolean apart(List<Line> held, Line alley, Line cross) {
        for (Line other : held) {
            if (other.alley() || other.equals(cross)) { continue; }
            if (alley.at() <= other.last() + ALLEY_APART && alley.last() >= other.at() - ALLEY_APART) { return false; }
        }
        return true;
    }

    static int branchAt(int cross) {
        int size = CityPlan.district();
        int full = CityPlan.fullWidth();
        return Mth.clamp(Math.floorMod(cross + full / 2 + size / 2, size) - full / 2, 0, size - full);
    }

    static boolean branches(Town town, List<Rail> rails, int band, boolean alongX) {
        int at = CityPlan.windowOf(band, !alongX) + branchAt(town.crossAcross(alongX));
        return Math.floorMod(Hashes.mix(town.hash() ^ BRANCH_SALT, band, alongX ? 1 : 0, 0), BRANCH_ODDS) > 0 && offRail(rails, alongX, at, at + CityPlan.fullWidth() - 1, 0);
    }

    @Nullable private static Line branch(RandomSource roll, int low, int high, int depth, int edge, int side, boolean alongX) {
        if (high < low || depth - ALLEY_APART < ALLEY_LEAST) { return null; }
        int at = low + roll.nextInt(high - low + 1);
        int length = ALLEY_LEAST + roll.nextInt(depth - ALLEY_APART - ALLEY_LEAST + 1);
        int from = side < 0 ? edge - length + 1 : edge;
        int to = side < 0 ? edge : edge + length - 1;
        return new Line(at, ALLEY_WIDTH, true, alongX, from, to, side < 0 ? End.BARE : End.MET, side > 0 ? End.BARE : End.MET);
    }

    static List<Line> alleyFill(CityGround ground, RandomSource roll, List<VillageDef> choices, Tally tally, List<Plot> found, List<Rail> rails, List<Line> refusing, List<Junction> plazas, List<Line> lines, List<Line> beyond, List<Court> courts, Map<Line, StreetGrade> grades, Town town, int block, int most) {
        List<Line> laid = new ArrayList<>();
        if (Mth.clamp(ContentControl.number(ContentControl.VILLAGES, "villagePathAlleyChance", Config.worldgen.villagePathAlleyChance()), 0, 100) <= 0) { return laid; }
        List<Held> pieces = new ArrayList<>();
        List<Line> roads = new ArrayList<>(lines);
        for (Line line : lines) { pieces.add(CityCulDeSacs.held(line)); }
        for (Line line : beyond) { pieces.add(CityCulDeSacs.held(line)); }
        for (Court court : courts) {
            if (court.room() > 0) { pieces.add(CityCulDeSacs.courtBox(court.street(), court.end(), court.dir(), court.room(), court.stem())); }
        }
        for (Plot plot : found) { pieces.add(new Held(plot.fromX(), plot.fromZ(), plot.toX(), plot.toZ(), Hold.PLOT, true)); }
        for (BoundingBox well : CityLayout.mapWells(town)) { pieces.add(new Held(well.minX(), well.minZ(), well.maxX(), well.maxZ(), Hold.FIXED, true)); }
        for (Rail rail : rails) { pieces.add(rail.alongX() ? new Held(rail.from(), rail.at(), rail.to(), rail.last(), Hold.FIXED, true) : new Held(rail.at(), rail.from(), rail.last(), rail.to(), Hold.FIXED, false)); }
        int spacing = 2 * block;
        int attach = Math.max(ATTACH_GAP, CityPlan.fullWidth() + 2);
        int reach = CityPlan.plazaReach();
        for (Line street : lines) {
            if (street.alley()) { continue; }
            boolean alongX = street.alongX();
            for (int side = 0; side < 2; side++) {
                int dir = side == 1 ? 1 : -1;
                int edge = side == 1 ? street.last() : street.at();
                for (int row = street.from() + 2; row <= street.to() - 2; ) {
                    int length = alleyRun(pieces, alongX, row, edge, dir, attach);
                    if (length >= ALLEY_SHORTEST && crowdedLane(pieces, alongX, row, edge, dir, length, spacing)) { length = 0; }
                    if (length < ALLEY_SHORTEST) {
                        row += ALLEY_STEP;
                        continue;
                    }
                    int from = edge + dir;
                    int to = edge + dir * length;
                    for (Held other : pieces) {
                        if (other.hold() == Hold.STREET || other.hold() == Hold.LANE) { continue; }
                        if (!(alongX ? other.meets(row - 1, Math.min(from, to), row + 1, Math.max(from, to)) : other.meets(Math.min(from, to), row - 1, Math.max(from, to), row + 1))) { continue; }
                        int near = dir > 0 ? (alongX ? other.minZ() : other.minX()) - 1 : (alongX ? other.maxZ() : other.maxX()) + 1;
                        if (dir > 0 ? near < to : near > to) { to = near; }
                    }
                    if ((dir > 0 ? to < from : to > from) || Math.abs(to - from) + 1 < ALLEY_SHORTEST) {
                        row += ALLEY_STEP;
                        continue;
                    }
                    Line alley = new Line(row - 1, ALLEY_WIDTH, true, !alongX, Math.min(from, to), Math.max(from, to), dir > 0 ? End.MET : End.BARE, dir > 0 ? End.BARE : End.MET);
                    Held box = CityCulDeSacs.held(alley);
                    if (onPlaza(town, box, reach) || hugsStreet(roads, alley, block) || hugsStreet(beyond, alley, block) || CityCulDeSacs.crossesHill(ground, grades, roads, box)) {
                        row += ALLEY_STEP;
                        continue;
                    }
                    laid.add(alley);
                    roads.add(alley);
                    refusing.add(alley);
                    pieces.add(box);
                    int before = found.size();
                    if (!choices.isEmpty()) { CityPlanPlots.flank(roll, choices, tally, found, rails, refusing, plazas, alley, block, most); }
                    for (Plot plot : found.subList(before, found.size())) { pieces.add(new Held(plot.fromX(), plot.fromZ(), plot.toX(), plot.toZ(), Hold.PLOT, true)); }
                    ContentLog.LOGGER.debug("An alley {} long fills the block beside the drawn street at {}, {}, running from {}, {} with {} house(s) along it", Math.abs(to - from) + 1, alongX ? street.from() : street.at(), alongX ? street.at() : street.from(), box.minX(), box.minZ(), found.size() - before);
                    row += spacing;
                }
            }
        }
        return laid;
    }

    private static int alleyRun(List<Held> pieces, boolean alongX, int row, int edge, int dir, int attach) {
        int nearAny = Integer.MAX_VALUE;
        int nearPath = Integer.MAX_VALUE;
        for (Held other : pieces) {
            int acrossLo = alongX ? other.minX() : other.minZ();
            int acrossHi = alongX ? other.maxX() : other.maxZ();
            if (acrossHi < row - 2 || acrossLo > row + 2) { continue; }
            int alongLo = alongX ? other.minZ() : other.minX();
            int alongHi = alongX ? other.maxZ() : other.maxX();
            int away = dir > 0 ? alongLo - edge : edge - alongHi;
            if (away < 1) {
                if (dir > 0 ? alongHi >= edge + 1 : alongLo <= edge - 1) { return 0; }
                continue;
            }
            nearAny = Math.min(nearAny, away);
            if (other.hold() == Hold.STREET || other.hold() == Hold.LANE) { nearPath = Math.min(nearPath, away); }
        }
        if (nearAny > ALLEY_LONGEST + 14) { return 0; }
        int length = Math.min(ALLEY_LONGEST, nearAny - 2);
        if (nearPath != Integer.MAX_VALUE) { length = Math.min(length, nearPath - attach - 1); }
        return length;
    }

    private static boolean crowdedLane(List<Held> pieces, boolean alongX, int row, int edge, int dir, int length, int spacing) {
        int from = Math.min(edge + dir, edge + dir * length);
        int to = Math.max(edge + dir, edge + dir * length);
        for (Held other : pieces) {
            if (other.hold() != Hold.LANE || other.alongX() == alongX) { continue; }
            int center = alongX ? (other.minX() + other.maxX()) / 2 : (other.minZ() + other.maxZ()) / 2;
            if (Math.abs(center - row) >= spacing) { continue; }
            int alongLo = alongX ? other.minZ() : other.minX();
            int alongHi = alongX ? other.maxZ() : other.maxX();
            if (alongHi >= from && alongLo <= to) { return true; }
        }
        return false;
    }

    private static boolean hugsStreet(List<Line> held, Line alley, int block) {
        for (Line other : held) {
            if (other.alley() || other.alongX() != alley.alongX() || other.to() < alley.from() || other.from() > alley.to()) { continue; }
            int between = Math.max(other.at() - alley.last(), alley.at() - other.last()) - 1;
            if (between >= 0 && between < block) { return true; }
        }
        return false;
    }

    private static boolean onPlaza(Town town, Held box, int reach) {
        for (BoundingBox well : CityLayout.mapWells(town)) {
            if (box.meets(well.minX() - reach, well.minZ() - reach, well.maxX() + reach, well.maxZ() + reach)) { return true; }
        }
        return false;
    }

    private static boolean clear(List<Line> held, Line line) {
        for (Line other : held) {
            if (line.at() <= other.last() && line.last() >= other.at()) { return false; }
        }
        return true;
    }
}
