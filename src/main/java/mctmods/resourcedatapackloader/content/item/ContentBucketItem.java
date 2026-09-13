package mctmods.resourcedatapackloader.content.item;

import mctmods.resourcedatapackloader.content.block.ContentFluidType;

import net.minecraft.locale.Language;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.capability.wrappers.FluidBucketWrapper;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class ContentBucketItem extends BucketItem {
    private static final String FILLED = "rdpl.item.bucketFilled";
    private final ContentFluidType type;

    public ContentBucketItem(Supplier<? extends Fluid> fluid, ContentFluidType type, Properties properties) {
        super(fluid, properties);
        this.type = type;
    }

    @Override @Nonnull public ICapabilityProvider initCapabilities(@Nonnull ItemStack stack, @Nullable CompoundTag nbt) { return new FluidBucketWrapper(stack); }

    @Override @Nonnull public Component getName(@Nonnull ItemStack stack) { return Language.getInstance().has(getDescriptionId()) ? super.getName(stack) : Component.translatable(FILLED, type.getDescription()); }
}
