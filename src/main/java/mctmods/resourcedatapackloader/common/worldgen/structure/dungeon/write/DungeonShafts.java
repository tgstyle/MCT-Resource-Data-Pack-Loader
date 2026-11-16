package mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.write;

import mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.plan.DungeonPlan;
import mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.plan.DungeonPlan.Level;
import mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.plan.DungeonPlan.Room;
import mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.plan.PlanRecords;
import mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.plan.PlanRecords.Shaft;
import mctmods.resourcedatapackloader.core.util.math.Hashes;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LadderBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public final class DungeonShafts {
    private static final int SIDE = 3;
    private static final int RING = 8;
    private static final int SPIRAL_CLEAR = 4;
    private static final int STAIRS_CLEAR = 2;
    private static final int[][] RING_OF = {{0, 7, 6}, {1, -1, 5}, {2, 3, 4}};
    private static final Direction[] ASCENT = {Direction.EAST, Direction.EAST, Direction.SOUTH, Direction.SOUTH, Direction.WEST, Direction.WEST, Direction.NORTH, Direction.NORTH};
    private static final int[] LADDER_DX = {1, 2, 1, 0};
    private static final int[] LADDER_DZ = {0, 1, 2, 1};
    private static final int[] BACKING_DX = {1, 3, 1, -1};
    private static final int[] BACKING_DZ = {-1, 1, 3, 1};
    private static final Direction[] LADDER_FACING = {Direction.SOUTH, Direction.WEST, Direction.NORTH, Direction.EAST};

    private DungeonShafts() {}

    public static void write(WorldGenLevel level, DungeonPlan plan, DungeonPlan.Bucket bucket, DungeonShapes.Palette palette, BoundingBox box) {
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (Shaft shaft : bucket.shafts) {
            int bottom = bottomOf(plan, shaft);
            DungeonShapes.Palette.Kit kit = palette.of(shaft);
            int x1 = Math.min(box.maxX(), shaft.x() + SIDE);
            int z1 = Math.min(box.maxZ(), shaft.z() + SIDE);
            int y1 = Math.min(box.maxY(), shaft.topY() - 1);
            for (int x = Math.max(box.minX(), shaft.x() - 1); x <= x1; x++) {
                for (int z = Math.max(box.minZ(), shaft.z() - 1); z <= z1; z++) {
                    for (int y = Math.max(box.minY(), bottom); y <= y1; y++) {
                        BlockState state = at(shaft, bottom, x, y, z, kit);
                        if (state != null) { DungeonWriter.set(level, pos.set(x, y, z), state); }
                    }
                }
            }
        }
    }

    public static int bottomOf(DungeonPlan plan, Shaft shaft) {
        for (Level level : plan.levels) {
            for (Room room : level.rooms) {
                if (room.templateId == null && room.contains(shaft.x() + 1, shaft.z() + 1) && room.y0 <= shaft.bottomY() && room.y1 >= shaft.bottomY()) { return room.y0; }
            }
        }
        return shaft.bottomY();
    }

    @Nullable public static BlockState at(Shaft shaft, int bottom, int x, int y, int z, DungeonShapes.Palette.Kit kit) {
        int dx = x - shaft.x();
        int dz = z - shaft.z();
        if (y < bottom || y >= shaft.topY() || dx < -1 || dx > SIDE || dz < -1 || dz > SIDE) { return null; }
        long hash = Hashes.mix(shaft.bottomY(), shaft.x(), shaft.topY(), shaft.z());
        int face = (int) Math.floorMod(hash >>> 8, 4L);
        if (dx < 0 || dx == SIDE || dz < 0 || dz == SIDE) {
            boolean backing = shaft.type() == PlanRecords.SHAFT_LADDER && dx == BACKING_DX[face] && dz == BACKING_DZ[face];
            return backing ? kit.state(DungeonShapes.Palette.WALLS, 0) : null;
        }
        boolean floorLayer = y == shaft.topY() - 1;
        if (shaft.type() == PlanRecords.SHAFT_LADDER) { return ladder(face, dx, dz, floorLayer, kit); }
        return ring(shaft, bottom, Math.floorMod(hash, RING), dx, dz, y, floorLayer, kit);
    }

    public static BlockPos topOpenStep(Shaft shaft, int bottom) {
        int last = (shaft.topY() - 1 - bottom) * 2;
        int cell = Math.floorMod(last - 1 + Math.floorMod(Hashes.mix(shaft.bottomY(), shaft.x(), shaft.topY(), shaft.z()), RING), RING);
        for (int dx = 0; dx < SIDE; dx++) {
            for (int dz = 0; dz < SIDE; dz++) {
                if (RING_OF[dx][dz] == cell) { return new BlockPos(shaft.x() + dx, shaft.topY() - 1, shaft.z() + dz); }
            }
        }
        return new BlockPos(shaft.x() + 1, shaft.topY() - 1, shaft.z() + 1);
    }

    private static BlockState ladder(int face, int dx, int dz, boolean floorLayer, DungeonShapes.Palette.Kit kit) {
        if (dx == LADDER_DX[face] && dz == LADDER_DZ[face]) { return ladderState(LADDER_FACING[face]); }
        return floorLayer ? kit.state(DungeonShapes.Palette.FLOORS, 0) : kit.state(DungeonShapes.Palette.AIR, 0);
    }

    static BlockState ladderState(Direction facing) { return Blocks.LADDER.defaultBlockState().setValue(LadderBlock.FACING, facing); }

    private static BlockState ring(Shaft shaft, int bottom, int offset, int dx, int dz, int y, boolean floorLayer, DungeonShapes.Palette.Kit kit) {
        boolean spiral = shaft.type() == PlanRecords.SHAFT_SPIRAL;
        int cell = RING_OF[dx][dz];
        BlockState air = kit.state(DungeonShapes.Palette.AIR, 0);
        BlockState floor = kit.state(DungeonShapes.Palette.FLOORS, 0);
        if (cell < 0) { return spiral ? kit.state(DungeonShapes.Palette.WALLS, 0) : floorLayer ? floor : air; }
        int per = spiral ? 2 : 1;
        int last = (shaft.topY() - 1 - bottom) * per;
        int first = (y - bottom) * per;
        for (int i = first; i < first + per; i++) {
            if (i <= last && (i + offset) % RING == cell) { return step(kit, i, cell, spiral); }
        }
        if (!floorLayer) { return air; }
        int clear = spiral ? SPIRAL_CLEAR : STAIRS_CLEAR;
        for (int k = 1; k <= clear; k++) {
            if (Math.floorMod(last - k + offset, RING) == cell) { return air; }
        }
        return floor;
    }

    private static BlockState step(DungeonShapes.Palette.Kit kit, int index, int cell, boolean spiral) {
        if (spiral) {
            BlockState slab = kit.state(DungeonShapes.Palette.SLABS, 0);
            if (!slab.hasProperty(SlabBlock.TYPE)) { slab = DungeonShapes.Palette.DEFAULT_SLAB; }
            return slab.setValue(SlabBlock.TYPE, index % 2 == 0 ? SlabType.BOTTOM : SlabType.TOP);
        }
        BlockState stair = kit.state(DungeonShapes.Palette.STAIRS, 0);
        if (!stair.hasProperty(StairBlock.FACING)) { stair = Blocks.STONE_BRICK_STAIRS.defaultBlockState(); }
        return stair.setValue(StairBlock.FACING, ASCENT[cell]);
    }
}
