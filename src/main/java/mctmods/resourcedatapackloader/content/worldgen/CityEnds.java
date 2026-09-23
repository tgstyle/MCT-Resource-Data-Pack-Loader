package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.worldgen.CityCulDeSacs.Held;
import mctmods.resourcedatapackloader.content.worldgen.CityCulDeSacs.Hold;
import mctmods.resourcedatapackloader.content.worldgen.CityCulDeSacs.StreetGrade;
import mctmods.resourcedatapackloader.content.worldgen.CityPlan.End;
import mctmods.resourcedatapackloader.content.worldgen.CityPlan.Line;
import mctmods.resourcedatapackloader.content.worldgen.CityPlan.Rail;
import mctmods.resourcedatapackloader.content.worldgen.CityPlan.Town;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.world.level.levelgen.structure.BoundingBox;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

final class CityEnds {
    private static final int ATTACH_GAP = 8;
    private static final int MERGE_ROWS = 3;

    private record Closing(CityGround ground, List<Line> rows, List<Line> columns, List<BoundingBox> wells, List<Rail> rails, int block, boolean told, Map<Line, StreetGrade> grades) {
        List<Line> of(boolean alongX) { return alongX ? rows : columns; }

        List<Line> everyone() {
            List<Line> all = new ArrayList<>(rows);
            all.addAll(columns);
            return all;
        }

        void say(String text, Object... values) {
            if (told) { ContentLog.LOGGER.debug(text, values); }
        }
    }

    private record Dead(Line line, boolean alongX, boolean outward, int end, int acrossLo, int acrossHi, int endX, int endZ) {}

    private CityEnds() {}

    static int attachGap() { return Math.max(ATTACH_GAP, CityPlan.fullWidth() + 2); }

    static void closeEnds(CityGround ground, @Nullable Town town, List<Line> rows, List<Line> columns, boolean hinted, boolean told) {
        long seed = ground.seed();
        List<BoundingBox> wells = town == null ? List.of() : CityLayout.mapWells(town);
        List<Rail> rails = town == null ? List.of() : CityRails.placed(seed, town);
        int block = town == null ? ContentVillages.largestPlot() : CityPlanPlots.townBlock(seed, town);
        Closing closing = new Closing(ground, rows, columns, wells, rails, block, told, new HashMap<>());
        int[] drawn = {rows.size(), columns.size()};
        for (int axis = 0; axis < 2; axis++) {
            List<Line> lines = closing.of(axis == 0);
            for (int index = 0; index < drawn[axis]; index++) {
                for (int side = 0; side < 2; side++) {
                    Line line = lines.get(index);
                    End end = side == 1 ? line.endsHigh() : line.endsLow();
                    if (line.alley() || end == End.MET || hinted && end == End.COURT) { continue; }
                    closeEnd(closing, line, side == 1);
                }
            }
        }
    }

    private static void closeEnd(Closing closing, Line line, boolean outward) {
        boolean alongX = line.alongX();
        int end = outward ? line.to() : line.from();
        Dead dead = new Dead(line, alongX, outward, end, line.at(), line.last(), alongX ? end : line.middle(), alongX ? line.middle() : end);
        List<Line> everyone = closing.everyone();
        if (metBeyond(everyone, line, alongX, end + (outward ? 1 : -1), line.at(), line.last())) { return; }
        int attachGap = attachGap();
        Line best = null;
        boolean bestCollinear = false;
        int bestScore = Integer.MAX_VALUE;
        for (Line other : everyone) {
            if (other.equals(line) || other.alley()) { continue; }
            if (other.alongX() == alongX) {
                if (other.last() < dead.acrossLo() || other.at() > dead.acrossHi()) { continue; }
                int gap = outward ? other.from() - end : end - other.to();
                if (gap < 2 || gap > attachGap * 2 || gap >= bestScore) { continue; }
                bestScore = gap;
                best = other;
                bestCollinear = true;
                continue;
            }
            int ahead = outward ? other.at() - end : end - other.last();
            if (ahead < 2 || ahead > attachGap * 2) { continue; }
            boolean overlaps = other.to() >= dead.acrossLo() && other.from() <= dead.acrossHi();
            int sideGap = overlaps ? 0 : other.from() > dead.acrossHi() ? other.from() - dead.acrossHi() - 1 : dead.acrossLo() - other.to() - 1;
            if (sideGap > attachGap || ahead + sideGap >= bestScore) { continue; }
            bestScore = ahead + sideGap;
            best = other;
            bestCollinear = false;
        }
        if (best == null) { return; }
        if (bestCollinear) { collinear(closing, dead, best); }
        else { crossing(closing, dead, best); }
    }

