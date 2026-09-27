package mctmods.resourcedatapackloader.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.OrderedSubmitNodeCollector;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.MovingBlockRenderState;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ARGB;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TintedCollector implements OrderedSubmitNodeCollector {
    private static final Map<int[], Integer> ITEM_TINTS = new WeakHashMap<>();
    private final OrderedSubmitNodeCollector inner;
    private final int tint;

    private TintedCollector(OrderedSubmitNodeCollector inner, int tint) {
        this.inner = inner;
        this.tint = tint;
    }

    public static SubmitNodeCollector wrap(SubmitNodeCollector collector, int tint) { return tint == 0 ? collector : new Root(collector, tint); }

    public static int itemColor(int color, int[] tintLayers) {
        Integer tint = ITEM_TINTS.get(tintLayers);
        return tint == null ? color : multiply(color, tint);
    }

    private static int multiply(int color, int tint) { return ARGB.multiply(color, 0xFF000000 | tint); }

    @Override public void submitShadow(@Nonnull PoseStack poseStack, float radius, @Nonnull List<EntityRenderState.ShadowPiece> pieces) { inner.submitShadow(poseStack, radius, pieces); }

    @Override public void submitNameTag(@Nonnull PoseStack poseStack, @Nullable Vec3 attachment, int offset, @Nonnull Component name, boolean seeThrough, int lightCoords, double distanceSq, @Nonnull CameraRenderState camera) { inner.submitNameTag(poseStack, attachment, offset, name, seeThrough, lightCoords, distanceSq, camera); }

    @Override public void submitText(@Nonnull PoseStack poseStack, float x, float y, @Nonnull FormattedCharSequence text, boolean dropShadow, @Nonnull Font.DisplayMode mode, int lightCoords, int color, int backgroundColor, int outlineColor) { inner.submitText(poseStack, x, y, text, dropShadow, mode, lightCoords, color, backgroundColor, outlineColor); }

    @Override public void submitFlame(@Nonnull PoseStack poseStack, @Nonnull EntityRenderState state, @Nonnull Quaternionf rotation) { inner.submitFlame(poseStack, state, rotation); }

    @Override public void submitLeash(@Nonnull PoseStack poseStack, @Nonnull EntityRenderState.LeashState leash) { inner.submitLeash(poseStack, leash); }

    @Override public <S> void submitModel(@Nonnull Model<? super S> model, @Nonnull S state, @Nonnull PoseStack poseStack, @Nonnull RenderType renderType, int lightCoords, int overlayCoords, int tintedColor, @Nullable TextureAtlasSprite sprite, int outlineColor, @Nullable ModelFeatureRenderer.CrumblingOverlay crumbling) {
        inner.submitModel(model, state, poseStack, renderType, lightCoords, overlayCoords, multiply(tintedColor, tint), sprite, outlineColor, crumbling);
    }

    @Override public void submitModelPart(@Nonnull ModelPart part, @Nonnull PoseStack poseStack, @Nonnull RenderType renderType, int lightCoords, int overlayCoords, @Nullable TextureAtlasSprite sprite, boolean sheeted, boolean hasFoil, int tintedColor, @Nullable ModelFeatureRenderer.CrumblingOverlay crumbling, int outlineColor) {
        inner.submitModelPart(part, poseStack, renderType, lightCoords, overlayCoords, sprite, sheeted, hasFoil, multiply(tintedColor, tint), crumbling, outlineColor);
    }

    @Override public void submitMovingBlock(@Nonnull PoseStack poseStack, @Nonnull MovingBlockRenderState state) { inner.submitMovingBlock(poseStack, state); }

    @Override public void submitBlockModel(@Nonnull PoseStack poseStack, @Nonnull RenderType renderType, @Nonnull List<BlockStateModelPart> parts, @Nonnull int[] tintLayers, int lightCoords, int overlayCoords, int outlineColor) { inner.submitBlockModel(poseStack, renderType, parts, tintLayers, lightCoords, overlayCoords, outlineColor); }

    @Override public void submitMultiLayerBlockModel(@Nonnull PoseStack poseStack, @Nonnull List<BlockStateModelPart> parts, boolean translucent, @Nonnull int[] tintLayers, int lightCoords, int overlayCoords, int outlineColor) { inner.submitMultiLayerBlockModel(poseStack, parts, translucent, tintLayers, lightCoords, overlayCoords, outlineColor); }

    @Override public void submitBreakingBlockModel(@Nonnull PoseStack poseStack, @Nonnull BlockStateModel model, long seed, int progress) { inner.submitBreakingBlockModel(poseStack, model, seed, progress); }

    @Override public void submitItem(@Nonnull PoseStack poseStack, @Nonnull ItemDisplayContext context, int lightCoords, int overlayCoords, int outlineColor, @Nonnull int[] tintLayers, @Nonnull List<BakedQuad> quads, @Nonnull ItemStackRenderState.FoilType foil) {
        int[] marked = tintLayers.clone();
        ITEM_TINTS.put(marked, tint);
        inner.submitItem(poseStack, context, lightCoords, overlayCoords, outlineColor, marked, quads, foil);
    }

    @Override public void submitCustomGeometry(@Nonnull PoseStack poseStack, @Nonnull RenderType renderType, @Nonnull SubmitNodeCollector.CustomGeometryRenderer renderer) {
        inner.submitCustomGeometry(poseStack, renderType, (pose, consumer) -> renderer.render(pose, EntityTint.wrap(consumer, tint)));
    }

    @Override public void submitParticleGroup(@Nonnull SubmitNodeCollector.ParticleGroupRenderer renderer) { inner.submitParticleGroup(renderer); }

    private static final class Root extends TintedCollector implements SubmitNodeCollector {
        private final SubmitNodeCollector root;
        private final int tint;

        private Root(SubmitNodeCollector root, int tint) {
            super(root, tint);
            this.root = root;
            this.tint = tint;
        }

        @Override @Nonnull public OrderedSubmitNodeCollector order(int order) { return new TintedCollector(root.order(order), tint); }
    }
}
