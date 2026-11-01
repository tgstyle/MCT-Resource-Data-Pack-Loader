package mctmods.resourcedatapackloader.common.worldgen.feature;

import mctmods.resourcedatapackloader.common.worldgen.placement.ContentPlacer;
import mctmods.resourcedatapackloader.common.worldgen.structure.city.ContentPalette;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public interface IContentShape {
    boolean generate(ContentPlacer placer, RandomSource random, BlockPos origin);

    default boolean places(WorldGenLevel level, ContentPalette palette, BlockState wood, List<BlockState> canopies) { return false; }

    default int reach(WorldGenLevel level) { return 0; }

    @Nullable default BoundingBox footprint(WorldGenLevel level, RandomSource random, int originX, int originZ) { return null; }
}
