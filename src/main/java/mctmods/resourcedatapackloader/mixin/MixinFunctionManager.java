package mctmods.resourcedatapackloader.mixin;

import mctmods.resourcedatapackloader.function.FunctionOverrides;

import net.minecraft.advancements.FunctionManager;
import net.minecraft.command.FunctionObject;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.Map;

@Mixin(FunctionManager.class)
public abstract class MixinFunctionManager {
    @Shadow @Final
    private Map<ResourceLocation, FunctionObject> functions;

    @Inject(method = "loadFunctions", at = @At("TAIL"))
    private void rdpl$injectPackFunctions(CallbackInfo ci) { FunctionOverrides.apply((FunctionManager) (Object) this, functions); }
}
