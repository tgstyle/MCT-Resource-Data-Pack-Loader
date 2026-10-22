package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.def.AmbienceDef;
import mctmods.resourcedatapackloader.content.def.DimensionDef;
import mctmods.resourcedatapackloader.content.def.DimensionTraitsDef;
import mctmods.resourcedatapackloader.content.def.SkyLookDef;
import mctmods.resourcedatapackloader.content.def.TimeWindowDef;
import mctmods.resourcedatapackloader.mixin.rdpl.client.IEntityRenderer;
import mctmods.resourcedatapackloader.util.Colors;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.MusicTicker;
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
import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.IntUnaryOperator;
import java.util.function.Supplier;
import javax.annotation.Nullable;

public final class ContentDimensionRules {
    private static final String FLAT_DEFAULT = "3;minecraft:bedrock,59*minecraft:stone,3*minecraft:dirt,minecraft:grass;1";
    private static final float BOSS_RED = 0.3F;
    private static final float BOSS_GREEN_BLUE = 0.4F;
    private static final ResourceLocation SUN = new ResourceLocation("textures/environment/sun.png");
    private static final ResourceLocation MOON = new ResourceLocation("textures/environment/moon_phases.png");
    private static final Map<World, ContentWeatherCycle> CYCLES = new WeakHashMap<>();

    private ContentDimensionRules() {}

    public static IChunkGenerator generator(World world, DimensionDef def) {
        switch (def.terrain) {
            case DimensionDef.FLAT: return new ChunkGeneratorFlat(world, world.getSeed(), def.structures, def.generatorOptions.isEmpty() ? FLAT_DEFAULT : def.generatorOptions);
            case DimensionDef.VOID: return new ChunkGeneratorFlat(world, world.getSeed(), false, "3;minecraft:air;1");
            case DimensionDef.NETHER: return new ChunkGeneratorHell(world, def.structures, world.getSeed());
            case DimensionDef.END: return new ChunkGeneratorEnd(world, def.structures, world.getSeed(), new BlockPos(100, 50, 0));
            default: return new ChunkGeneratorOverworld(world, world.getSeed(), def.structures, def.generatorOptions);
        }
    }

