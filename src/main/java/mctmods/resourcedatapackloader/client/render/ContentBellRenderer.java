package mctmods.resourcedatapackloader.client.render;

import mctmods.resourcedatapackloader.compat.ClientCompat;
import mctmods.resourcedatapackloader.content.ContentBells;
import mctmods.resourcedatapackloader.content.block.ContentBellBlockEntity;
import mctmods.resourcedatapackloader.content.interfaces.IContentBell;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.block.dispatch.BlockModelRotation;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.SimpleModelWrapper;
import net.minecraft.client.resources.model.cuboid.MissingCuboidModel;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.model.standalone.SimpleUnbakedStandaloneModel;
import net.neoforged.neoforge.client.model.standalone.StandaloneModelKey;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ContentBellRenderer implements BlockEntityRenderer<ContentBellBlockEntity, ContentBellRenderer.State> {
    private static final float PIVOT_Y = 0.75F;
    private static final String MISSING = MissingCuboidModel.LOCATION.toString();
    private static final Map<Identifier, StandaloneModelKey<List<BlockStateModelPart>>> BODIES = new ConcurrentHashMap<>();

    @SuppressWarnings("unused") public ContentBellRenderer(BlockEntityRendererProvider.Context context) {}

    public static final class State extends BlockEntityRenderState {
        List<BlockStateModelPart> body = List.of();
        float tiltX;
        float tiltZ;
    }

    private static StandaloneModelKey<List<BlockStateModelPart>> body(Identifier id) { return BODIES.computeIfAbsent(id, held -> new StandaloneModelKey<>(ContentBells.body(held)::toString)); }

    public static void register(ModelEvent.RegisterStandalone event, Identifier id) {
        event.register(body(id), new SimpleUnbakedStandaloneModel<>(ContentBells.body(id), (model, baker, _) -> MISSING.equals(model.debugName()) ? List.of() : List.of(SimpleModelWrapper.bake(baker, model, BlockModelRotation.IDENTITY))));
    }

    @Override @Nonnull public State createRenderState() { return new State(); }

    @Override public void extractRenderState(@Nonnull ContentBellBlockEntity held, @Nonnull State state, float partial, @Nonnull Vec3 camera, @Nullable ModelFeatureRenderer.CrumblingOverlay breaking) {
        BlockEntityRenderer.super.extractRenderState(held, state, partial, camera, breaking);
        state.body = List.of();
        state.tiltX = 0.0F;
        state.tiltZ = 0.0F;
        Block block = held.getBlockState().getBlock();
        if (!(block instanceof IContentBell bell) || !bell.swings()) { return; }
        List<BlockStateModelPart> model = Minecraft.getInstance().getModelManager().getStandaloneModel(body(BuiltInRegistries.BLOCK.getKey(block)));
        if (model == null || model.isEmpty()) { return; }
        state.body = model;
        if (!held.shaking()) { return; }
        float time = held.ticks() + partial;
        float swing = Mth.sin(time / (float) Math.PI) / (4.0F + time / 3.0F);
        switch (held.clicked()) {
            case NORTH -> state.tiltX = -swing;
            case SOUTH -> state.tiltX = swing;
            case EAST -> state.tiltZ = -swing;
            case WEST -> state.tiltZ = swing;
            default -> { }
        }
    }

    @Override public void submit(@Nonnull State state, @Nonnull PoseStack pose, @Nonnull SubmitNodeCollector collector, @Nonnull CameraRenderState camera) {
        if (state.body.isEmpty()) { return; }
        pose.pushPose();
        pose.translate(0.5D, PIVOT_Y, 0.5D);
        pose.mulPose(Axis.ZP.rotation(state.tiltZ));
        pose.mulPose(Axis.XP.rotation(state.tiltX));
        pose.translate(-0.5D, -PIVOT_Y, -0.5D);
        collector.submitBlockModel(pose, ClientCompat.cutoutBlockSheet(), state.body, BlockModelRenderState.EMPTY_TINTS, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
        pose.popPose();
    }
}
