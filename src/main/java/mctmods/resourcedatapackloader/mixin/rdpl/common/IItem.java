package mctmods.resourcedatapackloader.mixin.rdpl.common;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;
import javax.annotation.Nullable;

@Mixin(Item.class) public interface IItem {
    @Accessor("craftingRemainingItem") @Nullable ItemStackTemplate rdpl$getCraftingRemainingItem();

    @Accessor("craftingRemainingItem") @Mutable void rdpl$setCraftingRemainingItem(@Nullable ItemStackTemplate item);
}
