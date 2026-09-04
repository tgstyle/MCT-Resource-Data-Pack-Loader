package mctmods.resourcedatapackloader.mixin.vintagefix;

import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.pack.RDPLResourcePack;

import org.embeddedt.vintagefix.dynamicresources.ResourcePackHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.stream.Collectors;

@Mixin(value = ResourcePackHelper.class, remap = false) public abstract class MixinResourcePackHelper {
    @Inject(method = "<clinit>", at = @At("TAIL")) private static void rdpl$listPacks(CallbackInfo ci) {
        ResourcePackHelper.registerAdapter(RDPLResourcePack.class, (pack, filter) -> PackManager.get().rawPaths(pack.isOverriding()).stream().filter(filter).collect(Collectors.toList()).iterator());
    }
}
