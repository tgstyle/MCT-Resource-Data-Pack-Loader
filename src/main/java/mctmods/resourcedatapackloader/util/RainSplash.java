package mctmods.resourcedatapackloader.util;

import mctmods.resourcedatapackloader.content.def.RainDef;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.ParticleStatus;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.registries.ForgeRegistries;

public final class RainSplash {
    private static final int RADIUS = 10;
    private static int soundCounter;

    private RainSplash() {}

    public static ParticleOptions particle(RainDef rain) { return ForgeRegistries.PARTICLE_TYPES.getValue(rain.particle()) instanceof SimpleParticleType type ? type : ParticleTypes.RAIN; }

    public static void sound(ClientLevel level, RainDef rain, BlockPos camera, BlockPos at) {
        SoundEvent sound = ForgeRegistries.SOUND_EVENTS.getValue(rain.sound());
        if (sound == null) { return; }
        if (at.getY() > camera.getY() + 1 && level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, camera).getY() > Mth.floor(camera.getY())) { level.playLocalSound(at, sound, SoundSource.WEATHER, rain.volume() * 0.5F, 0.5F, false); }
        else { level.playLocalSound(at, sound, SoundSource.WEATHER, rain.volume(), 1.0F, false); }
    }

    public static void tick(ClientLevel level, RainDef rain, int ticks, Camera camera) {
        Minecraft mc = Minecraft.getInstance();
        float strength = level.getRainLevel(1.0F) / (Minecraft.useFancyGraphics() ? 1.0F : 2.0F);
        if (strength <= 0.0F || rain.splashless()) { return; }
        RandomSource random = RandomSource.create(ticks * 312987231L);
        BlockPos at = BlockPos.containing(camera.getPosition());
        BlockPos landed = null;
        ParticleStatus status = mc.options.particles().get();
        ParticleOptions splash = particle(rain);
        int count = (int) (100.0F * strength * strength) / (status == ParticleStatus.DECREASED ? 2 : 1);
        for (int drop = 0; drop < count; drop++) {
            BlockPos top = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, at.offset(random.nextInt(RADIUS * 2 + 1) - RADIUS, 0, random.nextInt(RADIUS * 2 + 1) - RADIUS));
            if (top.getY() <= level.getMinBuildHeight() || top.getY() > at.getY() + RADIUS || top.getY() < at.getY() - RADIUS) { continue; }
            Biome biome = level.getBiome(top).value();
            if (biome.getPrecipitationAt(top) != Biome.Precipitation.RAIN) { continue; }
            landed = top.below();
            if (status == ParticleStatus.MINIMAL) { break; }
            double dx = random.nextDouble();
            double dz = random.nextDouble();
            BlockState state = level.getBlockState(landed);
            FluidState fluid = level.getFluidState(landed);
            double y = Math.max(state.getCollisionShape(level, landed).max(Direction.Axis.Y, dx, dz), fluid.getHeight(level, landed));
            boolean hot = fluid.is(FluidTags.LAVA) || state.is(Blocks.MAGMA_BLOCK) || CampfireBlock.isLitCampfire(state);
            level.addParticle(hot ? ParticleTypes.SMOKE : splash, landed.getX() + dx, landed.getY() + y, landed.getZ() + dz, 0.0D, 0.0D, 0.0D);
        }
        if (landed == null) { return; }
        if (rain.interval() > 0 && random.nextInt(rain.interval()) >= soundCounter++) { return; }
        soundCounter = 0;
        sound(level, rain, at, landed);
    }
}
