package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class ContentCityStructureSewers {
    private ContentCityStructureSewers() {}

    static void sewers(CityPlan plan, CityPlan.Line line, int bottom, int start, int[] profile, boolean[] mine, boolean[] bridged, List<ContentCityStructure.Well> wells, List<StructurePiece> hatches, StructurePiecesBuilder builder) {
        int[] around = wellBoxes(wells);
        int[] rows = sewerRows(plan, line, wells);
        int cramped = 0;
        if (line.alongX()) {
            for (int row : hatchRows(plan, line)) {
                int index = row - start;
                if (!line.covers(row) || index < 0 || index >= profile.length || bridged[index] || !plan.emitsAlong(line, row) || plazaOffLoop(wells, row, line.middle() + 1) || ContentCitySewerPiece.cramped(bottom, profile[index])) { continue; }
                hatches.add(new ContentCitySewerHatchPiece(row, line.middle() + 1, profile[index]));
            }
        }
        int from = 0;
        for (int at = 1; at <= profile.length; at++) {
            boolean ends = at == profile.length || CityPlan.districtOf(start + at, line.alongX()) != CityPlan.districtOf(start + at - 1, line.alongX()) || profile[at] != profile[from] || mine[at] != mine[from] || bridged[at] != bridged[from];
            if (!ends) { continue; }
            int least = Math.max(start + from, line.from());
            int most = Math.min(start + at - 1, line.to());
            boolean dug = mine[from] && !bridged[from] && least <= most && plan.emitsAlong(line, least);
            if (dug && ContentCitySewerPiece.cramped(bottom, profile[from])) { cramped += most - least + 1; }
            else if (dug) {
                int fromX = line.alongX() ? least : line.middle();
                int fromZ = line.alongX() ? line.middle() : least;
                int toX = line.alongX() ? most : line.middle();
                int toZ = line.alongX() ? line.middle() : most;
                builder.addPiece(new ContentCitySewerPiece(fromX, fromZ, toX, toZ, profile[from], line.middle(), line.alongX(), rows, around));
            }
            from = at;
        }
        if (cramped > 0) { ContentLog.LOGGER.debug("The sewer under the {} at {} leaves {} row(s) undug where it would not fit between y {}, the world floor plus its lining, and the street", line.alley() ? "alley" : "street", line.at(), cramped, bottom + ContentCitySewerPiece.FLOOR_LEAST); }
    }

    static void joins(CityPlan plan, int bottom, Map<CityPlan.Line, ContentCityStructure.Laid> laid, List<ContentCityStructure.Well> wells, StructurePiecesBuilder builder) {
        int[] around = wellBoxes(wells);
        for (CityPlan.Line line : plan.alongX()) { joins(plan, line, bottom, laid, sewerRows(plan, line, wells), around, builder); }
        for (CityPlan.Line line : plan.alongZ()) { joins(plan, line, bottom, laid, sewerRows(plan, line, wells), around, builder); }
    }

    private static void joins(CityPlan plan, CityPlan.Line line, int bottom, Map<CityPlan.Line, ContentCityStructure.Laid> laid, int[] rows, int[] around, StructurePiecesBuilder builder) {
        ContentCityStructure.Laid own = laid.get(line);
        if (own == null) { return; }
        for (CityPlan.Line other : plan.crossing(line)) {
            ContentCityStructure.Laid through = laid.get(other);
            if (!other.covers(line.middle()) || through == null) { continue; }
            int junction = line.middle() - through.start();
            if (junction < 0 || junction >= through.profile().length || through.bridged()[junction]) { continue; }
            int level = through.profile()[junction];
            for (int end = 0; end < 2; end++) {
                boolean low = end == 0;
                int edge = low ? line.from() : line.to();
                int beyond = low ? edge - 1 : edge + 1;
                if (beyond < other.at() || beyond > other.last()) { continue; }
                int least = low ? other.middle() + 1 : beyond;
                int most = low ? beyond : other.middle() - 1;
                int index = edge - own.start();
                if (least > most || index < 0 || index >= own.profile().length || !own.mine()[index] || own.bridged()[index] || !plan.emitsAlong(line, least) || ContentCitySewerPiece.cramped(bottom, level)) { continue; }
                int fromX = line.alongX() ? least : line.middle();
                int fromZ = line.alongX() ? line.middle() : least;
                int toX = line.alongX() ? most : line.middle();
                int toZ = line.alongX() ? line.middle() : most;
                builder.addPiece(new ContentCitySewerPiece(fromX, fromZ, toX, toZ, level, line.middle(), line.alongX(), rows, around));
                ContentLog.LOGGER.debug("The sewer under the {} at {} runs on from row {} to {} to join the sewer under the {} at {}, at that street's grade y {} where its own edge row stands at y {}", line.alley() ? "alley" : "street", line.at(), edge, low ? least : most, other.alley() ? "alley" : "street", other.at(), level, own.profile()[index]);
            }
        }
    }

    private static int[] sewerRows(CityPlan plan, CityPlan.Line line, List<ContentCityStructure.Well> wells) {
        List<Integer> rows = new ArrayList<>();
        for (CityPlan.Line other : plan.crossing(line)) {
            if (other.from() > line.last() + 1 || other.to() < line.at() - 1) { continue; }
            rows.add(other.middle());
        }
        if (ContentCity.sewerWellEntrance()) {
            for (ContentCityStructure.Well well : wells) {
                if (!well.middle().alongX().equals(line) && !well.middle().alongZ().equals(line)) { continue; }
                BoundingBox box = well.box();
                rows.add((line.alongX() ? box.minX() : box.minZ()) - ContentCitySewerLoopPiece.LOOP);
                rows.add((line.alongX() ? box.maxX() : box.maxZ()) + ContentCitySewerLoopPiece.LOOP);
            }
        }
        int[] found = new int[rows.size()];
        for (int at = 0; at < found.length; at++) { found[at] = rows.get(at); }
        return found;
    }

    private static int[] hatchRows(CityPlan plan, CityPlan.Line line) {
        List<Integer> rows = new ArrayList<>();
        for (CityPlan.Line other : plan.crossing(line)) {
            if (other.from() > line.last() + 1 || other.to() < line.at() - 1) { continue; }
            rows.add(other.middle());
        }
        int[] found = new int[rows.size()];
        for (int at = 0; at < found.length; at++) { found[at] = rows.get(at); }
        return found;
    }

    private static boolean plazaOffLoop(List<ContentCityStructure.Well> wells, int x, int z) {
        int reach = CityPlan.plazaReach();
        for (ContentCityStructure.Well well : wells) {
            BoundingBox box = well.box();
            int ring = Math.max(x < box.minX() ? box.minX() - x : Math.max(0, x - box.maxX()), z < box.minZ() ? box.minZ() - z : Math.max(0, z - box.maxZ()));
            if (ring <= reach) { return ring != ContentCitySewerLoopPiece.LOOP; }
        }
        return false;
    }

    private static int[] wellBoxes(List<ContentCityStructure.Well> wells) {
        int[] found = new int[wells.size() * 4];
        for (int at = 0; at < wells.size(); at++) {
            BoundingBox box = wells.get(at).box();
            found[at * 4] = box.minX();
            found[at * 4 + 1] = box.minZ();
            found[at * 4 + 2] = box.maxX();
            found[at * 4 + 3] = box.maxZ();
        }
        return found;
    }
}
