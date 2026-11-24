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
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.registries.RegisterEvent;

public final class DerivedItems {
    private static final String DISPLAY_NAME = "getName";

    private DerivedItems() {}

    public static int register(RegisterEvent.RegisterHelper<Item> helper) {
        List<ItemDef> defs = new ArrayList<>();
        for (ItemDef def : ContentRegistry.itemDefs()) {
            if (def.derived() != null) { defs.add(def); }
        }
        int made = 0;
        for (ItemDef def : DerivedOrder.sort(defs)) {
            ResourceLocation key = def.key();
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

    private static boolean build(ItemDef def, RegisterEvent.RegisterHelper<Item> helper) {
        ResourceLocation key = def.key();
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
        Class<?> type = ItemOverrides.makeStored(base.getClass(), key, overrides);
        if (type == null) { return false; }
        Item item = ItemCopier.copy(base, type);
        ObjectPatch.apply(item, derived.set(), derived.addTo(), key, "");
        ItemCopier.holder(item);
        helper.register(key, item);
        ComponentPatch.remember(item, key, derived.components(), name(derived, key));
        ContentRegistry.addItem(key, item, def, null, null, def.creativeTab());
        return true;
    }

    private static String name(DerivedDef derived, ResourceLocation key) {
        if (derived.name().isEmpty()) { return ""; }
        for (Map.Entry<String, JsonElement> entry : derived.returns().entrySet()) {
            if (entry.getKey().startsWith(DISPLAY_NAME)) {
                ContentLog.LOGGER.warn("Derived item {} gives both a name and returns for {}, the returns wins", key, DISPLAY_NAME);
                return "";
            }
        }
        return derived.name();
    }
}
