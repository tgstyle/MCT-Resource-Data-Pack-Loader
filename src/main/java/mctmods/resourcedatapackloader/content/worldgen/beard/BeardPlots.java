package mctmods.resourcedatapackloader.content.worldgen.beard;

import mctmods.resourcedatapackloader.mixin.AccessorStructureComponentBox;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureStart;
import javax.annotation.Nullable;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import java.util.Locale;
import java.util.function.Predicate;

public final class BeardPlots {
    private BeardPlots() {}

    public static boolean roadAlongX(StructureComponent piece) {
        EnumFacing facing = piece.getCoordBaseMode();
        if (facing != null) { return facing.getAxis() == EnumFacing.Axis.X; }

        StructureBoundingBox box = piece.getBoundingBox();
        return box.maxX - box.minX >= box.maxZ - box.minZ;
    }

    public static boolean roadAlongX(StructureBoundingBox box) { return box.maxX - box.minX >= box.maxZ - box.minZ; }

    public static Predicate<BlockPos> outside(World world, StructureStart start, StructureComponent piece, StructureBoundingBox box, boolean inOwn, int top) {
        return spot -> world.isChunkGeneratedAt(spot.getX() >> 4, spot.getZ() >> 4)
                && !insideAnother(start, piece, spot)
                && !(inOwn && spot.getX() >= box.minX && spot.getX() <= box.maxX && spot.getZ() >= box.minZ && spot.getZ() <= box.maxZ && spot.getY() <= top);
    }

    public static boolean waystone(StructureComponent piece) { return piece.getClass().getName().toLowerCase(Locale.ROOT).contains("waystone"); }
    public static boolean insideAnother(StructureStart start, StructureComponent piece, BlockPos at) {
        for (StructureComponent other : start.getComponents()) {
            if (other == piece || other instanceof StructureVillagePieces.Path) { continue; }

            StructureBoundingBox box = other.getBoundingBox();
            if (box.isVecInside(at)) { return true; }
            if (other instanceof StructureVillagePieces.Well && at.getY() == box.maxY + 1 && at.getX() >= box.minX && at.getX() <= box.maxX && at.getZ() >= box.minZ && at.getZ() <= box.maxZ) { return true; }
        }
        return false;
    }
    public static boolean underAnother(StructureStart start, @Nullable StructureComponent piece, int x, int z) {
        for (StructureComponent other : start.getComponents()) {
            if (other == piece) { continue; }

            StructureBoundingBox box = other.getBoundingBox();
            if (x >= box.minX && x <= box.maxX && z >= box.minZ && z <= box.maxZ) { return true; }
        }
        return false;
    }
    public static boolean besideRoad(StructureStart start, StructureComponent piece, int x, int z) { return nearRoad(start, piece, x, z, 1); }

    public static boolean nearRoad(StructureStart start, StructureComponent piece, int x, int z, int reach) {
        for (StructureComponent other : start.getComponents()) {
            if (other == piece || !(other instanceof StructureVillagePieces.Path)) { continue; }

            StructureBoundingBox box = other.getBoundingBox();
            if (x >= box.minX - reach && x <= box.maxX + reach && z >= box.minZ - reach && z <= box.maxZ + reach) { return true; }
        }
        return false;
    }

    public static boolean underRoad(StructureStart start, StructureComponent piece, int x, int z) {
        for (StructureComponent other : start.getComponents()) {
            if (other == piece || !(other instanceof StructureVillagePieces.Path)) { continue; }

            StructureBoundingBox box = other.getBoundingBox();
            if (x >= box.minX && x <= box.maxX && z >= box.minZ && z <= box.maxZ) { return true; }
        }
        return false;
    }
    public static boolean underBuilding(StructureStart start, StructureComponent piece, int x, int z) {
        for (StructureComponent other : start.getComponents()) {
            if (other == piece || other instanceof StructureVillagePieces.Path) { continue; }

            StructureBoundingBox box = other.getBoundingBox();
            if (x >= box.minX && x <= box.maxX && z >= box.minZ && z <= box.maxZ) { return true; }
        }
        return false;
    }
    public static boolean overRoad(StructureStart start, int x, int z) {
        for (StructureComponent other : start.getComponents()) {
            if (!(other instanceof StructureVillagePieces.Path)) { continue; }

            StructureBoundingBox held = ((AccessorStructureComponentBox) other).rdpl$box();
            if (held != null && x >= held.minX && x <= held.maxX && z >= held.minZ && z <= held.maxZ) { return true; }
        }
        return false;
    }
    public static boolean roadCore(StructureStart start, StructureComponent piece, int x, int z) {
        for (StructureComponent other : start.getComponents()) {
            if (other == piece || !(other instanceof StructureVillagePieces.Path)) { continue; }

            StructureBoundingBox box = other.getBoundingBox();
            if (x < box.minX || x > box.maxX || z < box.minZ || z > box.maxZ) { continue; }

            boolean alongX = roadAlongX(other);
            int center = alongX ? (box.minZ + box.maxZ) / 2 : (box.minX + box.maxX) / 2;
            int offset = Math.abs((alongX ? z : x) - center);
            int span = (alongX ? box.maxZ - box.minZ : box.maxX - box.minX) + 1;
            if (offset <= Math.min(1 + BeardRoads.pathExtraWidth(), (span - 1) / 2)) { return true; }
        }
        return false;
    }
    public static int restingFloor(int[] tops, int depth, int spot, int from) {
        int own = tops[spot];
        int floor = own == Integer.MIN_VALUE ? from : own + 1;
        int best = own == Integer.MIN_VALUE ? Integer.MAX_VALUE : from - own;
        int spotX = spot / depth;
        int spotZ = spot % depth;
        for (int i = 0; i < tops.length; i++) {
            if (i == spot || tops[i] == Integer.MIN_VALUE) { continue; }

            int cost = (Math.abs(i / depth - spotX) + Math.abs(i % depth - spotZ)) * 4 + Math.max(0, from - tops[i]);
            if (cost >= best) { continue; }

            best = cost;
            floor = tops[i];
        }
        return floor;
    }
}
