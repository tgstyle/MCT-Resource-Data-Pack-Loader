package mctmods.resourcedatapackloader.client;

import mctmods.resourcedatapackloader.content.def.BiomeDef;
import mctmods.resourcedatapackloader.content.def.DimensionDef;
import mctmods.resourcedatapackloader.content.def.SkyLookDef;
import mctmods.resourcedatapackloader.content.worldgen.ContentBiomes;
import mctmods.resourcedatapackloader.content.worldgen.ContentDimensions;

import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import java.util.List;
import javax.annotation.Nonnull;

public final class ContentSnowTint {
    private static final int WHITE = 0xFFFFFF;
    private static final ColorResolver SNOW = (biome, _, _) -> column(biome);
    private static boolean wanted;

    private ContentSnowTint() {}

    public static boolean tints(BlockState state) { return wanted && (state.is(Blocks.SNOW) || state.is(Blocks.SNOW_BLOCK)); }

    private static boolean wanted() {
        for (DimensionDef def : ContentDimensions.all()) {
            if (def.look() != null && def.look().snowColor() != SkyLookDef.UNSET) { return true; }
        }
        return ContentBiomes.tintsSnow();
    }

    public static void resolvers(RegisterColorHandlersEvent.ColorResolvers event) {
        wanted = wanted();
        if (wanted) { event.register(SNOW); }
    }

    public static void sources(RegisterColorHandlersEvent.BlockTintSources event) {
        wanted = wanted();
        if (!wanted) { return; }
        BlockTintSource source = new BlockTintSource() {
            @Override public int color(@Nonnull BlockState state) { return ARGB.opaque(WHITE); }

            @Override public int colorInWorld(@Nonnull BlockState state, @Nonnull BlockAndTintGetter level, @Nonnull BlockPos pos) { return ARGB.opaque(level.getBlockTint(pos, SNOW)); }
        };
        event.register(List.of(source), Blocks.SNOW, Blocks.SNOW_BLOCK);
    }

    private static int column(Biome biome) {
        ClientLevel level = Minecraft.getInstance().level;
        SkyLookDef look = ContentFogSampler.look(level);
        int fallback = look == null || look.snowColor() == SkyLookDef.UNSET ? WHITE : look.snowColor();
        if (level == null) { return fallback; }
        Identifier key = level.registryAccess().lookupOrThrow(Registries.BIOME).getKey(biome);
        BiomeDef def = key == null ? null : ContentBiomes.def(key);
        return def == null || def.snowColor() == BiomeDef.NO_COLOR ? fallback : def.snowColor() & WHITE;
    }
}
