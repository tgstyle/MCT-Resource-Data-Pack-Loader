package mctmods.resourcedatapackloader.client;

import mctmods.resourcedatapackloader.util.Marks;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;

final class MarkText {
    private static final int CODE = 0x55FFFF;

    private MarkText() {}

    static Component text(CardFont.Face face, String said) { return text(face, said, 0); }

    static Component text(CardFont.Face face, String said, int marks) {
        if (marks == 0 && Marks.plain(said)) { return literal(face, said); }
        MutableComponent all = Component.empty();
        for (Marks.Run run : Marks.runs(said)) {
            int each = run.marks | marks;
            all.append(Component.literal(run.text).withStyle(style -> styled(style, face, each)));
        }
        return all;
    }

    static Component literal(CardFont.Face face, String said) { return Component.literal(said).withStyle(style -> style.withFont(face.plain())); }

    private static Style styled(Style style, CardFont.Face face, int marks) {
        CardFont.Face used = (marks & Marks.RUNIC) != 0 ? CardFont.runic() : face;
        ResourceLocation font = used.plain();
        Style styled = style;
        boolean bold = (marks & Marks.BOLD) != 0;
        boolean italic = (marks & Marks.ITALIC) != 0;
        if (bold) {
            if (used.bold() == null) { styled = styled.withBold(true); }
            else { font = used.bold(); }
        }
        if (italic) { styled = styled.withItalic(true); }
        if ((marks & Marks.STRIKE) != 0) { styled = styled.withStrikethrough(true); }
        if ((marks & Marks.LINK) != 0) { styled = styled.withUnderlined(true); }
        if ((marks & Marks.CODE) != 0) { styled = styled.withColor(TextColor.fromRgb(CODE)); }
        return styled.withFont(font);
    }
}
