package mctmods.resourcedatapackloader.content.def;

import mctmods.resourcedatapackloader.content.ContentDiet;
import mctmods.resourcedatapackloader.content.util.ItemMatch;
import mctmods.resourcedatapackloader.util.Json;
import mctmods.resourcedatapackloader.util.Standing;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.Identifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;

public final class HeartReward {
    public enum Trigger { ADVANCEMENT, DIET, FOODS, CONDITION }

    public static final String DEFAULT_MESSAGE = "rdpl.hearts.gained";
    public static final String EATEN = "eaten/";
    private static final String ALWAYS = "always";
    private static final List<String> REPEATS = Arrays.asList("once_per_player", ALWAYS);
    public final String key;
    public final String eatenKey;
    public final Trigger trigger;
    @Nullable public final Identifier advancement;
    public final int diet;
    public final List<String> foods;
    public final List<ItemMatch> matches;
    public final int need;
    public final Standing standing;
    public final float hearts;
    public final int limit;
    public final String message;
    public final List<String> requires;

    private HeartReward(String file, String id, Trigger trigger, JsonObject json, Identifier owner) {
        this.key = file + "/" + id;
        this.eatenKey = EATEN + key;
        this.trigger = trigger;
        this.advancement = trigger == Trigger.ADVANCEMENT ? advancement(json) : null;
        this.diet = trigger == Trigger.DIET ? tier(json) : 0;
        this.foods = trigger == Trigger.FOODS ? foodNames(json) : Collections.emptyList();
        this.matches = trigger == Trigger.FOODS ? foodMatches(owner, foods) : Collections.emptyList();
        this.need = trigger == Trigger.FOODS ? need(json, foods.size()) : 1;
        this.standing = trigger == Trigger.CONDITION ? condition(json) : Standing.NONE;
        this.hearts = hearts(json);
        boolean always = ALWAYS.equals(Json.choice(json, "repeat", REPEATS.getFirst(), key, REPEATS));
        this.limit = trigger == Trigger.FOODS || !always ? 1 : json.has("times") ? times(json) : Integer.MAX_VALUE;
        this.message = Json.getString(json, "message", DEFAULT_MESSAGE).trim();
        this.requires = Json.strings(json, "requires");
    }

    public static HeartReward parse(Identifier file, JsonObject json, List<String> seen) {
        if (!json.has("id")) { throw new IllegalArgumentException("a reward has no id"); }
        String id = Json.getString(json, "id").trim();
        if (id.isEmpty() || seen.contains(id)) { throw new IllegalArgumentException("reward id '" + id + "' is empty or used twice in the file"); }
        seen.add(id);
        try { return new HeartReward(file.toString(), id, trigger(json), json, file); }
        catch (RuntimeException ex) { throw new IllegalArgumentException("reward '" + id + "': " + ex.getMessage()); }
    }

    private static Trigger trigger(JsonObject json) {
        List<Trigger> found = new ArrayList<>();
        if (json.has("advancement")) { found.add(Trigger.ADVANCEMENT); }
        if (json.has("diet")) { found.add(Trigger.DIET); }
        if (json.has("foods")) { found.add(Trigger.FOODS); }
        if (json.has("team") || json.has("objective") || json.has("scoreAtLeast") || json.has("dietAtLeast")) { found.add(Trigger.CONDITION); }
        if (found.size() != 1) { throw new IllegalArgumentException("needs exactly one of advancement, diet, foods or team/objective/scoreAtLeast/dietAtLeast, found " + found.size()); }
        return found.getFirst();
    }

    private static Identifier advancement(JsonObject json) {
        String name = Json.getString(json, "advancement").trim();
        Identifier id = Identifier.tryParse(name);
        if (id == null) { throw new IllegalArgumentException("advancement '" + name + "' is not a valid id"); }
        return id;
    }

    private static int tier(JsonObject json) {
        int distinct = Json.getInt(json, "diet");
        if (!ContentDiet.hasTier(distinct)) { throw new IllegalArgumentException("diet " + distinct + " is no variety tier of a loaded diet file"); }
        return distinct;
    }

    private static List<String> foodNames(JsonObject json) {
        JsonArray array = Json.getJsonArray(json, "foods");
        List<String> names = new ArrayList<>();
        for (JsonElement element : array) { names.add(Json.getString(element, "foods entry").trim()); }
        if (names.isEmpty()) { throw new IllegalArgumentException("foods names no food"); }
        return Collections.unmodifiableList(names);
    }

    private static List<ItemMatch> foodMatches(Identifier owner, List<String> names) {
        List<ItemMatch> matches = new ArrayList<>();
        for (String name : names) {
            ItemMatch match = ItemMatch.parse(owner, name);
            if (match == null) { throw new IllegalArgumentException("foods entry '" + name + "' matches nothing"); }
            matches.add(match);
        }
        return Collections.unmodifiableList(matches);
    }

    private static int need(JsonObject json, int size) {
        int count = json.has("count") ? Json.getInt(json, "count") : size;
        if (count < 1 || count > size) { throw new IllegalArgumentException("count must be from 1 to " + size); }
        return count;
    }

    private static Standing condition(JsonObject json) {
        if (!json.has("team") && !json.has("dietAtLeast") && !(json.has("objective") && json.has("scoreAtLeast"))) { throw new IllegalArgumentException("a condition needs team, dietAtLeast, or objective with scoreAtLeast"); }
        return Standing.parse(json);
    }

    private static float hearts(JsonObject json) {
        if (!json.has("hearts")) { throw new IllegalArgumentException("hearts is required"); }
        float hearts = Json.getFloat(json, "hearts");
        if (hearts <= 0.0F || !halves(hearts)) { throw new IllegalArgumentException("hearts must be above 0 in steps of 0.5"); }
        return hearts;
    }

    private static int times(JsonObject json) {
        int times = Json.getInt(json, "times");
        if (times < 1) { throw new IllegalArgumentException("times must be 1 or more"); }
        return times;
    }

    public static boolean halves(float hearts) { return hearts * 2.0F == Math.rint(hearts * 2.0F); }
}
