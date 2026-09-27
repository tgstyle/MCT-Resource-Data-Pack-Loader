package mctmods.resourcedatapackloader.pack.port;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.datafixers.DSL;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import net.minecraft.SharedConstants;
import net.minecraft.util.datafix.DataFixers;
import net.minecraft.util.datafix.fixes.References;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nullable;

final class ModernFixes {
    static final int DATA_1_21 = 3955;
    static final String MINECRAFT = "minecraft:";
    private static final List<String> ATTRIBUTE_PREFIXES = List.of("generic.", "horse.", "player.", "zombie.");
    private static final Map<String, String> NAMES = new ConcurrentHashMap<>();
    private static final Map<String, String> RECIPES = new ConcurrentHashMap<>();

    private ModernFixes() {}

    private static JsonElement fix(DSL.TypeReference type, JsonElement value) {
        int current = SharedConstants.getCurrentVersion().dataVersion().version();
        return DataFixers.getDataFixer().update(type, new Dynamic<>(JsonOps.INSTANCE, value), DATA_1_21, current).getValue();
    }

    static JsonObject stack(JsonObject stack) {
        JsonElement fixed = fix(References.ITEM_STACK, stack);
        return fixed.isJsonObject() ? fixed.getAsJsonObject() : stack;
    }

    static JsonObject components(String item, JsonObject components) {
        JsonObject stack = new JsonObject();
        stack.addProperty("id", item);
        stack.addProperty("count", 1);
        stack.add("components", components);
        JsonObject fixed = stack(stack);
        return fixed.has("components") && fixed.get("components").isJsonObject() ? fixed.getAsJsonObject("components") : new JsonObject();
    }

    static JsonElement text(JsonElement text) { return fix(References.TEXT_COMPONENT, new JsonPrimitive(text.toString())); }

    static String name(String id) {
        return NAMES.computeIfAbsent(id, held -> {
            String item = fixed(References.ITEM_NAME, held);
            String block = item.equals(held) ? fixed(References.BLOCK_NAME, held) : item;
            return attribute(block);
        });
    }

    static String recipe(String id) { return RECIPES.computeIfAbsent(id, held -> fixed(References.RECIPE, held)); }

    private static String fixed(DSL.TypeReference type, String id) {
        JsonElement fixed = fix(type, new JsonPrimitive(id));
        return fixed.isJsonPrimitive() ? fixed.getAsString() : id;
    }

    private static String attribute(String id) {
        String named = id.indexOf(':') < 0 ? MINECRAFT + id : id;
        for (String prefix : ATTRIBUTE_PREFIXES) {
            if (named.startsWith(MINECRAFT + prefix)) { return MINECRAFT + named.substring(MINECRAFT.length() + prefix.length()); }
        }
        return id;
    }

    static boolean notIdentifier(String text) {
        int colon = text.indexOf(':');
        if (colon <= 0 || colon == text.length() - 1 || text.startsWith("#")) { return true; }
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (!(c >= 'a' && c <= 'z' || c >= '0' && c <= '9' || c == '_' || c == '-' || c == '.' || c == '/' || c == ':')) { return true; }
        }
        return false;
    }

    static JsonElement names(JsonElement element) {
        if (element.isJsonArray()) {
            JsonArray out = new JsonArray();
            element.getAsJsonArray().forEach(inner -> out.add(names(inner)));
            return out;
        }
        if (element.isJsonObject()) {
            JsonObject out = new JsonObject();
            element.getAsJsonObject().entrySet().forEach(entry -> out.add(entry.getKey(), names(entry.getValue())));
            return out;
        }
        if (!element.isJsonPrimitive() || !element.getAsJsonPrimitive().isString() || notIdentifier(element.getAsString())) { return element; }
        String renamed = name(element.getAsString());
        return renamed.equals(element.getAsString()) ? element : new JsonPrimitive(renamed);
    }

    static String firstItem(@Nullable JsonElement items) {
        JsonElement one = items != null && items.isJsonArray() && !items.getAsJsonArray().isEmpty() ? items.getAsJsonArray().get(0) : items;
        return one != null && one.isJsonPrimitive() && !one.getAsString().startsWith("#") ? one.getAsString() : MINECRAFT + "stone";
    }

    static void replace(JsonObject held, JsonObject walked) {
        for (String key : List.copyOf(held.keySet())) { held.remove(key); }
        walked.entrySet().forEach(entry -> held.add(entry.getKey(), entry.getValue()));
    }
}
