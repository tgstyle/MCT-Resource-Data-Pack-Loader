package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.content.def.CityMapDef;

import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.world.level.levelgen.structure.BoundingBox;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

final class CityMapDistricts {
    private static final int TRIES = 32;
    private static final int TIE_REACH = 112;
    private static final int JOIN = 4;
    private static final int GROW = 35;
    private static final int GROW_STEP = 7;
    private static final Grown NONE = new Grown(List.of(), Map.of(), List.of());

    record Grown(List<CityPlan.Line> ties, Map<Long, CityPlan> districts, List<CityPlan.Plot> plots) {}

    private record Standing(CityGround ground, CityPlan.Town town, List<CityPlan.Line> roads, List<BoundingBox> pieces, List<BoundingBox> drawn, List<BoundingBox> wells, List<CityPlan.Rail> rails, Map<Long, CityPlan> districts, List<CityPlan.Line> ties, int[] refused, List<BoundingBox> others) {}

    private CityMapDistricts() {}

    static Grown grow(CityGround ground, CityPlan.Town town, List<CityPlan.Line> lines, List<BoundingBox> pieces, List<CityPlan.Rail> rails, int built, List<BoundingBox> others) {
        int least = ContentVillages.plotsLeast();
        if (least <= 0 || built >= least) { return NONE; }
        List<BoundingBox> drawn = new ArrayList<>(pieces);
        for (CityPlan.Line line : lines) { drawn.add(box(line)); }
        Standing standing = new Standing(ground, town, new ArrayList<>(lines), new ArrayList<>(pieces), List.copyOf(drawn), new ArrayList<>(CityLayout.mapWells(town)), rails, new LinkedHashMap<>(), new ArrayList<>(), new int[6], others);
        List<CityPlan.Plot> plots = new ArrayList<>();
        Set<Long> tried = new HashSet<>();
        int attempts = 0;
        int allowed = Math.max(8 * TRIES, least + least / 2);
        int total = built;
        while (total < least && attempts < allowed) {
            long cell = site(standing, tried);
            if (cell == Long.MIN_VALUE) { break; }
            attempts++;
            tried.add(cell);
            CityPlan made = settle(standing, (int) (cell >> 32), (int) cell);
            if (made == null) { continue; }
            standing.districts().put(cell, made);
            standing.roads().addAll(made.alongX());
            standing.roads().addAll(made.alongZ());
            for (CityPlan.Plot plot : made.plots()) { standing.pieces().add(new BoundingBox(plot.fromX(), 0, plot.fromZ(), plot.toX(), 0, plot.toZ())); }
            standing.wells().addAll(made.wellBoxes());
            plots.addAll(made.plots());
            total += made.plots().size();
            tried.clear();
        }
        int[] refused = standing.refused();
        if (total < least) { ContentLog.LOGGER.debug("District growth beside the drawn city at {}, {} drained after {} attempt(s): {} sites too near a well, {} on rough ground, {} on built ground, {} across a standing piece, {} that could not be joined, {} beside another city; vanilla infill along the standing streets is not built on this line", town.wellX(), town.wellZ(), attempts, refused[0], refused[1], refused[2], refused[3], refused[4], refused[5]); }
        ContentLog.LOGGER.info("The drawn city at {}, {} grew {} district(s) to {} plot(s) against the asked minimum of {}", town.wellX(), town.wellZ(), standing.districts().size(), total, least);
        return new Grown(List.copyOf(standing.ties()), Collections.unmodifiableMap(new LinkedHashMap<>(standing.districts())), List.copyOf(plots));
    }

    private static int spacing() { return 2 * ContentVillages.largestPlot() + 2 * CityPlan.plazaReach() + 2 * CityPlan.streetFullWidth(); }

