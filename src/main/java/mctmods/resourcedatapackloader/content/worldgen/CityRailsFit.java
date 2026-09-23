package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import javax.annotation.Nullable;

public final class CityRailsFit {
    private static final int STRETCH = 32;

    private CityRailsFit() {}

    static void found(RandomSource roll, CityPlan.Town town, boolean sub, List<CityPlan.Rail> found) {
        int lines = CityRails.lines(sub);
        if (lines <= 0) { return; }
        boolean alongX = ContentCity.railsAlongX(roll, sub);
        int width = CityPlan.railWidth(sub);
        int spacing = CityRails.spacing(sub);
        int apart = width + spacing;
        int half = (width - 1) / 2;
        int clear = CityRails.clear(sub);
        int well = town.wellAcross(alongX);
        List<Integer> placed = new ArrayList<>();
        for (int line = 0; line < lines; line++) {
            int side = line % 2 == 0 ? 1 : -1;
            int rolled = side * (clear + (line / 2) * apart + roll.nextInt(Math.max(1, spacing / 2)));
            int candidate = sub ? offWell(town, alongX, well + rolled, side) - well : rolled;
            if (candidate != rolled) { ContentLog.LOGGER.debug("Subway line {} of the city at {}, {} rolled onto a well at {} {}, so it is laid {} block(s) further out, at {}, and its corridor leaves the well alone", line, town.wellX(), town.wellZ(), alongX ? "z" : "x", well + rolled, Math.abs(candidate - rolled), well + candidate); }
            boolean moved = true;
            while (moved) {
                moved = false;
                for (int other : placed) {
                    if (Math.abs(candidate - other) >= apart) { continue; }
                    candidate += side * apart;
                    moved = true;
                }
            }
            placed.add(candidate);
            int center = well + candidate;
            found.add(new CityPlan.Rail(center - half, width, alongX, sub, Integer.MIN_VALUE, Integer.MAX_VALUE, town.key()));
            ContentLog.LOGGER.debug("{} line {} of the city at {}, {} is laid {} at {} {}, {} wide, at least {} block(s) of ground from any other line of this city, before any street of the city", sub ? "Subway" : "Railway", line, town.wellX(), town.wellZ(), alongX ? "east to west" : "north to south", alongX ? "z" : "x", center, width, spacing);
        }
    }

    static int seekRoad(CityGround ground, CityPlan.Town town, List<CityPlan.Rail> mine, CityPlan.Rail rail, int reach) {
        boolean alongX = rail.alongX();
        int rolled = rail.middle();
        int best = Integer.MAX_VALUE;
        int wanted = rolled;
        Map<CityPlan.Rail, Optional<CityRails.Bed>> beds = new HashMap<>();
        Function<CityPlan.Rail, CityRails.Bed> bedOf = met -> beds.computeIfAbsent(met, key -> Optional.ofNullable(CityRailsGrade.stagedBed(ground, town, mine, key))).orElse(null);
        for (int center : streetCenters(town, CityRails.placed(ground.seed(), town), alongX, rolled, reach)) {
            int off = Math.abs(center - rolled);
            if (off > reach || off >= best) { continue; }
            if (wellMet(town, alongX, center) != null) {
                ContentLog.LOGGER.debug("A subway line leaves the street at {} {} alone: its corridor there would bore through a city well", alongX ? "z" : "x", center);
                continue;
            }
            if (unlaid(ground, town, mine, bedOf, rail, center)) {
                ContentLog.LOGGER.debug("A subway line leaves the street at {} {} alone: no district of the city lays it beside its railway lines", alongX ? "z" : "x", center);
                continue;
            }
            best = off;
            wanted = center;
        }
        if (wanted != rolled) { ContentLog.LOGGER.debug("A subway line is slid {} block(s) onto the street at {} {}, so it runs under a road and its stations can surface at the road side", wanted - rolled, alongX ? "z" : "x", wanted); }
        return wanted;
    }

