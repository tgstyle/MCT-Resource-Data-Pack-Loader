package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.content.worldgen.CityLinks.Trunk;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nullable;

final class CityLinksSurvey {
    private static final Map<Long, Optional<Node>> NODES = new ConcurrentHashMap<>();
    private static final Map<List<Long>, Decision> DECISIONS = new ConcurrentHashMap<>();

    private CityLinksSurvey() {}

    public enum Refusal {
        SEPARATION("the two cities stand too close for both to found"),
        CORNER("a join would stand too near a corner of the cells"),
        HEAD_ON("its spurs meet head on over a single-track trunk"),
        CROWDED("its two joins would crowd each other"),
        LENGTH("it is longer or shorter than the link bounds allow"),
        SPUR_ROOM("a spur has no room between its well and the trunk"),
        BORDER("it would reach past the world border"),
        UNGRADED("the trunk cannot be graded"),
        BRIDGE("it would need too long a bridge"),
        TUNNEL("it would need too long a tunnel"),
        MANSION("a woodland mansion may stand in its way"),
        ONE_SIDE("a city carried underground links on its shorter side only");

        private final String brief;

        Refusal(String brief) { this.brief = brief; }

        public String brief() { return brief; }
    }

    public record Node(CityPlan.Town town, boolean alongX, int center, boolean sub) {
        public int wellAlong() { return town.wellAlong(alongX); }
    }

    public record Link(Node low, Node high, Trunk trunk, int length, int seam) {
        public Node node(boolean lowSide) { return lowSide ? low : high; }

        public boolean lowSide(CityPlan.Town town) { return low.town().key() == town.key(); }

        public int spurEnd(boolean lowSide) { return lowSide ? seam - trunk.half() - 1 : seam + trunk.half() + 1; }
    }

    public record Decision(@Nullable Link link, @Nullable Refusal why) {}

    record Graded(int[] profile, int[] floor, boolean[] bridged) {}

    private static final Decision ABSENT = new Decision(null, null);

    static void forget() {
        NODES.clear();
        DECISIONS.clear();
    }

    private static int least() { return Math.max(0, ContentControl.number(ContentControl.VILLAGES, "villageRailLinkLeast", Config.worldgen.villageRailLinkLeast())); }

    private static int most() { return Math.max(0, ContentControl.number(ContentControl.VILLAGES, "villageRailLinkMost", Config.worldgen.villageRailLinkMost())); }

    private static int bridgeMost() { return Math.max(0, ContentControl.number(ContentControl.VILLAGES, "villageRailLinkBridgeMost", Config.worldgen.villageRailLinkBridgeMost())); }

    private static int tunnelMost() { return Math.max(0, ContentControl.number(ContentControl.VILLAGES, "villageRailLinkTunnelMost", Config.worldgen.villageRailLinkTunnelMost())); }

    private static int crowd(boolean buried) { return 2 * CityPlan.railWidth(buried) + 2 * ((CityPlan.railWidth(CityLinks.carriedUnderground()) - 1) / 2 + 1); }

    private static int spurRoom(boolean sub) { return Math.max(CityRails.clear(sub), CityPlan.plazaReach() + CityPlan.streetFullWidth()); }

    private static int climbRoom() { return spurRoom(true) + CityLinks.pinRows(false) + 2 * CityLinks.ramp() + CityRails.LEAST_OPEN; }

    private static int span() { return CityPlan.spacing() * CityPlan.district(); }

    private static int edge(int cell, boolean alongX) { return CityPlan.windowOf(cell * CityPlan.spacing(), alongX); }

    @Nullable static Node node(CityGround ground, int cellX, int cellZ) {
        long key = CityPlan.packed(cellX, cellZ);
        Optional<Node> held = NODES.get(key);
        if (held != null) { return held.orElse(null); }
        Node made = made(ground, cellX, cellZ);
        NODES.putIfAbsent(key, Optional.ofNullable(made));
        return made;
    }