    private static long site(Standing standing, Set<Long> tried) {
        int cx = standing.town().wellX();
        int cz = standing.town().wellZ();
        long best = Long.MIN_VALUE;
        long bestAway = Long.MAX_VALUE;
        int reach = CityGrowth.march();
        int spacing = spacing();
        for (CityPlan.Line road : standing.roads()) {
            boolean alongX = road.alongX();
            int line = (road.at() + road.last()) / 2;
            for (int sign = -1; sign <= 1; sign += 2) {
                int edge = sign < 0 ? road.from() : road.to();
                for (int away = CityPlan.plazaReach() + 4; away <= Math.max(48, spacing + 16); away += 8) {
                    int siteX = alongX ? edge + sign * away : line;
                    int siteZ = alongX ? line : edge + sign * away;
                    long far = (long) (siteX - cx) * (siteX - cx) + (long) (siteZ - cz) * (siteZ - cz);
                    if (far > (long) reach * reach || far >= bestAway) { continue; }
                    long cell = CityPlan.packed(CityPlan.districtOf(siteX, true), CityPlan.districtOf(siteZ, false));
                    if (tried.contains(cell) || closed(standing, cell) || crowded(standing.wells(), siteX, siteZ, spacing)) { continue; }
                    bestAway = far;
                    best = cell;
                }
            }
        }
        if (best != Long.MIN_VALUE) { return best; }
        int step = Math.max(8, spacing / 2);
        for (int dx = -reach; dx <= reach; dx += step) {
            for (int dz = -reach; dz <= reach; dz += step) {
                if (dx == 0 && dz == 0) { continue; }
                long away = (long) dx * dx + (long) dz * dz;
                if (away > (long) reach * reach || away >= bestAway) { continue; }
                long cell = CityPlan.packed(CityPlan.districtOf(cx + dx, true), CityPlan.districtOf(cz + dz, false));
                if (tried.contains(cell) || closed(standing, cell) || crowded(standing.wells(), cx + dx, cz + dz, spacing)) { continue; }
                bestAway = away;
                best = cell;
            }
        }
        return best;
    }

    private static boolean closed(Standing standing, long cell) {
        int size = CityPlan.district();
        int x = CityPlan.windowOf((int) (cell >> 32), true);
        int z = CityPlan.windowOf((int) cell, false);
        return standing.districts().containsKey(cell) || meetsAny(standing.drawn(), new BoundingBox(x, 0, z, x + size - 1, 0, z + size - 1));
    }

    private static boolean drawnOver(CityPlan.Town town, int cellX, int cellZ) {
        int size = CityPlan.district();
        int x = CityPlan.windowOf(cellX, true);
        int z = CityPlan.windowOf(cellZ, false);
        return x <= town.toX() && x + size - 1 >= town.fromX() && z <= town.toZ() && z + size - 1 >= town.fromZ();
    }

    @Nullable private static CityPlan settle(Standing standing, int cellX, int cellZ) {
        CityPlan.Town town = standing.town();
        int[] refused = standing.refused();
        int half = CityPlan.streetFullWidth() / 2;
        int centerX = CityPlan.windowOf(cellX, true) + town.crossX() + half;
        int centerZ = CityPlan.windowOf(cellZ, false) + town.crossZ() + half;
        if (crowded(standing.wells(), centerX, centerZ, spacing())) {
            refused[0]++;
            return null;
        }
        if (CityGrowth.rough(standing.ground(), centerX, centerZ)) {
            refused[1]++;
            return null;
        }
        int reach = CityPlan.plazaReach();
        int wellX = centerX - ContentCityWellPiece.SIZE / 2;
        int wellZ = centerZ - ContentCityWellPiece.SIZE / 2;
        BoundingBox well = new BoundingBox(wellX, 0, wellZ, wellX + ContentCityWellPiece.SIZE - 1, 0, wellZ + ContentCityWellPiece.SIZE - 1);
        BoundingBox square = new BoundingBox(well.minX() - reach, 0, well.minZ() - reach, well.maxX() + reach, 0, well.maxZ() + reach);
        if (meetsAny(standing.others(), square)) {
            refused[5]++;
            ContentLog.LOGGER.debug("The district at {}, {} beside the drawn city at {}, {} would stand beside another city, so it is not grown", wellX, wellZ, town.wellX(), town.wellZ());
            return null;
        }
        if (boreUnder(standing.rails(), well) || meetsAny(standing.pieces(), square) || meetsRoad(standing.roads(), square)) {
            refused[2]++;
            return null;
        }
        CityPlan laid = CityPlanTowns.laid(standing.ground(), cellX, cellZ, town);
        List<CityPlan.Line> own = new ArrayList<>(laid.alongX());
        own.addAll(laid.alongZ());
        for (CityPlan.Line line : own) {
            if (meetsAny(standing.pieces(), box(line))) {
                refused[3]++;
                ContentLog.LOGGER.debug("The district at {}, {} beside the drawn city would lay a street across a standing piece, so it is not grown", CityPlan.windowOf(cellX, true), CityPlan.windowOf(cellZ, false));
                return null;
            }
        }
        List<CityPlan.Plot> kept = new ArrayList<>();
        for (CityPlan.Plot plot : laid.plots()) {
            BoundingBox lot = new BoundingBox(plot.fromX(), 0, plot.fromZ(), plot.toX(), 0, plot.toZ());
            if (!meetsAny(standing.pieces(), lot) && !meetsRoad(standing.roads(), lot)) { kept.add(plot); }
        }
        CityPlan made = kept.size() == laid.plots().size() ? laid : laid.keeping(kept);
        if (joined(standing, own, square)) { return made; }
        List<CityPlan.Line> reached = regrow(standing, own, made, cellX, cellZ);
        if (!reached.isEmpty()) {
            standing.ties().addAll(reached);
            standing.roads().addAll(reached);
            return made;
        }
        if (ContentControl.flag(ContentControl.VILLAGES, "villageTieStreets", Config.worldgen.villageTieStreets())) {
            CityPlan.Line tie = tie(standing, own, made, cellX, cellZ, wellX, wellZ);
            if (tie != null) {
                standing.ties().add(tie);
                standing.roads().add(tie);
                return made;
            }
        }
        refused[4]++;
        ContentLog.LOGGER.debug("The district at {}, {} could not join its streets to the drawn city, so it is taken back down", wellX, wellZ);
        return null;
    }

