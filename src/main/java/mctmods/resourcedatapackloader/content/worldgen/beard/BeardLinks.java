package mctmods.resourcedatapackloader.content.worldgen.beard;

import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.content.village.RailPiece;
import mctmods.resourcedatapackloader.content.worldgen.beard.BeardLinksSurvey.Link;
import mctmods.resourcedatapackloader.content.worldgen.beard.BeardLinksSurvey.Node;
import mctmods.resourcedatapackloader.content.worldgen.ContentBeard;
import mctmods.resourcedatapackloader.content.worldgen.ContentSites;
import mctmods.resourcedatapackloader.content.worldgen.ContentStructureSearch;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Longs;
import mctmods.resourcedatapackloader.util.world.SeededRandom;

import net.minecraft.block.BlockRailBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureStart;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.WeakHashMap;
import javax.annotation.Nullable;

public final class BeardLinks {
    static final int NONE = 0;
    static final int OPEN = 1;
    static final int SPUR = 2;
    static final int CURVE = 3;
    static final int BROKEN = 4;
    static final int BED = 5;
    static final int WALL = 6;
    static final int APPROACH = 4;
    private static final int PACKED = 10;
    private static final int HEAD = 5;
    private static final int END = HEAD + PACKED;
    private static final Map<World, Set<Long>> FOUNDED = new WeakHashMap<>();

    private BeardLinks() {}

    public static boolean on() { return ContentControl.flag(ContentControl.VILLAGES, "villageRailLinks", Config.worldgen.villageRailLinks) && (BeardRails.lines(false) > 0 || BeardRails.lines(true) > 0); }

    static boolean carriedUnderground() { return BeardRails.lines(false) <= 0; }

    private static String stationShape() { return ContentControl.text(ContentControl.VILLAGES, "villageRailLinkStation", Config.worldgen.villageRailLinkStation).trim().toLowerCase(Locale.ROOT); }

    private static int stationLength() { return Math.max(0, ContentControl.number(ContentControl.VILLAGES, "villageRailLinkStationLength", Config.worldgen.villageRailLinkStationLength)); }

    private static int platformWidth() { return Math.max(0, ContentControl.number(ContentControl.VILLAGES, "villageRailLinkPlatformWidth", Config.worldgen.villageRailLinkPlatformWidth)); }

    private static boolean stations() { return !stationShape().isEmpty() && !stationShape().startsWith("n") && stationLength() > 0 && platformWidth() > 0; }

    static BeardRoads.Palette platformBlocks() { return BeardRoads.pathPalette("villageRailLinkPlatformBlock", Config.worldgen.villageRailLinkPlatformBlock, Blocks.STONEBRICK.getDefaultState()); }

    static BeardRoads.Palette railing() { return BeardStations.railingBlocks(); }

    static int pinRows(boolean buried) { return APPROACH + (stations() && !buried ? stationLength() : 0); }

    static int platformReach(boolean buried) { return stations() && !buried ? platformWidth() + 1 : 0; }

    static int linkReach() { return on() && carriedUnderground() ? platformReach(false) : 0; }

    static int ramp() { return BeardRails.subwayDepth() * BeardRails.climb(true); }

    private static int grid(World world) { return ContentSites.of(world, ContentBeard.villageSpacing(world)).spacing(); }

    public static int chunkRange(int spacing) { return spacing * 3 / 2 + 2; }

    static Random roll(World world, StructureBoundingBox well, boolean sub) { return SeededRandom.at(world, well.minX, sub ? 1 : 0, well.minZ); }

    public static final class Trunk {
        final boolean alongX;
        final int across;
        final int least;
        final int most;
        final boolean sub;
        final boolean buried;
        final int[] rows;
        final int[] sides;

        Trunk(boolean alongX, int across, int least, int most, boolean sub, boolean buried, int[] rows, int[] sides) {
            this.alongX = alongX;
            this.across = across;
            this.least = least;
            this.most = most;
            this.sub = sub;
            this.buried = buried;
            this.rows = rows;
            this.sides = sides;
        }

        public static Trunk of(int[] packed) { return new Trunk(packed[0] != 0, packed[1], packed[2], packed[3], packed[4] != 0, packed[5] != 0, new int[] { packed[6], packed[8] }, new int[] { packed[7], packed[9] }); }

