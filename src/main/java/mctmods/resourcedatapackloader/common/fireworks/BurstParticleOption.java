package mctmods.resourcedatapackloader.common.fireworks;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;

import java.util.Objects;
import javax.annotation.Nonnull;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

@SuppressWarnings("deprecation")
public final class BurstParticleOption implements ParticleOptions {
    public static final ParticleOptions.Deserializer<BurstParticleOption> DESERIALIZER = new ParticleOptions.Deserializer<>() {
        @Override
        @Nonnull
        public BurstParticleOption fromCommand(@Nonnull ParticleType<BurstParticleOption> type, @Nonnull StringReader reader) throws CommandSyntaxException {
            reader.expect(' ');
            Item item = ForgeRegistries.ITEMS.getValue(ResourceLocation.read(reader));
            return new BurstParticleOption(type, new ItemStack(Objects.requireNonNull(item)));
        }

        @Override
        @Nonnull
        public BurstParticleOption fromNetwork(@Nonnull ParticleType<BurstParticleOption> type, @Nonnull FriendlyByteBuf buffer) {
            return new BurstParticleOption(type, buffer.readItem());
        }
    };

    private final ParticleType<BurstParticleOption> type;
    private final ItemStack stack;

    public BurstParticleOption(ParticleType<BurstParticleOption> type, ItemStack stack) {
        this.type = type;
        this.stack = stack.copy();
    }

    public static Codec<BurstParticleOption> codec(ParticleType<BurstParticleOption> type) {
        return ItemStack.CODEC.xmap(stack -> new BurstParticleOption(type, stack), option -> option.stack);
    }

    @Override
    public void writeToNetwork(@Nonnull FriendlyByteBuf buffer) { buffer.writeItem(stack); }

    @Override
    @Nonnull
    public String writeToString() { return ForgeRegistries.PARTICLE_TYPES.getKey(type) + " " + ForgeRegistries.ITEMS.getKey(stack.getItem()); }

    @Override
    @Nonnull
    public ParticleType<BurstParticleOption> getType() { return type; }

    public ItemStack getItem() { return stack; }
}
