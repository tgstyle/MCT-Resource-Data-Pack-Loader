package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.content.worldgen.CityLinksSurvey.Decision;
import mctmods.resourcedatapackloader.content.worldgen.CityLinksSurvey.Graded;
import mctmods.resourcedatapackloader.content.worldgen.CityLinksSurvey.Link;
import mctmods.resourcedatapackloader.content.worldgen.CityLinksSurvey.Node;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nullable;

public final class CityLinks {
    public static final int NONE = 0;
    public static final int OPEN = 1;
    public static final int SPUR = 2;
    public static final int CURVE = 3;
    public static final int BROKEN = 4;
    public static final int BED = 5;
    public static final int WALL = 6;
    static final int APPROACH = 4;
    private static final int HEAD = 5;
    private static final int PACKED = 10;
    private static final int END = HEAD + PACKED;
    private static final int[] NO_ENDS = new int[0];
    private static final Map<Long, List<Link>> JOINED = new ConcurrentHashMap<>();
    private static final Map<CityPlan.Rail, Half> HALVES = new ConcurrentHashMap<>();
    private static final Map<CityPlan.Rail, int[]> ENDS = new ConcurrentHashMap<>();

    private CityLinks() {}

    public record Trunk(boolean alongX, int across, int least, int most, boolean sub, boolean buried, int lowRow, int lowSide, int highRow, int highSide) {
        public int half() { return (CityPlan.railWidth(buried) - 1) / 2; }

        public int spurHalf() { return (CityPlan.railWidth(sub) - 1) / 2; }

        public int reach() { return spurHalf() + 1; }

        public int row(boolean low) { return low ? lowRow : highRow; }

        public int mid() { return Math.floorDiv(lowRow + highRow, 2); }

        public boolean straight() { return lowRow == highRow; }

        public int side(boolean low) { return low ? lowSide : highSide; }

        public int[] tracks() { return trackRows(buried, across); }

        public static Trunk of(int[] packed) { return new Trunk(packed[0] != 0, packed[1], packed[2], packed[3], packed[4] != 0, packed[5] != 0, packed[6], packed[7], packed[8], packed[9]); }

        public int[] pack() { return new int[] {alongX ? 1 : 0, across, least, most, sub ? 1 : 0, buried ? 1 : 0, lowRow, lowSide, highRow, highSide}; }
    }

    private record Half(Trunk trunk, int partnerX, int partnerZ) {}

    public static void forget() {
        CityLinksSurvey.forget();
        JOINED.clear();
        HALVES.clear();
        ENDS.clear();
    }

    public static boolean on() { return ContentControl.flag(ContentControl.VILLAGES, "villageRailLinks", Config.worldgen.villageRailLinks()) && (CityRails.lines(false) > 0 || CityRails.lines(true) > 0); }

    static boolean carriedUnderground() { return CityRails.lines(false) <= 0; }

    private static String stationShape() { return ContentControl.text(ContentControl.VILLAGES, "villageRailLinkStation", Config.worldgen.villageRailLinkStation()).trim().toLowerCase(Locale.ROOT); }

    private static int stationLength() { return Math.max(0, ContentControl.number(ContentControl.VILLAGES, "villageRailLinkStationLength", Config.worldgen.villageRailLinkStationLength())); }

    private static int platformWidth() { return Math.max(0, ContentControl.number(ContentControl.VILLAGES, "villageRailLinkPlatformWidth", Config.worldgen.villageRailLinkPlatformWidth())); }

    private static boolean stations() { return !stationShape().isEmpty() && !stationShape().startsWith("n") && stationLength() > 0 && platformWidth() > 0; }

    static int pinRows(boolean buried) { return APPROACH + (stations() && !buried ? stationLength() : 0); }

    static int platformReach(boolean buried) { return stations() && !buried ? platformWidth() + 1 : 0; }

    static int linkReach() { return on() && carriedUnderground() ? platformReach(false) : 0; }

