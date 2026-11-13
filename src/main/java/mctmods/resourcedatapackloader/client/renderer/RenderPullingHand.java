package mctmods.resourcedatapackloader.client.renderer;

import mctmods.resourcedatapackloader.common.entities.PullingHand;

import javax.annotation.Nonnull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public final class RenderPullingHand extends Render<PullingHand> {
    public RenderPullingHand(RenderManager manager) { super(manager); }

    @Override public void doRender(@Nonnull PullingHand entity, double x, double y, double z, float entityYaw, float partialTicks) {
        ItemStack stack = entity.stack();
        if (stack.isEmpty()) { return; }
        bindEntityTexture(entity);
        GlStateManager.pushMatrix();
        GlStateManager.translate((float) x, (float) y + 0.25F, (float) z);
        GlStateManager.rotate(-renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate((renderManager.options.thirdPersonView == 2 ? -1 : 1) * renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
        Minecraft.getMinecraft().getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.GROUND);
        GlStateManager.popMatrix();
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    @Override @Nonnull protected ResourceLocation getEntityTexture(@Nonnull PullingHand entity) { return TextureMap.LOCATION_BLOCKS_TEXTURE; }
}
