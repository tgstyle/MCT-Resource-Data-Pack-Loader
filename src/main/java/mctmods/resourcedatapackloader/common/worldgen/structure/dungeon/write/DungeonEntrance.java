package mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.write;

import mctmods.resourcedatapackloader.common.pack.definitions.games.PickDef;
import mctmods.resourcedatapackloader.common.pack.definitions.worlds.DungeonDef;
import mctmods.resourcedatapackloader.common.util.worldgen.SeedStreams;
import mctmods.resourcedatapackloader.common.worldgen.feature.shape.ContentImprint;
import mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.plan.DungeonPlan;
import mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.plan.DungeonPlan.Room;
import mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.plan.DungeonSlots.TemplateSizes;
import mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.plan.PlanRecords;
import mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.plan.PlanRecords.Entrance;
import mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.plan.PlanRecords.Shaft;
import mctmods.resourcedatapackloader.core.util.LineCompat;
import mctmods.resourcedatapackloader.core.util.math.Hashes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

public final class DungeonEntrance {
    private static final int CLEAR_ABOVE = 2;
    private static final int DOOR_HEIGHT = 2;
    private static final int MIN_SIDE = 6;
    private static final int MAX_SIDE = 12;
    private static final int MIN_INNER = 3;
    private static final int INNER_SPREAD = 3;
    private static final int MIN_ROOM = 5;
    private static final int TEMPLATE_SIDE = 2 * PlanRecords.ENTRANCE_REACH;
    private static final int TEMPLATE_HEIGHT = PlanRecords.ENTRANCE_UP - CLEAR_ABOVE - 1;
    private static final int[] DOOR_DX = {0, 1, 0, -1};
    private static final int[] DOOR_DZ = {-1, 0, 1, 0};
    private static final List<String> TYPES = Arrays.asList("simple", "spiral", "toproom");

    private DungeonEntrance() {}

    public static Entrance plan(DungeonDef def, long siteSeed, int degree, int anchorX, int anchorZ, List<Room> rooms, TemplateSizes sizes) {
        int start = 0;
        for (Room room : rooms) {
            if (room.start) { start = room.index; }
        }
        List<Entrance> extras = new ArrayList<>();
        Random random = SeedStreams.random(siteSeed, "entranceExtra", 0);
        if (random.nextInt(100) < DungeonDef.Degree.CHANCES[degree]) {
            List<Room> open = new ArrayList<>();
            for (Room room : rooms) {
                if (plain(room) && Math.max(Math.abs((room.x0 + room.x1) / 2 - anchorX), Math.abs((room.z0 + room.z1) / 2 - anchorZ)) > TEMPLATE_SIDE) { open.add(room); }
            }
            if (!open.isEmpty()) {
                Room room = open.get(random.nextInt(open.size()));
                extras.add(made(def, siteSeed, 1, (room.x0 + room.x1) / 2, (room.z0 + room.z1) / 2, room.index, sizes, Collections.emptyList()));
            }
        }
        return made(def, siteSeed, 0, anchorX, anchorZ, start, sizes, extras);
    }

    private static boolean plain(Room room) {
        return !room.start && !room.boss && room.templateId == null && room.x1 - room.x0 + 1 >= MIN_ROOM && room.z1 - room.z0 + 1 >= MIN_ROOM && DungeonShapes.shapeRects(room).isEmpty();
    }

    private static Entrance made(DungeonDef def, long siteSeed, int n, int x, int z, int room, TemplateSizes sizes, List<Entrance> extras) {
        Random random = SeedStreams.random(siteSeed, "entrance", n);
        int type = Math.max(0, TYPES.indexOf(PickDef.pick(def.entrance().types(), random, TYPES.getFirst())));
        String template = type == PlanRecords.ENTRANCE_TOPROOM ? template(def, random, sizes) : null;
        return new Entrance(type, x, z, room, template, extras);
    }

    @Nullable private static String template(DungeonDef def, Random random, TemplateSizes sizes) {
        String named = PickDef.pick(def.entrance().topRoomTemplates(), random, null);
        int[] span = named == null ? null : sizes.size(named);
        return span != null && span[0] <= TEMPLATE_SIDE && span[2] <= TEMPLATE_SIDE && span[1] <= TEMPLATE_HEIGHT ? named : null;
    }

    public static void write(WorldGenLevel level, DungeonPlan plan, DungeonPlan.Bucket bucket, DungeonShapes.Palette palette, BoundingBox box) {
        for (Entrance entrance : bucket.entrances) {
            Room room = roomOf(plan, entrance);
            if (room == null) { continue; }
            DungeonShapes.Palette.Kit kit = palette.of(room);
            boolean housed = entrance.type() == PlanRecords.ENTRANCE_TOPROOM;
            if (housed) { building(level, plan, kit, box, top(level, plan, entrance)); }
            shaft(level, plan, kit, box, entrance, room, housed);
        }
    }

