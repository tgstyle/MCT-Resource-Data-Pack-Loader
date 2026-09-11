package mctmods.resourcedatapackloader.mixin.rdpl.common;

import net.minecraft.entity.monster.EntityCreeper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EntityCreeper.class) public interface IEntityCreeper {
    @Accessor("fuseTime") void rdpl$setFuseTime(int fuseTime);

    @Accessor("explosionRadius") void rdpl$setExplosionRadius(int explosionRadius);
}
