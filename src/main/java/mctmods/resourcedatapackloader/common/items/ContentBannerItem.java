package mctmods.resourcedatapackloader.common.items;

import mctmods.resourcedatapackloader.client.util.ContentBannerItemClient;

import java.util.function.Consumer;
import javax.annotation.Nonnull;
import net.minecraft.core.Direction;
import net.minecraft.world.item.StandingAndWallBlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

public final class ContentBannerItem extends StandingAndWallBlockItem {
    public ContentBannerItem(Block standing, Block wall, Properties properties) { super(standing, wall, properties, Direction.DOWN); }

    @Override public void initializeClient(@Nonnull Consumer<IClientItemExtensions> consumer) { consumer.accept(new ContentBannerItemClient()); }
}
