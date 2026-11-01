package mctmods.resourcedatapackloader.mixin.integration.groovyscript;

import mctmods.resourcedatapackloader.common.recipes.FurnaceBlocking;

import com.cleanroommc.groovyscript.compat.vanilla.Furnace;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Furnace.class, remap = false) public abstract class MixinFurnace {
    @Inject(method = "add(Lcom/cleanroommc/groovyscript/compat/vanilla/Furnace$Recipe;)V", at = @At("HEAD"), remap = false) private void rdpl$trust(CallbackInfo ci) { FurnaceBlocking.beginTrusted("GroovyScript"); }

    @Inject(method = "add(Lcom/cleanroommc/groovyscript/compat/vanilla/Furnace$Recipe;)V", at = @At("RETURN"), remap = false) private void rdpl$release(CallbackInfo ci) { FurnaceBlocking.endTrusted(); }
}
