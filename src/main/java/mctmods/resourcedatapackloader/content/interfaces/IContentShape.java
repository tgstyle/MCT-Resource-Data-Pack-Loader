package mctmods.resourcedatapackloader.content.interfaces;

import mctmods.resourcedatapackloader.content.worldgen.ContentPalette;
import mctmods.resourcedatapackloader.content.worldgen.ContentPlacer;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import java.util.List;
import javax.annotation.Nullable;

public interface IContentShape {
    boolean generate(ContentPlacer placer, RandomSource random, BlockPos origin);

    default boolean places(WorldGenLevel level, ContentPalette palette, BlockState wood, List<BlockState> canopies) { return false; }

    default int reach(WorldGenLevel level) { return 0; }

    @Nullable default BoundingBox footprint(WorldGenLevel level, RandomSource random, int originX, int originZ) { return null; }
}
