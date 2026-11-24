package mctmods.resourcedatapackloader.common.items.derived;

import mctmods.resourcedatapackloader.common.blocks.ContentSetup;
import mctmods.resourcedatapackloader.common.pack.definitions.items.DerivedDef;
import mctmods.resourcedatapackloader.common.pack.definitions.items.ItemDef;
import mctmods.resourcedatapackloader.common.pack.loading.ContentOwners;
import mctmods.resourcedatapackloader.common.pack.loading.ContentRegistry;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.Registries;
import mctmods.resourcedatapackloader.core.util.Summary;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

public final class DerivedItems {
    private static final String DISPLAY_NAME = "getItemStackDisplayName";

    private DerivedItems() {}

    public static void register(IForgeRegistry<Item> registry) {
        List<ItemDef> defs = new ArrayList<>();
        for (ItemDef def : ContentRegistry.itemDefs()) {
            if (def.derived != null) { defs.add(def); }
        }
        List<Item> made = new ArrayList<>();
        for (ItemDef def : DerivedOrder.sort(defs)) {
            ResourceLocation key = def.registryName;
            if (!ContentRegistry.available(def.requires, key)) { continue; }
            if (ForgeRegistries.ITEMS.containsKey(key)) {
                ContentLog.LOGGER.warn("An item named {} is already registered, skipping the pack definition", key);
                continue;
            }
            ContentOwners.as(key.getNamespace(), () -> {
                try {
                    Item item = build(def, registry);
                    if (item != null) { made.add(item); }
                }
                catch (RuntimeException e) { DerivedLog.skip(key, "could not be made", e); }
            });
        }
        if (!made.isEmpty()) { Summary.info("items.derived", "Registered " + made.size() + " derived item(s) from packs"); }
    }

    public static void resolveLate() {
        DerivedConstants.resolveAll();
        DerivedAdds.run();
    }

    private static Item build(ItemDef def, IForgeRegistry<Item> registry) {
        ResourceLocation key = def.registryName;
        DerivedDef derived = Objects.requireNonNull(def.derived);
        Item base = Registries.find(ForgeRegistries.ITEMS, derived.base);
        if (base == null) {
            DerivedLog.skip(key, "is based on {}, which nothing registers", derived.base);
            return null;
        }
        if (base instanceof ItemBlock) {
            DerivedLog.skip(key, "is based on {}, a block item, and derived blocks are not supported", derived.base);
            return null;
        }
        List<ItemClassMaker.Override> overrides = ItemOverrides.plan(base.getClass(), key, returns(derived, key), base);
        Class<?> type = ItemOverrides.makeStored(base.getClass(), key, overrides);
        if (type == null) { return null; }
        Item item = ItemCopier.copy(base, type, key);
        ObjectPatch.apply(item, derived.set, derived.addTo, key, "");
        ContentSetup.apply(item, def.creativeTab);
        registry.register(item);
        ContentRegistry.indexItem(key, item);
        return item;
    }

    private static JsonObject returns(DerivedDef derived, ResourceLocation key) {
        if (derived.name.isEmpty()) { return derived.returns; }
        for (Map.Entry<String, JsonElement> entry : derived.returns.entrySet()) {
            if (entry.getKey().startsWith(DISPLAY_NAME)) {
                ContentLog.LOGGER.warn("Derived item {} gives both a name and returns for {}, the returns wins", key, DISPLAY_NAME);
                return derived.returns;
            }
        }
        JsonObject all = new JsonObject();
        for (Map.Entry<String, JsonElement> entry : derived.returns.entrySet()) { all.add(entry.getKey(), entry.getValue()); }
        all.add(DISPLAY_NAME + "(ItemStack)", new JsonPrimitive(derived.name));
        return all;
    }
}
