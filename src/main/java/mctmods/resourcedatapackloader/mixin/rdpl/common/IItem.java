package mctmods.resourcedatapackloader.mixin.rdpl.common;

import net.minecraft.core.component.DataComponentMap;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;
import javax.annotation.Nullable;

@Mixin(Item.class) public interface IItem {
    @Accessor("components") void rdpl$setComponents(DataComponentMap components);

    @Accessor("craftingRemainingItem") @Nullable Item rdpl$getCraftingRemainingItem();

    @Accessor("craftingRemainingItem") @Mutable void rdpl$setCraftingRemainingItem(@Nullable Item item);
}
