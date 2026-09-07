package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.worldgen.ContentGameRules;

import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Level.class) public abstract class MixinLevel {
    @Inject(method = "getGameRules", at = @At("HEAD"), cancellable = true)
    private void rdpl$dimensionRules(CallbackInfoReturnable<GameRules> cir) {
        GameRules held = ContentGameRules.forLevel((Level) (Object) this);
        if (held != null) { cir.setReturnValue(held); }
    }
}