    static int ramp() { return ContentCity.subwayDepth() * ContentCity.railClimb(true); }

    static int cellOf(int at, boolean alongX) { return Math.floorDiv(CityPlan.districtOf(at, alongX), Math.max(1, CityPlan.spacing())); }

    static List<Link> linksOf(CityGround ground, CityPlan.Town town) {
        if (!on()) { return List.of(); }
        List<Link> held = JOINED.get(town.key());
        if (held != null) { return held; }
        int cellX = cellOf(town.wellX(), true);
        int cellZ = cellOf(town.wellZ(), false);
        Node mine = CityLinksSurvey.node(ground, cellX, cellZ);
        List<Link> found = new ArrayList<>();
        if (mine == null || mine.town().key() != town.key()) {
            ContentLog.LOGGER.debug("The city at {}, {} is not the city its cell links, so it takes no railway link", town.wellX(), town.wellZ());
            JOINED.putIfAbsent(town.key(), List.of());
            return List.of();
        }
        for (boolean alongX : new boolean[] {true, false}) {
            for (boolean lowSide : new boolean[] {true, false}) {
                Decision decision = CityLinksSurvey.built(ground, lowSide ? cellX : cellX - (alongX ? 1 : 0), lowSide ? cellZ : cellZ - (alongX ? 0 : 1), alongX);
                String side = alongX ? (lowSide ? "east" : "west") : (lowSide ? "south" : "north");
                Link link = decision.link();
                if (link == null) {
                    if (decision.why() != null) { ContentLog.LOGGER.debug("The city at {}, {} takes no railway link on its {} side: {}", town.wellX(), town.wellZ(), side, decision.why().brief()); }
                    continue;
                }
                Node partner = link.node(!link.lowSide(town));
                found.add(link);
                ContentLog.LOGGER.debug("The city at {}, {} takes the railway link on its {} side to the city at {}, {}: {} block(s) long, the seam at {} {}, its trunk from {} to {}, joining at {} and {}", town.wellX(), town.wellZ(), side, partner.town().wellX(), partner.town().wellZ(), link.length(), alongX ? "x" : "z", link.seam(), link.trunk().least(), link.trunk().most(), link.trunk().lowRow(), link.trunk().highRow());
            }
        }
        List<Link> made = List.copyOf(found);
        JOINED.putIfAbsent(town.key(), made);
        return made;
    }

    public static List<CityPlan.Rail> joined(CityGround ground, CityPlan.Town town, List<CityPlan.Rail> fitted) {
        List<Link> links = linksOf(ground, town);
        if (links.isEmpty()) { return fitted; }
        Node node = links.getFirst().node(links.getFirst().lowSide(town));
        int at = -1;
        for (int k = 0; k < fitted.size() && at < 0; k++) {
            CityPlan.Rail rail = fitted.get(k);
            if (rail.subway() == node.sub() && rail.alongX() == node.alongX() && rail.middle() == node.center()) { at = k; }
        }
        if (at < 0) {
            ContentLog.LOGGER.debug("The city at {}, {} lays none of its {} railway link(s): its first line was not laid", town.wellX(), town.wellZ(), links.size());
            return fitted;
        }
        CityPlan.Rail line = fitted.get(at);
        int from = line.from();
        int to = line.to();
        int[] ends = NO_ENDS;
        List<CityPlan.Rail> found = new ArrayList<>(fitted);
        for (Link link : links) {
            boolean lowSide = link.lowSide(town);
            int end = link.spurEnd(lowSide);
            int tail = lowSide ? to : from;
            if (lowSide) { to = end; }
            else { from = end; }
            Node partner = link.node(!lowSide);
            int[] record = Arrays.copyOf(new int[] {lowSide ? 1 : -1, end, tail, partner.town().wellX(), partner.town().wellZ()}, END);
            System.arraycopy(link.trunk().pack(), 0, record, HEAD, PACKED);
            int[] grown = Arrays.copyOf(ends, ends.length + END);
            System.arraycopy(record, 0, grown, ends.length, END);
            ends = grown;
            CityPlan.Rail half = half(link, lowSide, town);
            HALVES.putIfAbsent(half, new Half(link.trunk(), partner.town().wellX(), partner.town().wellZ()));
            found.add(half);
            ContentLog.LOGGER.debug("{} line 0 of the city at {}, {} runs on to row {} as a spur and joins its railway link trunk at {}, laying the trunk from row {} to row {}", node.sub() ? "Subway" : "Railway", town.wellX(), town.wellZ(), end, link.trunk().row(lowSide), half.from(), half.to());
        }
        CityPlan.Rail spur = new CityPlan.Rail(line.at(), line.width(), line.alongX(), line.subway(), from, to, town.key());
        ENDS.putIfAbsent(spur, ends);
        found.set(at, spur);
        return found;
    }

