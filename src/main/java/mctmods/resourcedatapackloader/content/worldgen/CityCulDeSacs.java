package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.worldgen.CityPlan.Court;
import mctmods.resourcedatapackloader.content.worldgen.CityPlan.End;
import mctmods.resourcedatapackloader.content.worldgen.CityPlan.Junction;
import mctmods.resourcedatapackloader.content.worldgen.CityPlan.Line;
import mctmods.resourcedatapackloader.content.worldgen.CityPlan.Plot;
import mctmods.resourcedatapackloader.content.worldgen.CityPlan.Rail;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.world.level.levelgen.structure.BoundingBox;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

final class CityCulDeSacs {
    enum Hold { STREET, LANE, PLOT, FIXED }

    record Held(int minX, int minZ, int maxX, int maxZ, Hold hold, boolean alongX) {
        static Held ofPlot(Plot plot) { return new Held(plot.fromX(), plot.fromZ(), plot.toX(), plot.toZ(), Hold.PLOT, true); }

        static Held ofBox(BoundingBox box, Hold hold) { return new Held(box.minX(), box.minZ(), box.maxX(), box.maxZ(), hold, true); }

        static Held ofWell(BoundingBox well) { return ofBox(well, Hold.FIXED); }

        static Held ofRail(Rail rail) { return rail.alongX() ? new Held(rail.from(), rail.at(), rail.to(), rail.last(), Hold.FIXED, true) : new Held(rail.at(), rail.from(), rail.last(), rail.to(), Hold.FIXED, false); }

        BoundingBox box() { return new BoundingBox(minX, 0, minZ, maxX, 0, maxZ); }

        boolean meets(int fromX, int fromZ, int toX, int toZ) { return box().intersects(fromX, fromZ, toX, toZ); }
    }

    record StreetGrade(int start, int[] profile, boolean[] bored) {}

    private CityCulDeSacs() {}

    static boolean steep(CityGround ground, Line tie, List<Line> roads) {
        if (ContentCity.tunnelDepth() <= 0) { return false; }
        List<Line> with = new ArrayList<>(roads);
        with.add(tie);
        Map<Line, StreetGrade> grades = new HashMap<>();
        for (boolean bored : grades.computeIfAbsent(tie, line -> streetGrade(ground, line, with)).bored()) {
            if (bored) { return true; }
        }
        return crossesHill(ground, grades, roads, held(tie));
    }

    static Held held(Line line) { return line.alongX() ? new Held(line.from(), line.at(), line.to(), line.last(), line.alley() ? Hold.LANE : Hold.STREET, true) : new Held(line.at(), line.from(), line.last(), line.to(), line.alley() ? Hold.LANE : Hold.STREET, false); }

    static boolean crossesHill(CityGround ground, Map<Line, StreetGrade> grades, List<Line> roads, Held box) {
        if (ContentCity.tunnelDepth() <= 0) { return false; }
        for (Line road : roads) {
            boolean alongX = road.alongX();
            if ((alongX ? box.minZ() : box.minX()) > road.last() + 3 || (alongX ? box.maxZ() : box.maxX()) < road.at() - 3) { continue; }
            StreetGrade grade = grades.computeIfAbsent(road, line -> streetGrade(ground, line, roads));
            int least = Math.max(grade.start(), (alongX ? box.minX() : box.minZ()) - 1);
            int most = Math.min(grade.start() + grade.bored().length - 1, (alongX ? box.maxX() : box.maxZ()) + 1);
            for (int row = least; row <= most; row++) {
                if (grade.bored()[row - grade.start()]) { return true; }
            }
        }
        return false;
    }

    static StreetGrade streetGrade(CityGround ground, Line line, List<Line> lines) {
        int start = line.from();
        int[] profile = new int[line.to() - line.from() + 1];
        boolean[] held = new boolean[profile.length];
        for (int at = 0; at < profile.length; at++) { profile[at] = CityRails.alongLine(ground, line, start + at); }
        CityGrade.flatRuns(profile, start, row -> CityRails.flatAt(ground, line, row, CityPlan.flatRun()));
        int[] natural = profile.clone();
        Map<Line, int[]> profiles = new HashMap<>();
        for (Line other : lines) {
            if (other.alongX() == line.alongX() || !line.covers(other.middle()) || !other.covers(line.middle())) { continue; }
            CityGrade.pin(profile, held, start, other.at(), other.last(), CityPlan.junctionLevel(ground, Junction.of(line, other), profiles));
        }
        CityGrade.reconcile(profile, held);
        CityGradeRails.rein(profile, held);
        boolean[] bored = CityGrade.bore(profile, natural, held, new boolean[profile.length], ContentCity.tunnelDepth(), line.endsLow() != End.MET, line.endsHigh() != End.MET);
        return new StreetGrade(start, profile, bored);
    }

