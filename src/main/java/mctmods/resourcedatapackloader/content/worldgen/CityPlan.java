package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.content.def.VillageDef;
import mctmods.resourcedatapackloader.content.worldgen.CityGrowth.Growth;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;

import com.google.gson.JsonObject;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import javax.annotation.Nullable;

public final class CityPlan {
    private static final int DISTRICT_LEAST = 96;
    public static final int WELL_HALF = 3;
    private static int district = DISTRICT_LEAST;
    private static final int VANILLA_WIDTH = 3;
    static final long SALT = 0x1F0D3F5B7C9E1A37L;
    private static final int MOUTH = 4;
    private static final Map<Long, Optional<CityPlan>> RAILED = new ConcurrentHashMap<>();

    public enum End { MET, BARE, COURT }

    public record Line(int at, int width, boolean alley, boolean alongX, int from, int to, End endsLow, End endsHigh, @Nullable JsonObject keys, List<Integer> lifts) {
        public Line(int at, int width, boolean alley, boolean alongX, int from, int to, End endsLow, End endsHigh) { this(at, width, alley, alongX, from, to, endsLow, endsHigh, null, List.of()); }

        Line ending(End low, End high) { return new Line(at, width, alley, alongX, from, to, low, high, keys, lifts); }

        Line reaching(int low, int high) { return new Line(at, width, alley, alongX, low, high, endsLow, endsHigh, keys, lifts); }

        public int last() { return at + width - 1; }

        public int middle() { return at + width / 2; }

        public boolean covers(int along) { return along >= from && along <= to; }

        public int rank() { return (alley ? 500 : 1000) + width; }

        public boolean beats(Line other) { return rank() != other.rank() ? rank() > other.rank() : alongX; }
    }

    public record Junction(Line alongX, Line alongZ) {
        public int fromX() { return alongZ.at(); }

        public int toX() { return alongZ.last(); }

        public int fromZ() { return alongX.at(); }

        public int toZ() { return alongX.last(); }
    }

    public record Plot(int fromX, int fromZ, int toX, int toZ, Line street, boolean lower, VillageDef def, boolean back) {
        public int middleAlong() { return street.alongX() ? (fromX + toX) / 2 : (fromZ + toZ) / 2; }

        public boolean frontsLow() { return lower != back; }

        public Plot slid(int by) { return street.alongX() ? new Plot(fromX + by, fromZ, toX + by, toZ, street, lower, def, back) : new Plot(fromX, fromZ + by, toX, toZ + by, street, lower, def, back); }
    }

    public record Lift(int fromX, int fromZ, int toX, int toZ, int height) {
        public int fromAlong(boolean alongX) { return alongX ? fromX : fromZ; }

        public int toAlong(boolean alongX) { return alongX ? toX : toZ; }

        public boolean spans(Line line) {
            int across = line.middle();
            return line.alongX() ? across >= fromZ && across <= toZ : across >= fromX && across <= toX;
        }
    }

    public record Rail(int at, int width, boolean alongX, boolean subway, int from, int to, long town) {
        public int last() { return at + width - 1; }

        public int middle() { return at + (width - 1) / 2; }
    }

    public record Town(long key, long hash, long salt, int wellX, int wellZ, int fromX, int toX, int fromZ, int toZ, List<Line> drawn, int crossX, int crossZ) {
        public int crossAcross(boolean alongX) { return alongX ? crossZ : crossX; }

        public int wellAlong(boolean alongX) { return alongX ? wellX : wellZ; }

        public int wellAcross(boolean alongX) { return alongX ? wellZ : wellX; }

        public int least(boolean alongX) { return alongX ? fromX : fromZ; }

        public int most(boolean alongX) { return alongX ? toX : toZ; }
    }

    private final int originX;
    private final int originZ;
    private final int block;
    final List<Line> alongX;
    final List<Line> alongZ;
    private final List<Plot> plots;
    private final List<Rail> rails;
    private final int windowX;
    private final int windowZ;
    private final boolean mapped;
    private final List<Junction> plazas;
    private final List<Lift> lifts;
    private final String type;
    @Nullable private final Town town;
    @Nullable private volatile CityPlan settled;
    @Nullable private volatile Map<Junction, Integer> aproned;

