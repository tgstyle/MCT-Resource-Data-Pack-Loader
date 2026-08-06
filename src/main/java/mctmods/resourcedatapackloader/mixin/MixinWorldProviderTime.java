package mctmods.resourcedatapackloader.mixin;

import mctmods.resourcedatapackloader.content.worldgen.ContentTerrain;

import net.minecraft.world.WorldProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WorldProvider.class)
public abstract class MixinWorldProviderTime {
    @Inject(method = "getWorldTime", at = @At("HEAD"), cancellable = true, remap = false)
    private void rdpl$lockedTime(CallbackInfoReturnable<Long> cir) {
        int wanted = rdpl$wanted();
        if (wanted < 0) { return; }

        cir.setReturnValue((long) wanted);
    }

    @Inject(method = "isDaytime", at = @At("HEAD"), cancellable = true, remap = false)
    private void rdpl$lockedDaytime(CallbackInfoReturnable<Boolean> cir) {
        int wanted = rdpl$wanted();
        if (wanted < 0) { return; }

        long time = wanted % 24000L;
        cir.setReturnValue(time < 12300L || time > 23850L);
    }

    @Unique private int rdpl$wanted() {
        WorldProvider self = (WorldProvider) (Object) this;
        if (self.getDimension() != 0) { return -1; }

        return ContentTerrain.worldTime();
    }
}
