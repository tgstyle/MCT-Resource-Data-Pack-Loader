package mctmods.resourcedatapackloader.client.render;

import mctmods.resourcedatapackloader.content.entity.ReturningThrow;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import javax.annotation.Nonnull;

public final class ReturningThrowRenderer extends EntityRenderer<ReturningThrow> {
    private final ItemRenderer items;

    public ReturningThrowRenderer(EntityRendererProvider.Context context) {
        super(context);
        items = context.getItemRenderer();
    }

    @Override public void render(@Nonnull ReturningThrow entity, float entityYaw, float partialTicks, @Nonnull PoseStack pose, @Nonnull MultiBufferSource buffers, int light) {
        ItemStack stack = entity.stack();
        if (stack.isEmpty()) { return; }
        pose.pushPose();
        pose.translate(0.0F, 0.25F, 0.0F);
        pose.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTicks, entity.yRotO, entity.getYRot()) - 90.0F));
        pose.mulPose(Axis.ZP.rotationDegrees((entity.tickCount + partialTicks) * -45.0F));
        items.renderStatic(stack, ItemDisplayContext.GROUND, light, OverlayTexture.NO_OVERLAY, pose, buffers, entity.level(), entity.getId());
        pose.popPose();
        super.render(entity, entityYaw, partialTicks, pose, buffers, light);
    }

    @Override @Nonnull public ResourceLocation getTextureLocation(@Nonnull ReturningThrow entity) { return InventoryMenu.BLOCK_ATLAS; }
}
