package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;

public final class CityRailsGrade {
    private static final int TOGETHER = 16;

    private CityRailsGrade() {}

    static CityRails.Laid lay(CityGround ground, CityPlan.Town town, CityPlan.Rail rail) {
        boolean sub = rail.subway();
        boolean alongX = rail.alongX();
        int start = rail.from();
        int rows = rail.to() - rail.from() + 1;
        int center = rail.middle();
        int half = CityRails.halfOf(rail);
        List<CityPlan.Line> lines = new ArrayList<>();
        List<BoundingBox> wells = new ArrayList<>();
        CityRails.gather(ground, CityLinks.ends(rail).length > 0 ? null : town, alongX, rail.from() - CityRails.NEAR, rail.to() + CityRails.NEAR, center - half - CityRails.NEAR, center + half + CityRails.NEAR, lines, wells);
        int sea = ground.sea();
        int[] floor = bedFloor(ground, rail, CityRails.fitted(ground, town));
        int climb = ContentCity.railClimb(sub);
        int[] rising = sub ? CityRails.surfacing(ground, rail, lines) : null;
        if (rising != null) { ContentLog.LOGGER.debug("Subway line at {} climbs out over the {} row(s) {} of row {}, {} of ramp to rise {} block(s) and the rest of it open track", center, rising[1] > 0 ? rail.to() - rising[0] : rising[0] - rail.from(), rising[1] > 0 ? "beyond" : "short", rising[0], ContentCity.subwayDepth() * climb, ContentCity.subwayDepth()); }
        List<CityRails.Station> stations = sub && ContentCity.stations() ? CityRails.claim(rail, town, lines, wells, rising) : List.of();
        boolean[] lowered = new boolean[rows];
        if (sub) { for (int at = 0; at < rows; at++) { lowered[at] = !CityRails.surfaced(rising, start + at); } }
        double[] base = CityGrade.railBase(floor, sea, lowered, ContentCity.subwayDepth(), ground.bottom() + ContentCitySewerPiece.FLOOR_LEAST);
        double[] wanted = sub ? CityGrade.sinking(base, climb) : null;
        int[] profile = wanted != null ? CityGrade.stepped(wanted, climb) : CityGrade.smoothed(base, climb);
        boolean[] fixed = new boolean[rows];
        int crossings = 0;
        for (CityPlan.Line line : lines) {
            if (line.alongX() == alongX || line.last() < rail.from() || line.at() > rail.to() || line.to() < center - half || line.from() > center + half) { continue; }
            int from = Math.max(0, line.at() - 1 - start);
            int to = Math.min(rows - 1, line.last() + 1 - start);
            if (from > to) { continue; }
            int middle = Mth.clamp(line.at() + (line.width() - 1) / 2 - start, 0, rows - 1);
            int level = profile[middle];
            if (sub && (floor[middle] == Integer.MIN_VALUE || floor[middle] - level > CityRails.FILL)) {
                if (ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("Subway line at {} runs {} block(s) under the street crossing it at row {}, so its grade is not held level there and climbs on at its own pace", center, floor[middle] == Integer.MIN_VALUE ? "an unread number of" : String.valueOf(floor[middle] - level), start + middle); }
                continue;
            }
            for (int at = from; at <= to; at++) {
                profile[at] = level;
                fixed[at] = true;
            }
            crossings++;
        }
        if (crossings > 0) { CityGrade.approach(profile, fixed, fixed, climb); }
        crossings += CityLinks.pin(ground, rail, start, profile, fixed, climb);
        boolean[] settled = fixed;
        if (sub && rising != null) {
            settled = fixed.clone();
            int ramp = ContentCity.subwayDepth() * climb;
            for (int at = 0; at < rows; at++) {
                int row = start + at;
                if (rising[1] > 0 ? row >= rising[0] - ramp : row <= rising[0] + ramp) { settled[at] = true; }
            }
        }
        if (crossings > 0) { CityGrade.spaceSteps(profile, fixed, settled, climb); }
        List<CityRails.Station> claimed = stations;
        if (!stations.isEmpty()) { stations = CityRails.reachable(ground, rail, lines, start, profile, stations); }
        int halfLength = ContentCity.stationLength() / 2;
        int holding = 0;
        for (CityRails.Station station : stations) {
            int heart = station.heart() - start;
            if (heart < 0 || heart >= rows) { continue; }
            int flat = profile[heart];
            for (int at = Math.max(0, heart - halfLength); at <= Math.min(rows - 1, heart + halfLength); at++) {
                if (profile[at] != flat) { holding++; }
                profile[at] = flat;
                fixed[at] = true;
            }
        }
        if (holding > 0) { ContentLog.LOGGER.debug("Subway line at {} holds {} station row(s) at one level, so its stored grade says what is laid there", center, holding); }
        if (wanted != null) {
            int ramped = CityGrade.eased(profile, wanted, fixed, start, center, climb);
            if (ramped > 0 && ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("Subway line at {} eased {} row(s) into the cone its held rows leave, one block every {} row(s) from each of them, so it ramps down to its depth instead of stepping", center, ramped, climb); }
        }
        boolean[] bridged = new boolean[rows];
        for (int at = 0; at < rows; at++) { bridged[at] = !sub && (floor[at] == Integer.MIN_VALUE || profile[at] > floor[at] + CityRails.FILL); }
        int[] undecked = profile.clone();
        CityGrade.levelDecks(profile, bridged, fixed, climb);
        int leveled = 0;
        for (int at = 0; at < rows; at++) { if (profile[at] != undecked[at]) { leveled++; } }
        if (leveled > 0) { ContentLog.LOGGER.debug("Leveled {} row(s) of railway line at {} so each of its trestles lies at one height end to end", leveled, center); }
        boolean[] bored = sub ? new boolean[rows] : CityGrade.roofed(profile, floor, bridged, ContentCity.railTunnelDepth());
        boolean[] tunnel = new boolean[rows];
        boolean[] crossed = new boolean[rows];
        int[] ends = CityLinks.ends(rail);
        for (int at = 0; at < rows; at++) {
            int row = start + at;
            boolean opened = CityLinks.opens(null, ends, row);
            if (sub) { tunnel[at] = !opened && !(CityRails.surfaced(rising, row) && floor[at] != Integer.MIN_VALUE && profile[at] >= floor[at] - 1); }
            else { tunnel[at] = !opened && !bridged[at] && bored[at] && CityRails.uncrossed(ground, lines, alongX, row, center, profile[at]); }
            crossed[at] = !tunnel[at] && CityRails.roadOver(lines, alongX, row, center);
        }
        boolean[] frames = new boolean[rows];
        if (!sub && !ContentCity.railFrameBlock().isEmpty()) {
            boolean[] decking = CityGrade.frameRows(bridged, ContentCity.railFrameLeast(), ContentCity.railFrameRun());
            for (int at = 0; at < rows; at++) { frames[at] = decking[at] && CityLinks.clearOfJoin(null, ends, start + at) && CityRails.uncrossed(ground, lines, alongX, start + at, center, profile[at]); }
        }
        if (ContentLog.LOGGER.debugEnabled()) {
            int trestle = 0;
            int cut = 0;
            for (int at = 0; at < rows; at++) {
                if (bridged[at]) { trestle++; }
                else if (floor[at] > profile[at]) { cut++; }
            }
            ContentLog.LOGGER.debug("{} line at {} grades {} row(s) from y {} to y {}, climbing a block every {} row(s) at most, {} on trestle and {} in cut, {} crossing(s), {} station(s){}", sub ? "Subway" : "Railway", center, rows, profile[0], profile[rows - 1], climb, trestle, cut, crossings, stations.size(), rising == null ? "" : ", climbing out past row " + rising[0]);
        }
        return new CityRails.Laid(rail, start, profile, floor, bridged, tunnel, bored, crossed, frames, rising, List.copyOf(stations), List.copyOf(claimed));
    }

