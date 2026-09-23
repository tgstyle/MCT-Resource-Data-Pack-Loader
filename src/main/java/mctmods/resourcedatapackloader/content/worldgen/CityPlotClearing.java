package mctmods.resourcedatapackloader.content.worldgen;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public final class CityPlotClearing {
    static final int OPEN_LEAST = 12;
    private static final int DOOR_STEPS = 5;
    private static final int DOOR_HEIGHT = 3;
    private static final int DOORSTEP_REACH = 2;
    private CityPlotClearing() {}

    public static int openOver(WorldGenLevel level, BoundingBox held, BoundingBox box, int seat, int[] keep, int[] roads) {
        int ceiling = Math.max(seat + OPEN_LEAST, held.maxY() + 1);
        BlockPos.MutableBlockPos at = new BlockPos.MutableBlockPos();
        int opened = 0;
        for (int x = Math.max(held.minX() - CityPlotGround.RING, box.minX()); x <= Math.min(held.maxX() + CityPlotGround.RING, box.maxX()); x++) {
            for (int z = Math.max(held.minZ() - CityPlotGround.RING, box.minZ()); z <= Math.min(held.maxZ() + CityPlotGround.RING, box.maxZ()); z++) {
                if (CityPlotGround.inside(held, x, z) || CityPlotGround.kept(keep, x, z) || CityPlotGround.nearRoad(roads, x, z, CityPlotGround.ROAD_BESIDE)) { continue; }
                int bed = pathTop(level, at, x, z, seat + 1, ceiling);
                for (int y = bed == Integer.MIN_VALUE ? seat + 1 : bed + 1; y <= ceiling; y++) {
                    at.set(x, y, z);
                    BlockState state = level.getBlockState(at);
                    if (!CityPlotGround.opening(state) && !state.is(Blocks.VINE)) { continue; }
                    level.setBlock(at, Blocks.AIR.defaultBlockState(), 2);
                    opened++;
                }
            }
        }
        doorsteps(level, held, box, seat, keep, roads, at);
        return opened;
    }

    private static void doorsteps(WorldGenLevel level, BoundingBox held, BoundingBox box, int seat, int[] keep, int[] roads, BlockPos.MutableBlockPos at) {
        for (int x = Math.max(held.minX() - CityPlotGround.RING, box.minX()); x <= Math.min(held.maxX() + CityPlotGround.RING, box.maxX()); x++) {
            for (int z = Math.max(held.minZ() - CityPlotGround.RING, box.minZ()); z <= Math.min(held.maxZ() + CityPlotGround.RING, box.maxZ()); z++) {
                if (CityPlotGround.kept(keep, x, z) || !CityPlotGround.nearRoad(roads, x, z, DOORSTEP_REACH)) { continue; }
                for (int y = seat - DOORSTEP_REACH; y <= seat + 1; y++) {
                    BlockState step = level.getBlockState(at.set(x, y, z));
                    if (!(step.getBlock() instanceof StairBlock)) { continue; }
                    BlockState below = level.getBlockState(at.set(x, y - 1, z));
                    at.set(x, y, z);
                    if (CityPlotGround.stoneStep(step) && CityPlotGround.liquid(below)) { level.setBlock(at, CityPlotGround.overWater(level, x, y, z), 2); }
                    else if (CityPlotGround.terrain(below)) { level.setBlock(at, CityPlotGround.exposed(level, at, CityPlotGround.groundFor(level, x, z)), 2); }
                }
            }
        }
    }

    private static int pathTop(WorldGenLevel level, BlockPos.MutableBlockPos at, int x, int z, int from, int to) {
        for (int y = to; y >= from; y--) {
            if (level.getBlockState(at.set(x, y, z)).is(Blocks.DIRT_PATH)) { return y; }
        }
        return Integer.MIN_VALUE;
    }

    public static int doorways(WorldGenLevel level, BoundingBox held, BoundingBox box, int[] keep, int[] roads) {
        BlockPos.MutableBlockPos at = new BlockPos.MutableBlockPos();
        int freed = 0;
        for (int x = Math.max(held.minX(), box.minX()); x <= Math.min(held.maxX(), box.maxX()); x++) {
            for (int z = Math.max(held.minZ(), box.minZ()); z <= Math.min(held.maxZ(), box.maxZ()); z++) {
                if (x != held.minX() && x != held.maxX() && z != held.minZ() && z != held.maxZ()) { continue; }
                for (int y = held.minY(); y <= held.maxY() - 1; y++) {
                    at.set(x, y, z);
                    if (!level.getBlockState(at).is(BlockTags.DOORS) && !doorwayAt(level, x, y, z)) { continue; }
                    int outX = x == held.minX() ? -1 : x == held.maxX() ? 1 : 0;
                    int outZ = outX != 0 ? 0 : z == held.minZ() ? -1 : 1;
                    at.set(x + outX, y - 1, z + outZ);
                    if (box.isInside(at) && !CityPlotGround.nearRoad(roads, x + outX, z + outZ, 0) && !CityPlotGround.kept(keep, x + outX, z + outZ)) {
                        BlockState step = level.getBlockState(at);
                        if (CityPlotGround.stoneStep(step) && CityPlotGround.liquid(level.getBlockState(at.below()))) { level.setBlock(at, CityPlotGround.overWater(level, at.getX(), at.getY(), at.getZ()), 2); }
                        else if (!CityPlotGround.solid(step) && !CityPlotGround.liquid(step)) { level.setBlock(at, CityPlotGround.exposed(level, at, CityPlotGround.groundFor(level, at.getX(), at.getZ())), 2); }
                    }
                    for (int out = 1; out <= DOOR_STEPS; out++) {
                        int stepX = x + outX * out;
                        int stepZ = z + outZ * out;
                        if (CityPlotGround.nearRoad(roads, stepX, stepZ, 0)) { break; }
                        if (CityPlotGround.kept(keep, stepX, stepZ)) { continue; }
                        for (int up = 0; up <= DOOR_HEIGHT; up++) {
                            at.set(stepX, y + up, stepZ);
                            if (!box.isInside(at) || (!CityPlotGround.clearable(level.getBlockState(at)) && !level.getBlockState(at).is(Blocks.DIRT_PATH))) { continue; }
                            level.setBlock(at, Blocks.AIR.defaultBlockState(), 2);
                            freed++;
                        }
                    }
                    break;
                }
            }
        }
        return freed;
    }

    private static boolean doorwayAt(WorldGenLevel level, int x, int y, int z) {
        BlockPos.MutableBlockPos at = new BlockPos.MutableBlockPos();
        if (CityPlotGround.solid(level.getBlockState(at.set(x, y, z))) || CityPlotGround.solid(level.getBlockState(at.set(x, y + 1, z)))) { return false; }
        if (!CityPlotGround.solid(level.getBlockState(at.set(x, y + 2, z))) || !CityPlotGround.solid(level.getBlockState(at.set(x, y - 1, z)))) { return false; }
        if (CityPlotGround.solid(level.getBlockState(at.set(x - 1, y, z))) && CityPlotGround.solid(level.getBlockState(at.set(x + 1, y, z)))) { return true; }
        return CityPlotGround.solid(level.getBlockState(at.set(x, y, z - 1))) && CityPlotGround.solid(level.getBlockState(at.set(x, y, z + 1)));
    }
}
