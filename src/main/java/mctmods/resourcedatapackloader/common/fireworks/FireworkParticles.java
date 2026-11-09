package mctmods.resourcedatapackloader.common.fireworks;

import com.mojang.serialization.MapCodec;
import mctmods.resourcedatapackloader.ResourceDataPackLoader;

import javax.annotation.Nonnull;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class FireworkParticles {
    public static final DeferredRegister<ParticleType<?>> REGISTER = DeferredRegister.create(Registries.PARTICLE_TYPE, ResourceDataPackLoader.MOD_ID);
    public static final DeferredHolder<ParticleType<?>, BurstType> BURST_ITEM = REGISTER.register("burst_item", BurstType::new);

    private FireworkParticles() {}

    public static final class BurstType extends ParticleType<ItemParticleOption> {
        private final MapCodec<ItemParticleOption> codec = ItemParticleOption.codec(this);
        private final StreamCodec<? super RegistryFriendlyByteBuf, ItemParticleOption> streamCodec = ItemParticleOption.streamCodec(this);

        public BurstType() { super(true); }

        @Override @Nonnull
        public MapCodec<ItemParticleOption> codec() { return codec; }

        @Override @Nonnull
        public StreamCodec<? super RegistryFriendlyByteBuf, ItemParticleOption> streamCodec() { return streamCodec; }
    }
}
