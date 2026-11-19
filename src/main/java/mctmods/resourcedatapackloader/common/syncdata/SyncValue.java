package mctmods.resourcedatapackloader.common.syncdata;

import mctmods.resourcedatapackloader.common.tooltips.TooltipText;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

public final class SyncValue {
    public static final int TEXT = 1;
    public static final int NUMBER = 2;
    public static final int BOOL = 3;
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
        if (value instanceof Number given) { return number(given.doubleValue()); }
        if (value instanceof Boolean given) { return bool(given); }
        return null;
    }

    @Nullable public static Object plain(JsonElement element) {
        if (!element.isJsonPrimitive()) { return null; }
        JsonPrimitive primitive = element.getAsJsonPrimitive();
        if (primitive.isNumber()) { return primitive.getAsDouble(); }
        return primitive.isBoolean() ? (Object) primitive.getAsBoolean() : primitive.getAsString();
    }

    public static String kindName(int kind) { return kind == NUMBER ? "number" : kind == BOOL ? "bool" : "text"; }

    @Nullable public static SyncValue fromNbt(CompoundTag tag, String key) {
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

    public SyncValue forClient() { return kind == TEXT ? text(ChatFormatting.stripFormatting(text)) : this; }

    public boolean matches(Object other) {
        if (kind == TEXT) { return other instanceof String && text.equals(other); }
        if (kind == NUMBER) { return other instanceof Number given && number == given.doubleValue(); }
        return other instanceof Boolean given && flag == given;
    }
}
