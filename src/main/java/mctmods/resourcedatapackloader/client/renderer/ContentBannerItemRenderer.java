package mctmods.resourcedatapackloader.client.renderer;

import mctmods.resourcedatapackloader.common.banners.IContentBanner;
import mctmods.resourcedatapackloader.common.blocks.banner.logic.ContentBannerBlockEntity;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public final class ContentBannerItemRenderer extends BlockEntityWithoutLevelRenderer {
    @Nullable private static ContentBannerItemRenderer instance;
    private final Map<Block, ContentBannerBlockEntity> entities = new HashMap<>();

    private ContentBannerItemRenderer() { super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels()); }

    public static ContentBannerItemRenderer get() {
        if (instance == null) { instance = new ContentBannerItemRenderer(); }
        return instance;
    }

    @Override public void renderByItem(@Nonnull ItemStack stack, @Nonnull ItemDisplayContext context, @Nonnull PoseStack pose, @Nonnull MultiBufferSource buffer, int light, int overlay) {
        if (!(stack.getItem() instanceof BlockItem item) || !(item.getBlock() instanceof IContentBanner)) { return; }
        ContentBannerBlockEntity entity = entities.computeIfAbsent(item.getBlock(), block -> new ContentBannerBlockEntity(BlockPos.ZERO, block.defaultBlockState()));
        Minecraft.getInstance().getBlockEntityRenderDispatcher().renderItem(entity, pose, buffer, light, overlay);
    }
}
