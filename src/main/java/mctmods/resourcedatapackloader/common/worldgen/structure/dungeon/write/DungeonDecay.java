package mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.write;

import mctmods.resourcedatapackloader.common.pack.definitions.worlds.DungeonDef;
import mctmods.resourcedatapackloader.common.pack.definitions.worlds.DungeonDef.LevelRule;
import mctmods.resourcedatapackloader.common.pack.definitions.worlds.DungeonThemeDef;
import mctmods.resourcedatapackloader.common.pack.definitions.worlds.RoomKindDef;
import mctmods.resourcedatapackloader.common.util.world.Box;
import mctmods.resourcedatapackloader.common.util.world.Coords;
import mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.plan.DungeonPlan;
import mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.plan.DungeonPlan.Level;
import mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.plan.DungeonPlan.Link;
import mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.plan.DungeonPlan.Room;
import mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.plan.DungeonPlan.Segment;
import mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.plan.PlanRecords.Shaft;
import mctmods.resourcedatapackloader.core.util.math.MathUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nullable;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;

public final class DungeonDecay {
    private static final String FAMILY = "decay";
    private static final int NONE = -1;
    private static final int FLOOR = 0;
    private static final int WALL = 1;
    private static final int CEILING = 2;
    private static final int CELL = 12;
    private static final int SWING = 40;
    private static final int COLLAPSE_ABOVE = 85;
    private static final int COLLAPSE_RATE = 5;
    private static final int COLLAPSE_SIZE = 3;
    private static final long HASH_SALT = 0x5DECA7L;
    private static final long COLLAPSE_SALT = 0xC011A95EL;
    private static final int DEGENERACY = DungeonDef.ROLLS.indexOf("degeneracy");
    private static final IBlockState[] NO_STATES = new IBlockState[0];
    private static final RoomKindDef HALL = RoomKindDef.hall();
    private static final Map<String, IBlockState[]> LISTS = new ConcurrentHashMap<>();

    private DungeonDecay() {}

    public static void write(World world, DungeonPlan plan, DungeonDef def, DungeonPlan.Bucket bucket, DungeonShapes.Palette palette, StructureBoundingBox columns, StructureBoundingBox window) {
        Run run = new Run(world, plan, def, bucket, palette, columns, window);
        for (Level level : plan.levels) {
            run.tier = run.tiers.get(level.index);
            if (run.tier.list.length == 0) { continue; }
            run.level = level;
            run.shaft = null;
            for (Room room : level.rooms) {
                run.room(room);
            }
            for (Link link : level.links) {
                run.corridor(link);
            }
            if (level.shaft != null) { run.shaft(level.shaft); }
        }
    }

    private static IBlockState[] listOf(@Nullable DungeonThemeDef theme) {
        if (theme == null) { return NO_STATES; }
        return LISTS.computeIfAbsent(theme.key.toString(), key -> resolve(theme));
    }

    private static IBlockState[] resolve(DungeonThemeDef theme) {
        return DungeonShapes.Palette.found(theme, FAMILY, null).toArray(NO_STATES);
    }

    private static boolean falling(IBlockState state) { return state.getBlock() instanceof BlockFalling; }

    private static boolean air(IBlockState state) { return state.getMaterial() == Material.AIR; }

    private static final class Tier {
        private final long seed;
        private final int index;
        private final int base;
        private final int perDepth;
        private final double depth;
        private final int degeneracy;
        private final IBlockState[] list;

        private Tier(DungeonPlan plan, DungeonDef def, Level level) {
            this.seed = plan.site.siteSeed;
            this.index = level.index;
            this.depth = DungeonDef.depthOf(level.index, plan.levels.size());
            DungeonThemeDef theme = level.theme();
            DungeonDef.Decay held = def.decay != null ? def.decay : theme != null && theme.decay != null ? theme.decay : new DungeonDef.Decay(DungeonDef.Decay.DEFAULT_BASE, DungeonDef.Decay.DEFAULT_PER_DEPTH);
            for (LevelRule rule : def.levelRules) {
                if (rule.fromDepth <= depth && rule.decay != null) { held = rule.decay; }
            }
            this.base = held.base;
            this.perDepth = held.perDepth;
            this.degeneracy = DungeonDef.Degree.CHANCES[level.roll(DEGENERACY)];
            this.list = listOf(theme);
        }

