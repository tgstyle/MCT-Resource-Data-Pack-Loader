package mctmods.resourcedatapackloader.content.types;

import mctmods.resourcedatapackloader.content.ContentRegistry;
import mctmods.resourcedatapackloader.content.def.ItemDef;
import mctmods.resourcedatapackloader.content.def.ItemVariant;
import mctmods.resourcedatapackloader.content.def.MaterialDef;
import mctmods.resourcedatapackloader.content.item.ContentDrinkItem;
import mctmods.resourcedatapackloader.content.item.ContentFoodItem;
import mctmods.resourcedatapackloader.content.item.ContentPotionItem;
import mctmods.resourcedatapackloader.content.util.ContentEffects;
import mctmods.resourcedatapackloader.content.util.ContentMaterials;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Registered;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.block.Block;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

public final class ContentItemTypes {
    public static final String BASIC = "basic";
    public static final String FOOD = "food";
    public static final String DRINK = "drink";
    public static final String TOOL = "tool";
    public static final String ARMOR = "armor";
    public static final String SEED = "seed";
    public static final String POTION = "potion";
    public static final String POTION_BOTTLE = "potion_bottle";
    private static final Set<String> KNOWN = Set.of(BASIC, FOOD, DRINK, TOOL, ARMOR, SEED, POTION, POTION_BOTTLE);
    private static final Map<String, ArmorItem.Type> SLOTS = Map.of("helmet", ArmorItem.Type.HELMET, "head", ArmorItem.Type.HELMET, "chestplate", ArmorItem.Type.CHESTPLATE, "chest", ArmorItem.Type.CHESTPLATE,
            "leggings", ArmorItem.Type.LEGGINGS, "legs", ArmorItem.Type.LEGGINGS, "boots", ArmorItem.Type.BOOTS, "feet", ArmorItem.Type.BOOTS);

    private ContentItemTypes() {}

    @Nullable public static Item create(ItemDef def, ItemVariant variant) {
        String type = def.type();
        if (!KNOWN.contains(type)) {
            ContentLog.LOGGER.error("Unknown item type '{}' in {}, treating it as '{}'. Known types are {}", type, def.key(), BASIC, KNOWN);
            type = BASIC;
        }
        Item.Properties properties = new Item.Properties().stacksTo(variant.maxSize()).rarity(ContentTypes.rarity(variant.rarity(), variant.id()));
        return switch (type) {
            case FOOD -> new ContentFoodItem(def, properties.food(food(def, variant)));
            case DRINK, POTION -> new ContentDrinkItem(def, variant, properties);
            case POTION_BOTTLE -> new ContentPotionItem(def, variant.id(), properties);
            case TOOL -> tool(def, variant, properties.stacksTo(1));
            case ARMOR -> armor(def, variant, properties.stacksTo(1));
            case SEED -> seed(def, variant, properties);
            default -> new Item(properties);
        };
    }

    private static FoodProperties food(ItemDef def, ItemVariant variant) {
        FoodProperties.Builder builder = new FoodProperties.Builder().nutrition(variant.healAmount()).saturationModifier(variant.saturation());
        if (def.alwaysEdible()) { builder = builder.alwaysEdible(); }
        MobEffectInstance effect = ContentEffects.parse(variant.id(), variant.potion());
        if (effect != null) { builder = builder.effect(() -> ContentEffects.copy(effect), 1.0F); }
        return builder.build();
    }

    @Nullable private static Item tool(ItemDef def, ItemVariant variant, Item.Properties properties) {
        MaterialDef material = ContentRegistry.material(def.material(), variant.id());
        if (material == null) { return null; }
        Tier tier = ContentMaterials.tier(material);
        return switch (def.toolClass()) {
            case "pickaxe" -> new PickaxeItem(tier, properties.attributes(PickaxeItem.createAttributes(tier, 1.0F, speed(def, -2.8F))));
            case "axe" -> new AxeItem(tier, properties.attributes(AxeItem.createAttributes(tier, 6.0F, speed(def, -3.2F))));
            case "shovel" -> new ShovelItem(tier, properties.attributes(ShovelItem.createAttributes(tier, 1.5F, speed(def, -3.0F))));
            case "sword" -> new SwordItem(tier, properties.attributes(SwordItem.createAttributes(tier, 3, speed(def, -2.4F))));
            default -> {
                ContentLog.LOGGER.error("Unknown toolClass '{}' in {}, the item is skipped. Known classes are pickaxe, axe, shovel and sword", def.toolClass(), variant.id());
                yield null;
            }
        };
    }

    private static float speed(ItemDef def, float fallback) { return Float.isNaN(def.attackSpeed()) ? fallback : def.attackSpeed(); }

    @Nullable private static Item armor(ItemDef def, ItemVariant variant, Item.Properties properties) {
        MaterialDef material = ContentRegistry.material(def.material(), variant.id());
        ArmorItem.Type slot = SLOTS.get(def.slot());
        if (material == null) { return null; }
        if (slot == null) {
            ContentLog.LOGGER.error("Unknown armor slot '{}' in {}, the item is skipped. Known slots are {}", def.slot(), variant.id(), SLOTS.keySet());
            return null;
        }
        return new ArmorItem(ContentMaterials.armor(material), slot, properties);
    }

    @Nullable private static Item seed(ItemDef def, ItemVariant variant, Item.Properties properties) {
        ResourceLocation named = ResourceLocation.tryParse(def.crop());
        ContentRegistry.BlockEntry made = named == null ? null : ContentRegistry.block(named);
        Block crop = made != null ? made.block() : Registered.find(BuiltInRegistries.BLOCK, named);
        if (crop == null) {
            ContentLog.LOGGER.error("Seed {} plants '{}', which is not a registered block, the item is skipped", variant.id(), def.crop());
            return null;
        }
        return new ItemNameBlockItem(crop, properties);
    }
}
