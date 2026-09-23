package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.entity.ContentTasks;

import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.animal.Animal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BreedGoal.class) public abstract class MixinBreedGoal {
    @Redirect(method = "getFreePartner", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/Animal;canMate(Lnet/minecraft/world/entity/animal/Animal;)Z"))
    private boolean rdpl$sameVariant(Animal animal, Animal partner) { return animal.canMate(partner) && ContentTasks.mates(animal, partner); }
}
