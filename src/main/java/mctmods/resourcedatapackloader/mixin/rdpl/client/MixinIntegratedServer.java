package mctmods.resourcedatapackloader.mixin.rdpl.client;

import mctmods.resourcedatapackloader.content.ContentServer;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.integrated.IntegratedServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(IntegratedServer.class) public abstract class MixinIntegratedServer {
    @ModifyVariable(method = "shareToLAN", at = @At("HEAD"), argsOnly = true) private boolean rdpl$lanCommands(boolean allowCheats) { return allowCheats && ContentServer.lanCommands(); }

    @Inject(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/fml/common/FMLCommonHandler;handleServerStarting(Lnet/minecraft/server/MinecraftServer;)Z", remap = false)) private void rdpl$packServerRules(CallbackInfoReturnable<Boolean> cir) { ContentServer.applyTo((MinecraftServer) (Object) this); }

    @Inject(method = "isCommandBlockEnabled", at = @At("HEAD"), cancellable = true) private void rdpl$commandBlocks(CallbackInfoReturnable<Boolean> cir) {
        if (Boolean.FALSE.equals(ContentServer.commandBlocks())) { cir.setReturnValue(false); }
    }
}