        int[] pack() { return new int[] { alongX ? 1 : 0, across, least, most, sub ? 1 : 0, buried ? 1 : 0, rows[0], sides[0], rows[1], sides[1] }; }

        public boolean alongX() { return alongX; }

        public int least() { return least; }

        public int most() { return most; }

        int half() { return (BeardRails.width(buried) - 1) / 2; }

        int spurHalf() { return (BeardRails.width(sub) - 1) / 2; }

        int reach() { return spurHalf() + 1; }

        int mid() { return Math.floorDiv(rows[0] + rows[1], 2); }

        boolean straight() { return rows[0] == rows[1]; }

        int[] tracks() { return BeardRails.trackRows(buried, across); }

        @Override public boolean equals(Object other) { return other instanceof Trunk && Arrays.equals(pack(), ((Trunk) other).pack()); }

        @Override public int hashCode() { return Arrays.hashCode(pack()); }
    }

    static int lineCenter(World world, StructureBoundingBox well, boolean sub, int rolled) {
        if (!on() || sub != carriedUnderground() || BeardSurface.unreadable(world)) { return rolled; }
        int grid = grid(world);
        Node node = BeardLinksSurvey.node(world, grid, Math.floorDiv((well.minX + 2) >> 4, grid), Math.floorDiv((well.minZ + 2) >> 4, grid));
        return node == null || !node.is(well) ? rolled : node.center;
    }

    @Nullable public static BeardRoads.Grade trunkGrade(World world, Trunk trunk) {
        int[][] graded = BeardLinksSurvey.trunkProfile(world, trunk);
        if (graded == null) { return null; }
        int rows = graded[0].length;
        boolean[] bridged = new boolean[rows];
        for (int at = 0; at < rows; at++) { bridged[at] = graded[2][at] != 0; }
        ContentLog.LOGGER.debug("A railway link trunk along {} {} grades rows {} to {} from y {} to y {}, level at its joins", trunk.alongX ? "z" : "x", trunk.across, trunk.least, trunk.most, graded[0][0], graded[0][rows - 1]);
        return new BeardRoads.Grade(graded[0].clone(), graded[1].clone(), bridged, new boolean[rows], trunk.least, 0);
    }

    static void extend(World world, StructureStart start, StructureVillagePieces.Start well, boolean sub) {
        if (!on() || sub != carriedUnderground() || BeardSurface.unreadable(world)) { return; }
        StructureBoundingBox wellBox = well.getBoundingBox();
        int grid = grid(world);
        int cellX = Math.floorDiv((wellBox.minX + 2) >> 4, grid);
        int cellZ = Math.floorDiv((wellBox.minZ + 2) >> 4, grid);
        Node node = BeardLinksSurvey.node(world, grid, cellX, cellZ);
        if (node == null || !node.is(wellBox)) {
            ContentLog.LOGGER.debug("The village at {}, {} is not the one its cell links, so it lays no railway link", wellBox.minX, wellBox.minZ);
            return;
        }
        List<StructureComponent> components = start.getComponents();
        RailPiece line = null;
        for (StructureComponent piece : components) {
            if (piece instanceof RailPiece && ((RailPiece) piece).line() == 0 && ((RailPiece) piece).subway() == sub && ((RailPiece) piece).trunk() == null) { line = (RailPiece) piece; }
        }
        if (line == null || line.alongX() != node.alongX) { return; }
        boolean widened = false;
        for (boolean lowSide : new boolean[] { true, false }) {
            Link link = BeardLinksSurvey.built(world, grid, lowSide ? cellX : cellX - (node.alongX ? 1 : 0), lowSide ? cellZ : cellZ - (node.alongX ? 0 : 1), node.alongX);
            if (link == null) { continue; }
            int dir = lowSide ? 1 : -1;
            int end = link.spurEnd(lowSide);
            StructureBoundingBox box = line.getBoundingBox();
            if (node.alongX) {
                if (lowSide) { box.maxX = end; }
                else { box.minX = end; }
            }
            else {
                if (lowSide) { box.maxZ = end; }
                else { box.minZ = end; }
            }
            if (!widened && platformReach(link.trunk.buried) > 0) {
                widened = true;
                int wider = platformReach(false);
                if (node.alongX) {
                    box.minZ -= wider;
                    box.maxZ += wider;
                }
                else {
                    box.minX -= wider;
                    box.maxX += wider;
                }
            }
            Node partner = link.node(!lowSide);
            int[] packed = new int[END];
            packed[0] = dir;
            packed[1] = end;
            packed[2] = end;
            packed[3] = partner.well.minX;
            packed[4] = partner.well.minZ;
            System.arraycopy(link.trunk.pack(), 0, packed, HEAD, PACKED);
            line.end(packed);
            line.regrade();
            components.add(trunkPiece(link, lowSide, well));
            ContentLog.LOGGER.debug("{} line 0 of the village at {}, {} runs on to row {} as a spur and joins its railway link trunk at {}, laying the trunk to row {}", sub ? "Subway" : "Railway", wellBox.minX, wellBox.minZ, end, link.trunk.rows[lowSide ? 0 : 1], link.trunk.mid());
        }
    }

