package mctmods.resourcedatapackloader.content.def;

import mctmods.resourcedatapackloader.content.ContentParser;
import mctmods.resourcedatapackloader.content.util.ItemMatch;
import mctmods.resourcedatapackloader.util.ContentLog;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;

public final class DietDef {
    public static final int DEFAULT_MEMORY = 10;
    private static final String DEFAULT_REPEAT = "rdpl.diet.repeat";
    private static final String DEFAULT_VARIETY = "rdpl.diet.variety";
    public final int memory;
    public final float[] repeat;
    public final List<Tier> variety;
    public final List<Group> groups;
    public final List<ItemMatch> exempt;
    public final String repeatMessage;
    public final String varietyMessage;

    private DietDef(int memory, float[] repeat, List<Tier> variety, List<Group> groups, List<ItemMatch> exempt, String repeatMessage, String varietyMessage) {
        this.memory = memory;
        this.repeat = repeat;
        this.variety = variety;
        this.groups = groups;
        this.exempt = exempt;
        this.repeatMessage = repeatMessage;
        this.varietyMessage = varietyMessage;
    }

    public static final class Group {
        final String text;
        final ItemMatch match;

        Group(String text, ItemMatch match) {
            this.text = text;
            this.match = match;
        }
    }

    public static final class Tier {
        public final int distinct;
        public final float multiplier;
        public final List<PotionEffectDef> effects;
        public final String runs;

        Tier(int distinct, float multiplier, List<PotionEffectDef> effects, String runs) {
            this.distinct = distinct;
            this.multiplier = multiplier;
            this.effects = effects;
            this.runs = runs;
        }
    }

    public static DietDef parse(ResourceLocation file, JsonObject json) {
        int memory = json.has("memory") ? whole(json.get("memory"), "memory") : DEFAULT_MEMORY;
        float[] repeat = {1.0F};
        if (json.has("repeat")) {
            JsonArray array = array(json.get("repeat"), "repeat");
            if (array.size() == 0) { throw new IllegalArgumentException("repeat names no multipliers"); }
            repeat = new float[array.size()];
            for (int i = 0; i < repeat.length; i++) { repeat[i] = (float) number(array.get(i), "repeat", 0.0D); }
        }
        List<Tier> variety = new ArrayList<>();
        if (json.has("variety")) {
            for (JsonElement element : array(json.get("variety"), "variety")) {
                if (!element.isJsonObject()) { throw new IllegalArgumentException("variety entries must be objects"); }
                JsonObject tier = element.getAsJsonObject();
                List<PotionEffectDef> effects = tier.has("effects") ? ContentParser.effects(file, array(tier.get("effects"), "effects"), 1200, false) : new ArrayList<>();
                String runs = tier.has("runs") ? text(tier.get("runs"), "runs") : "";
                variety.add(new Tier(whole(tier.get("distinct"), "distinct"), (float) number(tier.get("multiplier"), "multiplier", 0.0D), effects, runs));
            }
        }
        List<ItemMatch> exempt = new ArrayList<>();
        if (json.has("exempt")) {
            for (JsonElement element : array(json.get("exempt"), "exempt")) {
                String text = text(element, "exempt entry");
                ItemMatch match = ItemMatch.parse(file, text);
                if (match == null) { ContentLog.LOGGER.warn("Diet file {} has an exempt entry '{}' that matches nothing, so that entry is skipped", file, text); }
                else { exempt.add(match); }
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
        return new DietDef(memory, repeat, variety, groups, exempt, repeatMessage, varietyMessage);
    }

    public boolean exempts(ItemStack stack) {
        for (ItemMatch match : exempt) { if (match.matches(stack)) { return true; } }
        return false;
    }

    private static String text(JsonElement element, String name) {
        if (element == null || !element.isJsonPrimitive() || !element.getAsJsonPrimitive().isString()) { throw new IllegalArgumentException(name + " must be text"); }
        return element.getAsString().trim();
    }

    public String kind(ItemStack stack, String registryKey) {
        for (Group group : groups) { if (group.match.matches(stack)) { return group.text; } }
        return registryKey;
    }

    private static String message(JsonObject messages, String name, String fallback) {
        if (!messages.has(name)) { return fallback; }
        JsonElement element = messages.get(name);
        if (!element.isJsonPrimitive() || !element.getAsJsonPrimitive().isString()) { throw new IllegalArgumentException("messages." + name + " must be text"); }
        return element.getAsString().trim();
    }

    public boolean hasTier(int distinct) {
        for (Tier tier : variety) { if (tier.distinct == distinct) { return true; } }
        return false;
    }

    public float repeatMultiplier(int count) { return repeat[Math.min(count, repeat.length - 1)]; }

    public float varietyMultiplier(int distinct) {
        Tier best = bestTier(distinct);
        return best == null ? 1.0F : best.multiplier;
    }

    @Nullable public Tier bestTier(int distinct) {
        Tier best = null;
        for (Tier tier : variety) {
            if (tier.distinct <= distinct && (best == null || tier.distinct > best.distinct)) { best = tier; }
        }
        return best;
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
