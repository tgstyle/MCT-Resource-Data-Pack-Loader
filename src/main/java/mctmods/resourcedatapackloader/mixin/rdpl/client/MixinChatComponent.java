package mctmods.resourcedatapackloader.mixin.rdpl.client;

import net.minecraft.client.CommandHistory;
import net.minecraft.client.gui.components.ChatComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import java.util.Collection;
import java.util.List;

@Mixin(ChatComponent.class) public abstract class MixinChatComponent {
    @Redirect(method = "addRecentChat", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/CommandHistory;addCommand(Ljava/lang/String;)V"))
    private void rdpl$keepOwnHistory(CommandHistory history, String command) {}

    @Redirect(method = {"<init>", "clearMessages"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/CommandHistory;history()Ljava/util/Collection;"))
    private Collection<String> rdpl$seedFromKeeper(CommandHistory history) { return List.of(); }
}
