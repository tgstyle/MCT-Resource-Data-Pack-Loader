package mctmods.resourcedatapackloader.pack.port;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import net.minecraft.SharedConstants;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NumericTag;
import net.minecraft.nbt.ShortTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagParser;
import net.minecraft.util.datafix.DataFixers;
import net.minecraft.util.datafix.fixes.References;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
import javax.annotation.Nullable;

final class CrossStacks {
    private static final String ID = "id";
    private static final String DISPLAY = "display";
    private static final String COMPONENTS = "components";

    private CrossStacks() {}

    private static int current() { return SharedConstants.getCurrentVersion().dataVersion().version(); }

    static CompoundTag parse(String snbt) {
        try { return TagParser.parseCompoundFully(snbt); }
        catch (CommandSyntaxException failed) { throw new Commands.Kept("'" + snbt + "' is not valid NBT"); }
    }

    static CompoundTag compound(JsonElement written) {
        if (written.isJsonPrimitive()) { return parse(written.getAsString()); }
        Tag tag = new Dynamic<>(JsonOps.INSTANCE, written).convert(NbtOps.INSTANCE).getValue();
        return tag instanceof CompoundTag held ? held : new CompoundTag();
    }

    static JsonElement json(Tag tag) { return new Dynamic<>(NbtOps.INSTANCE, tag).convert(JsonOps.INSTANCE).getValue(); }

    static CompoundTag up(String id, @Nullable CompoundTag tag) {
        CompoundTag stack = new CompoundTag();
        stack.putString(ID, id);
        stack.putByte("Count", (byte) 1);
        if (tag != null && !tag.isEmpty()) { stack.put("tag", tag); }
        return upStack(stack);
    }

    private static CompoundTag upStack(CompoundTag stack) {
        Dynamic<Tag> fixed = DataFixers.getDataFixer().update(References.ITEM_STACK, new Dynamic<>(NbtOps.INSTANCE, stack), CrossIds.DATA_1_20, current());
        return fixed.getValue() instanceof CompoundTag held ? held : stack;
    }

    static CompoundTag components(CompoundTag stack) { return stack.getCompound(COMPONENTS).isPresent() ? stack.getCompoundOrEmpty(COMPONENTS) : new CompoundTag(); }

    static String upCommand(String id, @Nullable String snbt) {
        CompoundTag stack = up(id, snbt == null ? null : parse(snbt));
        CompoundTag components = components(stack);
        String named = stack.getStringOr(ID, "").isEmpty() ? id : stack.getStringOr(ID, "");
        if (components.isEmpty()) { return named; }
        List<String> pairs = new ArrayList<>();
        for (String key : new TreeSet<>(components.keySet())) { pairs.add(key + "=" + components.get(key)); }
        return named + "[" + String.join(",", pairs) + "]";
    }

    static String downCommand(String id, String bracketed, List<String> lost) {
        CompoundTag components = new CompoundTag();
        for (String part : CrossCommands.split(bracketed.substring(1, bracketed.length() - 1), ',')) {
            String trimmed = part.trim();
            if (trimmed.isEmpty()) { continue; }
            int equals = trimmed.indexOf('=');
            if (equals < 0 || trimmed.startsWith("!") || trimmed.indexOf('~') >= 0 && trimmed.indexOf('~') < equals) { throw new Commands.Kept("the item test '" + trimmed + "' has no 1.20.1 form"); }
            Tag value = parse("{v:" + trimmed.substring(equals + 1) + "}").get("v");
            if (value == null) { throw new Commands.Kept("the item test '" + trimmed + "' has no value"); }
            components.put(trimmed.substring(0, equals).trim(), value);
        }
        CompoundTag tag = down(components, lost);
        return tag.isEmpty() ? id : id + tag;
    }

