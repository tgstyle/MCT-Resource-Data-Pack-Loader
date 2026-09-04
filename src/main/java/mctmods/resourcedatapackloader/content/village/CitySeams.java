package mctmods.resourcedatapackloader.content.village;

import mctmods.resourcedatapackloader.content.worldgen.ContentBeard;
import mctmods.resourcedatapackloader.content.worldgen.ContentSites;
import mctmods.resourcedatapackloader.content.worldgen.ContentStructurePlacement;
import mctmods.resourcedatapackloader.content.worldgen.ContentStructureSearch;
import mctmods.resourcedatapackloader.content.worldgen.beard.BeardPlots;
import mctmods.resourcedatapackloader.content.worldgen.beard.BeardRoads;
import mctmods.resourcedatapackloader.content.worldgen.beard.BeardSite;
import mctmods.resourcedatapackloader.content.worldgen.beard.BeardSurface;
import mctmods.resourcedatapackloader.content.worldgen.beard.interfaces.RoadLayout;
import mctmods.resourcedatapackloader.mixin.rdpl.common.IStructureStartGrow;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.world.SeededRandom;

import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureStart;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.WeakHashMap;
import javax.annotation.Nullable;

public final class CitySeams {
    private static final int SLIDE = 24;
    private static final Set<StructureComponent> RESERVED = Collections.newSetFromMap(new WeakHashMap<>());

    private CitySeams() {}

    public static boolean reserved(StructureComponent piece) { return RESERVED.contains(piece); }

    private static final class Arrival {
        final StructureStart start;
        final StructureComponent street;
        final StructureBoundingBox box;
        final boolean onward;
        final int end;
        final int row;
        int target;
        List<StructureComponent> making = new ArrayList<>();

        Arrival(StructureStart start, StructureComponent street, boolean onward, int end, int row) {
            this.start = start;
            this.street = street;
            this.box = street.getBoundingBox();
            this.onward = onward;
            this.end = end;
            this.row = row;
            this.target = end;
        }

        boolean there(int at) { return onward ? end >= at : end <= at; }

        boolean reach(World world, List<StructureComponent> everyone, boolean alongX, int at) {
            making = new ArrayList<>();
            target = at;
            if (there(at)) {
                target = end;
                return true;
            }
            List<StructureComponent> own = start.getComponents();
            StructureBoundingBox reached = new StructureBoundingBox(box);
            if (alongX) { if (onward) { reached.maxX = at; } else { reached.minX = at; } }
            else { if (onward) { reached.maxZ = at; } else { reached.minZ = at; } }
            List<StructureComponent> found = crossable(world, everyone, own, street, alongX, reached);
            if (found == null) { return false; }
            int d = onward ? 1 : -1;
            StructureBoundingBox beside = ContentBeard.beside(own, ContentBeard.strip(box, alongX, Math.min(end + d, at), Math.max(end + d, at)), alongX);
            if (beside != null) {
                ContentLog.LOGGER.debug("The street at {}, {} cannot run to {}: it would run beside the road at {}, {}", box.minX, box.minZ, at, beside.minX, beside.minZ);
                return false;
            }
            EnumFacing facing = alongX ? (onward ? EnumFacing.EAST : EnumFacing.WEST) : (onward ? EnumFacing.SOUTH : EnumFacing.NORTH);
            int behind = end - (onward ? 1 : -1) * (BeardRoads.pathFullWidth() - 1) / 2;
            StructureBoundingBox tail = new StructureBoundingBox(reached);
            if (alongX) { if (onward) { tail.minX = Math.max(tail.minX, behind); } else { tail.maxX = Math.min(tail.maxX, behind); } }
            else { if (onward) { tail.minZ = Math.max(tail.minZ, behind); } else { tail.maxZ = Math.min(tail.maxZ, behind); } }
            int rows = (alongX ? tail.maxX - tail.minX : tail.maxZ - tail.minZ) + 1;
            List<StructureComponent> held = ContentBeard.laid();
            ContentBeard.laying(own);
            int kept;
            try { kept = BeardRoads.roadReach(tail, facing); }
            finally { ContentBeard.laying(held); }
            if (kept < rows) {
                ContentLog.LOGGER.debug("The street at {}, {} can run only {} of the {} row(s) to {}, so another is looked for", box.minX, box.minZ, kept, rows, at);
                return false;
            }
            making = found;
            return true;
        }
    }

