package mctmods.resourcedatapackloader.mixin.world.entity;

import mctmods.resourcedatapackloader.common.entities.behavior.ContentTasks;

import net.minecraft.world.entity.TamableAnimal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(TamableAnimal.class) public abstract class TamableAnimalMixin {
    @Shadow protected abstract boolean canFlyToOwner();

    @Redirect(method = "canTeleportTo", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/TamableAnimal;canFlyToOwner()Z")) private boolean rdpl$leavesByTask(TamableAnimal tamable) { return ContentTasks.landsOnLeaves(tamable, canFlyToOwner()); }
}
