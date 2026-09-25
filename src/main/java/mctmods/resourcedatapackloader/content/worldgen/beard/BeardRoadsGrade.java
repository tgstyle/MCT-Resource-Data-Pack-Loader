package mctmods.resourcedatapackloader.content.worldgen.beard;

import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.content.village.CityGrowth;
import mctmods.resourcedatapackloader.content.village.CityLayout;
import mctmods.resourcedatapackloader.content.worldgen.ContentBeard;
import mctmods.resourcedatapackloader.content.worldgen.beard.interfaces.IRoadLayout;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.MathUtil;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import java.util.List;
import javax.annotation.Nullable;

public final class BeardRoadsGrade {
    private BeardRoadsGrade() {}

    @Nullable public static BeardRoads.Grade roadProfile(World world, @Nullable StructureComponent piece, boolean alongX, int rowLeast, int rowMost, int acrossLeast, int acrossMost, boolean junctions) {
        return roadProfile(world, piece, piece == null ? null : piece.getCoordBaseMode(), alongX, rowLeast, rowMost, acrossLeast, acrossMost, junctions);
    }

    static boolean farHigh(@Nullable EnumFacing facing, boolean alongX) { return facing != null && (alongX ? facing.getXOffset() : facing.getZOffset()) > 0; }

    static boolean farLow(@Nullable EnumFacing facing, boolean alongX) { return facing != null && (alongX ? facing.getXOffset() : facing.getZOffset()) < 0; }

