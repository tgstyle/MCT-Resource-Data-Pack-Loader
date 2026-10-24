package mctmods.resourcedatapackloader.client;

import mctmods.resourcedatapackloader.content.ContentRegistry;
import mctmods.resourcedatapackloader.content.block.ContentLeavesBlock;
import mctmods.resourcedatapackloader.content.types.ContentBlockTypes;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.ChunkRenderTypeSet;
import net.minecraftforge.client.RenderTypeHelper;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.model.BakedModelWrapper;
import net.minecraftforge.client.model.data.ModelData;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class ContentRenderLayers {
    private ContentRenderLayers() {}

    @Nullable private static RenderType layer(ContentRegistry.BlockEntry entry) {
        String name = ContentBlockTypes.renderType(entry.def());
        if (entry.block() instanceof ContentLeavesBlock leaves) { return leaves.solidLayer() ? RenderType.solid() : null; }
        if (name == null) { return null; }
        return switch (name) {
            case "minecraft:cutout" -> RenderType.cutout();
            case "minecraft:cutout_mipped" -> RenderType.cutoutMipped();
            case "minecraft:translucent" -> RenderType.translucent();
            default -> null;
        };
    }

    public static void models(ModelEvent.ModifyBakingResult event) {
        Set<ResourceLocation> done = new HashSet<>();
        for (ContentRegistry.BlockEntry entry : ContentRegistry.blocks()) {
            RenderType layer = layer(entry);
            if (layer == null) { continue; }
            for (BlockState state : entry.block().getStateDefinition().getPossibleStates()) {
                ResourceLocation location = BlockModelShaper.stateToModelLocation(state);
                BakedModel model = event.getModels().get(location);
                if (model != null && done.add(location)) { event.getModels().put(location, new Layered(model, layer)); }
            }
        }
        for (ContentRegistry.ItemEntry entry : ContentRegistry.items()) {
            if (entry.block() == null) { continue; }
            RenderType layer = layer(entry.block());
            if (layer == null) { continue; }
            ResourceLocation location = new ModelResourceLocation(entry.id(), "inventory");
            BakedModel model = event.getModels().get(location);
            if (model != null && done.add(location)) { event.getModels().put(location, new Layered(model, layer)); }
        }
    }

    private static final class Layered extends BakedModelWrapper<BakedModel> {
        private final RenderType layer;
        private final RenderType from;

        private Layered(BakedModel model, RenderType layer) {
            super(model);
            this.layer = layer;
            this.from = layer == RenderType.solid() ? RenderType.cutoutMipped() : RenderType.solid();
        }

        @Override @Nonnull public ChunkRenderTypeSet getRenderTypes(@Nonnull BlockState state, @Nonnull RandomSource rand, @Nonnull ModelData data) {
            ChunkRenderTypeSet original = super.getRenderTypes(state, rand, data);
            if (!original.contains(from)) { return original; }
            List<RenderType> types = new ArrayList<>();
            for (RenderType type : original) { types.add(type == from ? layer : type); }
            return ChunkRenderTypeSet.of(types);
        }

        @Override @Nonnull public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull RandomSource rand, @Nonnull ModelData extraData, @Nullable RenderType renderType) {
            if (state == null || !layer.equals(renderType)) { return super.getQuads(state, side, rand, extraData, renderType); }
            ChunkRenderTypeSet original = super.getRenderTypes(state, rand, extraData);
            List<BakedQuad> quads = new ArrayList<>();
            if (original.contains(from)) { quads.addAll(super.getQuads(state, side, rand, extraData, from)); }
            if (original.contains(layer)) { quads.addAll(super.getQuads(state, side, rand, extraData, layer)); }
            return quads;
        }

        @Override @Nonnull public List<RenderType> getRenderTypes(@Nonnull ItemStack stack, boolean fabulous) {
            List<RenderType> original = super.getRenderTypes(stack, fabulous);
            return original.equals(List.of(RenderTypeHelper.getFallbackItemRenderType(stack, originalModel, fabulous))) ? List.of(RenderTypeHelper.getFallbackItemRenderType(stack, this, fabulous)) : original;
        }
    }
}