    public static void fitted(RailPiece rail, int dir, int tailEnd) {
        int[] ends = rail.ends();
        for (int at = 0; at + END <= ends.length; at += END) { if (ends[at] == dir) { ends[at + 2] = tailEnd; } }
    }

    private static StructureBoundingBox box(boolean alongX, int rowLeast, int rowMost, int acrossLeast, int acrossMost, StructureBoundingBox well) {
        int nominal = BeardSite.wellNominal(well);
        return alongX
                ? new StructureBoundingBox(rowLeast, nominal - BeardRails.BELOW, acrossLeast, rowMost, nominal + BeardRails.ABOVE, acrossMost)
                : new StructureBoundingBox(acrossLeast, nominal - BeardRails.BELOW, rowLeast, acrossMost, nominal + BeardRails.ABOVE, rowMost);
    }

    private static StructureBoundingBox trunkBox(Link link, boolean lowSide) {
        Trunk trunk = link.trunk;
        boolean first = trunk.straight() ? lowSide : trunk.rows[lowSide ? 0 : 1] == Math.min(trunk.rows[0], trunk.rows[1]);
        return box(trunk.alongX, first ? trunk.least : trunk.mid() + 1, first ? trunk.mid() : trunk.most, trunk.across - trunk.half(), trunk.across + trunk.half(), link.node(lowSide).well);
    }

    private static int[] trunkData(Link link, boolean lowSide) {
        Node partner = link.node(!lowSide);
        int[] packed = Arrays.copyOf(link.trunk.pack(), PACKED + 2);
        packed[PACKED] = partner.well.minX;
        packed[PACKED + 1] = partner.well.minZ;
        return packed;
    }

    private static RailPiece trunkPiece(Link link, boolean lowSide, StructureVillagePieces.Start well) {
        RailPiece piece = new RailPiece(well, trunkBox(link, lowSide), link.trunk.alongX, -1, link.trunk.buried);
        piece.trunk(trunkData(link, lowSide));
        return piece;
    }

    private static List<RailPiece> plan(Link link, boolean lowSide) {
        List<RailPiece> held = lowSide ? link.lowPlan : link.highPlan;
        if (held != null) { return held; }
        Node node = link.node(lowSide);
        int end = link.spurEnd(lowSide);
        int half = BeardRails.half(node.sub) + platformReach(link.trunk.buried);
        List<RailPiece> made = new ArrayList<>();
        made.add(new RailPiece(box(node.alongX, Math.min(node.wellAlong, end), Math.max(node.wellAlong, end), node.center - half, node.center + half, node.well), node.alongX, link.trunk.buried));
        RailPiece trunk = new RailPiece(trunkBox(link, lowSide), link.trunk.alongX, link.trunk.buried);
        trunk.trunk(trunkData(link, lowSide));
        made.add(trunk);
        if (lowSide) { link.lowPlan = made; }
        else { link.highPlan = made; }
        return made;
    }

