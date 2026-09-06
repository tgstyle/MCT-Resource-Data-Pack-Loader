package mctmods.resourcedatapackloader.content.item;

import mctmods.resourcedatapackloader.content.ContentBannerItemRenderer;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.Direction;
import net.minecraft.world.item.StandingAndWallBlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import java.util.function.Consumer;
import javax.annotation.Nonnull;

public class ContentBannerItem extends StandingAndWallBlockItem {
    public ContentBannerItem(Block standing, Block wall, Properties properties) { super(standing, wall, properties, Direction.DOWN); }

    @Override public void initializeClient(@Nonnull Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            @Override @Nonnull public BlockEntityWithoutLevelRenderer getCustomRenderer() { return ContentBannerItemRenderer.get(); }
        });
    }
}