    CityPlan(int originX, int originZ, int block, List<Line> alongX, List<Line> alongZ, List<Plot> plots, List<Rail> rails, int windowX, int windowZ, boolean mapped, List<Junction> plazas, List<Lift> lifts, String type, @Nullable Town town) {
        this.town = town;
        this.type = type;
        this.lifts = lifts;
        this.plazas = plazas;
        this.windowX = windowX;
        this.windowZ = windowZ;
        this.mapped = mapped;
        this.originX = originX;
        this.originZ = originZ;
        this.block = block;
        this.alongX = alongX;
        this.alongZ = alongZ;
        this.plots = plots;
        this.rails = rails;
    }

    public int originX() { return originX; }

    CityPlan withTown(Town held) { return new CityPlan(originX, originZ, block, alongX, alongZ, plots, rails, windowX, windowZ, mapped, plazas, lifts, type, held); }

    CityPlan withRails(List<Rail> held) { return new CityPlan(originX, originZ, block, alongX, alongZ, plots, held, windowX, windowZ, mapped, plazas, lifts, type, town); }

    CityPlan pulledBack(Map<Line, Line> pulled) {
        if (pulled.isEmpty()) { return this; }
        List<Plot> fronted = new ArrayList<>();
        for (Plot plot : plots) { fronted.add(new Plot(plot.fromX(), plot.fromZ(), plot.toX(), plot.toZ(), pulled.getOrDefault(plot.street(), plot.street()), plot.lower(), plot.def(), plot.back())); }
        List<Junction> squares = new ArrayList<>();
        for (Junction plaza : plazas) { squares.add(new Junction(pulled.getOrDefault(plaza.alongX(), plaza.alongX()), pulled.getOrDefault(plaza.alongZ(), plaza.alongZ()))); }
        Town drawn = town == null ? null : new Town(town.key(), town.hash(), town.salt(), town.wellX(), town.wellZ(), town.fromX(), town.toX(), town.fromZ(), town.toZ(), swapped(town.drawn(), pulled), town.crossX(), town.crossZ());
        return new CityPlan(originX, originZ, block, swapped(alongX, pulled), swapped(alongZ, pulled), List.copyOf(fronted), rails, windowX, windowZ, mapped, List.copyOf(squares), lifts, type, drawn);
    }

    private static List<Line> swapped(List<Line> lines, Map<Line, Line> pulled) {
        List<Line> found = new ArrayList<>();
        for (Line line : lines) { found.add(pulled.getOrDefault(line, line)); }
        return List.copyOf(found);
    }

    @Nullable CityPlan settled() { return settled; }

    synchronized CityPlan settle(CityPlan made) {
        if (settled == null) {
            made.settled = made;
            settled = made;
        }
        return settled;
    }

    @Nullable public Town town() { return town; }

    public String villageType() { return type; }

    public List<Lift> lifts() { return lifts; }

    public boolean laysElsewhere(Rail rail) {
        int across = rail.alongX() ? windowZ : windowX;
        return rail.middle() < across || rail.middle() >= across + district();
    }

    public int railWindow(Rail rail) { return rail.alongX() ? windowX : windowZ; }

    public List<BoundingBox> wellBoxes() {
        List<BoundingBox> found = new ArrayList<>();
        for (Junction plaza : plazas) {
            if (!plaza.alongX().covers(plaza.alongZ().middle()) || !plaza.alongZ().covers(plaza.alongX().middle())) { continue; }
            int x = (plaza.fromX() + plaza.toX()) / 2 - ContentCityWellPiece.SIZE / 2;
            int z = (plaza.fromZ() + plaza.toZ()) / 2 - ContentCityWellPiece.SIZE / 2;
            found.add(new BoundingBox(x, 0, z, x + ContentCityWellPiece.SIZE - 1, 0, z + ContentCityWellPiece.SIZE - 1));
        }
        return found;
    }

