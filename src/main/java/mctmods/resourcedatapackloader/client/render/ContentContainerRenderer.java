package mctmods.resourcedatapackloader.client.render;

import mctmods.resourcedatapackloader.content.block.ContentContainerBlockEntity;
import mctmods.resourcedatapackloader.content.def.ContainerDef;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import com.mojang.math.Axis;

import javax.annotation.Nonnull;

public class ContentContainerRenderer implements BlockEntityRenderer<ContentContainerBlockEntity> {
    private static final ResourceLocation VANILLA = ResourceLocation.fromNamespaceAndPath("minecraft", "textures/entity/chest/normal.png");
    private final ModelPart bottom;
    private final ModelPart lid;
    private final ModelPart lock;

    public ContentContainerRenderer(BlockEntityRendererProvider.Context context) {
        ModelPart root = context.bakeLayer(ModelLayers.CHEST);
        this.bottom = root.getChild("bottom");
        this.lid = root.getChild("lid");
        this.lock = root.getChild("lock");
    }

    @Override public void render(@Nonnull ContentContainerBlockEntity held, float partial, @Nonnull PoseStack pose, @Nonnull MultiBufferSource buffers, int light, int overlay) {
        ContainerDef def = held.def();
        if (!def.chestModel()) { return; }
        BlockState state = held.getBlockState();
        Direction facing = state.hasProperty(HorizontalDirectionalBlock.FACING) ? state.getValue(HorizontalDirectionalBlock.FACING) : Direction.SOUTH;
        pose.pushPose();
        pose.translate(0.5F, 0.5F, 0.5F);
        pose.mulPose(Axis.YP.rotationDegrees(-facing.toYRot()));
        pose.translate(-0.5F, -0.5F, -0.5F);
        float open = held.getOpenNess(partial);
        float eased = 1.0F - (1.0F - open) * (1.0F - open) * (1.0F - open);
        lid.xRot = -(eased * ((float) Math.PI / 2F));
        lock.xRot = lid.xRot;
        ResourceLocation sheet = def.chestTexture() == null ? VANILLA : def.chestTexture();
        VertexConsumer into = buffers.getBuffer(RenderType.entityCutout(sheet));
        lid.render(pose, into, light, overlay);
        lock.render(pose, into, light, overlay);
        bottom.render(pose, into, light, overlay);
        pose.popPose();
    }
}
