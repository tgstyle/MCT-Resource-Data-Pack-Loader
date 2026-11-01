package mctmods.resourcedatapackloader.common.worldgen.structure.city.road;

import mctmods.resourcedatapackloader.common.worldgen.structure.city.ContentCityStructureGrade;
import mctmods.resourcedatapackloader.common.worldgen.structure.city.layout.CityPlan;
import mctmods.resourcedatapackloader.common.worldgen.structure.city.layout.CityPlan.Line;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public final class CityLines {
    private CityLines() {}

    public static List<Line> of(CityPlan plan) {
        List<Line> all = new ArrayList<>(plan.alongX().size() + plan.alongZ().size());
        all.addAll(plan.alongX());
        all.addAll(plan.alongZ());
        return all;
    }

    public static int least(Line line, BoundingBox box) { return line.alongX() ? box.minX() : box.minZ(); }

    public static int most(Line line, BoundingBox box) { return line.alongX() ? box.maxX() : box.maxZ(); }

    public static boolean beside(Line street, Line other) { return other.from() <= street.last() + 1 && other.to() >= street.at() - 1; }

    public static void clearBeside(CityPlan plan, Line line, int start, boolean[] rows) {
        for (Line other : plan.crossing(line)) {
            if (!beside(line, other)) { continue; }
            ContentCityStructureGrade.clearRows(rows, start, other.at() - 1, other.last() + 1);
        }
    }
}
