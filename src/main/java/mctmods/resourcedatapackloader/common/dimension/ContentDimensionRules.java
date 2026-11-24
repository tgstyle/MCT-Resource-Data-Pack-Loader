package mctmods.resourcedatapackloader.common.dimension;

import mctmods.resourcedatapackloader.client.renderer.sky.ContentCloudRenderer;
import mctmods.resourcedatapackloader.client.renderer.sky.ContentFogSampler;
import mctmods.resourcedatapackloader.client.renderer.sky.ContentSkyRenderers;
import mctmods.resourcedatapackloader.client.renderer.sky.ContentWindowLook;
import mctmods.resourcedatapackloader.client.util.ContentDimensionRulesClient;
import mctmods.resourcedatapackloader.common.environment.ContentTimeWindows;
import mctmods.resourcedatapackloader.common.environment.ContentWeatherCycle;
import mctmods.resourcedatapackloader.common.pack.definitions.sky.SkyLookDef;
import mctmods.resourcedatapackloader.common.pack.definitions.sky.SnowRuleDef;
import mctmods.resourcedatapackloader.common.pack.definitions.sky.TimeWindowDef;
import mctmods.resourcedatapackloader.common.pack.definitions.worlds.DimensionDef;
import mctmods.resourcedatapackloader.common.pack.definitions.worlds.DimensionTraitsDef;
import mctmods.resourcedatapackloader.common.rules.ContentRuleSwitch;
import mctmods.resourcedatapackloader.common.worldgen.biome.ContentListBiomes;
import mctmods.resourcedatapackloader.common.worldgen.surface.ContentCaveWorld;
import mctmods.resourcedatapackloader.core.util.math.Colors;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.init.Biomes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraft.world.biome.BiomeProviderSingle;
import net.minecraft.world.gen.ChunkGeneratorEnd;
import net.minecraft.world.gen.ChunkGeneratorFlat;
import net.minecraft.world.gen.ChunkGeneratorHell;
import net.minecraft.world.gen.ChunkGeneratorOverworld;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public final class ContentDimensionRules {
    private static final float STILL_SUN = 0.25F;
    private static final String FLAT_DEFAULT = "3;minecraft:bedrock,59*minecraft:stone,3*minecraft:dirt,minecraft:grass;1";
    private static final Map<World, ContentWeatherCycle> CYCLES = new WeakHashMap<>();

    private ContentDimensionRules() {}

    public static IChunkGenerator generator(World world, DimensionDef def) {
        switch (def.terrain) {
            case DimensionDef.FLAT: return new ChunkGeneratorFlat(world, world.getSeed(), def.structures, def.generatorOptions.isEmpty() ? FLAT_DEFAULT : def.generatorOptions);
            case DimensionDef.VOID: return new ChunkGeneratorFlat(world, world.getSeed(), false, "3;minecraft:air;1");
            case DimensionDef.NETHER: return new ChunkGeneratorHell(world, def.structures, world.getSeed());
            case DimensionDef.END: return new ChunkGeneratorEnd(world, def.structures, world.getSeed(), new BlockPos(100, 50, 0));
            case DimensionDef.CAVE: return new ContentCaveWorld(world, def);
            default: return new ChunkGeneratorOverworld(world, world.getSeed(), def.structures, def.generatorOptions);
        }
    }

    public static BiomeProvider biomes(World world, DimensionDef def) {
        if (DimensionDef.INHERIT.equals(def.biomeSource)) { return new BiomeProvider(world.getWorldInfo()); }
        if (DimensionDef.LIST.equals(def.biomeSource)) { return ContentListBiomes.provider(world.getWorldInfo(), def.biomeList); }
        Biome single = ForgeRegistries.BIOMES.getValue(new ResourceLocation(def.biome));
        return new BiomeProviderSingle(single == null ? Biomes.PLAINS : single);
    }

    public static float[] lightBrightness(@Nullable DimensionDef def, float[] own) {
        if (def == null || def.ambientLight <= 0.0F) { return own; }
        float[] table = new float[16];
        lightTable(def.ambientLight, table);
        return table;
    }

    public static void lightTable(float ambient, float[] table) {
        for (int level = 0; level <= 15; level++) {
            float fade = 1.0F - level / 15.0F;
            table[level] = (1.0F - fade) / (fade * 3.0F + 1.0F) * (1.0F - ambient) + ambient;
        }
    }

    public static void renderers(World world, WorldProvider provider, DimensionDef def) { if (world.isRemote) { ContentSkyRenderers.apply(provider, def); } }

    static float celestialAngle(long dayLength, long worldTime, float partialTicks) {
        float angle = ((float) (worldTime % dayLength) + partialTicks) / dayLength - 0.25F;
        if (angle < 0.0F) { angle++; }
        if (angle > 1.0F) { angle--; }
        float eased = 1.0F - (float) ((Math.cos(angle * Math.PI) + 1.0D) / 2.0D);
        return angle + (eased - angle) / 3.0F;
    }

    static float angle(World world, @Nullable DimensionDef def, long worldTime, float partialTicks, float own) {
        if (def == null) { return own; }
        long length = ContentRuleSwitch.dayLength(world, def);
        if (length == DimensionTraitsDef.VANILLA_DAY) { return own; }
        if (length == DimensionTraitsDef.NO_DAY) { return STILL_SUN; }
        return celestialAngle(length, worldTime, partialTicks);
    }

    static boolean daytime(World world, @Nullable DimensionDef def, boolean own) {
        if (def == null) { return own; }
        if (ContentRuleSwitch.dayLength(world, def) == DimensionTraitsDef.NO_DAY) { return true; }
        return def.fixedTime >= 0 ? daytime(def.fixedTime) : own;
    }

    public static boolean daytime(int fixedTime) {
        long time = fixedTime % DimensionTraitsDef.VANILLA_DAY;
        return time < 12300L || time > 23850L;
    }

    static long worldTime(@Nullable DimensionDef def, long own) { return def != null && def.fixedTime >= 0 ? def.fixedTime : own; }

    static IChunkGenerator generator(World world, @Nullable DimensionDef def, boolean owned, Supplier<IChunkGenerator> own) { return def != null && (owned || def.gives("terrain.type")) ? generator(world, def) : own.get(); }

    static boolean bedsExplode(@Nullable DimensionDef def) { return def != null && !def.beds; }

    static boolean lightning(@Nullable DimensionDef def) { return def == null || def.traits.precipitation && def.traits.lightning; }

    static boolean precipitation(@Nullable DimensionDef def) { return def == null || def.traits.precipitation; }

    static int respawnDimension(@Nullable DimensionDef def, int own) { return def != null && def.hasRespawnDimension() ? def.respawnDimension : own; }

    private static <T> T given(@Nullable DimensionDef def, boolean owned, String key, Function<DimensionDef, T> value, T own) { return def != null && (owned || def.gives(key)) ? value.apply(def) : own; }

    private static <T> T given(@Nullable DimensionDef def, Predicate<DimensionDef> set, Function<DimensionDef, T> value, T own) { return def != null && set.test(def) ? value.apply(def) : own; }

    static double movementFactor(@Nullable DimensionDef def, boolean owned, double own) { return given(def, owned, "sky.movementFactor", held -> held.movementFactor, own); }

    static int groundLevel(@Nullable DimensionDef def, boolean owned, int own) { return given(def, owned, "sky.groundLevel", held -> held.groundLevel, own); }

    static boolean respawns(@Nullable DimensionDef def, boolean owned, boolean own) { return given(def, owned, "sky.respawn", held -> held.respawn, own); }

    static boolean surface(@Nullable DimensionDef def, boolean owned, boolean own) { return given(def, owned, "sky.surfaceWorld", held -> held.surfaceWorld, own); }

    @SideOnly(Side.CLIENT) static boolean showsFog(@Nullable DimensionDef def, boolean owned, boolean own) { return given(def, owned, "sky.showFog", held -> held.showFog, own); }

    @SideOnly(Side.CLIENT) static float starBrightness(@Nullable DimensionDef def, float own) { return given(def, held -> held.starBrightness >= 0.0F, held -> held.starBrightness, own); }

    @SideOnly(Side.CLIENT) @Nullable static float[] sunrise(@Nullable DimensionDef def, @Nullable float[] own) { return def != null && !def.sunriseColors ? null : own; }

    @SideOnly(Side.CLIENT) static Vec3d skyColor(World world, @Nullable DimensionDef def, float partialTicks, Vec3d own) { return given(def, held -> held.skyColor >= 0, held -> skyColor(world, held.skyColor, partialTicks), own); }

    @SideOnly(Side.CLIENT) static Vec3d cloudColor(@Nullable DimensionDef def, Vec3d own) {
        SkyLookDef.CloudLayer layer = ContentCloudRenderer.drawing();
        if (layer != null && layer.color >= 0) { return Colors.vec(layer.color); }
        return given(def, held -> held.cloudColor >= 0, held -> Colors.vec(held.cloudColor), own);
    }

    @SideOnly(Side.CLIENT) static float cloudHeight(@Nullable DimensionDef def, boolean owned, float own) {
        SkyLookDef.CloudLayer layer = ContentCloudRenderer.drawing();
        if (layer != null) { return layer.height; }
        return ContentDimensionRules.<Float>given(def, owned, "sky.cloudHeight", held -> (float) held.cloudHeight, own);
    }

    @SideOnly(Side.CLIENT) static Vec3d fogColor(World world, @Nullable DimensionDef def, float celestialAngle, Vec3d own) {
        if (def != null && def.look != null && def.look.sampleFog) { return ContentWindowLook.fogColor(world, ContentFogSampler.color(own), celestialAngle); }
        return ContentWindowLook.fogColor(world, def != null && def.fogColor >= 0 ? fogColor(def.fogColor, celestialAngle) : own, celestialAngle);
    }

    @SideOnly(Side.CLIENT) static void lightmap(@Nullable DimensionDef def, float partialTicks, float sunBrightness, float skyLight, float blockLight, float[] colors) {
        if (def != null && def.look != null && def.look.tintsLight()) { ContentDimensionRulesClient.lightmap(def.look, def.darkness != null, partialTicks, sunBrightness, skyLight, blockLight, colors); }
    }

    static void weather(World world, DimensionDef def, ContentWeatherCycle cycle, Runnable own) {
        if (ContentRuleSwitch.forces(world)) {
            ContentWeatherCycle.force(world, ContentEnvironments.wide(world.provider.getDimension()).weather);
            return;
        }
        DimensionTraitsDef traits = def.traits;
        TimeWindowDef window = ContentTimeWindows.active(world, def);
        int steps = ContentRuleSwitch.speed(world);
        if (!traits.precipitation) { ContentWeatherCycle.clear(world); }
        else if (window != null && window.holdsDry()) { ContentWeatherCycle.dry(world); }
        else if (window != null && window.gives(TimeWindowDef.ALWAYS_RAIN) ? window.alwaysRain : traits.alwaysRain) {
            ContentWeatherCycle.hurry(world, steps);
            own.run();
            ContentWeatherCycle.pour(world);
        }
        else if (traits.cycle != null) { cycle.tick(world, traits.cycle, steps); }
        else {
            ContentWeatherCycle.hurry(world, steps);
            own.run();
        }
    }

    @SideOnly(Side.CLIENT) static Vec3d skyColor(World world, int rgb, float partialTicks) {
        Vec3d sky = Colors.vec(rgb).scale(daylight(world.getCelestialAngle(partialTicks)));
        sky = overcast(sky, world.getRainStrength(partialTicks), 0.6D);
        sky = overcast(sky, world.getThunderStrength(partialTicks), 0.2D);
        if (world.getLastLightningBolt() <= 0) { return sky; }
        double flash = Math.min(world.getLastLightningBolt() - partialTicks, 1.0F) * 0.45D;
        return new Vec3d(sky.x * (1.0D - flash) + 0.8D * flash, sky.y * (1.0D - flash) + 0.8D * flash, sky.z * (1.0D - flash) + flash);
    }

    private static Vec3d overcast(Vec3d sky, float strength, double dim) {
        if (strength <= 0.0F) { return sky; }
        double gray = (sky.x * 0.3D + sky.y * 0.59D + sky.z * 0.11D) * dim;
        double keep = 1.0D - strength * 0.75D;
        return new Vec3d(sky.x * keep + gray * (1.0D - keep), sky.y * keep + gray * (1.0D - keep), sky.z * keep + gray * (1.0D - keep));
    }

    @SideOnly(Side.CLIENT) public static Vec3d fogColor(int rgb, float celestialAngle) {
        float light = daylight(celestialAngle);
        Vec3d fog = Colors.vec(rgb);
        return new Vec3d(fog.x * (light * 0.94F + 0.06F), fog.y * (light * 0.94F + 0.06F), fog.z * (light * 0.91F + 0.09F));
    }

    private static float daylight(float celestialAngle) { return MathHelper.clamp(MathHelper.cos(celestialAngle * (float) Math.PI * 2.0F) * 2.0F + 0.5F, 0.0F, 1.0F); }

    public static IChunkGenerator existingGenerator(World world, Supplier<IChunkGenerator> own) { return generator(world, ContentDimensions.overlay(world), false, own); }

    public static long existingWorldTime(World world, long own) { return worldTime(ContentDimensions.overlay(world), own); }

    public static boolean existingDaytime(World world, boolean own) {
        DimensionDef def = ContentDimensions.overlay(world);
        return def != null && def.fixedTime >= 0 ? daytime(def.fixedTime) : own;
    }

    public static float existingCelestialAngle(World world, long worldTime, float partialTicks, float own) {
        DimensionDef def = ContentDimensions.overlay(world);
        return def != null && (ContentEnvironments.wide(world.provider.getDimension()).dayLength > 0L || def.gives("time.dayLength")) ? celestialAngle(ContentRuleSwitch.dayLength(world, def), worldTime, partialTicks) : own;
    }

    public static void existingWeather(World world, Runnable own) {
        DimensionDef def = ContentDimensions.overlay(world);
        if (def == null) { own.run(); }
        else { weather(world, def, CYCLES.computeIfAbsent(world, held -> new ContentWeatherCycle()), own); }
    }

    public static boolean existingFreezes(World world, BlockPos pos, boolean own) {
        DimensionDef def = ContentDimensions.of(world);
        return snowRuled(ContentEnvironments.rules(world, pos).snow.freeze, own, def == null || def.traits.freeze);
    }

    public static boolean existingSnows(World world, BlockPos pos, boolean own) {
        DimensionDef def = ContentDimensions.of(world);
        return snowRuled(ContentEnvironments.rules(world, pos).snow.place, own, def == null || def.traits.snow);
    }

    private static boolean snowRuled(int mode, boolean own, boolean trait) {
        if (mode == SnowRuleDef.NEVER) { return false; }
        if (mode == SnowRuleDef.BIOME || mode == SnowRuleDef.ANY) { return own; }
        return own && trait;
    }

    public static boolean snowsAnywhere(World world, BlockPos pos) { return ContentEnvironments.rules(world, pos).snow.place == SnowRuleDef.ANY; }

    public static boolean freezesAnywhere(World world, BlockPos pos) { return ContentEnvironments.rules(world, pos).snow.freeze == SnowRuleDef.ANY; }

    public static boolean existingLightning(World world, boolean own) { return own && lightning(ContentDimensions.overlay(world)); }

    public static boolean existingPrecipitation(World world, boolean own) { return own && precipitation(ContentDimensions.overlay(world)); }

    public static boolean existingRespawn(WorldProvider provider, boolean own) { return respawns(ContentDimensions.overlay(provider.getDimension()), false, own); }

    public static int existingRespawnDimension(WorldProvider provider, int own) { return respawnDimension(ContentDimensions.overlay(provider.getDimension()), own); }

    public static double existingMovementFactor(WorldProvider provider, double own) { return movementFactor(ContentDimensions.overlay(provider.getDimension()), false, own); }

    public static int existingGroundLevel(World world, int own) { return groundLevel(ContentDimensions.overlay(world), false, own); }

    public static boolean existingSurface(WorldProvider provider, boolean own) { return surface(ContentDimensions.overlay(provider.getDimension()), false, own); }

    public static boolean existingBedsExplode(WorldProvider provider) { return bedsExplode(ContentDimensions.overlay(provider.getDimension())); }

    @SideOnly(Side.CLIENT) public static Vec3d existingSkyColor(World world, float partialTicks, Vec3d own) { return skyColor(world, ContentDimensions.overlay(world), partialTicks, own); }

    @SideOnly(Side.CLIENT) public static Vec3d existingCloudColor(World world, Vec3d own) { return cloudColor(ContentDimensions.overlay(world), own); }

    @SideOnly(Side.CLIENT) public static Vec3d existingFogColor(World world, float celestialAngle, Vec3d own) {
        DimensionDef def = ContentDimensions.overlay(world);
        return def == null ? own : fogColor(world, def, celestialAngle, own);
    }

    @SideOnly(Side.CLIENT) public static float existingStarBrightness(World world, float own) { return starBrightness(ContentDimensions.overlay(world), own); }

    @SideOnly(Side.CLIENT) public static float existingFogLight(World world, float own) {
        DimensionDef def = ContentDimensions.overlay(world);
        if (def == null) { return own; }
        return def.fogColor >= 0 ? 1.0F : own + (1.0F - own) * ContentWindowLook.colorFog(world);
    }

    @SideOnly(Side.CLIENT) @Nullable public static float[] existingSunrise(World world, @Nullable float[] own) { return sunrise(ContentDimensions.overlay(world), own); }

    @SideOnly(Side.CLIENT) public static boolean existingShowsFog(World world, boolean own) { return showsFog(ContentDimensions.overlay(world), false, own); }

    @SideOnly(Side.CLIENT) public static float existingCloudHeight(World world, float own) {
        SkyLookDef.CloudLayer layer = ContentCloudRenderer.drawing();
        DimensionDef def = ContentDimensions.overlay(world);
        if (def == null) { return own; }
        if (layer != null) { return layer.height; }
        return def.gives("sky.cloudHeight") ? def.cloudHeight : own;
    }

    @SideOnly(Side.CLIENT) public static void existingLightmap(World world, float partialTicks, float sunBrightness, float skyLight, float blockLight, float[] colors) { lightmap(ContentDimensions.overlay(world), partialTicks, sunBrightness, skyLight, blockLight, colors); }

    @SideOnly(Side.CLIENT) public static ResourceLocation skyTexture(World world, ResourceLocation asked) {
        DimensionDef def = ContentDimensions.of(world);
        if (def == null) { return asked; }
        if (DimensionTraitsDef.VANILLA_SUN_TEXTURE.equals(asked) && def.traits.sky != null) { return def.traits.sky.sunTexture; }
        if (DimensionTraitsDef.VANILLA_MOON_TEXTURE.equals(asked) && def.traits.moon != null) { return def.traits.moon.texture; }
        return asked;
    }
}
