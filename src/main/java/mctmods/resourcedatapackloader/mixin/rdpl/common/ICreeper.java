package mctmods.resourcedatapackloader.mixin.rdpl.common;

import net.minecraft.world.entity.monster.Creeper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Creeper.class) public interface ICreeper {
    @Accessor("maxSwell") void rdpl$setMaxSwell(int ticks);

    @Accessor("explosionRadius") void rdpl$setExplosionRadius(int radius);
}