    private static int[] bedFloor(CityGround ground, CityPlan.Rail rail, List<CityPlan.Rail> mates) {
        boolean alongX = rail.alongX();
        int acrossLeast = rail.middle() - CityRails.halfOf(rail);
        int acrossMost = rail.middle() + CityRails.halfOf(rail);
        boolean grew = true;
        while (grew) {
            grew = false;
            for (CityPlan.Rail met : mates) {
                if (met.equals(rail) || met.alongX() != alongX) { continue; }
                int metLeast = met.middle() - CityRails.halfOf(met);
                int metMost = met.middle() + CityRails.halfOf(met);
                if (metLeast > acrossMost + TOGETHER || metMost < acrossLeast - TOGETHER) { continue; }
                if (metLeast < acrossLeast) {
                    acrossLeast = metLeast;
                    grew = true;
                }
                if (metMost > acrossMost) {
                    acrossMost = metMost;
                    grew = true;
                }
            }
        }
        if (acrossLeast != rail.middle() - CityRails.halfOf(rail) || acrossMost != rail.middle() + CityRails.halfOf(rail)) { ContentLog.LOGGER.debug("Railway line at {} grades with the {} block(s) of formation it stands in, {} to {} across, so every line of it lies at one level", rail.middle(), acrossMost - acrossLeast + 1, acrossLeast, acrossMost); }

        return CityRails.floorRows(ground, alongX, rail.from(), rail.to(), acrossLeast, acrossMost);
    }

    @Nullable static CityRails.Bed stagedBed(CityGround ground, CityPlan.Town town, List<CityPlan.Rail> mine, CityPlan.Rail rail) {
        CityRails.Bed made = CityLinks.trunkBed(ground, rail);
        if (made != null) { return made; }
        CityPlan.Town owner = CityRails.TOWNS.get(rail.town());
        if (owner == null || rail.subway()) { return null; }
        return graded(ground, rail, owner.key() == town.key() ? mine : CityRails.staged(ground, owner));
    }

    static CityRails.Bed graded(CityGround ground, CityPlan.Rail rail, List<CityPlan.Rail> mates) {
        int[] floor = bedFloor(ground, rail, mates);
        int climb = ContentCity.railClimb(false);
        int[] base = CityGrade.railLine(floor, ground.sea(), climb);
        int[] pinned = base.clone();
        boolean[] fixed = new boolean[base.length];
        CityLinks.pin(ground, rail, rail.from(), pinned, fixed, climb);
        for (int at = 0; at < base.length; at++) { pinned[at] = fixed[at] ? pinned[at] : base[at]; }
        boolean[] bridged = new boolean[base.length];
        for (int at = 0; at < base.length; at++) { bridged[at] = floor[at] == Integer.MIN_VALUE || base[at] > floor[at] + CityRails.FILL; }
        return new CityRails.Bed(rail.from(), pinned, CityGrade.roofed(base, floor, bridged, ContentCity.railTunnelDepth()));
    }
}
