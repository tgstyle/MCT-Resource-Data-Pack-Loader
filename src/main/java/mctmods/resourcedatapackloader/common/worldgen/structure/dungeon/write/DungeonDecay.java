package mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.write;

import mctmods.resourcedatapackloader.common.blocks.ContentStates;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.BlockWeightDef;
import mctmods.resourcedatapackloader.common.pack.definitions.worlds.DungeonDef;
import mctmods.resourcedatapackloader.common.pack.definitions.worlds.DungeonDef.LevelRule;
import mctmods.resourcedatapackloader.common.pack.definitions.worlds.DungeonThemeDef;
import mctmods.resourcedatapackloader.common.pack.definitions.worlds.RoomKindDef;
import mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.ContentDungeonThemes;
import mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.plan.DungeonPlan;
import mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.plan.DungeonPlan.Level;
import mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.plan.DungeonPlan.Link;
import mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.plan.DungeonPlan.Room;
import mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.plan.DungeonPlan.Segment;
import mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.plan.PlanRecords.Entrance;
import mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.plan.PlanRecords.Shaft;
import mctmods.resourcedatapackloader.core.util.concurrent.Registered;
import mctmods.resourcedatapackloader.core.util.math.Hashes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.Mth;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

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
    private static final int ENTRANCE_KEEP = 3;
    private static final long HASH_SALT = 0x5DECA7L;
    private static final long COLLAPSE_SALT = 0xC011A95EL;
    private static final int DEGENERACY = DungeonDef.ROLLS.indexOf("degeneracy");
    private static final BlockState[] NO_STATES = new BlockState[0];
    private static final RoomKindDef HALL = RoomKindDef.hall();
    private static final Map<String, BlockState[]> LISTS = new ConcurrentHashMap<>();

    private DungeonDecay() {}

    public static void write(WorldGenLevel world, DungeonPlan plan, DungeonDef def, DungeonPlan.Bucket bucket, DungeonShapes.Palette palette, BoundingBox columns, BoundingBox window) {
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

    private static BlockState[] listOf(@Nullable DungeonThemeDef theme) {
        if (theme == null) { return NO_STATES; }
        return LISTS.computeIfAbsent(theme.key().toString(), _ -> resolve(theme));
    }

    private static BlockState[] resolve(DungeonThemeDef theme) {
        List<BlockState> found = new ArrayList<>();
        for (BlockWeightDef entry : theme.blocks().getOrDefault(FAMILY, Collections.emptyList())) {
            Block block = Registered.find(BuiltInRegistries.BLOCK, entry.block());
            if (block != null) { found.add(ContentStates.state(block, entry.properties(), theme.key())); }
        }
        return found.toArray(NO_STATES);
    }

    private static boolean falling(BlockState state) { return state.getBlock() instanceof FallingBlock; }

    private static boolean air(BlockState state) { return state.isAir(); }

    private static final class Tier {
        private final long seed;
        private final int index;
        private final int base;
        private final int perDepth;
        private final double depth;
        private final int degeneracy;
        private final BlockState[] list;

        private Tier(DungeonPlan plan, DungeonDef def, Level level) {
            this.seed = plan.site.siteSeed();
            this.index = level.index;
            this.depth = level.index / (double) Math.max(1, plan.levels.size() - 1);
            DungeonThemeDef theme = level.themeId == null ? null : ContentDungeonThemes.byName(level.themeId);
            DungeonDef.Decay held = def.decay() != null ? def.decay() : theme != null && theme.decay() != null ? theme.decay() : new DungeonDef.Decay(DungeonDef.Decay.DEFAULT_BASE, DungeonDef.Decay.DEFAULT_PER_DEPTH);
            for (LevelRule rule : def.levelRules()) {
                if (rule.fromDepth() <= depth && rule.decay() != null) { held = rule.decay(); }
            }
            this.base = held.base();
            this.perDepth = held.perDepth();
            this.degeneracy = DungeonDef.Degree.CHANCES[level.roll(DEGENERACY)];
            this.list = listOf(theme);
        }

        private int at(int x, int z) {
            int gx = Math.floorDiv(x, CELL);
            int gz = Math.floorDiv(z, CELL);
            float fx = (float) Math.floorMod(x, CELL) / CELL;
            float fz = (float) Math.floorMod(z, CELL) / CELL;
            float south = Mth.lerp(fx, corner(gx, gz), corner(gx + 1, gz));
            float north = Mth.lerp(fx, corner(gx, gz + 1), corner(gx + 1, gz + 1));
            float noise = Mth.lerp(fz, south, north);
            return base + (int) Math.round(perDepth * depth) + degeneracy + Math.round((noise - 0.5F) * SWING);
        }

        private float corner(int gx, int gz) { return Math.floorMod(Hashes.mix(seed, gx, index, gz), 1000) / 1000.0F; }
    }

    private static final class Run {
        private final WorldGenLevel world;
        private final DungeonPlan plan;
        private final DungeonDef def;
        private final DungeonPlan.Bucket bucket;
        private final DungeonShapes.Palette palette;
        private final BoundingBox columns;
        private final BoundingBox window;
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

        private Run(WorldGenLevel world, DungeonPlan plan, DungeonDef def, DungeonPlan.Bucket bucket, DungeonShapes.Palette palette, BoundingBox columns, BoundingBox window) {
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
            if (template && !kind.decayTemplate()) { return; }
            extra = tier.degeneracy + kind.decay();
            int pad = template ? 1 : 0;
            box(room.x0 + pad, room.y0 + pad, room.z0 + pad, room.x1 - pad, room.y1 - pad, room.z1 - pad);
        }

        private RoomKindDef kindOf(Room room) {
            List<RoomKindDef> kinds = def.roomKinds();
            for (LevelRule rule : def.levelRules()) {
                if (rule.fromDepth() <= tier.depth && rule.roomKinds() != null) { kinds = rule.roomKinds(); }
            }
            for (RoomKindDef kind : kinds) {
                if (kind.kind().equals(room.kind)) { return kind; }
            }
            return HALL;
        }

        private void shaft(Shaft held) {
            template = false;
            extra = tier.degeneracy;
            shaft = held;
            bottom = DungeonShafts.bottomOf(plan, held);
            box(held.x(), bottom, held.z(), held.x() + 2, held.topY() - 1, held.z() + 2);
            shaft = null;
        }

        private void box(int x0, int y0, int z0, int x1, int y1, int z1) {
            for (int x = Math.max(columns.minX(), x0 - 1); x <= Math.min(columns.maxX(), x1 + 1); x++) {
                for (int z = Math.max(columns.minZ(), z0 - 1); z <= Math.min(columns.maxZ(), z1 + 1); z++) {
                    for (int y = Math.max(columns.minY(), y0 - 1); y <= Math.min(columns.maxY(), y1 + 1); y++) {
                        int kind = ring(x, y, z, x0, y0, z0, x1, y1, z1);
                        if (kind != NONE) { cell(x, y, z, kind); }
                    }
                }
            }
        }

        private void corridor(Link link) {
            template = false;
            extra = tier.degeneracy;
            int lo = -((link.width - 1) / 2);
            int hi = link.width / 2;
            for (Segment s : link.segments) {
                int minX = Math.min(s.x0(), s.x1());
                int maxX = Math.max(s.x0(), s.x1());
                int minZ = Math.min(s.z0(), s.z1());
                int maxZ = Math.max(s.z0(), s.z1());
                int fx0 = minX != maxX ? minX : minX + lo;
                int fx1 = minX != maxX ? maxX : maxX + hi;
                int fz0 = minZ != maxZ ? minZ : minZ + lo;
                int fz1 = minZ != maxZ ? maxZ : maxZ + hi;
                for (int x = Math.max(columns.minX(), fx0 - 1); x <= Math.min(columns.maxX(), fx1 + 1); x++) {
                    for (int z = Math.max(columns.minZ(), fz0 - 1); z <= Math.min(columns.maxZ(), fz1 + 1); z++) {
                        int floor = s.floorAt(Math.clamp(x, minX, maxX), Math.clamp(z, minZ, maxZ));
                        for (int y = Math.max(columns.minY(), floor - 1); y <= Math.min(columns.maxY(), floor + link.height); y++) {
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

        private boolean kept(int x, int y, int z, BlockState have) {
            if (air(have) || have.hasBlockEntity()) { return true; }
            if (template ? !have.isCollisionShapeFullBlock(world, pos.set(x, y, z)) : have != DungeonShapes.at(plan, bucket, x, y, z, palette)) { return true; }
            if (shaft != null && DungeonShafts.at(shaft, bottom, x, y, z, palette.of(shaft)) != null) { return true; }
            if (plan.entrance == null) { return false; }
            List<Entrance> all = new ArrayList<>(plan.entrance.extras());
            all.add(plan.entrance);
            for (Entrance e : all) {
                if (Math.abs(x - e.x()) <= ENTRANCE_KEEP && Math.abs(z - e.z()) <= ENTRANCE_KEEP) { return true; }
            }
            return false;
        }

        private void cell(int x, int y, int z, int kind) {
            BlockState have = world.getBlockState(pos.set(x, y, z));
            if (kept(x, y, z, have)) { return; }
            int field = tier.at(x, z) + extra;
            if (kind == WALL && field > COLLAPSE_ABOVE && collapsed(x, y, z) && open(x + ox, y + oy, z + oz)) {
                DungeonWriter.set(world, pos.set(x, y, z), Blocks.AIR.defaultBlockState());
                return;
            }
            int hash = (int) Math.floorMod(Hashes.mix(tier.seed ^ HASH_SALT, x, y, z), 100L);
            BlockState[] list = tier.list;
            int step = 100 / (list.length + 1);
            int index = Math.min(list.length, Math.max(0, field - hash) / step) - 1;
            while (index >= 0 && blocked(list[index], kind, x, y, z)) {
                index--;
            }
            if (index >= 0 && list[index] != have) { DungeonWriter.set(world, pos.set(x, y, z), list[index]); }
        }

        private boolean blocked(BlockState pick, int kind, int x, int y, int z) {
            if (kind == CEILING) { return air(pick) || falling(pick); }
            if (kind == WALL && falling(pick)) { return true; }
            return air(pick) && !open(x + ox, y + oy, z + oz);
        }

        private boolean collapsed(int x, int y, int z) {
            if (level.index == 0 && y >= plan.site.surfaceY() - def.surfaceBuffer()) { return false; }
            boolean alongZ = ox != 0;
            int along = Math.floorDiv(alongZ ? z : x, COLLAPSE_SIZE);
            int face = alongZ ? x : z;
            int centerX = alongZ ? x : along * COLLAPSE_SIZE + 1;
            int centerZ = alongZ ? along * COLLAPSE_SIZE + 1 : z;
            int field = tier.at(centerX, centerZ) + extra;
            long hash = Hashes.mix(tier.seed ^ COLLAPSE_SALT, along, Math.floorDiv(y, COLLAPSE_SIZE), face * 2 + (alongZ ? 1 : 0));
            return Math.floorMod(hash, 100L) < (long) (field - COLLAPSE_ABOVE) * COLLAPSE_RATE;
        }

        @SuppressWarnings("deprecation") private boolean open(int x, int y, int z) {
            if (x < window.minX() || x > window.maxX() || y < window.minY() || y > window.maxY() || z < window.minZ() || z > window.maxZ()) { return false; }
            BlockState planned = DungeonShapes.at(plan, plan.bucket(x >> 4, z >> 4), x, y, z, palette);
            if (planned != null && air(planned)) { return true; }
            BlockState state = planned != null ? planned : world.getBlockState(back.set(x, y, z));
            return !air(state) && !state.liquid() && !falling(state);
        }
    }
}
