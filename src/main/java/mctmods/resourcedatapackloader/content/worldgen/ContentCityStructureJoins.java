package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.util.ContentLog;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.structure.Structure.GenerationContext;

public final class ContentCityStructureJoins {
    private ContentCityStructureJoins() {}

    static void raiseJunctions(GenerationContext context, CityPlan plan, Map<CityPlan.Junction, Integer> levels, int run, Map<CityPlan.Rail, CityRails.Laid> rails, List<ContentCityStructure.Well> wells) {
        Map<CityPlan.Junction, Integer> first = new HashMap<>(levels);
        List<CityPlan.Line> lines = new ArrayList<>(plan.alongX());
        lines.addAll(plan.alongZ());
        int passes = 0;
        Set<CityPlan.Junction> moved = raisedOnce(context, plan, lines, levels, run, rails, wells, null);
        while (!moved.isEmpty()) {
            passes++;
            moved = raisedOnce(context, plan, lines, levels, run, rails, wells, moved);
        }
        if (passes > 0) { ContentLog.LOGGER.debug("The crossings of the district at {}, {} took {} pass(es) of raising out of dips to settle", plan.originX(), plan.originZ(), passes); }
        for (Map.Entry<CityPlan.Junction, Integer> crossing : levels.entrySet()) {
            CityPlan.Junction junction = crossing.getKey();
            Integer was = first.get(junction);
            if (was == null || was.equals(crossing.getValue())) { continue; }
            ContentLog.LOGGER.debug("The crossing at {}, {} is raised from y {} to y {}, the level a street through it is lifted to out of a dip", junction.fromX(), junction.fromZ(), was, crossing.getValue());
        }
    }

    private static Set<CityPlan.Junction> raisedOnce(GenerationContext context, CityPlan plan, List<CityPlan.Line> lines, Map<CityPlan.Junction, Integer> levels, int run, Map<CityPlan.Rail, CityRails.Laid> rails, List<ContentCityStructure.Well> wells, @Nullable Set<CityPlan.Junction> moved) {
        Map<CityPlan.Junction, Integer> raised = new HashMap<>();
        CityGround ground = CityGround.of(context);
        for (CityPlan.Line line : lines) {
            if (moved != null && moved.stream().noneMatch(junction -> junction.alongX().equals(line) || junction.alongZ().equals(line))) { continue; }
            ContentCityStructure.Graded graded = ContentCityStructureGrade.graded(context, plan, line, levels, run, rails, wells, true);
            int start = graded.start();
            for (CityPlan.Line other : plan.crossing(line)) {
                CityPlan.Junction junction = line.alongX() ? new CityPlan.Junction(line, other) : new CityPlan.Junction(other, line);
                Integer level = levels.get(junction);
                if (level == null || !line.covers(other.middle()) || !other.covers(line.middle()) || ContentCityStructurePlaza.plazaOf(wells, junction) != null) { continue; }
                int most = level;
                for (int row = Math.max(start, other.at()); row <= Math.min(start + graded.profile().length - 1, other.last()); row++) { most = Math.max(most, graded.profile()[row - start]); }
                CityPlan.Line owner = CityPlan.setter(junction);
                int reached = wetCross(ground, junction) ? most : reachable(plan, owner, owner.equals(line) ? other : line, levels, most);
                if (reached > level) { raised.merge(junction, reached, Math::max); }
            }
        }
        levels.putAll(raised);
        return raised.keySet();
    }

    private static int reachable(CityPlan plan, CityPlan.Line owner, CityPlan.Line met, Map<CityPlan.Junction, Integer> levels, int grade) {
        int low = Integer.MIN_VALUE;
        int high = Integer.MAX_VALUE;
        int before = Integer.MIN_VALUE;
        int after = Integer.MAX_VALUE;
        int beforeLevel = 0;
        int afterLevel = 0;
        for (CityPlan.Line other : plan.crossing(owner)) {
            if (other.equals(met) || !owner.covers(other.middle()) || !other.covers(owner.middle())) { continue; }
            Integer level = levels.get(owner.alongX() ? new CityPlan.Junction(owner, other) : new CityPlan.Junction(other, owner));
            if (level == null) { continue; }
            if (other.last() < met.at() && other.last() > before) {
                before = other.last();
                beforeLevel = level;
            }
            if (other.at() > met.last() && other.at() < after) {
                after = other.at();
                afterLevel = level;
            }
        }
        if (before != Integer.MIN_VALUE) {
            low = beforeLevel - (met.at() - before);
            high = beforeLevel + (met.at() - before);
        }
        if (after != Integer.MAX_VALUE) {
            low = Math.max(low, afterLevel - (after - met.last()));
            high = Math.min(high, afterLevel + (after - met.last()));
        }
        if (low > high) { return grade < low ? low : high; }
        return Mth.clamp(grade, low, high);
    }

    static boolean wetSquare(CityGround ground, CityPlan.Junction junction, List<ContentCityStructure.Well> wells) { return ContentCityStructurePlaza.plazaOf(wells, junction) == null && wetCross(ground, junction); }

    static boolean wetCross(CityGround ground, CityPlan.Junction junction) {
        CityPlan.Line row = junction.alongX();
        CityPlan.Line column = junction.alongZ();
        if (row.alley() || column.alley()) { return false; }
        for (int x = column.at(); x <= column.last(); x++) {
            if (!wetStrip(ground, x, x, row.at(), row.last())) { return false; }
        }
        for (int z = row.at(); z <= row.last(); z++) {
            if (!wetStrip(ground, column.at(), column.last(), z, z)) { return false; }
        }
        if (row.from() < column.at() && !wetStrip(ground, column.at() - 1, column.at() - 1, row.at(), row.last())) { return false; }
        if (row.to() > column.last() && !wetStrip(ground, column.last() + 1, column.last() + 1, row.at(), row.last())) { return false; }
        if (column.from() < row.at() && !wetStrip(ground, column.at(), column.last(), row.at() - 1, row.at() - 1)) { return false; }
        return column.to() <= row.last() || wetStrip(ground, column.at(), column.last(), row.last() + 1, row.last() + 1);
    }

    private static boolean wetStrip(CityGround ground, int minX, int maxX, int minZ, int maxZ) {
        int wet = 0;
        int cells = 0;
        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                cells++;
                if (ground.floor(x, z) < ground.sea() - 2) { wet++; }
            }
        }
        return wet * 2 > cells;
    }
}
