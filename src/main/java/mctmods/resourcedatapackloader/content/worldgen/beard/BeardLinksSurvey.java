package mctmods.resourcedatapackloader.content.worldgen.beard;

import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.content.village.RailPiece;
import mctmods.resourcedatapackloader.content.worldgen.beard.BeardLinks.Trunk;
import mctmods.resourcedatapackloader.content.worldgen.ContentBeard;
import mctmods.resourcedatapackloader.content.worldgen.ContentSites;
import mctmods.resourcedatapackloader.content.worldgen.ContentStructurePlacement;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Longs;
import mctmods.resourcedatapackloader.util.world.GenHeights;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import javax.annotation.Nullable;

final class BeardLinksSurvey {
    private static final Map<World, Map<Long, Node>> NODES = new WeakHashMap<>();
    private static final Map<World, Map<List<Integer>, Link>> LINKS = new WeakHashMap<>();
    private static final Map<World, Map<Trunk, int[][]>> TRUNKS = new WeakHashMap<>();

    private BeardLinksSurvey() {}

    private static int least() { return Math.max(0, ContentControl.number(ContentControl.VILLAGES, "villageRailLinkLeast", Config.worldgen.villageRailLinkLeast)); }

    private static int most() { return Math.max(0, ContentControl.number(ContentControl.VILLAGES, "villageRailLinkMost", Config.worldgen.villageRailLinkMost)); }

    private static int bridgeMost() { return Math.max(0, ContentControl.number(ContentControl.VILLAGES, "villageRailLinkBridgeMost", Config.worldgen.villageRailLinkBridgeMost)); }

    private static int tunnelMost() { return Math.max(0, ContentControl.number(ContentControl.VILLAGES, "villageRailLinkTunnelMost", Config.worldgen.villageRailLinkTunnelMost)); }

    static final class Node {
        final int cellX;
        final int cellZ;
        final StructureBoundingBox well;
        final boolean sub;
        final boolean alongX;
        final int center;
        final int wellAlong;

        Node(int cellX, int cellZ, StructureBoundingBox well, boolean sub, boolean alongX, int center) {
            this.cellX = cellX;
            this.cellZ = cellZ;
            this.well = well;
            this.sub = sub;
            this.alongX = alongX;
            this.center = center;
            this.wellAlong = alongX ? (well.minX + well.maxX) / 2 : (well.minZ + well.maxZ) / 2;
        }

        boolean is(StructureBoundingBox box) { return well.minX == box.minX && well.minZ == box.minZ; }
    }

    static final class Link {
        final Node low;
        final Node high;
        final Trunk trunk;
        final int length;
        final int seam;
        @Nullable List<RailPiece> lowPlan;
        @Nullable List<RailPiece> highPlan;

        Link(Node low, Node high, Trunk trunk, int length, int seam) {
            this.low = low;
            this.high = high;
            this.trunk = trunk;
            this.length = length;
            this.seam = seam;
        }

        Node node(boolean lowSide) { return lowSide ? low : high; }

        int spurEnd(boolean lowSide) { return lowSide ? seam - trunk.half() - 1 : seam + trunk.half() + 1; }
    }

    @Nullable static Node node(World world, int grid, int cellX, int cellZ) {
        Map<Long, Node> held = NODES.computeIfAbsent(world, key -> new HashMap<>());
        long key = Longs.pack(cellX, cellZ);
        if (held.containsKey(key)) { return held.get(key); }
        Node made = made(world, grid, cellX, cellZ);
        held.put(key, made);
        return made;
    }

    @Nullable private static Node made(World world, int grid, int cellX, int cellZ) {
        int[] rolled = rolled(world, grid, cellX, cellZ);
        if (rolled == null) { return null; }
        StructureBoundingBox well = wellIn(world, grid, cellX, cellZ);
        if (well == null) { return null; }
        boolean alongX = rolled[0] != 0;
        int lowX = cellX - (alongX ? 1 : 0);
        int lowZ = cellZ - (alongX ? 0 : 1);
        int[] low = rolled(world, grid, lowX, lowZ);
        int center = rolled[1];
        if (low != null && low[0] == rolled[0] && crowds(world, grid, lowX, lowZ, alongX, low[1], rolled[1])) {
            int[] lower = rolled(world, grid, lowX - (alongX ? 1 : 0), lowZ - (alongX ? 0 : 1));
            boolean lowMoves = lower != null && lower[0] == low[0] && crowds(world, grid, lowX - (alongX ? 1 : 0), lowZ - (alongX ? 0 : 1), alongX, lower[1], low[1]);
            int wellAcross = alongX ? (well.minZ + well.maxZ) / 2 : (well.minX + well.maxX) / 2;
            if (!lowMoves && low[1] - wellAcross >= BeardRails.clear(BeardLinks.carriedUnderground()) && BeardRails.clearsWell(well, alongX, low[1], BeardLinks.carriedUnderground())) {
                ContentLog.LOGGER.debug("The first line of the village at {}, {} moves from {} to {} to meet its neighbor's line head on and cross the seam straight", well.minX, well.minZ, center, low[1]);
                center = low[1];
            }
        }
        return new Node(cellX, cellZ, well, BeardLinks.carriedUnderground(), alongX, center);
    }