    private static boolean unlaid(CityGround ground, CityPlan.Town town, List<CityPlan.Rail> mine, Function<CityPlan.Rail, CityRails.Bed> bedOf, CityPlan.Rail rail, int center) {
        int[] rows = rows(town, rail);
        int across = CityPlan.districtOf(center, !rail.alongX());
        for (int along = CityPlan.districtOf(rows[0], rail.alongX()); along <= CityPlan.districtOf(rows[1], rail.alongX()); along++) {
            if (CityPlanTowns.laysStreet(ground, town, mine, bedOf, rail.alongX() ? along : across, rail.alongX() ? across : along, rail.alongX(), center)) { return false; }
        }
        return true;
    }

    private static int acrossReach() { return (CityPlan.railWidth(true) - 1) / 2 + ContentCity.stationReach() + 1 + CityLinks.linkReach(); }

    private static int offWell(CityPlan.Town town, boolean alongX, int center, int side) {
        int reach = acrossReach();
        int moved = center;
        int[] met = wellMet(town, alongX, moved);
        while (met != null) {
            moved = side >= 0 ? met[1] + reach + 1 : met[0] - reach - 1;
            met = wellMet(town, alongX, moved);
        }
        return moved;
    }

    @Nullable private static int[] wellMet(CityPlan.Town town, boolean alongX, int center) {
        int reach = acrossReach();
        if (!town.drawn().isEmpty()) {
            for (BoundingBox well : CityLayout.mapWells(town)) {
                int least = alongX ? well.minZ() : well.minX();
                int most = alongX ? well.maxZ() : well.maxX();
                if (center + reach >= least && center - reach <= most) { return new int[] {least, most}; }
            }
            return null;
        }
        int base = town.crossAcross(alongX) + (CityPlan.streetFullWidth() - 1) / 2 - ContentCityWellPiece.SIZE / 2;
        for (int k = CityPlan.districtOf(center - reach - base, !alongX) - 1; k <= CityPlan.districtOf(center + reach - base, !alongX) + 1; k++) {
            int least = CityPlan.windowOf(k, !alongX) + base;
            int most = least + ContentCityWellPiece.SIZE - 1;
            if (center + reach >= least && center - reach <= most) { return new int[] {least, most}; }
        }
        return null;
    }

    private static List<Integer> streetCenters(CityPlan.Town town, List<CityPlan.Rail> rails, boolean alongX, int near, int reach) {
        List<Integer> found = new ArrayList<>();
        if (!town.drawn().isEmpty()) {
            for (CityPlan.Line line : town.drawn()) {
                if (line.alongX() == alongX && !line.alley()) { found.add(line.at() + (line.width() - 1) / 2); }
            }
            return found;
        }
        int offset = town.crossAcross(alongX) + (CityPlan.streetFullWidth() - 1) / 2;
        int side = CityAlleys.branchAt(town.crossAcross(alongX)) + (CityPlan.streetFullWidth() - 1) / 2;
        for (int k = CityPlan.districtOf(near - reach - offset, !alongX) - 1; k <= CityPlan.districtOf(near + reach - offset, !alongX) + 1; k++) {
            found.add(CityPlan.windowOf(k, !alongX) + offset);
            if (CityAlleys.branches(town, rails, k, alongX)) { found.add(CityPlan.windowOf(k, !alongX) + side); }
        }
        return found;
    }