    private static List<CityPlan.Line> regrow(Standing standing, List<CityPlan.Line> own, CityPlan made, int cellX, int cellZ) {
        List<BoundingBox> pieces = new ArrayList<>(standing.pieces());
        for (CityPlan.Plot plot : made.plots()) { pieces.add(new BoundingBox(plot.fromX(), 0, plot.fromZ(), plot.toX(), 0, plot.toZ())); }
        CityPlan.Line[] grown = new CityPlan.Line[own.size() * 2];
        for (int round = 1; round <= JOIN; round++) {
            boolean grew = false;
            for (int i = 0; i < own.size(); i++) {
                CityPlan.Line street = own.get(i);
                if (street.alley()) { continue; }
                for (int dir = -1; dir <= 1; dir += 2) {
                    int slot = 2 * i + (dir + 1) / 2;
                    CityPlan.Line held = grown[slot];
                    if ((dir > 0 ? street.endsHigh() : street.endsLow()) != CityPlan.End.BARE) { continue; }
                    int tip = held == null ? (dir > 0 ? street.to() : street.from()) : (dir > 0 ? held.to() : held.from());
                    int toward = street.alongX() ? standing.town().wellX() : standing.town().wellZ();
                    if ((toward - tip) * dir <= 0) { continue; }
                    List<CityPlan.Line> roads = new ArrayList<>(standing.roads());
                    roads.addAll(own);
                    for (CityPlan.Line other : grown) {
                        if (other != null && other != held) { roads.add(other); }
                    }
                    for (int length = GROW; length >= GROW_STEP; length -= GROW_STEP) {
                        int far = tip + dir * length;
                        BoundingBox strip = box(new CityPlan.Line(street.at(), street.width(), false, street.alongX(), Math.min(tip + dir, far), Math.max(tip + dir, far), CityPlan.End.MET, CityPlan.End.MET));
                        if (meetsAny(pieces, strip) || meetsRoad(roads, strip) || strays(standing, strip, cellX, cellZ)) { continue; }
                        int start = held == null ? tip + dir : (dir > 0 ? held.from() : held.to());
                        grown[slot] = new CityPlan.Line(street.at(), street.width(), false, street.alongX(), Math.min(start, far), Math.max(start, far), CityPlan.End.MET, CityPlan.End.MET);
                        grew = true;
                        break;
                    }
                }
            }
            if (!grew) { break; }
            List<CityPlan.Line> reached = new ArrayList<>();
            for (CityPlan.Line line : grown) {
                if (line != null && joined(standing, List.of(line), box(line))) { reached.add(line); }
            }
            if (!reached.isEmpty()) {
                ContentLog.LOGGER.debug("The district at {}, {} joined the drawn city after {} round(s) of street growth, lengthening {} of its street(s) toward it", CityPlan.windowOf(cellX, true), CityPlan.windowOf(cellZ, false), round, reached.size());
                return reached;
            }
        }
        return List.of();
    }

