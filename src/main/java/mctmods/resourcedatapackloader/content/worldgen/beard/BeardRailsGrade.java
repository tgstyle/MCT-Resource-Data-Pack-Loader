package mctmods.resourcedatapackloader.content.worldgen.beard;

import mctmods.resourcedatapackloader.content.village.RailPiece;
import mctmods.resourcedatapackloader.content.worldgen.ContentBeard;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.world.GenHeights;

import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import java.util.List;
import javax.annotation.Nullable;

public final class BeardRailsGrade {
    private static final int TOGETHER = 16;
    private static final double UNHELD = 1.0e6D;

    private BeardRailsGrade() {}

    private static int holdStation(RailPiece rail, int[] profile, int rowLeast, int rows, boolean[] fixed) {
        if (!rail.subway() || rail.stations().isEmpty()) { return 0; }
        int half = Math.max(0, BeardStations.length() / 2);
        int held = 0;
        for (StructureBoundingBox box : rail.stations()) {
            int heart = BeardStations.heartOf(rail, box) - rowLeast;
            if (heart < 0 || heart >= rows) { continue; }
            int flat = profile[heart];
            if (flat == Integer.MIN_VALUE) { continue; }
            for (int at = Math.max(0, heart - half); at <= Math.min(rows - 1, heart + half); at++) {
                if (profile[at] == Integer.MIN_VALUE) { continue; }
                fixed[at] = true;
                if (profile[at] == flat) { continue; }
                profile[at] = flat;
                held++;
            }
        }
        return held;
    }

    private static boolean onRamp(@Nullable int[] rising, int row) {
        if (rising == null) { return false; }
        int ramp = BeardRails.subwayDepth() * BeardRails.climb(true);
        return rising[1] > 0 ? row >= rising[0] - ramp : row <= rising[0] + ramp;
    }