    static CompoundTag down(CompoundTag components, List<String> lost) {
        CompoundTag tag = new CompoundTag();
        CompoundTag display = new CompoundTag();
        for (String key : components.keySet()) {
            Tag value = components.get(key);
            String bare = key.startsWith(CrossIds.MINECRAFT) ? key.substring(CrossIds.MINECRAFT.length()) : key;
            if (value == null) { continue; }
            switch (bare) {
                case "custom_data" -> { if (value instanceof CompoundTag data) { tag.merge(data); } }
                case "damage" -> tag.putInt("Damage", number(value));
                case "unbreakable" -> tag.putBoolean("Unbreakable", true);
                case "custom_name" -> display.put("Name", text(value));
                case "item_name" -> { if (!display.contains("Name")) { display.put("Name", text(value)); } }
                case "lore" -> display.put("Lore", lore(value));
                case "enchantments" -> tag.put("Enchantments", enchantments(value));
                case "stored_enchantments" -> tag.put("StoredEnchantments", enchantments(value));
                case "custom_model_data" -> tag.putInt("CustomModelData", number(value));
                case "repair_cost" -> tag.putInt("RepairCost", number(value));
                case "dyed_color" -> display.putInt("color", value instanceof CompoundTag held ? held.getIntOr("rgb", 0) : number(value));
                case "potion_contents" -> potion(value, tag, lost);
                default -> lost.add(key);
            }
        }
        if (!display.isEmpty()) { tag.put(DISPLAY, display); }
        return tag;
    }

    private static int number(Tag value) { return value instanceof NumericTag held ? held.intValue() : 0; }

    private static Tag text(Tag value) { return value instanceof StringTag ? value : StringTag.valueOf(json(value).toString()); }

    private static ListTag lore(Tag value) {
        ListTag out = new ListTag();
        if (value instanceof ListTag lines) { lines.forEach(line -> out.add(text(line))); }
        return out;
    }

    private static ListTag enchantments(Tag value) {
        ListTag out = new ListTag();
        if (!(value instanceof CompoundTag held)) { return out; }
        CompoundTag levels = held.getCompound("levels").isPresent() ? held.getCompoundOrEmpty("levels") : held;
        for (String enchantment : levels.keySet()) {
            if (!(levels.get(enchantment) instanceof NumericTag level)) { continue; }
            CompoundTag one = new CompoundTag();
            one.putString(ID, CrossIds.id(enchantment, Port.Line.V1_20));
            one.put("lvl", ShortTag.valueOf((short) level.intValue()));
            out.add(one);
        }
        return out;
    }

    private static void potion(Tag value, CompoundTag tag, List<String> lost) {
        if (value instanceof StringTag(String potion)) {
            tag.putString("Potion", potion);
            return;
        }
        if (!(value instanceof CompoundTag held)) { return; }
        if (held.contains("potion")) { tag.putString("Potion", held.getStringOr("potion", "")); }
        if (held.contains("custom_color")) { tag.putInt("CustomPotionColor", held.getIntOr("custom_color", 0)); }
        if (held.contains("custom_effects")) { lost.add("potion_contents.custom_effects"); }
    }

    static JsonObject upJson(String id, @Nullable JsonElement nbt) {
        CompoundTag components = components(up(id, nbt == null ? null : compound(nbt)));
        return json(components) instanceof JsonObject held ? held : new JsonObject();
    }

    static Tag walk(Tag tag, Port.Line to, List<String> lost) {
        if (tag instanceof ListTag list) {
            for (int i = 0; i < list.size(); i++) { list.set(i, walk(list.get(i), to, lost)); }
            return list;
        }
        if (!(tag instanceof CompoundTag held)) { return tag; }
        for (String key : new ArrayList<>(held.keySet())) {
            Tag inner = held.get(key);
            if (inner != null) { held.put(key, walk(inner, to, lost)); }
        }
        if (held.getString(ID).isEmpty()) { return held; }
        if (to == Port.Line.V1_21 && held.getInt("Count").isPresent()) { return upStack(held); }
        if (to == Port.Line.V1_20 && held.getInt("count").isPresent()) {
            CompoundTag out = held.copy();
            out.remove("count");
            out.put("Count", ByteTag.valueOf((byte) held.getIntOr("count", 0)));
            out.remove(COMPONENTS);
            CompoundTag nbt = down(components(held), lost);
            if (!nbt.isEmpty()) { out.put("tag", nbt); }
            out.putString(ID, CrossIds.id(held.getStringOr(ID, ""), to));
            return out;
        }
        return held;
    }

    static boolean stacks(Tag tag, Port.Line from) {
        if (tag instanceof ListTag list) {
            for (Tag inner : list) {
                if (stacks(inner, from)) { return true; }
            }
            return false;
        }
        if (!(tag instanceof CompoundTag held)) { return false; }
        if (held.getString(ID).isPresent() && held.getInt(from == Port.Line.V1_20 ? "Count" : "count").isPresent()) { return true; }
        for (String key : held.keySet()) {
            Tag inner = held.get(key);
            if (stacks(inner, from)) { return true; }
        }
        return false;
    }
}