    @Nullable public static BeardRoads.Grade roadProfile(World world, @Nullable StructureComponent piece, @Nullable EnumFacing facing, boolean alongX, int rowLeast, int rowMost, int acrossLeast, int acrossMost, boolean junctions) {
        int[] profile = BeardGrade.noiseProfile(world, alongX, rowLeast, rowMost, acrossLeast, acrossMost);
        if (profile == null) { return null; }
        int[] ground = profile.clone();
        BeardGrade.flatRuns(world, alongX, rowLeast, acrossLeast, acrossMost, profile);
        boolean[] bridged = BeardGrade.smooth(profile);
        boolean[] pinned = new boolean[profile.length];
        boolean[] plaza = new boolean[profile.length];
        boolean[] footed = new boolean[profile.length];
        boolean[] square = new boolean[profile.length];
        boolean[] decks = new boolean[profile.length];
        int capped;
        if (junctions) {
            BeardRoadsHolds.roadApron(world, piece, alongX, rowLeast, rowMost, acrossLeast, acrossMost, profile, ground, pinned, footed, plaza, bridged, square, decks);
            boolean[] aproned = plaza.clone();
            BeardRoadsHolds.clampToWell(world, alongX, rowLeast, acrossLeast, acrossMost, profile, plaza);
            int railed = BeardRails.hold(world, piece, alongX, rowLeast, acrossLeast, acrossMost, profile, plaza);
            if (railed > 0 && ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("Held {} row(s) of the road at {}, {} to the level of the railway line it crosses", railed, alongX ? rowLeast : acrossLeast, alongX ? acrossLeast : rowLeast); }
            for (int i = 0; i < pinned.length; i++) { if (plaza[i]) { pinned[i] = true; } }
            BeardRoadsHolds.frontHold(piece, alongX, rowLeast, acrossLeast, acrossMost, profile, pinned);
            boolean[] keep = new boolean[profile.length];
            for (int i = 0; i < keep.length; i++) { keep[i] = square[i] || footed[i] || (plaza[i] && !aproned[i]); }
            int ramped = BeardGrade.ramp(profile, pinned, keep);
            if (ramped > 0 && ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("The road at {}, {} had held rows meeting with a step of more than one block, so {} row(s) beside the step(s) are let go to ramp between the levels", alongX ? rowLeast : acrossLeast, alongX ? acrossLeast : rowLeast, ramped); }
            BeardGrade.settle(profile, pinned);
            boolean[] bored = BeardGrade.bore(profile, ground, pinned, bridged, BeardRoadsTunnels.tunnelDepth(), farLow(facing, alongX), farHigh(facing, alongX));
            if (ContentLog.LOGGER.debugEnabled()) {
                int level = 0;
                for (boolean row : bored) { if (row) { level++; } }
                if (level > 0) { ContentLog.LOGGER.debug("Held {} row(s) of the road at {}, {} level through a hill, to be bored where the ground over them is deep enough", level, alongX ? rowLeast : acrossLeast, alongX ? acrossLeast : rowLeast); }
            }
            boolean[] fixed = plaza.clone();
            boolean[] authority = new boolean[profile.length];
            for (int i = 0; i < authority.length; i++) { authority[i] = plaza[i] && !square[i]; }
            capped = 0;
            for (int pass = 0; pass < 4; pass++) {
                int clamped = BeardGrade.capEmbankment(profile, ground, bridged, fixed);
                capped += clamped;
                if (clamped == 0) { break; }
                boolean[] hold = new boolean[profile.length];
                for (int i = 0; i < hold.length; i++) { hold[i] = fixed[i] || bored[i] || (pinned[i] && profile[i] <= ground[i] + BeardGrade.CAP); }
                BeardGrade.settle(profile, hold);
            }
            boolean[] hold = new boolean[profile.length];
            for (int i = 0; i < hold.length; i++) { hold[i] = fixed[i] || bored[i] || (pinned[i] && (ground[i] == Integer.MIN_VALUE || (profile[i] <= ground[i] + BeardGrade.CAP && profile[i] >= ground[i] - BeardGrade.CAP))); }
            for (int i = 0; i < hold.length; i++) {
                if (hold[i] || bridged[i] || ground[i] == Integer.MIN_VALUE || profile[i] == Integer.MIN_VALUE) { continue; }
                int floor = ground[i];
                for (int near = Math.max(0, i - 2); near <= Math.min(ground.length - 1, i + 2); near++) {
                    if (ground[near] != Integer.MIN_VALUE && ground[near] < floor) { floor = ground[near]; }
                }
                if (profile[i] < floor - BeardGrade.CAP) { profile[i] = floor - BeardGrade.CAP; }
            }
            for (int round = 0; round < 4; round++) {
                rein(profile, hold, bridged);
                ramp(profile, bridged, plaza);
            }
            for (int i = 1; i < profile.length - 1; i++) {
                if (hold[i] || bridged[i] || profile[i] == Integer.MIN_VALUE) { continue; }
                if (profile[i - 1] == Integer.MIN_VALUE || profile[i + 1] == Integer.MIN_VALUE) { continue; }
                int flank = Math.max(profile[i - 1], profile[i + 1]);
                if (profile[i] > flank) { profile[i] = flank; }
            }
            for (int i = 0; i < footed.length; i++) { if (plaza[i] && !decks[i]) { footed[i] = true; } }
            int width = acrossMost - acrossLeast + 1;
            int lowRun = 0;
            while (lowRun < profile.length && profile[lowRun] == Integer.MIN_VALUE) { lowRun++; }
            if (lowRun >= width && attachedAt(piece, alongX, rowLeast - 1, acrossLeast, acrossMost)) {
                for (int i = 0; i < width; i++) { footed[i] = true; }
            }
            int highRun = 0;
            while (highRun < profile.length && profile[profile.length - 1 - highRun] == Integer.MIN_VALUE) { highRun++; }
            if (highRun >= width && attachedAt(piece, alongX, rowMost + 1, acrossLeast, acrossMost)) {
                for (int i = 0; i < width; i++) { footed[profile.length - 1 - i] = true; }
            }
            BeardGrade.sag(profile, bridged, world.getSeaLevel());
            int held = BeardGrade.holdCauseway(profile, bridged, world.getSeaLevel());
            if (held > 0) { ContentLog.LOGGER.debug("Held {} short land row(s) of the road at {}, {} up to the deck crossing them, so the causeway stays level over its shoals", held, alongX ? rowLeast : acrossLeast, alongX ? acrossLeast : rowLeast); }
            int piers = 0;
            for (int i = 0; i < profile.length; i++) {
                if (ground[i] == Integer.MIN_VALUE && profile[i] != Integer.MIN_VALUE && profile[i] < world.getSeaLevel()) {
                    profile[i] = world.getSeaLevel();
                    piers++;
                }
            }
            if (piers > 0) { ContentLog.LOGGER.debug("Held {} pier row(s) of the road at {}, {} up to the water line, so they meet the decks either side", piers, alongX ? rowLeast : acrossLeast, alongX ? acrossLeast : rowLeast); }
            int filled = piece == null ? 0 : BeardGrade.fillDips(profile, authority);
            if (filled > 0 && ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("Lifted {} row(s) of the road at {}, {} out of a dip, so it carries across at the level it meets on either side", filled, alongX ? rowLeast : acrossLeast, alongX ? acrossLeast : rowLeast); }
            boolean[] anchored = new boolean[profile.length];
            for (int i = 0; i < anchored.length; i++) { anchored[i] = plaza[i] && !aproned[i]; }
            int raised = piece == null ? 0 : CityLayout.lift(world, piece, alongX, rowLeast, acrossLeast, acrossMost, profile, bridged, anchored);
            if (raised > 0 && ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("Raised {} row(s) of the road at {}, {} onto the deck its city map draws there", raised, alongX ? rowLeast : acrossLeast, alongX ? acrossLeast : rowLeast); }
            int decked = BeardGrade.deckDrops(profile, ground, bridged, authority, Math.max(0, ContentControl.number(ContentControl.VILLAGES, "villagePathBridgeDrop", Config.worldgen.villagePathBridgeDrop)));
            if (decked > 0 && ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("Decked {} row(s) of the road at {}, {} where its grade stands clear of the ground, so the drop is bridged rather than filled", decked, alongX ? rowLeast : acrossLeast, alongX ? acrossLeast : rowLeast); }
            boolean[] unmoved = new boolean[profile.length];
            for (int i = 0; i < unmoved.length; i++) { unmoved[i] = (authority[i] && !(aproned[i] && bridged[i])) || (square[i] && !decks[i]); }
            int leveled = BeardGrade.levelDecks(profile, bridged, unmoved, 1);
            if (leveled > 0 && ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("Leveled {} row(s) of the road at {}, {} so each of its decks lies at one height end to end", leveled, alongX ? rowLeast : acrossLeast, alongX ? acrossLeast : rowLeast); }
            if (!BeardGrade.walkable(profile, bridged) || steppedOntoDeck(profile, bridged)) {
                boolean[] standing = new boolean[profile.length];
                for (int i = 0; i < standing.length; i++) { standing[i] = hold[i] || bridged[i]; }
                int freed = 0;
                for (int round = 0; round < 4; round++) {
                    int let = BeardGrade.rampSteps(profile, standing, keep);
                    if (let == 0) { break; }
                    freed += let;
                    BeardGrade.settle(profile, standing);
                    if (BeardGrade.walkable(profile, bridged) && !steppedOntoDeck(profile, bridged)) { break; }
                }
                if (freed > 0 && ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("The road at {}, {} met its held rows with a step of more than one block after every pin was known, so {} held row(s) beside the step(s) are let go to ramp between the levels, and it {}", alongX ? rowLeast : acrossLeast, alongX ? acrossLeast : rowLeast, freed, BeardGrade.walkable(profile, bridged) && !steppedOntoDeck(profile, bridged) ? "walks now" : "still steps"); }
            }
            for (int i = 0; i < square.length; i++) { if (square[i] && !decks[i]) { bridged[i] = false; } }
        }
        else { capped = BeardGrade.capEmbankment(profile, ground, bridged, plaza); }
        int trimmed = BeardGrade.groundDeckEnds(world, alongX, rowLeast, acrossLeast, acrossMost, profile, bridged);
        if (trimmed > 0 && ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("The road at {}, {} lets {} end row(s) of its decks go back to road, since most of each such row stands on the ground, so every deck left is a whole rectangle", alongX ? rowLeast : acrossLeast, alongX ? acrossLeast : rowLeast, trimmed); }
        return new BeardRoads.Grade(profile, ground, bridged, footed, rowLeast, capped);
    }

