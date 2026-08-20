package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.pack.PackOptions;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraftforge.fml.common.StartupQuery;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = StartupQuery.class, remap = false) public abstract class MixinStartupQuery {
    @Inject(method = "confirm", at = @At("HEAD"), cancellable = true, remap = false) private static void rdpl$packOptionsExplainIt(String text, CallbackInfoReturnable<Boolean> cir) {
        if (PackOptions.worldChanged().isEmpty()) { return; }

        ContentLog.LOGGER.info("Pack options have changed this world, so the warning about missing registry entries is passed over. A backup of the previous world is made in the saves folder. Changed: {}", PackOptions.worldChanged());
        cir.setReturnValue(Boolean.TRUE);
    }
}
