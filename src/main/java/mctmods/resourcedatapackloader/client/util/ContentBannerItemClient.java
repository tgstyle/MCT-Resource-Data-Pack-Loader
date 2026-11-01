package mctmods.resourcedatapackloader.client.util;

import mctmods.resourcedatapackloader.client.renderer.ContentBannerItemRenderer;

import javax.annotation.Nonnull;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

public final class ContentBannerItemClient implements IClientItemExtensions {
    @Override @Nonnull public BlockEntityWithoutLevelRenderer getCustomRenderer() { return ContentBannerItemRenderer.get(); }
}