    private static boolean steppedOntoDeck(int[] profile, boolean[] bridged) {
        for (int i = 1; i < profile.length; i++) {
            if (profile[i] == Integer.MIN_VALUE || profile[i - 1] == Integer.MIN_VALUE || bridged[i] == bridged[i - 1]) { continue; }
            if (Math.abs(profile[i] - profile[i - 1]) > 1) { return true; }
        }
        return false;
    }

    private static boolean attachedAt(@Nullable StructureComponent piece, boolean alongX, int row, int acrossLeast, int acrossMost) {
        List<StructureComponent> pieces = ContentBeard.components();
        if (pieces == null) { return false; }
        for (StructureComponent other : pieces) {
            if (other == piece) { continue; }
            StructureBoundingBox box = other.getBoundingBox();
            if (row < (alongX ? box.minX : box.minZ) || row > (alongX ? box.maxX : box.maxZ)) { continue; }
            if ((alongX ? box.maxZ : box.maxX) >= acrossLeast && (alongX ? box.minZ : box.minX) <= acrossMost) { return true; }
        }
        return false;
    }

    private static void rein(int[] profile, boolean[] held, boolean[] bridged) {
        for (int i = 1; i < profile.length; i++) {
            if (held[i] || bridged[i] || bridged[i - 1] || profile[i] == Integer.MIN_VALUE || profile[i - 1] == Integer.MIN_VALUE) { continue; }
            if (profile[i] > profile[i - 1] + 1) { profile[i] = profile[i - 1] + 1; }
        }
        for (int i = profile.length - 2; i >= 0; i--) {
            if (held[i] || bridged[i] || bridged[i + 1] || profile[i] == Integer.MIN_VALUE || profile[i + 1] == Integer.MIN_VALUE) { continue; }
            if (profile[i] > profile[i + 1] + 1) { profile[i] = profile[i + 1] + 1; }
        }
    }