    private static void collinear(Closing closing, Dead dead, Line met) {
        boolean outward = dead.outward();
        int metNear = outward ? met.from() : met.to();
        int cFrom = outward ? dead.end() + 1 : metNear + 1;
        int cTo = outward ? metNear - 1 : dead.end() - 1;
        int offset = (met.at() + met.last()) / 2 - (dead.acrossLo() + dead.acrossHi()) / 2;
        BoundingBox metBox = CityMapDistricts.box(met);
        if (offset != 0) {
            if (!mergeInto(closing, dead, met, cFrom, cTo, offset)) { crossStreet(closing, dead, met, cFrom, cTo); }
            return;
        }
        BoundingBox strip = strip(dead, cFrom, cTo);
        if (held(closing, dead.line(), met, strip)) {
            closing.say("The dead end at {}, {} cannot reach the facing street at {}, {}: the strip is held", dead.endX(), dead.endZ(), metBox.minX(), metBox.minZ());
            return;
        }
        Line beside = beside(closing, strip, dead.alongX());
        if (beside != null) {
            closing.say("The dead end at {}, {} cannot reach the facing street at {}, {}: it would run beside the road at {}, {}", dead.endX(), dead.endZ(), metBox.minX(), metBox.minZ(), CityMapDistricts.box(beside).minX(), CityMapDistricts.box(beside).minZ());
            return;
        }
        swap(closing, dead.line(), reached(dead.line(), outward, outward ? metNear - 1 : metNear + 1));
        swap(closing, met, reached(met, !outward, metNear));
        closing.say("The dead end at {}, {} reaches the facing street at {}, {} and joins it", dead.endX(), dead.endZ(), metBox.minX(), metBox.minZ());
    }

    private static void crossing(Closing closing, Dead dead, Line met) {
        boolean alongX = dead.alongX();
        boolean outward = dead.outward();
        BoundingBox metBox = CityMapDistricts.box(met);
        boolean overlaps = met.to() >= dead.acrossLo() && met.from() <= dead.acrossHi();
        int from = outward ? dead.end() + 1 : overlaps ? met.last() + 1 : met.at();
        int to = outward ? (overlaps ? met.at() - 1 : met.last()) : dead.end() - 1;
        BoundingBox strip = strip(dead, from, to);
        if (held(closing, dead.line(), met, strip)) {
            closing.say("The dead end at {}, {} cannot reach the road at {}, {}: a well or road holds the strip", dead.endX(), dead.endZ(), metBox.minX(), metBox.minZ());
            return;
        }
        Line beside = beside(closing, strip, alongX);
        if (beside != null) {
            closing.say("The dead end at {}, {} cannot reach the road at {}, {}: it would run beside the road at {}, {}", dead.endX(), dead.endZ(), metBox.minX(), metBox.minZ(), CityMapDistricts.box(beside).minX(), CityMapDistricts.box(beside).minZ());
            return;
        }
        if (hill(closing, List.of(met), strip)) {
            closing.say("The dead end at {}, {} cannot close into the road at {}, {}: it would meet it inside its tunnel through a hill", dead.endX(), dead.endZ(), metBox.minX(), metBox.minZ());
            return;
        }
        if (buriedEnd(closing, dead, outward ? to : from)) {
            closing.say("The dead end at {}, {} cannot close into the road at {}, {}: the longer road would not walk end to end", dead.endX(), dead.endZ(), metBox.minX(), metBox.minZ());
            return;
        }
        Line trimmed = met;
        if (!overlaps) {
            boolean beyond = met.from() > dead.acrossHi();
            int metFrom = beyond ? dead.acrossHi() + 1 : met.to() + 1;
            int metTo = beyond ? met.from() - 1 : dead.acrossLo() - 1;
            if (metFrom <= metTo) {
                BoundingBox side = alongX ? new BoundingBox(met.at(), 0, metFrom, met.last(), 0, metTo) : new BoundingBox(metFrom, 0, met.at(), metTo, 0, met.last());
                if (held(closing, dead.line(), met, side)) { return; }
                Line metBeside = beside(closing, side, !alongX);
                if (metBeside != null) {
                    closing.say("The dead end at {}, {} cannot close into a corner with the road at {}, {}: that road would run beside the road at {}, {}", dead.endX(), dead.endZ(), metBox.minX(), metBox.minZ(), CityMapDistricts.box(metBeside).minX(), CityMapDistricts.box(metBeside).minZ());
                    return;
                }
            }
            trimmed = reached(met, !beyond, beyond ? dead.acrossLo() : dead.acrossHi());
        }
        swap(closing, dead.line(), reached(dead.line(), outward, outward ? met.last() : met.at()));
        swap(closing, met, trimmed);
        closing.say("The dead end at {}, {} reaches the road at {}, {} and closes into a {}", dead.endX(), dead.endZ(), metBox.minX(), metBox.minZ(), overlaps ? "junction" : "corner");
    }

