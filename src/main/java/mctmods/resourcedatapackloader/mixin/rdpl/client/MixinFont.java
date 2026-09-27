package mctmods.resourcedatapackloader.mixin.rdpl.client;

import mctmods.resourcedatapackloader.client.FaceGlyph;
import mctmods.resourcedatapackloader.client.GameFont;

import com.mojang.blaze3d.font.GlyphProvider;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GlyphSource;
import net.minecraft.client.gui.font.FontSet;
import net.minecraft.client.gui.font.glyphs.BakedGlyph;
import net.minecraft.network.chat.FontDescription;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import javax.annotation.Nullable;

@Mixin(Font.class) public abstract class MixinFont {
    @Shadow @Final private Font.Provider provider;

    @SuppressWarnings("resource") @Inject(method = "getGlyph(ILnet/minecraft/network/chat/Style;)Lnet/minecraft/client/gui/font/glyphs/BakedGlyph;", at = @At("HEAD"), cancellable = true)
    private void rdpl$styledGlyph(int codepoint, Style style, CallbackInfoReturnable<BakedGlyph> cir) {
        if (style.isObfuscated() && codepoint != 32) { return; }
        Identifier styled = GameFont.styled(style);
        if (styled == null) { return; }
        GlyphSource face = provider.glyphs(new FontDescription.Resource(styled));
        GlyphSource regular = provider.glyphs(style.getFont());
        if (rdpl$from(face, codepoint) == rdpl$from(regular, codepoint)) { return; }
        cir.setReturnValue(new FaceGlyph(face.getGlyph(codepoint), regular.getGlyph(codepoint).info()));
    }

    @SuppressWarnings("resource") @Unique @Nullable private static GlyphProvider rdpl$from(GlyphSource source, int codepoint) {
        if (!(source instanceof FontSet.Source set)) { return null; }
        for (GlyphProvider provider : ((IFontSet) ((IFontSetSource) set).rdpl$fontSet()).rdpl$activeProviders()) {
            if (provider.getGlyph(codepoint) != null) { return provider; }
        }
        return null;
    }
}