    @Nullable private static int[] rolled(World world, int grid, int cellX, int cellZ) {
        StructureBoundingBox well = wellIn(world, grid, cellX, cellZ);
        return well == null ? null : BeardRails.firstLine(world, well, BeardLinks.carriedUnderground());
    }

    private static boolean crowds(World world, int grid, int lowX, int lowZ, boolean alongX, int lowCenter, int highCenter) {
        int gap = Math.abs(lowCenter - highCenter);
        if (gap == 0) { return false; }
        StructureBoundingBox low = wellIn(world, grid, lowX, lowZ);
        StructureBoundingBox high = wellIn(world, grid, lowX + (alongX ? 1 : 0), lowZ + (alongX ? 0 : 1));
        if (low == null || high == null) { return false; }
        boolean buried = buried(grid, lowX, lowZ, alongX, low, high);
        return BeardRails.trackRows(buried, 0).length > 1 && gap < crowd(buried);
    }

    private static int crowd(boolean buried) { return 2 * BeardRails.width(buried) + 2 * ((BeardRails.width(BeardLinks.carriedUnderground()) - 1) / 2 + 1); }

    private static boolean buried(int grid, int lowX, int lowZ, boolean alongX, StructureBoundingBox low, StructureBoundingBox high) {
        if (!BeardLinks.carriedUnderground()) { return false; }
        int seam = (alongX ? lowX + 1 : lowZ + 1) * grid * 16;
        int lowAlong = alongX ? (low.minX + low.maxX) / 2 : (low.minZ + low.maxZ) / 2;
        int highAlong = alongX ? (high.minX + high.maxX) / 2 : (high.minZ + high.maxZ) / 2;
        return Math.min(seam - lowAlong, highAlong - seam) < climbRoom() + BeardRails.width(true);
    }

    @Nullable private static StructureBoundingBox wellIn(World world, int grid, int cellX, int cellZ) {
        List<long[]> pins = ContentStructurePlacement.pins(ContentStructurePlacement.VILLAGES);
        if (pins != null) {
            long[] found = null;
            for (long[] pin : pins) {
                if (Math.floorDiv((int) pin[0] >> 4, grid) != cellX || Math.floorDiv((int) pin[1] >> 4, grid) != cellZ) { continue; }
                if (found != null) {
                    ContentLog.LOGGER.debug("Village cell {}, {} holds more than one pinned village, so none of them is linked by rail", cellX, cellZ);
                    return null;
                }
                found = pin;
            }
            if (found == null) { return null; }
            int x = (int) found[0] - 2;
            int z = (int) found[1] - 2;
            return new StructureBoundingBox(x, 64, z, x + 5, 78, z + 5);
        }
        long site = BeardSite.siteFor(world, ContentSites.of(world, grid), cellX, cellZ, grid);
        if (site == ContentBeard.NO_SITE) { return null; }
        int chunkX = Longs.high(site);
        int chunkZ = Longs.low(site);
        if (!ContentStructurePlacement.allows(ContentStructurePlacement.VILLAGES, world, chunkX, chunkZ) || BeardSite.mansionCandidateNear(world, chunkX, chunkZ)) { return null; }
        return new StructureBoundingBox((chunkX << 4) + 2, 64, (chunkZ << 4) + 2, (chunkX << 4) + 7, 78, (chunkZ << 4) + 7);
    }

    @Nullable private static Link candidate(World world, int grid, int lowX, int lowZ, boolean alongX) {
        Map<List<Integer>, Link> held = LINKS.computeIfAbsent(world, key -> new HashMap<>());
        List<Integer> key = Arrays.asList(lowX, lowZ, alongX ? 1 : 0);
        if (held.containsKey(key)) { return held.get(key); }
        Link made = weighed(world, grid, lowX, lowZ, alongX);
        held.put(key, made);
        return made;
    }

