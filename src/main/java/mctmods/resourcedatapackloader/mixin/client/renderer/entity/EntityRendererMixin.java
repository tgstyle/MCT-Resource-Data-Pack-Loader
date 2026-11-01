package mctmods.resourcedatapackloader.mixin.client.renderer.entity;

import mctmods.resourcedatapackloader.common.entities.ContentEntities;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderer.class) public abstract class EntityRendererMixin {
    @Inject(method = "getBlockLightLevel", at = @At("RETURN"), cancellable = true) private void rdpl$litRight(Entity entity, BlockPos pos, CallbackInfoReturnable<Integer> cir) { if (ContentEntities.bright(entity)) { cir.setReturnValue(15); } }
}