    private static boolean joined(Standing standing, List<CityPlan.Line> own, BoundingBox square) {
        int reach = CityPlan.plazaReach();
        List<BoundingBox> mine = new ArrayList<>(List.of(square));
        for (CityPlan.Line line : own) {
            if (!line.alley()) { mine.add(box(line)); }
        }
        for (BoundingBox at : mine) {
            BoundingBox grown = new BoundingBox(at.minX() - 1, 0, at.minZ() - 1, at.maxX() + 1, 0, at.maxZ() + 1);
            for (CityPlan.Line road : standing.roads()) {
                if (!road.alley() && box(road).intersects(grown)) { return true; }
            }
            for (BoundingBox well : standing.wells()) {
                if (new BoundingBox(well.minX() - reach, 0, well.minZ() - reach, well.maxX() + reach, 0, well.maxZ() + reach).intersects(grown)) { return true; }
            }
        }
        return false;
    }

    @Nullable private static CityPlan.Line tie(Standing standing, List<CityPlan.Line> own, CityPlan made, int cellX, int cellZ, int wellX, int wellZ) {
        CityPlan.Line best = null;
        int bestLength = Integer.MAX_VALUE;
        int[] refused = new int[4];
        int side = CityPlanTowns.blockAt(standing.ground().seed(), cellX, cellZ);
        List<BoundingBox> pieces = new ArrayList<>(standing.pieces());
        for (CityPlan.Plot plot : made.plots()) { pieces.add(new BoundingBox(plot.fromX(), 0, plot.fromZ(), plot.toX(), 0, plot.toZ())); }
        List<CityPlan.Line> roads = new ArrayList<>(standing.roads());
        roads.addAll(own);
        for (CityPlan.Line street : own) {
            if (street.alley()) { continue; }
            boolean alongX = street.alongX();
            int center = (street.at() + street.last()) / 2;
            for (int dir = -1; dir <= 1; dir += 2) {
                int end = dir > 0 ? street.to() : street.from();
                for (CityPlan.Line met : standing.roads()) {
                    if (met.alley()) { continue; }
                    if (met.alongX() != alongX && !met.covers(center)) { continue; }
                    if (met.alongX() == alongX && (met.at() + met.last()) / 2 != center) { continue; }
                    int metLow = met.alongX() == alongX ? met.from() : met.at();
                    int metHigh = met.alongX() == alongX ? met.to() : met.last();
                    int near = dir > 0 ? metLow : metHigh;
                    if ((near - end) * dir <= 1) { continue; }
                    int from = dir > 0 ? end + 1 : near + 1;
                    int to = dir > 0 ? near - 1 : end - 1;
                    int length = to - from + 1;
                    if (length >= bestLength) { continue; }
                    if (length > TIE_REACH || length <= CityPlan.streetFullWidth()) {
                        refused[0]++;
                        continue;
                    }
                    CityPlan.Line tried = new CityPlan.Line(street.at(), street.width(), false, alongX, from, to, CityPlan.End.MET, CityPlan.End.MET);
                    BoundingBox strip = box(tried);
                    if (meetsAny(pieces, strip) || meetsRoad(roads, strip) || strays(standing, strip, cellX, cellZ)) {
                        refused[1]++;
                        continue;
                    }
                    if (beside(roads, tried, side)) {
                        refused[2]++;
                        continue;
                    }
                    if (CityCulDeSacs.steep(standing.ground(), tried, roads)) {
                        refused[3]++;
                        continue;
                    }
                    best = tried;
                    bestLength = length;
                }
            }
        }
        if (best == null) {
            ContentLog.LOGGER.debug("The district at {}, {} has no straight, level and free line from any of its street ends to a street of the drawn city within {} blocks, so no tie street can join it: {} too short or long, {} across a piece or outside the city's districts, {} beside a road, {} through a tunnel", wellX, wellZ, TIE_REACH, refused[0], refused[1], refused[2], refused[3]);
            return null;
        }
        ContentLog.LOGGER.debug("The district at {}, {} could not meet the drawn city, so a tie street of {} row(s) is laid from {} to {} along {} at {}", wellX, wellZ, bestLength, best.from(), best.to(), best.alongX() ? "x" : "z", best.at());
        return best;
    }

