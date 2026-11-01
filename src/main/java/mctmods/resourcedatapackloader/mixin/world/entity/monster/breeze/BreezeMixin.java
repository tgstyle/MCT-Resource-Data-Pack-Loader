package mctmods.resourcedatapackloader.mixin.world.entity.monster.breeze;

import mctmods.resourcedatapackloader.common.entities.ContentEntities;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.monster.breeze.Breeze;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Breeze.class) public abstract class BreezeMixin {
    @Shadow protected abstract SoundEvent getAmbientSound();

    @Redirect(method = "playAmbientSound", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/monster/breeze/Breeze;getAmbientSound()Lnet/minecraft/sounds/SoundEvent;")) private SoundEvent rdpl$ambientSound(Breeze self) { return ContentEntities.sound(self, ContentEntities.AMBIENT, this::getAmbientSound); }
}
