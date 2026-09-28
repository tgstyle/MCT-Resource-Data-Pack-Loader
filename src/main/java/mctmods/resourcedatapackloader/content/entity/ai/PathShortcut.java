package mctmods.resourcedatapackloader.content.entity.ai;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.PathNavigationRegion;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.pathfinder.PathfindingContext;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.phys.Vec3;
import java.util.Arrays;
import java.util.EnumSet;

public final class PathShortcut {
    private static final double REACH = 8.0D;
    private static final float AVOIDED = 8.0F;
    private static final int MARGIN = 4;
    private static final int OWNER = System.identityHashCode(PathShortcut.class);
    private static final ThreadLocal<PathShortcut> HELD = ThreadLocal.withInitial(PathShortcut::new);
    private final PathNodeMemo memo = PathNodeMemo.held();
    private final Table columns = new Table();
    private final Table cells = new Table();
    private final BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
    private Mob mob;
    private PathfindingContext context;
    private Vec3 from = Vec3.ZERO;
    private int x;
    private int y;
    private int z;
    private int width;
    private int height;
    private int rail;
    private int world;
    private long tick;
    private boolean busy;

    private PathShortcut() {}

    public static void ahead(Mob mob, Level level, Path path) {
        if (path.isDone()) { return; }
        Vec3 from = mob.position();
        int start = path.getNextNodeIndex();
        int end = path.getNodeCount();
        int floor = Mth.floor(from.y);
        for (int i = start; i < end; i++) {
            if (path.getNode(i).y != floor) {
                end = i;
                break;
            }
        }
        if (end - 1 <= start) { return; }
        PathShortcut scan = HELD.get();
        if (scan.busy) { scan = new PathShortcut(); }
        scan.begin(mob, level, from);
        int found;
        try { found = scan.farthest(path, start, end); }
        finally { scan.finish(); }
        if (found > start) { path.setNextNodeIndex(found); }
    }

    private void begin(Mob mob, Level level, Vec3 from) {
        this.mob = mob;
        this.from = from;
        x = Mth.floor(from.x);
        y = Mth.floor(from.y);
        z = Mth.floor(from.z);
        width = Mth.ceil(mob.getBbWidth());
        height = Mth.ceil(mob.getBbHeight());
        int span = (int) REACH + 2 * width + MARGIN;
        Snapshot snapshot = new Snapshot(level, new BlockPos(x - span, y - MARGIN, z - span), new BlockPos(x + span, y + height + MARGIN, z + span));
        context = new PathfindingContext(snapshot.whole() ? snapshot : level, mob);
        rail = -1;
        world = System.identityHashCode(level);
        tick = level.getGameTime();
        busy = true;
        columns.clear();
        cells.clear();
    }

    private void finish() {
        mob = null;
        context = null;
        busy = false;
    }

    private int farthest(Path path, int start, int end) {
        for (int j = end - 1; j > start; j--) {
            Vec3 to = path.getEntityPosAtNode(mob, j);
            if (to.distanceToSqr(from) <= REACH * REACH && direct(to)) { return j; }
        }
        return start;
    }

    private boolean direct(Vec3 to) {
        double dx = to.x - from.x;
        double dz = to.z - from.z;
        double length = dx * dx + dz * dz;
        if (length < 1.0E-8D) { return false; }
        double scale = 1.0D / Math.sqrt(length);
        dx *= scale;
        dz *= scale;
        return clearLine(to, dx, dz) && !unsafe(x, z, width + 2, dx, dz);
    }

    private boolean clearLine(Vec3 to, double dx, double dz) {
        int atX = x;
        int atZ = z;
        double stepX = 1.0D / Math.abs(dx);
        double stepZ = 1.0D / Math.abs(dz);
        double nextX = atX - from.x;
        double nextZ = atZ - from.z;
        if (dx >= 0.0D) { nextX++; }
        if (dz >= 0.0D) { nextZ++; }
        nextX /= dx;
        nextZ /= dz;
        int signX = dx < 0.0D ? -1 : 1;
        int signZ = dz < 0.0D ? -1 : 1;
        int endX = Mth.floor(to.x);
        int endZ = Mth.floor(to.z);
        int leftX = endX - atX;
        int leftZ = endZ - atZ;
        while (leftX * signX > 0 || leftZ * signZ > 0) {
            if (nextX < nextZ) {
                nextX += stepX;
                atX += signX;
                leftX = endX - atX;
            }
            else {
                nextZ += stepZ;
                atZ += signZ;
                leftZ = endZ - atZ;
            }
            if (unsafe(atX, atZ, width, dx, dz)) { return false; }
        }
        return true;
    }

    private boolean unsafe(int atX, int atZ, int size, double dx, double dz) {
        int minX = atX - size / 2;
        int minZ = atZ - size / 2;
        for (int cx = minX; cx < minX + size; cx++) {
            for (int cz = minZ; cz < minZ + size; cz++) {
                if (inFront(cx, cz, dx, dz) && blocked(cx, cz)) { return true; }
            }
        }
        for (int cx = minX; cx < minX + size; cx++) {
            for (int cz = minZ; cz < minZ + size; cz++) {
                if (inFront(cx, cz, dx, dz) && hazard(cx, cz, size)) { return true; }
            }
        }
        return false;
    }

