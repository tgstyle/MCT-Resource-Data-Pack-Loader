package mctmods.resourcedatapackloader.client.render;

import mctmods.resourcedatapackloader.content.entity.ReturningThrow;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import javax.annotation.Nonnull;

public final class ReturningThrowRenderer extends EntityRenderer<ReturningThrow, ReturningThrowRenderer.State> {
    private final ItemModelResolver items;

    public ReturningThrowRenderer(EntityRendererProvider.Context context) {
        super(context);
        items = context.getItemModelResolver();
    }

    public static final class State extends EntityRenderState {
        final ItemStackRenderState item = new ItemStackRenderState();
        float yaw;
    }

    @Override @Nonnull public State createRenderState() { return new State(); }

    @Override public void extractRenderState(@Nonnull ReturningThrow entity, @Nonnull State state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        state.yaw = Mth.lerp(partialTicks, entity.yRotO, entity.getYRot());
        items.updateForNonLiving(state.item, entity.stack(), ItemDisplayContext.GROUND, entity);
    }

    @Override public void submit(@Nonnull State state, @Nonnull PoseStack pose, @Nonnull SubmitNodeCollector collector, @Nonnull CameraRenderState camera) {
        if (state.item.isEmpty()) { return; }
        pose.pushPose();
        pose.translate(0.0F, 0.25F, 0.0F);
        pose.last().rotate(Axis.YP.rotationDegrees(state.yaw - 90.0F));
        pose.last().rotate(Axis.ZP.rotationDegrees(state.ageInTicks * -45.0F));
        state.item.submit(pose, collector, state.lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor);
        pose.popPose();
        super.submit(state, pose, collector, camera);
    }
}
