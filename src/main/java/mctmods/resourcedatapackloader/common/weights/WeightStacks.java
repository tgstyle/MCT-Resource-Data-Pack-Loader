package mctmods.resourcedatapackloader.common.weights;

import mctmods.resourcedatapackloader.common.pack.definitions.weights.WeightDef;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;

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
        wanted.forEach((item, target) -> {
            int current = item.maxStackSize;
            if (target >= current) { return; }
            CHANGED.put(item, new Change(current, target));
            item.maxStackSize = target;
        });
    }

    public static void release() { restore(); }

    private static Map<Item, Integer> compute() {
        WeightDef settings = ContentWeights.settings();
        if (settings.stackBudget <= 0) { return Collections.emptyMap(); }
        Map<Item, Integer> found = new IdentityHashMap<>();
        for (Item item : ForgeRegistries.ITEMS) {
            int weight = ContentWeights.heaviest(item);
            int current = item.maxStackSize;
            if (weight <= 0 || current <= 1 || item.canBeDepleted()) { continue; }
            int target = Math.min(current, Math.max(settings.stackBudgetMin, settings.stackBudget / weight));
            if (target != current) { found.put(item, target); }
        }
        return found;
    }

    private static void restore() {
        CHANGED.forEach((item, change) -> {
            if (item.maxStackSize == change.applied) { item.maxStackSize = change.original; }
        });
        CHANGED.clear();
    }

    private record Change(int original, int applied) {}
}
