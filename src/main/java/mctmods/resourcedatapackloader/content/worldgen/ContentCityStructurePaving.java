package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure.GenerationContext;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public final class ContentCityStructurePaving {
    private ContentCityStructurePaving() {}

    static int[] lay(GenerationContext context, CityPlan plan, CityPlan.Line line, Map<CityPlan.Junction, Integer> levels, int run, Map<CityPlan.Rail, CityRails.Laid> rails, List<ContentCityStructure.Well> wells, Map<Integer, Integer> decks, Map<CityPlan.Line, int[]> seats, Map<CityPlan.Line, ContentCityStructure.Laid> laid, List<ContentCityStructure.Bulb> bulbs, List<StructurePiece> hatches, StructurePiecesBuilder builder) {
        ContentCityStructure.Graded graded = ContentCityStructureGrade.graded(context, plan, line, levels, run, rails, wells, false);
        int start = graded.start();
        int[] profile = graded.profile();
        boolean[] held = graded.held();
        boolean[] pinned = graded.pinned();
        boolean[] mine = graded.mine();
        boolean[] sewered = mine.clone();
        boolean[] bridged = graded.bridged();
        boolean[] bored = graded.bored();
        CityGrade.rampSteps(profile, held, pinned);
        int[] grade = profile.clone();
        boolean[] decked = new boolean[profile.length];
        seats.put(line, grade);
        ContentCityStructureGrade.lift(plan, line, start, profile, decked, bridged, bored, decks);
        for (int at = 0; at < decked.length; at++) { held[at] |= decked[at]; }
        CityGrade.rampSteps(profile, held, pinned);
        boolean[] roofed = CityGrade.roofed(profile, CityGradeBore.openEnds(graded.ground(), start, line), bridged, ContentCity.tunnelDepth());
        String paving = ContentControl.onRoad(line.keys(), () -> ContentCity.paving(line.alley()));
        String walk = ContentControl.onRoad(line.keys(), ContentCity::sidewalkBlock);
        boolean[] framed = ContentControl.onRoad(line.keys(), () -> framed(plan, line, start, bridged));
        boolean[] paved = mine.clone();
        for (ContentCityStructure.Well well : wells) {
            if (!well.middle().alongX().equals(line) && !well.middle().alongZ().equals(line)) { continue; }
            BoundingBox square = well.box();
            int reach = CityPlan.plazaPaved();
            int low = (line.alongX() ? square.minX() : square.minZ()) - reach;
            int high = (line.alongX() ? square.maxX() : square.maxZ()) + reach;
            for (int row = low; row <= high; row++) {
                if (row >= start && row < start + paved.length) { paved[row - start] = false; }
            }
        }
        boolean[] tunnels = tunnels(plan, line, start, roofed, paved);
        int[] lamps = tunnelLamps(tunnels, start, ContentCity.tunnelLightRun());
        int from = 0;
        int pieces = 0;
        int firstRow = line.endsLow() == CityPlan.End.MET ? -1 : line.from() - start + 1;
        int lastRow = line.endsHigh() == CityPlan.End.MET ? -1 : line.to() - start;
        for (int at = 1; at <= profile.length; at++) {
            boolean ends = at == profile.length || at == firstRow || at == lastRow || CityPlan.districtOf(start + at, line.alongX()) != CityPlan.districtOf(start + at - 1, line.alongX()) || profile[at] != profile[from] || paved[at] != paved[from] || bridged[at] != bridged[from] || tunnels[at] != tunnels[from] || pinned[at] != pinned[from];
            if (!ends) { continue; }
            int low = Math.max(start + from, line.from());
            int high = Math.min(start + at - 1, line.to());
            if (paved[from] && low <= high && plan.emitsAlong(line, low)) {
                builder.addPiece(piece(line, low, high, profile[from], paving, bridged[from], tunnels[from], framed(framed, start, low, high), lamps[from], pinned[from]));
                pieces++;
            }
            from = at;
        }
        if (line.alley()) { ContentLog.LOGGER.debug("The alley at {} running {} to {} is laid in {} piece(s) at y {} to {}", line.at(), line.from(), line.to(), pieces, profile[Math.max(0, Math.min(profile.length - 1, line.from() - start))], profile[Math.max(0, Math.min(profile.length - 1, line.to() - start))]); }
        if (ContentCity.sewers()) { ContentCityStructureSewers.sewers(plan, line, context.heightAccessor().getMinBuildHeight(), start, profile, sewered, bridged, wells, hatches, builder); }
        laid.put(line, new ContentCityStructure.Laid(start, profile, grade, decked, paved, bridged, tunnels, roofed, sewered));
        ContentCityStructureEnds.ends(context, plan, line, start, profile, paving, walk, bridged, wells, bulbs, builder);
        return profile;
    }

    static void overpasses(CityPlan plan, Map<CityPlan.Line, ContentCityStructure.Laid> laid, StructurePiecesBuilder builder) {
        for (CityPlan.Junction junction : plan.junctions()) {
            CityPlan.Line row = junction.alongX();
            CityPlan.Line column = junction.alongZ();
            if (row.alley() || column.alley() || !row.covers(column.middle()) || !column.covers(row.middle())) { continue; }
            CityPlan.Line owner = row.beats(column) ? row : column;
            CityPlan.Line other = owner == row ? column : row;
            ContentCityStructure.Laid owned = laid.get(owner);
            ContentCityStructure.Laid crossed = laid.get(other);
            if (!decked(owned, other.at(), other.last()) || !decked(crossed, owner.at(), owner.last())) { continue; }
            int under = owned.profile()[other.middle() - owned.start()];
            String paving = ContentControl.onRoad(other.keys(), () -> ContentCity.paving(false));
            int from = owner.at();
            for (int along = owner.at(); along <= owner.last() + 1; along++) {
                int level = along <= owner.last() ? crossed.profile()[along - crossed.start()] : Integer.MIN_VALUE;
                if (along <= owner.last() && level == crossed.profile()[from - crossed.start()]) { continue; }
                int over = crossed.profile()[from - crossed.start()];
                if (over != under && !crossed.paved()[from - crossed.start()] && plan.emitsAlong(other, from)) {
                    builder.addPiece(piece(other, from, along - 1, over, paving, true, false, new int[0], Integer.MIN_VALUE, false));
                    ContentLog.LOGGER.debug("The street along {} at {} to {} carries its deck over the square it shares with the street at {}, rows {} to {} at y {}, over that street's deck at y {}", other.alongX() ? "x" : "z", other.at(), other.last(), owner.at(), from, along - 1, over, under);
                }
                from = along;
            }
        }
    }

    private static boolean[] tunnels(CityPlan plan, CityPlan.Line line, int start, boolean[] roofed, boolean[] paved) {
        boolean[] tunnels = new boolean[roofed.length];
        for (int at = 0; at < tunnels.length; at++) { tunnels[at] = roofed[at] && paved[at]; }
        for (CityPlan.Line other : plan.crossing(line)) {
            if (other.from() > line.last() + 1 || other.to() < line.at() - 1) { continue; }
            for (int row = Math.max(start, other.at() - 1); row <= Math.min(start + tunnels.length - 1, other.last() + 1); row++) { tunnels[row - start] = false; }
        }
        CityGrade.dropShortRuns(tunnels);
        return tunnels;
    }

    private static int[] tunnelLamps(boolean[] tunnels, int start, int run) {
        int[] lamps = new int[tunnels.length];
        Arrays.fill(lamps, Integer.MIN_VALUE);
        int from = 0;
        while (from < tunnels.length) {
            if (!tunnels[from]) {
                from++;
                continue;
            }
            int to = from;
            while (to + 1 < tunnels.length && tunnels[to + 1]) { to++; }
            boolean any = false;
            for (int at = from; at <= to && !any; at++) { any = Math.floorMod(start + at, run) == 0; }
            if (!any) { Arrays.fill(lamps, from, to + 1, start + (from + to) / 2); }
            from = to + 1;
        }
        return lamps;
    }

    private static ContentCityPiece piece(CityPlan.Line line, int from, int to, int level, String paving, boolean bridged, boolean bored, int[] frames, int lamp, boolean held) {
        String keys = line.keys() == null ? "" : line.keys().toString();
        if (line.alongX()) { return new ContentCityPiece(from, line.at(), to, line.last(), level, paving, line.middle(), true, line.alley(), line.width(), bridged, bored, frames, lamp, keys, held); }
        return new ContentCityPiece(line.at(), from, line.last(), to, level, paving, line.middle(), false, line.alley(), line.width(), bridged, bored, frames, lamp, keys, held);

    }

    private static boolean[] framed(CityPlan plan, CityPlan.Line line, int start, boolean[] bridged) {
        if (ContentCity.frameBlock().isEmpty()) { return new boolean[bridged.length]; }
        boolean[] framed = CityGrade.frameRows(bridged, ContentCity.frameLeast(), ContentCity.frameRun());
        for (CityPlan.Line other : plan.crossing(line)) {
            if (other.from() > line.last() + 1 || other.to() < line.at() - 1) { continue; }
            for (int row = Math.max(start, other.at() - 1); row <= Math.min(start + framed.length - 1, other.last() + 1); row++) { framed[row - start] = false; }
        }
        return framed;
    }

    private static int[] framed(boolean[] framed, int start, int low, int high) {
        int count = 0;
        for (int row = low; row <= high; row++) { count += framed[row - start] ? 1 : 0; }
        int[] rows = new int[count];
        int next = 0;
        for (int row = low; row <= high; row++) {
            if (framed[row - start]) { rows[next++] = row; }
        }
        return rows;
    }

    static boolean decked(@Nullable ContentCityStructure.Laid held, int least, int most) {
        if (held == null) { return false; }
        for (int row = least; row <= most; row++) {
            int at = row - held.start();
            if (at < 0 || at >= held.bridged().length || !held.bridged()[at]) { return false; }
        }
        return true;
    }
}
