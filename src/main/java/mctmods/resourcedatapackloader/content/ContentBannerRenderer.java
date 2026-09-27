package mctmods.resourcedatapackloader.content;

import mctmods.resourcedatapackloader.content.block.ContentBannerBlock;
import mctmods.resourcedatapackloader.content.block.ContentBannerBlockEntity;
import mctmods.resourcedatapackloader.content.block.ContentWallBannerBlock;
import mctmods.resourcedatapackloader.content.interfaces.IContentBanner;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Transformation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.object.banner.BannerFlagModel;
import net.minecraft.client.model.object.banner.BannerModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BannerRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Unit;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3fc;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class ContentBannerRenderer implements BlockEntityRenderer<ContentBannerBlockEntity, ContentBannerRenderer.State> {
    private static final Identifier FALLBACK = Identifier.fromNamespaceAndPath("minecraft", "textures/entity/banner_base.png");
    private final Map<Block, Identifier> textures = new HashMap<>();
    private final BannerModel standing;
    private final BannerModel wall;
    private final BannerFlagModel standingFlag;
    private final BannerFlagModel wallFlag;

    public static final class State extends BlockEntityRenderState {
        boolean hidden;
        boolean onWall;
        float phase;
        Transformation transformation = Transformation.IDENTITY;
        Identifier texture = FALLBACK;
    }

    public ContentBannerRenderer(BlockEntityRendererProvider.Context context) { this(context.entityModelSet()); }

    public ContentBannerRenderer(EntityModelSet models) {
        this.standing = new BannerModel(models.bakeLayer(ModelLayers.STANDING_BANNER));
        this.wall = new BannerModel(models.bakeLayer(ModelLayers.WALL_BANNER));
        this.standingFlag = new BannerFlagModel(models.bakeLayer(ModelLayers.STANDING_BANNER_FLAG));
        this.wallFlag = new BannerFlagModel(models.bakeLayer(ModelLayers.WALL_BANNER_FLAG));
    }

    @Override @Nonnull public State createRenderState() { return new State(); }

    @Override public void extractRenderState(@Nonnull ContentBannerBlockEntity entity, @Nonnull State state, float partialTick, @Nonnull Vec3 camera, @Nullable ModelFeatureRenderer.CrumblingOverlay breaking) {
        BlockEntityRenderer.super.extractRenderState(entity, state, partialTick, camera, breaking);
        BlockState blockState = entity.getBlockState();
        state.hidden = entity.getLevel() != null && blockState.getBlock() instanceof IContentBanner banner && banner.modeled();
        state.texture = texture(blockState.getBlock());
        state.onWall = !(blockState.getBlock() instanceof ContentBannerBlock);
        state.transformation = state.onWall ? BannerRenderer.TRANSFORMATIONS.wallTransformation(blockState.getValue(ContentWallBannerBlock.FACING)) : BannerRenderer.TRANSFORMATIONS.freeTransformations(blockState.getValue(ContentBannerBlock.ROTATION));
        long time = entity.getLevel() == null ? 0L : entity.getLevel().getGameTime();
        BlockPos at = entity.getBlockPos();
        state.phase = ((float) Math.floorMod(at.getX() * 7L + at.getY() * 9L + at.getZ() * 13L + time, 100L) + partialTick) / 100.0F;
    }

    @Override public void submit(@Nonnull State state, @Nonnull PoseStack pose, @Nonnull SubmitNodeCollector collector, @Nonnull CameraRenderState camera) {
        if (state.hidden) { return; }
        pose.pushPose();
        pose.mulPose(state.transformation);
        submit(pose, collector, state.onWall ? wall : standing, state.onWall ? wallFlag : standingFlag, state.texture, state.lightCoords, OverlayTexture.NO_OVERLAY, state.phase, 0);
        pose.popPose();
    }

    public void getExtents(Consumer<Vector3fc> output) {
        PoseStack pose = new PoseStack();
        standing.root().getExtentsForGui(pose, output);
        standingFlag.setupAnim(0.0F);
        standingFlag.root().getExtentsForGui(pose, output);
    }

    public void submitItem(Block block, PoseStack pose, SubmitNodeCollector collector, int light, int overlay, int outline) { submit(pose, collector, standing, standingFlag, texture(block), light, overlay, 0.0F, outline); }

    private static void submit(PoseStack pose, SubmitNodeCollector collector, BannerModel model, BannerFlagModel flag, Identifier texture, int light, int overlay, float phase, int outline) {
        collector.submitModel(model, Unit.INSTANCE, pose, texture, light, overlay, outline, null);
        collector.submitModel(flag, phase, pose, texture, light, overlay, outline, null);
    }

    private Identifier texture(Block block) {
        return textures.computeIfAbsent(block, held -> {
            Identifier asked = held instanceof IContentBanner banner ? banner.texture() : FALLBACK;
            return Minecraft.getInstance().getResourceManager().getResource(asked).isPresent() ? asked : FALLBACK;
        });
    }
}
