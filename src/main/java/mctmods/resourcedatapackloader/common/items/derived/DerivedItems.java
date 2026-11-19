package mctmods.resourcedatapackloader.common.items.derived;

import mctmods.resourcedatapackloader.common.pack.definitions.items.DerivedDef;
import mctmods.resourcedatapackloader.common.pack.definitions.items.ItemDef;
import mctmods.resourcedatapackloader.common.pack.loading.ContentRegistry;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.Summary;

import com.google.gson.JsonElement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.registries.RegisterEvent;

public final class DerivedItems {
    private DerivedItems() {}

    public static int register(RegisterEvent.RegisterHelper<Item> helper) {
        List<ItemDef> defs = new ArrayList<>();
        for (ItemDef def : ContentRegistry.itemDefs()) {
            if (def.derived() != null) { defs.add(def); }
        }
        int made = 0;
        for (ItemDef def : DerivedOrder.sort(defs)) {
            Identifier key = def.key();
            if (!ContentRegistry.available(def.requires(), key)) { continue; }
            if (BuiltInRegistries.ITEM.containsKey(key)) {
                ContentLog.LOGGER.warn("An item named {} is already registered, skipping the pack definition", key);
                continue;
            }
            try {
                if (build(def, helper)) { made++; }
            }
            catch (RuntimeException e) { ContentLog.LOGGER.error("Derived item {} could not be made, leaving it out", key, e); }
        }
        if (made > 0) { Summary.info("items.derived", "Registered " + made + " derived item(s) from packs"); }
        return made;
    }

    public static void resolveLate() {
        DerivedConstants.resolveAll();
        DerivedAdds.run();
    }

    public static void verify() {
        for (ItemDef def : ContentRegistry.itemDefs()) {
            DerivedDef derived = def.derived();
            if (derived == null) { continue; }
            Item item = BuiltInRegistries.ITEM.getOptional(def.key()).orElse(null);
            Item base = BuiltInRegistries.ITEM.getOptional(derived.base()).orElse(null);
            if (item == null || base == null) { continue; }
            int removed = 0;
            for (Map.Entry<String, JsonElement> entry : derived.components().entrySet()) {
                if (entry.getKey().startsWith("!")) { removed++; }
            }
            if (!item.components().has(DataComponents.ITEM_NAME) || !item.components().has(DataComponents.ITEM_MODEL) || item.components().size() < base.components().size() - removed) {
                ContentLog.LOGGER.error("Derived item {} did not get the components of {}, it may be missing its name, model or other data", def.key(), derived.base());
            }
        }
    }

    private static boolean build(ItemDef def, RegisterEvent.RegisterHelper<Item> helper) {
        Identifier key = def.key();
        DerivedDef derived = Objects.requireNonNull(def.derived());
        Item base = BuiltInRegistries.ITEM.getOptional(derived.base()).orElse(null);
        if (base == null || base == Items.AIR) {
            ContentLog.LOGGER.error("Derived item {} is based on {}, which nothing registers, leaving it out", key, derived.base());
            return false;
        }
        if (base instanceof BlockItem) {
            ContentLog.LOGGER.error("Derived item {} is based on {}, a block item, and derived blocks are not supported, leaving it out", key, derived.base());
            return false;
        }
        List<ItemClassMaker.Override> overrides = ItemOverrides.plan(base.getClass(), key, derived.returns(), base);
        Class<?> type = ItemClassMaker.make(base.getClass(), key, overrides);
        if (type == null) { return false; }
        ItemOverrides.store(key, overrides);
        Item item = ItemCopier.copy(base, type, key, def);
        ObjectPatch.apply(item, derived.set(), derived.addTo(), key, "");
        helper.register(key, item);
        ContentRegistry.addItem(key, item, def, null, null, def.creativeTab());
        return true;
    }
}
