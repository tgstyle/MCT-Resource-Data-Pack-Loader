package mctmods.resourcedatapackloader.common.tooltips;

import mctmods.resourcedatapackloader.common.syncdata.DataReader;
import mctmods.resourcedatapackloader.common.syncdata.DataText;
import mctmods.resourcedatapackloader.core.util.ContentLog;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CollectionTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.item.ItemStack;

public final class TooltipText {
    private static final String NBT = "nbt";
    private static final String ITEM = "item";
    private static final String NBT_OPEN = "{nbt:";
    private static final String ITEM_OPEN = "{item:";
    private static final int MAX_DIGITS = 9;
    private static final int BYTE = 1;
    private static final int LONG = 4;
    private static final int FLOAT = 5;
    private static final int DOUBLE = 6;
    private static final int STRING = 8;
    private final String[] literal;
    private final DataComponentType<?>[] types;
    private final String[][] keys;
    private final int[][] index;
    private final Object[] seenValue;
    private final String[] seenRead;
    private final String[] fallback;

    private TooltipText(String[] literal, DataComponentType<?>[] types, String[][] keys, int[][] index, String[] fallback) {
        this.fallback = fallback;
        this.literal = literal;
        this.types = types;
        this.keys = keys;
        this.index = index;
        this.seenValue = new Object[keys.length];
        this.seenRead = new String[keys.length];
    }

    public static TooltipText parse(Identifier file, String raw) {
        List<String> literals = new ArrayList<>();
        List<DataComponentType<?>> types = new ArrayList<>();
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
            int end = DataText.close(raw, start, open);
            if (end < 0) { break; }
            String body = raw.substring(begin, end);
            if (isData ? DataText.name(body).isEmpty() : end == begin) {
                ContentLog.LOGGER.error("A tooltip line in {} has an empty {} {}, keeping it as text", file, isData ? "data" : isNbt ? NBT : ITEM, isData ? "name" : "path");
                from = end + 1;
                continue;
            }
            literals.add(raw.substring(kept, start));
            if (isData) {
                types.add(null);
                paths.add(new String[] {DataText.name(body)});
                indexes.add(new int[] {-1});
                fallbacks.add(DataText.fallback(body));
            }
            else {
                split(body, isNbt, types, paths, indexes);
                fallbacks.add(null);
            }
            kept = end + 1;
            from = kept;
        }
        literals.add(raw.substring(kept));
        return new TooltipText(literals.toArray(new String[0]), types.toArray(new DataComponentType<?>[0]), paths.toArray(new String[0][]), indexes.toArray(new int[0][]), fallbacks.toArray(new String[0]));
    }

    private static int first(int a, int b) { return a < 0 ? b : b < 0 ? a : Math.min(a, b); }

    private static void split(String path, boolean isNbt, List<DataComponentType<?>> types, List<String[]> paths, List<int[]> indexes) {
        List<String> parts = new ArrayList<>();
        int from = 0;
        while (true) {
            int dot = path.indexOf('.', from);
            if (dot < 0) { break; }
            parts.add(path.substring(from, dot));
            from = dot + 1;
        }
        parts.add(path.substring(from));
        int skip = isNbt ? 0 : 1;
        String[] names = parts.subList(skip, parts.size()).toArray(new String[0]);
        int[] numbers = new int[names.length];
        for (int at = 0; at < names.length; at++) { numbers[at] = number(names[at]); }
        types.add(isNbt ? DataComponents.CUSTOM_DATA : component(parts.getFirst()));
        paths.add(names);
        indexes.add(numbers);
    }

    @Nullable private static DataComponentType<?> component(String id) {
        Identifier key = Identifier.tryParse(id);
        return key == null ? null : BuiltInRegistries.DATA_COMPONENT_TYPE.getValue(key);
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

    public String fill(ItemStack stack, @Nullable HolderLookup.Provider access) {
        StringBuilder out = new StringBuilder(literal[0]);
        for (int at = 0; at < keys.length; at++) {
            out.append(fallback[at] == null ? read(stack, at, access) : DataText.value(DataReader.client(), keys[at][0], fallback[at])).append(literal[at + 1]);
        }
        return out.toString();
    }

    private String read(ItemStack stack, int at, @Nullable HolderLookup.Provider access) {
        DataComponentType<?> type = types[at];
        Object value = type == null ? null : stack.get(type);
        if (value != seenValue[at] || seenRead[at] == null) {
            seenValue[at] = value;
            seenRead[at] = value == null ? "" : walk(encode(type, value, access == null ? NbtOps.INSTANCE : RegistryOps.create(NbtOps.INSTANCE, access)), keys[at], index[at]);
        }
        return seenRead[at];
    }

    private static String walk(@Nullable Tag start, String[] keys, int[] index) {
        Tag current = start;
        for (int at = 0; at < keys.length && current != null; at++) {
            if (current instanceof CompoundTag compound) { current = compound.get(keys[at]); }
            else if (current instanceof CollectionTag list && index[at] >= 0 && index[at] < list.size()) { current = list.get(index[at]); }
            else { current = null; }
        }
        return format(current);
    }

    @Nullable private static <T> Tag encode(DataComponentType<T> type, Object value, DynamicOps<Tag> ops) {
        Codec<T> codec = type.codec();
        return codec == null ? null : codec.encodeStart(ops, unchecked(value)).result().orElse(null);
    }

    @SuppressWarnings("unchecked")
    private static <T> T unchecked(Object value) { return (T) value; }

    private static String format(@Nullable Tag tag) {
        if (tag == null) { return ""; }
        int id = tag.getId();
        if (id >= BYTE && id <= LONG) { return String.valueOf(tag.asNumber().orElse(0).longValue()); }
        if (id == FLOAT) {
            float value = tag.asNumber().orElse(0).floatValue();
            return value == (long) value ? String.valueOf((long) value) : String.valueOf(value);
        }
        if (id == DOUBLE) { return number(tag.asNumber().orElse(0).doubleValue()); }
        if (id == STRING) { return ChatFormatting.stripFormatting(tag.asString().orElse("")); }
        return "";
    }

    public static String number(double value) { return value == (long) value ? String.valueOf((long) value) : String.valueOf(value); }
}