    @Nullable public static BeardRoads.Grade profile(World world, RailPiece rail) {
        int[] trunk = rail.trunk();
        if (trunk != null) { return BeardLinks.trunkGrade(world, BeardLinks.Trunk.of(trunk)); }
        boolean sub = rail.subway();
        boolean alongX = rail.alongX();
        int rowLeast = rail.rowLeast();
        int acrossLeast = rail.acrossLeast();
        int acrossMost = rail.acrossMost();
        List<StructureComponent> pieces = ContentBeard.components();
        if (pieces != null) {
            boolean grew = true;
            while (grew) {
                grew = false;
                for (StructureComponent other : pieces) {
                    if (other == rail || !(other instanceof RailPiece) || ((RailPiece) other).alongX() != alongX) { continue; }
                    RailPiece met = (RailPiece) other;
                    if (met.acrossLeast() > acrossMost + TOGETHER || met.acrossMost() < acrossLeast - TOGETHER) { continue; }
                    if (met.acrossLeast() < acrossLeast) {
                        acrossLeast = met.acrossLeast();
                        grew = true;
                    }
                    if (met.acrossMost() > acrossMost) {
                        acrossMost = met.acrossMost();
                        grew = true;
                    }
                }
            }
            if (acrossLeast != rail.acrossLeast() || acrossMost != rail.acrossMost()) {
                ContentLog.LOGGER.debug("Railway line {} at {}, {} grades with the {} block(s) of formation it stands in, {} to {} across, so every line of it lies at one level", rail.line(), rail.getBoundingBox().minX, rail.getBoundingBox().minZ, acrossMost - acrossLeast + 1, acrossLeast, acrossMost);
            }
        }
        int[] ground = BeardGrade.noiseProfile(world, alongX, rowLeast, rail.rowMost(), acrossLeast, acrossMost);
        if (ground == null) { return null; }
        int rows = ground.length;
        double[] base = filled(ground, world.getSeaLevel());
        int[] rising = sub ? rail.rising(world) : null;
        if (sub) {
            double down = BeardRails.subwayDepth();
            double floorLeast = GenHeights.floor(world, BeardRails.FLOOR_LEAST);
            for (int at = 0; at < rows; at++) {
                if (BeardRails.surfaced(rising, rail.rowLeast() + at)) { continue; }
                base[at] = Math.max(floorLeast, base[at] - down);
            }
        }
        int climb = BeardRails.climb(sub);
        double[] wanted = sub ? sinking(base, climb) : null;
        int[] profile = sub ? stepped(wanted, climb) : smoothed(base, climb);
        boolean[] fixed = new boolean[rows];
        int crossings = level(rail, rowLeast, profile, ground, fixed);
        if (crossings > 0) { approach(profile, fixed, fixed, climb); }
        crossings += BeardLinks.pin(world, rail, rowLeast, profile, fixed, climb);
        boolean[] settled = fixed;
        if (sub && rising != null) {
            settled = fixed.clone();
            for (int at = 0; at < rows; at++) {
                if (onRamp(rising, rowLeast + at)) { settled[at] = true; }
            }
        }
        if (crossings > 0) { settle(profile, fixed, settled, climb); }
        int held = holdStation(rail, profile, rowLeast, rows, fixed);
        if (held > 0 && ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("Subway line {} holds {} station row(s) at one level, so its stored grade says what is laid there", rail.line(), held); }
        if (sub) {
            int ramped = eased(rail, profile, wanted, fixed, rowLeast, climb);
            if (ramped > 0 && ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("Subway line {} eased {} row(s) into the cone its held rows leave, one block every {} row(s) from each of them, so it ramps down to its depth instead of stepping", rail.line(), ramped, climb); }
        }
        boolean[] bridged = new boolean[rows];
        for (int at = 0; at < rows; at++) { bridged[at] = !sub && (ground[at] == Integer.MIN_VALUE || profile[at] > ground[at] + BeardRails.FILL); }
        int leveled = BeardGrade.levelDecks(profile, bridged, fixed, climb);
        if (leveled > 0 && ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("Leveled {} row(s) of railway line {} so each of its trestles lies at one height end to end", leveled, rail.line()); }
        if (ContentLog.LOGGER.debugEnabled()) {
            int trestle = 0;
            int cut = 0;
            for (int at = 0; at < rows; at++) {
                if (bridged[at]) { trestle++; }
                else if (ground[at] > profile[at]) { cut++; }
            }
            ContentLog.LOGGER.debug("Railway line {} at {}, {} grades {} row(s) from y {} to y {}, climbing a block every {} row(s) at most, {} on trestle and {} in cut", rail.line(), rail.getBoundingBox().minX, rail.getBoundingBox().minZ, rows, profile[0], profile[rows - 1], climb, trestle, cut);
        }
        return new BeardRoads.Grade(profile, ground, bridged, new boolean[rows], rowLeast, 0);
    }

    static double[] filled(int[] ground, int sea) {
        int rows = ground.length;
        double[] base = new double[rows];
        int i = 0;
        while (i < rows) {
            if (ground[i] != Integer.MIN_VALUE) {
                base[i] = ground[i];
                i++;
                continue;
            }
            int end = i;
            while (end + 1 < rows && ground[end + 1] == Integer.MIN_VALUE) { end++; }
            int before = i > 0 ? ground[i - 1] : Integer.MIN_VALUE;
            int after = end + 1 < rows ? ground[end + 1] : Integer.MIN_VALUE;
            for (int at = i; at <= end; at++) {
                double level;
                if (before == Integer.MIN_VALUE && after == Integer.MIN_VALUE) { level = sea; }
                else if (before == Integer.MIN_VALUE) { level = after; }
                else if (after == Integer.MIN_VALUE) { level = before; }
                else { level = before + (after - before) * (at - i + 1) / (double) (end - i + 2); }
                base[at] = Math.max(sea, level);
            }
            i = end + 1;
        }
        return base;
    }