    private static CityPlan.Rail half(Link link, boolean lowSide, CityPlan.Town town) {
        Trunk trunk = link.trunk();
        boolean first = trunk.straight() ? lowSide : trunk.row(lowSide) == Math.min(trunk.lowRow(), trunk.highRow());
        return new CityPlan.Rail(trunk.across() - trunk.half(), CityPlan.railWidth(trunk.buried()), trunk.alongX(), trunk.buried(), first ? trunk.least() : trunk.mid() + 1, first ? trunk.mid() : trunk.most(), town.key());
    }

    public static List<CityPlan.Rail> linkRails(List<CityPlan.Rail> rails) {
        List<CityPlan.Rail> found = new ArrayList<>();
        for (CityPlan.Rail rail : rails) {
            if (HALVES.containsKey(rail) || ENDS.containsKey(rail)) { found.add(rail); }
        }
        return found;
    }

    @Nullable public static int[] trunkData(CityPlan.Rail rail) {
        Half half = HALVES.get(rail);
        if (half == null) { return null; }
        int[] packed = Arrays.copyOf(half.trunk().pack(), PACKED + 2);
        packed[PACKED] = half.partnerX();
        packed[PACKED + 1] = half.partnerZ();
        return packed;
    }

    public static int[] ends(CityPlan.Rail rail) { return ENDS.getOrDefault(rail, NO_ENDS); }

    private static boolean founded(ServerLevel level, int wellX, int wellZ) { return ContentStructureMost.mayFound(level, new ChunkPos((wellX + 2) >> 4, (wellZ + 2) >> 4)); }

    public static boolean partnerless(WorldGenLevel level, @Nullable int[] packed, BoundingBox held) {
        if (packed == null || packed.length < PACKED + 2 || founded(level.getLevel(), packed[PACKED], packed[PACKED + 1])) { return false; }
        ContentLog.LOGGER.debug("Railway link trunk at {} lays nothing: the city at {}, {} it runs to was never founded", held, packed[PACKED], packed[PACKED + 1]);
        return true;
    }

    public static int[] unfounded(ServerLevel level, int[] ends) {
        int[] found = NO_ENDS;
        for (int at = 0; at + END <= ends.length; at += END) {
            if (founded(level, ends[at + 3], ends[at + 4])) { continue; }
            found = Arrays.copyOf(found, found.length + END);
            System.arraycopy(ends, at, found, found.length - END, END);
        }
        return found;
    }

    public static boolean unlaid(int[] unfounded, int row) {
        for (int at = 0; at + END <= unfounded.length; at += END) {
            if ((row - unfounded[at + 2]) * unfounded[at] > 0) { return true; }
        }
        return false;
    }

    public static boolean beardless(int[] ends, int least, int most) {
        int[] rows = bearded(ends, least, most);
        return rows[0] > rows[1];
    }

    public static int[] bearded(int[] ends, int least, int most) {
        int[] rows = { least, most };
        for (int at = 0; at + END <= ends.length; at += END) {
            if (ends[at] > 0) { rows[1] = Math.min(rows[1], ends[at + 2]); }
            else { rows[0] = Math.max(rows[0], ends[at + 2]); }
        }
        return rows;
    }