    @Nullable private static Node made(CityGround ground, int cellX, int cellZ) {
        CityPlan.Rail rolled = rolled(ground, cellX, cellZ);
        if (rolled == null) { return null; }
        CityPlan.Town town = townIn(ground, cellX, cellZ);
        if (town == null) { return null; }
        boolean alongX = rolled.alongX();
        int lowX = cellX - (alongX ? 1 : 0);
        int lowZ = cellZ - (alongX ? 0 : 1);
        CityPlan.Rail low = rolled(ground, lowX, lowZ);
        int center = rolled.middle();
        if (low != null && low.alongX() == alongX && crowds(ground, lowX, lowZ, alongX, low.middle(), center)) {
            CityPlan.Rail lower = rolled(ground, lowX - (alongX ? 1 : 0), lowZ - (alongX ? 0 : 1));
            boolean lowMoves = lower != null && lower.alongX() == alongX && crowds(ground, lowX - (alongX ? 1 : 0), lowZ - (alongX ? 0 : 1), alongX, lower.middle(), low.middle());
            if (!lowMoves && low.middle() - town.wellAcross(alongX) >= CityRails.clear(CityLinks.carriedUnderground()) && clearsWell(town, alongX, low.middle())) {
                ContentLog.LOGGER.debug("The first line of the city at {}, {} moves from {} to {} to meet its neighbor's line head on and cross the seam straight",town.wellX(), town.wellZ(), center, low.middle());
                center = low.middle();
            }
        }
        return new Node(town, alongX, center, CityLinks.carriedUnderground());
    }

    @Nullable private static CityPlan.Town townIn(CityGround ground, int cellX, int cellZ) {
        if (CityDistricts.pinsIn(cellX, cellZ) > 1) {
            ContentLog.LOGGER.debug("City cell {}, {} holds more than one pinned city, so none of them is linked by rail", cellX, cellZ);
            return null;
        }
        return CityPlanTowns.townIn(ground, cellX, cellZ);
    }

    @Nullable private static CityPlan.Rail rolled(CityGround ground, int cellX, int cellZ) {
        CityPlan.Town town = townIn(ground, cellX, cellZ);
        return town == null ? null : CityLinks.firstLine(ground, town);
    }

    private static boolean clearsWell(CityPlan.Town town, boolean alongX, int center) {
        if (!CityLinks.carriedUnderground()) { return true; }
        int reach = (CityPlan.railWidth(true) - 1) / 2 + ContentCity.stationReach() + 1 + CityLinks.platformReach(false);
        return Math.abs(center - town.wellAcross(alongX)) > reach + CityPlan.WELL_HALF;
    }

    private static boolean crowds(CityGround ground, int lowX, int lowZ, boolean alongX, int lowCenter, int highCenter) {
        int gap = Math.abs(lowCenter - highCenter);
        if (gap == 0) { return false; }
        CityPlan.Town low = townIn(ground, lowX, lowZ);
        CityPlan.Town high = townIn(ground, lowX + (alongX ? 1 : 0), lowZ + (alongX ? 0 : 1));
        if (low == null || high == null) { return false; }
        boolean buried = buried(alongX, lowX, lowZ, low, high);
        return ContentCity.railTrackCount(buried) > 1 && gap < crowd(buried);
    }

    private static boolean buried(boolean alongX, int lowX, int lowZ, CityPlan.Town low, CityPlan.Town high) {
        if (!CityLinks.carriedUnderground()) { return false; }
        int seam = edge((alongX ? lowX : lowZ) + 1, alongX);
        return Math.min(seam - low.wellAlong(alongX), high.wellAlong(alongX) - seam) < climbRoom() + CityPlan.railWidth(true);
    }

    static Decision built(CityGround ground, int lowX, int lowZ, boolean alongX) {
        Decision decision = candidate(ground, lowX, lowZ, alongX);
        Link link = decision.link();
        if (link == null) { return decision; }
        if (link.low().sub() && climbsToward(ground, link.low()) <= 0) { return new Decision(null, Refusal.ONE_SIDE); }
        if (link.high().sub() && climbsToward(ground, link.high()) >= 0) { return new Decision(null, Refusal.ONE_SIDE); }
        return decision;
    }

    private static int climbsToward(CityGround ground, Node node) {
        int cellX = CityLinks.cellOf(node.town().wellX(), true);
        int cellZ = CityLinks.cellOf(node.town().wellZ(), false);
        Link up = candidate(ground, cellX, cellZ, node.alongX()).link();
        Link down = candidate(ground, cellX - (node.alongX() ? 1 : 0), cellZ - (node.alongX() ? 0 : 1), node.alongX()).link();
        if (up == null) { return down == null ? 0 : -1; }
        if (down == null) { return 1; }
        return down.length() < up.length() ? -1 : 1;
    }

