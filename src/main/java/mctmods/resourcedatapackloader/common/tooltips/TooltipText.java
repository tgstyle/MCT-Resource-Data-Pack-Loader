package mctmods.resourcedatapackloader.common.tooltips;

import mctmods.resourcedatapackloader.common.syncdata.DataReader;
import mctmods.resourcedatapackloader.common.syncdata.DataText;
import mctmods.resourcedatapackloader.core.util.ContentLog;

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
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NumericTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

public final class TooltipText {
    private static final String NBT = "nbt";
    private static final String ITEM = "item";
    private static final String NBT_OPEN = "{nbt:";
    private static final String ITEM_OPEN = "{item:";
    private static final int MAX_DIGITS = 9;
    private static final ResourceLocation UNKNOWN = ResourceLocation.fromNamespaceAndPath("rdpl", "unknown");
    private final String[] literal;
    private final String[][] keys;
    private final int[][] index;
    private final ResourceLocation[] components;
    private final Object[] seenValue;
    private final String[] seenRead;
    private final String[] fallback;

    private TooltipText(String[] literal, String[][] keys, int[][] index, ResourceLocation[] components, String[] fallback) {
        this.fallback = fallback;
        this.literal = literal;
        this.keys = keys;
        this.index = index;
        this.components = components;
        this.seenValue = new Object[keys.length];
        this.seenRead = new String[keys.length];
    }

    public static TooltipText parse(Object file, String raw) {
        List<String> literals = new ArrayList<>();
        List<String[]> paths = new ArrayList<>();
        List<int[]> indexes = new ArrayList<>();
        List<ResourceLocation> components = new ArrayList<>();
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
            String opening = isData ? DataText.OPEN : isNbt ? NBT_OPEN : ITEM_OPEN;
            int open = start + opening.length();
            int end = raw.indexOf('}', open);
            if (end < 0) { break; }
            String body = raw.substring(open, end);
            if (isData ? DataText.name(body).isEmpty() : end == open) {
                ContentLog.LOGGER.error("A tooltip line in {} has an empty {} {}, keeping it as text", file, isData ? "data" : isNbt ? NBT : ITEM, isData ? "name" : "path");
                from = end + 1;
                continue;
            }
            literals.add(raw.substring(kept, start));
            if (isData) {
                paths.add(new String[] {DataText.name(body)});
                indexes.add(new int[] {-1});
                components.add(null);
                fallbacks.add(DataText.fallback(body));
            }
            else {
                split(body, isNbt, paths, indexes, components);
                fallbacks.add(null);
            }
            kept = end + 1;
            from = kept;
        }
        literals.add(raw.substring(kept));
        return new TooltipText(literals.toArray(new String[0]), paths.toArray(new String[0][]), indexes.toArray(new int[0][]), components.toArray(new ResourceLocation[0]), fallbacks.toArray(new String[0]));
    }

    private static int first(int a, int b) { return a < 0 ? b : b < 0 ? a : Math.min(a, b); }

    private static void split(String path, boolean isNbt, List<String[]> paths, List<int[]> indexes, List<ResourceLocation> components) {
        List<String> parts = new ArrayList<>();
        int from = 0;
        while (true) {
            int dot = path.indexOf('.', from);
            if (dot < 0) { break; }
            parts.add(path.substring(from, dot));
            from = dot + 1;
        }
        parts.add(path.substring(from));
        ResourceLocation component = isNbt ? null : ResourceLocation.tryParse(parts.removeFirst());
        String[] names = parts.toArray(new String[0]);
        int[] numbers = new int[names.length];
        for (int at = 0; at < names.length; at++) { numbers[at] = number(names[at]); }
        paths.add(names);
        indexes.add(numbers);
        components.add(isNbt ? null : component == null ? UNKNOWN : component);
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
        for (int at = 0; at < keys.length; at++) { out.append(read(stack, at, access)).append(literal[at + 1]); }
        return out.toString();
    }

    private String read(ItemStack stack, int at, @Nullable HolderLookup.Provider access) {
        if (fallback[at] != null) { return DataText.value(DataReader.client(), keys[at][0], fallback[at]); }
        ResourceLocation id = components[at];
        DataComponentType<?> type = id == null ? DataComponents.CUSTOM_DATA : BuiltInRegistries.DATA_COMPONENT_TYPE.get(id);
        Object value = type == null ? null : stack.get(type);
        if (value != seenValue[at] || seenRead[at] == null) {
            seenValue[at] = value;
            seenRead[at] = value == null ? "" : walk(id == null ? ((CustomData) value).copyTag() : encode(stack, type, access), keys[at], index[at]);
        }
        return seenRead[at];
    }

    @Nullable private static <T> Tag encode(ItemStack stack, DataComponentType<T> type, @Nullable HolderLookup.Provider access) {
        T value = stack.get(type);
        if (value == null || type.isTransient()) { return null; }
        DynamicOps<Tag> ops = access == null ? NbtOps.INSTANCE : access.createSerializationContext(NbtOps.INSTANCE);
        return type.codecOrThrow().encodeStart(ops, value).result().orElse(null);
    }

    public static String number(double value) { return value == (long) value ? String.valueOf((long) value) : String.valueOf(value); }

    static String walk(@Nullable Tag start, String[] keys, int[] index) {
        Tag current = start;
        for (int at = 0; at < keys.length && current != null; at++) {
            if (current instanceof CompoundTag compound) { current = compound.get(keys[at]); }
            else if (current instanceof CollectionTag<?> list && index[at] >= 0 && index[at] < list.size()) { current = list.get(index[at]); }
            else { current = null; }
        }
        return format(current);
    }

    static String format(@Nullable Tag tag) {
        if (tag instanceof FloatTag number) {
            float value = number.getAsFloat();
            return value == (long) value ? String.valueOf((long) value) : String.valueOf(value);
        }
        if (tag instanceof DoubleTag number) {
            double value = number.getAsDouble();
            return value == (long) value ? String.valueOf((long) value) : String.valueOf(value);
        }
        if (tag instanceof NumericTag number) { return String.valueOf(number.getAsLong()); }
        if (tag instanceof StringTag string) { return ChatFormatting.stripFormatting(string.getAsString()); }
        return "";
    }
}
