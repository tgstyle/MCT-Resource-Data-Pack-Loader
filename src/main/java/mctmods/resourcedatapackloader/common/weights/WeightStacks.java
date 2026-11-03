package mctmods.resourcedatapackloader.common.weights;

import mctmods.resourcedatapackloader.common.pack.definitions.weights.WeightDef;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;

public final class WeightStacks {
    private static final Map<Item, Change> CHANGED = new IdentityHashMap<>();
    private static volatile Map<Item, Integer> wanted = Collections.emptyMap();

    private WeightStacks() {}

    public static void apply() {
        restore();
        wanted = compute();
        reinstall();
    }

    public static void reinstall() {
        restore();
        for (Map.Entry<Item, Integer> entry : wanted.entrySet()) {
            Item item = entry.getKey();
            int current = item.components().getOrDefault(DataComponents.MAX_STACK_SIZE, 1);
            if (entry.getValue() >= current) { continue; }
            CHANGED.put(item, new Change(current, entry.getValue()));
            set(item, entry.getValue());
        }
    }

    public static void release() { restore(); }

    private static Map<Item, Integer> compute() {
        WeightDef settings = ContentWeights.settings();
        if (settings.stackBudget <= 0) { return Collections.emptyMap(); }
        Map<Item, Integer> targets = new IdentityHashMap<>();
        for (Item item : BuiltInRegistries.ITEM) {
            int weight = ContentWeights.heaviest(item);
            int current = item.components().getOrDefault(DataComponents.MAX_STACK_SIZE, 1);
            if (weight <= 0 || current <= 1 || item.components().has(DataComponents.MAX_DAMAGE)) { continue; }
            int target = Mth.clamp(settings.stackBudget / weight, settings.stackBudgetMin, current);
            if (target < current) { targets.put(item, target); }
        }
        return targets;
    }

    private static void restore() {
        for (Map.Entry<Item, Change> entry : CHANGED.entrySet()) {
            Item item = entry.getKey();
            if (item.components().getOrDefault(DataComponents.MAX_STACK_SIZE, 1) == entry.getValue().applied()) { set(item, entry.getValue().original()); }
        }
        CHANGED.clear();
    }

    private static void set(Item item, int size) { item.components = DataComponentMap.builder().addAll(item.components()).set(DataComponents.MAX_STACK_SIZE, size).build(); }

    private record Change(int original, int applied) {}
}
