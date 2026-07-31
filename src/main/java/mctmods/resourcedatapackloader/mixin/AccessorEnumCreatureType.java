package mctmods.resourcedatapackloader.mixin;

import net.minecraft.entity.EnumCreatureType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EnumCreatureType.class)
public interface AccessorEnumCreatureType {
    @Accessor("maxNumberOfCreature") int rdpl$getMaxNumberOfCreature();

    @Accessor("maxNumberOfCreature") @Mutable void rdpl$setMaxNumberOfCreature(int value);
}
