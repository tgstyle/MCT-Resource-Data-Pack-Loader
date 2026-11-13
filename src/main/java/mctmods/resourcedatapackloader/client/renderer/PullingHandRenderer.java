package mctmods.resourcedatapackloader.client.renderer;

import mctmods.resourcedatapackloader.common.entities.PullingHand;

import com.mojang.blaze3d.vertex.PoseStack;
import javax.annotation.Nonnull;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;

public final class PullingHandRenderer extends EntityRenderer<PullingHand, PullingHandRenderer.State> {
    private final ItemModelResolver items;

    public PullingHandRenderer(EntityRendererProvider.Context context) {
        super(context);
        items = context.getItemModelResolver();
    }

    public static final class State extends EntityRenderState {
        final ItemStackRenderState item = new ItemStackRenderState();
    }

    @Override @Nonnull public State createRenderState() { return new State(); }

    @Override public void extractRenderState(@Nonnull PullingHand entity, @Nonnull State state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        items.updateForNonLiving(state.item, entity.stack(), ItemDisplayContext.GROUND, entity);
    }

    @Override public void submit(@Nonnull State state, @Nonnull PoseStack pose, @Nonnull SubmitNodeCollector collector, @Nonnull CameraRenderState camera) {
        if (state.item.isEmpty()) { return; }
        pose.pushPose();
        pose.translate(0.0F, 0.25F, 0.0F);
        pose.last().rotate(camera.orientation);
        state.item.submit(pose, collector, state.lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor);
        pose.popPose();
        super.submit(state, pose, collector, camera);
    }
}
