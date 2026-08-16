package mctmods.resourcedatapackloader.mixin;

import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import javax.annotation.Nullable;

@Mixin(Item.class)
public interface AccessorItem {
    @Accessor("maxStackSize") int rdpl$getMaxStackSize();

    @Accessor("maxDamage") int rdpl$getMaxDamage();

    @Accessor("containerItem") void rdpl$setContainerItem(@Nullable Item containerItem);
}
