package mctmods.resourcedatapackloader.content.util;

import com.google.gson.JsonObject;
import net.minecraft.util.JsonUtils;
import javax.annotation.Nullable;

public final class TintFactory {
    public static final String FROM = "from";
    public static final String TO = "to";
    private static final int OPAQUE = 0xFF000000;
    private final int from;
    private final int to;

    private TintFactory(int from, int to) {
        this.from = from;
        this.to = to;
    }

    @Nullable public static TintFactory of(JsonObject json) {
        Integer starts = json.has(FROM) ? color(JsonUtils.getString(json, FROM, "")) : Integer.valueOf(OPAQUE);
        Integer ends = color(JsonUtils.getString(json, TO, ""));
        if (starts == null || ends == null) { return null; }

        return new TintFactory(starts, ends);
    }

    public static int opaque(int color) { return color >>> 24 == 0 ? color | OPAQUE : color; }

    @Nullable public static Integer color(String written) {
        String text = written.trim();
        if (text.startsWith("#")) { text = text.substring(1); }
        if (text.startsWith("0x") || text.startsWith("0X")) { text = text.substring(2); }
        if (text.length() != 6 && text.length() != 8) { return null; }

        try {
            long value = Long.parseLong(text, 16);
            return text.length() == 6 ? (int) (value | 0xFF000000L) : (int) value;
        }
        catch (NumberFormatException ex) { return null; }
    }

    public int shade(int color) {
        int level = (((color >> 16) & 0xFF) + ((color >> 8) & 0xFF) + (color & 0xFF)) / 3;
        return (color & 0xFF000000) | (ramp(level, 16) << 16) | (ramp(level, 8) << 8) | ramp(level, 0);
    }

    private int ramp(int level, int shift) {
        int starts = (from >> shift) & 0xFF;
        int ends = (to >> shift) & 0xFF;
        int made = (int) Math.floor(starts + level * (ends - starts) / 255.0 + 0.5);
        return Math.max(0, Math.min(255, made));
    }
}
