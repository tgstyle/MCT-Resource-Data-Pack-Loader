package mctmods.resourcedatapackloader.client;

import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.MarkLines;
import mctmods.resourcedatapackloader.util.Marks;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResource;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;

@SideOnly(Side.CLIENT) final class MarkPage {
    private static final int LINE = 12;
    private static final int INDENT = 12;
    private static final int GAP = 4;
    private static final int TEXT = 0xFFFFFF;
    private static final int DIM = 0xAAAAAA;
    private static final int RULE = 0xFF888888;
    private static final float[] HEADINGS = {2.0F, 1.5F, 1.25F};
    private final List<Row> rows = new ArrayList<>();
    private final String font;
    private final int widest;
    private float height;

    private static final class Row {
        final List<MarkText.Piece> pieces;
        final List<MarkText.Piece> lead;
        final int indent;
        final int shift;
        final float size;
        final int color;
        final float height;
        int span;
        @Nullable ResourceLocation image;
        int imageWidth;
        boolean rule;

        Row(List<MarkText.Piece> pieces, List<MarkText.Piece> lead, int indent, int shift, float size, int color, float height) {
            this.pieces = pieces;
            this.lead = lead;
            this.indent = indent;
            this.shift = shift;
            this.size = size;
            this.color = color;
            this.height = height;
            this.span = Math.round((indent + shift + MarkText.width(pieces)) * size);
        }
    }

    MarkPage(String font, List<String> written, int widest) {
        this.font = font;
        this.widest = widest;
        for (MarkLines.Line line : MarkLines.parse(written)) { lay(line); }
        for (Row row : rows) { height += row.height; }
    }

    float height() { return height; }

    float last() { return rows.isEmpty() ? LINE : rows.get(rows.size() - 1).height; }

    private void lay(MarkLines.Line line) {
        switch (line.kind) {
            case MarkLines.PLAIN:
                block(MarkText.literal(font, line.text), Collections.emptyList(), 0, 1.0F, TEXT);
                return;
            case MarkLines.HEADING:
                block(MarkText.pieces(font, line.text, Marks.BOLD), Collections.emptyList(), 0, Crisp.scale(HEADINGS[line.level - 1]), TEXT);
                return;
            case MarkLines.ITEM:
                block(MarkText.pieces(font, line.text), MarkText.literal(font, line.lead + " "), (line.level + 1) * INDENT, 1.0F, TEXT);
                return;
            case MarkLines.QUOTE:
                block(MarkText.pieces(font, line.text), Collections.emptyList(), INDENT, 1.0F, DIM);
                return;
            case MarkLines.RULE:
                Row rule = new Row(Collections.emptyList(), Collections.emptyList(), 0, 0, 1.0F, TEXT, LINE);
                rule.rule = true;
                rule.span = widest;
                rows.add(rule);
                return;
            case MarkLines.IMAGE:
                if (image(line)) { return; }
                block(MarkText.literal(font, line.text), Collections.emptyList(), 0, 1.0F, TEXT);
                return;
            default:
                block(MarkText.pieces(font, line.text), Collections.emptyList(), 0, 1.0F, TEXT);
        }
    }

    private void block(List<MarkText.Piece> pieces, List<MarkText.Piece> lead, int indent, float size, int color) {
        if (pieces.size() == 1 && pieces.get(0).text.isEmpty()) {
            rows.add(new Row(Collections.emptyList(), Collections.emptyList(), 0, 0, 1.0F, color, LINE));
            return;
        }
        int shift = MarkText.width(lead);
        int room = Math.max(1, (int) ((widest - (indent + shift) * size) / size));
        int first = rows.size();
        boolean leading = true;
        for (List<MarkText.Piece> wrapped : MarkText.wrap(pieces, room)) {
            rows.add(new Row(wrapped, leading ? lead : Collections.emptyList(), indent, shift, size, color, LINE * size));
            leading = false;
        }
        if (indent == 0 && shift == 0) { return; }
        int span = 0;
        for (int i = first; i < rows.size(); i++) { span = Math.max(span, rows.get(i).span); }
        for (int i = first; i < rows.size(); i++) { rows.get(i).span = span; }
    }

    private boolean image(MarkLines.Line line) {
        ResourceLocation where = new ResourceLocation(line.lead);
        int width;
        int tall;
        try (IResource resource = Minecraft.getMinecraft().getResourceManager().getResource(where)) {
            BufferedImage read = TextureUtil.readBufferedImage(resource.getInputStream());
            width = read.getWidth();
            tall = read.getHeight();
        }
        catch (IOException | RuntimeException e) {
            ContentLog.LOGGER.error("World intro image {} could not be read, so its alt text shows instead: {}", where, e.getMessage());
            return false;
        }
        int shown = Math.min(width, widest);
        Row row = new Row(Collections.emptyList(), Collections.emptyList(), 0, 0, 1.0F, TEXT, Math.max(1, Math.round(tall * shown / (float) width)) + GAP);
        row.image = where;
        row.imageWidth = shown;
        row.span = shown;
        rows.add(row);
        return true;
    }

    void draw(int screenWidth, int screenHeight, float top, float scale, boolean centered) {
        float y = top;
        float block = (screenWidth - widest * scale) / 2.0F;
        for (Row row : rows) {
            float tall = row.height * scale;
            if (y > -tall && y < screenHeight) {
                float left = centered ? (screenWidth - row.span * scale) / 2.0F : block;
                if (row.image != null) { picture(row, left, y, scale); }
                else if (row.rule) { rule(row, left, y, scale); }
                else { text(row, left, y, scale); }
            }
            y += tall;
        }
    }

    private void text(Row row, float left, float y, float scale) {
        float size = scale * row.size;
        GlStateManager.pushMatrix();
        GlStateManager.scale(size, size, 1.0F);
        float x = Crisp.fine(left + row.indent * scale, size) / size;
        float at = Crisp.fine(y, size) / size;
        MarkText.draw(row.lead, x, at, 0xFF000000 | row.color);
        MarkText.draw(row.pieces, x + row.shift, at, 0xFF000000 | row.color);
        GlStateManager.popMatrix();
    }

    private void rule(Row row, float left, float y, float scale) {
        GlStateManager.pushMatrix();
        GlStateManager.scale(scale, scale, 1.0F);
        int x = Math.round(left / scale);
        int middle = Math.round(y / scale) + LINE / 2;
        Gui.drawRect(x, middle - 1, x + row.span, middle, RULE);
        GlStateManager.enableBlend();
        GlStateManager.popMatrix();
    }

    private void picture(Row row, float left, float y, float scale) {
        if (row.image == null) { return; }
        GlStateManager.pushMatrix();
        GlStateManager.scale(scale, scale, 1.0F);
        GlStateManager.enableBlend();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        Minecraft.getMinecraft().getTextureManager().bindTexture(row.image);
        int shownHeight = Math.round(row.height) - GAP;
        Gui.drawModalRectWithCustomSizedTexture(Math.round(left / scale), Math.round(y / scale), 0.0F, 0.0F, row.imageWidth, shownHeight, row.imageWidth, shownHeight);
        GlStateManager.popMatrix();
    }
}