    private static Decision candidate(CityGround ground, int lowX, int lowZ, boolean alongX) {
        List<Long> key = List.of(CityPlan.packed(lowX, lowZ), alongX ? 1L : 0L);
        Decision held = DECISIONS.get(key);
        if (held != null) { return held; }
        Decision made = weighed(ground, lowX, lowZ, alongX);
        DECISIONS.putIfAbsent(key, made);
        return made;
    }

    private static Decision weighed(CityGround ground, int lowX, int lowZ, boolean alongX) {
        Node low = node(ground, lowX, lowZ);
        Node high = node(ground, lowX + (alongX ? 1 : 0), lowZ + (alongX ? 0 : 1));
        if (low == null || high == null || low.alongX() != alongX || high.alongX() != alongX) { return ABSENT; }
        String named = "The railway link between the cities of cells " + lowX + ", " + lowZ + " and " + (lowX + (alongX ? 1 : 0)) + ", " + (lowZ + (alongX ? 0 : 1));
        int apart = CityDistricts.separation();
        if (apart > 0) {
            int dx = Math.floorDiv(low.town().wellX(), 16) - Math.floorDiv(high.town().wellX(), 16);
            int dz = Math.floorDiv(low.town().wellZ(), 16) - Math.floorDiv(high.town().wellZ(), 16);
            if (dx * dx + dz * dz < apart * apart) { return refused(named, Refusal.SEPARATION, "the two cities stand closer than structureSeparation allows, so only one of them founds"); }
        }
        boolean sub = low.sub();
        int seam = edge((alongX ? lowX : lowZ) + 1, alongX);
        int lowSpur = seam - low.wellAlong();
        int highSpur = high.wellAlong() - seam;
        boolean buried = buried(alongX, lowX, lowZ, low.town(), high.town());
        int segLeast = edge(alongX ? lowZ : lowX, !alongX);
        int segMost = segLeast + span() - 1;
        int half = (CityPlan.railWidth(buried) - 1) / 2;
        int reach = (CityPlan.railWidth(sub) - 1) / 2 + 1;
        int margin = half + CityRails.MARGIN + reach + CityLinks.platformReach(buried);
        int rowLeast = Math.min(low.center(), high.center());
        int rowMost = Math.max(low.center(), high.center());
        if (rowLeast - margin < segLeast || rowMost + margin > segMost) { return refused(named, Refusal.CORNER, "a join would stand within " + margin + " block(s) of a corner of the cells"); }
        boolean crossroad = rowMost == rowLeast;
        if (crossroad && ContentCity.railTrackCount(buried) < 2) { return refused(named, Refusal.HEAD_ON, "its spurs meet head on and a single-track trunk cannot take a crossroad"); }
        if (!crossroad && rowMost - rowLeast < crowd(buried)) { return refused(named, Refusal.CROWDED, "its two joins, " + (rowMost - rowLeast) + " block(s) apart on the trunk, would crowd each other"); }
        int overhang = crossroad ? Math.max(reach, CityPlan.railWidth(buried) + 1) : reach;
        int length = lowSpur + (rowMost - rowLeast) + highSpur;
        if (length < least() || length > most()) { return refused(named, Refusal.LENGTH, "it is " + length + " block(s) long, outside " + least() + " to " + most()); }
        Trunk trunk = new Trunk(!alongX, seam, rowLeast - overhang, rowMost + overhang, sub, buried, low.center(), -1, high.center(), 1);
        Link link = new Link(low, high, trunk, length, seam);
        int room = spurRoom(sub) + CityLinks.pinRows(buried) + (sub && !buried ? 2 * CityLinks.ramp() + CityRails.LEAST_OPEN : 0);
        if (link.spurEnd(true) - low.wellAlong() < room || high.wellAlong() - link.spurEnd(false) < room) { return refused(named, Refusal.SPUR_ROOM, "a spur has less than the " + room + " block(s) it needs between its well and the trunk"); }
        int[] past = outside(link);
        if (past != null) { return refused(named, Refusal.BORDER, "it would reach past the world border at " + past[0] + ", " + past[1]); }
        Graded graded = graded(ground, trunk);
        if (graded == null) { return refused(named, Refusal.UNGRADED, "the trunk cannot be graded"); }
        Refusal trunkRuns = runs(graded, buried, 0, graded.profile().length - 1);
        if (trunkRuns != null) { return refused(named, trunkRuns, "its trunk would need " + longer(trunkRuns)); }
        for (boolean lowSide : new boolean[] {true, false}) {
            Refusal spurRuns = spurFits(ground, link, graded, lowSide);
            if (spurRuns != null) { return refused(named, spurRuns, "the spur of the city at " + link.node(lowSide).town().wellX() + ", " + link.node(lowSide).town().wellZ() + " would need " + longer(spurRuns)); }
        }
        int[] mansion = mansionOn(ground, link);
        if (mansion != null) { return refused(named, Refusal.MANSION, "a woodland mansion may stand in its way near chunk " + mansion[0] + ", " + mansion[1]); }
        if (crossroad) { ContentLog.LOGGER.debug("{} runs {} block(s){}: a spur from each well, meeting head on and running straight across the seam at {} {} on row {}", named, length, buried ? " underground" : "", alongX ? "x" : "z", seam, low.center()); }
        else { ContentLog.LOGGER.debug("{} runs {} block(s){}: a spur from each well to the seam at {} {} and a trunk of {} block(s) along it, meeting at {} and {}", named, length, buried ? " underground" : "", alongX ? "x" : "z", seam, rowMost - rowLeast, low.center(), high.center()); }
        return new Decision(link, null);
    }

