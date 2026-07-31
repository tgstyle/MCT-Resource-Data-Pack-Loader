package mctmods.resourcedatapackloader.mixin;

import mctmods.resourcedatapackloader.content.worldgen.ContentGameRules;

import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(World.class)
public abstract class MixinWorld {
    @Unique private static final int rdpl$NOTIFY_NEIGHBORS = 1;
    @Unique private static final int rdpl$SUPPRESS_OBSERVERS = 16;

    @Inject(method = "getGameRules", at = @At("HEAD"), cancellable = true)
    private void rdpl$dimensionRules(CallbackInfoReturnable<GameRules> cir) {
        GameRules rules = ContentGameRules.forWorld((World) (Object) this);
        if (rules != null) { cir.setReturnValue(rules); }
    }

    @ModifyVariable(method = "markAndNotifyBlock", at = @At("HEAD"), argsOnly = true, index = 5, remap = false)
    private int rdpl$suppressObserverScan(int flags) {
        if (AccessorChunk.rdpl$getPopulating() == null) { return flags; }
        return (flags | rdpl$SUPPRESS_OBSERVERS) & ~rdpl$NOTIFY_NEIGHBORS;
    }
}
