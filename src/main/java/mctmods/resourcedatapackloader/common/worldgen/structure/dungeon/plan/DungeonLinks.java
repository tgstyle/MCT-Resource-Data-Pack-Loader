package mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.plan;

import mctmods.resourcedatapackloader.common.pack.definitions.games.PickDef;
import mctmods.resourcedatapackloader.common.pack.definitions.worlds.DungeonDef;
import mctmods.resourcedatapackloader.common.util.worldgen.SeedStreams;
import mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.plan.DungeonPlan.Level;
import mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.plan.DungeonPlan.Link;
import mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.plan.DungeonPlan.Room;
import mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.plan.DungeonPlan.Segment;
import mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.plan.PlanRecords.Shaft;
import mctmods.resourcedatapackloader.core.util.ContentLog;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import javax.annotation.Nullable;

public final class DungeonLinks {
    private static final String[] SHAFT_NAMES = {"ladder", "spiral", "stairs"};
    private static final int SHAFT_SIDE = 3;
    private static final int[] STEP_X = {1, 0};
    private static final int[] STEP_Z = {0, 1};

    private final Level.Builder level;
    private final DungeonDef def;
    private final long seed;
    private final int q;
    private final int ox;
    private final int oz;
    private final int reach;
    private final int n;
    private final Room[] rooms;
    private final int start;
    private final Map<Long, List<int[]>> pairs = new TreeMap<>();
    private final List<List<Integer>> neighbors = new ArrayList<>();
    private final Set<Long> edges = new LinkedHashSet<>();

    private DungeonLinks(Level.Builder level, DungeonDef def, long seed) {
        this.level = level;
        this.def = def;
        this.seed = seed;
        this.q = level.slotSide();
        this.ox = level.gridX();
        this.oz = level.gridZ();
        this.reach = (def.corridor().width() + 1) / 2 + 1;
        this.n = level.roomCount();
        this.rooms = new Room[n];
        int first = 0;
        for (int i = 0; i < n; i++) {
            rooms[i] = level.room(i);
            neighbors.add(new ArrayList<>());
            if (rooms[i].start) { first = i; }
        }
        this.start = first;
    }

    public static Result link(Level.Builder level, DungeonDef def, long levelSeed, boolean bottom, int nextFloorY) {
        return new DungeonLinks(level, def, levelSeed).run(bottom, nextFloorY);
    }

    private Result run(boolean bottom, int nextFloorY) {
        findAdjacent();
        int bossAt = -1;
        for (int i = 0; i < n; i++) {
            if (rooms[i].boss) { bossAt = i; }
        }
        boolean sealed = bossAt >= 0 && DungeonSlots.BOSS_KIND.equals(rooms[bossAt].kind);
        tree(sealed ? bossAt : -1);
        if (bossAt >= 0 && !sealed) {
            boolean[] eligible = new boolean[n];
            for (int i = 0; i < n; i++) {
                eligible[i] = rooms[i].templateId == null;
            }
            bossAt = farthest(distances(), eligible, "boss");
        }
        loops(bossAt);
        if (sealed) { attachBoss(bossAt); }
        int exit = -1;
        if (!bottom) {
            boolean[] eligible = new boolean[n];
            for (int i = 0; i < n; i++) {
                eligible[i] = rooms[i].templateId == null && i != bossAt;
            }
            exit = farthest(distances(), eligible, "exit");
        }
        emitLinks();
        checkReach();
        for (int i = 0; i < n; i++) {
            boolean node = rooms[i].node || i == exit;
            boolean boss = i == bossAt;
            if (node != rooms[i].node || boss != rooms[i].boss) { level.replaceRoom(i, rooms[i].withFlags(node, boss)); }
        }
        if (exit < 0) { return new Result(null, bossAt); }
        Shaft shaft = shaftIn(rooms[exit], nextFloorY);
        level.shaft(shaft);
        level.exitRoom(exit);
        return new Result(new int[] {shaft.x() + 1, shaft.z() + 1}, bossAt);
    }

    private Random random(String purpose, int index) { return SeedStreams.random(seed, purpose, index); }

    private long key(int a, int b) { return (long) Math.min(a, b) * n + Math.max(a, b); }

    private int low(long key) { return (int) (key / n); }

    private int high(long key) { return (int) (key % n); }

    private static long cellKey(int gx, int gz) { return ((long) gx << 32) ^ (gz & 0xffffffffL); }