    private static boolean strays(Standing standing, BoundingBox strip, int cellX, int cellZ) {
        for (int x = CityPlan.districtOf(strip.minX(), true); x <= CityPlan.districtOf(strip.maxX(), true); x++) {
            for (int z = CityPlan.districtOf(strip.minZ(), false); z <= CityPlan.districtOf(strip.maxZ(), false); z++) {
                if ((x == cellX && z == cellZ) || drawnOver(standing.town(), x, z) || standing.districts().containsKey(CityPlan.packed(x, z))) { continue; }
                return true;
            }
        }
        return false;
    }

    private static boolean beside(List<CityPlan.Line> roads, CityPlan.Line tie, int side) {
        for (CityPlan.Line other : roads) {
            if (other.alongX() != tie.alongX() || other.width() <= 3) { continue; }
            if (Math.min(tie.to(), other.to()) - Math.max(tie.from(), other.from()) < 0) { continue; }
            int gap = Math.max(other.at() - tie.last(), tie.at() - other.last());
            if (gap > 0 && gap - 1 < side + ContentVillages.largestPlot()) { return true; }
        }
        return false;
    }

    private static boolean boreUnder(List<CityPlan.Rail> rails, BoundingBox well) {
        for (CityPlan.Rail rail : rails) {
            if (rail.subway() && (rail.alongX() ? new BoundingBox(rail.from(), 0, rail.at(), rail.to(), 0, rail.last()) : new BoundingBox(rail.at(), 0, rail.from(), rail.last(), 0, rail.to())).intersects(well)) { return true; }
        }
        return false;
    }

    static List<BoundingBox> others(CityGround ground, CityMapDef map, int cityX, int cityZ, CityPlan.Town town) {
        List<BoundingBox> found = new ArrayList<>();
        int spacing = CityPlan.spacing();
        if (spacing <= 1) { return found; }
        int regionX = Math.floorDiv(cityX, spacing);
        int regionZ = Math.floorDiv(cityZ, spacing);
        int reach = CityGrowth.march() * 2 / (spacing * CityPlan.district()) + 2;
        for (int dz = -reach; dz <= reach; dz++) {
            for (int dx = -reach; dx <= reach; dx++) {
                int[] site = CityDistricts.center(ground, (regionX + dx) * spacing, (regionZ + dz) * spacing);
                CityPlan.Town other = site == null ? null : CityLayout.mapTown(ground, map, site[0], site[1], true);
                if (other != null && other.key() != town.key()) { found.add(new BoundingBox(other.fromX(), 0, other.fromZ(), other.toX(), 0, other.toZ())); }
            }
        }
        return found;
    }

    private static boolean meetsAny(List<BoundingBox> pieces, BoundingBox box) {
        for (BoundingBox piece : pieces) {
            if (piece.intersects(box)) { return true; }
        }
        return false;
    }

    private static boolean meetsRoad(List<CityPlan.Line> roads, BoundingBox box) {
        for (CityPlan.Line road : roads) {
            if (box(road).intersects(box)) { return true; }
        }
        return false;
    }

    static BoundingBox box(CityPlan.Line line) { return line.alongX() ? new BoundingBox(line.from(), 0, line.at(), line.to(), 0, line.last()) : new BoundingBox(line.at(), 0, line.from(), line.last(), 0, line.to()); }

    private static boolean crowded(List<BoundingBox> wells, int x, int z, int spacing) {
        for (BoundingBox well : wells) {
            long dx = (well.minX() + well.maxX()) / 2 - x;
            long dz = (well.minZ() + well.maxZ()) / 2 - z;
            if (dx * dx + dz * dz < (long) spacing * spacing) { return true; }
        }
        return false;
    }
}