    static int[] smoothed(double[] base, int climb) {
        int rows = base.length;
        double step = 1.0D / climb;
        double[] mean = meaned(base, span(climb));
        double[] lower = under(mean, step);
        double[] upper = over(mean, step);
        double[] want = new double[rows];
        for (int at = 0; at < rows; at++) { want[at] = (lower[at] + upper[at]) / 2.0D; }
        return stepped(want, climb);
    }

    static double[] sinking(double[] base, int climb) { return under(lowest(base, span(climb)), 1.0D / climb); }

    public static int[] sunken(double[] base, int climb) { return stepped(sinking(base, climb), climb); }

    private static int span(int climb) { return Math.max(4, 2 * climb); }

    private static double[] meaned(double[] base, int span) {
        int rows = base.length;
        double[] mean = new double[rows];
        for (int at = 0; at < rows; at++) {
            double total = 0.0D;
            int count = 0;
            for (int near = Math.max(0, at - span); near <= Math.min(rows - 1, at + span); near++) {
                total += base[near];
                count++;
            }
            mean[at] = total / count;
        }
        return mean;
    }

    private static double[] lowest(double[] base, int span) {
        int rows = base.length;
        double[] least = new double[rows];
        for (int at = 0; at < rows; at++) {
            double found = base[at];
            for (int near = Math.max(0, at - span); near <= Math.min(rows - 1, at + span); near++) { found = Math.min(found, base[near]); }
            least[at] = found;
        }
        return least;
    }

    private static double[] under(double[] level, double step) {
        double[] lower = level.clone();
        for (int at = 1; at < lower.length; at++) { lower[at] = Math.min(lower[at], lower[at - 1] + step); }
        for (int at = lower.length - 2; at >= 0; at--) { lower[at] = Math.min(lower[at], lower[at + 1] + step); }
        return lower;
    }

    private static double[] over(double[] level, double step) {
        double[] upper = level.clone();
        for (int at = 1; at < upper.length; at++) { upper[at] = Math.max(upper[at], upper[at - 1] - step); }
        for (int at = upper.length - 2; at >= 0; at--) { upper[at] = Math.max(upper[at], upper[at + 1] - step); }
        return upper;
    }

    private static int[] stepped(double[] want, int climb) {
        int rows = want.length;
        int[] profile = new int[rows];
        int level = (int) Math.round(want[0]);
        int lastStep = -climb;
        for (int at = 0; at < rows; at++) {
            if (want[at] >= level + 1 && at - lastStep >= climb) {
                level++;
                lastStep = at;
            }
            else if (want[at] <= level - 1 && at - lastStep >= climb) {
                level--;
                lastStep = at;
            }
            profile[at] = level;
        }
        return profile;
    }

    private static int eased(RailPiece rail, int[] profile, double[] want, boolean[] fixed, int rowLeast, int climb) {
        int rows = profile.length;
        double step = 1.0D / climb;
        double[] high = new double[rows];
        double[] low = new double[rows];
        for (int at = 0; at < rows; at++) {
            high[at] = fixed[at] ? profile[at] : UNHELD;
            low[at] = fixed[at] ? profile[at] : -UNHELD;
        }
        high = under(high, step);
        low = over(low, step);
        double[] cone = new double[rows];
        for (int at = 0; at < rows; at++) { cone[at] = Math.min(high[at], Math.max(low[at], want[at])); }
        int[] made = stepped(cone, climb);
        int moved = 0;
        int strained = 0;
        int firstStrain = 0;
        int deepestStrain = 0;
        for (int at = 0; at < rows; at++) {
            if (fixed[at]) {
                if (made[at] == profile[at]) { continue; }
                if (strained == 0) { firstStrain = rowLeast + at; }
                strained++;
                deepestStrain = Math.max(deepestStrain, Math.abs(made[at] - profile[at]));
                continue;
            }
            if (made[at] == profile[at]) { continue; }
            profile[at] = made[at];
            moved++;
        }
        if (strained > 0) { ContentLog.LOGGER.warn("Subway line {} holds {} row(s) at levels no grade of one block every {} row(s) can join, the first at row {} and the worst {} block(s) out, so its grade steps there", rail.line(), strained, climb, firstStrain, deepestStrain); }
        return moved;
    }