    private static int spurRoom(boolean sub) { return Math.max(BeardRails.clear(sub), ContentBeard.plazaReach() + BeardRoads.pathFullWidth()); }

    private static int climbRoom() { return spurRoom(true) + BeardLinks.pinRows(false) + 2 * BeardLinks.ramp() + BeardRails.LEAST_OPEN; }

    @Nullable private static Link weighed(World world, int grid, int lowX, int lowZ, boolean alongX) {
        if (BeardSurface.unreadable(world)) { return null; }
        Node low = node(world, grid, lowX, lowZ);
        Node high = node(world, grid, lowX + (alongX ? 1 : 0), lowZ + (alongX ? 0 : 1));
        if (low == null || high == null || low.alongX != alongX || high.alongX != alongX) { return null; }
        String named = "The railway link between the villages of cells " + lowX + ", " + lowZ + " and " + (lowX + (alongX ? 1 : 0)) + ", " + (lowZ + (alongX ? 0 : 1));
        int apart = ContentStructurePlacement.separation(ContentStructurePlacement.VILLAGES, 0);
        if (apart > 0) {
            int dx = ((low.well.minX + 2) >> 4) - ((high.well.minX + 2) >> 4);
            int dz = ((low.well.minZ + 2) >> 4) - ((high.well.minZ + 2) >> 4);
            if (dx * dx + dz * dz < apart * apart) { return refused(named, "the two villages stand closer than structureSeparation allows, so only one of them founds"); }
        }
        boolean sub = low.sub;
        int span = grid * 16;
        int seam = (alongX ? lowX + 1 : lowZ + 1) * span;
        int lowSpur = seam - low.wellAlong;
        int highSpur = high.wellAlong - seam;
        boolean buried = buried(grid, lowX, lowZ, alongX, low.well, high.well);
        int segLeast = (alongX ? lowZ : lowX) * span;
        int segMost = segLeast + span - 1;
        int half = (BeardRails.width(buried) - 1) / 2;
        int reach = (BeardRails.width(sub) - 1) / 2 + 1;
        int margin = half + BeardRails.MARGIN + reach + BeardLinks.platformReach(buried);
        int rowLeast = Math.min(low.center, high.center);
        int rowMost = Math.max(low.center, high.center);
        if (rowLeast - margin < segLeast || rowMost + margin > segMost) { return refused(named, "a join would stand within " + margin + " block(s) of a corner of the cells"); }
        boolean straight = rowMost == rowLeast;
        if (straight && BeardRails.trackRows(buried, 0).length < 2) { return refused(named, "its spurs meet head on and a single-track trunk cannot take a crossroad"); }
        if (!straight && rowMost - rowLeast < crowd(buried)) { return refused(named, "its two joins, " + (rowMost - rowLeast) + " block(s) apart on the trunk, would crowd each other"); }
        int overhang = straight ? Math.max(reach, BeardRails.width(buried) + 1) : reach;
        int length = lowSpur + (rowMost - rowLeast) + highSpur;
        if (length < least() || length > most()) { return refused(named, "it is " + length + " block(s) long, outside " + least() + " to " + most()); }
        Trunk trunk = new Trunk(!alongX, seam, rowLeast - overhang, rowMost + overhang, sub, buried, new int[] { low.center, high.center }, new int[] { -1, 1 });
        Link link = new Link(low, high, trunk, length, seam);
        int room = spurRoom(sub) + BeardLinks.pinRows(buried) + (sub && !buried ? 2 * BeardLinks.ramp() + BeardRails.LEAST_OPEN : 0);
        if (link.spurEnd(true) - low.wellAlong < room || high.wellAlong - link.spurEnd(false) < room) { return refused(named, "a spur has less than the " + room + " block(s) it needs between its well and the trunk"); }
        String border = outside(world, link);
        if (border != null) { return refused(named, border); }
        int[][] graded = trunkProfile(world, trunk);
        if (graded == null) { return refused(named, "the trunk cannot be graded"); }
        String trunkRuns = runs(graded[0], graded[1], graded[2], buried, 0, graded[0].length - 1);
        if (trunkRuns != null) { return refused(named, "its trunk " + trunkRuns); }
        for (boolean lowSide : new boolean[] { true, false }) {
            String spurRuns = spurFits(world, link, lowSide);
            if (spurRuns != null) { return refused(named, "the spur of the village at " + link.node(lowSide).well.minX + ", " + link.node(lowSide).well.minZ + " " + spurRuns); }
        }
        String mansion = mansionOn(world, link);
        if (mansion != null) { return refused(named, mansion); }
        if (straight) { ContentLog.LOGGER.debug("{} runs {} block(s){}: a spur from each well, meeting head on and running straight across the seam at {} {} on row {}", named, length, buried ? " underground" : "", alongX ? "x" : "z", seam, low.center); }
        else { ContentLog.LOGGER.debug("{} runs {} block(s){}: a spur from each well to the seam at {} {} and a trunk of {} block(s) along it, meeting at {} and {}", named, length, buried ? " underground" : "", alongX ? "x" : "z", seam, rowMost - rowLeast, low.center, high.center); }
        return link;
    }

