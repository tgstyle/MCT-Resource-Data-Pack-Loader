package mctmods.resourcedatapackloader.mixin.rdpl.client;

import mctmods.resourcedatapackloader.client.ChatHistoryKeeper;

import com.mojang.brigadier.ParseResults;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Commands.class) public abstract class MixinCommandsKept {
    @Inject(method = "performCommand", at = @At("HEAD"))
    private void rdpl$watchTypedCommand(ParseResults<CommandSourceStack> parseResults, String command, CallbackInfo ci) { ChatHistoryKeeper.watch(parseResults.getContext().getSource()); }

    @Inject(method = "performCommand", at = @At("RETURN"))
    private void rdpl$keepRanCommand(ParseResults<CommandSourceStack> parseResults, String command, CallbackInfo ci) { ChatHistoryKeeper.finished(command); }
}
