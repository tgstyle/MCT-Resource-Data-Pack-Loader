package mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.write;

import mctmods.resourcedatapackloader.common.util.world.Box;
import mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.plan.DungeonPlan;
import mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.plan.DungeonPlan.Level;
import mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.plan.DungeonPlan.Room;
import mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.plan.PlanRecords;
import mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.plan.PlanRecords.Shaft;
import mctmods.resourcedatapackloader.core.util.math.MathUtil;

import javax.annotation.Nullable;
import net.minecraft.block.BlockLadder;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;

public final class DungeonShafts {
    private static final int SIDE = Shaft.SIDE;
    private static final int RING = 8;
    private static final int SPIRAL_CLEAR = 4;
    private static final int STAIRS_CLEAR = 2;
    private static final int[][] RING_OF = {{0, 7, 6}, {1, -1, 5}, {2, 3, 4}};
    private static final EnumFacing[] ASCENT = {EnumFacing.EAST, EnumFacing.EAST, EnumFacing.SOUTH, EnumFacing.SOUTH, EnumFacing.WEST, EnumFacing.WEST, EnumFacing.NORTH, EnumFacing.NORTH};
    private static final int[] LADDER_DX = {1, 2, 1, 0};
    private static final int[] LADDER_DZ = {0, 1, 2, 1};
    private static final int[] BACKING_DX = {1, 3, 1, -1};
    private static final int[] BACKING_DZ = {-1, 1, 3, 1};
    private static final EnumFacing[] LADDER_FACING = {EnumFacing.SOUTH, EnumFacing.WEST, EnumFacing.NORTH, EnumFacing.EAST};

    private DungeonShafts() {}

    public static void write(World world, DungeonPlan plan, DungeonPlan.Bucket bucket, DungeonShapes.Palette palette, StructureBoundingBox box) {
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (Shaft shaft : bucket.shafts) {
            int bottom = bottomOf(plan, shaft);
            DungeonShapes.Palette.Kit kit = palette.of(shaft);
            StructureBoundingBox clip = Box.clip(box, shaft.minX(1), bottom, shaft.minZ(1), shaft.maxX(1), shaft.topY - 1, shaft.maxZ(1), 0);
            for (int x = clip.minX; x <= clip.maxX; x++) {
                for (int z = clip.minZ; z <= clip.maxZ; z++) {
                    for (int y = clip.minY; y <= clip.maxY; y++) {
                        IBlockState state = at(shaft, bottom, x, y, z, kit);
                        if (state != null) { DungeonWriter.set(world, pos.setPos(x, y, z), state); }
                    }
                }
            }
        }
    }

    public static int bottomOf(DungeonPlan plan, Shaft shaft) {
        for (Level level : plan.levels) {
            for (Room room : level.rooms) {
                if (room.templateId == null && room.contains(shaft.centreX(), shaft.centreZ()) && room.y0 <= shaft.bottomY && room.y1 >= shaft.bottomY) { return room.y0; }
            }
        }
        return shaft.bottomY;
    }

    @Nullable public static IBlockState at(Shaft shaft, int bottom, int x, int y, int z, DungeonShapes.Palette.Kit kit) {
        int dx = x - shaft.x;
        int dz = z - shaft.z;
        if (y < bottom || y >= shaft.topY || !shaft.covers(x, z, 1)) { return null; }
        long hash = MathUtil.mix(shaft.bottomY, shaft.x, shaft.topY, shaft.z);
        int face = (int) Math.floorMod(hash >>> 8, 4L);
        if (!shaft.covers(x, z, 0)) {
            boolean backing = shaft.type == PlanRecords.SHAFT_LADDER && dx == BACKING_DX[face] && dz == BACKING_DZ[face];
            return backing ? kit.state(DungeonShapes.Palette.WALLS, 0) : null;
        }
        boolean floorLayer = y == shaft.topY - 1;
        if (shaft.type == PlanRecords.SHAFT_LADDER) { return ladder(face, dx, dz, floorLayer, kit); }
        return ring(shaft, bottom, (int) Math.floorMod(hash, RING), dx, dz, y, floorLayer, kit);
    }

    public static BlockPos topOpenStep(Shaft shaft, int bottom) {
        int last = (shaft.topY - 1 - bottom) * 2;
        int cell = Math.floorMod(last - 1 + (int) Math.floorMod(MathUtil.mix(shaft.bottomY, shaft.x, shaft.topY, shaft.z), RING), RING);
        for (int dx = 0; dx < SIDE; dx++) {
            for (int dz = 0; dz < SIDE; dz++) {
                if (RING_OF[dx][dz] == cell) { return new BlockPos(shaft.x + dx, shaft.topY - 1, shaft.z + dz); }
            }
        }
        return new BlockPos(shaft.centreX(), shaft.topY - 1, shaft.centreZ());
    }

    private static IBlockState ladder(int face, int dx, int dz, boolean floorLayer, DungeonShapes.Palette.Kit kit) {
        if (dx == LADDER_DX[face] && dz == LADDER_DZ[face]) { return ladderState(LADDER_FACING[face]); }
        return floorLayer ? kit.state(DungeonShapes.Palette.FLOORS, 0) : kit.state(DungeonShapes.Palette.AIR, 0);
    }

    static IBlockState ladderState(EnumFacing facing) { return Blocks.LADDER.getDefaultState().withProperty(BlockLadder.FACING, facing); }

    private static IBlockState ring(Shaft shaft, int bottom, int offset, int dx, int dz, int y, boolean floorLayer, DungeonShapes.Palette.Kit kit) {
        boolean spiral = shaft.type == PlanRecords.SHAFT_SPIRAL;
        int cell = RING_OF[dx][dz];
        IBlockState air = kit.state(DungeonShapes.Palette.AIR, 0);
        IBlockState floor = kit.state(DungeonShapes.Palette.FLOORS, 0);
        if (cell < 0) { return spiral ? kit.state(DungeonShapes.Palette.WALLS, 0) : floorLayer ? floor : air; }
        int per = spiral ? 2 : 1;
        int last = (shaft.topY - 1 - bottom) * per;
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

    private static IBlockState step(DungeonShapes.Palette.Kit kit, int index, int cell, boolean spiral) {
        if (spiral) {
            IBlockState slab = kit.state(DungeonShapes.Palette.SLABS, 0);
            if (!slab.getPropertyKeys().contains(BlockSlab.HALF)) { slab = DungeonShapes.Palette.DEFAULT_SLAB; }
            return slab.withProperty(BlockSlab.HALF, index % 2 == 0 ? BlockSlab.EnumBlockHalf.BOTTOM : BlockSlab.EnumBlockHalf.TOP);
        }
        IBlockState stair = kit.state(DungeonShapes.Palette.STAIRS, 0);
        if (!stair.getPropertyKeys().contains(BlockStairs.FACING)) { stair = Blocks.STONE_BRICK_STAIRS.getDefaultState(); }
        return stair.withProperty(BlockStairs.FACING, ASCENT[cell]);
    }
}
