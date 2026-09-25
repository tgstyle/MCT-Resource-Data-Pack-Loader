package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.util.ContentLog;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public final class ContentCityStructureStations {
    private ContentCityStructureStations() {}

    static int[] stations(Collection<CityRails.Laid> tracks) {
        IntList boxes = new IntArrayList();
        for (CityRails.Laid track : tracks) {
            if (!track.rail().subway()) { continue; }
            for (CityRails.Station station : track.stations()) {
                BoundingBox box = stationBox(track.rail(), station);
                boxes.addAll(IntList.of(box.minX(), box.minZ(), box.maxX(), box.maxZ()));
            }
        }
        return boxes.toIntArray();
    }

    static boolean claimed(List<BoundingBox> claims, CityPlan.Plot plot) {
        for (BoundingBox claim : claims) {
            if (claim.maxX() >= plot.fromX() && claim.minX() <= plot.toX() && claim.maxZ() >= plot.fromZ() && claim.minZ() <= plot.toZ()) { return true; }
        }
        return false;
    }

    static void layRail(CityPlan plan, CityRails.Laid track, boolean crossings, StructurePiecesBuilder builder) {
        CityPlan.Rail rail = track.rail();
        int window = plan.railWindow(rail);
        int start = track.start();
        int least = Math.max(start, window);
        int most = Math.min(start + track.profile().length - 1, window + CityPlan.district() - 1);
        if (least > most) { return; }
        int[] trunk = CityLinks.trunkData(rail);
        int[] linkEnds = CityLinks.ends(rail);
        int from = least;
        for (int row = least + 1; row <= most + 1; row++) {
            int at = row - start;
            int held = from - start;
            boolean ends = row > most || track.profile()[at] != track.profile()[held] || track.bridged()[at] != track.bridged()[held] || track.tunnel()[at] != track.tunnel()[held] || track.crossed()[at] != track.crossed()[held];
            if (!ends) { continue; }
            if (track.crossed()[held] == crossings) {
                IntList frames = new IntArrayList();
                for (int mark = from; mark < row; mark++) { if (track.frames()[mark - start]) { frames.add(mark); } }
                builder.addPiece(new ContentCityRailPiece(from, row - 1, track.profile()[held], rail.middle(), rail.alongX(), rail.width(), track.bridged()[held], track.tunnel()[held], rail.subway(), crossings, frames.toIntArray(), trunk, linkEnds));
            }
            from = row;
        }
    }

    private static BoundingBox stationBox(CityPlan.Rail rail, CityRails.Station station) {
        int row = station.row();
        int near = station.near();
        int run = ContentCityStairsPiece.RUN;
        int wide = ContentCityStairsPiece.WIDE;
        return rail.alongX() ? new BoundingBox(row - 1, 0, near - 1, row + run + 1, 0, near + wide) : new BoundingBox(near - 1, 0, row - 1, near + wide, 0, row + run + 1);
    }

    static void claim(CityRails.Laid track, List<BoundingBox> claims) {
        CityPlan.Rail rail = track.rail();
        int start = track.start();
        int rows = track.profile().length;
        if (rail.subway()) {
            int[] rising = track.rising();
            if (rising != null) {
                int half = CityRails.halfOf(rail);
                int from = rising[1] > 0 ? rising[0] : rail.from();
                int to = rising[1] > 0 ? rail.to() : rising[0];
                claims.add(rail.alongX() ? new BoundingBox(from, 0, rail.middle() - half, to, 0, rail.middle() + half) : new BoundingBox(rail.middle() - half, 0, from, rail.middle() + half, 0, to));
            }
            for (CityRails.Station station : track.claimed()) { claims.add(stationBox(rail, station)); }
            return;
        }
        int reach = CityLinks.claimReach(rail);
        int from = 0;
        for (int at = 1; at <= rows; at++) {
            boolean ends = at == rows || track.tunnel()[at] != track.tunnel()[from];
            if (!ends) { continue; }
            if (!track.tunnel()[from]) { claims.add(rail.alongX() ? new BoundingBox(start + from, 0, rail.at() - reach, start + at - 1, 0, rail.last() + reach) : new BoundingBox(rail.at() - reach, 0, start + from, rail.last() + reach, 0, start + at - 1)); }
            from = at;
        }
    }

    private static boolean outsideWindow(CityPlan plan, CityPlan.Rail rail, int row) {
        int window = plan.railWindow(rail);
        return row < window || row >= window + CityPlan.district();
    }

    static void chambers(CityPlan plan, CityRails.Laid track, StructurePiecesBuilder builder) {
        CityPlan.Rail rail = track.rail();
        int half = ContentCity.stationLength() / 2;
        int bedHalf = ContentCity.railBedHalf(true);
        if (half <= 0) { return; }
        for (CityRails.Station station : track.stations()) {
            int heart = station.heart();
            if (outsideWindow(plan, rail, heart) || !track.tunnelAt(heart)) { continue; }
            int from = heart - half;
            int to = heart + half;
            while (!track.tunnelAt(from)) { from++; }
            while (!track.tunnelAt(to)) { to--; }
            int low =
 track.tunnelAt(from - 1) ? track.level(from - 1) : ContentCityStationPiece.OPEN;
            int high = track.tunnelAt(to + 1) ? track.level(to + 1) : ContentCityStationPiece.OPEN;
            builder.addPiece(new ContentCityStationPiece(from, to, track.level(heart), rail.middle(), rail.alongX(), bedHalf, low, high, station.row() - 1, station.way()));
        }
    }

    static void heads(CityGround ground, CityPlan plan, CityRails.Laid track, Map<CityPlan.Line, ContentCityStructure.Laid> streets, StructurePiecesBuilder builder) {
        CityPlan.Rail rail = track.rail();
        int bedHalf = ContentCity.railBedHalf(true);
        Vec3i built = ContentCity.stationSpan();
        if (built == null) { return; }
        for (CityRails.Station station : track.stations()) {
            if (outsideWindow(plan, rail, station.heart())) { continue; }
            int level = track.level(station.heart());
            CityPlan.Line street = CityRails.streetAlong(streets.keySet(), rail.alongX(), rail.middle(), station.first(), station.last());
            int top = topFor(street == null ? null : streets.get(street), station);
            String why = ContentCityStampPiece.unreachable(built, level, top, station.row(), station.near(), station.way(), rail.middle(), rail.alongX(), bedHalf);
            if (why != null) {
                ContentLog.LOGGER.error("The subway station at {}, {} on the line at {} was kept when its city was planned, but its stairs cannot be laid: {}", rail.alongX() ? station.row() : station.near(), rail.alongX() ? station.near() : station.row(), rail.middle(), why);
                continue;
            }
            builder.addPiece(new ContentCityStampPiece(level, top, station.row(), station.near(), station.way(), rail.middle(), rail.alongX(), bedHalf, built.getX(), built.getZ(), streetEdge(street, station), deckRows(ground, street, street == null ? null : streets.get(street), station.row())));
        }
    }

    private static int streetEdge(@Nullable CityPlan.Line line, CityRails.Station station) {
        if (line == null) { return ContentCityStairsPiece.wall(station.near(), station.way()); }
        return station.way() > 0 ? line.last() + 1 : line.at() - 1;
    }

    private static int deckRows(CityGround ground, @Nullable CityPlan.Line line, @Nullable ContentCityStructure.Laid street, int row) {
        if (line == null || street == null) { return 0; }
        int rows = 0;
        for (int along = 0; along <= ContentCityStairsPiece.RUN; along++) {
            int index = row + along - street.start();
            if (index >= 0 && index < street.bridged().length && street.bridged()[index] && ContentCityStructureGrade.underfoot(ground, line, row + along) != Integer.MIN_VALUE) { rows |= 1 << along; }
        }
        return rows;
    }

    private static int topFor(@Nullable ContentCityStructure.Laid street, CityRails.Station station) {
        if (street == null) { return Integer.MIN_VALUE; }
        return street.grade()[Mth.clamp(station.heart() - street.start(), 0, street.grade().length - 1)];
    }
}
