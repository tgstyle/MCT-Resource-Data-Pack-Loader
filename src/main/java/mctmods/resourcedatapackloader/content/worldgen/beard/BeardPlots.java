package mctmods.resourcedatapackloader.content.worldgen.beard;

import mctmods.resourcedatapackloader.content.worldgen.ContentBeard;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureStart;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.Predicate;
import javax.annotation.Nullable;

public final class BeardPlots {
    private static final StructureComponent[] NONE = new StructureComponent[0];
    private static final int ANY = 0;
    private static final int ROADS = 1;
    private static final int BUILDINGS = 2;
    private static final Map<StructureStart, Index> INDEXES = new WeakHashMap<>();
    @Nullable private static List<StructureComponent> layingOut;
    @Nullable private static Index layout;
    private static WeakReference<List<StructureComponent>> wellsFrom = new WeakReference<>(null);
    private static int wellsCount;
    private static final List<StructureBoundingBox> WELLS = new ArrayList<>();

    private BeardPlots() {}

    private static final class Index {
        private final int count;
        @Nullable private final StructureComponent last;
        private final int cellX;
        private final int cellZ;
        private final int wide;
        private final int deep;
        private final StructureComponent[][] cells;

        private Index(List<StructureComponent> components) {
            count = components.size();
            last = count == 0 ? null : components.get(count - 1);
            int minX = Integer.MAX_VALUE;
            int minZ = Integer.MAX_VALUE;
            int maxX = Integer.MIN_VALUE;
            int maxZ = Integer.MIN_VALUE;
            for (StructureComponent piece : components) {
                StructureBoundingBox box = piece.getBoundingBox();
                minX = Math.min(minX, box.minX);
                minZ = Math.min(minZ, box.minZ);
                maxX = Math.max(maxX, box.maxX);
                maxZ = Math.max(maxZ, box.maxZ);
            }
            if (minX > maxX) {
                cellX = cellZ = wide = deep = 0;
                cells = new StructureComponent[0][];
                return;
            }
            cellX = minX >> 4;
            cellZ = minZ >> 4;
            wide = (maxX >> 4) - cellX + 1;
            deep = (maxZ >> 4) - cellZ + 1;
            int[] counts = new int[wide * deep];
            for (StructureComponent piece : components) {
                StructureBoundingBox box = piece.getBoundingBox();
                for (int cx = (box.minX >> 4) - cellX; cx <= (box.maxX >> 4) - cellX; cx++) {
                    for (int cz = (box.minZ >> 4) - cellZ; cz <= (box.maxZ >> 4) - cellZ; cz++) { counts[cx * deep + cz]++; }
                }
            }
            cells = new StructureComponent[wide * deep][];
            for (int i = 0; i < cells.length; i++) { cells[i] = counts[i] == 0 ? NONE : new StructureComponent[counts[i]]; }
            int[] filled = new int[wide * deep];
            for (StructureComponent piece : components) {
                StructureBoundingBox box = piece.getBoundingBox();
                for (int cx = (box.minX >> 4) - cellX; cx <= (box.maxX >> 4) - cellX; cx++) {
                    for (int cz = (box.minZ >> 4) - cellZ; cz <= (box.maxZ >> 4) - cellZ; cz++) {
                        int cell = cx * deep + cz;
                        cells[cell][filled[cell]++] = piece;
                    }
                }
            }
        }

        private StructureComponent[] at(int x, int z) {
            int cx = (x >> 4) - cellX;
            int cz = (z >> 4) - cellZ;
            if (cx < 0 || cx >= wide || cz < 0 || cz >= deep) { return NONE; }
            return cells[cx * deep + cz];
        }

        private boolean stale(List<StructureComponent> components) { return count != components.size() || last != (components.isEmpty() ? null : components.get(components.size() - 1)); }
    }

    private static Index index(StructureStart start) {
        List<StructureComponent> components = start.getComponents();
        Index held = INDEXES.get(start);
        if (held != null && !held.stale(components)) { return held; }
        held = new Index(components);
        INDEXES.put(start, held);
        return held;
    }

    private static StructureComponent[] around(StructureStart start, int x, int z) { return index(start).at(x, z); }

