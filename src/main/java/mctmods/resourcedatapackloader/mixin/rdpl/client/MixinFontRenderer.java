package mctmods.resourcedatapackloader.mixin.rdpl.client;

import mctmods.resourcedatapackloader.client.GameFont;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import javax.annotation.Nullable;

@Mixin(FontRenderer.class) public abstract class MixinFontRenderer {
    @Shadow @Final protected ResourceLocation locationFontTexture;
    @Shadow @Final protected int[] charWidth;
    @Shadow protected float posX;
    @Shadow protected float posY;
    @Shadow private boolean unicodeFlag;
    @Shadow private boolean randomStyle;
    @Shadow private boolean boldStyle;
    @Shadow private boolean italicStyle;
    @Unique private ResourceLocation rdpl$sheet;
    @Unique private GameFont.Page[] rdpl$pages;
    @Unique private GameFont.Face rdpl$bold;
    @Unique private GameFont.Face rdpl$italic;
    @Unique private GameFont.Face rdpl$own;
    @Unique private GameFont.Face rdpl$fill;
    @Unique private GameFont.Face rdpl$fillItalic;
    @Unique private boolean rdpl$drewBold;

    @Shadow(remap = false) protected abstract void bindTexture(ResourceLocation location);

    @Inject(method = "onResourceManagerReload", at = @At("HEAD"))
    private void rdpl$pickSheets(IResourceManager resourceManager, CallbackInfo info) {
        rdpl$sheet = GameFont.sheet(resourceManager, locationFontTexture);
        ResourceLocation base = GameFont.base(rdpl$sheet);
        boolean cipher = !base.equals(rdpl$sheet);
        rdpl$pages = GameFont.pages(resourceManager, base);
        rdpl$bold = GameFont.styled(resourceManager, rdpl$sheet, "_bold");
        rdpl$italic = GameFont.styled(resourceManager, rdpl$sheet, "_italic");
        rdpl$own = cipher ? GameFont.face(resourceManager, rdpl$sheet, "") : null;
        rdpl$fill = cipher ? GameFont.face(resourceManager, base, "") : null;
        rdpl$fillItalic = cipher ? GameFont.styled(resourceManager, base, "_italic") : null;
    }

    @Redirect(method = {"readFontTexture", "renderDefaultChar"}, at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/FontRenderer;locationFontTexture:Lnet/minecraft/util/ResourceLocation;", opcode = Opcodes.GETFIELD))
    private ResourceLocation rdpl$drawnSheet(FontRenderer self) { return rdpl$sheet == null ? locationFontTexture : rdpl$sheet; }

    @Inject(method = "readFontTexture", at = @At("TAIL"))
    private void rdpl$fillWidths(CallbackInfo info) {
        if (rdpl$own == null || rdpl$fill == null) { return; }
        for (int cell = 0; cell < GameFont.CELLS; cell++) {
            if (cell != ' ' && rdpl$own.width(cell) == 0 && rdpl$fill.width(cell) > 0) { charWidth[cell] = rdpl$fill.width(cell); }
        }
    }

    @Inject(method = "renderChar", at = @At("HEAD"), cancellable = true)
    private void rdpl$drawPage(char ch, boolean italic, CallbackInfoReturnable<Float> info) {
        if (rdpl$drewBold) {
            rdpl$drewBold = false;
            info.setReturnValue(0.0F);
            return;
        }
        GameFont.Page page = rdpl$page(ch);
        if (page == null || page.plain == null) { return; }
        int cell = ch - page.first;
        int width = page.plain.width(cell);
        GameFont.Face face = rdpl$styled(page.bold, page.italic, cell);
        if (face == null) { rdpl$quad(page.plain.sheet, cell, width, italic); }
        else { rdpl$quad(face.sheet, cell, face.width(cell), italic && face == page.bold); }
        info.setReturnValue((float) width);
    }

    @Inject(method = "renderDefaultChar", at = @At("HEAD"), cancellable = true)
    private void rdpl$drawStyled(int ch, boolean italic, CallbackInfoReturnable<Float> info) {
        GameFont.Face face = rdpl$styled(rdpl$bold, rdpl$italic, ch);
        if (face != null) {
            rdpl$quad(face.sheet, ch, face.width(ch), italic && face == rdpl$bold);
            info.setReturnValue((float) charWidth[ch]);
            return;
        }
        if (rdpl$own == null || rdpl$fill == null || rdpl$own.width(ch) > 0 || rdpl$fill.width(ch) == 0) { return; }
        boolean slanted = italicStyle && rdpl$fillItalic != null && rdpl$fillItalic.width(ch) > 0;
        GameFont.Face fill = slanted ? rdpl$fillItalic : rdpl$fill;
        rdpl$quad(fill.sheet, ch, fill.width(ch), italic && !slanted);
        info.setReturnValue((float) charWidth[ch]);
    }

    @Inject(method = "getCharWidth", at = @At("HEAD"), cancellable = true)
    private void rdpl$pageWidth(char character, CallbackInfoReturnable<Integer> info) {
        int width = rdpl$width(character);
        if (width > 0) { info.setReturnValue(width); }
    }

    @Redirect(method = "renderStringAtPos", at = @At(value = "INVOKE", target = "Ljava/lang/String;indexOf(I)I"))
    private int rdpl$sheetIndex(String chars, int ch) {
        int at = chars.indexOf(ch);
        return at < 0 && chars.length() > GameFont.CELLS / 2 && !randomStyle && rdpl$width(ch) > 0 ? 0 : at;
    }

    @Unique @Nullable private GameFont.Face rdpl$styled(@Nullable GameFont.Face bold, @Nullable GameFont.Face italic, int cell) {
        GameFont.Face face = boldStyle ? bold : italicStyle ? italic : null;
        if (face == null || face.width(cell) == 0) { return null; }
        rdpl$drewBold = face == bold;
        return face;
    }

    @Unique private void rdpl$quad(ResourceLocation sheet, int cell, int width, boolean italic) {
        float u = (float) (cell % 16 * 8);
        float v = (float) (cell / 16 * 8);
        float slant = italic ? 1.0F : 0.0F;
        float wide = width - 0.01F;
        bindTexture(sheet);
        GlStateManager.glBegin(5);
        GlStateManager.glTexCoord2f(u / 128.0F, v / 128.0F);
        GlStateManager.glVertex3f(posX + slant, posY, 0.0F);
        GlStateManager.glTexCoord2f(u / 128.0F, (v + 7.99F) / 128.0F);
        GlStateManager.glVertex3f(posX - slant, posY + 7.99F, 0.0F);
        GlStateManager.glTexCoord2f((u + wide - 1.0F) / 128.0F, v / 128.0F);
        GlStateManager.glVertex3f(posX + wide - 1.0F + slant, posY, 0.0F);
        GlStateManager.glTexCoord2f((u + wide - 1.0F) / 128.0F, (v + 7.99F) / 128.0F);
        GlStateManager.glVertex3f(posX + wide - 1.0F - slant, posY + 7.99F, 0.0F);
        GlStateManager.glEnd();
    }

    @Unique @Nullable private GameFont.Page rdpl$page(int ch) {
        if (unicodeFlag || rdpl$pages == null) { return null; }
        for (GameFont.Page page : rdpl$pages) {
            if (page.width(ch) > 0) { return page; }
        }
        return null;
    }

    @Unique private int rdpl$width(int ch) {
        GameFont.Page page = rdpl$page(ch);
        return page == null ? 0 : page.width(ch);
    }
}
