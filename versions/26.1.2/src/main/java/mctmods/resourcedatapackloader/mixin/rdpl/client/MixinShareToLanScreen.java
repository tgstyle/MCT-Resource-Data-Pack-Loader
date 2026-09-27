package mctmods.resourcedatapackloader.mixin.rdpl.client;

import mctmods.resourcedatapackloader.content.ContentServer;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.ShareToLanScreen;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ShareToLanScreen.class) public abstract class MixinShareToLanScreen {
    @Shadow private boolean commands;

    @Inject(method = "init", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/screens/ShareToLanScreen;commands:Z", opcode = Opcodes.PUTFIELD, shift = At.Shift.AFTER)) private void rdpl$holdCommandsOff(CallbackInfo ci) { commands &= ContentServer.lanCommands(); }

    @ModifyArg(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/ShareToLanScreen;addRenderableWidget(Lnet/minecraft/client/gui/components/events/GuiEventListener;)Lnet/minecraft/client/gui/components/events/GuiEventListener;", ordinal = 1)) private GuiEventListener rdpl$lockCommands(GuiEventListener widget) {
        if (widget instanceof AbstractWidget button) { button.active &= ContentServer.lanCommands(); }
        return widget;
    }
}
