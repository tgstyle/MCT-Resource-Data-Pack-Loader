package mctmods.resourcedatapackloader.mixin.rdpl.common;

import com.google.common.collect.ImmutableList;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Potion.class) public interface IPotion { @Accessor("effects") @Mutable void rdpl$setEffects(ImmutableList<MobEffectInstance> effects); }
