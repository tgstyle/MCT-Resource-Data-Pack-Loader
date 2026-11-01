package mctmods.resourcedatapackloader.mixin.client.server;

import mctmods.resourcedatapackloader.common.pack.loading.ContentServer;

import net.minecraft.client.server.IntegratedServer;
import net.minecraft.world.level.GameType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(IntegratedServer.class) public abstract class IntegratedServerMixin {
    @ModifyArg(method = "updateCommandsAllowedForOtherPlayers()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/players/PlayerList;setAllowCommandsForAllPlayers(Z)V")) private boolean rdpl$lanCommands(boolean allowCommands) { return allowCommands && ContentServer.lanCommands(); }

    @Inject(method = "getForcedGameType()Lnet/minecraft/world/level/GameType;", at = @At("HEAD"), cancellable = true) private void rdpl$forceGameMode(CallbackInfoReturnable<GameType> cir) { if (ContentServer.off(ContentServer.forceGameMode())) { cir.setReturnValue(null); } }
}
