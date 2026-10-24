package mctmods.resourcedatapackloader.content;

import mctmods.resourcedatapackloader.content.util.AnvilMath;
import mctmods.resourcedatapackloader.content.util.ItemMatch;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AnvilUpdateEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.annotation.Nullable;

public final class AnvilRules {
    private static final String MATCH = "match";
    private static final String REPAIR_WITH = "repairWith";
    private static final String PER_UNIT = "repairPerUnit";
    private static final String PENALTY = "penalty";
    private static final String MULTIPLIER = "multiplier";
    private static final String FACTOR = "factor";
    private static final double MOST_MULTIPLIED = 100.0D;
    private static final int MOST_FACTOR = 16;
    private static final List<Entry> RULES = new ArrayList<>();
    private static final ThreadLocal<EntityPlayer> WORKER = new ThreadLocal<>();
    private static final ThreadLocal<Integer> REFUSED = new ThreadLocal<>();

    private AnvilRules() {}

    private static final class Entry {
        private final ItemMatch match;
        private final AnvilMath.Rule rule;

        private Entry(ItemMatch match, AnvilMath.Rule rule) {
            this.match = match;
            this.rule = rule;
        }
    }

    static void reset() { RULES.clear(); }

    static int count() { return RULES.size(); }

    static boolean names(JsonObject json) { return json.has(MATCH); }

    static void read(ResourceLocation key, JsonObject json) {
        ItemMatch match = ItemMatch.parse(key, Json.text(json, MATCH, ""));
        if (match == null) { return; }
        ItemMatch with = null;
        if (json.has(REPAIR_WITH)) {
            with = ItemMatch.parse(key, Json.text(json, REPAIR_WITH, ""));
            if (with == null) { return; }
        }
        AnvilMath.Rule rule = new AnvilMath.Rule(with, Json.ranged(json, PER_UNIT, 0.0F, Float.MIN_VALUE, 1.0F, key), cost(key, json, "repairCost"), cost(key, json, "combineCost"), cost(key, json, "bookCost"), growth(key, json), Json.getBoolean(json, "repair", true), Json.getBoolean(json, "combine", true));
        RULES.add(new Entry(match, rule));
    }

    private static AnvilMath.Cost cost(ResourceLocation key, JsonObject json, String name) {
        if (!json.has(name)) { return AnvilMath.VANILLA_COST; }
        JsonElement held = json.get(name);
        if (held.isJsonPrimitive() && held.getAsJsonPrimitive().isNumber()) {
            double flat = held.getAsDouble();
            if (flat >= 1 && flat <= AnvilCosts.HIGHEST && flat == Math.rint(flat)) { return new AnvilMath.Cost((int) flat, 1.0D); }
        }
        else if (held.isJsonObject() && held.getAsJsonObject().size() == 1 && held.getAsJsonObject().has(MULTIPLIER)) {
            JsonElement times = held.getAsJsonObject().get(MULTIPLIER);
            if (times.isJsonPrimitive() && times.getAsJsonPrimitive().isNumber() && times.getAsDouble() > 0.0D && times.getAsDouble() <= MOST_MULTIPLIED) { return new AnvilMath.Cost(-1, times.getAsDouble()); }
        }
        ContentLog.LOGGER.error("Anvil rule {} gives {} the value {}, which is neither a whole number of levels from 1 to {} nor {\"{}\": a number above 0 up to {}}, so vanilla's cost is used", key, name, held, AnvilCosts.HIGHEST, MULTIPLIER, MOST_MULTIPLIED);
        return AnvilMath.VANILLA_COST;
    }

    private static int growth(ResourceLocation key, JsonObject json) {
        if (!json.has(PENALTY)) { return AnvilMath.VANILLA_GROWTH; }
        JsonElement held = json.get(PENALTY);
        if (held.isJsonPrimitive() && held.getAsJsonPrimitive().isString()) {
            String named = held.getAsString().trim().toLowerCase(Locale.ROOT);
            if ("vanilla".equals(named)) { return AnvilMath.VANILLA_GROWTH; }
            if ("none".equals(named)) { return AnvilMath.NO_GROWTH; }
        }
        else if (held.isJsonObject() && held.getAsJsonObject().has(FACTOR)) {
            JsonElement factor = held.getAsJsonObject().get(FACTOR);
            if (factor.isJsonPrimitive() && factor.getAsJsonPrimitive().isNumber() && factor.getAsDouble() == Math.rint(factor.getAsDouble()) && factor.getAsInt() >= 0 && factor.getAsInt() <= MOST_FACTOR) { return factor.getAsInt(); }
        }
        ContentLog.LOGGER.error("Anvil rule {} gives {} the value {}, which is not \"vanilla\", \"none\" or {\"{}\": a whole number from 0 to {}}, so vanilla's penalty is used", key, PENALTY, held, FACTOR, MOST_FACTOR);
        return AnvilMath.VANILLA_GROWTH;
    }

    @Nullable static EntityPlayer worker() { return WORKER.get(); }

    public static void begin(EntityPlayer player) {
        WORKER.set(player);
        REFUSED.remove();
    }

    @Nullable public static Integer end() {
        WORKER.remove();
        Integer refused = REFUSED.get();
        REFUSED.remove();
        return refused;
    }

    static void apply(AnvilUpdateEvent event) {
        EntityPlayer player = WORKER.get();
        if (RULES.isEmpty() || player == null || event.getLeft().isEmpty() || event.getRight().isEmpty()) { return; }
        Entry entry = ItemMatch.pick(RULES, each -> each.match, event.getLeft());
        if (entry == null) { return; }
        AnvilMath.Worked worked = AnvilMath.work(event.getLeft(), event.getRight(), event.getName(), player, entry.rule);
        if (worked.output.isEmpty()) {
            REFUSED.set(worked.cost);
            event.setCanceled(true);
            return;
        }
        event.setOutput(worked.output);
        event.setCost(worked.cost);
        event.setMaterialCost(worked.material);
    }
}
