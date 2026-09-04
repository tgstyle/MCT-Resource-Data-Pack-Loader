package mctmods.resourcedatapackloader.content.rubic.world.interfaces;

import mctmods.resourcedatapackloader.client.CubeProviderClient;
import mctmods.resourcedatapackloader.content.rubic.RubicWorldControl;
import mctmods.resourcedatapackloader.content.rubic.lighting.ILightingManager;
import mctmods.resourcedatapackloader.content.rubic.server.CubeProviderServer;
import mctmods.resourcedatapackloader.content.rubic.server.SpawnCubes;
import mctmods.resourcedatapackloader.content.rubic.world.cube.Cube;
import mctmods.resourcedatapackloader.util.IntRange;
import mctmods.resourcedatapackloader.util.XYZMap;
import mctmods.resourcedatapackloader.util.XZMap;
import mctmods.resourcedatapackloader.util.world.CubeSplitTickList;
import mctmods.resourcedatapackloader.util.world.CubeSplitTickSet;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public interface IRubicWorldInternal extends IRubicWorld {
    void rdpl$tickRubicWorld();

    @Override ICubeProviderInternal rdpl$getCubeCache();

    ILightingManager rdpl$getLightingManager();

    @Override Cube rdpl$getCubeFromCubeCoords(int cubeX, int cubeY, int cubeZ);

    @Override Cube rdpl$getCubeFromBlockCoords(BlockPos pos);

    void rdpl$fakeWorldHeight(int height);

    default BlockPos rdpl$groundInWindow(BlockPos pos) {
        World world = (World) this;
        int floor = MathHelper.clamp(RubicWorldControl.terrainOffsetCubes() << 4, rdpl$getMinHeight(), rdpl$getMaxHeight());
        int ceiling = MathHelper.clamp(world.provider.getActualHeight(), floor, rdpl$getMaxHeight() - 1);
        Chunk chunk = world.getChunk(pos);
        BlockPos current = new BlockPos(pos.getX(), ceiling, pos.getZ());
        while (current.getY() > floor) {
            BlockPos next = current.down();
            IBlockState state = chunk.getBlockState(next);
            if (state.getMaterial().blocksMovement() && !state.getBlock().isLeaves(state, world, next) && !state.getBlock().isFoliage(world, next)) {
                break;
            }
            current = next;
        }
        return current;
    }

    default BlockPos getTopSolidOrLiquidBlockVanilla(BlockPos pos) {
        Chunk chunk = ((World) this).getChunk(pos);
        BlockPos current = new BlockPos(pos.getX(), chunk.getTopFilledSegment() + 16, pos.getZ());
        while (current.getY() >= 0) {
            BlockPos next = current.down();
            IBlockState state = chunk.getBlockState(next);
            if (state.getMaterial().blocksMovement() && !state.getBlock().isLeaves(state, (World) this, next) && !state.getBlock().isFoliage((World) this, next)) {
                break;
            }
            current = next;
        }
        return current;
    }

    interface IServer extends IRubicWorldInternal, IRubicWorldServer {
        void rdpl$initRubicWorldServer(IntRange heightRange, IntRange generationRange);

        @Override CubeProviderServer rdpl$getCubeCache();

        void rdpl$removeForcedCube(ICube cube);

        void rdpl$addForcedCube(ICube cube);

        XYZMap<ICube> rdpl$getForcedCubes();

        XZMap<IColumn> rdpl$getForcedColumns();

        CubeSplitTickSet rdpl$getScheduledTicks();

        CubeSplitTickList rdpl$getThisTickScheduledTicks();

        SpawnCubes rdpl$getSpawnArea();

        void rdpl$setSpawnArea(SpawnCubes spawn);

        ICompatGenerationScope rdpl$doCompatibilityGeneration();

        boolean rdpl$isCompatGenerationScope();
    }

    interface IClient extends IRubicWorldInternal {
        void rdpl$initRubicWorldClient(IntRange heightRange, IntRange generationRange);

        CubeProviderClient rdpl$getCubeCache();
    }

    interface ICompatGenerationScope extends AutoCloseable { void close(); }
}
