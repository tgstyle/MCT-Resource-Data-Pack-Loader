package mctmods.resourcedatapackloader.common.items;

import mctmods.resourcedatapackloader.common.blocks.fluid.ContentFluidType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.BlockHitResult;

public final class ContentBucketItem extends BucketItem {
    private static final String FILLED = "rdpl.item.bucketFilled";
    private final ContentFluidType type;
    private final boolean placeable;

    public ContentBucketItem(Fluid fluid, ContentFluidType type, boolean placeable, Properties properties) {
        super(fluid, properties);
        this.type = type;
        this.placeable = placeable;
    }

    @Override public boolean emptyContents(@Nullable Player player, @Nonnull Level level, @Nonnull BlockPos pos, @Nullable BlockHitResult result, @Nullable ItemStack container) { return placeable && super.emptyContents(player, level, pos, result, container); }

    @Override @Nonnull public Component getName(@Nonnull ItemStack stack) { return Language.getInstance().has(getDescriptionId()) ? super.getName(stack) : Component.translatable(FILLED, type.getDescription()); }
}