    public static BiomeProvider biomes(World world, DimensionDef def) {
        if (DimensionDef.INHERIT.equals(def.biomeSource)) { return new BiomeProvider(world.getWorldInfo()); }
        if (DimensionDef.LIST.equals(def.biomeSource)) { return ContentListBiomes.provider(world.getWorldInfo(), def.biomeList); }
        Biome single =ForgeRegistries.BIOMES.getValue(new ResourceLocation(def.biome));
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

    static boolean daytime(int fixedTime) {
        long time = fixedTime % DimensionTraitsDef.VANILLA_DAY;
        return time < 12300L || time > 23850L;
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

    static Vec3d color(int rgb) { return new Vec3d(Colors.red(rgb) / 255.0D, Colors.green(rgb) / 255.0D, Colors.blue(rgb) / 255.0D); }

    @SideOnly(Side.CLIENT) static Vec3d skyColor(World world, int rgb, float partialTicks) {
        Vec3d sky = color(rgb).scale(daylight(world.getCelestialAngle(partialTicks)));
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

    @SideOnly(Side.CLIENT) static Vec3d fogColor(int rgb, float celestialAngle) {
        float light = daylight(celestialAngle);
        Vec3d fog = color(rgb);
        return new Vec3d(fog.x * (light * 0.94F + 0.06F), fog.y * (light * 0.94F + 0.06F), fog.z * (light * 0.91F + 0.09F));
    }

    private static float daylight(float celestialAngle) { return MathHelper.clamp(MathHelper.cos(celestialAngle * (float) Math.PI * 2.0F) * 2.0F + 0.5F, 0.0F, 1.0F); }

    @SideOnly(Side.CLIENT) static void lightmap(SkyLookDef look, float partialTicks, float sunBrightness, float skyLight, float blockLight, float[] colors) {
        float sky = skyLight * look.skyFactor;
        float skyRed = sky * (sunBrightness * 0.65F + 0.35F);
        float blockGreen = blockLight * ((blockLight * 0.6F + 0.4F) * 0.6F + 0.4F);
        float blockBlue = blockLight * (blockLight * blockLight * 0.6F + 0.4F);
        colors[0] = (skyRed * channel(look.lightSkyColor, Colors::red) + blockLight * channel(look.lightBlockColor, Colors::red)) * 0.96F + 0.03F;
        colors[1] = (skyRed * channel(look.lightSkyColor, Colors::green) + blockGreen * channel(look.lightBlockColor, Colors::green)) * 0.96F + 0.03F;
        colors[2] = (sky * channel(look.lightSkyColor, Colors::blue) + blockBlue * channel(look.lightBlockColor, Colors::blue)) * 0.96F + 0.03F;
        IEntityRenderer renderer = (IEntityRenderer) Minecraft.getMinecraft().entityRenderer;
        float boss = renderer.getBossColorModifierPrev() + (renderer.getBossColorModifier() - renderer.getBossColorModifierPrev()) * partialTicks;
        if (boss <= 0.0F) { return; }
        colors[0] *= 1.0F - BOSS_RED * boss;
        colors[1] *= 1.0F - BOSS_GREEN_BLUE * boss;
        colors[2] *= 1.0F - BOSS_GREEN_BLUE * boss;
    }

    private static float channel(int rgb, IntUnaryOperator pick) { return rgb == SkyLookDef.UNSET ? 1.0F : pick.applyAsInt(rgb) / 255.0F; }

    public static IChunkGenerator existingGenerator(World world, Supplier<IChunkGenerator> own) {
        DimensionDef def = ContentDimensions.overlay(world);
        return def != null && def.gives("terrain.type") ? generator(world, def) : own.get();
    }

    public static long existingWorldTime(World world, long own) {
        DimensionDef def = ContentDimensions.overlay(world);
        return def != null && def.fixedTime >= 0 ? def.fixedTime : own;
    }

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

    public static boolean existingFreezes(World world, boolean own) {
        DimensionDef def = ContentDimensions.overlay(world);
        return own && (def == null || def.traits.freeze);
    }

    public static boolean existingSnows(World world, boolean own) {
        DimensionDef def = ContentDimensions.overlay(world);
        return own && (def == null || def.traits.snow);
    }

    public static boolean existingLightning(World world, boolean own) {
        DimensionDef def = ContentDimensions.overlay(world);
        return own && (def == null || def.traits.precipitation && def.traits.lightning);
    }

    public static boolean existingPrecipitation(World world, boolean own) {
        DimensionDef def = ContentDimensions.overlay(world);
        return own && (def == null || def.traits.precipitation);
    }

    public static boolean existingRespawn(WorldProvider provider, boolean own) {
        DimensionDef def = ContentDimensions.overlay(provider.getDimension());
        return def != null && def.gives("sky.respawn") ? def.respawn : own;
    }

    public static int existingRespawnDimension(WorldProvider provider, int own) {
        DimensionDef def = ContentDimensions.overlay(provider.getDimension());
        return def != null && def.respawnDimension != Integer.MIN_VALUE ? def.respawnDimension : own;
    }

    public static double existingMovementFactor(WorldProvider provider, double own) {
        DimensionDef def = ContentDimensions.overlay(provider.getDimension());
        return def != null && def.gives("sky.movementFactor") ? def.movementFactor : own;
    }

    public static int existingGroundLevel(World world, int own) {
        DimensionDef def = ContentDimensions.overlay(world);
        return def != null && def.gives("sky.groundLevel") ? def.groundLevel : own;
    }

    public static boolean existingSurface(WorldProvider provider, boolean own) {
        DimensionDef def = ContentDimensions.overlay(provider.getDimension());
        return def != null && def.gives("sky.surfaceWorld") ? def.surfaceWorld : own;
    }

    public static boolean existingBedsExplode(WorldProvider provider) {
        DimensionDef def = ContentDimensions.overlay(provider.getDimension());
        return def != null && !def.beds;
    }

    @SideOnly(Side.CLIENT) public static Vec3d existingSkyColor(World world, float partialTicks, Vec3d own) {
        DimensionDef def = ContentDimensions.overlay(world);
        return def != null && def.skyColor >= 0 ? skyColor(world, def.skyColor, partialTicks) : own;
    }

    @SideOnly(Side.CLIENT) public static Vec3d existingCloudColor(World world, Vec3d own) {
        SkyLookDef.CloudLayer layer = ContentCloudRenderer.drawing();
        if (layer != null && layer.color >= 0) { return color(layer.color); }
        DimensionDef def = ContentDimensions.overlay(world);
        return def != null && def.cloudColor >= 0 ? color(def.cloudColor) : own;
    }

    @SideOnly(Side.CLIENT) public static Vec3d existingFogColor(World world, float celestialAngle, Vec3d own) {
        DimensionDef def = ContentDimensions.overlay(world);
        if (def == null) { return own; }
        if (def.look != null && def.look.sampleFog) { return ContentWindowLook.fogColor(world, ContentFogSampler.color(own), celestialAngle); }
        return ContentWindowLook.fogColor(world, def.fogColor >= 0 ? fogColor(def.fogColor, celestialAngle) : own, celestialAngle);
    }

    @SideOnly(Side.CLIENT) public static float existingStarBrightness(World world, float own) {
        DimensionDef def = ContentDimensions.overlay(world);
        return def != null && def.starBrightness >= 0.0F ? def.starBrightness : own;
    }

    @SideOnly(Side.CLIENT) public static float existingFogLight(World world, float own) {
        DimensionDef def = ContentDimensions.overlay(world);
        if (def == null) { return own; }
        return def.fogColor >= 0 ? 1.0F : own + (1.0F - own) * ContentWindowLook.colorFog(world);
    }

    @SideOnly(Side.CLIENT) @Nullable public static float[] existingSunrise(World world, @Nullable float[] own) {
        DimensionDef def = ContentDimensions.overlay(world);
        return def != null && !def.sunriseColors ? null : own;
    }

    @SideOnly(Side.CLIENT) public static boolean existingShowsFog(World world, boolean own) {
        DimensionDef def = ContentDimensions.overlay(world);
        return def != null && def.gives("sky.showFog") ? def.showFog : own;
    }

    @SideOnly(Side.CLIENT) public static float existingCloudHeight(World world, float own) {
        SkyLookDef.CloudLayer layer = ContentCloudRenderer.drawing();
        DimensionDef def = ContentDimensions.overlay(world);
        if (def == null) { return own; }
        if (layer != null) { return layer.height; }
        return def.gives("sky.cloudHeight") ? def.cloudHeight : own;
    }

    @SideOnly(Side.CLIENT) public static void existingLightmap(World world, float partialTicks, float sunBrightness, float skyLight, float blockLight, float[] colors) {
        DimensionDef def = ContentDimensions.overlay(world);
        if (def != null && def.look != null && def.look.tintsLight()) { lightmap(def.look, partialTicks, sunBrightness, skyLight, blockLight, colors); }
    }

    @SideOnly(Side.CLIENT) @Nullable public static MusicTicker.MusicType existingMusic(World world, @Nullable MusicTicker.MusicType own) {
        DimensionDef def = ContentDimensions.overlay(world);
        AmbienceDef ambience = def == null ? null : def.traits.ambience;
        return ambience == null || ambience.music == null ? own : ContentDimensionAmbience.music(ambience);
    }

    @SideOnly(Side.CLIENT) public static ResourceLocation skyTexture(World world, ResourceLocation asked) {
        DimensionDef def = ContentDimensions.of(world);
        if (def == null) { return asked; }
        if (SUN.equals(asked) && def.traits.sky != null) { return def.traits.sky.sunTexture; }
        if (MOON.equals(asked) && def.traits.moon != null) { return def.traits.moon.texture; }
        return asked;
    }
}
