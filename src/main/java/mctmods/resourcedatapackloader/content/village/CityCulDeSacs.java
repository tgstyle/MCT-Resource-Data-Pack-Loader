package mctmods.resourcedatapackloader.content.village;

import mctmods.resourcedatapackloader.content.worldgen.ContentBeard;
import mctmods.resourcedatapackloader.content.worldgen.ContentStructureSearch;
import mctmods.resourcedatapackloader.content.worldgen.beard.BeardGrade;
import mctmods.resourcedatapackloader.content.worldgen.beard.BeardPlots;
import mctmods.resourcedatapackloader.content.worldgen.beard.BeardRails;
import mctmods.resourcedatapackloader.content.worldgen.beard.BeardRoads;
import mctmods.resourcedatapackloader.content.worldgen.beard.BeardRoadsGrade;
import mctmods.resourcedatapackloader.content.worldgen.beard.BeardSurface;
import mctmods.resourcedatapackloader.mixin.rdpl.common.IStructureStartGrow;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.world.SeededRandom;

import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureStart;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import net.minecraft.util.math.MathHelper;
import java.util.*;
import javax.annotation.Nullable;

public final class CityCulDeSacs {
    private static final int DIG = 4;

    private CityCulDeSacs() {}

    public static void culDeSacs(StructureStart held, World world, Random rand) {
        if (!ContentBeard.wanted() || BeardSurface.unreadable(world)) { return; }
        List<StructureComponent> components = held.getComponents();
        if (components.isEmpty() || !(components.get(0) instanceof StructureVillagePieces.Start)) { return; }
        StructureVillagePieces.Start startPiece = (StructureVillagePieces.Start) components.get(0);
        int wide = BeardRoads.pathFullWidth() + 8;
        int sea = world.getSeaLevel();
        int bulbs = 0;
        for (StructureComponent piece : components.toArray(new StructureComponent[0])) {
            if (!(piece instanceof StructureVillagePieces.Path) || CityGrowth.bulbWide(piece) || CitySeams.reserved(piece)) { continue; }
            StructureBoundingBox box = piece.getBoundingBox();
            boolean alongX = BeardPlots.roadAlongX(piece);
            if (BeardRoads.roadNarrow(box, alongX)) { continue; }
            int center = alongX ? (box.minZ + box.maxZ) / 2 : (box.minX + box.maxX) / 2;
            for (int side = 0; side < 2; side++) {
                boolean outward = side == 1;
                int dir = outward ? 1 : -1;
                int end = alongX ? (outward ? box.maxX : box.minX) : (outward ? box.maxZ : box.minZ);
                int endX = alongX ? end : center;
                int endZ = alongX ? center : end;
                if (metAtEnd(world, held, piece, alongX, end, alongX ? box.minZ : box.minX, alongX ? box.maxZ : box.maxX)) { continue; }
                if (CitySeams.facesNeighbor(world, components, alongX, end, dir, center)) {
                    ContentLog.LOGGER.debug("The dead end at {}, {} faces a neighboring village's site, so it rolls no cul-de-sac and stays open for that village's streets", endX, endZ);
                    continue;
                }
                if (CityLayout.skipsBulb(piece, outward, SeededRandom.at(world, endX + dir, endZ + dir))) { continue; }
                int least = (BeardRoads.pathFullWidth() + 1) / 2 + 1;
                int found = 0;
                int stem = 0;
                boolean steep = false;
                List<StructureComponent> yielding = null;
                for (int r = (wide - 1) / 2; r >= least && found == 0; r--) {
                    for (int out = 1; out <= 7; out += 3) {
                        int spot = end + dir * (out + r + 1);
                        int discX = alongX ? spot : center;
                        int discZ = alongX ? center : spot;
                        if (!clearFor(world, held, piece, discX, discZ, r)) { continue; }
                        yielding = yielding(world, held, piece, court(box, alongX, end, dir, outward, center, r, out));
                        if (yielding == null) { continue; }
                        if (!seated(world, piece, alongX, end, discX, discZ, r)) {
                            steep = true;
                            continue;
                        }
                        found = r;
                        stem = out;
                        break;
                    }
                }
                if (found == 0) {
                    if (steep && ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("The dead end at {}, {} rolled a cul-de-sac, but every court it could seat would cut the ground beside it, so it stays a plain end", endX, endZ); }
                    continue;
                }
                int half = found;
                StructureBoundingBox bulb = court(box, alongX, end, dir, outward, center, half, stem);
                int heart = end + dir * (stem + half + 1);
                int discX = alongX ? heart : center;
                int discZ = alongX ? center : heart;
                boolean dry = true;
                for (int z = bulb.minZ; z <= bulb.maxZ && dry; z += 2) {
                    for (int x = bulb.minX; x <= bulb.maxX; x += 2) {
                        int off = (x - discX) * (x - discX) + (z - discZ) * (z - discZ);
                        if (off > half * half + half) { continue; }
                        if (BeardSurface.surfaceAt(world, x, z) < sea - 1) {
                            dry = false;
                            break;
                        }
                    }
                }
                if (!dry) { continue; }
                for (StructureComponent plot : yielding) { makeWay(world, held, plot, bulb); }
                EnumFacing facing = alongX ? (outward ? EnumFacing.EAST : EnumFacing.WEST) : (outward ? EnumFacing.SOUTH : EnumFacing.NORTH);
                StructureVillagePieces.Path bulbPiece = new StructureVillagePieces.Path(startPiece, 0, rand, bulb, facing);
                components.add(bulbPiece);
                CityGrowth.laying = true;
                CityGrowth.bulbLaying = true;
                try {
                    if (!CityLayout.drawn()) {
                        bulbPiece.buildComponent(startPiece, components, rand);
                        CityGrowth.drain(startPiece, components, rand);
                    }
                }
                finally {
                    CityGrowth.laying = false;
                    CityGrowth.bulbLaying = false;
                }
                bulbs++;
                ContentLog.LOGGER.debug("The dead end at {}, {} rolled a cul-de-sac, {} across at {}, {}", endX, endZ, wide, bulb.minX, bulb.minZ);
            }
        }
        if (bulbs > 0) { ((IStructureStartGrow) held).rdpl$updateBoundingBox(); }
    }

