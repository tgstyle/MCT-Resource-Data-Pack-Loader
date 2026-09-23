package mctmods.resourcedatapackloader.mixin.rdpl.client;

import mctmods.resourcedatapackloader.client.GuiGameModeSwitcher;
import mctmods.resourcedatapackloader.util.Config;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.util.text.TextComponentTranslation;
import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import javax.annotation.Nullable;

@Mixin(Minecraft.class) public abstract class MixinMinecraft {
    @Shadow private IntegratedServer integratedServer;
    @Shadow public EntityPlayerSP player;
    @Shadow public GuiIngame ingameGUI;

    @Shadow public abstract void displayGuiScreen(@Nullable GuiScreen guiScreenIn);

    @Shadow protected abstract void debugFeedbackTranslated(String untranslatedTemplate, Object... objs);

    @ModifyArg(
            method = "launchIntegratedServer",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/LoadingScreenRenderer;displayLoadingString(Ljava/lang/String;)V", ordinal = 0),
            index = 0
    )
    private String rdpl$showSpawnPercent(String message) {
        if (!Config.client.loadingScreenPercent || integratedServer == null || message.isEmpty()) { return message; }
        String task = integratedServer.currentTask;
        if (task == null) { return message; }
        return message + " " + integratedServer.percentDone + "%";
    }

    @Inject(method = "processKeyF3", at = @At("HEAD"), cancellable = true)
    private void rdpl$openGameModeSwitcher(int auxKey, CallbackInfoReturnable<Boolean> cir) {
        if (auxKey != Keyboard.KEY_F4) { return; }
        if (player.canUseCommand(2, "")) { displayGuiScreen(new GuiGameModeSwitcher()); }
        else { debugFeedbackTranslated("rdpl.debug.gamemodes.error"); }
        cir.setReturnValue(true);
    }

    @Inject(
            method = "processKeyF3",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiNewChat;printChatMessage(Lnet/minecraft/util/text/ITextComponent;)V", ordinal = 9, shift = At.Shift.AFTER)
    )
    private void rdpl$listGameModeSwitcher(int auxKey, CallbackInfoReturnable<Boolean> cir) { ingameGUI.getChatGUI().printChatMessage(new TextComponentTranslation("rdpl.debug.gamemodes.help")); }
}
