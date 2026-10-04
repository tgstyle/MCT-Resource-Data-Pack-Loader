package mctmods.resourcedatapackloader.client;

import mctmods.resourcedatapackloader.content.def.BiomeDef;
import mctmods.resourcedatapackloader.content.def.DimensionDef;
import mctmods.resourcedatapackloader.content.def.SkyLookDef;
import mctmods.resourcedatapackloader.content.worldgen.ContentBiomes;
import mctmods.resourcedatapackloader.content.worldgen.ContentDimensions;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
import net.neoforged.neoforge.client.model.data.ModelData;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class ContentSnowTint {
    private static final int WHITE = 0xFFFFFF;
    private static final ColorResolver SNOW = (biome, x, z) -> column(biome);

    private ContentSnowTint() {}

    private static boolean wanted() {
        for (DimensionDef def : ContentDimensions.all()) {
            if (def.look() != null && def.look().snowColor() != SkyLookDef.UNSET) { return true; }
        }
        return ContentBiomes.tintsSnow();
    }

    private static Block[] blocks() { return new Block[] {Blocks.SNOW, Blocks.SNOW_BLOCK}; }

    public static void resolvers(RegisterColorHandlersEvent.ColorResolvers event) {
        if (wanted()) { event.register(SNOW); }
    }

    public static void colors(RegisterColorHandlersEvent.Block event) {
        if (wanted()) { event.register((state, level, pos, index) -> level == null || pos == null ? WHITE : level.getBlockTint(pos, SNOW), blocks()); }
    }

    public static void models(ModelEvent.ModifyBakingResult event) {
        if (!wanted()) { return; }
        Set<ModelResourceLocation> done = new HashSet<>();
        for (Block block : blocks()) {
            for (BlockState state : block.getStateDefinition().getPossibleStates()) {
                ModelResourceLocation location = BlockModelShaper.stateToModelLocation(state);
                BakedModel model = event.getModels().get(location);
                if (model != null && done.add(location)) { event.getModels().put(location, new Tinted(model)); }
            }
        }
    }

    private static int column(Biome biome) {
        ClientLevel level = Minecraft.getInstance().level;
        SkyLookDef look = ContentFogSampler.look(level);
        int fallback = look == null || look.snowColor() == SkyLookDef.UNSET ? WHITE : look.snowColor();
        if (level == null) { return fallback; }
        ResourceLocation key = level.registryAccess().registryOrThrow(Registries.BIOME).getKey(biome);
        BiomeDef def = key == null ? null : ContentBiomes.def(key);
        return def == null || def.snowColor() == BiomeDef.NO_COLOR ? fallback : def.snowColor() & WHITE;
    }

    private static final class Tinted extends BakedModelWrapper<BakedModel> {
        private final Map<Direction, List<BakedQuad>> sides = new EnumMap<>(Direction.class);
        private final List<BakedQuad> general;

        private Tinted(BakedModel model) {
            super(model);
            RandomSource random = RandomSource.create(42L);
            for (Direction side : Direction.values()) { sides.put(side, tint(model.getQuads(null, side, random, ModelData.EMPTY, null))); }
            general = tint(model.getQuads(null, null, random, ModelData.EMPTY, null));
        }

        @Override @Nonnull public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull RandomSource rand) { return side == null ? general : sides.get(side); }

        @Override @Nonnull public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull RandomSource rand, @Nonnull ModelData extraData, @Nullable RenderType renderType) { return getQuads(state, side, rand); }

        private static List<BakedQuad> tint(List<BakedQuad> quads) {
            List<BakedQuad> out = new ArrayList<>(quads.size());
            for (BakedQuad quad : quads) { out.add(new BakedQuad(quad.getVertices(), 0, quad.getDirection(), quad.getSprite(), quad.isShade(), quad.hasAmbientOcclusion())); }
            return List.copyOf(out);
        }
    }
}
