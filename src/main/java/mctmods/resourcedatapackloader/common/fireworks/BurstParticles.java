package mctmods.resourcedatapackloader.common.fireworks;

import com.mojang.serialization.Codec;
import mctmods.resourcedatapackloader.ResourceDataPackLoader;

import javax.annotation.Nonnull;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public final class BurstParticles {
    public static final DeferredRegister<ParticleType<?>> REGISTER = DeferredRegister.create(Registries.PARTICLE_TYPE, ResourceDataPackLoader.MOD_ID);
    public static final RegistryObject<ParticleType<BurstParticleOption>> BURST = REGISTER.register("firework_burst", () -> new ParticleType<>(false, BurstParticleOption.DESERIALIZER) {
        @Override
        @Nonnull
        public Codec<BurstParticleOption> codec() { return BurstParticleOption.codec(this); }
    });

    private BurstParticles() {}
}
