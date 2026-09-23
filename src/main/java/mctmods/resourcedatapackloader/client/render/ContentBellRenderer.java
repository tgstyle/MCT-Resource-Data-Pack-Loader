package mctmods.resourcedatapackloader.client.render;

import mctmods.resourcedatapackloader.content.ContentBells;
import mctmods.resourcedatapackloader.content.block.ContentBellBlockEntity;
import mctmods.resourcedatapackloader.content.interfaces.IContentBell;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;

import javax.annotation.Nonnull;

public class ContentBellRenderer implements BlockEntityRenderer<ContentBellBlockEntity> {
    private static final float PIVOT_Y = 0.75F;
    private final BlockRenderDispatcher blocks;

    public ContentBellRenderer(BlockEntityRendererProvider.Context context) { this.blocks = context.getBlockRenderDispatcher(); }

    public static ModelResourceLocation body(ResourceLocation id) { return ModelResourceLocation.standalone(ContentBells.body(id)); }

    @Override public void render(@Nonnull ContentBellBlockEntity held, float partial, @Nonnull PoseStack pose, @Nonnull MultiBufferSource buffers, int light, int overlay) {
        BlockState state = held.getBlockState();
        Block block = state.getBlock();
        if (!(block instanceof IContentBell bell) || !bell.swings()) { return; }
        ResourceLocation id = BuiltInRegistries.BLOCK.getKey(block);
        ModelManager models = Minecraft.getInstance().getModelManager();
        BakedModel model = models.getModel(body(id));
        if (model == models.getMissingModel()) { return; }
        float tiltX = 0.0F;
        float tiltZ = 0.0F;
        if (held.shaking()) {
            float time = held.ticks() + partial;
            float swing = Mth.sin(time / (float) Math.PI) / (4.0F + time / 3.0F);
            switch (held.clicked()) {
                case NORTH -> tiltX = -swing;
                case SOUTH -> tiltX = swing;
                case EAST -> tiltZ = -swing;
                case WEST -> tiltZ = swing;
                default -> { }
            }
        }
        pose.pushPose();
        pose.translate(0.5D, PIVOT_Y, 0.5D);
        pose.mulPose(Axis.ZP.rotation(tiltZ));
        pose.mulPose(Axis.XP.rotation(tiltX));
        pose.translate(-0.5D, -PIVOT_Y, -0.5D);
        blocks.getModelRenderer().renderModel(pose.last(), buffers.getBuffer(RenderType.cutout()), state, model, 1.0F, 1.0F, 1.0F, light, overlay, ModelData.EMPTY, RenderType.cutout());
        pose.popPose();
    }
}
