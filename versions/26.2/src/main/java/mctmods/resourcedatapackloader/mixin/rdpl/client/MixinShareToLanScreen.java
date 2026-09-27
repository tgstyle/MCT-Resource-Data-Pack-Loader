package mctmods.resourcedatapackloader.mixin.rdpl.client;

import mctmods.resourcedatapackloader.content.ContentServer;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.screens.MultiplayerOptionsScreen;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MultiplayerOptionsScreen.class) public abstract class MixinShareToLanScreen {
    @Shadow private boolean commands;

    @Inject(method = "init()V", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/screens/MultiplayerOptionsScreen;commands:Z", opcode = Opcodes.PUTFIELD, shift = At.Shift.AFTER)) private void rdpl$holdCommandsOff(CallbackInfo ci) { commands &= ContentServer.lanCommands(); }

    @ModifyArg(method = "init()V", slice = @Slice(from = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/screens/MultiplayerOptionsScreen;initialCommands:Z", opcode = Opcodes.PUTFIELD)), at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/layouts/LinearLayout;addChild(Lnet/minecraft/client/gui/layouts/LayoutElement;)Lnet/minecraft/client/gui/layouts/LayoutElement;", ordinal = 0)) private LayoutElement rdpl$lockCommands(LayoutElement widget) {
        if (widget instanceof AbstractWidget button) { button.active &= ContentServer.lanCommands(); }
        return widget;
    }
}
