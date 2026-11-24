package mctmods.resourcedatapackloader.common.tints;

import mctmods.resourcedatapackloader.common.types.ContentTypes;
import mctmods.resourcedatapackloader.core.util.json.Json;
import mctmods.resourcedatapackloader.core.util.math.Colors;

import com.google.gson.JsonObject;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.util.math.MathHelper;

public final class TintFactory {
    public static final String FROM = "from";
    public static final String TO = "to";
    private static final List<String> TINT_KEYS = Collections.unmodifiableList(Arrays.asList(FROM, TO));
    private static final int OPAQUE = 0xFF000000;
    private final int from;
    private final int to;

    private TintFactory(int from, int to) {
        this.from = from;
        this.to = to;
    }

    @Nullable public static TintFactory of(JsonObject json) {
        Json.unknownKeys(json, "tint", TINT_KEYS);
        Integer starts = json.has(FROM) ? color(Json.getString(json, FROM, "")) : Integer.valueOf(OPAQUE);
        Integer ends = color(Json.getString(json, TO, ""));
        if (starts == null || ends == null) { return null; }
        return new TintFactory(starts, ends);
    }

    public static int opaque(int color) { return color >>> 24 == 0 ? color | OPAQUE : color; }

    @Nullable public static Integer color(String written) {
        int digits = ContentTypes.hexDigits(written);
        if (digits != 6 && digits != 8) { return null; }
        Long value = ContentTypes.hex(written);
        if (value == null) { return null; }
        return digits == 6 ? opaque(value.intValue()) : value.intValue();
    }

    public int shade(int color) {
        int level = (Colors.red(color) + Colors.green(color) + Colors.blue(color)) / 3;
        return (color & OPAQUE) | Colors.rgb(ramp(level, Colors.red(from), Colors.red(to)), ramp(level, Colors.green(from), Colors.green(to)), ramp(level, Colors.blue(from), Colors.blue(to)));
    }

    private static int ramp(int level, int starts, int ends) {
        int made = (int) Math.floor(starts + level * (ends - starts) / 255.0 + 0.5);
        return MathHelper.clamp(made, 0, 255);
    }
}
