package mctmods.resourcedatapackloader.common.fireworks;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import javax.annotation.Nonnull;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

public final class FireworkParticleOption implements ParticleOptions {
    private static final Codec<ItemStack> ITEM_CODEC = Codec.withAlternative(ItemStack.SINGLE_ITEM_CODEC, ItemStack.ITEM_NON_AIR_CODEC, ItemStack::new);
    private final ParticleType<FireworkParticleOption> type;
    private final ItemStack stack;

    public FireworkParticleOption(ParticleType<FireworkParticleOption> type, ItemStack stack) {
        this.type = type;
        this.stack = stack.copy();
    }

    public static MapCodec<FireworkParticleOption> codec(ParticleType<FireworkParticleOption> type) {
        return ITEM_CODEC.xmap(stack -> new FireworkParticleOption(type, stack), FireworkParticleOption::getItem).fieldOf("item");
    }

    public static StreamCodec<? super RegistryFriendlyByteBuf, FireworkParticleOption> streamCodec(ParticleType<FireworkParticleOption> type) {
        return ItemStack.STREAM_CODEC.map(stack -> new FireworkParticleOption(type, stack), FireworkParticleOption::getItem);
    }

    @Override @Nonnull
    public ParticleType<FireworkParticleOption> getType() { return type; }

    public ItemStack getItem() { return stack; }
}