    void apron(CityGround ground, Line line, int start, int[] profile) {
        boolean[] held = new boolean[profile.length];
        List<BoundingBox> wells = new ArrayList<>();
        for (BoundingBox box : wellBoxes()) {
            if (emits(box.minX(), box.minZ())) { wells.add(box); }
        }
        List<Line> met = new ArrayList<>();
        for (Line other : crossing(line)) {
            if (line.covers(other.middle()) && other.covers(line.middle())) { met.add(other); }
        }
        Map<Junction, Integer> levels = apronLevels(ground, wells);
        for (Line other : met) { CityGrade.pin(profile, held, start, other.at(), other.last(), squareLevel(levels, line, other)); }
        for (Line other : met) {
            int[] rows = mouth(line, other, start, profile.length);
            if (rows == null) { continue; }
            BoundingBox plaza = mouthWell(line, wells, rows[0], rows[1]);
            int flat = plaza == null ? squareLevel(levels, line, other) : wellGround(ground, plaza.minX(), plaza.minZ());
            for (int row = rows[0]; row <= rows[1]; row++) {
                if (held[row - start]) { continue; }
                profile[row - start] = flat;
                held[row - start] = true;
            }
        }
        for (BoundingBox box : wells) { clampToWell(line, start, profile, held, box, wellGround(ground, box.minX(), box.minZ())); }
    }

    private Map<Junction, Integer> apronLevels(CityGround ground, List<BoundingBox> wells) {
        Map<Junction, Integer> known = aproned;
        if (known != null) { return known; }
        Map<Junction, Integer> levels = new HashMap<>();
        Map<Line, int[]> profiles = new HashMap<>();
        for (Junction junction : junctions()) {
            if (junction.alongX().covers(junction.alongZ().middle()) && junction.alongZ().covers(junction.alongX().middle())) { levels.put(junction, plazaLevel(ground, wells, junction, profiles)); }
        }
        Map<Junction, Integer> clamped = Map.copyOf(ContentLog.LOGGER.quietly(() -> {
            ContentCityStructure.aprons(ground, this, levels, wells);
            return levels;
        }));
        aproned = clamped;
        return clamped;
    }

    private static int squareLevel(Map<Junction, Integer> levels, Line line, Line other) { return levels.get(line.alongX() ? new Junction(line, other) : new Junction(other, line)); }

    private static int plazaLevel(CityGround ground, List<BoundingBox> wells, Junction junction, Map<Line, int[]> profiles) {
        for (BoundingBox box : wells) {
            if (inPlaza(box, junction)) { return wellGround(ground, box.minX(), box.minZ()); }
        }
        return junctionLevel(ground, junction, profiles);
    }

    static boolean inPlaza(List<BoundingBox> wells, Junction junction) {
        for (BoundingBox box : wells) {
            if (inPlaza(box, junction)) { return true; }
        }
        return false;
    }

    @Nullable private static BoundingBox mouthWell(Line line, List<BoundingBox> wells, int from, int to) {
        for (BoundingBox box : wells) {
            if (nearMouth(line, box, from, to)) { return box; }
        }
        return null;
    }

    @Nullable static int[] mouth(Line line, Line other, int start, int span) {
        int reach = MOUTH + extraWidthOf(line);
        boolean high = line.from() < other.at() && line.to() - other.last() <= reach;
        if (high == (line.to() > other.last() && other.at() - line.from() <= reach)) { return null; }
        return new int[] {Math.max(start, high ? other.at() - reach - 1 : other.last() + 1), Math.min(start + span - 1, high ? other.at() - 1 : other.last() + reach + 1)};
    }

    static boolean inPlaza(BoundingBox box, Junction junction) {
        int reach = plazaReach();
        return box.maxX() + reach >= junction.fromX() && box.minX() - reach <= junction.toX() && box.maxZ() + reach >= junction.fromZ() && box.minZ() - reach <= junction.toZ();
    }

    static boolean nearMouth(Line line, BoundingBox box, int from, int to) {
        int reach = plazaReach();
        return !offWell(line, box, reach) && to >= (line.alongX() ? box.minX() : box.minZ()) - reach && from <= (line.alongX() ? box.maxX() : box.maxZ()) + reach;
    }

