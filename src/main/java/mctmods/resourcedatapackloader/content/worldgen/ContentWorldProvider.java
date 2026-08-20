package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.def.DimensionDef;

import net.minecraft.init.Biomes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldProviderSurface;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeProviderSingle;
import net.minecraft.world.gen.ChunkGeneratorEnd;
import net.minecraft.world.gen.ChunkGeneratorFlat;
import net.minecraft.world.gen.ChunkGeneratorHell;
import net.minecraft.world.gen.ChunkGeneratorOverworld;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ContentWorldProvider extends WorldProviderSurface {
    private static final String FLAT_DEFAULT = "3;minecraft:bedrock,59*minecraft:stone,3*minecraft:dirt,minecraft:grass;1";
    @Nullable private DimensionDef def;

    @Override protected void init() {
        this.def = ContentDimensions.byId(getDimension());
        this.hasSkyLight = def == null || def.hasSkyLight;
        this.biomeProvider = provider();
        if (def != null && FMLCommonHandler.instance().getSide().isClient()) { ContentSkyRenderers.apply(this, def); }
    }

    private net.minecraft.world.biome.BiomeProvider provider() {
        if (def == null || DimensionDef.INHERIT.equals(def.biomeSource)) { return new net.minecraft.world.biome.BiomeProvider(world.getWorldInfo()); }
        Biome single = ForgeRegistries.BIOMES.getValue(new ResourceLocation(def.biome));
        return new BiomeProviderSingle(single == null ? Biomes.PLAINS : single);
    }

    @Override @Nonnull public IChunkGenerator createChunkGenerator() {
        if (def == null) { return super.createChunkGenerator(); }
        switch (def.terrain) {
            case DimensionDef.FLAT: return new ChunkGeneratorFlat(world, world.getSeed(), def.structures, def.generatorOptions.isEmpty() ? FLAT_DEFAULT : def.generatorOptions);
            case DimensionDef.VOID: return new ChunkGeneratorFlat(world, world.getSeed(), false, "3;minecraft:air;1");
            case DimensionDef.NETHER: return new ChunkGeneratorHell(world, def.structures, world.getSeed());
            case DimensionDef.END: return new ChunkGeneratorEnd(world, def.structures, world.getSeed(), new BlockPos(100, 50, 0));
            default: return new ChunkGeneratorOverworld(world, world.getSeed(), def.structures, def.generatorOptions);
        }
    }

    @Override @Nonnull public DimensionType getDimensionType() { return ContentDimensions.typeFor(getDimension()); }

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
        if (def == null || def.cloudColor < 0) { return super.getCloudColor(partialTicks); }
        return new Vec3d(((def.cloudColor >> 16) & 255) / 255.0D, ((def.cloudColor >> 8) & 255) / 255.0D, (def.cloudColor & 255) / 255.0D);
    }

    @Override @Nonnull public float[] getLightBrightnessTable() {
        if (def == null || def.ambientLight <= 0.0F) { return super.getLightBrightnessTable(); }
        float[] table = new float[16];
        for (int level = 0; level <= 15; level++) {
            float fade = 1.0F - level / 15.0F;
            table[level] = (1.0F - fade) / (fade * 3.0F + 1.0F) * (1.0F - def.ambientLight) + def.ambientLight;
        }
        return table;
    }

    @Override public boolean canRespawnHere() { return def != null && def.respawn; }

    @Override public int getAverageGroundLevel() { return def == null ? super.getAverageGroundLevel() : def.groundLevel; }

    @Override public double getMovementFactor() { return def == null ? super.getMovementFactor() : def.movementFactor; }

    @Override @SideOnly(Side.CLIENT) public float getCloudHeight() { return def == null ? super.getCloudHeight() : def.cloudHeight; }

    @Override public long getWorldTime() {
        if (def == null || def.fixedTime < 0) { return super.getWorldTime(); }
        return def.fixedTime;
    }

    @Override public boolean isDaytime() {
        if (def == null || def.fixedTime < 0) { return super.isDaytime(); }
        long time = def.fixedTime % 24000L;
        return time < 12300L || time > 23850L;
    }

    @Override @SideOnly(Side.CLIENT) @Nullable public float[] calcSunriseSunsetColors(float celestialAngle, float partialTicks) {
        if (def != null && !def.sunriseColors) { return null; }
        return super.calcSunriseSunsetColors(celestialAngle, partialTicks);
    }

    @Override @SideOnly(Side.CLIENT) @Nonnull public Vec3d getSkyColor(@Nonnull Entity camera, float partialTicks) {
        if (def == null || def.skyColor < 0) { return super.getSkyColor(camera, partialTicks); }
        return new Vec3d(((def.skyColor >> 16) & 255) / 255.0D, ((def.skyColor >> 8) & 255) / 255.0D, (def.skyColor & 255) / 255.0D);
    }

    @Override @SideOnly(Side.CLIENT) @Nonnull public Vec3d getFogColor(float celestialAngle, float partialTicks) {
        if (def == null || def.fogColor < 0) { return super.getFogColor(celestialAngle, partialTicks); }
        return new Vec3d(((def.fogColor >> 16) & 255) / 255.0D, ((def.fogColor >> 8) & 255) / 255.0D, (def.fogColor & 255) / 255.0D);
    }

    @Override @Nonnull public String getSaveFolder() { return def == null ? "DIM" + getDimension() : def.suffix; }
}
