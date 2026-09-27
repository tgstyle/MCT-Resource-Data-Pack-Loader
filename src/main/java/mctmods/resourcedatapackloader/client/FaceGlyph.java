package mctmods.resourcedatapackloader.client;

import com.mojang.blaze3d.font.GlyphInfo;
import net.minecraft.client.gui.font.TextRenderable;
import net.minecraft.client.gui.font.glyphs.BakedGlyph;
import net.minecraft.network.chat.Style;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public record FaceGlyph(BakedGlyph face, @Nonnull GlyphInfo info) implements BakedGlyph {
    @Override @Nullable public TextRenderable.Styled createGlyph(float x, float y, int color, int shadowColor, @Nonnull Style style, float boldOffset, float shadowOffset) {
        return face.createGlyph(x, y, color, shadowColor, style.isBold() ? style.withBold(false) : style.withItalic(false), boldOffset, shadowOffset);
    }
}
