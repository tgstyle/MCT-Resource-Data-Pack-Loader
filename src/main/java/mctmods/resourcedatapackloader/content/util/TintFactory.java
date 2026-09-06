package mctmods.resourcedatapackloader.content.util;

import com.google.gson.JsonObject;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
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
        Integer starts = json.has(FROM) ? color(GsonHelper.getAsString(json, FROM, "")) : Integer.valueOf(OPAQUE);
        Integer ends = color(GsonHelper.getAsString(json, TO, ""));
        if (starts == null || ends == null) { return null; }
        return new TintFactory(starts, ends);
    }

    @Nullable public static Integer color(String written) {
        String digits = digits(written);
        if (digits.length() != 6 && digits.length() != 8) { return null; }
        try {
            long value = Long.parseLong(digits, 16);
            return digits.length() == 6 ? (int) (value | 0xFF000000L) : (int) value;
        }
        catch (NumberFormatException ex) { return null; }
    }

    private static String digits(String value) {
        String cleaned = value == null ? "" : value.trim();
        if (cleaned.startsWith("#")) { cleaned = cleaned.substring(1); }
        if (cleaned.startsWith("0x") || cleaned.startsWith("0X")) { cleaned = cleaned.substring(2); }
        return cleaned;
    }

    public int shade(int color) {
        int level = (((color >> 16) & 0xFF) + ((color >> 8) & 0xFF) + (color & 0xFF)) / 3;
        return (color & 0xFF000000) | (ramp(level, 16) << 16) | (ramp(level, 8) << 8) | ramp(level, 0);
    }

    private int ramp(int level, int shift) {
        int starts = (from >> shift) & 0xFF;
        int ends = (to >> shift) & 0xFF;
        int made = (int) Math.floor(starts + level * (ends - starts) / 255.0 + 0.5);
        return Mth.clamp(made, 0, 255);
    }
}