    static void settle(int[] profile, boolean[] fixed, boolean[] settled, int climb) {
        int rows = profile.length;
        rein(profile, fixed);
        int since = -climb;
        for (int at = 1; at < rows; at++) {
            if (profile[at] == profile[at - 1]) { continue; }
            if (at - since >= climb) {
                since = at;
                continue;
            }
            int back = at;
            while (back < rows && !settled[back] && profile[back] != profile[at - 1]) {
                profile[back] = profile[at - 1];
                back++;
            }
            if (back == at) { since = at; }
        }
        rein(profile, fixed);
    }

    static void approach(int[] profile, boolean[] fixed, boolean[] band, int climb) {
        int rows = profile.length;
        int[] target = profile.clone();
        boolean[] claimed = new boolean[rows];
        for (int at = 0; at < rows; at++) {
            if (!band[at]) { continue; }
            if (at > 0 && !band[at - 1]) { BeardGrade.walk(profile, target, fixed, claimed, at - 1, -1, profile[at], climb); }
            if (at + 1 < rows && !band[at + 1]) { BeardGrade.walk(profile, target, fixed, claimed, at + 1, 1, profile[at], climb); }
        }
    }

    private static void rein(int[] profile, boolean[] fixed) {
        for (int at = 1; at < profile.length; at++) {
            if (fixed[at]) { continue; }
            profile[at] = Math.max(profile[at - 1] - 1, Math.min(profile[at - 1] + 1, profile[at]));
        }
        for (int at = profile.length - 2; at >= 0; at--) {
            if (fixed[at]) { continue; }
            profile[at] = Math.max(profile[at + 1] - 1, Math.min(profile[at + 1] + 1, profile[at]));
        }
    }

    private static int level(RailPiece rail, int rowLeast, int[] profile, int[] ground, boolean[] fixed) {
        List<StructureComponent> pieces = ContentBeard.components();
        if (pieces == null) { return 0; }
        boolean alongX = rail.alongX();
        int rows = profile.length;
        int crossings = 0;
        for (StructureComponent other : pieces) {
            if (!(other instanceof StructureVillagePieces.Path) || BeardPlots.roadAlongX(other) == alongX) { continue; }
            StructureBoundingBox road = other.getBoundingBox();
            if (!road.intersectsWith(rail.getBoundingBox().minX, rail.getBoundingBox().minZ, rail.getBoundingBox().maxX, rail.getBoundingBox().maxZ)) { continue; }
            int from = Math.max(0, (alongX ? road.minX : road.minZ) - 1 - rowLeast);
            int to = Math.min(rows - 1, (alongX ? road.maxX : road.maxZ) + 1 - rowLeast);
            if (from > to) { continue; }
            int center = Math.max(0, Math.min(rows - 1, (alongX ? (road.minX + road.maxX) / 2 : (road.minZ + road.maxZ) / 2) - rowLeast));
            int level = profile[center];
            if (rail.subway() && (ground[center] == Integer.MIN_VALUE || ground[center] - level > BeardRails.FILL)) {
                ContentLog.LOGGER.debug("Subway line {} runs {} block(s) under the street crossing it at row {}, so its grade is not held level there and climbs on at its own pace", rail.line(), ground[center] == Integer.MIN_VALUE ? "an unread number of" : String.valueOf(ground[center] - level), rowLeast + center);
                continue;
            }
            for (int at = from; at <= to; at++) {
                profile[at] = level;
                fixed[at] = true;
            }
            crossings++;
        }
        return crossings;
    }
}