    public static int claimReach(CityPlan.Rail rail) {
        int[] ends = ends(rail);
        for (int at = 0; at + END <= ends.length; at += END) {
            if (ends[at + HEAD + 5] == 0) { return platformReach(false); }
        }
        return 0;
    }

    @Nullable public static CityRails.Laid trunkLaid(CityGround ground, CityPlan.Rail rail) {
        Half half = HALVES.get(rail);
        if (half == null) { return null; }
        Trunk trunk = half.trunk();
        Graded graded = CityLinksSurvey.graded(ground, trunk);
        if (graded == null) { return null; }
        int length = graded.profile().length;
        int start = rail.from();
        int first = start - trunk.least();
        int last = rail.to() - trunk.least() + 1;
        boolean[] bored = trunk.buried() ? new boolean[length] : CityGrade.roofed(graded.profile(), graded.floor(), graded.bridged(), ContentCity.railTunnelDepth());
        boolean[] decking = trunk.buried() || ContentCity.railFrameBlock().isEmpty() ? new boolean[length] : CityGrade.frameRows(graded.bridged(), ContentCity.railFrameLeast(), ContentCity.railFrameRun());
        int[] packed = trunk.pack();
        List<CityPlan.Line> streets = trunk.buried() ? List.of() : CityRails.streetsAcross(ground, rail);
        int[] profile = Arrays.copyOfRange(graded.profile(), first, last);
        boolean[] tunnel = new boolean[last - first];
        boolean[] frames = new boolean[last - first];
        boolean[] crossed = new boolean[last - first];
        for (int at = first; at < last; at++) {
            int row = trunk.least() + at;
            boolean uncrossed = CityRails.uncrossed(ground, streets, trunk.alongX(), row, trunk.across(), graded.profile()[at]);
            tunnel[at - first] = trunk.buried() || (!opens(packed, NO_ENDS, row) && !graded.bridged()[at] && bored[at] && uncrossed);
            frames[at - first] = decking[at] && clearOfJoin(packed, NO_ENDS, row) && uncrossed;
            crossed[at - first] = !tunnel[at - first] && CityRails.roadOver(streets, trunk.alongX(), row, trunk.across());
        }
        int crossings = 0;
        for (CityPlan.Line line : streets) {
            if (line.alongX() == trunk.alongX() || trunk.across() < line.from() || trunk.across() > line.to() || line.last() < start || line.at() > rail.to()) { continue; }
            int level = graded.profile()[Mth.clamp(line.at() + (line.width() - 1) / 2 - trunk.least(), 0, length - 1)];
            boolean met = false;
            for (int row = Math.max(start, line.at()); row <= Math.min(rail.to(), line.last()); row++) {
                if (!crossed[row - start]) { continue; }
                profile[row - start] = level;
                met = true;
            }
            if (met) { crossings++; }
        }
        ContentLog.LOGGER.debug("The railway link trunk at {} {} grades rows {} to {} of its trunk from y {} to y {}, {} street crossing(s){}", trunk.alongX() ? "z" : "x", trunk.across(), start, rail.to(), graded.profile()[first], graded.profile()[last - 1], crossings, trunk.buried() ? ", underground, with no station chamber" : "");
        return new CityRails.Laid(rail, start, profile, Arrays.copyOfRange(graded.floor(), first, last), Arrays.copyOfRange(graded.bridged(), first, last), tunnel, Arrays.copyOfRange(bored, first, last), crossed, frames, null, List.of(), List.of());
    }