    private static void ramp(int[] profile, boolean[] bridged, boolean[] fixed) {
        for (int i = 1; i < profile.length; i++) {
            if (fixed[i] || bridged[i] || bridged[i - 1] || profile[i] == Integer.MIN_VALUE || profile[i - 1] == Integer.MIN_VALUE) { continue; }
            if (profile[i] < profile[i - 1] - 1) { profile[i] = profile[i - 1] - 1; }
        }
        for (int i = profile.length - 2; i >= 0; i--) {
            if (fixed[i] || bridged[i] || bridged[i + 1] || profile[i] == Integer.MIN_VALUE || profile[i + 1] == Integer.MIN_VALUE) { continue; }
            if (profile[i] < profile[i + 1] - 1) { profile[i] = profile[i + 1] - 1; }
        }
    }

    public static int roadReach(StructureBoundingBox box, EnumFacing facing) { return roadReach(box, facing, 0); }

    public static int roadReach(StructureBoundingBox box, EnumFacing facing, int extra) {
        World world = ContentBeard.samplerWorld;
        if (world == null || facing == null || ContentBeard.samplerFor(world) == null) {
            if (ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("The reach test for a road at {}, {} cannot run: world {}, facing {}, ContentBeard.sampler {}", box.minX, box.minZ, world == null ? "none" : "held", facing, world == null || ContentBeard.samplerFor(world) == null ? "none" : "held"); }
            return Integer.MAX_VALUE;
        }
        boolean alongX = facing.getAxis() == EnumFacing.Axis.X;
        int rows = (alongX ? box.maxX - box.minX : box.maxZ - box.minZ) + 1;
        int step = (alongX ? facing.getXOffset() : facing.getZOffset()) >= 0 ? 1 : -1;
        int from = step > 0 ? (alongX ? box.minX : box.minZ) : (alongX ? box.maxX : box.maxZ);
        int acrossLeast = alongX ? box.minZ : box.minX;
        int acrossMost = alongX ? box.maxZ : box.maxX;
        int depth = BeardRoadsTunnels.tunnelDepth();
        for (int length = rows; length >= 7; length -= 7) {
            BeardRoads.Grade grade = reachGrade(world, box, facing, alongX, from, step, length, acrossLeast, acrossMost);
            if (grade == null) {
                if (ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("The reach test for a road at {}, {} along {} has no profile at length {}, so its full {} rows stand", box.minX, box.minZ, alongX ? "x" : "z", length, rows); }
                return rows;
            }
            if (!BeardGrade.walkable(grade.profile, grade.bridged)) { continue; }
            int buried = depth > 0 ? deadEnd(grade, step > 0, depth) : 0;
            if (buried == 0) { return length; }
            for (int longer = length + 7; longer <= rows + extra; longer += 7) {
                BeardRoads.Grade through = reachGrade(world, box, facing, alongX, from, step, longer, acrossLeast, acrossMost);
                if (through == null) { break; }
                if (!BeardGrade.walkable(through.profile, through.bridged) || deadEnd(through, step > 0, depth) > 0) { continue; }
                if (ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("A road from {}, {} facing {} runs into a hill {} row(s) before its end at {} rows and comes out the other side at {} rows, so it is lengthened to bore through", box.minX, box.minZ, facing, buried, length, longer); }
                return longer;
            }
            int foot = length - buried;
            if (ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("A road from {}, {} facing {} runs into a hill {} row(s) before its end at {} rows and finds no other side within {} more row(s), so it stops at the foot after {} row(s)", box.minX, box.minZ, facing, buried, length, extra, foot); }
            if (foot >= 7) { return foot; }
        }
        return 0;
    }

