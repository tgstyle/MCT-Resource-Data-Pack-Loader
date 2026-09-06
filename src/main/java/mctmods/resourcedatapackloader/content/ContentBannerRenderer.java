package mctmods.resourcedatapackloader.content;

import mctmods.resourcedatapackloader.content.block.ContentBannerBlock;
import mctmods.resourcedatapackloader.content.block.ContentBannerBlockEntity;
import mctmods.resourcedatapackloader.content.block.ContentWallBannerBlock;
import mctmods.resourcedatapackloader.content.block.IContentBanner;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RotationSegment;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nonnull;

public final class ContentBannerRenderer implements BlockEntityRenderer<ContentBannerBlockEntity> {
    private static final ResourceLocation FALLBACK = ResourceLocation.fromNamespaceAndPath("minecraft", "textures/entity/banner_base.png");
    private static final float SCALE = 0.6666667F;
    private final ModelPart flag;
    private final ModelPart pole;
    private final ModelPart bar;
    private final Map<Block, ResourceLocation> textures = new HashMap<>();

    public ContentBannerRenderer(BlockEntityRendererProvider.Context context) {
        ModelPart root = context.bakeLayer(ModelLayers.BANNER);
        this.flag = root.getChild("flag");
        this.pole = root.getChild("pole");
        this.bar = root.getChild("bar");
    }

    @Override public void render(@Nonnull ContentBannerBlockEntity entity, float partialTick, @Nonnull PoseStack pose, @Nonnull MultiBufferSource buffer, int light, int overlay) {
        BlockState state = entity.getBlockState();
        pose.pushPose();
        long time;
        if (entity.getLevel() == null) {
            time = 0L;
            pose.translate(0.5F, 0.5F, 0.5F);
            pole.visible = true;
        }
        else {
            time = entity.getLevel().getGameTime();
            if (state.getBlock() instanceof ContentBannerBlock) {
                pose.translate(0.5F, 0.5F, 0.5F);
                pose.mulPose(Axis.YP.rotationDegrees(-RotationSegment.convertToDegrees(state.getValue(ContentBannerBlock.ROTATION))));
                pole.visible = true;
            }
            else {
                pose.translate(0.5F, -0.16666667F, 0.5F);
                pose.mulPose(Axis.YP.rotationDegrees(-state.getValue(ContentWallBannerBlock.FACING).toYRot()));
                pose.translate(0.0F, -0.3125F, -0.4375F);
                pole.visible = false;
            }
        }
        pose.pushPose();
        pose.scale(SCALE, -SCALE, -SCALE);
        VertexConsumer consumer = buffer.getBuffer(RenderType.entitySolid(texture(state.getBlock())));
        pole.render(pose, consumer, light, overlay);
        bar.render(pose, consumer, light, overlay);
        BlockPos at = entity.getBlockPos();
        float swing = ((float) Math.floorMod(at.getX() * 7L + at.getY() * 9L + at.getZ() * 13L + time, 100L) + partialTick) / 100.0F;
        flag.xRot = (-0.0125F + 0.01F * Mth.cos((float) (Math.PI * 2) * swing)) * (float) Math.PI;
        flag.y = -32.0F;
        flag.render(pose, consumer, light, overlay);
        pose.popPose();
        pose.popPose();
    }

    private ResourceLocation texture(Block block) {
        return textures.computeIfAbsent(block, held -> {
            ResourceLocation asked = held instanceof IContentBanner banner ? banner.texture() : FALLBACK;
            return Minecraft.getInstance().getResourceManager().getResource(asked).isPresent() ? asked : FALLBACK;
        });
    }
}