        private int at(int x, int z) {
            int gx = Math.floorDiv(x, CELL);
            int gz = Math.floorDiv(z, CELL);
            float fx = (float) Math.floorMod(x, CELL) / CELL;
            float fz = (float) Math.floorMod(z, CELL) / CELL;
            float south = MathUtil.lerp(fx, corner(gx, gz), corner(gx + 1, gz));
            float north = MathUtil.lerp(fx, corner(gx, gz + 1), corner(gx + 1, gz + 1));
            float noise = MathUtil.lerp(fz, south, north);
            return base + (int) Math.round(perDepth * depth) + degeneracy + Math.round((noise - 0.5F) * SWING);
        }

        private float corner(int gx, int gz) { return Math.floorMod(MathUtil.mix(seed, gx, index, gz), 1000) / 1000.0F; }
    }

    private static final class Run {
        private final World world;
        private final DungeonPlan plan;
        private final DungeonDef def;
        private final DungeonPlan.Bucket bucket;
        private final DungeonShapes.Palette palette;
        private final StructureBoundingBox columns;
        private final StructureBoundingBox window;
        private final List<Tier> tiers = new ArrayList<>();
        private final BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        private final BlockPos.MutableBlockPos back = new BlockPos.MutableBlockPos();
        private Tier tier;
        private Level level;
        @Nullable private Shaft shaft;
        private int bottom;
        private int extra;
        private boolean template;
        private int ox;
        private int oy;
        private int oz;

        private Run(World world, DungeonPlan plan, DungeonDef def, DungeonPlan.Bucket bucket, DungeonShapes.Palette palette, StructureBoundingBox columns, StructureBoundingBox window) {
            this.world = world;
            this.plan = plan;
            this.def = def;
            this.bucket = bucket;
            this.palette = palette;
            this.columns = columns;
            this.window = window;
            for (Level held : plan.levels) {
                tiers.add(new Tier(plan, def, held));
            }
        }

        private void room(Room room) {
            RoomKindDef kind = kindOf(room);
            template = room.templateId != null;
            if (template && !kind.decayTemplate) { return; }
            extra = tier.degeneracy + kind.decay;
            int pad = template ? 1 : 0;
            box(room.x0 + pad, room.y0 + pad, room.z0 + pad, room.x1 - pad, room.y1 - pad, room.z1 - pad);
        }

        private RoomKindDef kindOf(Room room) {
            for (RoomKindDef kind : def.kindsAt(tier.depth)) {
                if (kind.kind.equals(room.kind)) { return kind; }
            }
            return HALL;
        }

        private void shaft(Shaft held) {
            template = false;
            extra = tier.degeneracy;
            shaft = held;
            bottom = DungeonShafts.bottomOf(plan, held);
            box(held.minX(0), bottom, held.minZ(0), held.maxX(0), held.topY - 1, held.maxZ(0));
            shaft = null;
        }

        private void box(int x0, int y0, int z0, int x1, int y1, int z1) {
            StructureBoundingBox clip = Box.clip(columns, x0, y0, z0, x1, y1, z1, 1);
            for (int x = clip.minX; x <= clip.maxX; x++) {
                for (int z = clip.minZ; z <= clip.maxZ; z++) {
                    for (int y = clip.minY; y <= clip.maxY; y++) {
                        int kind = ring(x, y, z, x0, y0, z0, x1, y1, z1);
                        if (kind != NONE) { cell(x, y, z, kind); }
                    }
                }
            }
        }

        private void corridor(Link link) {
            template = false;
            extra = tier.degeneracy;
            for (Segment s : link.segments) {
                int[] foot = link.footprint(s);
                int fx0 = foot[0];
                int fz0 = foot[1];
                int fx1 = foot[2];
                int fz1 = foot[3];
                StructureBoundingBox clip = Box.clip(columns, fx0, columns.minY, fz0, fx1, columns.maxY, fz1, 1);
                for (int x = clip.minX; x <= clip.maxX; x++) {
                    for (int z = clip.minZ; z <= clip.maxZ; z++) {
                        int floor = s.floorClamped(x, z);
                        for (int y = Math.max(columns.minY, floor - 1); y <= Math.min(columns.maxY, floor + link.height); y++) {
                            int kind = ring(x, y, z, fx0, floor, fz0, fx1, floor + link.height - 1, fz1);
                            if (kind != NONE) { cell(x, y, z, kind); }
                        }
                    }
                }
            }
        }

