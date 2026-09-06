package mctmods.resourcedatapackloader.mixin.rdpl.common;

import net.minecraft.world.entity.MobCategory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MobCategory.class) public interface IMobCategory {
    @Accessor("max") @Mutable void rdpl$setMax(int max);
}
