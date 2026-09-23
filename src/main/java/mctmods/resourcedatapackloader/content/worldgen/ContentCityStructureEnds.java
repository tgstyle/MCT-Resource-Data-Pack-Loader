package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure.GenerationContext;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public final class ContentCityStructureEnds {
    private static final int[] COURT_STEMS = { 1, 4, 7 };

    private ContentCityStructureEnds() {}

    private record Room(int radius, BoundingBox box) {}

    static void ends(GenerationContext context, CityPlan plan, CityPlan.Line line, int start, int[] profile, String paving, String walk, boolean[] bridged, List<ContentCityStructure.Well> wells, List<ContentCityStructure.Bulb> bulbs, StructurePiecesBuilder builder) {
        if (line.endsLow() == CityPlan.End.MET && line.endsHigh() == CityPlan.End.MET) { return; }
        RandomSource roll = RandomSource.create(context.seed() ^ ContentCityStructure.SALT ^ (line.at() * 132897987541L + (line.alongX() ? 3L : 5L)));
        String rail = ContentControl.onRoad(line.keys(), ContentCity::bridgeBarrierBlock);
        boolean paved = !line.alley() && !walk.isEmpty();
        for (int side = 0; side < 2; side++) {
            boolean low = side == 0;
            CityPlan.End end = low ? line.endsLow() : line.endsHigh();
            if (end == CityPlan.End.MET) { continue; }
            int along = low ? line.from() : line.to();
            CityPlan.Court planned = CityPlan.plannedCourt(plan, line, low);
            if (planned != null && planned.room() > 0 && !line.covers(planned.end())) {
                strandedCourt(CityGround.of(context), plan, line, planned, paving, walk, bulbs, builder);
                continue;
            }
            int at = Mth.clamp(along - start, 0, profile.length - 1);
            int x = line.alongX() ? along : line.middle();
            int z = line.alongX() ? line.middle() : along;
            if (!plan.emits(x, z)) { continue; }
            if (CitySeams.facesNeighbor(CityGround.of(context), plan.town(), line.alongX(), along, low ? -1 : 1, line.middle())) {
                ContentLog.LOGGER.debug("The dead end at {}, {} faces a neighboring village's site, so it rolls no cul-de-sac and stays open for that village's streets", x, z);
                continue;
            }
            int level = profile[at];

            if (!line.alley() && bridged[at]) {
                String style = ContentCity.pierStyle(roll);
                if (style != null) {
                    int run = at;
                    while (run - (low ? -1 : 1) >= 0 && run - (low ? -1 : 1) < profile.length && bridged[run - (low ? -1 : 1)] && line.covers(start + run - (low ? -1 : 1))) { run -= low ? -1 : 1; }
                    int from = Math.min(at, run) + start;
                    int to = Math.max(at, run) + start;
                    builder.addPiece(pier(line, from, to, level, style, along));
                    ContentLog.LOGGER.debug("A street runs out over water at {}, {} and ends as a {} pier over rows {} to {}", x, z, style, from, to);
                    continue;
                }
            }
            String closed = ContentCity.deadEnd(roll, paved, !rail.isEmpty());
            String laid = closed;
            Room found = planned == null && end == CityPlan.End.COURT ? courtRoom(CityGround.of(context), plan, line, wells, bulbs, low, level) : null;
            int room = planned != null ? planned.room() : found != null ? found.radius() : 0;
            if (room > 0) {
                int heart = along + (low ? -1 : 1) * (room + 1);
                ContentCityBulbPiece.Court pad = new ContentCityBulbPiece.Court(line.alongX() ? heart : line.middle(), line.alongX() ? line.middle() : heart, level, room, line.alongX(), low, line.middle(), (line.width() - 1) / 2, 0);
                builder.addPiece(new ContentCityBulbPiece(pad, courtDress(line, paving, walk)));
                bulbs.add(new ContentCityStructure.Bulb(pad, line, found != null ? found.box() : court(line, along, low ? -1 : 1, room, planned == null ? 0 : planned.stem())));
                laid = "a cul-de-sac";
            }
            else if (closed != null) { builder.addPiece(cap(line, along, level, closed, walk, rail)); }

            else { laid = "nothing"; }
            ContentLog.LOGGER.debug("The {} dead end at {}, {} y {} of the street along {} closes with {}", low ? "low" : "high", x, z, level, line.alongX() ? "x" : "z", laid);
        }
    }

    private static void strandedCourt(CityGround ground, CityPlan plan, CityPlan.Line line, CityPlan.Court planned, String paving, String walk, List<ContentCityStructure.Bulb> bulbs, StructurePiecesBuilder builder) {
        BoundingBox box = court(line, planned.end(), planned.dir(), planned.room(), planned.stem());
        int heart = line.alongX() ? (box.minX() + box.maxX()) / 2 : (box.minZ() + box.maxZ()) / 2;
        int x = line.alongX() ? heart : line.middle();
        int z = line.alongX() ? line.middle() : heart;
        if (!plan.emits(x, z)) { return; }
        int level = ContentCityStructureSeat.groundAverage(ground, box.minX(), box.minZ(), box.maxX(), box.maxZ());
        ContentCityBulbPiece.Court pad = new ContentCityBulbPiece.Court(x, z, level, planned.room(), line.alongX(), planned.low(), line.middle(), -1, 0);
        builder.addPiece(new ContentCityBulbPiece(pad, courtDress(line, paving, walk)));
        bulbs.add(new ContentCityStructure.Bulb(pad, line, box));
        ContentLog.LOGGER.debug("The street along {} at {} is pulled back short of its planned cul-de-sac, so the court stands alone at {}, {} y {} with its houses around it", line.alongX() ? "x" : "z", line.at(), x, z, level);
    }

    private static ContentCityBulbPiece.Dress courtDress(CityPlan.Line line, String paving, String walk) {
        CityCross cross = CityCross.of(line);
        return new ContentCityBulbPiece.Dress(cross.core(), cross.lines(), cross.walk(), paving, ContentControl.onRoad(line.keys(), ContentCity::lineBlock), walk);
    }

    @Nullable private static Room courtRoom(CityGround ground, CityPlan plan, CityPlan.Line line, List<ContentCityStructure.Well> wells, List<ContentCityStructure.Bulb> bulbs, boolean low, int level) {
        int end = low ? line.from() : line.to();
        int dir = low ? -1 : 1;
        int most = (line.width() + 7) / 2;
        int least = (line.width() + 1) / 2 + 1;
        int endX = line.alongX() ? end : line.middle();
        int endZ = line.alongX() ? line.middle() : end;
        List<BoundingBox> standing = new ArrayList<>();
        List<BoundingBox> ways = new ArrayList<>();
        stand(plan, line, standing, ways);
        int far = end + dir * (2 * most + 1 + COURT_STEMS[COURT_STEMS.length - 1]);
        CityPlan beyond = CityPlan.of(ground, CityPlan.districtOf(line.alongX() ? far : line.middle(), true), CityPlan.districtOf(line.alongX() ? line.middle() : far, false));
        if (beyond != null && (beyond.windowX() != plan.windowX() || beyond.windowZ() != plan.windowZ())) { stand(beyond, line, standing, ways); }
        for (ContentCityStructure.Bulb bulb : bulbs) {
            standing.add(bulb.box());
            ways.add(bulb.box());
        }
        for (ContentCityStructure.Well well : wells) {
            standing.add(well.box());
            ways.add(well.box());
        }
        boolean cut = false;
        for (int radius = most; radius >= least; radius--) {
            int heart = end + dir * (radius + 1);
            if (crowded(standing, line.alongX() ? heart : line.middle(), line.alongX() ? line.middle() : heart, radius)) { continue; }
            for (int stem : COURT_STEMS) {
                int spot = heart + dir * stem;
                int discX = line.alongX() ? spot : line.middle();
                int discZ = line.alongX() ? line.middle() : spot;
                BoundingBox box = court(line, end, dir, radius, stem);
                if (crowded(standing, discX, discZ, radius) || crossed(ways, box)) { continue; }
                if (ContentCityStructure.steep(ground, discX, discZ, radius, level)) {
                    cut = true;
                    continue;
                }
                if (ContentCityStructure.dry(ground, box.minX(), box.minZ(), discX, discZ, radius)) { return new Room(radius, box); }
                ContentLog.LOGGER.debug("The dead end at {}, {} rolled a cul-de-sac, but water stands under the court it would take, so it stays a plain end", endX, endZ);
                return null;
            }
        }
        if (cut) { ContentLog.LOGGER.debug("The dead end at {}, {} rolled a cul-de-sac, but every court it could seat would cut the ground beside it, so it stays a plain end", endX, endZ); }
        else { ContentLog.LOGGER.debug("The dead end at {}, {} rolled a cul-de-sac, but a plot, street, railway, well or cul-de-sac stands within its reach at every size it could take, so it stays a plain end", endX, endZ); }
        return null;
    }

    static void stand(CityPlan plan, CityPlan.Line line, List<BoundingBox> standing, List<BoundingBox> ways) {
        for (CityPlan.Plot plot : plan.plots()) { standing.add(new BoundingBox(plot.fromX(), 0, plot.fromZ(), plot.toX(), 0, plot.toZ())); }
        for (List<CityPlan.Line> lines : List.of(plan.alongX(), plan.alongZ())) {
            for (CityPlan.Line other : lines) {
                if (other.equals(line)) { continue; }
                BoundingBox held = other.alongX() ? new BoundingBox(other.from(), 0, other.at(), other.to(), 0, other.last()) : new BoundingBox(other.at(), 0, other.from(), other.last(), 0, other.to());
                standing.add(held);
                if (!other.alley()) { ways.add(held); }
            }
        }
        for (CityPlan.Rail rail : plan.rails()) {
            if (rail.subway()) { continue; }
            BoundingBox held = rail.alongX() ? new BoundingBox(rail.from(), 0, rail.at(), rail.to(), 0, rail.last()) : new BoundingBox(rail.at(), 0, rail.from(), rail.last(), 0, rail.to());
            standing.add(held);
            ways.add(held);
        }
    }

    static BoundingBox court(CityPlan.Line line, int end, int dir, int radius, int stem) {
        int near = end + dir;
        int far = end + dir * (stem + 2 * radius + 1);
        int least = Math.min(near, far);
        int most = Math.max(near, far);
        return line.alongX() ? new BoundingBox(least, 0, line.middle() - radius, most, 0, line.middle() + radius) : new BoundingBox(line.middle() - radius, 0, least, line.middle() + radius, 0, most);
    }

    private static boolean crossed(List<BoundingBox> ways, BoundingBox court) {
        for (BoundingBox way : ways) {
            if (way.intersects(court)) { return true; }
        }
        return false;
    }

    private static boolean crowded(List<BoundingBox> standing, int x, int z, int radius) {
        for (BoundingBox box : standing) {
            int nearX = Mth.clamp(x, box.minX(), box.maxX()) - x;
            int nearZ = Mth.clamp(z, box.minZ(), box.maxZ()) - z;
            if (nearX * nearX + nearZ * nearZ <= (radius + 1) * (radius + 1)) { return true; }
        }
        return false;
    }

    private static ContentCityCapPiece cap(CityPlan.Line line, int along, int level, String closed, String walk, String rail) {
        boolean sidewalk = ContentCity.SIDEWALK_END.equals(closed);
        int rise = sidewalk ? 0 : ContentControl.onRoad(line.keys(), ContentCity::bridgeBarrierHeight);
        String block = sidewalk ? walk : rail;
        if (line.alongX()) { return new ContentCityCapPiece(along, line.at(), along, line.last(), level, block, rise); }
        return new ContentCityCapPiece(line.at(), along, line.last(), along, level, block, rise);
    }

    private static ContentCityPierPiece pier(CityPlan.Line line, int from, int to, int level, String style, int head) {
        if (line.alongX()) { return new ContentCityPierPiece(from, line.at(), to, line.last(), level, line.middle(), true, line.width(), style, head); }
        return new ContentCityPierPiece(line.at(), from, line.last(), to, level, line.middle(), false, line.width(), style, head);
    }
}