    static void makeWay(List<Plot> found, List<Held> held, Held court) {
        for (Plot plot : List.copyOf(found)) {
            if (!court.meets(plot.fromX(), plot.fromZ(), plot.toX(), plot.toZ())) { continue; }
            Held own = Held.ofPlot(plot);
            int[][] pushes = {{court.maxX() + 1 - plot.fromX(), 0}, {court.minX() - 1 - plot.toX(), 0}, {0, court.maxZ() + 1 - plot.fromZ()}, {0, court.minZ() - 1 - plot.toZ()}};
            Arrays.sort(pushes, Comparator.comparingInt(push -> Math.abs(push[0]) + Math.abs(push[1])));
            held.remove(own);
            found.remove(plot);
            Plot moved = null;
            for (int[] push : pushes) {
                Plot tried = new Plot(plot.fromX() + push[0], plot.fromZ() + push[1], plot.toX() + push[0], plot.toZ() + push[1], plot.street(), plot.lower(), plot.def(), plot.back());
                if (standsFree(held, court, tried)) {
                    moved = tried;
                    break;
                }
            }
            if (moved == null) {
                ContentLog.LOGGER.debug("Plot {} at {}, {} makes way for the cul-de-sac at {}, {}, having no room to slide out of it", plot.def().key(), plot.fromX(), plot.fromZ(), court.minX(), court.minZ());
                continue;
            }
            found.add(moved);
            held.add(Held.ofPlot(moved));
            ContentLog.LOGGER.debug("Plot {} at {}, {} slides {}, {} out of the cul-de-sac at {}, {}", plot.def().key(), plot.fromX(), plot.fromZ(), moved.fromX() - plot.fromX(), moved.fromZ() - plot.fromZ(), court.minX(), court.minZ());
        }
    }

    private static boolean standsFree(List<Held> held, Held court, Plot tried) {
        if (court.meets(tried.fromX(), tried.fromZ(), tried.toX(), tried.toZ())) { return false; }
        for (Held other : held) {
            if (other.meets(tried.fromX(), tried.fromZ(), tried.toX(), tried.toZ())) { return false; }
        }
        return true;
    }

    static Court court(CityGround ground, Line street, boolean low, List<Held> held, int level) {
        int end = low ? street.from() : street.to();
        int endX = street.alongX() ? end : street.middle();
        int endZ = street.alongX() ? street.middle() : end;
        Held own = held(street);
        CityCourtSearch.Seat seat = CityCourtSearch.seat(ground, street, low, level, null, (x, z, radius, box) -> nearDisc(held, own, x, z, radius) || yields(held, own, Held.ofBox(box, Hold.STREET)));
        if (seat.verdict() == CityCourtSearch.Verdict.SEATED) { return new Court(street, low, seat.radius(), seat.stem()); }
        if (seat.verdict() == CityCourtSearch.Verdict.WET) { ContentLog.LOGGER.debug("The dead end at {}, {} of the drawn city rolled a cul-de-sac, but water stands under the court it would take, so it stays a plain end", endX, endZ); }
        else if (seat.verdict() == CityCourtSearch.Verdict.CUT) { ContentLog.LOGGER.debug("The dead end at {}, {} of the drawn city rolled a cul-de-sac, but every court it could seat would cut the ground beside it, so it stays a plain end", endX, endZ); }
        else { ContentLog.LOGGER.debug("The dead end at {}, {} of the drawn city rolled a cul-de-sac, but finds no room for a court at any size it could take, so it stays a plain end", endX, endZ); }
        return new Court(street, low, 0, 0);
    }

    static Held courtBox(Line street, int end, int dir, int radius, int stem) { return held(courtLine(street, end, dir, radius, stem)); }

    static Line courtLine(Line street, int end, int dir, int radius, int stem) {
        int near = end + dir;
        int far = end + dir * (stem + 2 * radius + 1);
        return Line.met(street.middle() - radius, 2 * radius + 1, street.alongX(), Math.min(near, far), Math.max(near, far));
    }

    private static boolean nearDisc(List<Held> held, Held own, int discX, int discZ, int radius) {
        for (Held other : held) {
            if (!other.equals(own) && CityCourtSearch.crowds(other.minX(), other.minZ(), other.maxX(), other.maxZ(), discX, discZ, radius)) { return true; }
        }
        return false;
    }

    private static boolean yields(List<Held> held, Held own, Held court) {
        for (Held other : held) {
            if (other.equals(own) || other.hold() == Hold.LANE || other.hold() == Hold.PLOT) { continue; }
            if (other.meets(court.minX(), court.minZ(), court.maxX(), court.maxZ())) { return true; }
        }
        return false;
    }
}
