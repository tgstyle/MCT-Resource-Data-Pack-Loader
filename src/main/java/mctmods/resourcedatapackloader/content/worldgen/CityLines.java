package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.worldgen.CityPlan.Line;

import net.minecraft.world.level.levelgen.structure.BoundingBox;
import java.util.ArrayList;
import java.util.List;

final class CityLines {
    private CityLines() {}

    static List<Line> of(CityPlan plan) {
        List<Line> all = new ArrayList<>(plan.alongX().size() + plan.alongZ().size());
        all.addAll(plan.alongX());
        all.addAll(plan.alongZ());
        return all;
    }

    static int least(Line line, BoundingBox box) { return line.alongX() ? box.minX() : box.minZ(); }

    static int most(Line line, BoundingBox box) { return line.alongX() ? box.maxX() : box.maxZ(); }

    static boolean beside(Line street, Line other) { return other.from() <= street.last() + 1 && other.to() >= street.at() - 1; }

    static void clearBeside(CityPlan plan, Line line, int start, boolean[] rows) {
        for (Line other : plan.crossing(line)) {
            if (!beside(line, other)) { continue; }
            ContentCityStructureGrade.clearRows(rows, start, other.at() - 1, other.last() + 1);
        }
    }
}
