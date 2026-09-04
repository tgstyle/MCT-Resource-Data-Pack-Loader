package mctmods.resourcedatapackloader.mixin.rdpl.common;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;
import java.util.List;

@Mixin(Potion.class) public interface IPotion { @Accessor("effects") @Mutable void rdpl$setEffects(List<MobEffectInstance> effects); }
