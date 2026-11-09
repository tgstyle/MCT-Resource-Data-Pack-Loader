package mctmods.resourcedatapackloader.common.fireworks;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;

import com.mojang.serialization.MapCodec;
import javax.annotation.Nonnull;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class FireworkParticles {
    public static final DeferredRegister<ParticleType<?>> REGISTER = DeferredRegister.create(Registries.PARTICLE_TYPE, ResourceDataPackLoader.MOD_ID);
    public static final DeferredHolder<ParticleType<?>, ParticleType<FireworkParticleOption>> BURST = REGISTER.register("firework_burst", BurstType::new);

    private FireworkParticles() {}

    private static final class BurstType extends ParticleType<FireworkParticleOption> {
        BurstType() { super(false); }

        @Override @Nonnull
        public MapCodec<FireworkParticleOption> codec() { return FireworkParticleOption.codec(this); }

        @Override @Nonnull
        public StreamCodec<? super RegistryFriendlyByteBuf, FireworkParticleOption> streamCodec() { return FireworkParticleOption.streamCodec(this); }
    }
}