    private static boolean metAtEnd(World world, StructureStart held, StructureComponent piece, boolean alongX, int end, int acrossLeast, int acrossMost) { return ContentStructureSearch.anyVillage(world, held, village -> metIn(village, piece, alongX, end, acrossLeast, acrossMost)); }

    private static boolean metIn(StructureStart village, StructureComponent piece, boolean alongX, int end, int acrossLeast, int acrossMost) {
        for (StructureComponent other : village.getComponents()) {
            if (other == piece || !(other instanceof StructureVillagePieces.Path)) { continue; }
            StructureBoundingBox road = other.getBoundingBox();
            if (BeardPlots.roadAlongX(road) == alongX) { continue; }
            int least = alongX ? road.minX : road.minZ;
            int most = alongX ? road.maxX : road.maxZ;
            if (least - 1 > end || most + 1 < end) { continue; }
            if ((alongX ? road.maxZ : road.maxX) + 1 < acrossLeast || (alongX ? road.minZ : road.minX) - 1 > acrossMost) { continue; }
            ContentLog.LOGGER.debug("The end of the road at {}, {} is a junction with the road at {}, {}, so it is no dead end and grows no cul-de-sac", piece.getBoundingBox().minX, piece.getBoundingBox().minZ, road.minX, road.minZ);
            return true;
        }
        return false;
    }

    private static StructureBoundingBox court(StructureBoundingBox box, boolean alongX, int end, int dir, boolean outward, int center, int half, int stem) {
        int span = stem + 2 * half + 1;
        int lo = end + dir * (outward ? 1 : span);
        int hi = end + dir * (outward ? span : 1);
        return new StructureBoundingBox(
                alongX ? Math.min(lo, hi) : center - half, box.minY, alongX ? center - half : Math.min(lo, hi),
                alongX ? Math.max(lo, hi) : center + half, box.maxY, alongX ? center + half : Math.max(lo, hi));
    }

