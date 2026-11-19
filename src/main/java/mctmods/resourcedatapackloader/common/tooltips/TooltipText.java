package mctmods.resourcedatapackloader.common.tooltips;

import mctmods.resourcedatapackloader.common.syncdata.DataReader;
import mctmods.resourcedatapackloader.common.syncdata.DataText;
import mctmods.resourcedatapackloader.core.util.ContentLog;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NumericTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;

public final class TooltipText {
    private static final String NBT = "nbt";
    private static final String ITEM = "item";
    private static final String NBT_OPEN = "{nbt:";
    private static final String ITEM_OPEN = "{item:";
    private static final int MAX_DIGITS = 9;
    private final String[] literal;
    private final String[][] keys;
    private final int[][] index;

    private final String[] fallback;

    private TooltipText(String[] literal, String[][] keys, int[][] index, String[] fallback) {
        this.literal = literal;
        this.keys = keys;
        this.index = index;
        this.fallback = fallback;
    }

    public static TooltipText parse(Object file, String raw) {
        List<String> literals = new ArrayList<>();
        List<String[]> paths = new ArrayList<>();
        List<int[]> indexes = new ArrayList<>();
        List<String> fallbacks = new ArrayList<>();
        int kept = 0;
        int from = 0;
        while (true) {
            int nbt = raw.indexOf(NBT_OPEN, from);
            int item = raw.indexOf(ITEM_OPEN, from);
            int data = DataText.token(raw, from);
            if (nbt < 0 && item < 0 && data < 0) { break; }
            int start = first(first(nbt, item), data);
            boolean isData = start == data;
            boolean isNbt = start == nbt;
            String open = isData ? DataText.OPEN : isNbt ? NBT_OPEN : ITEM_OPEN;
            int begin = start + open.length();
            int end = raw.indexOf('}', begin);
            if (end < 0) { break; }
            String body = raw.substring(begin, end);
            if (isData ? DataText.name(body).isEmpty() : end == begin) {
                ContentLog.LOGGER.error("A tooltip line in {} has an empty {} {}, keeping it as text", file, isData ? "data" : isNbt ? NBT : ITEM, isData ? "name" : "path");
                from = end + 1;
                continue;
            }
            literals.add(raw.substring(kept, start));
            if (isData) {
                paths.add(new String[] {DataText.name(body)});
                indexes.add(new int[] {-1});
                fallbacks.add(DataText.fallback(body));
            }
            else {
                split(body, paths, indexes);
                fallbacks.add(null);
            }
            kept = end + 1;
            from = kept;
        }
        literals.add(raw.substring(kept));
        return new TooltipText(literals.toArray(new String[0]), paths.toArray(new String[0][]), indexes.toArray(new int[0][]), fallbacks.toArray(new String[0]));
    }

    private static int first(int a, int b) { return a < 0 ? b : b < 0 ? a : Math.min(a, b); }

    private static void split(String path, List<String[]> paths, List<int[]> indexes) {
        List<String> parts = new ArrayList<>();
        int from = 0;
        while (true) {
            int dot = path.indexOf('.', from);
            if (dot < 0) { break; }
            parts.add(path.substring(from, dot));
            from = dot + 1;
        }
        parts.add(path.substring(from));
        String[] names = parts.toArray(new String[0]);
        int[] numbers = new int[names.length];
        for (int at = 0; at < names.length; at++) { numbers[at] = number(names[at]); }
        paths.add(names);
        indexes.add(numbers);
    }

    private static int number(String part) {
        if (part.isEmpty() || part.length() > MAX_DIGITS) { return -1; }
        int value = 0;
        for (int at = 0; at < part.length(); at++) {
            char c = part.charAt(at);
            if (c < '0' || c > '9') { return -1; }
            value = value * 10 + (c - '0');
        }
        return value;
    }

    public boolean plain() { return keys.length == 0; }

    public String fill(ItemStack stack) {
        StringBuilder out = new StringBuilder(literal[0]);
        for (int at = 0; at < keys.length; at++) {
            out.append(fallback[at] == null ? read(stack, keys[at], index[at]) : DataText.value(DataReader.client(), keys[at][0], fallback[at])).append(literal[at + 1]);
        }
        return out.toString();
    }

    static String read(ItemStack stack, String[] keys, int[] index) {
        Tag current = stack.getTag();
        for (int at = 0; at < keys.length && current != null; at++) {
            if (current instanceof CompoundTag compound) { current = compound.get(keys[at]); }
            else if (current instanceof ListTag list && index[at] >= 0 && index[at] < list.size()) { current = list.get(index[at]); }
            else { current = null; }
        }
        return format(current);
    }

    public static String number(double value) { return value == (long) value ? String.valueOf((long) value) : String.valueOf(value); }

    static String format(@Nullable Tag tag) {
        if (tag == null) { return ""; }
        int id = tag.getId();
        if (id >= Tag.TAG_BYTE && id <= Tag.TAG_LONG) { return String.valueOf(((NumericTag) tag).getAsLong()); }
        if (id == Tag.TAG_FLOAT) {
            float value = ((NumericTag) tag).getAsFloat();
            return value == (long) value ? String.valueOf((long) value) : String.valueOf(value);
        }
        if (id == Tag.TAG_DOUBLE) {
            double value = ((NumericTag) tag).getAsDouble();
            return value == (long) value ? String.valueOf((long) value) : String.valueOf(value);
        }
        if (id == Tag.TAG_STRING) { return ChatFormatting.stripFormatting(tag.getAsString()); }
        return "";
    }
}
