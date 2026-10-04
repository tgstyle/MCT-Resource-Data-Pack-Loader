package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.def.BiomeDef;
import mctmods.resourcedatapackloader.content.def.DimensionDef;
import mctmods.resourcedatapackloader.content.def.SkyLookDef;
import mctmods.resourcedatapackloader.content.types.ContentTypes;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.BakedModelWrapper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@SideOnly(Side.CLIENT) public final class ContentSnowTint {
    private static final int WHITE = 0xFFFFFF;
    private static final Block[] SNOW = {Blocks.SNOW_LAYER, Blocks.SNOW};

    private ContentSnowTint() {}

    private static boolean wanted() {
        for (DimensionDef def : ContentDimensions.all().values()) {
            if (def.look != null && def.look.snowColor != SkyLookDef.UNSET) { return true; }
        }
        for (BiomeDef def : ContentBiomes.defs()) {
            if (def.snowColor != ContentTypes.NO_COLOR) { return true; }
        }
        return false;
    }

    @SubscribeEvent public static void registerColors(ColorHandlerEvent.Block event) {
        if (wanted()) { event.getBlockColors().registerBlockColorHandler((state, world, pos, layer) -> color(world, pos), SNOW); }
    }

    @SubscribeEvent public static void tintModels(ModelBakeEvent event) {
        if (!wanted()) { return; }
        Set<ModelResourceLocation> done = new HashSet<>();
        for (Block block : SNOW) {
            for (Map.Entry<IBlockState, ModelResourceLocation> entry : event.getModelManager().getBlockModelShapes().getBlockStateMapper().getVariants(block).entrySet()) {
                ModelResourceLocation location = entry.getValue();
                IBakedModel model = event.getModelRegistry().getObject(location);
                if (model != null && done.add(location)) { event.getModelRegistry().putObject(location, new Tinted(model)); }
            }
        }
    }

    private static int color(@Nullable IBlockAccess world, @Nullable BlockPos pos) {
        if (world == null || pos == null) { return WHITE; }
        SkyLookDef look = ContentFogSampler.look(Minecraft.getMinecraft().world);
        int fallback = look == null || look.snowColor == SkyLookDef.UNSET ? WHITE : look.snowColor;
        int red = 0;
        int green = 0;
        int blue = 0;
        for (BlockPos.MutableBlockPos at : BlockPos.getAllInBoxMutable(pos.add(-1, 0, -1), pos.add(1, 0, 1))) {
            int color = column(world.getBiome(at), fallback);
            red += color >> 16 & 255;
            green += color >> 8 & 255;
            blue += color & 255;
        }
        return (red / 9 & 255) << 16 | (green / 9 & 255) << 8 | blue / 9 & 255;
    }

    private static int column(Biome biome, int fallback) {
        if (!(biome instanceof ContentBiome)) { return fallback; }
        int own = ((ContentBiome) biome).snowColor();
        return own == ContentTypes.NO_COLOR ? fallback : own & WHITE;
    }

    private static final class Tinted extends BakedModelWrapper<IBakedModel> {
        private final Map<EnumFacing, List<BakedQuad>> sides = new EnumMap<>(EnumFacing.class);
        private final List<BakedQuad> general;

        private Tinted(IBakedModel model) {
            super(model);
            for (EnumFacing side : EnumFacing.values()) { sides.put(side, tint(model.getQuads(null, side, 0L))); }
            general = tint(model.getQuads(null, null, 0L));
        }

        @Override @Nonnull public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) { return side == null ? general : sides.get(side); }

        private static List<BakedQuad> tint(List<BakedQuad> quads) {
            List<BakedQuad> out = new ArrayList<>(quads.size());
            for (BakedQuad quad : quads) { out.add(new BakedQuad(quad.getVertexData(), 0, quad.getFace(), quad.getSprite(), quad.shouldApplyDiffuseLighting(), quad.getFormat())); }
            return Collections.unmodifiableList(out);
        }
    }
}
