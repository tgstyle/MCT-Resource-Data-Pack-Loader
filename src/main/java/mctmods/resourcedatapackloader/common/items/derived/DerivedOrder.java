package mctmods.resourcedatapackloader.common.items.derived;

import mctmods.resourcedatapackloader.common.pack.definitions.items.DerivedDef;
import mctmods.resourcedatapackloader.common.pack.definitions.items.ItemDef;
import mctmods.resourcedatapackloader.core.util.ContentLog;

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
import net.minecraft.resources.ResourceLocation;

final class DerivedOrder {
    private final Map<ResourceLocation, ItemDef> byKey = new LinkedHashMap<>();
    private final Map<ResourceLocation, Boolean> done = new HashMap<>();
    private final Set<ResourceLocation> refused = new HashSet<>();
    private final List<ResourceLocation> walking = new ArrayList<>();
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
        ResourceLocation key = def.key();
        if (done.containsKey(key)) { return; }
        done.put(key, false);
        walking.add(key);
        for (ResourceLocation need : needs(def)) {
            ItemDef other = byKey.get(need);
            if (other == null || other == def) { continue; }
            if (!done.containsKey(need)) { visit(other); }
            else if (!done.get(need)) { loop(need); }
        }
        walking.removeLast();
        done.put(key, true);
        if (!refused.contains(key)) { sorted.add(def); }
    }

    private void loop(ResourceLocation start) {
        List<ResourceLocation> members = new ArrayList<>(walking.subList(walking.indexOf(start), walking.size()));
        StringBuilder text = new StringBuilder();
        for (ResourceLocation member : members) { text.append(member).append(" -> "); }
        ContentLog.LOGGER.error("Derived items depend on each other in a loop, {}{}, leaving them out", text, start);
        refused.addAll(members);
    }

    private static List<ResourceLocation> needs(ItemDef def) {
        DerivedDef derived = Objects.requireNonNull(def.derived());
        List<ResourceLocation> needs = new ArrayList<>();
        needs.add(derived.base());
        reused(derived.set(), needs);
        return needs;
    }

    private static void reused(JsonObject set, List<ResourceLocation> into) {
        for (Map.Entry<String, JsonElement> entry : set.entrySet()) {
            JsonElement value = entry.getValue();
            if (value.isJsonObject() && value.getAsJsonObject().has("set") && value.getAsJsonObject().get("set").isJsonObject()) { reused(value.getAsJsonObject().getAsJsonObject("set"), into); }
            else if (value.isJsonPrimitive() && value.getAsJsonPrimitive().isString() && value.getAsString().indexOf(':') > 0) {
                ResourceLocation named = ResourceLocation.tryParse(value.getAsString());
                if (named == null) { ContentLog.LOGGER.debug("{} is not an item name", value); }
                else { into.add(named); }
            }
        }
    }
}
