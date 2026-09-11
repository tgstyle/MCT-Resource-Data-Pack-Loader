package mctmods.resourcedatapackloader.mixin.rdpl.common;

import net.minecraft.entity.monster.EntityGhast;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EntityGhast.class) public interface IEntityGhast {
    @Accessor("explosionStrength") void rdpl$setExplosionStrength(int explosionStrength);
}
