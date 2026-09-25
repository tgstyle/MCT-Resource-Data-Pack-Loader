package mctmods.resourcedatapackloader.client;

import mctmods.resourcedatapackloader.content.ContentParser;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.MarkLines;
import mctmods.resourcedatapackloader.util.Marks;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;

final class MarkPage {
    private static final int LINE = 12;
    private static final int INDENT = 12;
    private static final int GAP = 4;
    private static final int TEXT = 0xFFFFFF;
    private static final int DIM = 0xAAAAAA;
    private static final int RULE = 0xFF888888;
    private static final float[] HEADINGS = {2.0F, 1.5F, 1.25F};
    private final List<Row> rows = new ArrayList<>();
    private final Font font;
    private final CardFont.Face face;
    private final int widest;
    private float height;

    private static final class Row {
        final FormattedCharSequence line;
        final FormattedCharSequence lead;
        final int indent;
        final int shift;
        final float size;
        final int color;
        final float height;
        int span;
        @Nullable ResourceLocation image;
        int imageWidth;
        int imageHeight;
        boolean rule;

        Row(Font font, FormattedCharSequence line, FormattedCharSequence lead, int indent, int shift, float size, int color, float height) {
            this.line = line;
            this.lead = lead;
            this.indent = indent;
            this.shift = shift;
            this.size = size;
            this.color = color;
            this.height = height;
            this.span = Math.round((indent + shift + font.width(line)) * size);
        }
    }

    MarkPage(Font font, List<String> written, int widest) {
        this.font = font;
        this.face = CardFont.of("");
        this.widest = widest;
        for (MarkLines.Line line : MarkLines.parse(written)) { lay(line); }
        for (Row row : rows) { height += row.height; }
    }

    float height() { return height; }

    float last() { return rows.isEmpty() ? LINE : rows.get(rows.size() - 1).height; }

    private void lay(MarkLines.Line line) {
        switch (line.kind) {
            case MarkLines.PLAIN -> block(MarkText.literal(face, line.text), null, 0, 1.0F, TEXT);
            case MarkLines.HEADING -> block(MarkText.text(face, line.text, Marks.BOLD), null, 0, Crisp.scale(HEADINGS[line.level - 1]), TEXT);
            case MarkLines.ITEM -> block(MarkText.text(face, line.text), MarkText.literal(face, line.lead + " "), (line.level + 1) * INDENT, 1.0F, TEXT);
            case MarkLines.QUOTE -> block(MarkText.text(face, line.text), null, INDENT, 1.0F, DIM);
            case MarkLines.RULE -> {
                Row rule = new Row(font, FormattedCharSequence.EMPTY, FormattedCharSequence.EMPTY, 0, 0, 1.0F, TEXT, LINE);
                rule.rule = true;
                rule.span = widest;
                rows.add(rule);
            }
            case MarkLines.IMAGE -> {
                if (!image(line)) { block(MarkText.literal(face, line.text), null, 0, 1.0F, TEXT); }
            }
            default -> block(MarkText.text(face, line.text), null, 0, 1.0F, TEXT);
        }
    }

    private void block(Component text, @Nullable Component lead, int indent, float size, int color) {
        if (text.getString().isEmpty()) {
            rows.add(new Row(font, FormattedCharSequence.EMPTY, FormattedCharSequence.EMPTY, 0, 0, 1.0F, color, LINE));
            return;
        }
        int shift = lead == null ? 0 : font.width(lead);
        int room = Math.max(1, (int) ((widest - (indent + shift) * size) / size));
        int first = rows.size();
        FormattedCharSequence leading = lead == null ? FormattedCharSequence.EMPTY : lead.getVisualOrderText();
        for (FormattedCharSequence wrapped : font.split(text, room)) {
            rows.add(new Row(font, wrapped, leading, indent, shift, size, color, LINE * size));
            leading = FormattedCharSequence.EMPTY;
        }
        if (indent == 0 && shift == 0) { return; }
        int span = 0;
        for (int i = first; i < rows.size(); i++) { span = Math.max(span, rows.get(i).span); }
        for (int i = first; i < rows.size(); i++) { rows.get(i).span = span; }
    }

    private boolean image(MarkLines.Line line) {
        ResourceLocation where = ContentParser.location(line.lead);
        if (where == null) { return false; }
        int width;
        int tall;
        try (InputStream stream = Minecraft.getInstance().getResourceManager().open(where); NativeImage read = NativeImage.read(stream)) {
            width = read.getWidth();
            tall = read.getHeight();
        }
        catch (IOException | RuntimeException e) {
            ContentLog.LOGGER.error("World intro image {} could not be read, so its alt text shows instead: {}", where, e.getMessage());
            return false;
        }
        int shown = Math.min(width, widest);
        int shownHeight = Math.max(1, Math.round(tall * shown / (float) width));
        Row row = new Row(font, FormattedCharSequence.EMPTY, FormattedCharSequence.EMPTY, 0, 0, 1.0F, TEXT, shownHeight + GAP);
        row.image = where;
        row.imageWidth = shown;
        row.imageHeight = shownHeight;
        row.span = shown;
        rows.add(row);
        return true;
    }

    void draw(GuiGraphics graphics, int screenWidth, int screenHeight, float top, float scale, boolean centered) {
        float y = top;
        float block = (screenWidth - widest * scale) / 2.0F;
        for (Row row : rows) {
            float tall = row.height * scale;
            if (y > -tall && y < screenHeight) {
                float left = centered ? (screenWidth - row.span * scale) / 2.0F : block;
                if (row.image != null) { picture(graphics, row, left, y, scale); }
                else if (row.rule) { rule(graphics, row, left, y, scale); }
                else { text(graphics, row, left, y, scale); }
            }
            y += tall;
        }
    }

    private void text(GuiGraphics graphics, Row row, float left, float y, float scale) {
        float size = scale * row.size;
        graphics.pose().pushPose();
        graphics.pose().scale(size, size, 1.0F);
        float x = Crisp.snap(left + row.indent * scale) / size;
        float at = Crisp.snap(y) / size;
        graphics.drawString(font, row.lead, x, at, 0xFF000000 | row.color, true);
        graphics.drawString(font, row.line, x + row.shift, at, 0xFF000000 | row.color, true);
        graphics.pose().popPose();
    }

    private void rule(GuiGraphics graphics, Row row, float left, float y, float scale) {
        graphics.pose().pushPose();
        graphics.pose().scale(scale, scale, 1.0F);
        int x = Math.round(left / scale);
        int middle = Math.round(y / scale) + LINE / 2;
        graphics.fill(x, middle - 1, x + row.span, middle, RULE);
        graphics.pose().popPose();
    }

    private void picture(GuiGraphics graphics, Row row, float left, float y, float scale) {
        if (row.image == null) { return; }
        graphics.pose().pushPose();
        graphics.pose().scale(scale, scale, 1.0F);
        RenderSystem.enableBlend();
        graphics.blit(row.image, Math.round(left / scale), Math.round(y / scale), 0, 0.0F, 0.0F, row.imageWidth, row.imageHeight, row.imageWidth, row.imageHeight);
        RenderSystem.disableBlend();
        graphics.pose().popPose();
    }
}