    public static boolean collides(List<StructureComponent> placed, StructureBoundingBox box) {
        Index held = layout;
        if (held == null || layingOut != placed || held.stale(placed)) {
            held = new Index(placed);
            layout = held;
            layingOut = placed;
        }
        for (int cx = box.minX >> 4; cx <= box.maxX >> 4; cx++) {
            for (int cz = box.minZ >> 4; cz <= box.maxZ >> 4; cz++) {
                for (StructureComponent other : held.at(cx << 4, cz << 4)) {
                    if (other.getBoundingBox().intersectsWith(box.minX, box.minZ, box.maxX, box.maxZ)) { return true; }
                }
            }
        }
        return false;
    }

    public static boolean roadAlongX(StructureComponent piece) {
        EnumFacing facing = piece.getCoordBaseMode();
        if (facing != null) { return facing.getAxis() == EnumFacing.Axis.X; }
        StructureBoundingBox box = piece.getBoundingBox();
        return box.maxX - box.minX >= box.maxZ - box.minZ;
    }

    public static boolean roadAlongX(StructureBoundingBox box) {
        int spanX = box.maxX - box.minX + 1;
        int spanZ = box.maxZ - box.minZ + 1;
        int full = BeardRoads.pathFullWidth();
        if (spanX == full && spanZ != full) { return false; }
        if (spanZ == full && spanX != full) { return true; }
        return spanX >= spanZ;
    }

    public static Predicate<BlockPos> outside(World world, StructureStart start, StructureComponent piece, StructureBoundingBox box, boolean inOwn, int top) {
        return spot -> world.isChunkGeneratedAt(spot.getX() >> 4, spot.getZ() >> 4)
                && !insideAnother(start, piece, spot)
                && !(inOwn && spot.getX() >= box.minX && spot.getX() <= box.maxX && spot.getZ() >= box.minZ && spot.getZ() <= box.maxZ && spot.getY() <= top);
    }

    public static boolean waystone(StructureComponent piece) { return piece.getClass().getName().toLowerCase(Locale.ROOT).contains("waystone"); }

    public static boolean insideAnother(StructureStart start, StructureComponent piece, BlockPos at) {
        for (StructureComponent other : around(start, at.getX(), at.getZ())) {
            if (other == piece || other instanceof StructureVillagePieces.Path) { continue; }
            StructureBoundingBox box = other.getBoundingBox();
            if (box.isVecInside(at)) { return true; }
            if (other instanceof StructureVillagePieces.Well && at.getY() == box.maxY + 1 && at.getX() >= box.minX && at.getX() <= box.maxX && at.getZ() >= box.minZ && at.getZ() <= box.maxZ) { return true; }
        }
        return false;
    }

    public static boolean underAnother(StructureStart start, @Nullable StructureComponent piece, int x, int z) { return hit(start, piece, x, z, ANY); }

    private static boolean hit(StructureStart start, @Nullable StructureComponent piece, int x, int z, int kind) {
        for (StructureComponent other : around(start, x, z)) {
            if (other == piece) { continue; }
            boolean road = other instanceof StructureVillagePieces.Path;
            if (kind == ROADS && !road) { continue; }
            if (kind == BUILDINGS && road) { continue; }
            StructureBoundingBox box = other.getBoundingBox();
            if (x >= box.minX && x <= box.maxX && z >= box.minZ && z <= box.maxZ) { return true; }
        }
        return false;
    }

    public static boolean besideRoad(StructureStart start, StructureComponent piece, int x, int z) { return nearRoad(start, piece, x, z, 1); }

    public static boolean nearRoad(StructureStart start, StructureComponent piece, int x, int z, int reach) {
        Index held = index(start);
        for (int cx = (x - reach) >> 4; cx <= (x + reach) >> 4; cx++) {
            for (int cz = (z - reach) >> 4; cz <= (z + reach) >> 4; cz++) {
                for (StructureComponent other : held.at(cx << 4, cz << 4)) {
                    if (other == piece || !(other instanceof StructureVillagePieces.Path)) { continue; }
                    StructureBoundingBox box = other.getBoundingBox();
                    if (x >= box.minX - reach && x <= box.maxX + reach && z >= box.minZ - reach && z <= box.maxZ + reach) { return true; }
                }
            }
        }
        return false;
    }

