package mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.write;

import mctmods.resourcedatapackloader.common.blocks.ContentStates;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.BlockWeightDef;
import mctmods.resourcedatapackloader.common.pack.definitions.worlds.DungeonDef;
import mctmods.resourcedatapackloader.common.pack.definitions.worlds.DungeonThemeDef;
import mctmods.resourcedatapackloader.common.util.world.Coords;
import mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.ContentDungeonThemes;
import mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.plan.DungeonFeatures;
import mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.plan.DungeonPlan;
import mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.plan.DungeonPlan.Level;
import mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.plan.DungeonPlan.Link;
import mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.plan.DungeonPlan.Room;
import mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.plan.DungeonPlan.Segment;
import mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.plan.PlanRecords;
import mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.plan.PlanRecords.Entrance;
import mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.plan.PlanRecords.Feature;
import mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.plan.PlanRecords.Shaft;
import mctmods.resourcedatapackloader.core.util.Registries;
import mctmods.resourcedatapackloader.core.util.math.MathUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockStoneSlab;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public final class DungeonShapes {
    private static final int SLOT_SALT = 7;
    private static final int RANK_SHELL = 1;
    private static final int RANK_AIR = 2;
    private static final int RANK_LIQUID = 3;
    private static final int RANK_SOLID = 4;
    private static final int TUNNEL_DEPTH = 4;
    private static final int NOISE_CELL = 4;
    private static final int NOISE_RANGE = 201;
    private static final int VARIANT_SLOTS = 3;
    private static final int ENTRANCE_KEEP = 3;
    private static final int STAND_AIR = 2;
    private static final int MIN_PILLAR_SIDE = 5;
    private static final int MIN_CORE_SIDE = 3;
    private static final int VARIABILITY = DungeonDef.ROLLS.indexOf("variability");
    private static final int[][] NEIGHBORS = {{1, 0, 0}, {-1, 0, 0}, {0, 1, 0}, {0, -1, 0}, {0, 0, 1}, {0, 0, -1}};

    private DungeonShapes() {}

    @Nullable public static IBlockState at(DungeonPlan plan, DungeonPlan.Bucket bucket, int x, int y, int z, Palette palette) {
        Hit hit = new Hit();
        for (Room room : bucket.rooms) {
            room(plan, bucket, room, x, y, z, palette.of(room), hit);
        }
        for (Link link : bucket.links) {
            corridor(bucket, link, x, y, z, palette.of(link), hit);
        }
        for (Shaft shaft : bucket.shafts) {
            shaft(shaft, x, y, z, palette.of(shaft), hit);
        }
        return hit.state;
    }

    public static boolean isFloorCell(Room room, List<Feature> features, int x, int z) {
        if (!room.contains(x, z)) { return false; }
        if (isCave(room, features)) {
            int top = Math.min(room.floorY + STAND_AIR, room.y1);
            for (int y = room.floorY; y <= top; y++) {
                if (!caveInside(room, x, y, z)) { return false; }
            }
        }
        if (solidShape(room, x, z)) { return false; }
        boolean island = onIsland(features, x, z);
        if (!island && has(pitRect(room), x, z)) { return false; }
        for (Feature f : features) {
            if (!inRect(f, x, z)) { continue; }
            switch (f.type) {
                case PlanRecords.FEATURE_PILLARS:
                    if (onGrid(f.x0, f.z0, f.seed & 1, x, z)) { return false; }
                    break;
                case PlanRecords.FEATURE_POOL:
                    if (!island) { return false; }
                    break;
                case PlanRecords.FEATURE_ISLAND:
                case PlanRecords.FEATURE_CAVE:
                    break;
                default:
                    return false;
            }
        }
        return true;
    }

    public static List<int[]> shapeRects(Room room) {
        List<int[]> rects = new ArrayList<>();
        addRect(rects, pillarRect(room));
        addRect(rects, coreRect(room));
        rects.addAll(cornerRects(room));
        addRect(rects, pitRect(room));
        return rects;
    }

    private static void addRect(List<int[]> rects, @Nullable int[] rect) {
        if (rect != null) { rects.add(rect); }
    }

    private static boolean unshaped(Room room) { return room.templateId != null || room.boss; }

    private static boolean notShape(Room room, String shape) { return unshaped(room) || !shape.equals(room.shape); }

    @Nullable public static int[] pillarRect(Room room) {
        return notShape(room, "pillared") ? null : inset(room, 1, MIN_PILLAR_SIDE);
    }

    @Nullable private static int[] inset(Room room, int walk, int minSide) {
        if (room.x1 - room.x0 + 1 - 2 * walk < minSide || room.z1 - room.z0 + 1 - 2 * walk < minSide) { return null; }
        return new int[] {room.x0 + walk, room.z0 + walk, room.x1 - walk, room.z1 - walk};
    }

    @Nullable public static int[] coreRect(Room room) {
        if (notShape(room, "ring") || room.entrance) { return null; }
        int[] walks = {2 + (int) ((room.seed >>> 1) & 1), 2};
        for (int walk : walks) {
            int[] rect = inset(room, walk, MIN_CORE_SIDE);
            if (rect != null) { return rect; }
        }
        return null;
    }

    public static List<int[]> cornerRects(Room room) {
        if (notShape(room, "cross") || room.entrance) { return Collections.emptyList(); }
        int cw = Math.max(1, (room.x1 - room.x0 + 1) / 3);
        int cd = Math.max(1, (room.z1 - room.z0 + 1) / 3);
        List<int[]> corners = new ArrayList<>();
        for (int sx = 0; sx < 2; sx++) {
            for (int sz = 0; sz < 2; sz++) {
                int x = sx == 0 ? room.x0 : room.x1 - cw + 1;
                int z = sz == 0 ? room.z0 : room.z1 - cd + 1;
                corners.add(new int[] {x, z, x + cw - 1, z + cd - 1});
            }
        }
        return corners;
    }

    @Nullable public static int[] pitRect(Room room) {
        return notShape(room, "pit") || room.entrance ? null : inset(room, 2, MIN_CORE_SIDE);
    }

    private static boolean has(@Nullable int[] rect, int x, int z) { return rect != null && x >= rect[0] && x <= rect[2] && z >= rect[1] && z <= rect[3]; }

    private static boolean solidShape(Room room, int x, int z) {
        int[] pillars = pillarRect(room);
        if (has(pillars, x, z) && onGrid(pillars[0], pillars[1], (int) (room.seed & 1), x, z)) { return true; }
        if (has(coreRect(room), x, z)) { return true; }
        for (int[] corner : cornerRects(room)) {
            if (has(corner, x, z)) { return true; }
        }
        return false;
    }

    private static boolean onGrid(int x0, int z0, int bit, int x, int z) {
        int step = 3 + bit;
        return (x - x0) % step == step / 2 && (z - z0) % step == step / 2;
    }

    private static boolean onIsland(List<Feature> features, int x, int z) {
        for (Feature f : features) {
            if (f.type == PlanRecords.FEATURE_ISLAND && inRect(f, x, z)) { return true; }
        }
        return false;
    }

    private static void room(DungeonPlan plan, DungeonPlan.Bucket bucket, Room room, int x, int y, int z, Palette.Kit palette, Hit hit) {
        if (room.templateId != null || x < room.x0 - 1 || x > room.x1 + 1 || z < room.z0 - 1 || z > room.z1 + 1) { return; }
        int slot = roomSlot(plan, room);
        if (isCave(room, room.features)) { cave(plan, room, x, y, z, palette, slot, hit); }
        else {
            box(room, x, y, z, palette, slot, hit);
            shape(plan, bucket, room, x, y, z, palette, slot, hit);
        }
        for (Feature f : room.features) {
            feature(f, x, y, z, palette, hit);
        }
    }

    private static void box(Room room, int x, int y, int z, Palette.Kit palette, int slot, Hit hit) {
        if (y < room.y0 - 1 || y > room.y1 + 1) { return; }
        boolean inside = x >= room.x0 && x <= room.x1 && z >= room.z0 && z <= room.z1 && y >= room.y0 && y <= room.y1;
        if (inside) { hit.offer(RANK_AIR, palette.state(Palette.AIR, 0)); }
        else { hit.offer(RANK_SHELL, palette.state(surface(y, room.y0, room.y1), slot)); }
    }

    private static void shape(DungeonPlan plan, DungeonPlan.Bucket bucket, Room room, int x, int y, int z, Palette.Kit palette, int slot, Hit hit) {
        int[] pit = pitRect(room);
        if (pit != null) {
            pit(plan, bucket, room, pit, x, y, z, palette, slot, hit);
            return;
        }
        if (y < room.y0 || y > room.y1 || !room.contains(x, z) || !solidShape(room, x, z) || kept(plan, bucket, room, x, z)) { return; }
        hit.offer(RANK_SOLID, palette.state(pillarRect(room) != null ? Palette.PILLARS : Palette.WALLS, slot));
    }

    private static void pit(DungeonPlan plan, DungeonPlan.Bucket bucket, Room room, int[] rect, int x, int y, int z, Palette.Kit palette, int slot, Hit hit) {
        if (y < room.y0 - DungeonFeatures.PIT_DEPTH - 1 || y > room.y0 - 1 || x < rect[0] - 1 || x > rect[2] + 1 || z < rect[1] - 1 || z > rect[3] + 1) { return; }
        if (pitOpen(plan, bucket, room, rect, x, y, z)) {
            hit.offer(RANK_AIR, palette.state(Palette.AIR, 0));
            return;
        }
        for (int[] d : NEIGHBORS) {
            if (pitOpen(plan, bucket, room, rect, x + d[0], y + d[1], z + d[2])) {
                hit.offer(RANK_SHELL, palette.state(Palette.FLOORS, slot));
                return;
            }
        }
    }

    private static boolean pitOpen(DungeonPlan plan, DungeonPlan.Bucket bucket, Room room, int[] rect, int x, int y, int z) {
        return y >= room.y0 - DungeonFeatures.PIT_DEPTH && y <= room.y0 - 1 && has(rect, x, z) && !kept(plan, bucket, room, x, z);
    }

    static boolean nearEntrance(DungeonPlan plan, int x, int z) {
        for (Entrance e : plan.entrances()) {
            if (Math.abs(x - e.x) <= ENTRANCE_KEEP && Math.abs(z - e.z) <= ENTRANCE_KEEP) { return true; }
        }
        return false;
    }

    private static boolean kept(DungeonPlan plan, DungeonPlan.Bucket bucket, Room room, int x, int z) {
        for (Shaft s : bucket.shafts) {
            if (s.bottomY <= room.y1 && s.topY >= room.y0 && s.covers(x, z, 1)) { return true; }
        }
        if (room.entrance && nearEntrance(plan, x, z)) { return true; }
        for (int[] door : doors(plan, room, x, z)) {
            if (nearDoor(door, x, z)) { return true; }
        }
        return false;
    }

    public static boolean nearDoor(int[] door, int x, int z) {
        int pad = 1 + door[2] / 2;
        return Math.abs(x - door[0]) <= pad && Math.abs(z - door[1]) <= pad;
    }

    public static List<int[]> doors(DungeonPlan plan, Room room, int x, int z) {
        List<int[]> doors = new ArrayList<>();
        for (int cx = Coords.blockToCube(x) - 1; cx <= Coords.blockToCube(x) + 1; cx++) {
            for (int cz = Coords.blockToCube(z) - 1; cz <= Coords.blockToCube(z) + 1; cz++) {
                for (Link link : plan.bucket(cx, cz).links) {
                    if (link.roomA == room.index && link.floorYA == room.floorY) { doors.add(new int[] {link.doorAX, link.doorAZ, link.width, link.height}); }
                    if (link.roomB == room.index && link.floorYB == room.floorY) { doors.add(new int[] {link.doorBX, link.doorBZ, link.width, link.height}); }
                }
            }
        }
        return doors;
    }

    private static String surface(int y, int low, int high) { return y < low ? Palette.FLOORS : y > high ? Palette.CEILINGS : Palette.WALLS; }

    private static int roomSlot(DungeonPlan plan, Room room) {
        long h = MathUtil.mix(room.seed, SLOT_SALT, 0, 0);
        return MathUtil.percent(h) < DungeonDef.Degree.CHANCES[plan.roll(VARIABILITY)] ? 1 + (int) Math.floorMod(h >>> 8, VARIANT_SLOTS) : 0;
    }

    private static boolean isCave(Room room, List<Feature> features) {
        if (unshaped(room)) { return false; }
        if ("cave".equals(room.shape)) { return true; }
        for (Feature f : features) {
            if (f.type == PlanRecords.FEATURE_CAVE) { return true; }
        }
        return false;
    }

    private static void cave(DungeonPlan plan, Room room, int x, int y, int z, Palette.Kit palette, int slot, Hit hit) {
        if (y < room.y0 - 1 || y > room.y1 + 1) { return; }
        if (caveOpen(plan, room, x, y, z)) {
            hit.offer(RANK_AIR, palette.state(Palette.AIR, 0));
            return;
        }
        for (int[] d : NEIGHBORS) {
            if (caveOpen(plan, room, x + d[0], y + d[1], z + d[2])) {
                hit.offer(RANK_SHELL, palette.state(Palette.CAVE, slot));
                return;
            }
        }
    }

    private static boolean caveOpen(DungeonPlan plan, Room room, int x, int y, int z) { return caveInside(room, x, y, z) || tunnel(plan, room, x, y, z); }

    private static boolean caveInside(Room room, int x, int y, int z) {
        if (!room.contains(x, z) || y < room.y0 || y > room.y1) { return false; }
        float off = noise(room.seed, x, y, z);
        double ar = Math.max(0.5D, (room.x1 - room.x0 + 1) / 2.0D - off);
        double cr = Math.max(0.5D, (room.z1 - room.z0 + 1) / 2.0D - off);
        double br = Math.max(0.5D, (room.y1 - room.y0 + 1) - off / 2.0D);
        double dx = x - (room.x0 + room.x1) / 2.0D;
        double dy = y - (room.y0 - 0.5D);
        double dz = z - (room.z0 + room.z1) / 2.0D;
        return dx * dx / (ar * ar) + dy * dy / (br * br) + dz * dz / (cr * cr) <= 1.0D;
    }

    private static float noise(long seed, int x, int y, int z) {
        int gx = Math.floorDiv(x, NOISE_CELL);
        int gy = Math.floorDiv(y, NOISE_CELL);
        int gz = Math.floorDiv(z, NOISE_CELL);
        float fx = (float) Math.floorMod(x, NOISE_CELL) / NOISE_CELL;
        float fy = (float) Math.floorMod(y, NOISE_CELL) / NOISE_CELL;
        float fz = (float) Math.floorMod(z, NOISE_CELL) / NOISE_CELL;
        float low = MathUtil.lerp(fz, MathUtil.lerp(fx, corner(seed, gx, gy, gz), corner(seed, gx + 1, gy, gz)), MathUtil.lerp(fx, corner(seed, gx, gy, gz + 1), corner(seed, gx + 1, gy, gz + 1)));
        float high = MathUtil.lerp(fz, MathUtil.lerp(fx, corner(seed, gx, gy + 1, gz), corner(seed, gx + 1, gy + 1, gz)), MathUtil.lerp(fx, corner(seed, gx, gy + 1, gz + 1), corner(seed, gx + 1, gy + 1, gz + 1)));
        return MathUtil.lerp(fy, low, high);
    }

    private static float corner(long seed, int gx, int gy, int gz) { return Math.floorMod(MathUtil.mix(seed, gx, gy, gz), NOISE_RANGE) / 100.0F; }

    private static boolean tunnel(DungeonPlan plan, Room room, int x, int y, int z) {
        if (!room.contains(x, z)) { return false; }
        for (int[] door : doors(plan, room, x, z)) {
            if (opens(room, door, x, y, z)) { return true; }
        }
        return false;
    }

    private static boolean opens(Room room, int[] door, int x, int y, int z) {
        if (y < room.floorY || y >= room.floorY + door[3]) { return false; }
        int east = Math.abs(door[0] - room.x1);
        int west = Math.abs(door[0] - room.x0);
        int south = Math.abs(door[1] - room.z1);
        int north = Math.abs(door[1] - room.z0);
        boolean alongX = Math.min(east, west) <= Math.min(south, north);
        int sign = alongX ? (east < west ? 1 : -1) : (south < north ? 1 : -1);
        int along = alongX ? x - door[0] : z - door[1];
        int lateral = alongX ? z - door[1] : x - door[0];
        int depth = -sign * along;
        return depth >= 0 && depth <= TUNNEL_DEPTH && wide(lateral, door[2]);
    }

    private static boolean wide(int delta, int width) { return delta >= Link.low(width) && delta <= Link.high(width); }

    private static void feature(Feature f, int x, int y, int z, Palette.Kit palette, Hit hit) {
        switch (f.type) {
            case PlanRecords.FEATURE_PILLARS:
                if (inside(f, x, y, z) && onGrid(f.x0, f.z0, f.seed & 1, x, z)) { hit.offer(RANK_SOLID, palette.state(Palette.PILLARS, f.blockSlot)); }
                break;
            case PlanRecords.FEATURE_PARTITION:
                if (inside(f, x, y, z)) { hit.offer(RANK_SOLID, palette.state(Palette.WALLS, f.blockSlot)); }
                break;
            case PlanRecords.FEATURE_ISLAND:
                if (inside(f, x, y, z)) { hit.offer(RANK_SOLID, palette.state(Palette.FLOORS, f.blockSlot)); }
                break;
            case PlanRecords.FEATURE_FENCE:
                if (inside(f, x, y, z) && (x == f.x0 || x == f.x1 || z == f.z0 || z == f.z1)) { hit.offer(RANK_SOLID, palette.state(Palette.FENCING, f.blockSlot)); }
                break;
            case PlanRecords.FEATURE_POOL:
                pool(f, x, y, z, palette, hit);
                break;
            case PlanRecords.FEATURE_SINKHOLE:
                sinkhole(f, x, y, z, palette, hit);
                break;
            default:
                break;
        }
    }

    private static void pool(Feature f, int x, int y, int z, Palette.Kit palette, Hit hit) {
        if (x < f.x0 - 1 || x > f.x1 + 1 || z < f.z0 - 1 || z > f.z1 + 1 || y < f.y0 - 1 || y > f.y1) { return; }
        if (inside(f, x, y, z)) { hit.offer(RANK_LIQUID, palette.state(Palette.LIQUID, f.blockSlot)); }
        else { hit.offer(RANK_SHELL, palette.state(Palette.FLOORS, f.blockSlot)); }
    }

    private static void sinkhole(Feature f, int x, int y, int z, Palette.Kit palette, Hit hit) {
        if (x < f.x0 - 1 || x > f.x1 + 1 || z < f.z0 - 1 || z > f.z1 + 1 || y < f.y0 || y > f.y1) { return; }
        if (inRect(f, x, z)) { hit.offer(RANK_AIR, palette.state(Palette.AIR, 0)); }
        else if (y > f.y0 && y < f.y1) { hit.offer(RANK_SHELL, palette.state(Palette.WALLS, f.blockSlot)); }
    }

    private static boolean inRect(Feature f, int x, int z) { return x >= f.x0 && x <= f.x1 && z >= f.z0 && z <= f.z1; }

    private static boolean inside(Feature f, int x, int y, int z) { return inRect(f, x, z) && y >= f.y0 && y <= f.y1; }

    private static void corridor(DungeonPlan.Bucket bucket, Link link, int x, int y, int z, Palette.Kit palette, Hit hit) {
        for (Segment s : link.segments) {
            int[] foot = link.footprint(s);
            int fx0 = foot[0];
            int fz0 = foot[1];
            int fx1 = foot[2];
            int fz1 = foot[3];
            if (x < fx0 - 1 || x > fx1 + 1 || z < fz0 - 1 || z > fz1 + 1) { continue; }
            int floor = s.floorClamped(x, z);
            if (y < floor - 1 || y > floor + link.height) { continue; }
            boolean inside = x >= fx0 && x <= fx1 && z >= fz0 && z <= fz1 && y >= floor && y < floor + link.height;
            if (inside) { hit.offer(RANK_AIR, palette.state(Palette.AIR, 0)); }
            else if (!inTemplate(bucket, x, y, z)) { hit.offer(RANK_SHELL, palette.state(surface(y, floor, floor + link.height - 1), 0)); }
        }
    }

    private static boolean inTemplate(DungeonPlan.Bucket bucket, int x, int y, int z) {
        for (Room room : bucket.rooms) {
            if (room.templateId != null && room.contains(x, z) && y >= room.y0 && y <= room.y1) { return true; }
        }
        return false;
    }

    private static void shaft(Shaft shaft, int x, int y, int z, Palette.Kit palette, Hit hit) {
        if (y < shaft.bottomY || y >= shaft.topY || !shaft.covers(x, z, 1)) { return; }
        if (shaft.covers(x, z, 0)) { hit.offer(RANK_AIR, palette.state(Palette.AIR, 0)); }
        else { hit.offer(RANK_SHELL, palette.state(Palette.WALLS, 0)); }
    }

    private static final class Hit {
        private int rank;
        @Nullable private IBlockState state;

        private void offer(int offered, IBlockState block) {
            if (offered > rank) {
                rank = offered;
                state = block;
            }
        }
    }

    public static final class Palette {
        public static final String WALLS = "walls";
        public static final String FLOORS = "floors";
        public static final String CEILINGS = "ceilings";
        public static final String PILLARS = "pillars";
        public static final String FENCING = "fencing";
        public static final String LIQUID = "liquid";
        public static final String CAVE = "cave";
        public static final String SUPPORT = "support";
        public static final String STAIRS = "stairs";
        public static final String SLABS = "slabs";
        public static final IBlockState DEFAULT_SLAB = Blocks.STONE_SLAB.getDefaultState().withProperty(BlockStoneSlab.VARIANT, BlockStoneSlab.EnumType.SMOOTHBRICK);
        public static final String AIR = "air";
        private static final int SLOTS = VARIANT_SLOTS + 1;
        private final Map<Object, Kit> owners = new IdentityHashMap<>();
        private final Kit fallback;

        public Palette(DungeonPlan plan) {
            Map<String, Kit> byTheme = new HashMap<>();
            long seed = plan.site.siteSeed;
            this.fallback = kit(plan.themeId, seed, byTheme);
            for (Level level : plan.levels) {
                Kit kit = kit(level.themeId, seed, byTheme);
                for (Room room : level.rooms) {
                    owners.put(room, kit);
                }
                for (Link link : level.links) {
                    owners.put(link, kit);
                }
                if (level.shaft != null) { owners.put(level.shaft, kit); }
            }
        }

        public Kit of(Object owner) {
            Kit kit = owners.get(owner);
            return kit == null ? fallback : kit;
        }

        private static Kit kit(@Nullable String themeId, long seed, Map<String, Kit> cache) {
            String key = themeId == null ? "" : themeId;
            Kit held = cache.get(key);
            if (held == null) {
                held = new Kit(themeId == null ? null : ContentDungeonThemes.byName(themeId), seed);
                cache.put(key, held);
            }
            return held;
        }

        public static final class Kit {
            private final Map<String, IBlockState[]> states = new HashMap<>();

            private Kit(@Nullable DungeonThemeDef theme, long seed) {
                IBlockState[] walls = resolve(theme, WALLS, seed, filled(Blocks.STONE.getDefaultState()));
                states.put(WALLS, walls);
                for (String family : Arrays.asList(FLOORS, CEILINGS, PILLARS, FENCING, CAVE)) {
                    states.put(family, resolve(theme, family, seed, walls));
                }
                states.put(SUPPORT, resolve(theme, SUPPORT, seed, states.get(FLOORS)));
                states.put(STAIRS, resolve(theme, STAIRS, seed, filled(Blocks.STONE_BRICK_STAIRS.getDefaultState())));
                states.put(SLABS, resolve(theme, SLABS, seed, filled(DEFAULT_SLAB)));
                states.put(LIQUID, resolve(theme, LIQUID, seed, filled(Blocks.WATER.getDefaultState())));
                states.put(AIR, resolve(theme, AIR, seed, filled(Blocks.AIR.getDefaultState())));
            }

            public IBlockState state(String family, int slot) { return states.get(family)[MathHelper.clamp(slot, 0, SLOTS - 1)]; }
        }

        private static IBlockState[] filled(IBlockState state) {
            IBlockState[] slots = new IBlockState[SLOTS];
            Arrays.fill(slots, state);
            return slots;
        }

        static List<IBlockState> found(DungeonThemeDef theme, String family, @Nullable List<Integer> weights) {
            List<IBlockState> found = new ArrayList<>();
            for (BlockWeightDef entry : theme.blocks.getOrDefault(family, Collections.emptyList())) {
                Block block = Registries.find(ForgeRegistries.BLOCKS, entry.block);
                if (block == null) { continue; }
                found.add(ContentStates.of(block, entry.meta, entry.properties, theme.key));
                if (weights != null) { weights.add(entry.weight); }
            }
            return found;
        }

        private static IBlockState[] resolve(@Nullable DungeonThemeDef theme, String family, long seed, IBlockState[] fallback) {
            if (theme == null) { return fallback; }
            List<Integer> weights = new ArrayList<>();
            List<IBlockState> found = found(theme, family, weights);
            if (found.isEmpty()) { return fallback; }
            IBlockState[] slots = new IBlockState[SLOTS];
            int first = pick(weights, -1, MathUtil.mix(seed, family.hashCode(), 0, 0));
            slots[0] = found.get(first);
            for (int slot = 1; slot < SLOTS; slot++) {
                slots[slot] = found.get(pick(weights, found.size() > 1 ? first : -1, MathUtil.mix(seed, family.hashCode(), slot, 0)));
            }
            return slots;
        }

        private static int pick(List<Integer> weights, int skip, long hash) {
            long total = 0;
            for (int i = 0; i < weights.size(); i++) {
                if (i != skip) { total += weights.get(i); }
            }
            long roll = Math.floorMod(hash, total);
            for (int i = 0; i < weights.size(); i++) {
                if (i == skip) { continue; }
                roll -= weights.get(i);
                if (roll < 0) { return i; }
            }
            return 0;
        }
    }
}
