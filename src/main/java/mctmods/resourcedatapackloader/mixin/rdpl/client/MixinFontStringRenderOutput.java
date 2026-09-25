package mctmods.resourcedatapackloader.mixin.rdpl.client;

import mctmods.resourcedatapackloader.client.GameFont;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.font.FontSet;
import net.minecraft.client.gui.font.glyphs.BakedGlyph;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.minecraft.client.gui.Font$StringRenderOutput") public abstract class MixinFontStringRenderOutput {
    @Unique private Style rdpl$style = Style.EMPTY;
    @Unique private ResourceLocation rdpl$drawn;

    @Inject(method = "accept", at = @At("HEAD"))
    private void rdpl$takeStyle(int positionInCurrentSequence, Style style, int codePoint, CallbackInfoReturnable<Boolean> info) {
        rdpl$style = style;
        rdpl$drawn = null;
    }

    @Redirect(method = "accept", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/font/FontSet;getGlyph(I)Lnet/minecraft/client/gui/font/glyphs/BakedGlyph;"))
    private BakedGlyph rdpl$styledGlyph(FontSet regular, int codePoint) {
        ResourceLocation styled = GameFont.styled(rdpl$style);
        return styled == null ? regular.getGlyph(codePoint) : rdpl$glyph(((IFont) Minecraft.getInstance().font).rdpl$fontSet(styled), regular, codePoint, styled);
    }

    @ModifyArg(method = "accept", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Font;renderChar(Lnet/minecraft/client/gui/font/glyphs/BakedGlyph;ZZFFFLorg/joml/Matrix4f;Lcom/mojang/blaze3d/vertex/VertexConsumer;FFFFI)V"), index = 1)
    private boolean rdpl$syntheticBold(boolean bold) { return bold && rdpl$drawn == null; }

    @ModifyArg(method = "accept", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Font;renderChar(Lnet/minecraft/client/gui/font/glyphs/BakedGlyph;ZZFFFLorg/joml/Matrix4f;Lcom/mojang/blaze3d/vertex/VertexConsumer;FFFFI)V"), index = 2)
    private boolean rdpl$syntheticItalic(boolean italic) { return italic && (rdpl$drawn == null || rdpl$style.isBold()); }

    @Unique private BakedGlyph rdpl$glyph(FontSet face, FontSet regular, int codePoint, ResourceLocation styled) {
        if (face.getGlyphInfo(codePoint, false) == regular.getGlyphInfo(codePoint, false)) { return regular.getGlyph(codePoint); }
        rdpl$drawn = styled;
        return face.getGlyph(codePoint);
    }
}
