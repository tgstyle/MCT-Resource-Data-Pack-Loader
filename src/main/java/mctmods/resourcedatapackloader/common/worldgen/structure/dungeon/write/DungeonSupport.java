package mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.write;

import mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.plan.DungeonPlan;
import mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.plan.DungeonPlan.Level;
import mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.plan.PlanRecords;

import javax.annotation.Nullable;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;

public final class DungeonSupport {
    private DungeonSupport() {}

    public static void write(World world, DungeonPlan plan, DungeonPlan.Bucket bucket, DungeonShapes.Palette palette, StructureBoundingBox box, int depth, int worldFloor) {
        if (depth <= 0) { return; }
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int x = box.minX; x <= box.maxX; x++) {
            for (int z = box.minZ; z <= box.maxZ; z++) {
                IBlockState below = DungeonShapes.at(plan, bucket, x, box.minY - 1, z, palette);
                for (int y = box.minY; y <= box.maxY; y++) {
                    IBlockState state = DungeonShapes.at(plan, bucket, x, y, z, palette);
                    if (below == null && state != null && solid(state)) { fill(world, pos, x, y, z, depth, worldFloor, palette.of(levelAt(plan, y)).state(DungeonShapes.Palette.SUPPORT, 0)); }
                    below = state;
                }
            }
        }
    }

    static void fill(World world, BlockPos.MutableBlockPos pos, int x, int y, int z, int depth, int worldFloor, IBlockState support) {
        for (int d = 1; d <= depth && y - d >= worldFloor; d++) {
            Material material = world.getBlockState(pos.setPos(x, y - d, z)).getMaterial();
            if (!material.isReplaceable() && !material.isLiquid()) { return; }
            DungeonWriter.set(world, pos, support);
        }
    }

    private static boolean solid(IBlockState state) { return state.getMaterial() != Material.AIR && !state.getMaterial().isLiquid(); }

    @Nullable private static Object levelAt(DungeonPlan plan, int y) {
        for (Level level : plan.levels) {
            if (level.floorY - PlanRecords.SLAB_GAP - 1 <= y) { return level.rooms.isEmpty() ? null : level.rooms.get(0); }
        }
        return null;
    }
}