    private void findAdjacent() {
        Map<Long, Integer> at = new HashMap<>();
        for (int i = 0; i < n; i++) {
            for (int dx = 0; dx < rooms[i].kx; dx++) {
                for (int dz = 0; dz < rooms[i].kz; dz++) { at.put(cellKey(rooms[i].gx + dx, rooms[i].gz + dz), i); }
            }
        }
        for (int i = 0; i < n; i++) {
            for (int dx = 0; dx < rooms[i].kx; dx++) {
                for (int dz = 0; dz < rooms[i].kz; dz++) {
                    int gx = rooms[i].gx + dx;
                    int gz = rooms[i].gz + dz;
                    for (int s = 0; s < STEP_X.length; s++) {
                        Integer j = at.get(cellKey(gx + STEP_X[s], gz + STEP_Z[s]));
                        if (j == null || j == i) { continue; }
                        int[] cells = i < j ? new int[] {gx, gz, gx + STEP_X[s], gz + STEP_Z[s]} : new int[] {gx + STEP_X[s], gz + STEP_Z[s], gx, gz};
                        pairs.computeIfAbsent(key(i, j), _ -> new ArrayList<>()).add(cells);
                    }
                }
            }
        }
        for (long edge : pairs.keySet()) {
            neighbors.get(low(edge)).add(high(edge));
            neighbors.get(high(edge)).add(low(edge));
        }
    }

    private void tree(int excluded) {
        Random random = random("links", 0);
        boolean[] seen = new boolean[n];
        Deque<Integer> stack = new ArrayDeque<>();
        seen[start] = true;
        stack.push(start);
        while (!stack.isEmpty()) {
            int from = stack.peek();
            List<Integer> open = new ArrayList<>();
            for (int to : neighbors.get(from)) {
                if (!seen[to] && to != excluded) { open.add(to); }
            }
            if (open.isEmpty()) {
                stack.pop();
                continue;
            }
            int to = open.get(random.nextInt(open.size()));
            seen[to] = true;
            edges.add(key(from, to));
            stack.push(to);
        }
    }

    private void loops(int bossAt) {
        List<Long> spare = new ArrayList<>();
        for (long edge : pairs.keySet()) {
            if (!edges.contains(edge) && low(edge) != bossAt && high(edge) != bossAt) { spare.add(edge); }
        }
        Collections.shuffle(spare, random("loops", 0));
        int count = spare.size() * Math.clamp(def.loops(), 0, 100) / 100;
        edges.addAll(spare.subList(0, count));
    }

    private void attachBoss(int bossAt) {
        double bx = (rooms[bossAt].x0 + rooms[bossAt].x1) / 2.0;
        double bz = (rooms[bossAt].z0 + rooms[bossAt].z1) / 2.0;
        Random random = random("boss", 1);
        List<Integer> best = new ArrayList<>();
        double near = Double.MAX_VALUE;
        for (int to : neighbors.get(bossAt)) {
            double dx = (rooms[to].x0 + rooms[to].x1) / 2.0 - bx;
            double dz = (rooms[to].z0 + rooms[to].z1) / 2.0 - bz;
            double distance = dx * dx + dz * dz;
            if (distance < near) {
                near = distance;
                best.clear();
            }
            if (distance == near) { best.add(to); }
        }
        if (!best.isEmpty()) { edges.add(key(bossAt, best.get(random.nextInt(best.size())))); }
    }