    @Nullable private static BeardRoads.Grade reachGrade(World world, StructureBoundingBox box, EnumFacing facing, boolean alongX, int from, int step, int length, int acrossLeast, int acrossMost) {
        int far = from + step * (length - 1);
        int rowLeast = Math.min(from, far);
        int rowMost = Math.max(from, far);
        BeardRoads.Grade grade = roadProfile(world, null, facing, alongX, rowLeast, rowMost, acrossLeast, acrossMost, true);
        if (grade != null && ContentLog.LOGGER.debugEnabled()) {
            StringBuilder trace = new StringBuilder();
            for (int i = 0; i < grade.profile.length; i++) {
                trace.append(' ').append(rowLeast + i).append(':');
                trace.append(grade.ground[i] == Integer.MIN_VALUE ? "-" : String.valueOf(grade.ground[i])).append('/');
                trace.append(grade.profile[i] == Integer.MIN_VALUE ? "-" : String.valueOf(grade.profile[i]));
                if (grade.bridged[i]) { trace.append('b'); }
            }
            ContentLog.LOGGER.debug("The reach test for a road at {}, {} along {} at length {} is {}, capped {} row(s), as row:ground/graded:{}", box.minX, box.minZ, alongX ? "x" : "z", length, BeardGrade.walkable(grade.profile, grade.bridged) ? "walkable" : "too steep", grade.capped, trace);
        }
        return grade;
    }

    private static int deadEnd(BeardRoads.Grade grade, boolean farHigh, int depth) {
        int rows = grade.profile.length;
        int last = farHigh ? rows - 1 : 0;
        if (!grade.buriedAt(grade.start + last, depth)) { return 0; }
        int back = farHigh ? -1 : 1;
        int at = last;
        while (at + back >= 0 && at + back < rows) {
            int next = at + back;
            if (grade.profile[next] == Integer.MIN_VALUE || grade.ground[next] == Integer.MIN_VALUE || grade.ground[next] - grade.profile[next] <= BeardGrade.CAP) { break; }
            at = next;
        }
        return farHigh ? rows - at : at + 1;
    }

    @Nullable public static BeardRoads.Grade chainGrade(World world, StructureComponent road, boolean alongX) {
        BeardRoads.Grade laid = road instanceof IRoadLayout ? ((IRoadLayout) road).rdpl$layout() : null;
        if (ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("The grade of the road at {}, {} comes from {}", road.getBoundingBox().minX, road.getBoundingBox().minZ, laid != null ? "its built layout" : chaining ? "a plain profile" : "a profile with its own junctions"); }
        if (laid != null) { return laid; }
        StructureBoundingBox box = road.getBoundingBox();
        int rowLeast = alongX ? box.minX : box.minZ;
        int rowMost = alongX ? box.maxX : box.maxZ;
        int acrossLeast = alongX ? box.minZ : box.minX;
        int acrossMost = alongX ? box.maxZ : box.maxX;
        if (chaining) { return roadProfile(world, road, alongX, rowLeast, rowMost, acrossLeast, acrossMost, false); }
        chaining = true;
        try { return roadProfile(world, road, alongX, rowLeast, rowMost, acrossLeast, acrossMost, true); }
        finally { chaining = false; }
    }

    private static boolean chaining;

    public static int roadGradeBeside(World world, StructureBoundingBox box) {
        List<StructureComponent> pieces = ContentBeard.components();
        if (pieces == null) { return Integer.MIN_VALUE; }
        for (StructureComponent other : pieces) {
            if (!(other instanceof StructureVillagePieces.Path)) { continue; }
            StructureBoundingBox road = other.getBoundingBox();
            int gap = MathUtil.max(road.minX - box.maxX, box.minX - road.maxX, road.minZ - box.maxZ, box.minZ - road.maxZ);
            if (gap > 2) { continue; }
            if (CityGrowth.bulbWide(other)) {
                BeardRoadsEnds.Bulb bulb = BeardRoadsEnds.bulbAt(world, other);
                if (bulb == null) { continue; }
                return bulb.level + 1;
            }
            boolean alongX = BeardPlots.roadAlongX(other);
            int start = alongX ? road.minX : road.minZ;
            BeardRoads.Grade grade = other instanceof IRoadLayout ? ((IRoadLayout) other).rdpl$layout() : null;
            if (grade == null) { grade = roadProfile(world, other, alongX, start, alongX ? road.maxX : road.maxZ, alongX ? road.minZ : road.minX, alongX ? road.maxZ : road.maxX, true); }
            if (grade == null) { return Integer.MIN_VALUE; }
            int center = alongX ? (box.minX + box.maxX) / 2 : (box.minZ + box.maxZ) / 2;
            int row = MathHelper.clamp(center, start, start + grade.profile.length - 1);
            if (grade.profile[row - start] == Integer.MIN_VALUE) { continue; }
            return grade.profile[row - start] + 1;
        }
        return Integer.MIN_VALUE;
    }
}
