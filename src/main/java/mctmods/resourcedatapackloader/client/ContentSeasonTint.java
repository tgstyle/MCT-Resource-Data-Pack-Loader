package mctmods.resourcedatapackloader.client;

import mctmods.resourcedatapackloader.content.def.DimensionDef;
import mctmods.resourcedatapackloader.content.def.SkyLookDef;
import mctmods.resourcedatapackloader.content.worldgen.ContentDimensions;
import mctmods.resourcedatapackloader.content.worldgen.ContentEnvironments;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.FoliageColor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.event.TickEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nullable;

public final class ContentSeasonTint {
    private static final int WHITE = 0xFFFFFF;
    private static final int GRASS = 0;
    private static final int FOLIAGE = 1;
    private static final int[] NONE = {WHITE, WHITE};
    private static final Map<ResourceLocation, Map<Biome, int[]>> CACHE = new ConcurrentHashMap<>();
    private static final ColorResolver EVERGREEN = (biome, x, z) -> multiply(FoliageColor.getEvergreenColor(), tints(biome)[FOLIAGE]);
    private static final ColorResolver BIRCH = (biome, x, z) -> multiply(FoliageColor.getBirchColor(), tints(biome)[FOLIAGE]);
    @Nullable private static volatile Boolean found;
    private static volatile boolean dirty;
    @Nullable private static List<Object> last;

    private ContentSeasonTint() {}

    static boolean wanted() {
        Boolean known = found;
        if (known == null) {
            known = compute();
            found = known;
        }
        return known;
    }

    private static boolean compute() {
        for (DimensionDef def : ContentDimensions.all()) {
            if (def.look() != null && def.look().tintsPlants()) { return true; }
        }
        for (SkyLookDef look : ContentEnvironments.looks()) {
            if (look.tintsPlants()) { return true; }
        }
        return false;
    }

    public static void changed() { dirty = true; }

    public static int grassBase(Biome biome, int color) { return swamp(biome) ? color : tinted(biome, GRASS, color); }

    public static int grass(Biome biome, int color) { return swamp(biome) ? tinted(biome, GRASS, color) : color; }

    private static boolean swamp(Biome biome) { return biome.getSpecialEffects().getGrassColorModifier() == BiomeSpecialEffects.GrassColorModifier.SWAMP; }

    public static int foliage(Biome biome, int color) { return tinted(biome, FOLIAGE, color); }

    public static void resolvers(RegisterColorHandlersEvent.ColorResolvers event) {
        if (wanted()) {
            event.register(EVERGREEN);
            event.register(BIRCH);
        }
    }

    public static void colors(RegisterColorHandlersEvent.Block event) {
        if (!wanted()) { return; }
        event.register((state, level, pos, index) -> level == null || pos == null ? FoliageColor.getEvergreenColor() : level.getBlockTint(pos, EVERGREEN), Blocks.SPRUCE_LEAVES);
        event.register((state, level, pos, index) -> level == null || pos == null ? FoliageColor.getBirchColor() : level.getBlockTint(pos, BIRCH), Blocks.BIRCH_LEAVES);
    }

    public static void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !dirty) { return; }
        dirty = false;
        Minecraft client = Minecraft.getInstance();
        ClientLevel level = client.level;
        if (level == null) { return; }
        CACHE.clear();
        if (!wanted() && !ContentSnowTint.wanted()) { return; }
        ResourceLocation dimension = level.dimension().location();
        List<Object> now = new ArrayList<>();
        now.add(dimension);
        now.addAll(ContentEnvironments.tints(dimension));
        List<Object> before = last == null ? List.of(dimension) : last;
        last = now;
        if (!now.equals(before)) { client.levelRenderer.allChanged(); }
    }

    public static void forget() {
        CACHE.clear();
        last = null;
        dirty = false;
    }

    private static int tinted(Biome biome, int which, int color) {
        if (!wanted()) { return color; }
        int tint = tints(biome)[which];
        return tint == WHITE ? color : multiply(color, tint);
    }

    private static int multiply(int color, int tint) {
        int red = (color >> 16 & 255) * (tint >> 16 & 255) / 255;
        int green = (color >> 8 & 255) * (tint >> 8 & 255) / 255;
        int blue = (color & 255) * (tint & 255) / 255;
        return color & 0xFF000000 | red << 16 | green << 8 | blue;
    }

    private static int[] tints(Biome biome) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) { return NONE; }
        ResourceLocation dimension = level.dimension().location();
        return CACHE.computeIfAbsent(dimension, ignored -> new ConcurrentHashMap<>()).computeIfAbsent(biome, key -> look(level, dimension, key));
    }

    private static int[] look(ClientLevel level, ResourceLocation dimension, Biome biome) {
        Holder<Biome> holder = level.registryAccess().registryOrThrow(Registries.BIOME).wrapAsHolder(biome);
        DimensionDef def = ContentDimensions.def(dimension, holder);
        SkyLookDef look = def == null ? null : def.look();
        if (look == null) { return NONE; }
        return new int[] {look.grassTint() == SkyLookDef.UNSET ? WHITE : look.grassTint(), look.foliageTint() == SkyLookDef.UNSET ? WHITE : look.foliageTint()};
    }
}
