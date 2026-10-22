package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.def.AmbienceDef;
import mctmods.resourcedatapackloader.content.def.DimensionDef;
import mctmods.resourcedatapackloader.content.def.DimensionTraitsDef;
import mctmods.resourcedatapackloader.content.def.RainDef;
import mctmods.resourcedatapackloader.content.def.SkyLookDef;

import net.minecraft.client.audio.MusicTicker;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldProviderSurface;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ContentWorldProvider extends WorldProviderSurface {
    private static final float STILL_SUN = 0.25F;
    private final ContentWeatherCycle weatherCycle = new ContentWeatherCycle();
    @Nullable protected DimensionDef def;

    @Override protected void init() {
        this.def = ContentDimensions.byId(getDimension());
        this.hasSkyLight = def == null || def.hasSkyLight;
        this.biomeProvider = provider();
        if (def != null && FMLCommonHandler.instance().getSide().isClient()) { ContentSkyRenderers.apply(this, def); }
    }

    void refresh() {
        this.def = ContentDimensions.byId(getDimension());
        if (def != null && world.isRemote) { ContentSkyRenderers.apply(this, def); }
    }

    private BiomeProvider provider() { return def == null ? new BiomeProvider(world.getWorldInfo()) : ContentDimensionRules.biomes(world, def); }

    @Override @Nonnull public IChunkGenerator createChunkGenerator() { return def == null ? super.createChunkGenerator() : ContentDimensionRules.generator(world, def); }

    @Override @Nonnull public DimensionType getDimensionType() { return ContentDimensions.typeFor(getDimension()); }

    public boolean galacticraftPhysics() { return false; }

    @Override public boolean isSurfaceWorld() { return def == null || def.surfaceWorld; }

    @Override public boolean isNether() { return def != null && def.nether; }

    @Override public boolean doesWaterVaporize() { return def == null ? super.doesWaterVaporize() : def.waterVaporizes; }

    @Override @Nonnull public WorldProvider.WorldSleepResult canSleepAt(@Nonnull EntityPlayer player, @Nonnull BlockPos pos) {
        if (def != null && !def.beds) { return WorldProvider.WorldSleepResult.BED_EXPLODES; }
        return super.canSleepAt(player, pos);
    }

    @Override public int getRespawnDimension(@Nonnull EntityPlayerMP player) {
        if (def == null || def.respawnDimension == Integer.MIN_VALUE) { return super.getRespawnDimension(player); }
        return def.respawnDimension;
    }

    @Override @SideOnly(Side.CLIENT) public boolean doesXZShowFog(int x, int z) { return def != null && def.showFog; }

    @Override @SideOnly(Side.CLIENT) public float getStarBrightness(float partialTicks) {
        if (def == null || def.starBrightness < 0.0F) { return super.getStarBrightness(partialTicks); }
        return def.starBrightness;
    }

    @Override @SideOnly(Side.CLIENT) @Nonnull public Vec3d getCloudColor(float partialTicks) {
        SkyLookDef.CloudLayer layer = ContentCloudRenderer.drawing();
        if (layer != null && layer.color >= 0) { return ContentDimensionRules.color(layer.color); }
        if (def == null || def.cloudColor < 0) { return super.getCloudColor(partialTicks); }
        return ContentDimensionRules.color(def.cloudColor);
    }

    @Override @SideOnly(Side.CLIENT) public void getLightmapColors(float partialTicks, float sunBrightness, float skyLight, float blockLight, @Nonnull float[] colors) { if (def != null && def.look != null && def.look.tintsLight()) { ContentDimensionRules.lightmap(def.look, partialTicks, sunBrightness, skyLight, blockLight, colors); } }

    @Override @Nonnull public float[] getLightBrightnessTable() { return ContentDimensionRules.lightBrightness(def, super.getLightBrightnessTable()); }

    @Override public boolean canRespawnHere() { return def != null && def.respawn; }

    @Override public int getAverageGroundLevel() { return def == null ? super.getAverageGroundLevel() : def.groundLevel; }

    @Override public double getMovementFactor() { return def == null ? super.getMovementFactor() : def.movementFactor; }

    @Override @SideOnly(Side.CLIENT) public float getCloudHeight() {
        SkyLookDef.CloudLayer layer = ContentCloudRenderer.drawing();
        if (layer != null) { return layer.height; }
        return def == null ? super.getCloudHeight() : def.cloudHeight;
    }

    @Override public long getWorldTime() {
        if (def == null || def.fixedTime < 0) { return super.getWorldTime(); }
        return def.fixedTime;
    }

    @Override public float calculateCelestialAngle(long worldTime, float partialTicks) {
        long length = ContentRuleSwitch.dayLength(world, def);
        if (def == null || length == DimensionTraitsDef.VANILLA_DAY) { return super.calculateCelestialAngle(worldTime, partialTicks); }
        if (length == DimensionTraitsDef.NO_DAY) { return STILL_SUN; }
        return ContentDimensionRules.celestialAngle(length, worldTime, partialTicks);
    }

    @Override public void updateWeather() {
        if (def == null) { super.updateWeather(); }
        else { ContentDimensionRules.weather(world, def, weatherCycle, super::updateWeather); }
    }

    @Nullable public RainDef rain() { return def == null ? null : def.traits.rain; }

    @Nullable public RainDef rainSplash() { return rain(); }

    @Override @Nullable @SideOnly(Side.CLIENT) public MusicTicker.MusicType getMusicType() {
        AmbienceDef ambience = def == null ? null : def.traits.ambience;
        return ambience == null || ambience.music == null ? super.getMusicType() : ContentDimensionAmbience.music(ambience);
    }

    @Override public boolean canDoLightning(@Nonnull Chunk chunk) { return def == null || def.traits.precipitation && def.traits.lightning; }

    @Override public boolean canDoRainSnowIce(@Nonnull Chunk chunk) { return def == null || def.traits.precipitation; }

    @Override public boolean canSnowAt(@Nonnull BlockPos pos, boolean checkLight) { return (def == null || def.traits.snow) && super.canSnowAt(pos, checkLight); }

    @Override public boolean canBlockFreeze(@Nonnull BlockPos pos, boolean byWater) { return (def == null || def.traits.freeze) && super.canBlockFreeze(pos, byWater); }

    @Override public boolean isDaytime() {
        if (def != null && ContentRuleSwitch.dayLength(world, def) == DimensionTraitsDef.NO_DAY) { return true; }
        if (def == null || def.fixedTime < 0) { return super.isDaytime(); }
        return ContentDimensionRules.daytime(def.fixedTime);
    }

    @Override @SideOnly(Side.CLIENT) @Nullable public float[] calcSunriseSunsetColors(float celestialAngle, float partialTicks) {
        if (def != null && !def.sunriseColors) { return null; }
        return super.calcSunriseSunsetColors(celestialAngle, partialTicks);
    }

    @Override @SideOnly(Side.CLIENT) @Nonnull public Vec3d getSkyColor(@Nonnull Entity camera, float partialTicks) {
        if (def == null || def.skyColor < 0) { return super.getSkyColor(camera, partialTicks); }
        return ContentDimensionRules.skyColor(world, def.skyColor, partialTicks);
    }

    @Override @SideOnly(Side.CLIENT) @Nonnull public Vec3d getFogColor(float celestialAngle, float partialTicks) {
        if (def != null && def.look != null && def.look.sampleFog) { return ContentWindowLook.fogColor(world, ContentFogSampler.color(super.getFogColor(celestialAngle, partialTicks)), celestialAngle); }
        if (def == null || def.fogColor < 0) { return ContentWindowLook.fogColor(world, super.getFogColor(celestialAngle, partialTicks), celestialAngle); }
        return ContentWindowLook.fogColor(world, ContentDimensionRules.fogColor(def.fogColor, celestialAngle), celestialAngle);
    }

    @Override @Nonnull public String getSaveFolder() { return def == null ? "DIM" + getDimension() : def.suffix; }
}
