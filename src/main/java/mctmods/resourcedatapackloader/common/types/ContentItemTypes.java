package mctmods.resourcedatapackloader.common.types;

import mctmods.resourcedatapackloader.common.blocks.ContentStates;
import mctmods.resourcedatapackloader.common.items.ContentArmorItem;
import mctmods.resourcedatapackloader.common.items.ContentDrinkItem;
import mctmods.resourcedatapackloader.common.items.ContentFoodItem;
import mctmods.resourcedatapackloader.common.items.ContentItem;
import mctmods.resourcedatapackloader.common.items.ContentPotionItem;
import mctmods.resourcedatapackloader.common.items.ContentRocketItem;
import mctmods.resourcedatapackloader.common.items.ContentSeedItem;
import mctmods.resourcedatapackloader.common.items.ContentToolItem;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.MaterialDef;
import mctmods.resourcedatapackloader.common.util.ContentMaterials;
import mctmods.resourcedatapackloader.common.util.NamedRegistry;
import mctmods.resourcedatapackloader.core.util.ContentLog;

import java.util.Collections;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraftforge.fml.common.Loader;

public final class ContentItemTypes {
    public static final String DEFAULT = "basic";
    private static final NamedRegistry<IItemType> TYPES = new NamedRegistry<>("Item", DEFAULT);

    private ContentItemTypes() {}

    static {
        register(DEFAULT, def -> Collections.singletonList(new ContentItem(def)));
        register("container", def -> Collections.singletonList(new mctmods.resourcedatapackloader.common.items.ContentContainerItem(def)));
        register("container_upgrade", def -> Collections.singletonList(new mctmods.resourcedatapackloader.common.items.ContentContainerUpgradeItem(def)));
        register("portway", def -> Collections.singletonList(new mctmods.resourcedatapackloader.common.items.ContentPortwayItem(def)));
        register("food", def -> Collections.singletonList(new ContentFoodItem(def)));
        IItemType drink = def -> Collections.singletonList(new ContentDrinkItem(def));
        register("drink", drink);
        register("potion", drink);
        register("tool", def -> {
            MaterialDef material = ContentMaterials.find(def.material, def.registryName);
            ContentToolTypes.Profile profile = ContentToolTypes.tool(def.toolClass, def.registryName);
            if (material == null || profile == null || material.getTool() == null) { return Collections.emptyList(); }
            float speed = Float.isNaN(def.attackSpeed) ? profile.speed : def.attackSpeed;
            ContentToolItem tool = new ContentToolItem(def, material.getTool(), profile.damage, speed, profile.effective);
            tool.setHarvestLevel(profile.toolClass, material.harvestLevel);
            return Collections.singletonList(tool);
        });
        register("seed", def -> {
            Block crop = ContentStates.block(def.crop, def.registryName);
            Block soil = ContentStates.block(def.soil, def.registryName);
            if (crop == null || soil == null) { return Collections.emptyList(); }
            return Collections.singletonList(new ContentSeedItem(def, crop, soil));
        });
        register("potion_bottle", def -> Collections.singletonList(new ContentPotionItem(def)));
        register("rocket", def -> {
            if (!Loader.isModLoaded("galacticraftcore") || def.rocket.isEmpty()) {
                ContentLog.LOGGER.error("Rocket item {} needs Galacticraft and a rocket variant to place, leaving it out", def.registryName);
                return Collections.emptyList();
            }
            return Collections.singletonList(new ContentRocketItem(def));
        });
        register("armor", def -> {
            MaterialDef material = ContentMaterials.find(def.material, def.registryName);
            EntityEquipmentSlot slot = ContentToolTypes.slot(def.slot, def.registryName);
            if (material == null || slot == null || material.getArmor() == null) { return Collections.emptyList(); }
            return Collections.singletonList(new ContentArmorItem(def, material.getArmor(), slot));
        });
    }

    public static void register(String name, IItemType type) { TYPES.register(name, type); }

    @Nullable public static IItemType find(String name) { return TYPES.find(name); }

    public static IItemType get(String name, Object context) { return TYPES.get(name, context); }

    public static Set<String> names() { return TYPES.names(); }
}
