package mctmods.resourcedatapackloader.common.worldgen.feature.shape;

import mctmods.resourcedatapackloader.common.worldgen.feature.IContentShape;
import mctmods.resourcedatapackloader.common.worldgen.placement.ContentPlacer;

import java.util.List;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.World;

abstract class ContentPlacedShape implements IContentShape {
    final ContentPlacer placer;

    ContentPlacedShape(ContentPlacer placer) { this.placer = placer; }

    @Override public boolean places(World world, IBlockState wood, List<IBlockState> canopies) { return placer.offers(wood); }
}
