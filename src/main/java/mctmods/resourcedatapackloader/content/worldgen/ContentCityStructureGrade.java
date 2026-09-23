package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure.GenerationContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ContentCityStructureGrade {
    private static final int LIFT_SAMPLE = 4;
    private static final int RAIL_OVER = 6;

    private ContentCityStructureGrade() {}

    static Map<Integer, Integer> decks(GenerationContext context, CityPlan plan, List<ContentCityStructure.Well> wells, Map<CityPlan.Junction, Integer> levels) {
        Map<Integer, Integer> found = new HashMap<>();
        for (int index = 0; index < plan.lifts().size(); index++) {
            CityPlan.Lift lift = plan.lifts().get(index);
            if (held(plan, lift, wells)) {
                ContentLog.LOGGER.debug("The raised stretch from {}, {} to {}, {} would lift rows that a railway or a well holds at its own level, so its streets stay at grade there", lift.fromX(), lift.fromZ(), lift.toX(), lift.toZ());
                continue;
            }
            found.put(index, highestGround(context, lift) + lift.height());
        }
        for (Map.Entry<CityPlan.Junction, Integer> crossing : levels.entrySet()) {
            for (int index : crossing.getKey().alongX().lifts()) {
                Integer deck = found.get(index);
                if (deck == null || !crossing.getKey().alongZ().lifts().contains(index)) { continue; }
                crossing.setValue(deck);
                break;
            }
        }
        return found;
    }

    private static boolean held(CityPlan plan, CityPlan.Lift lift, List<ContentCityStructure.Well> wells) {
        int reach = lift.height();
        for (CityPlan.Rail rail : plan.rails()) {
            if (rail.subway()) { continue; }
            int low = rail.alongX() ? lift.fromZ() : lift.fromX();
            int high = rail.alongX() ? lift.toZ() : lift.toX();
            int alongLow = rail.alongX() ? plan.originX() : plan.originZ();
            int alongFrom = rail.alongX() ? lift.fromX() : lift.fromZ();
            int alongTo = rail.alongX() ? lift.toX() : lift.toZ();
            if (rail.last() >= low - reach && rail.at() <= high + reach && alongTo >= alongLow && alongFrom < alongLow + CityPlan.district()) { return true; }
        }
        for (ContentCityStructure.Well well : wells) {
            BoundingBox box = well.box();
            if (box.maxX() >= lift.fromX() - reach && box.minX() <= lift.toX() + reach && box.maxZ() >= lift.fromZ() - reach && box.minZ() <= lift.toZ() + reach) { return true; }
        }
        return false;
    }

    private static int highestGround(GenerationContext context, CityPlan.Lift lift) {
        CityGround ground = CityGround.of(context);
        int sea = ground.sea();
        int highest = sea;
        int acrossCount = (lift.toZ() - lift.fromZ()) / LIFT_SAMPLE + 1;
        int[] across = new int[acrossCount];
        for (int x = lift.fromX(); x <= lift.toX(); x += LIFT_SAMPLE) {
            int count = 0;
            for (int step = 0; step < acrossCount; step++) {
                int sampled = ground.surface(x, lift.fromZ() + step * LIFT_SAMPLE);
                if (sampled >= sea - 1) { across[count++] = sampled; }
            }
            if (count * 2 <= acrossCount) { continue; }
            Arrays.sort(across, 0, count);
            highest = Math.max(highest, across[count / 2]);
        }
        return highest;
    }

    static void lift(CityPlan plan, CityPlan.Line line, int start, int[] profile, boolean[] decked, boolean[] bridged, boolean[] bored, Map<Integer, Integer> decks) {
        int lifted = 0;
        for (int index : line.lifts()) {
            Integer deck = decks.get(index);
            CityPlan.Lift lift = plan.lifts().get(index);
            if (deck == null || !lift.spans(line)) { continue; }
            int from = lift.fromAlong(line.alongX()) - start;
            int to = lift.toAlong(line.alongX()) - start;
            for (int at = 0; at < profile.length; at++) {
                if (at >= from && at <= to) {
                    profile[at] = deck;
                    decked[at] = true;
                    bridged[at] = true;
                    bored[at] = false;
                    lifted++;
                    continue;
                }
                int ramp = deck - (at < from ? from - at : at - to);
                if (profile[at] < ramp) { profile[at] = ramp; }
            }
        }
        if (lifted > 0) { ContentLog.LOGGER.debug("Raised {} row(s) of the street at {} onto the deck its city map draws there", lifted, line.at()); }
    }

    static ContentCityStructure.Graded graded(GenerationContext context, CityPlan plan, CityPlan.Line line, Map<CityPlan.Junction, Integer> levels, int run, Map<CityPlan.Rail, CityRails.Laid> rails, List<ContentCityStructure.Well> wells, boolean squaresFree) {
        int start = plan.spanStart(line);
        int span = plan.spanLength(line);
        int[] profile = new int[span];
        boolean[] held = new boolean[span];
        boolean[] square = new boolean[span];
        boolean[] mine = new boolean[span];
        boolean[] crossed = new boolean[span];
        boolean[] dry = new boolean[span];
        int wetSquares = 0;
        Arrays.fill(mine, true);
        CityGround surveyed = CityGround.of(context);
        int[] underfoot = new int[span];
        for (int at = 0; at < profile.length; at++) {
            profile[at] = CityRails.alongLine(surveyed, line, start + at);
            underfoot[at] = underfoot(surveyed, line, start + at);
        }
        CityGrade.flatRuns(profile, start, row -> CityRails.flatAt(surveyed, line, row, run));
        int[] smoothed = profile.clone();
        CityGrade.smoothRoad(smoothed);
        List<CityPlan.Line> crossing = plan.crossing(line);
        for (CityPlan.Line other : crossing) {
            CityPlan.Junction junction = line.alongX() ? new CityPlan.Junction(line, other) : new CityPlan.Junction(other, line);
            Integer level = levels.get(junction);
            if (level == null || !line.covers(other.middle()) || !other.covers(line.middle())) { continue; }
            CityGrade.pin(profile, held, start, other.at(), other.last(), level);
            if (squaresFree) { CityGrade.pin(profile, square, start, other.at(), other.last(), level); }
            boolean decked = ContentCityStructureJoins.wetSquare(surveyed, junction, wells);
            if (decked) { wetSquares++; }
            for (int row = Math.max(start, other.at()); row <= Math.min(start + span - 1, other.last()); row++) {
                crossed[row - start] = true;
                dry[row - start] |= !decked;
            }
            if (!other.beats(line)) { continue; }
            for (int row = other.at(); row <= other.last(); row++) {
                if (row >= start && row < start + mine.length) { mine[row - start] = false; }
            }
        }
        boolean[] wet = new boolean[span];
        boolean[] mouth = new boolean[span];
        for (CityPlan.Line other : crossing) {
            CityPlan.Junction junction = line.alongX() ? new CityPlan.Junction(line, other) : new CityPlan.Junction(other, line);
            Integer level = levels.get(junction);
            if (level == null || !line.covers(other.middle()) || !other.covers(line.middle())) { continue; }
            int[] rows = CityPlan.mouth(line, other, start, span);
            if (rows == null) { continue; }
            int from = rows[0];
            int to = rows[1];
            ContentCityStructure.Well plaza = ContentCityStructurePlaza.mouthPlaza(line, wells, from, to);
            int flat = plaza == null ? level : plaza.level();
            boolean decked = plaza == null && ContentCityStructureJoins.wetSquare(surveyed, junction, wells);
            int aproned = 0;
            for (int row = from; row <= to; row++) {
                if (held[row - start]) { continue; }
                profile[row - start] = flat;
                held[row - start] = true;
                square[row - start] = squaresFree;
                wet[row - start] = decked && CityPlan.underwater(surveyed, line, row);
                mouth[row - start] = true;
                aproned++;
            }
            if (!squaresFree && aproned > 0) { ContentLog.LOGGER.debug("The street along {} at {} to {} ends on the street crossing it at {}, so its mouth rows {} to {} hold the junction's level, y {}{}", line.alongX() ? "x" : "z", line.at(), line.last(), other.middle(), from, to, flat, decked ? ", decked over the water with the square it meets" : ""); }
        }
        for (int at = 0; at < span; at++) { wet[at] |= crossed[at] && !dry[at]; }
        for (ContentCityStructure.Well well : wells) { CityPlan.clampToWell(line, start, profile, held, well.box(), well.level()); }
        for (Map.Entry<CityPlan.Rail, CityRails.Laid> entry : rails.entrySet()) {
            CityPlan.Rail rail = entry.getKey();
            if (rail.subway() || rail.alongX() == line.alongX()) { continue; }
            CityRails.Laid over = entry.getValue();
            int center = line.at() + (line.width() - 1) / 2;
            if (!over.within(center) || rail.last() < start || rail.at() > start + span - 1 || rail.middle() < line.from() || rail.middle() > line.to()) { continue; }
            int level = over.level(center);
            if (over.boredAt(center)) {
                int middle = (Math.max(start, rail.at()) + Math.min(start + span - 1, rail.last())) / 2;
                if ((held[middle - start] ? profile[middle - start] : smoothed[middle - start]) >= level + RAIL_OVER) { continue; }
            }
            int railed = 0;
            for (int row = Math.max(start, rail.at() - 1); row <= Math.min(start + span - 1, rail.last() + 1); row++) {
                if (held[row - start]) { continue; }
                profile[row - start] = level;
                held[row - start] = true;
                railed++;
            }
            if (!squaresFree && railed > 0) { ContentLog.LOGGER.debug("Held {} row(s) of the street along {} at {} to the level of the railway line at {} it crosses, y {}", railed, line.alongX() ? "x" : "z", line.at(), rail.middle(), level); }
        }
        boolean[] pinned = held.clone();
        for (int at = 0; at < span; at++) { pinned[at] &= !mouth[at]; }
        boolean[] front = frontHold(surveyed, plan, line, start, profile, held, !squaresFree);
        for (int at = 0; at < span; at++) { held[at] |= front[at]; }
        CityGrade.reconcile(profile, held);
        CityGrade.smooth(profile, held);
        int[] ground = new int[profile.length];
        int afloat = 0;
        for (int at = 0; at < profile.length; at++) {
            if (profile[at] >= surveyed.sea() || !CityPlan.underwater(surveyed, line, start + at)) { continue; }
            profile[at] = surveyed.sea();
            afloat++;
        }
        boolean[] under = new boolean[span];
        boolean[] valued = new boolean[span];
        for (int at = 0; at < span; at++) {
            under[at] = CityPlan.underwater(surveyed, line, start + at);
            valued[at] = crossed[at] || mouth[at];
        }
        boolean[] open = CityGrade.openRows(under, valued);
        boolean[] authority = held.clone();
        for (int at = 0; at < span; at++) { authority[at] &= !wet[at] && !front[at]; }
        boolean[] bridged = bridges(surveyed, line, start, profile, held, authority, ground, crossed, wet);
        CityGrade.smooth(profile, held);
        boolean[] kept = pinned.clone();
        for (int at = 0; at < span; at++) { kept[at] |= mouth[at]; }
        CityGrade.rampSteps(profile, held, kept);
        CityGrade.settle(profile, held, open);
        boolean[] bored = CityGrade.bore(profile, ground, held, bridged, ContentCity.tunnelDepth(), line.endsLow() != CityPlan.End.MET, line.endsHigh() != CityPlan.End.MET);
        for (int at = 0; at < bored.length; at++) { held[at] |= bored[at]; }
        CityGrade.smooth(profile, held);
        boolean[] capped = held.clone();
        for (int at = 0; at < span; at++) {
            capped[at] &= !front[at] || underfoot[at] == Integer.MIN_VALUE || profile[at] <= underfoot[at] + CityGrade.CAP;
            held[at] &= !front[at];
        }
        int cappedRows = CityGrade.capEmbankment(profile, underfoot, capped, open);
        for (int at = 0; at < span; at++) { capped[at] &= !front[at] || underfoot[at] == Integer.MIN_VALUE || profile[at] >= underfoot[at] - CityGrade.CAP; }
        int cutRows = CityGrade.limitCut(profile, underfoot, capped);
        CityGrade.easeCaps(profile, capped, held, bridged);
        boolean[] keep = pinned.clone();
        for (int at = 0; at < keep.length; at++) { keep[at] &= !square[at]; }
        int filled = CityGrade.fillDips(profile, keep);
        CityGrade.trimPeaks(profile, capped, bridged);
        int dropped = CityGrade.deckDrops(profile, underfoot, bridged, authority, ContentCity.bridgeDrop());
        if (dropped > 0) {
            CityGrade.levelDecks(profile, bridged, authority, 1);
            for (int at = 0; at < span; at++) { capped[at] |= bridged[at]; }
        }
        int trimmed = CityGrade.groundDeckEnds(bridged, landed(surveyed, line, start, profile, bridged));
        if (!squaresFree) {
            int streetX = line.alongX() ? plan.originX() : line.at();
            int streetZ = line.alongX() ? line.at() : plan.originZ();
            int boredRows = 0;
            for (boolean row : bored) { boredRows += row ? 1 : 0; }
            if (boredRows > 0) { ContentLog.LOGGER.debug("Bored {} row(s) of tunnel on the street at {}, {}", boredRows, streetX, streetZ); }
            if (cappedRows > 0) { ContentLog.LOGGER.debug("Capped {} row(s) of the street at {}, {} at {} block(s) over the ground under them, so it does not ride an embankment", cappedRows, streetX, streetZ, CityGrade.CAP); }
            if (cutRows > 0) { ContentLog.LOGGER.debug("Held {} row(s) of the street at {}, {} no more than {} block(s) under the lowest ground near them, so it does not sink into a cut", cutRows, streetX, streetZ, CityGrade.CAP); }
            if (afloat > 0) { ContentLog.LOGGER.debug("Held {} pier row(s) of the street at {}, {} up to the water line, so they meet the decks either side", afloat, streetX, streetZ); }
            if (filled > 0) { ContentLog.LOGGER.debug("Lifted {} row(s) of the street at {}, {} out of a dip, so it carries across at the level it meets on either side", filled, streetX, streetZ); }
            if (wetSquares > 0) { ContentLog.LOGGER.debug("The street at {}, {} decks {} junction square(s) whole, since the square and the arms it meets stand mostly over water", streetX, streetZ, wetSquares); }
            if (dropped > 0) { ContentLog.LOGGER.debug("Decked {} row(s) of the street at {}, {} where its grade stands clear of the ground, so the drop is bridged rather than filled", dropped, streetX, streetZ); }
            if (trimmed > 0) { ContentLog.LOGGER.debug("The street at {}, {} lets {} end row(s) of its decks go back to road, since most of each such row stands on the ground, so every deck left is a whole rectangle", streetX, streetZ, trimmed); }
        }
        return new ContentCityStructure.Graded(start, profile, ground, capped, pinned, mine, bridged, bored);
    }

    static int underfoot(CityGround ground, CityPlan.Line line, int row) {
        int[] taken = new int[line.width()];
        int count = 0;
        for (int across = line.at(); across <= line.last(); across++) {
            int floor = line.alongX() ? ground.floor(row, across) : ground.floor(across, row);
            if (floor < ground.sea() - 1) { continue; }
            taken[count++] = floor;
        }
        if (count * 2 <= line.width()) { return Integer.MIN_VALUE; }
        Arrays.sort(taken, 0, count);
        return taken[count / 2];
    }

    private static boolean[] frontHold(CityGround ground, CityPlan plan, CityPlan.Line line, int start, int[] profile, boolean[] held, boolean told) {
        boolean[] front = new boolean[profile.length];
        int last = start + profile.length - 1;
        int before = line.from() <= start ? CityRails.alongLine(ground, line, start - 1) : Integer.MIN_VALUE;
        int after = line.to() >= last ? CityRails.alongLine(ground, line, last + 1) : Integer.MIN_VALUE;
        List<CityPlan.Plot> plots = new ArrayList<>();
        for (CityPlan near : ContentCityStructureSite.plansOver(ground, plan, line.alongX() ? start : line.at() - ContentCityStructure.FRONT_REACH, line.alongX() ? last : line.last() + ContentCityStructure.FRONT_REACH, line.alongX() ? line.at() - ContentCityStructure.FRONT_REACH : start, line.alongX() ? line.last() + ContentCityStructure.FRONT_REACH : last)) { plots.addAll(near.plots()); }
        for (CityPlan.Plot plot : plots) {
            if ((line.alongX() ? plot.toZ() : plot.toX()) < line.at() - ContentCityStructure.FRONT_REACH || (line.alongX() ? plot.fromZ() : plot.fromX()) > line.last() + ContentCityStructure.FRONT_REACH) { continue; }
            int plotLeast = line.alongX() ? plot.fromX() : plot.fromZ();
            int plotMost = line.alongX() ? plot.toX() : plot.toZ();
            int least = Math.max(start, plotLeast);
            int most = Math.min(last, plotMost);
            if (most < least) { continue; }
            int grade = profile[Mth.clamp((plotLeast + plotMost) / 2, start, last) - start];
            if (outOfReach(profile, held, front, start, least, most, grade, before, after)) {
                if (told) { ContentLog.LOGGER.debug("The frontage of plot {} at {}, {} stands at y {}, which the street along {} at {} to {} cannot reach at a walkable slope from its held rows, so it is not held to it", plot.def().key(), plot.fromX(), plot.fromZ(), grade, line.alongX() ? "x" : "z", line.at(), line.last()); }
                continue;
            }
            int fronted = 0;
            for (int row = least; row <= most; row++) {
                if (held[row - start] || front[row - start]) { continue; }
                profile[row - start] = grade;
                front[row - start] = true;
                fronted++;
            }
            if (told && fronted > 0) { ContentLog.LOGGER.debug("The frontage of plot {} at {}, {} holds rows {} to {} of the street along {} at {} to {} to y {}, the grade at its center", plot.def().key(), plot.fromX(), plot.fromZ(), least, most, line.alongX() ? "x" : "z", line.at(), line.last(), grade); }
        }
        return front;
    }

    private static boolean outOfReach(int[] profile, boolean[] held, boolean[] front, int start, int least, int most, int grade, int before, int after) {
        for (int at = 0; at < profile.length; at++) {
            if (!held[at] && !front[at]) { continue; }
            int row = start + at;
            int away = row < least ? least - row : Math.max(0, row - most);
            if (Math.abs(grade - profile[at]) > away) { return true; }
        }
        if (before != Integer.MIN_VALUE && Math.abs(grade - before) > least - start + 1) { return true; }
        return after != Integer.MIN_VALUE && Math.abs(grade - after) > start + profile.length - most;
    }

    private static boolean[] bridges(CityGround surveyed, CityPlan.Line line, int start, int[] profile, boolean[] held, boolean[] keep, int[] ground, boolean[] crossed, boolean[] wet) {
        boolean[] bridged = new boolean[profile.length];
        for (int at = 0; at < profile.length; at++) {
            int across = line.middle();
            int x = line.alongX() ? start + at : across;
            int z = line.alongX() ? across : start + at;
            int top = surveyed.surface(x, z);
            ground[at] = top;
            bridged[at] = crossed[at] ? wet[at] : top > surveyed.floor(x, z) + 1;
        }
        for (int at = 0; at < profile.length; at++) { bridged[at] |= wet[at]; }
        CityGrade.levelDecks(profile, bridged, keep, 1);
        for (int at = 0; at < profile.length; at++) { held[at] |= bridged[at]; }
        return bridged;
    }

    private static boolean[] landed(CityGround ground, CityPlan.Line line, int start, int[] profile, boolean[] bridged) {
        boolean[] landed = new boolean[profile.length];
        int sea = ground.sea();
        for (int at = 0; at < profile.length; at++) {
            if (!bridged[at]) { continue; }
            int standing = 0;
            for (int across = line.at(); across <= line.last(); across++) {
                int top = ground.floor(line.alongX() ? start + at : across, line.alongX() ? across : start + at);
                if (top >= profile[at] - 1 || (top == profile[at] - 2 && profile[at] - 1 < sea)) { standing++; }
            }
            landed[at] = standing * 2 > line.width();
        }
        return landed;
    }
}
