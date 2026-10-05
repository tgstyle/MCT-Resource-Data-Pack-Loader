package mctmods.resourcedatapackloader.util;

import mctmods.resourcedatapackloader.content.def.RainDef;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ParticleStatus;
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

public final class RainSplash {
    private static final int REACH = 10;
    private static final float PER_BLOCK = 0.225F;
    private static int soundCounter;

    private RainSplash() {}

    public static ParticleOptions particle(RainDef rain) { return BuiltInRegistries.PARTICLE_TYPE.getValue(rain.particle()) instanceof SimpleParticleType type ? type : ParticleTypes.RAIN; }

    public static void sound(ClientLevel level, RainDef rain, BlockPos camera, BlockPos at) {
        SoundEvent sound = BuiltInRegistries.SOUND_EVENT.getValue(rain.sound());
        if (sound == null) { return; }
        if (at.getY() > camera.getY() + 1 && level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, camera).getY() > Mth.floor(camera.getY())) { level.playLocalSound(at, sound, SoundSource.WEATHER, rain.volume() * 0.5F, 0.5F, false); }
        else { level.playLocalSound(at, sound, SoundSource.WEATHER, rain.volume(), 1.0F, false); }
    }

    public static void tick(ClientLevel level, RainDef rain, long ticks, Camera camera) {
        float strength = level.getRainLevel(1.0F);
        if (strength <= 0.0F || rain.splashless()) { return; }
        Minecraft mc = Minecraft.getInstance();
        ParticleStatus status = mc.options.particles().get();
        int radius = mc.options.weatherRadius().get();
        int diameter = 2 * radius + 1;
        RandomSource random = RandomSource.createThreadLocalInstance(ticks * 312987231L);
        BlockPos at = BlockPos.containing(camera.position());
        BlockPos landed = null;
        ParticleOptions splash = particle(rain);
        int count = (int) (PER_BLOCK * diameter * diameter * strength * strength) / (status == ParticleStatus.DECREASED ? 2 : 1);
        for (int drop = 0; drop < count; drop++) {
            BlockPos top = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, at.offset(random.nextInt(diameter) - radius, 0, random.nextInt(diameter) - radius));
            if (top.getY() <= level.getMinY() || top.getY() > at.getY() + REACH || top.getY() < at.getY() - REACH || precipitation(level, top) != Biome.Precipitation.RAIN) { continue; }
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

    private static Biome.Precipitation precipitation(ClientLevel level, BlockPos pos) {
        if (!level.getChunkSource().hasChunk(SectionPos.blockToSectionCoord(pos.getX()), SectionPos.blockToSectionCoord(pos.getZ()))) { return Biome.Precipitation.NONE; }
        return level.getBiome(pos).value().getPrecipitationAt(pos, level.getSeaLevel());
    }
}
