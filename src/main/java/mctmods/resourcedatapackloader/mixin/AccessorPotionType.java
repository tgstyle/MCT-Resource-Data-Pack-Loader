package mctmods.resourcedatapackloader.mixin;

import com.google.common.collect.ImmutableList;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(PotionType.class)
public interface AccessorPotionType {
    @Accessor("effects") ImmutableList<PotionEffect> rdpl$getEffects();

    @Accessor("effects") @Mutable void rdpl$setEffects(ImmutableList<PotionEffect> effects);
}
