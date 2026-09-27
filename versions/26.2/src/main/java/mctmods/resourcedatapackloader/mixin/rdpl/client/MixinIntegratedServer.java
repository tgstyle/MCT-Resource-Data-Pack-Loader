package mctmods.resourcedatapackloader.mixin.rdpl.client;

import mctmods.resourcedatapackloader.content.ContentServer;

import net.minecraft.client.server.IntegratedServer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.GameType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(IntegratedServer.class) public abstract class MixinIntegratedServer {
    @ModifyArg(method = "updateCommandsAllowedForOtherPlayers()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/players/PlayerList;setAllowCommandsForAllPlayers(Z)V")) private boolean rdpl$lanCommands(boolean allowCommands) { return allowCommands && ContentServer.lanCommands(); }

    @Inject(method = "initServer()Z", at = @At(value = "INVOKE", target = "Lnet/neoforged/neoforge/server/ServerLifecycleHooks;handleServerStarting(Lnet/minecraft/server/MinecraftServer;)V")) private void rdpl$packServerRules(CallbackInfoReturnable<Boolean> cir) { ContentServer.applyTo((MinecraftServer) (Object) this); }

    @Inject(method = "getForcedGameType()Lnet/minecraft/world/level/GameType;", at = @At("HEAD"), cancellable = true) private void rdpl$forceGameMode(CallbackInfoReturnable<GameType> cir) {
        if (Boolean.FALSE.equals(ContentServer.forceGameMode())) { cir.setReturnValue(null); }
    }
}