    static void clampToWell(Line line, int start, int[] profile, boolean[] held, BoundingBox box, int level) {
        int reach = plazaReach();
        if (offWell(line, box, reach)) { return; }
        int from = Math.max(start, (line.alongX() ? box.minX() : box.minZ()) - reach);
        int to = Math.min(start + profile.length - 1, (line.alongX() ? box.maxX() : box.maxZ()) + reach);
        for (int row = from; row <= to; row++) {
            if (held[row - start]) { continue; }
            profile[row - start] = level;
            held[row - start] = true;
        }
    }

    private static boolean offWell(Line line, BoundingBox box, int reach) { return line.last() < (line.alongX() ? box.minZ() : box.minX()) - reach || line.at() > (line.alongX() ? box.maxZ() : box.maxX()) + reach; }

    static int wellGround(CityGround ground, int x, int z) {
        int lowest = Integer.MAX_VALUE;
        for (int dz = 0; dz < ContentCityWellPiece.SIZE; dz++) {
            for (int dx = 0; dx < ContentCityWellPiece.SIZE; dx++) { lowest = Math.min(lowest, ground.surface(x + dx, z + dz)); }
        }
        return Math.max(lowest, ground.sea());
    }

    public int originZ() { return originZ; }

    public int windowX() { return windowX; }

    public int windowZ() { return windowZ; }

    boolean mapped() { return mapped; }

    public int spanStart(Line line) {
        if (!mapped) { return line.alongX() ? originX : originZ; }
        return line.from();
    }

    public int spanLength(Line line) {
        if (!mapped) { return district(); }
        return line.to() - line.from() + 1;
    }

    public boolean emits(int x, int z) {
        if (!mapped) { return true; }
        return x >= windowX && x < windowX + district() && z >= windowZ && z < windowZ + district();
    }

    public boolean emitsAlong(Line line, int along) {
        if (!mapped) { return true; }
        return line.alongX() ? emits(along, line.middle()) : emits(line.middle(), along);
    }

    public List<Line> alongX() { return alongX; }

    public List<Line> alongZ() { return alongZ; }

    public List<Line> crossing(Line line) { return line.alongX() ? alongZ : alongX; }

    public List<Junction> plazas() { return plazas; }

    public List<Junction> junctions() {
        List<Junction> found = new ArrayList<>();
        for (Line row : alongX) {
            for (Line column : alongZ) { found.add(new Junction(row, column)); }
        }
        return found;
    }

    @Nullable public static CityPlan of(CityGround ground, int districtX, int districtZ) {
        long held = packed(districtX, districtZ);
        Optional<CityPlan> found = RAILED.get(held);
        if (found != null) { return found.orElse(null); }
        CityDistricts.siteAhead(ground, List.of(new int[] {districtX, districtZ, districtX, districtZ}));
        CityPlan made = CityPlanTowns.railed(ground, districtX, districtZ);
        RAILED.putIfAbsent(held, Optional.ofNullable(made));
        return made;
    }

    CityPlan clearOfRails(CityGround ground, Function<Rail, CityRails.Bed> beds) {
        if (mapped) { return this; }
        List<Line> refused = new ArrayList<>();
        for (List<Line> lines : List.of(alongX, alongZ)) {
            for (Line line : lines) {
                if (offPlaza(line) && railCramped(ground, line, beds)) { refused.add(line); }
            }
        }
        if (refused.isEmpty()) { return this; }
        CityPlan made = dropping(refused);
        ContentLog.LOGGER.debug("The district at {}, {} lays {} street(s) fewer beside its railway lines and drops the {} plot(s) that fronted them", originX, originZ, refused.size(), plots.size() - made.plots.size());
        return made;
    }

    CityPlan dropping(List<Line> refused) {
        List<Plot> kept = new ArrayList<>();
        for (Plot plot : plots) {
            if (!refused.contains(plot.street())) { kept.add(plot); }
        }
        List<Line> rows = new ArrayList<>(alongX);
        List<Line> columns = new ArrayList<>(alongZ);
        rows.removeAll(refused);
        columns.removeAll(refused);
        return new CityPlan(originX, originZ, block, List.copyOf(rows), List.copyOf(columns), List.copyOf(kept), rails, windowX, windowZ, mapped, plazas, lifts, type, town);
    }

