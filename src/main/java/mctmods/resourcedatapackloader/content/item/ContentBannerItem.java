package mctmods.resourcedatapackloader.content.item;

import net.minecraft.core.Direction;
import net.minecraft.world.item.StandingAndWallBlockItem;
import net.minecraft.world.level.block.Block;

public class ContentBannerItem extends StandingAndWallBlockItem {
    public ContentBannerItem(Block standing, Block wall, Properties properties) { super(standing, wall, properties, Direction.DOWN); }
}
