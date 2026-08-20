package mctmods.resourcedatapackloader.content.rubic.world.interfaces;

import mctmods.resourcedatapackloader.util.CubePos;

import net.minecraft.util.math.BlockPos;
import java.util.function.Predicate;

public interface IRubicWorld extends IMinMaxHeight {
    boolean rdpl$isRubicWorld();

    ICubeProvider rdpl$getCubeCache();

    default boolean rdpl$testForCubes(int minBlockX, int minBlockY, int minBlockZ, int maxBlockX, int maxBlockY, int maxBlockZ, Predicate<ICube> test) {
        return rdpl$testForCubes(
                CubePos.fromBlockCoords(minBlockX, minBlockY, minBlockZ),
                CubePos.fromBlockCoords(maxBlockX, maxBlockY, maxBlockZ),
                test
        );
    }

    boolean rdpl$testForCubes(CubePos start, CubePos end, Predicate<? super ICube> test);

    ICube rdpl$getCubeFromCubeCoords(int cubeX, int cubeY, int cubeZ);

    ICube rdpl$getCubeFromBlockCoords(BlockPos pos);

    boolean rdpl$isBlockColumnLoaded(BlockPos pos);

    boolean rdpl$isBlockColumnLoaded(BlockPos pos, boolean allowEmpty);

    int rdpl$getMaxGenerationHeight();
}