    private boolean offPlaza(Line line) {
        for (Junction plaza : plazas) {
            if (plaza.alongX().equals(line) || plaza.alongZ().equals(line)) { return false; }
        }
        return true;
    }

    private boolean railCramped(CityGround ground, Line line, Function<Rail, CityRails.Bed> beds) {
        String kind = line.alley() ? "alley" : "street";
        Map<Line, int[]> profiles = new HashMap<>();
        for (Rail rail : rails) {
            if (rail.subway() || rail.alongX() != line.alongX()) { continue; }
            boolean beyond = line.at() > rail.last();
            int clear = beyond ? line.at() - rail.last() - 1 : rail.at() - line.last() - 1;
            if (clear < 0 && line.to() >= rail.from() && line.from() <= rail.to()) {
                ContentLog.LOGGER.debug("The {} at {} of the district at {}, {} is not laid: it runs on the railway line at {}", kind, line.at(), originX, originZ, rail.middle());
                return true;
            }
            CityRails.Bed track = beds.apply(rail);
            if (track == null || clear < 0) { continue; }
            int railRow = beyond ? rail.last() + 1 : rail.at() - 1;
            int streetRow = beyond ? line.at() : line.last();
            for (Line crossing : crossing(line)) {
                if (!line.covers(crossing.middle()) || !crossing.covers(line.middle())) { continue; }
                int center = crossing.at() + (crossing.width() - 1) / 2;
                int start = spanStart(crossing);
                int end = start + spanLength(crossing) - 1;
                if (!track.within(center) || rail.last() < start || rail.at() > end || rail.middle() < crossing.from() || rail.middle() > crossing.to() || metBetween(crossing, line, railRow, streetRow)) { continue; }
                int level = track.level(center);
                if (track.boredAt(center) && CityRails.roadAt(ground, this, crossing, (Math.max(start, rail.at()) + Math.min(end, rail.last())) / 2) >= level + CityRails.OVER) { continue; }
                if (clear < CityRails.MARGIN) {
                    ContentLog.LOGGER.debug("The {} at {} of the district at {}, {} is not laid: it would meet the street at {} {} row(s) from the railway line at {}, inside the {} row(s) a crossing keeps clear", kind, line.at(), originX, originZ, crossing.at(), clear, rail.middle(), CityRails.MARGIN);
                    return true;
                }
                int met = junctionLevel(ground, line.alongX() ? new Junction(line, crossing) : new Junction(crossing, line), profiles);
                if (Math.abs(met - level) > Math.abs(streetRow - railRow)) {
                    ContentLog.LOGGER.debug("The {} at {} of the district at {}, {} is not laid: the street at {} could not climb from the railway line at {} at y {} to it at y {} in {} row(s)", kind, line.at(), originX, originZ, crossing.at(), rail.middle(), level, met, Math.abs(streetRow - railRow));
                    return true;
                }
            }
        }
        return false;
    }

    private boolean metBetween(Line crossing, Line line, int railRow, int streetRow) {
        int least = Math.min(railRow, streetRow);
        int most = Math.max(railRow, streetRow);
        for (Line other : crossing(crossing)) {
            if (other.equals(line) || !other.covers(crossing.middle()) || !crossing.covers(other.middle())) { continue; }
            if (other.at() > least && other.last() < most) { return true; }
        }
        return false;
    }

    static int junctionLevel(CityGround ground, Junction junction, Map<Line, int[]> profiles) {
        Line setter = setter(junction);
        return apronLevel(ground, setter, setter.equals(junction.alongX()) ? junction.alongZ() : junction.alongX(), profiles.computeIfAbsent(setter, line -> apronProfile(ground, line)));
    }

    static Line setter(Junction junction) {
        Line row = junction.alongX();
        Line column = junction.alongZ();
        boolean rowEnds = endsOn(row, column);
        if (rowEnds != endsOn(column, row)) { return rowEnds ? column : row; }
        return owns(row, column) ? row : column;
    }

    private static boolean endsOn(Line line, Line other) { return line.from() >= other.at() || line.to() <= other.last(); }