    @Nullable static CityRails.Bed trunkBed(CityGround ground, CityPlan.Rail rail) {
        Half half = HALVES.get(rail);
        if (half == null) { return null; }
        Trunk trunk = half.trunk();
        Graded graded = CityLinksSurvey.graded(ground, trunk);
        if (graded == null) { return null; }
        int first = rail.from() - trunk.least();
        int last = rail.to() - trunk.least() + 1;
        boolean[] bored = trunk.buried() ? new boolean[graded.profile().length] : CityGrade.roofed(graded.profile(), graded.floor(), graded.bridged(), ContentCity.railTunnelDepth());
        return new CityRails.Bed(rail.from(), Arrays.copyOfRange(graded.profile(), first, last), Arrays.copyOfRange(bored, first, last));
    }

    static int pin(CityGround ground, CityPlan.Rail rail, int rowLeast, int[] profile, boolean[] fixed, int climb) {
        int[] ends = ends(rail);
        int pinned = 0;
        boolean[] band = new boolean[profile.length];
        for (int at = 0; at + END <= ends.length; at += END) {
            int dir = ends[at];
            Trunk trunk = Trunk.of(Arrays.copyOfRange(ends, at + HEAD, at + END));
            Graded graded = CityLinksSurvey.graded(ground, trunk);
            if (graded == null) { continue; }
            int level = graded.profile()[trunk.row(trunk.lowSide() == -dir) - trunk.least()];
            for (int k = 0; k < pinRows(trunk.buried()); k++) {
                int row = ends[at + 1] - dir * k - rowLeast;
                if (row < 0 || row >= profile.length) { continue; }
                profile[row] = level;
                fixed[row] = true;
                band[row] = true;
            }
            pinned++;
            ContentLog.LOGGER.debug("The railway line at {} holds its last {} row(s) at y {} where it joins its link trunk at row {}", rail.middle(), pinRows(trunk.buried()), level, ends[at + 1]);
        }
        if (pinned > 0) { CityGrade.approach(profile, fixed, band, climb); }
        return pinned;
    }

    @Nullable static int[] climbOut(int[] ends) {
        if (ends.length < END || ends[HEAD + 5] != 0) { return null; }
        int dir = ends[0];
        return new int[] {ends[1] - dir * (pinRows(false) - 1) - dir * ramp(), dir};
    }

    private static int fromEnd(int[] ends, int row, boolean surfaceOnly, boolean station) {
        for (int at = 0; at + END <= ends.length; at += END) {
            boolean buried = ends[at + HEAD + 5] != 0;
            if (surfaceOnly && buried) { continue; }
            int k = (ends[at + 1] - row) * ends[at];
            if (k >= 0 && k < (station ? pinRows(buried) : APPROACH)) { return k; }
        }
        return -1;
    }

    static boolean opens(@Nullable int[] packed, int[] ends, int row) {
        if (packed == null) { return fromEnd(ends, row, true, true) >= 0; }
        Trunk trunk = Trunk.of(packed);
        if (trunk.buried()) { return false; }
        for (boolean low : new boolean[] {true, false}) {
            if (Math.abs(row - trunk.row(low)) <= trunk.reach() + APPROACH) { return true; }
        }
        return false;
    }

    public static boolean clearOfJoin(@Nullable int[] packed, int[] ends, int row) {
        if (packed == null) { return fromEnd(ends, row, false, false) < 0; }
        Trunk trunk = Trunk.of(packed);
        for (boolean low : new boolean[] {true, false}) {
            if (Math.abs(row - trunk.row(low)) <= trunk.reach()) { return false; }
        }
        return true;
    }

    public static int platformAt(@Nullable int[] packed, int[] ends, int row) {
        if (packed != null || !stations()) { return -1; }
        return fromEnd(ends, row, true, true) >= APPROACH ? platformWidth() : -1;
    }

    public static int platformOver(@Nullable int[] packed, int[] ends, int from, int to) {
        for (int row = from; row <= to; row++) {
            if (platformAt(packed, ends, row) > 0) { return platformWidth(); }
        }
        return 0;
    }

    public static boolean platformSide(int[] ends, boolean alongX, int row, int side) {
        if (!stationShape().startsWith("o")) { return true; }
        for (int at = 0; at + END <= ends.length; at += END) {
            int k = (ends[at + 1] - row) * ends[at];
            if (k < 0 || k >= pinRows(false)) { continue; }
            return side == (alongX ? -ends[at] : ends[at]);
        }
        return false;
    }