    @Nullable private static Link refused(String named, String why) {
        ContentLog.LOGGER.debug("{} is dropped whole: {}", named, why);
        return null;
    }

    @Nullable private static String outside(World world, Link link) {
        WorldBorder border = world.getWorldBorder();
        Trunk trunk = link.trunk;
        List<BlockPos> corners = new ArrayList<>();
        for (int row : new int[] { trunk.least, trunk.most }) {
            for (int across : new int[] { trunk.across - trunk.half(), trunk.across + trunk.half() }) { corners.add(trunk.alongX ? new BlockPos(row, 64, across) : new BlockPos(across, 64, row)); }
        }
        for (boolean lowSide : new boolean[] { true, false }) {
            Node node = link.node(lowSide);
            corners.add(node.alongX ? new BlockPos(link.spurEnd(lowSide), 64, node.center) : new BlockPos(node.center, 64, link.spurEnd(lowSide)));
        }
        for (BlockPos at : corners) {
            if (!border.contains(at)) { return "it would reach past the world border at " + at.getX() + ", " + at.getZ(); }
        }
        return null;
    }

    @Nullable private static String mansionOn(World world, Link link) {
        Set<Long> chunks = new HashSet<>();
        Trunk trunk = link.trunk;
        for (int row = trunk.least; row <= trunk.most + 15; row += 16) {
            int at = Math.min(row, trunk.most);
            chunks.add(trunk.alongX ? Longs.pack(at >> 4, trunk.across >> 4) : Longs.pack(trunk.across >> 4, at >> 4));
        }
        for (boolean lowSide : new boolean[] { true, false }) {
            Node node = link.node(lowSide);
            int from = Math.min(node.wellAlong, link.spurEnd(lowSide));
            int to = Math.max(node.wellAlong, link.spurEnd(lowSide));
            for (int row = from; row <= to + 15; row += 16) {
                int at = Math.min(row, to);
                chunks.add(node.alongX ? Longs.pack(at >> 4, node.center >> 4) : Longs.pack(node.center >> 4, at >> 4));
            }
        }
        for (long chunk : chunks) {
            if (BeardSite.mansionCandidateNear(world, Longs.high(chunk), Longs.low(chunk))) { return "a woodland mansion may stand in its way near chunk " + Longs.high(chunk) + ", " + Longs.low(chunk); }
        }
        return null;
    }

    @Nullable private static String spurFits(World world, Link link, boolean lowSide) {
        Node node = link.node(lowSide);
        if (link.trunk.buried) { return null; }
        int dir = lowSide ? 1 : -1;
        int end = link.spurEnd(lowSide);
        int from = node.wellAlong + dir * Math.max(BeardRails.clear(node.sub), ContentBeard.plazaReach());
        int rowLeast = Math.min(from, end);
        int rowMost = Math.max(from, end);
        int half = (BeardRails.width(node.sub) - 1) / 2;
        int[] ground = BeardGrade.noiseProfile(world, node.alongX, rowLeast, rowMost, node.center - half, node.center + half);
        int[][] graded = trunkProfile(world, link.trunk);
        if (ground == null || graded == null) { return "cannot be graded"; }
        int climb = BeardRails.climb(false);
        int[] profile = BeardRailsGrade.smoothed(BeardRailsGrade.filled(ground, world.getSeaLevel()), climb);
        boolean[] fixed = new boolean[profile.length];
        int level = graded[0][node.center - link.trunk.least];
        for (int k = 0; k < BeardLinks.pinRows(false); k++) {
            int i = end - dir * k - rowLeast;
            if (i < 0 || i >= profile.length) { continue; }
            profile[i] = level;
            fixed[i] = true;
        }
        BeardRailsGrade.settle(profile, fixed, fixed, climb);
        int[] decked = decked(profile, ground, fixed, climb);
        if (!node.sub) { return runs(profile, ground, decked, false, 0, profile.length - 1); }
        int open = BeardLinks.pinRows(false) + BeardLinks.ramp();
        int first = lowSide ? Math.max(0, profile.length - open) : 0;
        int last = lowSide ? profile.length - 1 : Math.min(profile.length - 1, open);
        return runs(profile, ground, decked, true, first, last);
    }

