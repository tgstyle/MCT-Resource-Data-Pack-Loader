package mctmods.resourcedatapackloader.client.renderer;

import mctmods.resourcedatapackloader.common.entities.ReturningThrow;

import javax.annotation.Nonnull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public final class RenderReturningThrow extends Render<ReturningThrow> {
    public RenderReturningThrow(RenderManager manager) { super(manager); }

    static void drawStack(Entity entity, ItemStack stack, double x, double y, double z, float partialTicks, float spin) {
        GlStateManager.pushMatrix();
        GlStateManager.translate((float) x, (float) y + 0.25F, (float) z);
        GlStateManager.rotate(entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks - 90.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(spin, 0.0F, 0.0F, 1.0F);
        Minecraft.getMinecraft().getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.GROUND);
        GlStateManager.popMatrix();
    }

    @Override public void doRender(@Nonnull ReturningThrow entity, double x, double y, double z, float entityYaw, float partialTicks) {
        ItemStack stack = entity.stack();
        if (stack.isEmpty()) { return; }
        bindEntityTexture(entity);
        drawStack(entity, stack, x, y, z, partialTicks, (entity.ticksExisted + partialTicks) * -45.0F);
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    @Override @Nonnull protected ResourceLocation getEntityTexture(@Nonnull ReturningThrow entity) { return TextureMap.LOCATION_BLOCKS_TEXTURE; }
}
