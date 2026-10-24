package mctmods.resourcedatapackloader.content.def;

import mctmods.resourcedatapackloader.content.util.ItemMatch;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import java.util.ArrayList;
import java.util.List;

public final class DietDef {
    public static final int DEFAULT_MEMORY = 10;
    private static final String DEFAULT_REPEAT = "rdpl.diet.repeat";
    private static final String DEFAULT_VARIETY = "rdpl.diet.variety";
    private final int memory;
    private final float[] repeat;
    private final List<Tier> variety;
    private final List<Group> groups;
    private final String repeatMessage;
    private final String varietyMessage;

    private DietDef(int memory, float[] repeat, List<Tier> variety, List<Group> groups, String repeatMessage, String varietyMessage) {
        this.memory = memory;
        this.repeat = repeat;
        this.variety = variety;
        this.groups = groups;
        this.repeatMessage = repeatMessage;
        this.varietyMessage = varietyMessage;
    }

    private record Tier(int distinct, float multiplier) {}

    private record Group(String text, ItemMatch match) {}

    public static DietDef parse(ResourceLocation file, JsonObject json) {
        int memory = json.has("memory") ? whole(json.get("memory"), "memory") : DEFAULT_MEMORY;
        float[] repeat = {1.0F};
        if (json.has("repeat")) {
            JsonArray array = array(json.get("repeat"), "repeat");
            if (array.isEmpty()) { throw new IllegalArgumentException("repeat names no multipliers"); }
            repeat = new float[array.size()];
            for (int i = 0; i < repeat.length; i++) { repeat[i] = (float) number(array.get(i), "repeat", 0.0D); }
        }
        List<Tier> variety = new ArrayList<>();
        if (json.has("variety")) {
            for (JsonElement element : array(json.get("variety"), "variety")) {
                if (!element.isJsonObject()) { throw new IllegalArgumentException("variety entries must be objects"); }
                JsonObject tier = element.getAsJsonObject();
                variety.add(new Tier(whole(tier.get("distinct"), "distinct"), (float) number(tier.get("multiplier"), "multiplier", 0.0D)));
            }
        }
        List<Group> groups = new ArrayList<>();
        if (json.has("groups")) {
            for (JsonElement element : array(json.get("groups"), "groups")) {
                if (!element.isJsonPrimitive() || !element.getAsJsonPrimitive().isString()) { throw new IllegalArgumentException("groups entries must be text"); }
                String text = element.getAsString().trim();
                ItemMatch match = ItemMatch.parse(file, text);
                if (match == null) { throw new IllegalArgumentException("groups entry '" + text + "' matches nothing"); }
                groups.add(new Group(text, match));
            }
        }
        String repeatMessage = DEFAULT_REPEAT;
        String varietyMessage = DEFAULT_VARIETY;
        if (json.has("messages")) {
            if (!json.get("messages").isJsonObject()) { throw new IllegalArgumentException("messages must be an object"); }
            JsonObject messages = json.getAsJsonObject("messages");
            repeatMessage = message(messages, "repeat", DEFAULT_REPEAT);
            varietyMessage = message(messages, "variety", DEFAULT_VARIETY);
        }
        return new DietDef(memory, repeat, variety, groups, repeatMessage, varietyMessage);
    }

    public int memory() { return memory; }

    public String kind(ItemStack stack, String registryKey) {
        for (Group group : groups) { if (group.match.matches(stack)) { return group.text; } }
        return registryKey;
    }

    public float repeatMultiplier(int count) { return repeat[Math.min(count, repeat.length - 1)]; }

    public float varietyMultiplier(int distinct) {
        Tier best = null;
        for (Tier tier : variety) {
            if (tier.distinct <= distinct && (best == null || tier.distinct > best.distinct)) { best = tier; }
        }
        return best == null ? 1.0F : best.multiplier;
    }

    public String messageFor(float repeatMultiplier, float varietyMultiplier) { return varietyMultiplier > 1.0F ? varietyMessage : repeatMultiplier < 1.0F ? repeatMessage : ""; }

    private static String message(JsonObject messages, String name, String fallback) {
        if (!messages.has(name)) { return fallback; }
        JsonElement element = messages.get(name);
        if (!element.isJsonPrimitive() || !element.getAsJsonPrimitive().isString()) { throw new IllegalArgumentException("messages." + name + " must be text"); }
        return element.getAsString().trim();
    }

    private static JsonArray array(JsonElement element, String name) {
        if (element == null || !element.isJsonArray()) { throw new IllegalArgumentException(name + " must be a list"); }
        return element.getAsJsonArray();
    }

    private static double number(JsonElement element, String name, double least) {
        if (element == null || !element.isJsonPrimitive() || !element.getAsJsonPrimitive().isNumber()) { throw new IllegalArgumentException(name + " must be a number"); }
        double value = element.getAsDouble();
        if (value < least) { throw new IllegalArgumentException(name + " must be at least " + least); }
        return value;
    }

    private static int whole(JsonElement element, String name) {
        double value = number(element, name, 1.0D);
        if (value != Math.rint(value) || value > Integer.MAX_VALUE) { throw new IllegalArgumentException(name + " must be a whole number"); }
        return (int) value;
    }
}
