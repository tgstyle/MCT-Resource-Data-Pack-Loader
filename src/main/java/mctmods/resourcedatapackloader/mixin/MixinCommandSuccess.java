package mctmods.resourcedatapackloader.mixin;

import mctmods.resourcedatapackloader.client.ChatHistoryKeeper;

import net.minecraft.command.CommandHandler;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CommandHandler.class)
public abstract class MixinCommandSuccess {
    @Inject(method = "executeCommand", at = @At("RETURN"))
    private void rdpl$keepRanCommand(ICommandSender sender, String rawCommand, CallbackInfoReturnable<Integer> cir) {
        if (cir.getReturnValueI() <= 0 || !(sender instanceof EntityPlayerMP)) { return; }
        if (FMLCommonHandler.instance().getSide() != Side.CLIENT) { return; }

        ChatHistoryKeeper.commandRan(rawCommand);
    }
}
