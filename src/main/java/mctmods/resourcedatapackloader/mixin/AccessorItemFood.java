package mctmods.resourcedatapackloader.mixin;

import net.minecraft.item.ItemFood;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ItemFood.class)
public interface AccessorItemFood {
    @Accessor("healAmount") int rdpl$getHealAmount();

    @Accessor("healAmount") @Mutable void rdpl$setHealAmount(int heal);

    @Accessor("saturationModifier") float rdpl$getSaturationModifier();

    @Accessor("saturationModifier") @Mutable void rdpl$setSaturationModifier(float saturation);

    @Accessor("alwaysEdible") boolean rdpl$getAlwaysEdible();

    @Accessor("alwaysEdible") void rdpl$setAlwaysEdible(boolean alwaysEdible);
}
