package mctmods.resourcedatapackloader.content.util;

import mctmods.resourcedatapackloader.content.types.ContentTypes;

import com.google.gson.JsonObject;
import net.minecraft.util.math.MathHelper;
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
        int digits = ContentTypes.hexDigits(written);
        if (digits != 6 && digits != 8) { return null; }
        Long value = ContentTypes.hex(written);
        if (value == null) { return null; }
        return digits == 6 ? (int) (value | 0xFF000000L) : value.intValue();
    }

    public int shade(int color) {
        int level = (((color >> 16) & 0xFF) + ((color >> 8) & 0xFF) + (color & 0xFF)) / 3;
        return (color & 0xFF000000) | (ramp(level, 16) << 16) | (ramp(level, 8) << 8) | ramp(level, 0);
    }

    private int ramp(int level, int shift) {
        int starts = (from >> shift) & 0xFF;
        int ends = (to >> shift) & 0xFF;
        int made = (int) Math.floor(starts + level * (ends - starts) / 255.0 + 0.5);
        return MathHelper.clamp(made, 0, 255);
    }
}