        private int ring(int x, int y, int z, int x0, int y0, int z0, int x1, int y1, int z1) {
            if (x >= x0 && x <= x1 && y >= y0 && y <= y1 && z >= z0 && z <= z1) { return NONE; }
            ox = 0;
            oy = 0;
            oz = 0;
            if (y < y0) {
                oy = -1;
                return FLOOR;
            }
            if (y > y1) {
                oy = 1;
                return CEILING;
            }
            if (x < x0) { ox = -1; }
            else if (x > x1) { ox = 1; }
            else if (z < z0) { oz = -1; }
            else { oz = 1; }
            return WALL;
        }

        private boolean kept(int x, int y, int z, IBlockState have) {
            if (air(have) || have.getBlock().hasTileEntity(have)) { return true; }
            if (template ? !have.isFullCube() : have != DungeonShapes.at(plan, bucket, x, y, z, palette)) { return true; }
            if (shaft != null && DungeonShafts.at(shaft, bottom, x, y, z, palette.of(shaft)) != null) { return true; }
            return DungeonShapes.nearEntrance(plan, x, z);
        }

        private void cell(int x, int y, int z, int kind) {
            IBlockState have = world.getBlockState(pos.setPos(x, y, z));
            if (kept(x, y, z, have)) { return; }
            int field = tier.at(x, z) + extra;
            if (kind == WALL && field > COLLAPSE_ABOVE && collapsed(x, y, z) && open(x + ox, y + oy, z + oz)) {
                DungeonWriter.set(world, pos.setPos(x, y, z), Blocks.AIR.getDefaultState());
                return;
            }
            int hash = MathUtil.percent(MathUtil.mix(tier.seed ^ HASH_SALT, x, y, z));
            IBlockState[] list = tier.list;
            int step = 100 / (list.length + 1);
            int index = Math.min(list.length, Math.max(0, field - hash) / step) - 1;
            while (index >= 0 && blocked(list[index], kind, x, y, z)) {
                index--;
            }
            if (index >= 0 && list[index] != have) { DungeonWriter.set(world, pos.setPos(x, y, z), list[index]); }
        }

        private boolean blocked(IBlockState pick, int kind, int x, int y, int z) {
            if (kind == CEILING) { return air(pick) || falling(pick); }
            if (kind == WALL && falling(pick)) { return true; }
            return air(pick) && !open(x + ox, y + oy, z + oz);
        }

        private boolean collapsed(int x, int y, int z) {
            if (level.index == 0 && y >= plan.site.surfaceY - def.surfaceBuffer) { return false; }
            boolean alongZ = ox != 0;
            int along = Math.floorDiv(alongZ ? z : x, COLLAPSE_SIZE);
            int face = alongZ ? x : z;
            int centerX = alongZ ? x : along * COLLAPSE_SIZE + 1;
            int centerZ = alongZ ? along * COLLAPSE_SIZE + 1 : z;
            int field = tier.at(centerX, centerZ) + extra;
            long hash = MathUtil.mix(tier.seed ^ COLLAPSE_SALT, along, Math.floorDiv(y, COLLAPSE_SIZE), face * 2 + (alongZ ? 1 : 0));
            return MathUtil.percent(hash) < (long) (field - COLLAPSE_ABOVE) * COLLAPSE_RATE;
        }

        private boolean open(int x, int y, int z) {
            if (x < window.minX || x > window.maxX || y < window.minY || y > window.maxY || z < window.minZ || z > window.maxZ) { return false; }
            IBlockState planned = DungeonShapes.at(plan, plan.bucket(Coords.blockToCube(x), Coords.blockToCube(z)), x, y, z, palette);
            if (planned != null && air(planned)) { return true; }
            IBlockState state = planned != null ? planned : world.getBlockState(back.setPos(x, y, z));
            return !air(state) && !state.getMaterial().isLiquid() && !falling(state);
        }
    }
}