    public static CityPalette platformBlocks() { return CityPalette.of(ContentControl.text(ContentControl.VILLAGES, "villageRailLinkPlatformBlock", Config.worldgen.villageRailLinkPlatformBlock()).trim(), Blocks.STONE_BRICKS.defaultBlockState()); }

    public static int benches(WorldGenLevel level, BoundingBox clip, @Nullable int[] packed, int[] ends, int[] unfounded, boolean alongX, int center, int boreHalf, int top, BlockPos.MutableBlockPos at) {
        if (packed != null || !stations()) { return 0; }
        int span = ContentCity.benchLength();
        int laid = 0;
        for (int end = 0; end + END <= ends.length; end += END) {
            if (ends[end + HEAD + 5] != 0) { continue; }
            int dir = ends[end];
            int near = ends[end + 1] - dir * APPROACH;
            int far = ends[end + 1] - dir * (pinRows(false) - 1);
            int middle = (near + far) / 2;
            if (unlaid(unfounded, middle)) { continue; }
            for (int side : new int[] {-1, 1}) {
                if (!platformSide(ends, alongX, middle, side)) { continue; }
                int across = center + side * (boreHalf + (platformWidth() + 1) / 2);
                laid += ContentCityStairsPiece.bench(level, clip, alongX, middle - span / 2, across, top + 1, ContentCityStairsPiece.awayFrom(alongX, side), at);
            }
        }
        return laid;
    }

    public static int junction(@Nullable int[] packed, int row, int across) {
        if (packed == null) { return NONE; }
        Trunk trunk = Trunk.of(packed);
        int half = trunk.half();
        if (trunk.straight()) { return crossing(trunk, row, across); }
        for (boolean low : new boolean[] {true, false}) {
            int join = trunk.row(low);
            int side = trunk.side(low);
            if (Math.abs(row - join) > trunk.spurHalf()) { continue; }
            int out = (across - trunk.across()) * side;
            int[] tracks = trunk.tracks();
            int near = side < 0 ? tracks[0] : tracks[tracks.length - 1];
            int nearOut = (near - trunk.across()) * side;
            if (out > half + 1 || out < nearOut) { continue; }
            if (out == half + 1) { return OPEN; }
            int[] spur = trackRows(trunk.sub(), join);
            if (out > nearOut) { return contains(spur, row) ? SPUR : BED; }
            if (spur.length == 1) { return row == join ? CURVE : NONE; }
            if (row == spur[0] || row == spur[spur.length - 1]) { return CURVE; }
            if (row > spur[0] && row < spur[spur.length - 1]) { return BROKEN; }
        }
        return NONE;
    }

    private static int crossing(Trunk trunk, int row, int across) {
        int off = Math.abs(row - trunk.lowRow());
        if (Math.abs(across - trunk.across()) > trunk.half() || off > trunk.reach()) { return OPEN; }
        if (off == trunk.reach()) { return WALL; }
        return contains(trackRows(trunk.sub(), trunk.lowRow()), row) ? SPUR : BED;
    }

