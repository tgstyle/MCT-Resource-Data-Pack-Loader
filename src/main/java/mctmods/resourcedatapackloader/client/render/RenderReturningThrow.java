package mctmods.resourcedatapackloader.client.render;

import mctmods.resourcedatapackloader.content.entity.EntityReturningThrow;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import javax.annotation.Nonnull;

public final class RenderReturningThrow extends Render<EntityReturningThrow> {
    public RenderReturningThrow(RenderManager manager) { super(manager); }

    @Override public void doRender(@Nonnull EntityReturningThrow entity, double x, double y, double z, float entityYaw, float partialTicks) {
        ItemStack stack = entity.stack();
        if (stack.isEmpty()) { return; }
        GlStateManager.pushMatrix();
        GlStateManager.translate((float) x, (float) y + 0.25F, (float) z);
        GlStateManager.rotate(entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks - 90.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate((entity.ticksExisted + partialTicks) * -45.0F, 0.0F, 0.0F, 1.0F);
        bindEntityTexture(entity);
        Minecraft.getMinecraft().getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.GROUND);
        GlStateManager.popMatrix();
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    @Override @Nonnull protected ResourceLocation getEntityTexture(@Nonnull EntityReturningThrow entity) { return TextureMap.LOCATION_BLOCKS_TEXTURE; }
}
