package mctmods.resourcedatapackloader.common.items.derived;

import mctmods.resourcedatapackloader.common.pack.definitions.items.DerivedDef;
import mctmods.resourcedatapackloader.common.pack.definitions.items.ItemDef;
import mctmods.resourcedatapackloader.common.pack.loading.ContentRegistry;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.Summary;
import mctmods.resourcedatapackloader.core.util.concurrent.Registered;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;

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
            if (ForgeRegistries.ITEMS.containsKey(key)) {
                ContentLog.LOGGER.warn("An item named {} is already registered, skipping the pack definition", key);
                continue;
            }
            try { if (build(def, helper)) { made++; } }
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
        Item base = Registered.find(ForgeRegistries.ITEMS, derived.base());
        if (base == null) {
            ContentLog.LOGGER.error("Derived item {} is based on {}, which nothing registers, leaving it out", key, derived.base());
            return false;
        }
        if (base instanceof BlockItem) {
            ContentLog.LOGGER.error("Derived item {} is based on {}, a block item, and derived blocks are not supported, leaving it out", key, derived.base());
            return false;
        }
        List<ItemClassMaker.Override> overrides = ItemOverrides.plan(base.getClass(), key, returns(derived, key), base);
        Class<?> type = ItemClassMaker.make(base.getClass(), key, overrides);
        if (type == null) { return false; }
        ItemOverrides.store(key, overrides);
        Item item = ItemCopier.copy(base, type, key);
        ObjectPatch.apply(item, derived.set(), derived.addTo(), key, "");
        ItemCopier.attach(item);
        try { helper.register(key, item); }
        catch (RuntimeException e) {
            ItemCopier.release(item);
            throw e;
        }
        ContentRegistry.addItem(key, item, def, null, null, def.creativeTab());
        return true;
    }

    private static JsonObject returns(DerivedDef derived, ResourceLocation key) {
        if (derived.name().isEmpty()) { return derived.returns(); }
        for (Map.Entry<String, JsonElement> entry : derived.returns().entrySet()) {
            if (entry.getKey().startsWith(DISPLAY_NAME)) {
                ContentLog.LOGGER.warn("Derived item {} gives both a name and returns for {}, the returns wins", key, DISPLAY_NAME);
                return derived.returns();
            }
        }
        JsonObject all = new JsonObject();
        for (Map.Entry<String, JsonElement> entry : derived.returns().entrySet()) { all.add(entry.getKey(), entry.getValue()); }
        all.add(DISPLAY_NAME + "(ItemStack)", new JsonPrimitive(derived.name()));
        return all;
    }
}