    public static boolean underRoad(StructureStart start, StructureComponent piece, int x, int z) { return hit(start, piece, x, z, ROADS); }

    public static boolean underBuilding(StructureStart start, StructureComponent piece, int x, int z) { return hit(start, piece, x, z, BUILDINGS); }

    public static boolean overRoad(StructureStart start, int x, int z) { return hit(start, null, x, z, ROADS); }

    public static List<StructureBoundingBox> wellBoxes(@Nullable List<StructureComponent> pieces) {
        if (pieces == null) { return new ArrayList<>(); }
        return new ArrayList<>(wells(pieces));
    }

    private static List<StructureBoundingBox> wells(List<StructureComponent> pieces) {
        if (pieces != wellsFrom.get() || pieces.size() != wellsCount) {
            WELLS.clear();
            if (!pieces.isEmpty() && pieces.get(0) instanceof StructureVillagePieces.Start) {
                for (StructureComponent piece : pieces) {
                    if (piece instanceof StructureVillagePieces.Well) { WELLS.add(piece.getBoundingBox()); }
                }
            }
            wellsFrom = new WeakReference<>(pieces);
            wellsCount = pieces.size();
        }
        return WELLS;
    }

    public static List<StructureBoundingBox> plazaSquares(@Nullable List<StructureComponent> pieces) {
        List<StructureBoundingBox> squares = wellBoxes(pieces);
        int reach = ContentBeard.plazaReach();
        for (int i = 0; i < squares.size(); i++) {
            StructureBoundingBox well = squares.get(i);
            squares.set(i, new StructureBoundingBox(well.minX - reach, well.minY, well.minZ - reach, well.maxX + reach, well.maxY, well.maxZ + reach));
        }
        return squares;
    }

    public static boolean insidePlaza(@Nullable List<StructureComponent> pieces, int x, int z) {
        if (pieces == null) { return false; }
        int reach = ContentBeard.plazaReach();
        for (StructureBoundingBox well : wells(pieces)) {
            if (x >= well.minX - reach && x <= well.maxX + reach && z >= well.minZ - reach && z <= well.maxZ + reach) { return true; }
        }
        return false;
    }

    public static boolean roadLine(StructureStart start, StructureComponent piece, int x, int z) {
        for (StructureComponent other : around(start, x, z)) {
            if (other == piece || !(other instanceof StructureVillagePieces.Path)) { continue; }
            StructureBoundingBox box = other.getBoundingBox();
            if (x < box.minX || x > box.maxX || z < box.minZ || z > box.maxZ) { continue; }
            boolean alongX = roadAlongX(other);
            int offset = roadOffset(box, alongX, x, z);
            int span = roadSpan(box, alongX);
            int core = coreOf(span);
            if (offset > core && offset <= core + BeardRoads.pathLineColumns() && offset <= (span - 1) / 2) { return true; }
        }
        return false;
    }

    public static boolean roadCore(StructureStart start, StructureComponent piece, int x, int z) {
        for (StructureComponent other : around(start, x, z)) {
            if (other == piece || !(other instanceof StructureVillagePieces.Path)) { continue; }
            StructureBoundingBox box = other.getBoundingBox();
            if (x < box.minX || x > box.maxX || z < box.minZ || z > box.maxZ) { continue; }
            boolean alongX = roadAlongX(other);
            if (roadOffset(box, alongX, x, z) <= coreOf(roadSpan(box, alongX))) { return true; }
        }
        return false;
    }

    private static int roadOffset(StructureBoundingBox box, boolean alongX, int x, int z) {
        int center = alongX ? (box.minZ + box.maxZ) / 2 : (box.minX + box.maxX) / 2;
        return Math.abs((alongX ? z : x) - center);
    }

    static int roadSpan(StructureBoundingBox box, boolean alongX) { return (alongX ? box.maxZ - box.minZ : box.maxX - box.minX) + 1; }

    static int coreOf(int span) { return Math.min(1 + BeardRoads.pathExtraWidth(), (span - 1) / 2); }

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
