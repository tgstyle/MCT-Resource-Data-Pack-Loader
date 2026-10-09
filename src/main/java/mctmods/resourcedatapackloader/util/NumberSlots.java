package mctmods.resourcedatapackloader.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Map;
import java.util.function.BiFunction;
import javax.annotation.Nullable;
import net.minecraft.util.JsonUtils;

public final class NumberSlots {
    private static final String TYPE = "type";
    private static final String COUNT = "count";

    private NumberSlots() {}

    private enum Slot { INT, FLOAT, RANGE }

    public static JsonElement walk(JsonElement element, BiFunction<JsonElement, Boolean, JsonElement> fit) {
        if (element.isJsonArray()) { return JsonTree.map(element.getAsJsonArray(), item -> walk(item, fit)); }
        if (!element.isJsonObject()) { return element; }
        JsonObject in = element.getAsJsonObject();
        String owner = type(in);
        return JsonTree.map(in, (key, value) -> {
            Slot slot = slot(in, owner, key);
            return slot == null ? nested(owner, key, value, fit) : fitted(value, slot, fit);
        });
    }

    @Nullable private static Slot slot(JsonObject owner, String type, String key) {
        if (owner.has("rolls") && owner.has("entries")) { return "rolls".equals(key) ? Slot.INT : "bonus_rolls".equals(key) ? Slot.FLOAT : null; }
        if (owner.has("wants") && owner.has("gives")) { return "max_uses".equals(key) || "xp".equals(key) ? Slot.INT : "reputation_discount".equals(key) ? Slot.FLOAT : null; }
        if (owner.has("amount") && owner.has("trades")) { return "amount".equals(key) ? Slot.INT : null; }
        switch (type + "|" + key) {
            case "enchant_with_levels|levels":
            case "set_count|count":
            case "set_ominous_bottle_amplifier|amplifier":
            case "set_random_dyes|number_of_dyes":
            case "int_value_check|value": return Slot.INT;
            case "random_chance|chance":
            case "set_damage|damage":
            case "enchanted_count_increase|count": return Slot.FLOAT;
            case "limit_count|limit":
            case "int_value_check|test":
            case "time_check|value": return Slot.RANGE;
            default: return null;
        }
    }

    private static JsonElement nested(String owner, String key, JsonElement value, BiFunction<JsonElement, Boolean, JsonElement> fit) {
        switch (owner + "|" + key) {
            case "set_enchantments|enchantments":
            case "entity_scores|scores": {
                if (!value.isJsonObject()) { return walk(value, fit); }
                JsonObject out = new JsonObject();
                Slot slot = "set_enchantments".equals(owner) ? Slot.INT : Slot.RANGE;
                value.getAsJsonObject().entrySet().forEach(entry -> out.add(entry.getKey(), fitted(entry.getValue(), slot, fit)));
                return out;
            }
            case "set_stew_effect|effects":
            case "set_attributes|modifiers": {
                boolean stew = "set_stew_effect".equals(owner);
                return each(value, stew ? "duration" : "amount", stew ? Slot.INT : Slot.FLOAT, fit);
            }
            case "set_custom_model_data|floats": {
                if (value.isJsonArray()) { return listed(value.getAsJsonArray(), fit); }
                if (!value.isJsonObject() || !JsonUtils.isJsonArray(value.getAsJsonObject(), "values")) { return walk(value, fit); }
                JsonObject out = JsonTree.copy(value.getAsJsonObject());
                out.add("values", listed(out.getAsJsonArray("values"), fit));
                return out;
            }
            default: {
                if (("wants".equals(key) || "additional_wants".equals(key)) && value.isJsonObject() && value.getAsJsonObject().has(COUNT)) {
                    JsonObject out = JsonTree.copy(value.getAsJsonObject());
                    out.add(COUNT, fit.apply(out.get(COUNT), true));
                    return out;
                }
                return walk(value, fit);
            }
        }
    }

    private static JsonElement each(JsonElement value, String key, Slot slot, BiFunction<JsonElement, Boolean, JsonElement> fit) {
        if (!value.isJsonArray()) { return walk(value, fit); }
        JsonArray out = new JsonArray();
        for (JsonElement item : value.getAsJsonArray()) {
            JsonElement held = walk(item, fit);
            if (held.isJsonObject() && held.getAsJsonObject().has(key)) { held.getAsJsonObject().add(key, fitted(item.getAsJsonObject().get(key), slot, fit)); }
            out.add(held);
        }
        return out;
    }

    private static JsonArray listed(JsonArray values, BiFunction<JsonElement, Boolean, JsonElement> fit) { return JsonTree.map(values, item -> fit.apply(item, false)); }

    private static JsonElement fitted(JsonElement value, Slot slot, BiFunction<JsonElement, Boolean, JsonElement> fit) {
        if (slot != Slot.RANGE) { return fit.apply(value, slot == Slot.INT); }
        if (!value.isJsonObject() || value.getAsJsonObject().has(TYPE)) { return fit.apply(value, true); }
        JsonObject out = new JsonObject();
        for (Map.Entry<String, JsonElement> entry : value.getAsJsonObject().entrySet()) { out.add(entry.getKey(), "min".equals(entry.getKey()) || "max".equals(entry.getKey()) ? fit.apply(entry.getValue(), true) : JsonTree.copy(entry.getValue())); }
        return out;
    }

    public static String type(JsonObject object) {
        String type = JsonUtils.isJsonPrimitive(object, TYPE) ? object.get(TYPE).getAsString() : "";
        return IdNames.bare(type);
    }
}
