package mctmods.resourcedatapackloader.mixin.client.commands;

import mctmods.resourcedatapackloader.client.util.ChatHistoryKeeper;

import com.mojang.brigadier.ParseResults;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Commands.class) public abstract class CommandsKeptMixin {
    @Inject(method = "performCommand", at = @At("RETURN")) private void rdpl$keepRanCommand(ParseResults<CommandSourceStack> parseResults, String command, CallbackInfoReturnable<Integer> cir) { if (cir.getReturnValueI() > 0 && parseResults.getContext().getSource().getEntity() instanceof ServerPlayer) { ChatHistoryKeeper.commandRan(command); } }
}