    public static void tie(StructureStart start, World world, Random rand) {
        if (!ContentBeard.wanted() || BeardSurface.unreadable(world)) { return; }
        List<StructureComponent> components = start.getComponents();
        if (components.isEmpty() || !(components.get(0) instanceof StructureVillagePieces.Start)) { return; }
        StructureVillagePieces.Start startPiece = (StructureVillagePieces.Start) components.get(0);
        StructureBoundingBox well = startPiece.getBoundingBox();
        int wx = well.minX + 2;
        int wz = well.minZ + 2;
        int tied = 0;
        for (int[] site : neighbors(world, wx, wz)) { if (tieTo(start, startPiece, world, rand, wx, wz, site[0], site[1])) { tied++; } }
        if (tied > 0) { ((IStructureStartGrow) start).rdpl$updateBoundingBox(); }
    }

    private static List<int[]> neighbors(World world, int wx, int wz) {
        List<int[]> found = new ArrayList<>();
        int reach = CityGrowth.march() * 2 + 64;
        List<long[]> pinned = ContentStructurePlacement.pins(ContentStructurePlacement.VILLAGES);
        if (pinned != null) {
            for (long[] pin : pinned) { consider(found, wx, wz, (int) pin[0] >> 4, (int) pin[1] >> 4, reach); }
            return found;
        }
        ContentSites known = ContentSites.of(world, ContentBeard.villageSpacing(world));
        int grid = known.spacing();
        int cellX = Math.floorDiv(wx >> 4, grid);
        int cellZ = Math.floorDiv(wz >> 4, grid);
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                long chosen = BeardSite.siteFor(world, known, cellX + dx, cellZ + dz, grid);
                if (chosen == ContentBeard.NO_SITE) { continue; }
                int chunkX = (int) (chosen >> 32);
                int chunkZ = (int) chosen;
                if (!ContentStructurePlacement.allows(ContentStructurePlacement.VILLAGES, world, chunkX, chunkZ) || BeardSite.mansionCandidateNear(world, chunkX, chunkZ)) { continue; }
                consider(found, wx, wz, chunkX, chunkZ, reach);
            }
        }
        return found;
    }

    private static void consider(List<int[]> found, int wx, int wz, int chunkX, int chunkZ, int reach) {
        int x = (chunkX << 4) + 4;
        int z = (chunkZ << 4) + 4;
        if ((x == wx && z == wz) || Math.abs(x - wx) > reach || Math.abs(z - wz) > reach) { return; }
        found.add(new int[] { x, z });
    }

    @Nullable private static StructureStart villageAt(World world, StructureStart own, int x, int z) {
        for (StructureStart other : ContentStructureSearch.villageStarts(world)) {
            if (other == own || other.getComponents().isEmpty()) { continue; }
            StructureBoundingBox well = other.getComponents().get(0).getBoundingBox();
            if (well.minX + 2 == x && well.minZ + 2 == z) { return other; }
        }
        return null;
    }

    private static boolean tieTo(StructureStart start, StructureVillagePieces.Start startPiece, World world, Random rand, int wx, int wz, int bx, int bz) {
        int dx = bx - wx;
        int dz = bz - wz;
        boolean alongX = Math.abs(dx) >= Math.abs(dz);
        int span = alongX ? Math.abs(dx) : Math.abs(dz);
        int half = (BeardRoads.pathFullWidth() - 1) / 2;
        int keepOff = ContentBeard.plazaReach() + BeardRoads.pathFullWidth() + 8;
        if (span < 2 * keepOff + BeardRoads.pathFullWidth()) { return false; }
        Random roll = SeededRandom.at(world, wx + bx, wz + bz);
        int middle = alongX ? (wx + bx) / 2 : (wz + bz) / 2;
        int seeded = middle + roll.nextInt(span / 2 + 1) - span / 4;
        int low = Math.min(alongX ? wx : wz, alongX ? bx : bz) + keepOff;
        int high = Math.max(alongX ? wx : wz, alongX ? bx : bz) - keepOff;
        seeded = Math.max(low, Math.min(high, seeded));
        boolean onward = alongX ? dx > 0 : dz > 0;
        List<StructureComponent> components = start.getComponents();
        List<StructureComponent> everyone = ContentBeard.everyone(components);
        StructureStart neighbor = villageAt(world, start, bx, bz);
        if (neighbor != null) {
            unroll(world, neighbor, alongX, alongX ? wx : wz, alongX ? bx : bz);
            ContentBeard.closeEnds(neighbor, world, rand, plot -> built(world, plot.getBoundingBox()), components);
            ContentBeard.closeEnds(start, world, rand, plot -> built(world, plot.getBoundingBox()), components);
            stubs(start, startPiece, world, rand, neighbor);
        }
        if (neighbor != null && meets(components, neighbor.getComponents())) {
            ContentLog.LOGGER.debug("The village at {}, {} already meets the village at {}, {} at a street, so no seam is tied between them", wx, wz, bx, bz);
            return false;
        }
        if (neighbor != null && collinear(start, world, everyone, neighbor, wx, wz, bx, bz, alongX, onward)) { return true; }
        int standing = neighbor == null ? Integer.MIN_VALUE : existingTie(everyone, components, alongX, seeded, low, high, alongX ? wz : wx, alongX ? bz : bx);
        if (standing != Integer.MIN_VALUE && attempt(start, startPiece, world, rand, everyone, neighbor, wx, wz, bx, bz, alongX, onward, half, standing, true, low, high)) { return true; }
        for (int step = 0; step <= high - low; step += 8) {
            for (int sign = 1; sign >= -1; sign -= 2) {
                int seam = seeded + sign * step;
                if (seam < low || seam > high) { continue; }
                seam = dryAt(world, alongX, seam, alongX ? wz : wx, alongX ? bz : bx, low, high);
                if (attempt(start, startPiece, world, rand, everyone, neighbor, wx, wz, bx, bz, alongX, onward, half, seam, false, low, high)) { return true; }
                if (step == 0) { break; }
            }
        }
        ContentLog.LOGGER.debug("The village at {}, {} finds no seam between {} and {} along {} at which it can tie toward the village site at {}, {}", wx, wz, low, high, alongX ? "x" : "z", bx, bz);
        return false;
    }

    private static boolean street(StructureComponent piece) {
        if (!(piece instanceof StructureVillagePieces.Path) || CityGrowth.bulbWide(piece)) { return false; }
        return !BeardRoads.roadNarrow(piece.getBoundingBox(), BeardPlots.roadAlongX(piece));
    }

    private static boolean meets(List<StructureComponent> own, List<StructureComponent> theirs) {
        for (StructureComponent mine : own) {
            if (!street(mine)) { continue; }
            StructureBoundingBox a = mine.getBoundingBox();
            for (StructureComponent other : theirs) {
                if (!street(other)) { continue; }
                StructureBoundingBox b = other.getBoundingBox();
                boolean overX = b.maxX >= a.minX && b.minX <= a.maxX;
                boolean overZ = b.maxZ >= a.minZ && b.minZ <= a.maxZ;
                boolean nearX = b.maxX >= a.minX - 1 && b.minX <= a.maxX + 1;
                boolean nearZ = b.maxZ >= a.minZ - 1 && b.minZ <= a.maxZ + 1;
                if ((overX && nearZ) || (overZ && nearX)) { return true; }
            }
        }
        return false;
    }

    private static int existingTie(List<StructureComponent> everyone, List<StructureComponent> own, boolean alongX, int seeded, int low, int high, int rowA, int rowB) {
        int best = Integer.MIN_VALUE;
        int reach = CityGrowth.march();
        for (StructureComponent other : everyone) {
            if (own.contains(other) || !street(other) || BeardPlots.roadAlongX(other) == alongX) { continue; }
            StructureBoundingBox met = other.getBoundingBox();
            int center = alongX ? (met.minX + met.maxX) / 2 : (met.minZ + met.maxZ) / 2;
            if (center < low || center > high) { continue; }
            int metLo = alongX ? met.minZ : met.minX;
            int metHi = alongX ? met.maxZ : met.maxX;
            if (metHi < Math.min(rowA, rowB) - reach || metLo > Math.max(rowA, rowB) + reach) { continue; }
            if (best == Integer.MIN_VALUE || Math.abs(center - seeded) < Math.abs(best - seeded)) { best = center; }
        }
        return best;
    }

    private static List<Arrival> candidates(StructureStart start, boolean alongX, boolean onward, int wellAt, int limit) {
        List<Arrival> found = new ArrayList<>();
        for (StructureComponent piece : start.getComponents()) {
            if (!street(piece) || BeardPlots.roadAlongX(piece) != alongX) { continue; }
            StructureBoundingBox box = piece.getBoundingBox();
            int end = alongX ? (onward ? box.maxX : box.minX) : (onward ? box.maxZ : box.minZ);
            if (onward ? (end <= wellAt || end >= limit) : (end >= wellAt || end <= limit)) { continue; }
            found.add(new Arrival(start, piece, onward, end, alongX ? (box.minZ + box.maxZ) / 2 : (box.minX + box.maxX) / 2));
        }
        return found;
    }

    private static List<Arrival> ordered(List<Arrival> arrivals, int at, int aim, boolean onward) {
        List<Arrival> sorted = new ArrayList<>(arrivals);
        sorted.sort(Comparator.comparingInt((Arrival a) -> a.there(at) ? 0 : 1).thenComparingInt(a -> a.there(at) ? Math.abs(a.row - aim) : Math.abs(at - a.end)).thenComparingInt(a -> onward ? -a.end : a.end));
        return sorted;
    }

    private static boolean collinear(StructureStart start, World world, List<StructureComponent> everyone, StructureStart neighbor, int wx, int wz, int bx, int bz, boolean alongX, boolean onward) {
        int d = onward ? 1 : -1;
        int wellAt = alongX ? wx : wz;
        int siteAt = alongX ? bx : bz;
        List<Arrival> theirs = candidates(neighbor, alongX, !onward, siteAt, wellAt);
        for (Arrival m : ordered(candidates(start, alongX, onward, wellAt, siteAt), siteAt, alongX ? bz : bx, onward)) {
            for (Arrival t : theirs) {
                if (m.row != t.row || (onward ? t.end <= m.end : t.end >= m.end)) { continue; }
                if (!m.reach(world, everyone, alongX, t.end - d)) { continue; }
                arrive(world, m, alongX, "the street coming the other way");
                ContentLog.LOGGER.debug("The street at {}, {} of the village at {}, {} runs to the street at {}, {} of the village at {}, {} on the same line, so the two villages join there and no tie road is needed", m.box.minX, m.box.minZ, wx, wz, t.box.minX, t.box.minZ, bx, bz);
                return true;
            }
        }
        return false;
    }

    private static boolean attempt(StructureStart start, StructureVillagePieces.Start startPiece, World world, Random rand, List<StructureComponent> everyone, @Nullable StructureStart neighbor, int wx, int wz, int bx, int bz, boolean alongX, boolean onward, int half, int seam, boolean standing, int low, int high) {
        String axis = alongX ? "x" : "z";
        int d = onward ? 1 : -1;
        int wellAt = alongX ? wx : wz;
        int siteAt = alongX ? bx : bz;
        int siteRow = alongX ? bz : bx;
        List<StructureComponent> components = start.getComponents();
        List<Arrival> mine = candidates(start, alongX, onward, wellAt, siteAt);
        if (mine.isEmpty()) {
            ContentLog.LOGGER.debug("The village at {}, {} has no street heading toward the village site at {}, {}", wx, wz, bx, bz);
            return false;
        }
        List<Arrival> theirs = neighbor == null ? new ArrayList<>() : candidates(neighbor, alongX, !onward, siteAt, wellAt);
        if (!standing) {
            if (neighbor == null) {
                for (Arrival m : ordered(mine, seam + d * half, siteRow, onward)) {
                    if (Math.abs(m.row - siteRow) >= BeardRoads.pathFullWidth() || !m.reach(world, everyone, alongX, seam + d * half)) { continue; }
                    arrive(world, m, alongX, "the seam");
                    RESERVED.add(m.street);
                    ContentLog.LOGGER.debug("The street at {}, {} of the village at {}, {} runs through the seam at {} {} on the line of the village site at {}, {}, so its end is kept open for the street that village will send this way", m.box.minX, m.box.minZ, wx, wz, axis, seam, bx, bz);
                    return true;
                }
            }
        }
        int near = seam - d * (half + 1);
        for (Arrival m : ordered(mine, near, siteRow, onward)) {
            int snapped = snap(m, seam, d, half, low, high);
            if (snapped != seam && lay(start, startPiece, world, rand, everyone, neighbor, components, m, theirs, wx, wz, bx, bz, alongX, onward, half, snapped, false, low, high)) { return true; }
            if (lay(start, startPiece, world, rand, everyone, neighbor, components, m, theirs, wx, wz, bx, bz, alongX, onward, half, seam, snapped == seam, low, high)) { return true; }
        }
        ContentLog.LOGGER.debug("The village at {}, {} has no street that can reach the seam at {} {} toward the village site at {}, {}", wx, wz, axis, seam, bx, bz);
        return false;
    }

    private static int snap(Arrival m, int seam, int d, int half, int low, int high) {
        int width = BeardRoads.pathFullWidth();
        int near = seam - d * (half + 1);
        int over = (m.end - near) * d;
        int at = seam;
        if (over > 0 && over < width) { at = m.end + d * (half + 1); }
        else if (over >= width && over < 2 * width) { at = m.end - d * half; }
        return at < low || at > high ? seam : at;
    }

    private static boolean lay(StructureStart start, StructureVillagePieces.Start startPiece, World world, Random rand, List<StructureComponent> everyone, @Nullable StructureStart neighbor, List<StructureComponent> components, Arrival m, List<Arrival> theirs, int wx, int wz, int bx, int bz, boolean alongX, boolean onward, int half, int seam, boolean snapToTheirs, int low, int high) {
        String axis = alongX ? "x" : "z";
        int d = onward ? 1 : -1;
        int siteRow = alongX ? bz : bx;
        int near = seam - d * (half + 1);
        int far = seam + d * (half + 1);
        if (!m.reach(world, everyone, alongX, near)) { return false; }
        Arrival t = null;
        for (Arrival other : ordered(theirs, far, m.row, !onward)) {
            if (other.reach(world, everyone, alongX, far)) {
                t = other;
                break;
            }
        }
        if (t != null && snapToTheirs) {
            int snapped = snap(t, seam, -d, half, low, high);
            if (snapped != seam && m.reach(world, everyone, alongX, snapped - d * (half + 1)) && t.reach(world, everyone, alongX, snapped + d * (half + 1))) {
                seam = snapped;
                near = seam - d * (half + 1);
                far = seam + d * (half + 1);
            }
            else {
                m.reach(world, everyone, alongX, near);
                t.reach(world, everyone, alongX, far);
            }
        }
        int farRow = t != null ? t.row : siteRow;
        List<StructureBoundingBox> ties = tieBoxes(everyone, alongX, seam, half, m.row, farRow, m.box.minY, m.box.maxY);
        List<StructureComponent> making = new ArrayList<>();
        for (StructureBoundingBox tie : ties) {
            List<StructureComponent> found = crossable(world, everyone, components, m.street, !alongX, tie);
            if (found == null || ContentBeard.beside(components, tie, !alongX) != null) {
                ContentLog.LOGGER.debug("The tie road at {} {} between the village at {}, {} and the site at {}, {} has no room", axis, seam, wx, wz, bx, bz);
                return false;
            }
            making.addAll(found);
        }
        arrive(world, m, alongX, "the seam");
        for (StructureComponent plot : making) {
            if (!dismiss(world, components, plot)) { continue; }
            ContentLog.LOGGER.debug("{} at {}, {} makes way for the tie road", plot.getClass().getSimpleName(), plot.getBoundingBox().minX, plot.getBoundingBox().minZ);
        }
        for (StructureBoundingBox tie : ties) {
            StructureVillagePieces.Path lane = new StructureVillagePieces.Path(startPiece, 0, rand, tie, alongX ? EnumFacing.SOUTH : EnumFacing.EAST);
            components.add(lane);
            RESERVED.add(lane);
            ContentLog.LOGGER.debug("The street at {}, {} of the village at {}, {} reaches the seam at {} {} toward the site at {}, {}, and the tie road {} is laid along it", m.box.minX, m.box.minZ, wx, wz, axis, seam, bx, bz, tie);
        }
        if (ties.isEmpty()) { ContentLog.LOGGER.debug("The street at {}, {} of the village at {}, {} reaches the seam at {} {} toward the site at {}, {} and ends on the tie road already standing there", m.box.minX, m.box.minZ, wx, wz, axis, seam, bx, bz); }
        if (t != null) {
            arrive(world, t, alongX, "the tie road");
            ((IStructureStartGrow) neighbor).rdpl$updateBoundingBox();
            ContentLog.LOGGER.debug("The street at {}, {} of the neighboring village at {}, {} is brought to the tie road as well, so the two villages join at {} {}", t.box.minX, t.box.minZ, bx, bz, axis, seam);
        }
        return true;
    }

    private static void arrive(World world, Arrival at, boolean alongX, String what) {
        for (StructureComponent plot : at.making) {
            if (!dismiss(world, at.start.getComponents(), plot)) { continue; }
            ContentLog.LOGGER.debug("{} at {}, {} makes way for the street reaching {}", plot.getClass().getSimpleName(), plot.getBoundingBox().minX, plot.getBoundingBox().minZ, what);
        }
        if (at.there(at.target)) { return; }
        if (alongX) { if (at.onward) { at.box.maxX = at.target; } else { at.box.minX = at.target; } }
        else { if (at.onward) { at.box.maxZ = at.target; } else { at.box.minZ = at.target; } }
        if (at.street instanceof RoadLayout) { ((RoadLayout) at.street).rdpl$layout(null); }
    }

    @Nullable private static List<StructureComponent> crossable(World world, List<StructureComponent> everyone, List<StructureComponent> own, StructureComponent piece, boolean alongX, StructureBoundingBox strip) {
        List<StructureComponent> plots = new ArrayList<>();
        for (StructureComponent held : everyone) {
            if (held == piece || !held.getBoundingBox().intersectsWith(strip.minX, strip.minZ, strip.maxX, strip.maxZ)) { continue; }
            if (held instanceof StructureVillagePieces.Well) { return null; }
            if (held instanceof StructureVillagePieces.Path) {
                StructureBoundingBox met = held.getBoundingBox();
                if (BeardRoads.roadNarrow(met, BeardPlots.roadAlongX(held))) { continue; }
                if (own.contains(held) && BeardPlots.roadAlongX(held) != alongX && !CityGrowth.bulbWide(held)) { continue; }
                return null;
            }
            if (!own.contains(held) && built(world, held.getBoundingBox())) { return null; }
            plots.add(held);
        }
        return plots;
    }

    public static boolean built(World world, StructureBoundingBox box) {
        for (int chunkX = (box.minX - 8) >> 4; chunkX <= (box.maxX - 8) >> 4; chunkX++) {
            for (int chunkZ = (box.minZ - 8) >> 4; chunkZ <= (box.maxZ - 8) >> 4; chunkZ++) {
                Chunk loaded = world.getChunkProvider().getLoadedChunk(chunkX, chunkZ);
                if (loaded != null ? loaded.isTerrainPopulated() : world.getChunkProvider().isChunkGeneratedAt(chunkX, chunkZ)) { return true; }
            }
        }
        return false;
    }

    private static boolean dismiss(World world, List<StructureComponent> own, StructureComponent plot) {
        if (own.remove(plot)) { return true; }
        for (StructureStart other : ContentStructureSearch.villageStarts(world)) {
            if (other.getComponents().remove(plot)) {
                ((IStructureStartGrow) other).rdpl$updateBoundingBox();
                return true;
            }
        }
        return false;
    }

    public static boolean facesNeighbor(World world, List<StructureComponent> own, boolean alongX, int end, int dir, int row) {
        if (own.isEmpty()) { return false; }
        StructureBoundingBox well = own.get(0).getBoundingBox();
        int reach = CityGrowth.march();
        for (int[] site : neighbors(world, well.minX + 2, well.minZ + 2)) {
            int siteAt = alongX ? site[0] : site[1];
            int siteRow = alongX ? site[1] : site[0];
            if ((siteAt - end) * dir > 0 && Math.abs(row - siteRow) <= reach) { return true; }
        }
        return false;
    }

    private static void stubs(StructureStart start, StructureVillagePieces.Start startPiece, World world, Random rand, StructureStart neighbor) {
        List<StructureComponent> components = start.getComponents();
        List<StructureComponent> everyone = ContentBeard.everyone(components);
        int reach = ContentBeard.attachGap() * 2;
        for (StructureComponent piece : neighbor.getComponents().toArray(new StructureComponent[0])) {
            if (!street(piece)) { continue; }
            StructureBoundingBox box = piece.getBoundingBox();
            boolean alongX = BeardPlots.roadAlongX(piece);
            int acrossLo = alongX ? box.minZ : box.minX;
            int acrossHi = alongX ? box.maxZ : box.maxX;
            for (int side = 0; side < 2; side++) {
                int dir = side == 1 ? 1 : -1;
                int end = alongX ? (dir > 0 ? box.maxX : box.minX) : (dir > 0 ? box.maxZ : box.minZ);
                if (metBeyond(everyone, piece, alongX, end + dir, acrossLo, acrossHi)) { continue; }
                StructureBoundingBox best = null;
                int bestAhead = Integer.MAX_VALUE;
                for (StructureComponent mine : components) {
                    if (!street(mine) || BeardPlots.roadAlongX(mine) == alongX) { continue; }
                    StructureBoundingBox met = mine.getBoundingBox();
                    int ahead = dir > 0 ? (alongX ? met.minX : met.minZ) - end : end - (alongX ? met.maxX : met.maxZ);
                    if (ahead < 2 || ahead > reach || ahead >= bestAhead) { continue; }
                    if ((alongX ? met.maxZ : met.maxX) < acrossLo || (alongX ? met.minZ : met.minX) > acrossHi) { continue; }
                    bestAhead = ahead;
                    best = met;
                }
                if (best == null) { continue; }
                int from = dir > 0 ? end + 1 : (alongX ? best.maxX : best.maxZ) + 1;
                int to = dir > 0 ? (alongX ? best.minX : best.minZ) - 1 : end - 1;
                StructureBoundingBox stub = alongX ? new StructureBoundingBox(from, box.minY, acrossLo, to, box.maxY, acrossHi) : new StructureBoundingBox(acrossLo, box.minY, from, acrossHi, box.maxY, to);
                List<StructureComponent> making = crossable(world, everyone, components, piece, alongX, stub);
                if (making == null || ContentBeard.beside(components, stub, alongX) != null) {
                    ContentLog.LOGGER.debug("The dead end at {}, {} of the neighboring village cannot be reached from the street at {}, {}: the strip is held", alongX ? end : (acrossLo + acrossHi) / 2, alongX ? (acrossLo + acrossHi) / 2 : end, best.minX, best.minZ);
                    continue;
                }
                for (StructureComponent plot : making) {
                    if (!dismiss(world, components, plot)) { continue; }
                    ContentLog.LOGGER.debug("{} at {}, {} makes way for the stub reaching a neighboring village's dead end", plot.getClass().getSimpleName(), plot.getBoundingBox().minX, plot.getBoundingBox().minZ);
                }
                StructureVillagePieces.Path lane = new StructureVillagePieces.Path(startPiece, 0, rand, stub, alongX ? EnumFacing.EAST : EnumFacing.SOUTH);
                components.add(lane);
                RESERVED.add(lane);
                ContentLog.LOGGER.debug("The dead end at {}, {} of the neighboring village at {}, {} is reached by the stub {} from the street at {}, {}, so the two villages join there", alongX ? end : (acrossLo + acrossHi) / 2, alongX ? (acrossLo + acrossHi) / 2 : end, neighbor.getBoundingBox().minX, neighbor.getBoundingBox().minZ, stub, best.minX, best.minZ);
                break;
            }
        }
    }

    private static boolean metBeyond(List<StructureComponent> pieces, StructureComponent piece, boolean alongX, int beyond, int acrossLo, int acrossHi) {
        for (StructureComponent other : pieces) {
            if (other == piece || !(other instanceof StructureVillagePieces.Path)) { continue; }
            StructureBoundingBox met = other.getBoundingBox();
            if (BeardRoads.roadNarrow(met, BeardPlots.roadAlongX(other))) { continue; }
            if (beyond < (alongX ? met.minX : met.minZ) - 1 || beyond > (alongX ? met.maxX : met.maxZ) + 1) { continue; }
            if ((alongX ? met.maxZ : met.maxX) < acrossLo || (alongX ? met.minZ : met.minX) > acrossHi) { continue; }
            return true;
        }
        return false;
    }

    private static void unroll(World world, StructureStart neighbor, boolean alongX, int wellAt, int siteAt) {
        List<StructureComponent> theirs = neighbor.getComponents();
        for (StructureComponent piece : theirs.toArray(new StructureComponent[0])) {
            if (!(piece instanceof StructureVillagePieces.Path) || !CityGrowth.bulbWide(piece)) { continue; }
            StructureBoundingBox bulb = piece.getBoundingBox();
            int at = alongX ? (bulb.minX + bulb.maxX) / 2 : (bulb.minZ + bulb.maxZ) / 2;
            if (at <= Math.min(wellAt, siteAt) || at >= Math.max(wellAt, siteAt)) { continue; }
            if (built(world, bulb)) {
                ContentLog.LOGGER.debug("The cul-de-sac at {}, {} of the village at {}, {} faces this village but is already built, so it stays", bulb.minX, bulb.minZ, neighbor.getBoundingBox().minX, neighbor.getBoundingBox().minZ);
                continue;
            }
            List<StructureComponent> fronting = new ArrayList<>();
            boolean standing = false;
            for (StructureComponent other : theirs) {
                if (other == piece || other instanceof StructureVillagePieces.Path || !other.getBoundingBox().intersectsWith(bulb.minX - 1, bulb.minZ - 1, bulb.maxX + 1, bulb.maxZ + 1)) { continue; }
                if (built(world, other.getBoundingBox())) {
                    standing = true;
                    break;
                }
                fronting.add(other);
            }
            if (standing) {
                ContentLog.LOGGER.debug("The cul-de-sac at {}, {} faces this village but a house on it is already built, so it stays", bulb.minX, bulb.minZ);
                continue;
            }
            theirs.remove(piece);
            theirs.removeAll(fronting);
            ((IStructureStartGrow) neighbor).rdpl$updateBoundingBox();
            ContentLog.LOGGER.debug("The cul-de-sac at {}, {} of the village at {}, {} faces this village and nothing of it is built yet, so it and the {} plot(s) on it are unrolled and its street's end is open again", bulb.minX, bulb.minZ, neighbor.getBoundingBox().minX, neighbor.getBoundingBox().minZ, fronting.size());
        }
    }

    private static int dryAt(World world, boolean alongX, int seam, int rowA, int rowB, int low, int high) {
        int sea = world.getSeaLevel() - 1;
        for (int step = 0; step <= SLIDE; step += 4) {
            for (int sign = 1; sign >= -1; sign -= 2) {
                int at = seam + sign * step;
                if (at < low || at > high) { continue; }
                int a = BeardSurface.surfaceAt(world, alongX ? at : rowA, alongX ? rowA : at);
                int b = BeardSurface.surfaceAt(world, alongX ? at : rowB, alongX ? rowB : at);
                if (a >= sea && b >= sea) { return at; }
                if (step == 0) { break; }
            }
        }
        return seam;
    }

    private static List<StructureBoundingBox> tieBoxes(List<StructureComponent> everyone, boolean alongX, int seam, int half, int ownRow, int farRow, int minY, int maxY) {
        List<int[]> open = new ArrayList<>();
        open.add(new int[] { Math.min(ownRow, farRow) - half, Math.max(ownRow, farRow) + half });
        for (StructureComponent other : everyone) {
            if (!street(other) || BeardPlots.roadAlongX(other) == alongX) { continue; }
            StructureBoundingBox met = other.getBoundingBox();
            int center = alongX ? (met.minX + met.maxX) / 2 : (met.minZ + met.maxZ) / 2;
            if (Math.abs(center - seam) > 1) { continue; }
            int metLo = alongX ? met.minZ : met.minX;
            int metHi = alongX ? met.maxZ : met.maxX;
            List<int[]> left = new ArrayList<>();
            for (int[] span : open) {
                if (metHi < span[0] || metLo > span[1]) {
                    left.add(span);
                    continue;
                }
                if (span[0] < metLo) { left.add(new int[] { span[0], metLo - 1 }); }
                if (span[1] > metHi) { left.add(new int[] { metHi + 1, span[1] }); }
            }
            open = left;
        }
        List<StructureBoundingBox> ties = new ArrayList<>();
        for (int[] span : open) {
            int lo = span[0];
            int hi = span[1];
            if (hi - lo < half) {
                if (lo == Math.min(ownRow, farRow) - half) { lo = hi - half; }
                else { hi = lo + half; }
            }
            ties.add(alongX
                    ? new StructureBoundingBox(seam - half, minY, lo, seam + half, maxY, hi)
                    : new StructureBoundingBox(lo, minY, seam - half, hi, maxY, seam + half));
        }
        return ties;
    }
}