    private static boolean mergeInto(Closing closing, Dead dead, Line met, int cFrom, int cTo, int offset) {
        boolean alongX = dead.alongX();
        int rows = cTo - cFrom + 1;
        int shift = Math.abs(offset);
        BoundingBox metBox = CityMapDistricts.box(met);
        int full = CityPlan.fullWidth();
        if (shift > (full - 1) / 2 || rows < MERGE_ROWS * shift) {
            closing.say("The dead end at {}, {} faces the street at {}, {} {} block(s) off its line with {} row(s) between them, too far off or too close to merge", dead.endX(), dead.endZ(), metBox.minX(), metBox.minZ(), shift, rows);
            return false;
        }
        int unionLo = Math.min(dead.acrossLo(), met.at());
        int unionHi = Math.max(dead.acrossHi(), met.last());
        BoundingBox merge = alongX ? new BoundingBox(cFrom, 0, unionLo, cTo, 0, unionHi) : new BoundingBox(unionLo, 0, cFrom, unionHi, 0, cTo);
        if (hill(closing, closing.everyone(), merge)) {
            closing.say("The dead end at {}, {} cannot merge into the street at {}, {}: the merge would meet a tunnel through a hill", dead.endX(), dead.endZ(), metBox.minX(), metBox.minZ());
            return false;
        }
        if (held(closing, dead.line(), met, merge)) {
            closing.say("The dead end at {}, {} cannot merge into the street at {}, {}: the ground between them is held", dead.endX(), dead.endZ(), metBox.minX(), metBox.minZ());
            return false;
        }
        Line beside = beside(closing, merge, alongX);
        if (beside != null) {
            closing.say("The dead end at {}, {} cannot merge into the street at {}, {}: the merge would run beside the road at {}, {}", dead.endX(), dead.endZ(), metBox.minX(), metBox.minZ(), CityMapDistricts.box(beside).minX(), CityMapDistricts.box(beside).minZ());
            return false;
        }
        int ownCenter = (dead.acrossLo() + dead.acrossHi()) / 2;
        int metCenter = (met.at() + met.last()) / 2;
        int fromCenter = dead.outward() ? ownCenter : metCenter;
        int toCenter = dead.outward() ? metCenter : ownCenter;
        int half = (full - 1) / 2;
        int run = cFrom;
        for (int row = cFrom; row <= cTo; row++) {
            int center = centerAt(row, cFrom, rows, fromCenter, toCenter);
            if (row < cTo && centerAt(row + 1, cFrom, rows, fromCenter, toCenter) == center) { continue; }
            closing.of(alongX).add(new Line(center - half, half * 2 + 1, false, alongX, run, row, End.MET, End.MET));
            run = row + 1;
        }
        swap(closing, dead.line(), reached(dead.line(), dead.outward(), dead.end()));
        swap(closing, met, reached(met, !dead.outward(), dead.outward() ? met.from() : met.to()));
        closing.say("The dead end at {}, {} meets the street at {}, {} {} block(s) off its line and merges into it over {} row(s) at {}, {}", dead.endX(), dead.endZ(), metBox.minX(), metBox.minZ(), shift, rows, merge.minX(), merge.minZ());
        return true;
    }

    private static int centerAt(int row, int least, int rows, int fromCenter, int toCenter) {
        int shift = toCenter - fromCenter;
        int steps = Math.abs(shift);
        if (steps == 0 || rows <= 1) { return toCenter; }
        int level = Math.min(steps, (int) Math.floor((row - least + 0.5) * (steps + 1) / rows));
        return fromCenter + Integer.signum(shift) * level;
    }

