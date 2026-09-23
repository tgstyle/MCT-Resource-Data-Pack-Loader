package mctmods.resourcedatapackloader.mixin.rdpl.common;

import net.minecraft.world.entity.monster.Ghast;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Ghast.class) public interface IGhast {
    @Accessor("explosionPower") void rdpl$setExplosionPower(int power);
}
