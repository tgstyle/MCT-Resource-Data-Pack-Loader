package mctmods.resourcedatapackloader.mixin.client.server;

import mctmods.resourcedatapackloader.common.pack.loading.ContentServer;

import net.minecraft.client.server.IntegratedServer;
import net.minecraft.world.level.GameType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(IntegratedServer.class) public abstract class IntegratedServerMixin {
    @ModifyVariable(method = "publishServer", at = @At("HEAD"), argsOnly = true) private boolean rdpl$lanCommands(boolean cheats) { return cheats && ContentServer.lanCommands(); }

    @Inject(method = "isCommandBlockEnabled", at = @At("HEAD"), cancellable = true) private void rdpl$commandBlocks(CallbackInfoReturnable<Boolean> cir) { if (ContentServer.off(ContentServer.commandBlocks())) { cir.setReturnValue(false); } }

    @Inject(method = "getForcedGameType", at = @At("HEAD"), cancellable = true) private void rdpl$forceGameMode(CallbackInfoReturnable<GameType> cir) { if (ContentServer.off(ContentServer.forceGameMode())) { cir.setReturnValue(null); } }
}
