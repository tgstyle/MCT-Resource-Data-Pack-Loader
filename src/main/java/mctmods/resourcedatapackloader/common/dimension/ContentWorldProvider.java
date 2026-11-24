package mctmods.resourcedatapackloader.common.dimension;

import mctmods.resourcedatapackloader.client.renderer.sky.ContentDimensionAmbience;
import mctmods.resourcedatapackloader.client.renderer.sky.ContentSkyRenderers;
import mctmods.resourcedatapackloader.common.environment.ContentWeatherCycle;
import mctmods.resourcedatapackloader.common.pack.definitions.sky.AmbienceDef;
import mctmods.resourcedatapackloader.common.pack.definitions.sky.RainDef;
import mctmods.resourcedatapackloader.common.pack.definitions.worlds.DimensionDef;
import mctmods.resourcedatapackloader.core.util.SideUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.client.audio.MusicTicker;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldProviderSurface;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ContentWorldProvider extends WorldProviderSurface {
    private final ContentWeatherCycle weatherCycle = new ContentWeatherCycle();
    @Nullable protected DimensionDef def;

    @Override protected void init() {
        this.def = ContentDimensions.byId(getDimension());
        this.hasSkyLight = def == null || def.hasSkyLight;
        this.biomeProvider = provider();
        if (def != null && SideUtils.isClient()) { ContentSkyRenderers.apply(this, def); }
    }

    void refresh() {
        this.def = ContentDimensions.byId(getDimension());
        if (def != null && world.isRemote) { ContentSkyRenderers.apply(this, def); }
    }

    private BiomeProvider provider() { return def == null ? new BiomeProvider(world.getWorldInfo()) : ContentDimensionRules.biomes(world, def); }

    @Override @Nonnull public IChunkGenerator createChunkGenerator() { return ContentDimensionRules.generator(world, def, true, super::createChunkGenerator); }

    @Override @Nonnull public DimensionType getDimensionType() { return ContentDimensions.typeFor(getDimension()); }

    public boolean galacticraftPhysics() { return false; }

    @Override public boolean isSurfaceWorld() { return ContentDimensionRules.surface(def, true, true); }

    @Override public boolean isNether() { return def != null && def.nether; }

    @Override public boolean doesWaterVaporize() { return def == null ? super.doesWaterVaporize() : def.waterVaporizes; }

    @Override @Nonnull public WorldProvider.WorldSleepResult canSleepAt(@Nonnull EntityPlayer player, @Nonnull BlockPos pos) {
        if (ContentDimensionRules.bedsExplode(def)) { return WorldProvider.WorldSleepResult.BED_EXPLODES; }
        return super.canSleepAt(player, pos);
    }

    @Override public int getRespawnDimension(@Nonnull EntityPlayerMP player) { return ContentDimensionRules.respawnDimension(def, super.getRespawnDimension(player)); }

    @Override @SideOnly(Side.CLIENT) public boolean doesXZShowFog(int x, int z) { return ContentDimensionRules.showsFog(def, true, false); }

    @Override @SideOnly(Side.CLIENT) public float getStarBrightness(float partialTicks) { return ContentDimensionRules.starBrightness(def, super.getStarBrightness(partialTicks)); }

    @Override @SideOnly(Side.CLIENT) @Nonnull public Vec3d getCloudColor(float partialTicks) { return ContentDimensionRules.cloudColor(def, super.getCloudColor(partialTicks)); }

    @Override @SideOnly(Side.CLIENT) public void getLightmapColors(float partialTicks, float sunBrightness, float skyLight, float blockLight, @Nonnull float[] colors) { ContentDimensionRules.lightmap(def, partialTicks, sunBrightness, skyLight, blockLight, colors); }

    @Override @Nonnull public float[] getLightBrightnessTable() { return ContentDimensionRules.lightBrightness(def, super.getLightBrightnessTable()); }

    @Override public boolean canRespawnHere() { return ContentDimensionRules.respawns(def, true, false); }

    @Override public int getAverageGroundLevel() { return ContentDimensionRules.groundLevel(def, true, super.getAverageGroundLevel()); }

    @Override public double getMovementFactor() { return ContentDimensionRules.movementFactor(def, true, super.getMovementFactor()); }

    @Override @SideOnly(Side.CLIENT) public float getCloudHeight() { return ContentDimensionRules.cloudHeight(def, true, super.getCloudHeight()); }

    @Override public long getWorldTime() { return ContentDimensionRules.worldTime(def, super.getWorldTime()); }

    @Override public float calculateCelestialAngle(long worldTime, float partialTicks) { return ContentDimensionRules.angle(world, def, worldTime, partialTicks, super.calculateCelestialAngle(worldTime, partialTicks)); }

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

    @Override public boolean canDoLightning(@Nonnull Chunk chunk) { return ContentDimensionRules.lightning(def); }

    @Override public boolean canDoRainSnowIce(@Nonnull Chunk chunk) { return ContentDimensionRules.precipitation(def); }

    @Override public boolean isDaytime() { return ContentDimensionRules.daytime(world, def, super.isDaytime()); }

    @Override @SideOnly(Side.CLIENT) @Nullable public float[] calcSunriseSunsetColors(float celestialAngle, float partialTicks) { return ContentDimensionRules.sunrise(def, super.calcSunriseSunsetColors(celestialAngle, partialTicks)); }

    @Override @SideOnly(Side.CLIENT) @Nonnull public Vec3d getSkyColor(@Nonnull Entity camera, float partialTicks) { return ContentDimensionRules.skyColor(world, def, partialTicks, super.getSkyColor(camera, partialTicks)); }

    @Override @SideOnly(Side.CLIENT) @Nonnull public Vec3d getFogColor(float celestialAngle, float partialTicks) { return ContentDimensionRules.fogColor(world, def, celestialAngle, super.getFogColor(celestialAngle, partialTicks)); }

    @Override @Nonnull public String getSaveFolder() { return def == null ? "DIM" + getDimension() : def.suffix; }
}
