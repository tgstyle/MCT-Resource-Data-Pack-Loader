package mctmods.resourcedatapackloader.common.weights;

import mctmods.resourcedatapackloader.common.pack.definitions.weights.WeightDef;
import mctmods.resourcedatapackloader.common.util.ItemHolders;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeMap;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;

public final class WeightStacks {
    private static final Map<Item, Change> CHANGED = new IdentityHashMap<>();
    private static volatile Map<Item, Integer> wanted = Collections.emptyMap();
    private static volatile Map<Item, Integer> floors = Collections.emptyMap();
    private static volatile RecipeMap loaded;

    private WeightStacks() {}

    public static synchronized void apply() {
        restore();
        wanted = compute();
        install();
    }

    public static synchronized void reinstall() {
        restore();
        install();
    }

    public static synchronized void release() { restore(); }

    public static void recipeFloors(RecipeMap recipes) { loaded = recipes; }

    public static void bindFloors() {
        RecipeMap recipes = loaded;
        if (recipes == null) { return; }
        loaded = null;
        Map<Item, Integer> found = new IdentityHashMap<>();
        for (RecipeHolder<?> holder : recipes.values()) {
            for (RecipeDisplay display : holder.value().display()) {
                if (display.result() instanceof SlotDisplay.ItemStackSlotDisplay(ItemStackTemplate stack)) { found.merge(stack.item().value(), stack.count(), Math::max); }
            }
        }
        floors = found;
    }

    private static Map<Item, Integer> compute() {
        WeightDef settings = ContentWeights.settings();
        Map<Item, Integer> targets = new IdentityHashMap<>();
        if (settings.stackBudget <= 0) { return targets; }
        for (Item item : BuiltInRegistries.ITEM) {
            if (!ItemHolders.of(item).areComponentsBound()) { continue; }
            DataComponentMap components = item.components();
            int weight = ContentWeights.heaviest(item);
            int current = components.getOrDefault(DataComponents.MAX_STACK_SIZE, 1);
            if (weight <= 0 || current <= 1 || components.has(DataComponents.MAX_DAMAGE)) { continue; }
            int budget = Math.clamp(settings.stackBudget / weight, Math.min(settings.stackBudgetMin, current), current);
            int stock = Math.max(floors.getOrDefault(item, 0), budget);
            targets.put(item, Math.min(stock, current));
        }
        return targets;
    }

    private static void install() {
        for (Map.Entry<Item, Integer> entry : wanted.entrySet()) {
            Item item = entry.getKey();
            Holder.Reference<Item> holder = ItemHolders.of(item);
            if (!holder.areComponentsBound()) { continue; }
            DataComponentMap original = item.components();
            int current = original.getOrDefault(DataComponents.MAX_STACK_SIZE, 1);
            if (current <= 1 || entry.getValue() >= current || original.has(DataComponents.MAX_DAMAGE)) { continue; }
            DataComponentMap applied = DataComponentMap.builder().addAll(original).set(DataComponents.MAX_STACK_SIZE, entry.getValue()).build();
            holder.bindComponents(applied);
            CHANGED.put(item, new Change(original, applied));
        }
    }

    private static void restore() {
        for (Map.Entry<Item, Change> entry : CHANGED.entrySet()) {
            Item item = entry.getKey();
            if (item.components() == entry.getValue().applied()) { ItemHolders.of(item).bindComponents(entry.getValue().original()); }
        }
        CHANGED.clear();
    }

    private record Change(DataComponentMap original, DataComponentMap applied) {}
}