    private static boolean owns(Line row, Line column) {
        if (row.from() != column.at()) { return row.from() < column.at(); }
        if (row.at() != column.from()) { return row.at() < column.from(); }
        if (row.to() != column.last()) { return row.to() < column.last(); }
        return row.last() <= column.to();
    }

    private static int[] apronProfile(CityGround ground, Line line) {
        int[] profile = new int[line.to() - line.from() + 1];
        for (int at = 0; at < profile.length; at++) {
            int row = line.from() + at;
            if (underwater(ground, line, row)) {
                profile[at] = Integer.MIN_VALUE;
                continue;
            }
            int flat = CityRails.flatAt(ground, line, row, flatRun());
            profile[at] = flat != Integer.MIN_VALUE ? flat : CityRails.alongLine(ground, line, row);
        }
        CityGrade.smoothRoad(profile);
        return profile;
    }

    private static int apronLevel(CityGround ground, Line setter, Line met, int[] profile) {
        int center = Math.clamp((met.at() + met.last()) / 2, setter.from(), setter.to()) - setter.from();
        int level = profile[center] != Integer.MIN_VALUE ? profile[center] : CityGrade.carried(profile, center);
        for (int row = Math.max(setter.from(), met.at()); row <= Math.min(setter.to(), met.last()); row++) {
            if (underwater(ground, setter, row)) { return Math.max(level, ground.sea()); }
        }
        return level == Integer.MIN_VALUE ? ground.sea() : level;
    }

    static boolean underwater(CityGround ground, Line line, int row) {
        int wet = 0;
        for (int across = line.at(); across <= line.last(); across++) {
            if (ground.floor(line.alongX() ? row : across, line.alongX() ? across : row) < ground.sea() - 1) { wet++; }
        }
        return wet * 2 >= line.width();
    }

    static long packed(int x, int z) { return ((long) x << 32) ^ (z & 0xFFFFFFFFL); }

    static long key(long seed, int districtX, int districtZ) { return seed ^ (districtX * 341873128712L) ^ (districtZ * 132897987541L); }

    CityPlan keeping(List<Plot> kept) { return new CityPlan(originX, originZ, block, alongX, alongZ, List.copyOf(kept), rails, windowX, windowZ, mapped, plazas, lifts, type, town); }

    CityPlan grownFrom(Town held, List<Line> ties) {
        List<Line> rows = new ArrayList<>(alongX);
        List<Line> columns = new ArrayList<>(alongZ);
        for (Line tie : ties) { (tie.alongX() ? rows : columns).add(tie); }
        return new CityPlan(originX, originZ, block, List.copyOf(rows), List.copyOf(columns), plots, rails, windowX, windowZ, mapped, plazas, lifts, type, held);
    }

    public List<Plot> plots() { return plots; }

    public boolean blocks(Plot tried, Plot moved) {
        if (!mapped && (tried.fromX() < originX || tried.fromZ() < originZ || tried.toX() >= originX + district || tried.toZ() >= originZ + district)) { return true; }
        for (Plot held : plots) {
            if (held != moved && tried.fromX() <= held.toX() && tried.toX() >= held.fromX() && tried.fromZ() <= held.toZ() && tried.toZ() >= held.fromZ()) { return true; }
        }
        for (Court court : plannedCourts(this)) {
            if (court.meets(tried.fromX(), tried.fromZ(), tried.toX(), tried.toZ())) { return true; }
        }
        List<Line> lines = new ArrayList<>(alongX);
        lines.addAll(alongZ);
        return CityPlanPlots.onRail(rails, tried) || CityPlanPlots.onLine(lines, tried, CityPlanPlots.GAP) || CityPlanPlots.besideWell(plazas, tried);
    }

    public List<Rail> rails() { return rails; }

    public static int railTail(boolean sub) { return Math.max(0, ContentControl.number(ContentControl.VILLAGES, sub ? "villageSubwayTail" : "villageRailTail", sub ? Config.worldgen.villageSubwayTail() : Config.worldgen.villageRailTail())); }

    public static int railWidth(boolean sub) { return ContentCity.railBed(sub) + ContentCity.railShoulderWidth(sub) * 2; }

    record Court(Line street, boolean low, int room, int stem) {
        int end() { return low ? street.from() : street.to(); }

