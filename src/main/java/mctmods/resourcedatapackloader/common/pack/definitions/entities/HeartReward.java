package mctmods.resourcedatapackloader.common.pack.definitions.entities;

import mctmods.resourcedatapackloader.common.diet.ContentDiet;
import mctmods.resourcedatapackloader.common.util.ItemMatch;
import mctmods.resourcedatapackloader.common.util.entity.Standing;
import mctmods.resourcedatapackloader.core.util.json.Json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;

public final class HeartReward {
    public enum Trigger { ADVANCEMENT, DIET, FOODS, CONDITION, DEATH }

    public static final String DEFAULT_MESSAGE = "rdpl.hearts.gained";
    public static final String LOST_MESSAGE = "rdpl.hearts.lost";
    public static final String EATEN = "eaten/";
    public static final String LOST = "lost/";
    private static final String ONCE = "once_per_player";
    private static final String ALWAYS = "always";
    private static final String WHILE_MET = "while_met";
    private static final List<String> REPEATS = Arrays.asList(ONCE, ALWAYS);
    private static final List<String> LOSS_REPEATS = Arrays.asList(ONCE, ALWAYS, WHILE_MET);
    private static final List<String> DEATH_BY = Arrays.asList("any", "player", "mob", "world");
    public final boolean loss;
    public final boolean invert;
    public final boolean whileMet;
    public final boolean clearOnDeath;
    public final boolean clearFoodsOnDeath;
    public final String deathBy;
    public final String key;
    public final String eatenKey;
    public final Trigger trigger;
    @Nullable public final ResourceLocation advancement;
    public final int diet;
    public final List<String> foods;
    public final List<ItemMatch> matches;
    public final int need;
    public final Standing standing;
    public final float hearts;
    public final int limit;
    public final String message;
    public final List<String> requires;

    private HeartReward(String file, String id, Trigger trigger, JsonObject json, ResourceLocation owner, boolean loss) {
        this.loss = loss;
        this.key = (loss ? LOST : "") + file + "/" + id;
        this.deathBy = trigger == Trigger.DEATH ? Json.choice(json, "deathBy", DEATH_BY.get(0), key, DEATH_BY) : DEATH_BY.get(0);
        this.invert = loss && json.has("invert") && Json.getBoolean(json, "invert");
        if (invert && trigger != Trigger.CONDITION) { throw new IllegalArgumentException("invert needs a team, score or diet condition"); }
        if (invert && json.has("dietAtLeast") && !ContentDiet.loaded()) { throw new IllegalArgumentException("invert on dietAtLeast needs a diet file"); }
        String repeat = Json.choice(json, "repeat", trigger == Trigger.DEATH ? ALWAYS : ONCE, key, loss ? LOSS_REPEATS : REPEATS);
        this.whileMet = WHILE_MET.equals(repeat);
        if (whileMet && trigger != Trigger.CONDITION) { throw new IllegalArgumentException("while_met needs a team, score or diet condition"); }
        this.clearOnDeath = loss && json.has("clearOnDeath") && Json.getBoolean(json, "clearOnDeath");
        if (clearOnDeath && (whileMet || trigger == Trigger.DEATH)) { throw new IllegalArgumentException("clearOnDeath does not go with while_met or death"); }
        this.clearFoodsOnDeath = loss && json.has("clearFoodsOnDeath") && Json.getBoolean(json, "clearFoodsOnDeath");
        if (clearFoodsOnDeath && trigger != Trigger.FOODS) { throw new IllegalArgumentException("clearFoodsOnDeath needs the foods trigger"); }
        this.eatenKey = EATEN + key;
        this.trigger = trigger;
        this.advancement = trigger == Trigger.ADVANCEMENT ? new ResourceLocation(Json.getString(json, "advancement").trim()) : null;
        this.diet = trigger == Trigger.DIET ? tier(json) : 0;
        this.foods = trigger == Trigger.FOODS ? foodNames(json) : Collections.emptyList();
        this.matches = trigger == Trigger.FOODS ? foodMatches(owner, foods) : Collections.emptyList();
        this.need = trigger == Trigger.FOODS ? need(json, foods.size()) : 1;
        this.standing = trigger == Trigger.CONDITION ? condition(json) : Standing.NONE;
        this.hearts = hearts(json);
        this.limit = trigger == Trigger.FOODS && !loss || !ALWAYS.equals(repeat) ? 1 : json.has("times") ? times(json) : Integer.MAX_VALUE;
        this.message = Json.getString(json, "message", loss ? LOST_MESSAGE : DEFAULT_MESSAGE).trim();
        this.requires = Json.strings(json, "requires");
    }

    public static HeartReward parse(ResourceLocation file, JsonObject json, List<String> seen, boolean loss) {
        String noun = loss ? "loss" : "reward";
        if (!json.has("id")) { throw new IllegalArgumentException("a " + noun + " has no id"); }
        String id = Json.getString(json, "id").trim();
        if (id.isEmpty() || seen.contains(id)) { throw new IllegalArgumentException(noun + " id '" + id + "' is empty or used twice in the file"); }
        seen.add(id);
        try { return new HeartReward(file.toString(), id, trigger(json, loss), json, file, loss); }
        catch (RuntimeException ex) { throw new IllegalArgumentException(noun + " '" + id + "': " + ex.getMessage()); }
    }

    private static Trigger trigger(JsonObject json, boolean loss) {
        List<Trigger> found = new ArrayList<>();
        if (json.has("advancement")) { found.add(Trigger.ADVANCEMENT); }
        if (json.has("diet")) {
            if (loss) { throw new IllegalArgumentException("a loss cannot use diet; use dietAtLeast with invert"); }
            found.add(Trigger.DIET);
        }
        if (json.has("foods")) { found.add(Trigger.FOODS); }
        if (json.has("team") || json.has("objective") || json.has("scoreAtLeast") || json.has("dietAtLeast")) { found.add(Trigger.CONDITION); }
        if (loss && json.has("death") && Json.getBoolean(json, "death")) { found.add(Trigger.DEATH); }
        if (found.size() != 1) { throw new IllegalArgumentException("needs exactly one of advancement, diet, foods or team/objective/scoreAtLeast/dietAtLeast, found " + found.size()); }
        return found.get(0);
    }

    public boolean met(EntityPlayer player) {
        if (invert && standing.unreadable(player)) { return !whileMet; }
        return standing.earnedBy(player) != invert;
    }

    public boolean diedTo(EntityPlayer died, DamageSource source) {
        Entity killer = source.getTrueSource();
        boolean nobody = killer == null || killer == died;
        switch (deathBy) {
            case "player": return !nobody && killer instanceof EntityPlayer;
            case "mob": return !nobody && !(killer instanceof EntityPlayer);
            case "world": return nobody;
            default: return true;
        }
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

    private static List<ItemMatch> foodMatches(ResourceLocation owner, List<String> names) {
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
