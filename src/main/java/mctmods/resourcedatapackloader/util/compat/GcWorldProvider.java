package mctmods.resourcedatapackloader.util.compat;

import mctmods.resourcedatapackloader.content.def.DimensionTraitsDef;
import mctmods.resourcedatapackloader.content.def.GalacticraftDef;
import mctmods.resourcedatapackloader.content.def.GalaxySpaceDef;
import mctmods.resourcedatapackloader.content.worldgen.ContentPhysics;
import mctmods.resourcedatapackloader.content.worldgen.ContentWorldProvider;

import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody;
import micdoodle8.mods.galacticraft.api.world.EnumAtmosphericGas;
import micdoodle8.mods.galacticraft.api.world.IGalacticraftWorldProvider;
import micdoodle8.mods.galacticraft.api.world.ISolarLevel;
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore;
import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.DimensionType;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.Loader;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class GcWorldProvider extends ContentWorldProvider implements IGalacticraftWorldProvider, ISolarLevel {
    private static final double VANILLA_GRAVITY = 0.08D;
    private static final double VANILLA_ARROW = 0.05D;

    private static final boolean EXTRA_PLANETS = Loader.isModLoaded("extraplanets");

    @Nullable private GalacticraftDef gc() { return def == null ? null : def.galacticraft; }

    @Nullable public GalaxySpaceDef galaxySpace() {
        GalacticraftDef gc = gc();
        return gc == null ? null : gc.body.galaxySpace;
    }

    public int extraPlanetsPressure() {
        GalacticraftDef gc = gc();
        return gc == null || gc.extraPlanets == null ? -1 : gc.extraPlanets.pressure;
    }

    static float gravity(int dimension) { return (float) (VANILLA_GRAVITY * (1.0D - ContentPhysics.gravityFactor(dimension))); }

    @Override @Nonnull public DimensionType getDimensionType() {
        if (!DimensionManager.isDimensionRegistered(getDimension())) { return super.getDimensionType(); }
        return DimensionManager.getProviderType(getDimension());
    }

    @Override public boolean galacticraftPhysics() { return true; }

    @Nullable public ResourceLocation rocketGui() {
        GalacticraftDef gc = gc();
        return gc == null ? null : gc.rocketGui;
    }

    @Override public float getGravity() { return gravity(getDimension()); }

    @Override public float getArrowGravity() { return (float) (VANILLA_ARROW * ContentPhysics.gravityFactor(getDimension())); }

    @Override public float getFallDamageModifier() { return (float) ContentPhysics.fallDamageFactor(getDimension()); }

    @Override public double getMeteorFrequency() {
        GalacticraftDef gc = gc();
        if (gc == null) { return 0.0D; }
        double frequency = gc.meteorFrequency >= 0.0D ? gc.meteorFrequency : gc.density <= 0.0F ? 5.0D : gc.density * 100.0D;
        return frequency * 750.0D / ConfigManagerCore.meteorSpawnMod < 1.0D ? 0.0D : frequency;
    }

    @Override public double getFuelUsageMultiplier() {
        GalacticraftDef gc = gc();
        return gc == null ? 1.0D : gc.fuelMultiplier;
    }

    @Override public boolean canSpaceshipTierPass(int tier) {
        GalacticraftDef gc = gc();
        return gc != null && tier >= gc.minTier;
    }

    @Override public boolean hasNoAtmosphere() {
        GalacticraftDef gc = gc();
        return gc == null || gc.gases.isEmpty();
    }

    @Override public float getSoundVolReductionAmount() {
        GalacticraftDef gc = gc();
        if (gc == null) { return 1.0F; }
        if (gc.soundReduction >= 0.0F) { return gc.soundReduction; }
        if (gc.density <= 0.0F) { return 20.0F; }
        return gc.density > 5.0F ? 0.2F : 1.0F / gc.density;
    }

    @Override public boolean hasBreathableAtmosphere() {
        CelestialBody body = getCelestialBody();
        return body != null && body.atmosphere.isBreathable();
    }

    @Override public boolean netherPortalsOperational() {
        GalacticraftDef gc = gc();
        return gc != null && gc.netherPortals;
    }

    @Override public boolean isGasPresent(EnumAtmosphericGas gas) {
        GalacticraftDef gc = gc();
        return gc != null && gc.gases.contains(gas.name());
    }

    @Override public float getThermalLevelModifier() {
        GalacticraftDef gc = gc();
        if (gc == null) { return 0.0F; }
        if (EXTRA_PLANETS && gc.extraPlanets != null && !Float.isNaN(gc.extraPlanets.temperatureDay)) { return EpPlanets.temperature(gc.extraPlanets, isDaytime(), gc.temperature); }
        return gc.temperature;
    }

    @Override public float getWindLevel() {
        GalacticraftDef gc = gc();
        return gc == null ? 0.0F : gc.wind;
    }

    @Override public float getSolarSize() {
        if (def != null && def.traits.sky != null) { return def.traits.sky.sunSize / DimensionTraitsDef.VANILLA_SUN; }
        GalacticraftDef gc = gc();
        return gc == null || gc.body.distance <= 0.0F ? 1.0F : 1.0F / gc.body.distance;
    }

    @Override @Nullable public CelestialBody getCelestialBody() { return GcCelestial.body(getDimension()); }

    @Override public boolean shouldDisablePrecipitation() { return def != null && !def.traits.precipitation; }

    @Override public boolean shouldCorrodeArmor() {
        GalacticraftDef gc = gc();
        return gc != null && gc.corrosive;
    }

    @Override public int getDungeonSpacing() {
        GalacticraftDef gc = gc();
        return gc == null ? 0 : gc.dungeonSpacing;
    }

    @Override @Nullable public ResourceLocation getDungeonChestType() {
        GalacticraftDef gc = gc();
        return gc == null ? null : gc.dungeonChest;
    }

    @Override public List<Block> getSurfaceBlocks() { return Collections.emptyList(); }

    @Override public double getSolarEnergyMultiplier() {
        GalacticraftDef gc = gc();
        return gc == null ? 1.0D : gc.solarEnergy;
    }
}
