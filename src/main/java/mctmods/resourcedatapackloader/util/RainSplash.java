package mctmods.resourcedatapackloader.util;

import mctmods.resourcedatapackloader.content.def.RainDef;
import mctmods.resourcedatapackloader.mixin.rdpl.client.IParticleManager;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRain;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import java.util.Random;
import javax.annotation.Nullable;

public final class RainSplash {
    private static final int RADIUS = 10;
    private static final Random RANDOM = new Random();
    private static int soundCounter;

    private RainSplash() {}

    public static Particle particle(WorldClient world, @Nullable RainDef rain, double x, double y, double z) {
        EnumParticleTypes type = rain == null ? null : EnumParticleTypes.getByName(rain.particle);
        IParticleFactory factory = type == null ? null : ((IParticleManager) Minecraft.getMinecraft().effectRenderer).getParticleTypes().get(type.getParticleID());
        Particle particle = factory == null ? null : factory.createParticle(type.getParticleID(), world, x, y, z, 0.0D, 0.0D, 0.0D);
        return particle == null ? new ParticleRain.Factory().createParticle(EnumParticleTypes.WATER_DROP.getParticleID(), world, x, y, z, 0.0D, 0.0D, 0.0D) : particle;
    }

    public static void sound(World world, RainDef rain, BlockPos pos, double x, double y, double z) {
        SoundEvent sound = ForgeRegistries.SOUND_EVENTS.getValue(rain.sound);
        if (sound == null) { return; }
        if ((int) y >= pos.getY() + 1 && world.getPrecipitationHeight(pos).getY() > pos.getY()) { world.playSound(x, y, z, sound, SoundCategory.WEATHER, rain.volume * 0.5F, 0.5F, false); }
        else { world.playSound(x, y, z, sound, SoundCategory.WEATHER, rain.volume, 1.0F, false); }
    }

    public static void tick(Minecraft mc, RainDef rain, int rendererUpdateCount) {
        WorldClient world = mc.world;
        Entity view = mc.getRenderViewEntity();
        float strength = world.getRainStrength(1.0F);
        if (!mc.gameSettings.fancyGraphics) { strength /= 2.0F; }
        if (strength == 0.0F || view == null) { return; }
        RANDOM.setSeed(rendererUpdateCount * 312987231L);
        BlockPos at = new BlockPos(view);
        int count = (int) (100.0F * strength * strength);
        if (mc.gameSettings.particleSetting == 1) { count >>= 1; }
        else if (mc.gameSettings.particleSetting == 2) { count = 0; }
        int landed = 0;
        double soundX = 0.0D;
        double soundY = 0.0D;
        double soundZ = 0.0D;
        for (int drop = 0; drop < count; drop++) {
            BlockPos top = world.getPrecipitationHeight(at.add(RANDOM.nextInt(RADIUS) - RANDOM.nextInt(RADIUS), 0, RANDOM.nextInt(RADIUS) - RANDOM.nextInt(RADIUS)));
            Biome biome = world.getBiome(top);
            if (top.getY() > at.getY() + RADIUS || top.getY() < at.getY() - RADIUS || !biome.canRain() || biome.getTemperature(top) < 0.15F) { continue; }
            BlockPos ground = top.down();
            IBlockState state = world.getBlockState(ground);
            double dx = RANDOM.nextDouble();
            double dz = RANDOM.nextDouble();
            AxisAlignedBB box = state.getBoundingBox(world, ground);
            if (state.getMaterial() == Material.LAVA || state.getBlock() == Blocks.MAGMA) { world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, top.getX() + dx, top.getY() + 0.1D - box.minY, top.getZ() + dz, 0.0D, 0.0D, 0.0D); }
            else if (state.getMaterial() != Material.AIR) {
                double y = ground.getY() + 0.1D + box.maxY;
                if (RANDOM.nextInt(++landed) == 0) {
                    soundX = ground.getX() + dx;
                    soundY = y - 1.0D;
                    soundZ = ground.getZ() + dz;
                }
                mc.effectRenderer.addEffect(particle(world, rain, ground.getX() + dx, y, ground.getZ() + dz));
            }
        }
        if (landed == 0) { return; }
        if (rain.interval > 0 && RANDOM.nextInt(rain.interval) >= soundCounter++) { return; }
        soundCounter = 0;
        sound(world, rain, at, soundX, soundY, soundZ);
    }
}
