package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.worldgen.ContentPregen;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(World.class) public abstract class MixinWorldEntityHold {
    @Inject(method = "updateEntityWithOptionalForce", at = @At("HEAD"), cancellable = true) private void rdpl$holdStillWhileLandIsMade(Entity entityIn, boolean forceUpdate, CallbackInfo ci) {
        if (entityIn instanceof EntityPlayer) { return; }
        if (ContentPregen.holdsStill(World.class.cast(this))) { ci.cancel(); }
    }
}