    private int[] distances() {
        List<List<Integer>> joined = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            joined.add(new ArrayList<>());
        }
        for (long edge : edges) {
            joined.get(low(edge)).add(high(edge));
            joined.get(high(edge)).add(low(edge));
        }
        int[] dist = new int[n];
        Arrays.fill(dist, -1);
        dist[start] = 0;
        Deque<Integer> queue = new ArrayDeque<>();
        queue.add(start);
        while (!queue.isEmpty()) {
            int from = queue.poll();
            for (int to : joined.get(from)) {
                if (dist[to] < 0) {
                    dist[to] = dist[from] + 1;
                    queue.add(to);
                }
            }
        }
        return dist;
    }

    private int farthest(int[] dist, boolean[] eligible, String purpose) {
        int far = -1;
        List<Integer> ties = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            if (!eligible[i] || dist[i] < 0) { continue; }
            if (dist[i] > far) {
                far = dist[i];
                ties.clear();
            }
            if (dist[i] == far) { ties.add(i); }
        }
        return ties.isEmpty() ? start : ties.get(random(purpose, 0).nextInt(ties.size()));
    }

    private void checkReach() {
        if (!ContentLog.LOGGER.debugEnabled()) { return; }
        int[] dist = distances();
        for (int d : dist) {
            if (d < 0) {
                ContentLog.LOGGER.debug("dungeon links unreachable key={} level={} rooms={}", def.key(), level.index(), n);
                return;
            }
        }
    }

    private void emitLinks() {
        int index = 0;
        for (long edge : edges) {
            level.addLink(corridor(edge, index++));
        }
    }

    private static int lo(Room room, int axis) { return axis == 0 ? room.x0 : room.z0; }

    private static int hi(Room room, int axis) { return axis == 0 ? room.x1 : room.z1; }

    private static int wall(Room room) { return room.templateId != null || DungeonSlots.BOSS_KIND.equals(room.kind) ? 0 : 1; }

    private static int[] point(int axis, int along, int lateral) { return axis == 0 ? new int[] {along, lateral} : new int[] {lateral, along}; }

    private int lateral(Room room, int axis, int strip, Random random) {
        int from = Math.max(lo(room, axis), strip) + reach;
        int to = Math.min(hi(room, axis), strip + q - 1) - reach;
        if (to < from) { return (Math.max(lo(room, axis), strip) + Math.min(hi(room, axis), strip + q - 1)) / 2; }
        return from + random.nextInt(to - from + 1);
    }

    private Link corridor(long edge, int index) {
        int a = low(edge);
        int b = high(edge);
        Room ra = rooms[a];
        Room rb = rooms[b];
        List<int[]> cells = pairs.get(edge);
        int[] cell = cells.get(random("pairs", index).nextInt(cells.size()));
        int p = cell[0] != cell[2] ? 0 : 1;
        int lat = 1 - p;
        boolean aFirst = p == 0 ? cell[0] < cell[2] : cell[1] < cell[3];
        int boundary = (p == 0 ? ox : oz) + (aFirst ? (p == 0 ? cell[2] : cell[3]) : (p == 0 ? cell[0] : cell[1])) * q;
        int strip = (lat == 0 ? ox : oz) + (lat == 0 ? cell[0] : cell[1]) * q;
        Random doors = random("doors", index);
        int latA = lateral(ra, lat, strip, doors);
        int latB = lateral(rb, lat, strip, doors);
        int alongA = aFirst ? hi(ra, p) + wall(ra) : lo(ra, p) - wall(ra);
        int alongB = aFirst ? lo(rb, p) - wall(rb) : hi(rb, p) + wall(rb);
        int[] doorA = point(p, alongA, latA);
        int[] doorB = point(p, alongB, latB);
        int[] endA = point(p, boundary, latA);
        int[] endB = point(p, boundary, latB);
        int floorA = ra.floorY;
        int floorB = rb.floorY;
        int legHigh = Math.abs(boundary - (floorA >= floorB ? alongA : alongB));
        int jogY = Math.max(floorA, floorB) - Math.min(Math.abs(floorA - floorB), legHigh);
        List<Segment> segments = new ArrayList<>();
        segments.add(new Segment(doorA[0], doorA[1], endA[0], endA[1], floorA, jogY));
        if (latA != latB) { segments.add(new Segment(endA[0], endA[1], endB[0], endB[1], jogY, jogY)); }
        segments.add(new Segment(doorB[0], doorB[1], endB[0], endB[1], floorB, jogY));
        return new Link(a, b, doorA, doorB, new int[] {endA[0], endA[1], endB[0], endB[1]}, lat, boundary, def.corridor().width(), def.corridor().height(), floorA, floorB, segments);
    }

    private Shaft shaftIn(Room room, int bottomY) {
        Random random = random("exit", 1);
        int x = shaftSpot(room.x0, room.x1, random);
        int z = shaftSpot(room.z0, room.z1, random);
        String name = PickDef.pick(def.shafts(), random("shaftType", 0), SHAFT_NAMES[PlanRecords.SHAFT_LADDER]);
        int type = Arrays.asList(SHAFT_NAMES).indexOf(name);
        return new Shaft(Math.max(PlanRecords.SHAFT_LADDER, type), x, z, room.floorY, bottomY);
    }

    private static int shaftSpot(int low, int high, Random random) {
        int from = low + 1;
        int to = high - SHAFT_SIDE;
        return to < from ? (low + high) / 2 - 1 : from + random.nextInt(to - from + 1);
    }

    public static final class Result {
        @Nullable public final int[] anchor;
        public final int bossRoom;

        private Result(@Nullable int[] anchor, int bossRoom) {
            this.anchor = anchor;
            this.bossRoom = bossRoom;
        }
    }
}
