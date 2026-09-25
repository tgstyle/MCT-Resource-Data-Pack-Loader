package mctmods.resourcedatapackloader.client;

import mctmods.resourcedatapackloader.util.Marks;

import net.minecraft.client.gui.FontRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SideOnly(Side.CLIENT) final class MarkText {
    static final int CODE = 0x55FFFF;

    private MarkText() {}

    static final class Piece {
        final FontRenderer face;
        final String text;
        final String codes;
        final boolean code;
        final int width;

        Piece(FontRenderer face, String codes, String text, boolean code) {
            this.face = face;
            this.codes = codes;
            this.text = text;
            this.code = code;
            this.width = face.getStringWidth(codes + text);
        }

        Piece with(String part) { return new Piece(face, codes, part, code); }
    }

    static List<Piece> pieces(String font, String said) { return pieces(font, said, 0); }

    static List<Piece> pieces(String font, String said, int marks) {
        if (marks == 0 && Marks.plain(said)) { return Collections.singletonList(new Piece(CardFont.of(font), "", said, false)); }
        List<Piece> pieces = new ArrayList<>();
        for (Marks.Run run : Marks.runs(said)) { pieces.add(piece(font, run.text, run.marks | marks)); }
        return pieces;
    }

    static List<Piece> literal(String font, String said) { return Collections.singletonList(new Piece(CardFont.of(font), "", said, false)); }

    private static Piece piece(String font, String text, int marks) {
        String used = (marks & Marks.RUNIC) != 0 ? CardFont.RUNIC : font;
        FontRenderer face = CardFont.of(used);
        StringBuilder codes = new StringBuilder();
        boolean bold = (marks & Marks.BOLD) != 0;
        boolean italic = (marks & Marks.ITALIC) != 0;
        if (bold) {
            FontRenderer heavy = CardFont.bold(used);
            if (heavy == null) { codes.append("§l"); }
            else { face = heavy; }
        }
        if (italic) { codes.append("§o"); }
        if ((marks & Marks.STRIKE) != 0) { codes.append("§m"); }
        if ((marks & Marks.LINK) != 0) { codes.append("§n"); }
        return new Piece(face, codes.toString(), text, (marks & Marks.CODE) != 0);
    }

    static int width(List<Piece> pieces) {
        int width = 0;
        for (Piece piece : pieces) { width += piece.width; }
        return width;
    }

    static int width(String font, String said) { return width(pieces(font, said)); }

    static void draw(List<Piece> pieces, float x, float y, int color) {
        float at = x;
        for (Piece piece : pieces) {
            piece.face.drawStringWithShadow(piece.codes + piece.text, at, y, piece.code ? color & 0xFF000000 | CODE : color);
            at += piece.width;
        }
    }

    static void draw(String font, String said, float x, float y, int color) { draw(pieces(font, said), x, y, color); }

    static List<List<Piece>> wrap(List<Piece> pieces, int widest) {
        List<List<Piece>> lines = new ArrayList<>();
        if (pieces.size() == 1 && pieces.get(0).codes.isEmpty() && !pieces.get(0).code) {
            Piece only = pieces.get(0);
            for (String part : only.face.listFormattedStringToWidth(only.text, widest)) { lines.add(Collections.singletonList(only.with(part))); }
            return lines;
        }
        List<Piece> line = new ArrayList<>();
        int used = 0;
        for (Piece piece : pieces) {
            String left = piece.text;
            while (!left.isEmpty()) {
                String part = fit(piece, left, widest - used, line.isEmpty());
                if (part.isEmpty()) {
                    lines.add(line);
                    line = new ArrayList<>();
                    used = 0;
                    left = left.replaceFirst("^ +", "");
                    continue;
                }
                Piece placed = piece.with(part);
                line.add(placed);
                used += placed.width;
                left = left.substring(part.length());
                if (!left.isEmpty()) {
                    lines.add(line);
                    line = new ArrayList<>();
                    used = 0;
                    left = FontRenderer.getFormatFromString(part) + left.replaceFirst("^ +", "");
                }
            }
        }
        if (!line.isEmpty() || lines.isEmpty()) { lines.add(line); }
        return lines;
    }

    private static String fit(Piece piece, String text, int room, boolean fresh) {
        String all = piece.codes + text;
        if (piece.face.getStringWidth(all) <= room) { return text; }
        String trimmed = piece.face.trimStringToWidth(all, Math.max(0, room));
        String most = trimmed.length() <= piece.codes.length() ? "" : trimmed.substring(piece.codes.length());
        int space = most.lastIndexOf(' ');
        if (space > 0) { return text.substring(0, space); }
        if (!fresh) { return ""; }
        return most.isEmpty() ? text.substring(0, 1) : most;
    }
}
