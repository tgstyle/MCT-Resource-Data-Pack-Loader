package mctmods.resourcedatapackloader.util.compat;

import mctmods.resourcedatapackloader.content.worldgen.ContentSkyRenderers;

import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody;
import micdoodle8.mods.galacticraft.api.vector.Vector3;
import micdoodle8.mods.galacticraft.core.client.CloudRenderer;
import micdoodle8.mods.galacticraft.core.client.SkyProviderOrbit;
import micdoodle8.mods.galacticraft.core.dimension.WorldProviderOverworldOrbit;
import net.minecraft.world.DimensionType;
import net.minecraftforge.client.IRenderHandler;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class GcStationWorldProvider extends WorldProviderOverworldOrbit {
    @Nullable private GcStations.Station station;

    private static Vector3 color(int rgb) { return new Vector3(((rgb >> 16) & 255) / 255.0D, ((rgb >> 8) & 255) / 255.0D, (rgb & 255) / 255.0D); }

    @Override protected void init() {
        station = DimensionManager.isDimensionRegistered(getDimension()) ? GcStations.byType(DimensionManager.getProviderType(getDimension()).getId()) : null;
        super.init();
    }

    @Override @Nonnull public DimensionType getDimensionType() {
        if (!DimensionManager.isDimensionRegistered(getDimension())) { return super.getDimensionType(); }
        return DimensionManager.getProviderType(getDimension());
    }

    @Override public CelestialBody getCelestialBody() { return station == null ? super.getCelestialBody() : station.body; }

    @SuppressWarnings("deprecation") @Override public String getPlanetToOrbit() { return station == null ? super.getPlanetToOrbit() : station.home.getName(); }

    @Override public int getPlanetIdToOrbit() { return station == null ? super.getPlanetIdToOrbit() : station.home.getDimensionID(); }

    @Override public boolean canSpaceshipTierPass(int tier) { return station == null ? super.canSpaceshipTierPass(tier) : tier >= station.body.getTierRequirement(); }

    @Override public Vector3 getFogColor() { return station == null || station.def.fogColor < 0 ? super.getFogColor() : color(station.def.fogColor); }

    @Override public Vector3 getSkyColor() { return station == null || station.def.skyColor < 0 ? super.getSkyColor() : color(station.def.skyColor); }

    @Override public long getDayLength() { return station == null ? super.getDayLength() : station.def.traits.dayLength; }

    @Override @SideOnly(Side.CLIENT) public float getStarBrightness(float partialTicks) {
        if (station == null || station.def.starBrightness < 0.0F) { return super.getStarBrightness(partialTicks); }
        return station.def.starBrightness;
    }

    @Override @SideOnly(Side.CLIENT) public void setSpinDeltaPerTick(float angle) {
        IRenderHandler sky = getSkyRenderer();
        if (sky instanceof SkyProviderOrbit) { ((SkyProviderOrbit) sky).spinDeltaPerTick = angle; }
    }

    @Override @SideOnly(Side.CLIENT) public float getSkyRotation() {
        IRenderHandler sky = getSkyRenderer();
        return sky instanceof SkyProviderOrbit ? ((SkyProviderOrbit) sky).spinAngle : 0.0F;
    }

    @Override @SideOnly(Side.CLIENT) public void createSkyProvider() {
        if (station == null) {
            super.createSkyProvider();
            return;
        }
        setSkyRenderer(new SkyProviderOrbit(station.home.getBodyIcon(), true, true));
        setSpinDeltaPerTick(getSpinManager().getSpinRate());
        ContentSkyRenderers.apply(this, station.def);
        if (getCloudRenderer() == null) { setCloudRenderer(new CloudRenderer()); }
    }
}
