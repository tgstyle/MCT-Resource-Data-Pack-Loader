package mctmods.resourcedatapackloader.mixin.entity;

import mctmods.resourcedatapackloader.common.board.ContentBoards;
import mctmods.resourcedatapackloader.common.dimension.ContentDimensionRules;
import mctmods.resourcedatapackloader.common.entities.ContentEntities;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.Entity;
import net.minecraft.world.WorldProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class) public abstract class MixinEntity {
    @Inject(method = "setFire", at = @At("HEAD"), cancellable = true) private void rdpl$neverCatchesFire(int seconds, CallbackInfo ci) {
        Entity self = (Entity) (Object) this;
        if (ContentEntities.fireproof(self) || ContentEntities.struckFireless(self) || ContentBoards.piece(self)) { ci.cancel(); }
    }

    @WrapOperation(method = "changeDimension(ILnet/minecraftforge/common/util/ITeleporter;)Lnet/minecraft/entity/Entity;", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/WorldProvider;getMovementFactor()D"), remap = false) private double rdpl$existingMovementFactor(WorldProvider provider, Operation<Double> original) { return ContentDimensionRules.existingMovementFactor(provider, original.call(provider)); }
}