    @Nullable private static List<StructureComponent> yielding(World world, StructureStart held, StructureComponent piece, StructureBoundingBox bulb) {
        if (ContentStructureSearch.anyOtherOver(world, held.getComponents(), bulb)) { return null; }
        List<StructureComponent> plots = new ArrayList<>();
        for (StructureComponent other : held.getComponents()) {
            if (other == piece || BeardRails.buriedUnder(other, bulb) || !other.getBoundingBox().intersectsWith(bulb.minX, bulb.minZ, bulb.maxX, bulb.maxZ)) { continue; }
            if (other instanceof StructureVillagePieces.Well || other instanceof RailPiece) { return null; }
            if (other instanceof StructureVillagePieces.Path) {
                if (BeardRoads.roadNarrow(other.getBoundingBox(), BeardPlots.roadAlongX(other))) { continue; }
                return null;
            }
            plots.add(other);
        }
        return plots;
    }

    private static void makeWay(World world, StructureStart held, StructureComponent plot, StructureBoundingBox bulb) {
        StructureBoundingBox box = plot.getBoundingBox();
        int[][] pushes = {
                { bulb.maxX + 1 - box.minX, 0 }, { bulb.minX - 1 - box.maxX, 0 },
                { 0, bulb.maxZ + 1 - box.minZ }, { 0, bulb.minZ - 1 - box.maxZ } };
        Arrays.sort(pushes, Comparator.comparingInt(a -> Math.abs(a[0]) + Math.abs(a[1])));
        for (int[] push : pushes) {
            StructureBoundingBox tried = new StructureBoundingBox(box);
            tried.offset(push[0], 0, push[1]);
            if (!standsFree(world, held, plot, tried)) { continue; }
            box.offset(push[0], 0, push[1]);
            ContentLog.LOGGER.debug("{} at {}, {} slides {}, {} out of the cul-de-sac at {}, {}", plot.getClass().getSimpleName(), box.minX, box.minZ, push[0], push[1], bulb.minX, bulb.minZ);
            return;
        }
        held.getComponents().remove(plot);
        ContentLog.LOGGER.debug("{} at {}, {} makes way for the cul-de-sac at {}, {}, having no room to slide out of it", plot.getClass().getSimpleName(), box.minX, box.minZ, bulb.minX, bulb.minZ);
    }

    private static boolean standsFree(World world, StructureStart held, StructureComponent plot, StructureBoundingBox tried) {
        for (StructureComponent other : held.getComponents()) {
            if (other != plot && !BeardRails.buriedUnder(other, tried) && other.getBoundingBox().intersectsWith(tried.minX, tried.minZ, tried.maxX, tried.maxZ)) { return false; }
        }
        return !ContentStructureSearch.anyOtherOver(world, held.getComponents(), tried);
    }

    private static boolean seated(World world, StructureComponent road, boolean alongX, int end, int discX, int discZ, int radius) {
        BeardRoads.Grade grade = BeardRoadsGrade.chainGrade(world, road, alongX);
        int level = grade == null ? Integer.MIN_VALUE : grade.at(end);
        if (level == Integer.MIN_VALUE) { level = BeardSurface.surfaceAt(world, discX, discZ); }
        int reach = radius + CityGrowth.VERGE;
        for (int z = discZ - reach; z <= discZ + reach; z += 2) {
            for (int x = discX - reach; x <= discX + reach; x += 2) {
                if ((x - discX) * (x - discX) + (z - discZ) * (z - discZ) > reach * reach + reach) { continue; }
                int ground = BeardSurface.surfaceAt(world, x, z);
                if (level - ground > BeardGrade.CAP || ground - level > DIG) { return false; }
            }
        }
        return true;
    }

    private static boolean clearFor(World world, StructureStart held, StructureComponent piece, int discX, int discZ, int radius) { return !ContentStructureSearch.anyVillage(world, held, village -> nearDisc(village, piece, discX, discZ, radius)); }

    private static boolean nearDisc(StructureStart village, StructureComponent piece, int discX, int discZ, int radius) {
        for (StructureComponent other : village.getComponents()) {
            if (other == piece) { continue; }
            StructureBoundingBox held = other.getBoundingBox();
            int nearX = MathHelper.clamp(discX, held.minX, held.maxX);
            int nearZ = MathHelper.clamp(discZ, held.minZ, held.maxZ);
            int away = (nearX - discX) * (nearX - discX) + (nearZ - discZ) * (nearZ - discZ);
            if (away <= (radius + 1) * (radius + 1)) { return true; }
        }
        return false;
    }
}