    public static BlockState junctionTrack(@Nullable int[] packed, int row, int across, BlockState track) {
        int code = junction(packed, row, across);
        if (packed == null || (code != SPUR && code != CURVE)) { return track; }
        Trunk trunk = Trunk.of(packed);
        if (code == SPUR) { return ContentCityRailPiece.shaped(track, trunk.alongX() ? RailShape.NORTH_SOUTH : RailShape.EAST_WEST, !trunk.alongX()); }
        boolean low = Math.abs(row - trunk.lowRow()) <= trunk.spurHalf();
        int side = trunk.side(low);
        Direction.Axis rowAxis = trunk.alongX() ? Direction.Axis.X : Direction.Axis.Z;
        Direction.Axis acrossAxis = trunk.alongX() ? Direction.Axis.Z : Direction.Axis.X;
        int[] spur = trackRows(trunk.sub(), trunk.row(low));
        int way = spur.length == 1 ? Direction.get(side > 0 ? Direction.AxisDirection.NEGATIVE : Direction.AxisDirection.POSITIVE, acrossAxis).getCounterClockWise().getAxisDirection().getStep() : row == spur[0] ? -1 : 1;
        Direction toSpur = Direction.get(side > 0 ? Direction.AxisDirection.POSITIVE : Direction.AxisDirection.NEGATIVE, acrossAxis);
        Direction along = Direction.get(way > 0 ? Direction.AxisDirection.POSITIVE : Direction.AxisDirection.NEGATIVE, rowAxis);
        boolean north = toSpur == Direction.NORTH || along == Direction.NORTH;
        boolean east = toSpur == Direction.EAST || along == Direction.EAST;
        RailShape shape = north ? (east ? RailShape.NORTH_EAST : RailShape.NORTH_WEST) : (east ? RailShape.SOUTH_EAST : RailShape.SOUTH_WEST);
        return ContentCityRailPiece.shaped(track, shape, trunk.alongX());
    }

    public static int force(WorldGenLevel level, BoundingBox clip, @Nullable int[] packed, int first, int last, int y, BlockState track, BlockPos.MutableBlockPos at) {
        if (packed == null || !(track.getBlock() instanceof BaseRailBlock)) { return 0; }
        Trunk trunk = Trunk.of(packed);
        int[] tracks = trunk.tracks();
        int forced = 0;
        for (boolean low : new boolean[] {true, false}) {
            int join = trunk.row(low);
            for (int row = Math.max(first, join - trunk.reach()); row <= Math.min(last, join + trunk.reach()); row++) {
                for (int across = trunk.across() - trunk.half(); across <= trunk.across() + trunk.half(); across++) {
                    int code = junction(packed, row, across);
                    BlockState wanted = code == SPUR || code == CURVE ? junctionTrack(packed, row, across, track) : code == NONE && contains(tracks, across) ? track : null;
                    if (wanted == null) { continue; }
                    at.set(trunk.alongX() ? row : across, y + 1, trunk.alongX() ? across : row);
                    if (!clip.isInside(at)) { continue; }
                    BlockState held = level.getBlockState(at);
                    if (held.getBlock() != wanted.getBlock() || held == wanted) { continue; }
                    level.setBlock(at, wanted, 2);
                    forced++;
                }
            }
        }
        return forced;
    }

    private static int[] trackRows(boolean sub, int center) {
        int count = Math.max(1, ContentCity.railTrackCount(sub));
        int gap = ContentCity.railTrackGap(sub);
        int first = count == 1 ? center : center - (count - 1) * gap / 2;
        int[] rows = new int[count];
        for (int at = 0; at < count; at++) { rows[at] = first + at * gap; }
        return rows;
    }

    private static boolean contains(int[] rows, int row) {
        for (int held : rows) {
            if (held == row) { return true; }
        }
        return false;
    }

    public static int linkCenter(CityGround ground, CityPlan.Town town, CityPlan.Rail rail) {
        if (!on() || rail.subway() != carriedUnderground()) { return Integer.MIN_VALUE; }
        CityPlan.Rail first = firstLine(ground, town);
        if (first == null || !first.equals(rail)) { return Integer.MIN_VALUE; }
        Node node = CityLinksSurvey.node(ground, cellOf(town.wellX(), true), cellOf(town.wellZ(), false));
        return node == null || node.town().key() != town.key() ? Integer.MIN_VALUE : node.center();
    }

    @Nullable static CityPlan.Rail firstLine(CityGround ground, CityPlan.Town town) {
        boolean sub = carriedUnderground();
        for (CityPlan.Rail rail : CityRails.placed(ground.seed(), town)) {
            if (rail.subway() == sub) { return rail; }
        }
        return null;
    }
}
