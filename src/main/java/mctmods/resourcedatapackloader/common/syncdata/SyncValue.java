package mctmods.resourcedatapackloader.common.syncdata;

import mctmods.resourcedatapackloader.common.tooltips.TooltipText;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

public final class SyncValue {
    public static final int TEXT = 1;
    public static final int NUMBER = 2;
    public static final int BOOL = 3;
    public static final List<String> KIND_NAMES = List.of("text", "number", "bool");
    public final int kind;
    public final String text;
    public final double number;
    public final boolean flag;
    public final String display;

    private SyncValue(int kind, String text, double number, boolean flag, String display) {
        this.kind = kind;
        this.text = text;
        this.number = number;
        this.flag = flag;
        this.display = display;
    }

    public static SyncValue text(String text) { return new SyncValue(TEXT, text, 0.0D, false, text); }

    public static SyncValue number(double number) { return new SyncValue(NUMBER, "", number, false, TooltipText.number(number)); }

    public static SyncValue bool(boolean flag) { return new SyncValue(BOOL, "", 0.0D, flag, String.valueOf(flag)); }

    @Nullable public static SyncValue of(Object value) {
        if (value instanceof String string) { return text(string); }
        if (value instanceof Number number) { return number(number.doubleValue()); }
        if (value instanceof Boolean flag) { return bool(flag); }
        return null;
    }

    @Nullable public static Object plain(JsonElement element) {
        if (!element.isJsonPrimitive()) { return null; }
        JsonPrimitive primitive = element.getAsJsonPrimitive();
        if (primitive.isNumber()) { return primitive.getAsDouble(); }
        return primitive.isBoolean() ? (Object) primitive.getAsBoolean() : primitive.getAsString();
    }

    public static String kindName(int kind) { return KIND_NAMES.get(kind == NUMBER || kind == BOOL ? kind - 1 : 0); }

    public static int kindOf(String type) { return KIND_NAMES.indexOf(type) + 1; }

    @Nullable public static SyncValue fromNbt(CompoundTag tag, String key) {
        if (!tag.contains(key)) { return null; }
        Tag value = tag.get(key);
        if (value == null) { return null; }
        if (value.getId() == Tag.TAG_STRING) { return text(tag.getString(key)); }
        if (value.getId() == Tag.TAG_DOUBLE) { return number(tag.getDouble(key)); }
        return value.getId() == Tag.TAG_BYTE ? bool(tag.getBoolean(key)) : null;
    }

    public void toNbt(CompoundTag tag, String key) {
        if (kind == TEXT) { tag.putString(key, text); }
        else if (kind == NUMBER) { tag.putDouble(key, number); }
        else { tag.putBoolean(key, flag); }
    }

    public Object raw() { return kind == TEXT ? text : kind == NUMBER ? number : flag; }

    public SyncValue forClient() {
        String plain = kind == TEXT ? ChatFormatting.stripFormatting(text) : null;
        return plain == null ? this : text(plain);
    }

    public boolean matches(Object other) {
        if (kind == TEXT) { return other instanceof String && text.equals(other); }
        if (kind == NUMBER) { return other instanceof Number && number == ((Number) other).doubleValue(); }
        return other instanceof Boolean && flag == (Boolean) other;
    }
}
