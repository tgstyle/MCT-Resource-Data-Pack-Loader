package mctmods.resourcedatapackloader.common.worldgen.feature.shape;

import mctmods.resourcedatapackloader.common.worldgen.feature.IContentShape;
import mctmods.resourcedatapackloader.common.worldgen.structure.city.ContentPalette;

import java.util.List;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;

abstract class ContentPlacedShape implements IContentShape {
    @Override public boolean places(WorldGenLevel level, ContentPalette palette, BlockState wood, List<BlockState> canopies) { return palette.offers(wood); }
}
