package mctmods.resourcedatapackloader.mixin.rdpl.client;

import mctmods.resourcedatapackloader.content.ContentServer;

import net.minecraft.client.server.IntegratedServer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.GameType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(IntegratedServer.class) public abstract class MixinIntegratedServer {
    @ModifyVariable(method = "setGuestCommandAccess(Z)V", at = @At("HEAD"), argsOnly = true) private boolean rdpl$lanCommands(boolean commandAccess) { return commandAccess && ContentServer.lanCommands(); }

    @Inject(method = "initServer()Z", at = @At(value = "INVOKE", target = "Lnet/neoforged/neoforge/server/ServerLifecycleHooks;handleServerStarting(Lnet/minecraft/server/MinecraftServer;)V")) private void rdpl$packServerRules(CallbackInfoReturnable<Boolean> cir) { ContentServer.applyTo((MinecraftServer) (Object) this); }

    @Inject(method = "getForcedGameType()Lnet/minecraft/world/level/GameType;", at = @At("HEAD"), cancellable = true) private void rdpl$forceGameMode(CallbackInfoReturnable<GameType> cir) {
        if (Boolean.FALSE.equals(ContentServer.forceGameMode())) { cir.setReturnValue(null); }
    }
}