    @Nullable private static Room roomOf(DungeonPlan plan, Entrance entrance) {
        for (Room room : plan.levels.getFirst().rooms) {
            if (room.index == entrance.room()) { return room; }
        }
        return null;
    }

    private static Shaft spiralOf(DungeonPlan plan, Entrance entrance, Room room) {
        return new Shaft(PlanRecords.SHAFT_SPIRAL, entrance.x() - 1, entrance.z() - 1, plan.site.surfaceY() + 1, room.y0);
    }

    public static BlockPos standingCell(DungeonPlan plan) {
        Entrance entrance = plan.entrance;
        Room room = entrance == null ? null : roomOf(plan, entrance);
        if (room == null) { return new BlockPos(plan.levels.getFirst().anchorX, plan.levels.getFirst().floorY + 1, plan.levels.getFirst().anchorZ); }
        if (entrance.type() == PlanRecords.ENTRANCE_SPIRAL) { return DungeonShafts.topOpenStep(spiralOf(plan, entrance, room), room.y0); }
        return new BlockPos(entrance.x(), plan.site.surfaceY() + 1, entrance.z());
    }

    private static void shaft(WorldGenLevel level, DungeonPlan plan, DungeonShapes.Palette.Kit kit, BoundingBox box, Entrance entrance, Room room, boolean housed) {
        int sample = plan.site.surfaceY();
        Shaft spiral = spiralOf(plan, entrance, room);
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int x = Math.max(box.minX(), entrance.x() - 1); x <= Math.min(box.maxX(), entrance.x() + 1); x++) {
            for (int z = Math.max(box.minZ(), entrance.z() - 1); z <= Math.min(box.maxZ(), entrance.z() + 1); z++) {
                int top = housed ? sample : DungeonWriter.broken() ? Math.max(sample, ground(level, pos, x, z, sample)) : ground(level, pos, x, z, sample);
                for (int y = Math.max(box.minY(), room.y0); y <= Math.min(box.maxY(), top + CLEAR_ABOVE); y++) {
                    BlockState state = y > top ? kit.state(DungeonShapes.Palette.AIR, 0) : entrance.type() == PlanRecords.ENTRANCE_SPIRAL ? stairs(spiral, room, kit, x, y, z, sample) : ladder(entrance, room, kit, x, y, z, sample, housed);
                    if (state != null) { DungeonWriter.set(level, pos.set(x, y, z), state); }
                }
            }
        }
    }

    private static BlockState stairs(Shaft spiral, Room room, DungeonShapes.Palette.Kit kit, int x, int y, int z, int sample) {
        BlockState state = y <= sample ? DungeonShafts.at(spiral, room.y0, x, y, z, kit) : null;
        return state != null ? state : kit.state(DungeonShapes.Palette.AIR, 0);
    }

    @Nullable private static BlockState ladder(Entrance entrance, Room room, DungeonShapes.Palette.Kit kit, int x, int y, int z, int sample, boolean housed) {
        if (x == entrance.x() && z == entrance.z()) { return DungeonShafts.ladderState(Direction.SOUTH); }
        if (housed && y == sample) { return null; }
        boolean backing = x == entrance.x() && z == entrance.z() - 1;
        return backing || y > room.y1 ? kit.state(DungeonShapes.Palette.WALLS, 0) : null;
    }

    @SuppressWarnings("deprecation") private static int ground(WorldGenLevel level, BlockPos.MutableBlockPos pos, int x, int z, int sample) {
        for (int y = sample + PlanRecords.ENTRANCE_UP; y >= sample - PlanRecords.ENTRANCE_DOWN; y--) {
            BlockState state = level.getBlockState(pos.set(x, y, z));
            if (LineCompat.blocksMotion(state) && !state.liquid() && !state.canBeReplaced() && !state.is(BlockTags.LEAVES) && !state.is(BlockTags.LOGS)) { return y; }
        }
        return sample;
    }

    private static Top top(WorldGenLevel level, DungeonPlan plan, Entrance entrance) {
        Random random = new Random(Hashes.mix(plan.site.siteSeed(), entrance.x(), 5, entrance.z()));
        StructureTemplate template = DungeonTemplates.load(level, entrance.toproomTemplate());
        if (template != null) {
            Rotation rotation = Rotation.values()[random.nextInt(Rotation.values().length)];
            Vec3i span = template.getSize(rotation);
            int x0 = entrance.x() - span.getX() / 2;
            int z0 = entrance.z() - span.getZ() / 2;
            return new Top(x0, z0, x0 + span.getX() - 1, z0 + span.getZ() - 1, span.getY(), random.nextInt(4), template, rotation, span);
        }
        int wide = MIN_SIDE + random.nextInt(MAX_SIDE - MIN_SIDE + 1);
        int deep = MIN_SIDE + random.nextInt(MAX_SIDE - MIN_SIDE + 1);
        int x0 = entrance.x() - (wide - 1) / 2;
        int z0 = entrance.z() - (deep - 1) / 2;
        return new Top(x0, z0, x0 + wide - 1, z0 + deep - 1, MIN_INNER + random.nextInt(INNER_SPREAD), random.nextInt(4), null, Rotation.NONE, Vec3i.ZERO);
    }

    private static void building(WorldGenLevel level, DungeonPlan plan, DungeonShapes.Palette.Kit kit, BoundingBox box, Top top) {
        int floorY = plan.site.surfaceY();
        int clearTop = floorY + (top.template == null ? top.height + 1 : top.span.getY() - 1) + CLEAR_ABOVE;
        BlockState support = kit.state(DungeonShapes.Palette.SUPPORT, 0);
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int x = Math.max(box.minX(), top.x0 - 1); x <= Math.min(box.maxX(), top.x1 + 1); x++) {
            for (int z = Math.max(box.minZ(), top.z0 - 1); z <= Math.min(box.maxZ(), top.z1 + 1); z++) {
                if (top.outside(x, z) && !top.apron(x, z)) { continue; }
                DungeonSupport.fill(level, pos, x, floorY, z, PlanRecords.ENTRANCE_DOWN, box.minY(), support);
                for (int y = Math.max(box.minY(), floorY); y <= Math.min(box.maxY(), clearTop); y++) {
                    BlockState state = hall(top, kit, x, z, y - floorY);
                    if (state != null) { DungeonWriter.set(level, pos.set(x, y, z), state); }
                }
            }
        }
        StructureTemplate template = top.template;
        if (template != null && top.x1 >= box.minX() && top.x0 <= box.maxX() && top.z1 >= box.minZ() && top.z0 <= box.maxZ()) { place(level, plan, box, top, template); }
    }

    private static void place(WorldGenLevel level, DungeonPlan plan, BoundingBox box, Top top, StructureTemplate template) {
        BlockPos at = new BlockPos(top.x0 + ContentImprint.backX(top.rotation, top.span), plan.site.surfaceY(), top.z0 + ContentImprint.backZ(top.rotation, top.span));
        DungeonTemplates.place(level, template, at, top.rotation, box, null, Hashes.mix(plan.site.siteSeed(), top.x0, 6, top.z0));
    }

    @Nullable private static BlockState hall(Top top, DungeonShapes.Palette.Kit kit, int x, int z, int dy) {
        BlockState air = kit.state(DungeonShapes.Palette.AIR, 0);
        if (top.outside(x, z)) { return dy >= 0 && dy <= DOOR_HEIGHT ? dy == 0 ? kit.state(DungeonShapes.Palette.FLOORS, 0) : air : null; }
        if (top.template != null) { return air; }
        if (dy == 0) { return kit.state(DungeonShapes.Palette.FLOORS, 0); }
        if (dy == top.height + 1) { return kit.state(DungeonShapes.Palette.CEILINGS, 0); }
        if (dy > top.height + 1) { return air; }
        boolean rim = x == top.x0 || x == top.x1 || z == top.z0 || z == top.z1;
        boolean door = x == top.doorX && z == top.doorZ && dy <= DOOR_HEIGHT;
        return rim && !door ? kit.state(DungeonShapes.Palette.WALLS, 0) : air;
    }

    private static final class Top {
        private final int x0;
        private final int z0;
        private final int x1;
        private final int z1;
        private final int height;
        private final int doorX;
        private final int doorZ;
        private final int outX;
        private final int outZ;
        @Nullable private final StructureTemplate template;
        private final Rotation rotation;
        private final Vec3i span;

        private Top(int x0, int z0, int x1, int z1, int height, int side, @Nullable StructureTemplate template, Rotation rotation, Vec3i span) {
            this.x0 = x0;
            this.z0 = z0;
            this.x1 = x1;
            this.z1 = z1;
            this.height = height;
            this.doorX = DOOR_DX[side] == 0 ? (x0 + x1) / 2 : DOOR_DX[side] > 0 ? x1 : x0;
            this.doorZ = DOOR_DZ[side] == 0 ? (z0 + z1) / 2 : DOOR_DZ[side] > 0 ? z1 : z0;
            this.outX = doorX + DOOR_DX[side];
            this.outZ = doorZ + DOOR_DZ[side];
            this.template = template;
            this.rotation = rotation;
            this.span = span;
        }

        private boolean outside(int x, int z) { return x < x0 || x > x1 || z < z0 || z > z1; }

        private boolean apron(int x, int z) { return template == null && x == outX && z == outZ; }
    }
}