    public static List<RailPiece> planned(World world, @Nullable List<StructureComponent> own) {
        if (own == null || own.isEmpty() || !(own.get(0) instanceof StructureVillagePieces.Start) || !on() || BeardSurface.unreadable(world)) { return Collections.emptyList(); }
        StructureBoundingBox well = own.get(0).getBoundingBox();
        int grid = grid(world);
        int cellX = Math.floorDiv((well.minX + 2) >> 4, grid);
        int cellZ = Math.floorDiv((well.minZ + 2) >> 4, grid);
        List<RailPiece> found = new ArrayList<>();
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                for (boolean alongX : new boolean[] { true, false }) {
                    Link link = BeardLinksSurvey.built(world, grid, cellX + dx, cellZ + dz, alongX);
                    if (link == null) { continue; }
                    for (boolean lowSide : new boolean[] { true, false }) {
                        Node node = link.node(lowSide);
                        if (node.is(well)) { continue; }
                        if (founded(world, node.well.minX, node.well.minZ)) { continue; }
                        found.addAll(plan(link, lowSide));
                    }
                }
            }
        }
        return found;
    }

    private static boolean founded(World world, int wellX, int wellZ) {
        Set<Long> known = FOUNDED.computeIfAbsent(world, key -> new HashSet<>());
        long key = Longs.pack(wellX, wellZ);
        if (known.contains(key)) { return true; }
        if (!ContentStructureSearch.villageFoundedAt(world, (wellX + 2) >> 4, (wellZ + 2) >> 4)) { return false; }
        known.add(key);
        return true;
    }

    public static boolean partnerless(World world, RailPiece rail) {
        int[] packed = rail.trunk();
        if (packed == null || packed.length < PACKED + 2) { return false; }
        if (founded(world, packed[PACKED], packed[PACKED + 1])) { return false; }
        ContentLog.LOGGER.debug("Railway link trunk at {} lays nothing: the village at {}, {} it runs to was never founded", rail.getBoundingBox(), packed[PACKED], packed[PACKED + 1]);
        return true;
    }

    public static boolean unlaid(World world, RailPiece rail, int row) {
        int[] ends = rail.ends();
        for (int at = 0; at + END <= ends.length; at += END) {
            int dir = ends[at];
            if ((row - ends[at + 2]) * dir <= 0) { continue; }
            if (!founded(world, ends[at + 3], ends[at + 4])) { return true; }
        }
        return false;
    }

    public static boolean linked(RailPiece rail, int dir) {
        int[] ends = rail.ends();
        for (int at = 0; at + END <= ends.length; at += END) { if (ends[at] == dir) { return true; } }
        return false;
    }

    private static Trunk trunkOfEnd(int[] ends, int at) { return Trunk.of(Arrays.copyOfRange(ends, at + HEAD, at + END)); }

    private static int joinOf(Trunk trunk, int dir) { return trunk.sides[0] == -dir ? 0 : 1; }

    static int pin(World world, RailPiece rail, int rowLeast, int[] profile, boolean[] fixed, int climb) {
        int[] ends = rail.ends();
        int pinned = 0;
        boolean[] band = new boolean[profile.length];
        for (int at = 0; at + END <= ends.length; at += END) {
            int dir = ends[at];
            Trunk trunk = trunkOfEnd(ends, at);
            int[][] graded = BeardLinksSurvey.trunkProfile(world, trunk);
            if (graded == null) { continue; }
            int level = graded[0][trunk.rows[joinOf(trunk, dir)] - trunk.least];
            for (int k = 0; k < pinRows(trunk.buried); k++) {
                int i = ends[at + 1] - dir * k - rowLeast;
                if (i < 0 || i >= profile.length) { continue; }
                profile[i] = level;
                fixed[i] = true;
                band[i] = true;
            }
            pinned++;
            ContentLog.LOGGER.debug("Railway line {} holds its last {} row(s) at y {} where it joins its link trunk", rail.line(), pinRows(trunk.buried), level);
        }
        if (pinned > 0) { BeardRailsGrade.approach(profile, fixed, band, climb); }
        return pinned;
    }

    @Nullable static int[] climbOut(RailPiece rail) {
        int[] ends = rail.ends();
        if (ends.length < END || trunkOfEnd(ends, 0).buried) { return null; }
        int dir = ends[0];
        return new int[] { ends[1] - dir * (pinRows(false) - 1) - dir * ramp(), dir };
    }

    private static int fromEnd(RailPiece rail, int row, boolean surfaceOnly, boolean station) {
        int[] ends = rail.ends();
        for (int at = 0; at + END <= ends.length; at += END) {
            boolean buried = trunkOfEnd(ends, at).buried;
            if (surfaceOnly && buried) { continue; }
            int k = (ends[at + 1] - row) * ends[at];
            if (k >= 0 && k < (station ? pinRows(buried) : APPROACH)) { return k; }
        }
        return -1;
    }

    static boolean opens(RailPiece rail, int row) {
        int[] packed = rail.trunk();
        if (packed == null) { return fromEnd(rail, row, true, true) >= 0; }
        Trunk trunk = Trunk.of(packed);
        if (trunk.buried) { return false; }
        for (int join : trunk.rows) { if (Math.abs(row - join) <= trunk.reach() + APPROACH) { return true; } }
        return false;
    }

    static boolean joinRow(RailPiece rail, int row) {
        int[] packed = rail.trunk();
        if (packed == null) { return fromEnd(rail, row, false, false) >= 0; }
        Trunk trunk = Trunk.of(packed);
        for (int join : trunk.rows) { if (Math.abs(row - join) <= trunk.reach()) { return true; } }
        return false;
    }

    static int platformAt(RailPiece rail, int row) {
        if (rail.trunk() != null || !stations()) { return -1; }
        int k = fromEnd(rail, row, true, true);
        return k >= APPROACH ? platformWidth() : -1;
    }

    static boolean platformSide(RailPiece rail, int row, int side) {
        if (!stationShape().startsWith("o")) { return true; }
        int[] ends = rail.ends();
        for (int at = 0; at + END <= ends.length; at += END) {
            int k = (ends[at + 1] - row) * ends[at];
            if (k < 0 || k >= pinRows(false)) { continue; }
            return side == (rail.alongX() ? -ends[at] : ends[at]);
        }
        return false;
    }

    static int junction(RailPiece rail, int row, int across) {
        int[] packed = rail.trunk();
        if (packed == null) { return NONE; }
        Trunk trunk = Trunk.of(packed);
        int half = trunk.half();
        if (trunk.straight()) { return crossing(trunk, row, across); }
        for (int j = 0; j < 2; j++) {
            int join = trunk.rows[j];
            int side = trunk.sides[j];
            if (Math.abs(row - join) > trunk.spurHalf()) { continue; }
            int out = (across - trunk.across) * side;
            int[] tracks = trunk.tracks();
            int near = side < 0 ? tracks[0] : tracks[tracks.length - 1];
            int nearOut = (near - trunk.across) * side;
            if (out > half + 1 || out < nearOut) { continue; }
            if (out == half + 1) { return OPEN; }
            int[] spur = BeardRails.trackRows(trunk.sub, join);
            if (out > nearOut) { return contains(spur, row) ? SPUR : BED; }
            if (spur.length == 1) { return row == join ? CURVE : NONE; }
            if (row == spur[0] || row == spur[spur.length - 1]) { return CURVE; }
            if (row > spur[0] && row < spur[spur.length - 1]) { return BROKEN; }
        }
        return NONE;
    }

    private static int crossing(Trunk trunk, int row, int across) {
        int off = Math.abs(row - trunk.rows[0]);
        if (Math.abs(across - trunk.across) > trunk.half() || off > trunk.reach()) { return OPEN; }
        if (off == trunk.reach()) { return WALL; }
        return contains(BeardRails.trackRows(trunk.sub, trunk.rows[0]), row) ? SPUR : BED;
    }

    private static boolean contains(int[] rows, int row) {
        for (int held : rows) { if (held == row) { return true; } }
        return false;
    }

    static IBlockState junctionTrack(RailPiece rail, int row, int across, IBlockState track) {
        int code = junction(rail, row, across);
        int[] packed = rail.trunk();
        if (packed == null || (code != SPUR && code != CURVE)) { return track; }
        Trunk trunk = Trunk.of(packed);
        if (code == SPUR) { return BeardRails.shaped(track, trunk.alongX ? BlockRailBase.EnumRailDirection.NORTH_SOUTH : BlockRailBase.EnumRailDirection.EAST_WEST, !trunk.alongX); }
        int j = Math.abs(row - trunk.rows[0]) <= trunk.spurHalf() ? 0 : 1;
        int side = trunk.sides[j];
        EnumFacing.Axis rowAxis = trunk.alongX ? EnumFacing.Axis.X : EnumFacing.Axis.Z;
        EnumFacing.Axis acrossAxis = trunk.alongX ? EnumFacing.Axis.Z : EnumFacing.Axis.X;
        int[] spur = BeardRails.trackRows(trunk.sub, trunk.rows[j]);
        int way;
        if (spur.length == 1) { way = EnumFacing.getFacingFromAxis(side > 0 ? EnumFacing.AxisDirection.NEGATIVE : EnumFacing.AxisDirection.POSITIVE, acrossAxis).rotateYCCW().getAxisDirection().getOffset(); }
        else { way = row == spur[0] ? -1 : 1; }
        EnumFacing toSpur = EnumFacing.getFacingFromAxis(side > 0 ? EnumFacing.AxisDirection.POSITIVE : EnumFacing.AxisDirection.NEGATIVE, acrossAxis);
        EnumFacing along = EnumFacing.getFacingFromAxis(way > 0 ? EnumFacing.AxisDirection.POSITIVE : EnumFacing.AxisDirection.NEGATIVE, rowAxis);
        boolean north = toSpur == EnumFacing.NORTH || along == EnumFacing.NORTH;
        boolean east = toSpur == EnumFacing.EAST || along == EnumFacing.EAST;
        BlockRailBase.EnumRailDirection shape = north ? (east ? BlockRailBase.EnumRailDirection.NORTH_EAST : BlockRailBase.EnumRailDirection.NORTH_WEST) : (east ? BlockRailBase.EnumRailDirection.SOUTH_EAST : BlockRailBase.EnumRailDirection.SOUTH_WEST);
        return BeardRails.shaped(track, shape, trunk.alongX);
    }

    static int force(World world, StructureBoundingBox clip, RailPiece rail, BeardRoads.Grade grade, IBlockState track, BlockPos.MutableBlockPos at) {
        int[] packed = rail.trunk();
        if (packed == null || !BeardRails.railBlock(track)) { return 0; }
        Trunk trunk = Trunk.of(packed);
        int[] tracks = trunk.tracks();
        int forced = 0;
        for (int join : trunk.rows) {
            for (int row = join - trunk.reach(); row <= join + trunk.reach(); row++) {
                int level = grade.at(row);
                if (level == Integer.MIN_VALUE) { continue; }
                for (int across = trunk.across - trunk.half(); across <= trunk.across + trunk.half(); across++) {
                    int code = junction(rail, row, across);
                    IBlockState wanted = code == SPUR || code == CURVE ? junctionTrack(rail, row, across, track) : code == NONE && contains(tracks, across) ? track : null;
                    if (wanted == null) { continue; }
                    int x = trunk.alongX ? row : across;
                    int z = trunk.alongX ? across : row;
                    at.setPos(x, level + 1, z);
                    if (!clip.isVecInside(at) || BeardKeep.holds(x, level + 1, z)) { continue; }
                    IBlockState held = world.getBlockState(at);
                    if (held.getBlock() != wanted.getBlock() || held == wanted) { continue; }
                    world.setBlockState(at, wanted, 2);
                    forced++;
                }
            }
        }
        return forced;
    }

    static int benches(World world, StructureBoundingBox clip, RailPiece rail, BeardRoads.Grade grade, int center, int boreHalf, boolean inBed) {
        if (rail.trunk() != null || !stations()) { return 0; }
        int[] ends = rail.ends();
        int span = BeardStations.benchLength();
        BlockPos.MutableBlockPos at = new BlockPos.MutableBlockPos();
        int laid = 0;
        for (int end = 0; end + END <= ends.length; end += END) {
            if (trunkOfEnd(ends, end).buried) { continue; }
            int dir = ends[end];
            int near = ends[end + 1] - dir * APPROACH;
            int far = ends[end + 1] - dir * (pinRows(false) - 1);
            int middle = (near + far) / 2;
            int level = grade.at(middle);
            if (level == Integer.MIN_VALUE || unlaid(world, rail, middle)) { continue; }
            int top = inBed ? level : level + 1;
            for (int side : new int[] { -1, 1 }) {
                if (!platformSide(rail, middle, side)) { continue; }
                int across = center + side * (boreHalf + (platformWidth() + 1) / 2);
                laid += BeardStations.bench(world, clip, rail.alongX(), middle - span / 2, across, top + 1, BeardStations.awayFrom(rail.alongX(), side), at);
            }
        }
        return laid;
    }
}
