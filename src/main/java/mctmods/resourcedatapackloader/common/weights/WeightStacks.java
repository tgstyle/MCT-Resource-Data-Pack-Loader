package mctmods.resourcedatapackloader.common.weights;

import mctmods.resourcedatapackloader.common.pack.definitions.weights.WeightDef;

import java.util.IdentityHashMap;
import java.util.Map;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public final class WeightStacks {
    private static final Map<Item, Change> CHANGED = new IdentityHashMap<>();

    private WeightStacks() {}

    public static void apply() {
        restore();
        WeightDef settings = ContentWeights.settings();
        if (settings.stackBudget <= 0) { return; }
        for (Item item : ForgeRegistries.ITEMS) {
            int weight = ContentWeights.heaviest(item);
            int current = item.maxStackSize;
            if (weight <= 0 || current <= 1 || item.isDamageable()) { continue; }
            int target = Math.min(current, Math.max(settings.stackBudgetMin, settings.stackBudget / weight));
            if (target == current) { continue; }
            CHANGED.put(item, new Change(current, target));
            item.setMaxStackSize(target);
        }
    }

    private static void restore() {
        for (Map.Entry<Item, Change> entry : CHANGED.entrySet()) {
            Item item = entry.getKey();
            if (item.maxStackSize == entry.getValue().applied) { item.setMaxStackSize(entry.getValue().original); }
        }
        CHANGED.clear();
    }

    private static final class Change {
        final int original;
        final int applied;

        Change(int original, int applied) {
            this.original = original;
            this.applied = applied;
        }
    }
}
