package mctmods.resourcedatapackloader.content.rubic.world;

import mctmods.resourcedatapackloader.content.rubic.Rubic;
import mctmods.resourcedatapackloader.content.rubic.world.cube.Cube;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.ICubeProviderServer;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IRubicWorld;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IRubicWorldServer;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Coords;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import javax.annotation.Nullable;
import java.util.Objects;

public final class SpawnPlaceFinder {
    private SpawnPlaceFinder() { throw new Error(); }

    private static final int MIN_FREE_SPACE_SPAWN = 32;

    public static BlockPos getRandomizedSpawnPoint(World world) {
        BlockPos ret = world.getSpawnPoint();
        ContentLog.LOGGER.debug("Finding spawnpoint starting from {}", ret);
        boolean isAdventure = world.getWorldInfo().getGameType() == GameType.ADVENTURE;
        int spawnFuzz;
        if (world instanceof WorldServer) {
            spawnFuzz = world.getWorldType().getSpawnFuzz((WorldServer) world,
                    Objects.requireNonNull(world.getMinecraftServer()));
        }
        else { spawnFuzz = 1; }
        int border = MathHelper.floor(world.getWorldBorder().getClosestDistance(ret.getX(), ret.getZ()));
        if (border < spawnFuzz) { spawnFuzz = border; }
        if (!world.provider.isNether() && !isAdventure && spawnFuzz != 0) {
            if (spawnFuzz < 2) { spawnFuzz = 2; }
            int spawnFuzzHalf = spawnFuzz / 2;
            ContentLog.LOGGER.debug("Running bisect with spawn fizz {}", spawnFuzz);
            ret = getTopBlockBisect(world, ret.add(
                    world.rand.nextInt(spawnFuzzHalf) - spawnFuzz,
                    0,
                    world.rand.nextInt(spawnFuzzHalf) - spawnFuzz
            ));
            if (ret == null) {
                ret = world.getSpawnPoint();
                ContentLog.LOGGER.debug("No spawnpoint place found starting at {}, spawning at {}", ret, ret);
            }
            else { ret = ret.up(); }
        }
        return ret;
    }

    @Nullable public static BlockPos getTopBlockBisect(World world, BlockPos pos) {
        BlockPos minPos, maxPos;
        if (findNonEmpty(world, pos) == null) {
            ContentLog.LOGGER.debug("Starting bisect with empty space at init {}", pos);
            maxPos = pos;
            minPos = findMinPos(world, pos);
        }
        else {
            ContentLog.LOGGER.debug("Starting bisect without empty space at init {}", pos);
            minPos = pos;
            maxPos = findMaxPos(world, pos);
        }
        ContentLog.LOGGER.debug("Found minPos {} and maxPos {}", minPos, maxPos);
        if (minPos == null || maxPos == null) {
            Rubic.LOGGER.error("No suitable spawn found, using original input {} (min={}, max={})", pos, minPos, maxPos);
            return pos;
        }
        assert findNonEmpty(world, maxPos) == null && findNonEmpty(world, minPos) != null;
        return bisect(world, minPos.down(MIN_FREE_SPACE_SPAWN), maxPos.up(MIN_FREE_SPACE_SPAWN));
    }

    @Nullable private static BlockPos bisect(World world, BlockPos min, BlockPos max) {
        while (min.getY() < max.getY() - 1) {
            ContentLog.LOGGER.debug("Bisect step with min={}, max={}", min, max);
            BlockPos middle = middleY(min, max);
            if (findNonEmpty(world, middle) != null) { min = middle; }
            else { max = middle; }
        }
        return findNonEmpty(world, min);
    }

    private static BlockPos middleY(BlockPos min, BlockPos max) { return new BlockPos(min.getX(), (int) ((min.getY() + (long) max.getY()) >> 1), min.getZ()); }

    @Nullable private static BlockPos findMinPos(World world, BlockPos pos) {
        double dy = Cube.SIZE;
        while (findNonEmpty(world, inWorldUp(world, pos, -dy)) == null) {
            if (dy > Integer.MAX_VALUE) {
                ContentLog.LOGGER.debug("Error finding spawn point: can't find solid start height at {}", pos);
                return null;
            }
            dy *= 2;
        }
        return inWorldUp(world, pos, -dy);
    }

    @Nullable private static BlockPos findMaxPos(World world, BlockPos pos) {
        double dy = Cube.SIZE;
        while (findNonEmpty(world, inWorldUp(world, pos, dy)) != null) {
            if (dy > Integer.MAX_VALUE) {
                ContentLog.LOGGER.debug("Error finding spawn point: can't find non-solid end height at {}", pos);
                return null;
            }
            dy *= 2;
        }
        return inWorldUp(world, pos, dy);
    }

    @Nullable private static BlockPos findNonEmpty(World world, BlockPos pos) {
        pos = pos.down(MIN_FREE_SPACE_SPAWN);
        for (int i = 0; i < MIN_FREE_SPACE_SPAWN * 2; i++, pos = pos.up()) {
            ((IRubicWorldServer) world).rdpl$getCubeCache().getCubeNow(
                    Coords.blockToCube(pos.getX()), Coords.blockToCube(pos.getY()), Coords.blockToCube(pos.getZ()),
                    ICubeProviderServer.Requirement.POPULATE
            );
            if (world.getBlockState(pos).isSideSolid(world, pos, EnumFacing.UP)) { return pos; }
        }
        return null;
    }

    private static BlockPos inWorldUp(World world, BlockPos original, double up) {
        int y = (int) (original.getY() + up);
        y = MathHelper.clamp(y, ((IRubicWorld) world).rdpl$getMinHeight(), ((IRubicWorld) world).rdpl$getMaxHeight());
        return new BlockPos(original.getX(), y, original.getZ());
    }
}