    private static String longer(Refusal why) { return why == Refusal.BRIDGE ? "a bridge longer than " + bridgeMost() + " block(s)" : "a tunnel longer than " + tunnelMost() + " block(s)"; }

    private static Decision refused(String named, Refusal why, String detail) {
        ContentLog.LOGGER.debug("{} is dropped whole: {}", named, detail);
        return new Decision(null, why);
    }

    @Nullable private static int[] outside(Link link) {
        boolean live = CityBorder.live();
        int border = ContentTerrain.worldBorder();
        if (!live && border <= 0) { return null; }
        int edge = border / 2;
        Trunk trunk = link.trunk();
        List<int[]> corners = new ArrayList<>();
        for (int row : new int[] {trunk.least(), trunk.most()}) {
            for (int across : new int[] {trunk.across() - trunk.half(), trunk.across() + trunk.half()}) { corners.add(trunk.alongX() ? new int[] {row, across} : new int[] {across, row}); }
        }
        for (boolean lowSide : new boolean[] {true, false}) {
            Node node = link.node(lowSide);
            corners.add(node.alongX() ? new int[] {link.spurEnd(lowSide), node.center()} : new int[] {node.center(), link.spurEnd(lowSide)});
        }
        for (int[] at : corners) {
            if (live ? CityBorder.beyond(at[0], at[1]) : Math.abs(at[0]) > edge || Math.abs(at[1]) > edge) { return at; }

        }
        return null;
    }

    @Nullable private static int[] mansionOn(CityGround ground, Link link) {
        List<int[]> chunks = new ArrayList<>();
        Trunk trunk = link.trunk();
        for (int row = trunk.least(); row <= trunk.most() + 15; row += 16) {
            int at = Math.min(row, trunk.most());
            chunks.add(trunk.alongX() ? new int[] {at >> 4, trunk.across() >> 4} : new int[] {trunk.across() >> 4, at >> 4});
        }
        for (boolean lowSide : new boolean[] {true, false}) {
            Node node = link.node(lowSide);
            int from = Math.min(node.wellAlong(), link.spurEnd(lowSide));
            int to = Math.max(node.wellAlong(), link.spurEnd(lowSide));
            for (int row = from; row <= to + 15; row += 16) {
                int at = Math.min(row, to);
                chunks.add(node.alongX() ? new int[] {at >> 4, node.center() >> 4} : new int[] {node.center() >> 4, at >> 4});
            }
        }
        for (int[] chunk : chunks) {
            if (ground.mansionNear(chunk[0], chunk[1])) { return chunk; }
        }
        return null;
    }

