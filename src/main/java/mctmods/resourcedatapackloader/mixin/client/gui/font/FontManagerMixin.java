package mctmods.resourcedatapackloader.mixin.client.gui.font;

import mctmods.resourcedatapackloader.client.util.GameFont;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.client.gui.font.FontManager;
import net.minecraft.client.gui.font.providers.GlyphProviderDefinition;
import net.minecraft.server.packs.resources.ResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FontManager.class) public abstract class FontManagerMixin {
    @Inject(method = "prepare", at = @At("HEAD")) private void rdpl$pickGameFont(ResourceManager resourceManager, Executor executor, CallbackInfoReturnable<CompletableFuture<?>> cir) { GameFont.pick(resourceManager); }

    @ModifyVariable(method = "loadResourceStack", at = @At("STORE"), ordinal = 2) @SuppressWarnings("ModifyVariableMayUseName") private static List<GlyphProviderDefinition.Conditional> rdpl$yieldToPackFont(List<GlyphProviderDefinition.Conditional> list1) { return GameFont.providers(list1); }
}