    private boolean inFront(int atX, int atZ, double dx, double dz) { return (atX + 0.5D - from.x) * dx + (atZ + 0.5D - from.z) * dz >= 0.0D; }

    private boolean blocked(int atX, int atZ) {
        long key = PathNodeMemo.packed(atX, 0, atZ);
        int known = columns.get(key);
        if (known >= 0) { return known == 1; }
        boolean blocked = false;
        for (int atY = y; atY < y + height && !blocked; atY++) { blocked = !context.getBlockState(pos.set(atX, atY, atZ)).isPathfindable(PathComputationType.LAND); }
        columns.put(key, blocked ? 1 : 0);
        return blocked;
    }

    private boolean hazard(int atX, int atZ, int size) {
        long key = PathNodeMemo.packed(atX, size, atZ);
        int known = cells.get(key);
        if (known >= 0) { return known == 1; }
        boolean hazard = dangerous(atX, atZ, size);
        cells.put(key, hazard ? 1 : 0);
        return hazard;
    }

    private boolean dangerous(int atX, int atZ, int size) {
        PathType ground = type(atX, y - 1, atZ, size);
        if (ground == PathType.WATER || ground == PathType.LAVA || ground == PathType.OPEN) { return true; }
        PathType feet = type(atX, y, atZ, size);
        float malus = mob.getPathfindingMalus(feet);
        if (malus < 0.0F || malus >= AVOIDED) { return true; }
        return feet == PathType.DAMAGE_FIRE || feet == PathType.DANGER_FIRE || feet == PathType.DAMAGE_OTHER;
    }

    private PathType type(int atX, int atY, int atZ, int size) {
        EnumSet<PathType> seen = EnumSet.noneOf(PathType.class);
        PathType first = PathType.BLOCKED;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < height; j++) {
                for (int k = 0; k < size; k++) {
                    PathType type = kind(atX + i, atY + j, atZ + k);
                    if (type == PathType.DOOR_WOOD_CLOSED) { type = PathType.WALKABLE; }
                    if (type == PathType.RAIL && offRail()) { type = PathType.FENCE; }
                    if (i == 0 && j == 0 && k == 0) { first = type; }
                    seen.add(type);
                }
            }
        }
        if (seen.contains(PathType.FENCE)) { return PathType.FENCE; }
        PathType worst = PathType.BLOCKED;
        for (PathType type : seen) {
            float malus = mob.getPathfindingMalus(type);
            if (malus < 0.0F) { return type; }
            if (malus >= mob.getPathfindingMalus(worst)) { worst = type; }
        }
        return first == PathType.OPEN && mob.getPathfindingMalus(worst) == 0.0F ? PathType.OPEN : worst;
    }

    private PathType kind(int atX, int atY, int atZ) {
        PathType known = memo.known(OWNER, world, tick, atX, atY, atZ);
        if (known != null) { return known; }
        PathType found = WalkNodeEvaluator.getPathTypeStatic(context, pos.set(atX, atY, atZ));
        memo.remember(OWNER, world, tick, atX, atY, atZ, found);
        return found;
    }

    private boolean offRail() {
        if (rail < 0) {
            BlockPos at = mob.blockPosition();
            rail = context.getBlockState(at).getBlock() instanceof BaseRailBlock || context.getBlockState(at.below()).getBlock() instanceof BaseRailBlock ? 0 : 1;
        }
        return rail == 1;
    }

    private static final class Snapshot extends PathNavigationRegion {
        private Snapshot(Level level, BlockPos start, BlockPos end) { super(level, start, end); }

        private boolean whole() {
            for (ChunkAccess[] row : chunks) {
                if (Arrays.asList(row).contains(null)) { return false; }
            }
            return true;
        }
    }

    private static final class Table {
        private static final int BITS = 11;
        private static final int SLOTS = 1 << BITS;
        private static final int MASK = SLOTS - 1;
        private static final int LIMIT = SLOTS * 3 / 4;
        private static final long SPREAD = 0x9E3779B97F4A7C15L;
        private final long[] keys = new long[SLOTS];
        private final int[] stamps = new int[SLOTS];
        private final byte[] values = new byte[SLOTS];
        private int stamp;
        private int used;

        private static int slot(long key) { return (int) ((key * SPREAD) >>> (64 - BITS)); }

        private void clear() {
            used = 0;
            if (++stamp == 0) {
                Arrays.fill(stamps, 0);
                stamp = 1;
            }
        }

        private int get(long key) {
            for (int slot = slot(key); stamps[slot] == stamp; slot = (slot + 1) & MASK) {
                if (keys[slot] == key) { return values[slot]; }
            }
            return -1;
        }

        private void put(long key, int value) {
            if (used >= LIMIT) { return; }
            int slot = slot(key);
            while (stamps[slot] == stamp) { slot = (slot + 1) & MASK; }
            keys[slot] = key;
            stamps[slot] = stamp;
            values[slot] = (byte) value;
            used++;
        }
    }
}
