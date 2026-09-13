package mctmods.resourcedatapackloader.content.item;

import mctmods.resourcedatapackloader.content.block.ContentFluidType;

import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import javax.annotation.Nonnull;

public final class ContentBucketItem extends BucketItem {
    private static final String FILLED = "rdpl.item.bucketFilled";
    private final ContentFluidType type;

    public ContentBucketItem(Fluid fluid, ContentFluidType type, Properties properties) {
        super(fluid, properties);
        this.type = type;
    }

    @Override @Nonnull public Component getName(@Nonnull ItemStack stack) { return Language.getInstance().has(getDescriptionId()) ? super.getName(stack) : Component.translatable(FILLED, type.getDescription()); }
}