    @Nullable static CityPlan.Rail fit(CityGround ground, CityPlan.Town town, CityPlan.Rail rolled, int moved) {
        CityPlan.Rail rail = moved == rolled.middle() ? rolled : new CityPlan.Rail(rolled.at() + moved - rolled.middle(), rolled.width(), rolled.alongX(), rolled.subway(), rolled.from(), rolled.to(), rolled.town());
        int size = CityPlan.district();
        boolean sub = rail.subway();
        boolean alongX = rail.alongX();
        int tail = CityPlan.railTail(sub);
        int wellAt = town.wellAlong(alongX);
        int[] rows = rows(town, rail);
        int from = rows[0];
        int to = rows[1];
        int half = CityRails.halfOf(rail);
        int center = rail.middle();
        for (int along = CityPlan.districtOf(from, alongX); along <= CityPlan.districtOf(to, alongX); along++) {
            for (int across = CityPlan.districtOf(center - half, !alongX); across <= CityPlan.districtOf(center + half, !alongX); across++) {
                CityPlan other = CityPlanTowns.raw(ground, alongX ? along : across, alongX ? across : along);
                if (other == null || other.town() == null || other.town().key() == town.key()) { continue; }
                int metLeast = CityPlan.windowOf(along, alongX);
                int metMost = metLeast + size - 1;
                if (metLeast > wellAt) { to = Math.min(to, metLeast - 1 - CityRails.MARGIN); }
                else if (metMost < wellAt) { from = Math.max(from, metMost + 1 + CityRails.MARGIN); }
                else {
                    ContentLog.LOGGER.debug("Railway line at {} of the city at {}, {} has no room left beside the cities around it, so it is not laid", center, town.wellX(), town.wellZ());
                    return null;
                }
                ContentLog.LOGGER.debug("Railway line at {} of the city at {}, {} stops short of the city district at {}, {}", center, town.wellX(), town.wellZ(), alongX ? along : across, alongX ? across : along);
            }
        }
        if (from > to) {
            ContentLog.LOGGER.debug("Railway line at {} of the city at {}, {} has no room left beside the cities around it, so it is not laid", center, town.wellX(), town.wellZ());
            return null;
        }
        int shallow = sub ? shallowRow(ground, rail, from, to) : Integer.MIN_VALUE;
        if (shallow != Integer.MIN_VALUE) {
            ContentLog.LOGGER.debug("Subway line at {} of the city at {}, {} has no room under the ground at row {}: {} block(s) of depth would reach below y {}, the world floor plus its lining, so it is not laid", center, town.wellX(), town.wellZ(), shallow, ContentCity.subwayDepth(), ground.bottom() + ContentCitySewerPiece.FLOOR_LEAST);
            return null;
        }
        ContentLog.LOGGER.debug("{} line at {} of the city at {}, {} is fitted to rows {} to {}, {} beyond the city either way", sub ? "Subway" : "Railway", center, town.wellX(), town.wellZ(), from, to, tail);
        return new CityPlan.Rail(rail.at(), rail.width(), alongX, sub, from, to, town.key());
    }

    private static int shallowRow(CityGround ground, CityPlan.Rail rail, int from, int to) {
        int least = ground.bottom() + ContentCitySewerPiece.FLOOR_LEAST + ContentCity.subwayDepth();
        for (int row = from; row <= to; row++) {
            int sampled = rail.alongX() ? ground.floor(row, rail.middle()) : ground.floor(rail.middle(), row);
            if (sampled < least) { return row; }
        }
        return Integer.MIN_VALUE;
    }

    static int[] rows(CityPlan.Town town, CityPlan.Rail rail) {
        boolean alongX = rail.alongX();
        int tail = CityPlan.railTail(rail.subway());
        int reach = CityGrowth.march() + tail + STRETCH;
        int wellAt = town.wellAlong(alongX);
        return new int[] {Math.max(wellAt - reach, town.least(alongX) - tail), Math.min(wellAt + reach, town.most(alongX) + tail)};
    }

    static int roofUnder(CityRails.Laid laid, BoundingBox met) {
        CityPlan.Rail rail = laid.rail();
        boolean alongX = rail.alongX();
        int half = CityRails.halfOf(rail) + 1;
        if ((alongX ? met.maxZ() : met.maxX()) < rail.middle() - half || (alongX ? met.minZ() : met.minX()) > rail.middle() + half) { return Integer.MIN_VALUE; }
        int roof = Integer.MIN_VALUE;
        for (int row = Math.max(alongX ? met.minX() : met.minZ(), rail.from()); row <= Math.min(alongX ? met.maxX() : met.maxZ(), rail.to()); row++) {
            if (!CityRails.surfaced(laid.rising(), row)) { roof = Math.max(roof, laid.level(row) + CityRails.CLEAR + 1); }
        }
        return roof;
    }
}
