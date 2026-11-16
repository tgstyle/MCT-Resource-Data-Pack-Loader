package mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.write;

import mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.plan.DungeonPlan;
import mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.plan.DungeonPlan.Level;
import mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.plan.PlanRecords;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public final class DungeonSupport {
    private DungeonSupport() {}

    public static void write(WorldGenLevel level, DungeonPlan plan, DungeonPlan.Bucket bucket, DungeonShapes.Palette palette, BoundingBox box, int depth, int worldFloor) {
        if (depth <= 0) { return; }
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int x = box.minX(); x <= box.maxX(); x++) {
            for (int z = box.minZ(); z <= box.maxZ(); z++) {
                BlockState below = DungeonShapes.at(plan, bucket, x, box.minY() - 1, z, palette);
                for (int y = box.minY(); y <= box.maxY(); y++) {
                    BlockState state = DungeonShapes.at(plan, bucket, x, y, z, palette);
                    if (below == null && state != null && solid(state)) { fill(level, pos, x, y, z, depth, worldFloor, palette.of(levelAt(plan, y)).state(DungeonShapes.Palette.SUPPORT, 0)); }
                    below = state;
                }
            }
        }
    }

    @SuppressWarnings("deprecation") static void fill(WorldGenLevel level, BlockPos.MutableBlockPos pos, int x, int y, int z, int depth, int worldFloor, BlockState support) {
        for (int d = 1; d <= depth && y - d >= worldFloor; d++) {
            BlockState there = level.getBlockState(pos.set(x, y - d, z));
            if (!there.canBeReplaced() && !there.liquid()) { return; }
            DungeonWriter.set(level, pos, support);
        }
    }

    @SuppressWarnings("deprecation") private static boolean solid(BlockState state) { return !state.isAir() && !state.liquid(); }

    @Nullable private static Object levelAt(DungeonPlan plan, int y) {
        for (Level level : plan.levels) {
            if (level.floorY - PlanRecords.SLAB_GAP - 1 <= y) { return level.rooms.isEmpty() ? null : level.rooms.get(0); }
        }
        return null;
    }
}