    private static void crossStreet(Closing closing, Dead dead, Line met, int cFrom, int cTo) {
        boolean alongX = dead.alongX();
        boolean outward = dead.outward();
        BoundingBox metBox = CityMapDistricts.box(met);
        int full = CityPlan.fullWidth();
        if (cTo - cFrom + 1 < full) {
            closing.say("The dead end at {}, {} faces the offset street at {}, {} too closely for a cross street to tie them, so both stay closed", dead.endX(), dead.endZ(), metBox.minX(), metBox.minZ());
            return;
        }
        int conLo = outward ? cTo - full + 1 : cFrom;
        int conHi = outward ? cTo : cFrom + full - 1;
        int unionLo = Math.min(dead.acrossLo(), met.at());
        int unionHi = Math.max(dead.acrossHi(), met.last());
        BoundingBox avenue = alongX ? new BoundingBox(conLo, 0, unionLo, conHi, 0, unionHi) : new BoundingBox(unionLo, 0, conLo, unionHi, 0, conHi);
        if (hill(closing, closing.everyone(), avenue)) {
            closing.say("The dead end at {}, {} cannot tie to the offset street at {}, {}: the cross street would meet a tunnel through a hill", dead.endX(), dead.endZ(), metBox.minX(), metBox.minZ());
            return;
        }
        if (held(closing, dead.line(), met, avenue)) {
            closing.say("The dead end at {}, {} cannot tie to the offset street at {}, {}: the cross street's ground is held", dead.endX(), dead.endZ(), metBox.minX(), metBox.minZ());
            return;
        }
        Line beside = beside(closing, avenue, !alongX);
        if (beside != null) {
            closing.say("The dead end at {}, {} cannot tie to the offset street at {}, {}: the cross street would run beside the road at {}, {}", dead.endX(), dead.endZ(), metBox.minX(), metBox.minZ(), CityMapDistricts.box(beside).minX(), CityMapDistricts.box(beside).minZ());
            return;
        }
        int exFrom = outward ? dead.end() + 1 : conHi + 1;
        int exTo = outward ? conLo - 1 : dead.end() - 1;
        if (exFrom <= exTo) {
            BoundingBox own = strip(dead, exFrom, exTo);
            if (held(closing, dead.line(), met, own)) {
                closing.say("The dead end at {}, {} cannot reach the cross street that would tie it: its own strip is held", dead.endX(), dead.endZ());
                return;
            }
            if (beside(closing, own, alongX) != null) {
                closing.say("The dead end at {}, {} cannot reach the cross street that would tie it: its own strip would run beside another road", dead.endX(), dead.endZ());
                return;
            }
        }
        closing.of(!alongX).add(new Line(conLo, full, false, !alongX, unionLo, unionHi, End.MET, End.MET));
        swap(closing, dead.line(), reached(dead.line(), outward, outward ? conHi : conLo));
        swap(closing, met, reached(met, !outward, outward ? conLo : conHi));
        closing.say("The dead end at {}, {} and the offset street at {}, {} tie through a cross street at {}, {}", dead.endX(), dead.endZ(), metBox.minX(), metBox.minZ(), avenue.minX(), avenue.minZ());
    }

    static boolean metBeyond(List<Line> lines, Line line, boolean alongX, int beyond, int acrossLo, int acrossHi) {
        for (Line other : lines) {
            if (other.equals(line) || other.alley()) { continue; }
            BoundingBox met = CityMapDistricts.box(other);
            int lo = alongX ? met.minX() : met.minZ();
            int hi = alongX ? met.maxX() : met.maxZ();
            if (beyond < lo - 1 || beyond > hi + 1) { continue; }
            int oLo = alongX ? met.minZ() : met.minX();
            int oHi = alongX ? met.maxZ() : met.maxX();
            if (oHi < acrossLo || oLo > acrossHi) { continue; }
            return true;
        }
        return false;
    }

    private static boolean held(Closing closing, Line line, Line met, BoundingBox box) {
        if (box.maxX() < box.minX() || box.maxZ() < box.minZ()) { return false; }
        for (Line other : closing.everyone()) {
            if (other.equals(line) || other.equals(met) || other.alley()) { continue; }
            if (CityMapDistricts.box(other).intersects(box)) { return true; }
        }
        for (BoundingBox well : closing.wells()) {
            if (well.intersects(box)) { return true; }
        }
        BoundingBox whole = whole(line, box);
        for (Rail rail : closing.rails()) {
            if (bed(rail).intersects(box) && !railCrosses(rail, whole)) { return true; }
        }
        return false;
    }

    static boolean stubHeld(CityGround ground, Town town, Town neighbor, Line street, BoundingBox strip) {
        long seed = ground.seed();
        List<Line> rows = new ArrayList<>();
        List<Line> columns = new ArrayList<>();
        List<BoundingBox> wells = new ArrayList<>();
        List<Rail> rails = new ArrayList<>();
        for (Town one : List.of(town, neighbor)) {
            for (Line line : one.drawn()) { (line.alongX() ? rows : columns).add(line); }
            wells.addAll(CityLayout.mapWells(one));
            rails.addAll(CityRails.placed(seed, one));
        }
        Closing closing = new Closing(ground, rows, columns, wells, rails, CityPlanPlots.townBlock(seed, town), false, new HashMap<>());
        return stubCrosses(closing, town, street, strip) || beside(closing, strip, street.alongX()) != null || hill(closing, closing.everyone(), strip);
    }

