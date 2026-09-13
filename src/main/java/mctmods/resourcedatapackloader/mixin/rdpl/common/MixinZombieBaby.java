package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.entity.ContentEntities;

import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Zombie;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Zombie.class) public abstract class MixinZombieBaby {
    @Redirect(method = "finalizeSpawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/monster/Zombie;getSpawnAsBabyOdds(Lnet/minecraft/util/RandomSource;)Z"))
    private boolean rdpl$ownYoung(RandomSource random) {
        return ContentEntities.baseBabyChance((Entity) (Object) this, 1.0F) > 0.0F && Zombie.getSpawnAsBabyOdds(random);
    }
}
