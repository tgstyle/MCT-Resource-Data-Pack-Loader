package mctmods.resourcedatapackloader.client.render;

import mctmods.resourcedatapackloader.content.block.ContentContainerBlockEntity;
import mctmods.resourcedatapackloader.content.def.ContainerDef;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.object.chest.ChestModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ContentContainerRenderer implements BlockEntityRenderer<ContentContainerBlockEntity, ContentContainerRenderer.State> {
    private static final Identifier VANILLA = Identifier.fromNamespaceAndPath("minecraft", "textures/entity/chest/normal.png");
    private final ChestModel model;

    public ContentContainerRenderer(BlockEntityRendererProvider.Context context) { this.model = new ChestModel(context.bakeLayer(ModelLayers.CHEST)); }

    public static final class State extends BlockEntityRenderState {
        @Nullable Identifier sheet;
        Direction facing = Direction.SOUTH;
        float open;
    }

    @Override @Nonnull public State createRenderState() { return new State(); }

    @Override public void extractRenderState(@Nonnull ContentContainerBlockEntity held, @Nonnull State state, float partial, @Nonnull Vec3 camera, @Nullable ModelFeatureRenderer.CrumblingOverlay breaking) {
        BlockEntityRenderer.super.extractRenderState(held, state, partial, camera, breaking);
        ContainerDef def = held.def();
        if (!def.chestModel()) {
            state.sheet = null;
            return;
        }
        BlockState block = held.getBlockState();
        state.facing = block.hasProperty(HorizontalDirectionalBlock.FACING) ? block.getValue(HorizontalDirectionalBlock.FACING) : Direction.SOUTH;
        state.sheet = def.chestTexture() == null ? VANILLA : def.chestTexture();
        float open = held.getOpenNess(partial);
        state.open = 1.0F - (1.0F - open) * (1.0F - open) * (1.0F - open);
    }

    @Override public void submit(@Nonnull State state, @Nonnull PoseStack pose, @Nonnull SubmitNodeCollector collector, @Nonnull CameraRenderState camera) {
        if (state.sheet == null) { return; }
        pose.pushPose();
        pose.translate(0.5F, 0.5F, 0.5F);
        pose.mulPose(Axis.YP.rotationDegrees(-state.facing.toYRot()));
        pose.translate(-0.5F, -0.5F, -0.5F);
        collector.submitModel(model, state.open, pose, RenderTypes.entityCutout(state.sheet), state.lightCoords, OverlayTexture.NO_OVERLAY, 0, state.breakProgress);
        pose.popPose();
    }
}