        int dir() { return low ? -1 : 1; }

        Line line() { return CityCulDeSacs.courtLine(street, end(), dir(), room, stem); }

        boolean meets(int fromX, int fromZ, int toX, int toZ) { return room > 0 && CityCulDeSacs.courtBox(street, end(), dir(), room, stem).meets(fromX, fromZ, toX, toZ); }

        boolean serves(Line line) { return line.alongX() == street.alongX() && line.at() == street.at() && line.width() == street.width() && line.from() >= street.from() && line.to() <= street.to(); }
    }

    @Nullable static Court plannedCourt(CityPlan plan, Line line, boolean low) {
        for (Court court : plannedCourts(plan)) {
            if (court.low() == low && court.serves(line)) { return court; }
        }
        return null;
    }

    static List<Court> plannedCourts(CityPlan plan) {
        if (!plan.mapped || plan.town == null) { return List.of(); }
        Growth growth = CityGrowth.MAP_GROWTH.get(plan.town.key());
        return growth == null ? List.of() : growth.courts();
    }

    public static int spacing() {
        int packed = ContentControl.number(ContentControl.VILLAGES, "villageCitySpacing", Integer.MIN_VALUE);
        if (packed != Integer.MIN_VALUE) { return packed; }
        int chunks = CityDistricts.villageNumber("structureSpacing");
        if (chunks > 0) { return Math.max(1, (Math.max(9, chunks) * 16 + district() - 1) / district()); }
        return Config.worldgen.villageCitySpacing();
    }

    public static int flatRun() { return Math.max(1, ContentControl.number(ContentControl.VILLAGES, "villagePathFlatRun", Config.worldgen.villagePathFlatRun())); }

    public static int streetFullWidth() { return fullWidth(); }

    static int fullWidthFor(@Nullable JsonObject keys) { return keys == null ? fullWidth() : ContentControl.onRoad(keys, CityPlan::fullWidth); }

    static int fullWidth() { return VANILLA_WIDTH + extraWidth() * 2 + lineWidth() * 2 + walkWidth() * 2; }

    public static int extraWidthOf(Line line) { return ContentControl.onRoad(line.keys(), CityPlan::extraWidth); }

    static int extraWidth() { return Math.max(0, ContentControl.number(ContentControl.VILLAGES, "villagePathExtraWidth", Config.worldgen.villagePathExtraWidth())); }

    public static int lineWidth() { return ContentCity.lineBlock().isEmpty() ? 0 : 1; }

    public static int walkWidth() { return ContentCity.sidewalkBlock().isEmpty() ? 0 : Math.max(0, ContentControl.number(ContentControl.VILLAGES, "villagePathSidewalkWidth", Config.worldgen.villagePathSidewalkWidth())); }

    public static int plazaReach() { return 3 + (fullWidth() - 3) / 2; }

    public static int plazaPaved() { return plazaReach() - walkWidth(); }

    public static void reset() {
        district = Math.max(DISTRICT_LEAST, (2 * ContentVillages.largestPlot() + 2 * plazaReach() + 2 * fullWidth() + 15) / 16 * 16);
        CityPlanTowns.PLANS.clear();
        CityGrowth.CITIES.clear();
        CityDistricts.SITES.clear();
        CityDistricts.PLACES.clear();
        CityPlanTowns.RAW.clear();
        RAILED.clear();
        CitySeams.TIES.clear();
        CityLayout.MAP_TOWNS.clear();
        CityLayout.MAP_WELLS.clear();
        CityGrowth.MAP_GROWTH.clear();
        CityGrowth.MAP_SMALL.clear();
        CityDistricts.SHIFTED.clear();
        CityDistricts.pinnedAt = null;
        CityDistricts.offset = null;
        CityRails.forget();
        CityLinks.forget();
        CityGround.forget();
    }

    public static int district() { return district; }

    static int districtOf(int at, boolean alongX) { return Math.floorDiv(at - CityDistricts.offset(alongX), district); }

    static int windowOf(int index, boolean alongX) { return index * district + CityDistricts.offset(alongX); }

    public static int chunks() { return district / 16; }
}
