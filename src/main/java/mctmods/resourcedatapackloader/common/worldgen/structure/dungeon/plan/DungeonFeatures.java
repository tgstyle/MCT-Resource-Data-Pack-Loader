package mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.plan;

import mctmods.resourcedatapackloader.common.pack.definitions.worlds.DungeonDef;
import mctmods.resourcedatapackloader.common.pack.definitions.worlds.DungeonDef.Degree;
import mctmods.resourcedatapackloader.common.pack.definitions.worlds.RoomKindDef;
import mctmods.resourcedatapackloader.common.util.worldgen.SeedStreams;
import mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.plan.DungeonPlan.Level;
import mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.plan.DungeonPlan.Link;
import mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.plan.DungeonPlan.Room;
import mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.plan.PlanRecords.Feature;
import mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.plan.PlanRecords.Shaft;
import mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.write.DungeonShapes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public final class DungeonFeatures {
    private static final int OUTSIDE = DungeonDef.ROLLS.indexOf("outside");
    private static final int LIQUIDS = DungeonDef.ROLLS.indexOf("liquids");
    private static final int SUBROOMS = DungeonDef.ROLLS.indexOf("subrooms");
    private static final int ISLANDS = DungeonDef.ROLLS.indexOf("islands");
    private static final int PILLARS = DungeonDef.ROLLS.indexOf("pillars");
    private static final int SYMMETRY = DungeonDef.ROLLS.indexOf("symmetry");
    private static final int VARIABILITY = DungeonDef.ROLLS.indexOf("variability");
    private static final int COMPLEXITY = DungeonDef.ROLLS.indexOf("complexity");
    private static final int FENCES = DungeonDef.ROLLS.indexOf("fences");
    private static final int NATURALS = DungeonDef.ROLLS.indexOf("naturals");
    private static final int MAX_FEATURES = 5;
    private static final int FULL_AREA = 100;
    public static final int PIT_DEPTH = 2;
    private static final int SHAFT_SIDE = 3;
    private static final int ANCHOR_REACH = 2;
    private static final int VARIANT_SLOTS = 3;
    private static final int TRIES = 4;
    private static final int MIN_PARTITION_SIDE = 7;
    private static final int MIN_PILLAR_SIDE = 5;
    private static final int GAP = 2;

    private DungeonFeatures() {}

    static void features(Level.Builder level, List<RoomKindDef> kinds, long levelSeed, boolean topLevel, int surfaceY) {
        Map<String, RoomKindDef> byName = new HashMap<>();
        for (RoomKindDef kind : kinds) {
            byName.put(kind.kind, kind);
        }
        for (int i = 0; i < level.roomCount(); i++) {
            Room room = level.room(i);
            if (room.templateId != null || room.boss) { continue; }
            Placer placer = new Placer(level, room, rollsOf(level, room, byName.get(room.kind)), SeedStreams.random(levelSeed, "features", room.index), surfaceY);
            placer.zones(level);
            placer.shapeFeatures(byName.get(room.kind));
            placer.extras();
            if (topLevel && !room.entrance) { placer.sinkhole(); }
        }
    }

    private static int[] rollsOf(Level.Builder level, Room room, RoomKindDef kind) {
        int[] rolls = level.rolls();
        if (kind == null) { return rolls; }
        for (int i = 0; i < rolls.length; i++) {
            Degree degree = kind.rolls.get(DungeonDef.ROLLS.get(i));
            if (degree != null && degree.valid()) { rolls[i] = draw(degree, SeedStreams.random(room.seed, "kindrolls", i)); }
        }
        return rolls;
    }

    private static int draw(Degree degree, Random random) {
        int total = 0;
        for (int weight : degree.weights) {
            total += Math.max(0, weight);
        }
        int roll = random.nextInt(total);
        for (int i = 0; i < degree.weights.length; i++) {
            roll -= Math.max(0, degree.weights[i]);
            if (roll < 0) { return i; }
        }
        return 0;
    }

    private static final class Placer {
        private final Level.Builder level;
        private final Room room;
        private final int[] rolls;
        private final Random random;
        private final int surfaceY;
        private final int base;
        private final List<int[]> zones = new ArrayList<>();
        private final List<int[]> placed = new ArrayList<>();

        private Placer(Level.Builder level, Room room, int[] rolls, Random random, int surfaceY) {
            this.level = level;
            this.room = room;
            this.rolls = rolls;
            this.random = random;
            this.surfaceY = surfaceY;
            this.base = room.y0;
        }

        private boolean hit(int roll) { return random.nextInt(100) < Degree.CHANCES[rolls[roll]]; }

        private int slot() { return hit(VARIABILITY) ? 1 + random.nextInt(VARIANT_SLOTS) : 0; }

        private void zones(Level.Builder level) {
            Shaft shaft = level.shaft();
            if (shaft != null) { zones.add(new int[] {shaft.x - 1, shaft.z - 1, shaft.x + SHAFT_SIDE, shaft.z + SHAFT_SIDE}); }
            if (room.start) { zones.add(new int[] {level.anchorX() - ANCHOR_REACH, level.anchorZ() - ANCHOR_REACH, level.anchorX() + ANCHOR_REACH, level.anchorZ() + ANCHOR_REACH}); }
            for (Link link : level.links()) {
                int pad = 1 + link.width / 2;
                if (link.roomA == room.index) { zones.add(new int[] {link.doorAX - pad, link.doorAZ - pad, link.doorAX + pad, link.doorAZ + pad}); }
                if (link.roomB == room.index) { zones.add(new int[] {link.doorBX - pad, link.doorBZ - pad, link.doorBX + pad, link.doorBZ + pad}); }
            }
        }

        private boolean free(int[] rect) {
            for (int[] zone : zones) {
                if (overlaps(zone, rect)) { return false; }
            }
            for (int[] other : placed) {
                if (overlaps(other, rect)) { return false; }
            }
            return true;
        }

        private static boolean overlaps(int[] a, int[] b) { return a[0] <= b[2] && b[0] <= a[2] && a[1] <= b[3] && b[1] <= a[3]; }

        private void add(int type, int[] rect, int y0, int y1, int slot, boolean register) {
            level.addFeature(room.index, new Feature(type, rect[0], y0, rect[1], rect[2], y1, rect[3], slot, random.nextInt()));
            if (register) { placed.add(rect); }
        }

        private int[] sub(int[] bounds, int minSide, boolean needFree) {
            int inset = 1;
            int iw = bounds[2] - bounds[0] + 1 - 2 * inset;
            int id = bounds[3] - bounds[1] + 1 - 2 * inset;
            if (iw < minSide || id < minSide) { return null; }
            for (int t = 0; t < TRIES; t++) {
                int w = minSide + random.nextInt(Math.max(minSide, iw / 2) - minSide + 1);
                int d = minSide + random.nextInt(Math.max(minSide, id / 2) - minSide + 1);
                int x = bounds[0] + inset + random.nextInt(iw - w + 1);
                int z = bounds[1] + inset + random.nextInt(id - d + 1);
                int[] rect = {x, z, x + w - 1, z + d - 1};
                if (!needFree || free(rect)) { return rect; }
            }
            return null;
        }

        private int[] interior() { return new int[] {room.x0, room.z0, room.x1, room.z1}; }

        private void shapeFeatures(RoomKindDef kind) {
            String shape = room.shape;
            boolean plainHall = kind != null && RoomKindDef.HALL.equals(kind.kind) && "box".equals(shape);
            if (plainHall && hit(NATURALS)) {
                add(PlanRecords.FEATURE_CAVE, interior(), room.y0, room.y1, slot(), false);
                return;
            }
            pit();
            placed.addAll(DungeonShapes.shapeRects(room));
        }

        private void pit() {
            int[] rect = DungeonShapes.pitRect(room);
            if (rect == null || !free(rect)) { return; }
            int slot = slot();
            if (hit(LIQUIDS)) { add(PlanRecords.FEATURE_POOL, rect, base - PIT_DEPTH, base - 1, slot, false); }
            if (hit(FENCES)) { add(PlanRecords.FEATURE_FENCE, rect, base, base, slot, false); }
            for (int t = 0; t < TRIES; t++) {
                if (!hit(ISLANDS)) { continue; }
                int[] island = sub(rect, 2, false);
                if (island != null) { add(PlanRecords.FEATURE_ISLAND, island, base - PIT_DEPTH, base - 1, slot(), false); }
            }
        }

        private int count() {
            int chance = Degree.CHANCES[rolls[COMPLEXITY]];
            int area = (room.x1 - room.x0 + 1) * (room.z1 - room.z0 + 1);
            return (chance * MAX_FEATURES * Math.min(area, FULL_AREA) + FULL_AREA * FULL_AREA / 2) / (FULL_AREA * FULL_AREA);
        }

        private void extras() {
            if ("cave".equals(room.shape)) { return; }
            int n = count();
            for (int k = 0; k < n; k++) {
                switch (random.nextInt(3)) {
                    case 0:
                        if (hit(PILLARS)) { pillars(); }
                        break;
                    case 1:
                        if (hit(LIQUIDS) && !room.entrance) { pool(); }
                        break;
                    default:
                        if (hit(SUBROOMS)) { partition(); }
                        break;
                }
            }
        }

        private void pillars() {
            int[] rect = sub(interior(), MIN_PILLAR_SIDE, true);
            if (rect != null) { mirrored(PlanRecords.FEATURE_PILLARS, rect, base, room.y1); }
        }

        private void pool() {
            int[] rect = sub(interior(), 2, true);
            if (rect != null) { mirrored(PlanRecords.FEATURE_POOL, rect, base - 1, base - 1); }
        }

        private void mirrored(int type, int[] rect, int y0, int y1) {
            int slot = slot();
            add(type, rect, y0, y1, slot, true);
            if (!hit(SYMMETRY)) { return; }
            int[] twin = random.nextBoolean() ? new int[] {room.x0 + room.x1 - rect[2], rect[1], room.x0 + room.x1 - rect[0], rect[3]} : new int[] {rect[0], room.z0 + room.z1 - rect[3], rect[2], room.z0 + room.z1 - rect[1]};
            if (free(twin)) { add(type, twin, y0, y1, slot, true); }
        }

        private void partition() {
            int width = room.x1 - room.x0 + 1;
            int depth = room.z1 - room.z0 + 1;
            boolean alongZ = width >= depth;
            int length = alongZ ? depth : width;
            int span = alongZ ? width : depth;
            if (span < MIN_PARTITION_SIDE || length < GAP + 2) { return; }
            int at = 2 + random.nextInt(span - 4);
            int gap = 1 + random.nextInt(length - GAP - 1);
            int[][] parts = new int[2][];
            parts[0] = wall(alongZ, at, 0, gap - 1);
            parts[1] = wall(alongZ, at, gap + GAP, length - 1);
            for (int[] part : parts) {
                if (part != null && !free(part)) { return; }
            }
            int slot = slot();
            for (int[] part : parts) {
                if (part != null) { add(PlanRecords.FEATURE_PARTITION, part, base, room.y1, slot, true); }
            }
        }

        private int[] wall(boolean alongZ, int at, int from, int to) {
            if (to < from) { return null; }
            return alongZ ? new int[] {room.x0 + at, room.z0 + from, room.x0 + at, room.z0 + to} : new int[] {room.x0 + from, room.z0 + at, room.x0 + to, room.z0 + at};
        }

        private void sinkhole() {
            if (!hit(OUTSIDE)) { return; }
            int[] rect = sub(interior(), SHAFT_SIDE, true);
            if (rect == null) { return; }
            int slot = slot();
            add(PlanRecords.FEATURE_SINKHOLE, rect, room.y1, surfaceY, slot, true);
            if (hit(FENCES)) { add(PlanRecords.FEATURE_FENCE, rect, room.y1, room.y1, slot, false); }
        }
    }
}
