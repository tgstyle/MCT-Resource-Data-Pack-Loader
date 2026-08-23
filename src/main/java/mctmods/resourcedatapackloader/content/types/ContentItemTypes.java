package mctmods.resourcedatapackloader.content.types;

import mctmods.resourcedatapackloader.content.ContentStates;
import mctmods.resourcedatapackloader.content.def.MaterialDef;
import mctmods.resourcedatapackloader.content.interfaces.IItemType;
import mctmods.resourcedatapackloader.content.item.ContentItem;
import mctmods.resourcedatapackloader.content.item.ContentItemArmor;
import mctmods.resourcedatapackloader.content.item.ContentItemDrink;
import mctmods.resourcedatapackloader.content.item.ContentItemFood;
import mctmods.resourcedatapackloader.content.item.ContentItemPotion;
import mctmods.resourcedatapackloader.content.item.ContentItemSeed;
import mctmods.resourcedatapackloader.content.item.ContentItemTool;
import mctmods.resourcedatapackloader.content.util.ContentMaterials;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.block.Block;
import net.minecraft.inventory.EntityEquipmentSlot;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

public final class ContentItemTypes {
    public static final String DEFAULT = "basic";
    private static final Map<String, IItemType> TYPES = new LinkedHashMap<>();

    private ContentItemTypes() {}

    static {
        register(DEFAULT, def -> Collections.singletonList(new ContentItem(def)));
        register("food", def -> Collections.singletonList(new ContentItemFood(def)));
        IItemType drink = def -> Collections.singletonList(new ContentItemDrink(def));
        register("drink", drink);
        register("potion", drink);
        register("tool", def -> {
            MaterialDef material = ContentMaterials.find(def.material, def.registryName);
            ContentToolTypes.Profile profile = ContentToolTypes.tool(def.toolClass, def.registryName);
            if (material == null || profile == null || material.getTool() == null) { return Collections.emptyList(); }
            float speed = Float.isNaN(def.attackSpeed) ? profile.speed : def.attackSpeed;
            ContentItemTool tool = new ContentItemTool(def, material.getTool(), profile.damage, speed, profile.effective);
            tool.setHarvestLevel(profile.toolClass, material.harvestLevel);
            return Collections.singletonList(tool);
        });
        register("seed", def -> {
            Block crop = ContentStates.block(def.crop, def.registryName);
            Block soil = ContentStates.block(def.soil, def.registryName);
            if (crop == null || soil == null) { return Collections.emptyList(); }
            return Collections.singletonList(new ContentItemSeed(def, crop, soil));
        });
        register("potion_bottle", def -> Collections.singletonList(new ContentItemPotion(def)));
        register("armor", def -> {
            MaterialDef material = ContentMaterials.find(def.material, def.registryName);
            EntityEquipmentSlot slot = ContentToolTypes.slot(def.slot, def.registryName);
            if (material == null || slot == null || material.getArmor() == null) { return Collections.emptyList(); }
            return Collections.singletonList(new ContentItemArmor(def, material.getArmor(), slot));
        });
    }

    public static void register(String name, IItemType type) {
        IItemType previous = TYPES.put(name.toLowerCase(Locale.ROOT), type);
        if (previous != null) { ContentLog.LOGGER.warn("Item type '{}' was registered twice, the later one wins", name); }
    }

    @Nullable public static IItemType find(String name) { return TYPES.get(name == null ? "" : name.toLowerCase(Locale.ROOT)); }

    public static IItemType get(String name, Object context) {
        IItemType type = find(name);
        if (type != null) { return type; }
        ContentLog.LOGGER.error("Unknown item type '{}' in {}, treating it as '{}'. Known types are {}", name, context, DEFAULT, names());
        return TYPES.get(DEFAULT);
    }

    public static Set<String> names() { return Collections.unmodifiableSet(TYPES.keySet()); }
}
