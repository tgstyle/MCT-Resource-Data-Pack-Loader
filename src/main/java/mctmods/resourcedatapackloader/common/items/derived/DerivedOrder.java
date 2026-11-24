package mctmods.resourcedatapackloader.common.items.derived;

import mctmods.resourcedatapackloader.common.pack.definitions.items.DerivedDef;
import mctmods.resourcedatapackloader.common.pack.definitions.items.ItemDef;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.json.Json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import net.minecraft.resources.Identifier;

final class DerivedOrder {
    private final Map<Identifier, ItemDef> byKey = new LinkedHashMap<>();
    private final Map<Identifier, Boolean> done = new HashMap<>();
    private final Set<Identifier> refused = new HashSet<>();
    private final List<Identifier> walking = new ArrayList<>();
    private final List<ItemDef> sorted = new ArrayList<>();

    private DerivedOrder(List<ItemDef> defs) {
        for (ItemDef def : defs) { byKey.put(def.key(), def); }
    }

    static List<ItemDef> sort(List<ItemDef> defs) {
        DerivedOrder order = new DerivedOrder(defs);
        for (ItemDef def : defs) { order.visit(def); }
        return order.sorted;
    }

    private void visit(ItemDef def) {
        Identifier key = def.key();
        if (done.containsKey(key)) { return; }
        done.put(key, false);
        walking.add(key);
        for (Identifier need : needs(def)) {
            ItemDef other = byKey.get(need);
            if (other == null || other == def) { continue; }
            if (!done.containsKey(need)) { visit(other); }
            else if (!done.get(need)) { loop(need); }
        }
        walking.removeLast();
        done.put(key, true);
        if (!refused.contains(key)) { sorted.add(def); }
    }

    private void loop(Identifier start) {
        List<Identifier> members = new ArrayList<>(walking.subList(walking.indexOf(start), walking.size()));
        StringBuilder text = new StringBuilder();
        for (Identifier member : members) { text.append(member).append(" -> "); }
        ContentLog.LOGGER.error("Derived items depend on each other in a loop, {}{}, leaving them out", text, start);
        refused.addAll(members);
    }

    private static List<Identifier> needs(ItemDef def) {
        DerivedDef derived = Objects.requireNonNull(def.derived());
        List<Identifier> needs = new ArrayList<>();
        needs.add(derived.base());
        reused(derived.set(), needs);
        return needs;
    }

    private static void reused(JsonObject set, List<Identifier> into) {
        for (Map.Entry<String, JsonElement> entry : set.entrySet()) {
            JsonElement value = entry.getValue();
            if (value.isJsonObject()) { reused(Json.object(value.getAsJsonObject(), ObjectPatch.SET), into); }
            else if (Json.isString(value) && value.getAsString().indexOf(':') > 0) {
                try { into.add(Identifier.parse(value.getAsString())); }
                catch (RuntimeException notAName) { ContentLog.LOGGER.debug("{} is not an item name", value); }
            }
        }
    }
}