    @Nullable private static Refusal spurFits(CityGround ground, Link link, Graded trunk, boolean lowSide) {
        if (link.trunk().buried()) { return null; }
        Node node = link.node(lowSide);
        int dir = lowSide ? 1 : -1;
        int end = link.spurEnd(lowSide);
        int from = node.wellAlong() + dir * Math.max(CityRails.clear(node.sub()), CityPlan.plazaReach());
        int rowLeast = Math.min(from, end);
        int rowMost = Math.max(from, end);
        int half = (CityPlan.railWidth(node.sub()) - 1) / 2;
        int[] floor = CityRails.floorRows(ground, node.alongX(), rowLeast, rowMost, node.center() - half, node.center() + half);
        int climb = ContentCity.railClimb(false);
        int[] profile = CityGrade.railLine(floor, ground.sea(), climb);
        boolean[] fixed = new boolean[profile.length];
        int level = trunk.profile()[node.center() - link.trunk().least()];
        for (int k = 0; k < CityLinks.pinRows(false); k++) {
            int at = end - dir * k - rowLeast;
            if (at < 0 || at >= profile.length) { continue; }
            profile[at] = level;
            fixed[at] = true;
        }
        CityGrade.spaceSteps(profile, fixed, fixed, climb);
        Graded graded = new Graded(profile, floor, decked(profile, floor, fixed, climb));
        if (!node.sub()) { return runs(graded, false, 0, profile.length - 1); }
        int open = CityLinks.pinRows(false) + CityLinks.ramp();
        int first = lowSide ? Math.max(0, profile.length - open) : 0;
        int last = lowSide ? profile.length - 1 : Math.min(profile.length - 1, open);
        return runs(graded, true, first, last);
    }

    @Nullable private static Refusal runs(Graded graded, boolean bridgesOnly, int first, int last) {
        int depth = ContentCity.railTunnelDepth();
        int bridge = 0;
        int bore = 0;
        for (int at = first; at <= last; at++) {
            bridge = graded.bridged()[at] ? bridge + 1 : 0;
            boolean bored = !bridgesOnly && !graded.bridged()[at] && depth > 0 && graded.floor()[at] != Integer.MIN_VALUE && graded.floor()[at] >= graded.profile()[at] + depth;
            bore = bored ? bore + 1 : 0;
            if (bridge > bridgeMost()) { return Refusal.BRIDGE; }
            if (bore > tunnelMost()) { return Refusal.TUNNEL; }
        }
        return null;
    }

    private static boolean[] bridgedRows(int[] profile, int[] floor) {
        boolean[] bridged = new boolean[profile.length];
        for (int at = 0; at < profile.length; at++) { bridged[at] = floor[at] == Integer.MIN_VALUE || profile[at] > floor[at] + CityRails.FILL; }
        return bridged;
    }

    private static boolean[] decked(int[] profile, int[] floor, boolean[] keep, int climb) {
        boolean[] bridged = bridgedRows(profile, floor);
        CityGrade.levelDecks(profile, bridged, keep, climb);
        return bridged;
    }

    private static void deck(int[] profile, int[] floor, boolean[] keep, int climb) { CityGrade.levelDecks(profile, bridgedRows(profile, floor), keep, climb); }

    @Nullable static Graded graded(CityGround ground, Trunk trunk) {
        int half = trunk.half();
        int[] floor = CityRails.floorRows(ground, trunk.alongX(), trunk.least(), trunk.most(), trunk.across() - half, trunk.across() + half);
        int climb = ContentCity.railClimb(trunk.buried());
        boolean[] lowered = new boolean[floor.length];
        if (trunk.buried()) { Arrays.fill(lowered, true); }
        double[] base = CityGrade.railBase(floor, ground.sea(), lowered, ContentCity.subwayDepth(), ground.bottom() + ContentCitySewerPiece.FLOOR_LEAST);
        int[] profile = trunk.buried() ? CityGrade.sunken(base, climb) : CityGrade.smoothed(base, climb);
        boolean[] fixed = new boolean[profile.length];
        if (!trunk.buried()) { deck(profile, floor, fixed, climb); }
        int reach = trunk.reach() + CityLinks.APPROACH;
        for (boolean low : new boolean[] {true, false}) {
            int at = trunk.row(low) - trunk.least();
            if (at < 0 || at >= profile.length) { return null; }
            int level = profile[at];
            for (int row = Math.max(0, at - reach); row <= Math.min(profile.length - 1, at + reach); row++) {
                profile[row] = level;
                fixed[row] = true;
            }
        }
        CityGrade.approach(profile, fixed, fixed, climb);
        CityGrade.spaceSteps(profile, fixed, fixed, climb);
        if (trunk.buried()) { return new Graded(profile, floor, new boolean[profile.length]); }
        return new Graded(profile, floor, decked(profile, floor, fixed, climb));
    }
}
