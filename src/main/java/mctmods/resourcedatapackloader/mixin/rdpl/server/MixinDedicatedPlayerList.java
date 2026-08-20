package mctmods.resourcedatapackloader.mixin.rdpl.server;

import mctmods.resourcedatapackloader.mixin.rdpl.common.MixinPlayerList;

import net.minecraft.server.dedicated.DedicatedPlayerList;
import net.minecraft.server.dedicated.DedicatedServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DedicatedPlayerList.class) public class MixinDedicatedPlayerList extends MixinPlayerList {
    @Inject(method = "<init>", at = @At(value = "RETURN")) private void setVerticalViewDistance(DedicatedServer server, CallbackInfo cbi) {
        this.list$setVerticalViewDistance(server.getIntProperty("vertical-view-distance", -1));
    }
}
