package mctmods.resourcedatapackloader.mixin.rdpl.common;

import net.minecraft.world.entity.EntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EntityType.class) public interface IEntityType {
    @Accessor("factory") EntityType.EntityFactory<?> rdpl$factory();
}