    @Nullable private static String runs(int[] profile, int[] ground, int[] decked, boolean bridgesOnly, int first, int last) {
        int depth = BeardRails.tunnelDepth(false);
        int bridge = 0;
        int bore = 0;
        for (int at = first; at <= last; at++) {
            bridge = decked[at] != 0 ? bridge + 1 : 0;
            boolean bored = !bridgesOnly && decked[at] == 0 && depth > 0 && ground[at] != Integer.MIN_VALUE && ground[at] >= profile[at] + depth;
            bore = bored ? bore + 1 : 0;
            if (bridge > bridgeMost()) { return "would need a bridge longer than " + bridgeMost() + " block(s)"; }
            if (bore > tunnelMost()) { return "would need a tunnel longer than " + tunnelMost() + " block(s)"; }
        }
        return null;
    }

    private static int[] decked(int[] profile, int[] ground, boolean[] keep, int climb) {
        boolean[] bridged = new boolean[profile.length];
        for (int at = 0; at < profile.length; at++) { bridged[at] = ground[at] == Integer.MIN_VALUE || profile[at] > ground[at] + BeardRails.FILL; }
        BeardGrade.levelDecks(profile, bridged, keep, climb);
        int[] marked = new int[profile.length];
        for (int at = 0; at < profile.length; at++) { marked[at] = bridged[at] ? 1 : 0; }
        return marked;
    }

    @Nullable static int[][] trunkProfile(World world, Trunk trunk) {
        Map<Trunk, int[][]> held = TRUNKS.computeIfAbsent(world, key -> new HashMap<>());
        if (held.containsKey(trunk)) { return held.get(trunk); }
        int[][] made = graded(world, trunk);
        held.put(trunk, made);
        return made;
    }

    @Nullable private static int[][] graded(World world, Trunk trunk) {
        int half = trunk.half();
        int[] ground = BeardGrade.noiseProfile(world, trunk.alongX, trunk.least, trunk.most, trunk.across - half, trunk.across + half);
        if (ground == null) { return null; }
        int climb = BeardRails.climb(trunk.buried);
        double[] base = BeardRailsGrade.filled(ground, world.getSeaLevel());
        if (trunk.buried) {
            double floor = GenHeights.floor(world, 6);
            for (int at = 0; at < base.length; at++) { base[at] = Math.max(floor, base[at] - BeardRails.subwayDepth()); }
        }
        int[] profile = trunk.buried ? BeardRailsGrade.sunken(base, climb) : BeardRailsGrade.smoothed(base, climb);
        boolean[] fixed = new boolean[profile.length];
        if (!trunk.buried) { decked(profile, ground, fixed, climb); }
        int reach = trunk.reach() + BeardLinks.APPROACH;
        for (int join : trunk.rows) {
            int at = join - trunk.least;
            int level = profile[at];
            for (int row = Math.max(0, at - reach); row <= Math.min(profile.length - 1, at + reach); row++) {
                profile[row] = level;
                fixed[row] = true;
            }
        }
        BeardRailsGrade.approach(profile, fixed, fixed, climb);
        BeardRailsGrade.settle(profile, fixed, fixed, climb);
        int[] bridged = trunk.buried ? new int[profile.length] : decked(profile, ground, fixed, climb);
        return new int[][] { profile, ground, bridged };
    }

    @Nullable static Link built(World world, int grid, int lowX, int lowZ, boolean alongX) {
        Link link = candidate(world, grid, lowX, lowZ, alongX);
        if (link == null) { return null; }
        if (link.low.sub && climbsToward(world, grid, link.low) <= 0) { return null; }
        if (link.high.sub && climbsToward(world, grid, link.high) >= 0) { return null; }
        return link;
    }

    private static int climbsToward(World world, int grid, Node node) {
        Link up = candidate(world, grid, node.cellX, node.cellZ, node.alongX);
        Link down = candidate(world, grid, node.cellX - (node.alongX ? 1 : 0), node.cellZ - (node.alongX ? 0 : 1), node.alongX);
        if (up == null) { return down == null ? 0 : -1; }
        if (down == null) { return 1; }
        return down.length < up.length ? -1 : 1;
    }
}