    private static boolean stubCrosses(Closing closing, Town town, Line street, BoundingBox strip) {
        for (Line other : closing.everyone()) {
            if (other.equals(street) || other.alley() || other.width() < CityPlan.fullWidth() || !CityMapDistricts.box(other).intersects(strip)) { continue; }
            if (other.alongX() != street.alongX() && town.drawn().contains(other)) { continue; }
            return true;
        }
        for (BoundingBox well : closing.wells()) {
            if (well.intersects(strip)) { return true; }
        }
        List<Rail> own = CityRails.placed(closing.ground().seed(), town);
        BoundingBox whole = whole(street, strip);
        for (Rail rail : closing.rails()) {
            if (bed(rail).intersects(strip) && !(own.contains(rail) && railCrosses(rail, whole))) { return true; }
        }
        return false;
    }

    private static BoundingBox whole(Line line, BoundingBox box) {
        BoundingBox road = CityMapDistricts.box(line);
        return new BoundingBox(Math.min(road.minX(), box.minX()), 0, Math.min(road.minZ(), box.minZ()), Math.max(road.maxX(), box.maxX()), 0, Math.max(road.maxZ(), box.maxZ()));
    }

    private static BoundingBox bed(Rail rail) { return rail.alongX() ? new BoundingBox(rail.from(), 0, rail.at(), rail.to(), 0, rail.last()) : new BoundingBox(rail.at(), 0, rail.from(), rail.last(), 0, rail.to()); }

    private static boolean railCrosses(Rail rail, BoundingBox box) {
        boolean boxAlongX = box.maxX() - box.minX() >= box.maxZ() - box.minZ();
        if (rail.alongX() == boxAlongX) { return false; }
        int least = rail.alongX() ? box.minZ() : box.minX();
        int most = rail.alongX() ? box.maxZ() : box.maxX();
        return least <= rail.at() - CityRails.MARGIN && most >= rail.last() + CityRails.MARGIN;
    }

    @Nullable private static Line beside(Closing closing, BoundingBox strip, boolean alongX) {
        int side = closing.block() * 2;
        for (Line other : closing.everyone()) {
            if (other.alongX() != alongX || other.width() <= 3) { continue; }
            int overlap = Math.min(alongX ? strip.maxX() : strip.maxZ(), other.to()) - Math.max(alongX ? strip.minX() : strip.minZ(), other.from());
            if (overlap < 0) { continue; }
            int gap = Math.max(other.at() - (alongX ? strip.maxZ() : strip.maxX()), (alongX ? strip.minZ() : strip.minX()) - other.last());
            if (gap > 0 && gap - 1 < side) { return other; }
        }
        return null;
    }

    private static boolean hill(Closing closing, List<Line> roads, BoundingBox box) {
        if (ContentCity.tunnelDepth() <= 0) { return false; }
        List<Line> everyone = closing.everyone();
        for (Line road : roads) { closing.grades().computeIfAbsent(road, line -> CityCulDeSacs.streetGrade(closing.ground(), line, everyone)); }
        return CityCulDeSacs.crossesHill(closing.ground(), closing.grades(), roads, new Held(box.minX(), box.minZ(), box.maxX(), box.maxZ(), Hold.STREET, true));
    }

    private static boolean buriedEnd(Closing closing, Dead dead, int reach) {
        if (ContentCity.tunnelDepth() <= 0) { return false; }
        Line line = dead.line();
        Line longer = line.reaching(dead.outward() ? line.from() : reach, dead.outward() ? reach : line.to()).ending(End.BARE, End.BARE);
        boolean[] bored = CityCulDeSacs.streetGrade(closing.ground(), longer, List.of()).bored();
        return bored[dead.outward() ? bored.length - 1 : 0];
    }

    private static BoundingBox strip(Dead dead, int from, int to) { return dead.alongX() ? new BoundingBox(from, 0, dead.acrossLo(), to, 0, dead.acrossHi()) : new BoundingBox(dead.acrossLo(), 0, from, dead.acrossHi(), 0, to); }

    private static Line reached(Line line, boolean high, int end) { return line.reaching(high ? line.from() : end, high ? end : line.to()).ending(high ? line.endsLow() : End.MET, high ? End.MET : line.endsHigh()); }

    private static void swap(Closing closing, Line was, Line now) {
        List<Line> lines = closing.of(was.alongX());
        int at = lines.indexOf(was);
        if (at >= 0) { lines.set(at, now); }
    }
}
