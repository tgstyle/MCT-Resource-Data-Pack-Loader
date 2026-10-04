package mctmods.resourcedatapackloader.content.types;

import mctmods.resourcedatapackloader.compat.LineCompat;
import mctmods.resourcedatapackloader.content.ContentFormats;
import mctmods.resourcedatapackloader.content.ContentRegistry;
import mctmods.resourcedatapackloader.content.def.ContainerDef;
import mctmods.resourcedatapackloader.content.def.ItemDef;
import mctmods.resourcedatapackloader.content.def.ItemVariant;
import mctmods.resourcedatapackloader.content.def.MaterialDef;
import mctmods.resourcedatapackloader.content.item.ContentContainerItem;
import mctmods.resourcedatapackloader.content.item.ContentDrinkItem;
import mctmods.resourcedatapackloader.content.item.ContentFoodItem;
import mctmods.resourcedatapackloader.content.item.ContentPotionItem;
import mctmods.resourcedatapackloader.content.item.ContentRollItem;
import mctmods.resourcedatapackloader.content.util.ContentEffects;
import mctmods.resourcedatapackloader.content.util.ContentMaterials;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Registered;

import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Unit;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.component.Consumables;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;
import net.minecraft.world.item.equipment.ArmorType;
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
    public static final String CONTAINER = "container";
    private static final Set<String> KNOWN = Set.of(BASIC, FOOD, DRINK, TOOL, ARMOR, SEED, POTION, POTION_BOTTLE, CONTAINER);
    private static final ContainerDef POUCH = new ContainerDef(1, 9, "", false, null, null, 0, 0, "");
    private static final TagKey<Block> SWORD_MINEABLE = TagKey.create(Registries.BLOCK, Identifier.parse(ContentFormats.MINEABLE_SWORD));
    private static final Map<String, ArmorType> SLOTS = Map.of("helmet", ArmorType.HELMET, "head", ArmorType.HELMET, "chestplate", ArmorType.CHESTPLATE, "chest", ArmorType.CHESTPLATE,
            "leggings", ArmorType.LEGGINGS, "legs", ArmorType.LEGGINGS, "boots", ArmorType.BOOTS, "feet", ArmorType.BOOTS);

    private ContentItemTypes() {}

    @Nullable public static Item create(ItemDef def, ItemVariant variant) {
        String type = def.type();
        if (!KNOWN.contains(type)) {
            ContentLog.LOGGER.error("Unknown item type '{}' in {}, treating it as '{}'. Known types are {}", type, def.key(), BASIC, KNOWN);
            type = BASIC;
        }
        Item.Properties properties = new Item.Properties().setId(ResourceKey.create(Registries.ITEM, variant.id())).stacksTo(variant.maxSize()).rarity(ContentTypes.rarity(variant.rarity(), variant.id()));
        return switch (type) {
            case FOOD -> {
                MobEffectInstance effect = ContentEffects.parse(variant.id(), variant.potion());
                yield new ContentFoodItem(def, effect, properties.food(food(def, variant), consumable(effect)));
            }
            case DRINK, POTION -> new ContentDrinkItem(def, variant, properties);
            case POTION_BOTTLE -> new ContentPotionItem(def, variant.id(), properties.stacksTo(1));
            case TOOL -> tool(def, variant, properties.stacksTo(1));
            case ARMOR -> armor(def, variant, properties.stacksTo(1));
            case SEED -> seed(def, variant, properties);
            case CONTAINER -> new ContentContainerItem(def.holds() == null ? POUCH : def.holds(), properties);
            default -> def.rolls().isEmpty() && !def.passesTurn() ? new Item(properties) : new ContentRollItem(def, properties);
        };
    }

    private static FoodProperties food(ItemDef def, ItemVariant variant) {
        FoodProperties.Builder builder = new FoodProperties.Builder().nutrition(variant.healAmount()).saturationModifier(variant.saturation());
        if (def.alwaysEdible()) { builder = builder.alwaysEdible(); }
        return builder.build();
    }

    private static Consumable consumable(@Nullable MobEffectInstance effect) {
        Consumable.Builder builder = Consumables.defaultFood();
        if (effect != null) { builder = builder.onConsume(new ApplyStatusEffectsConsumeEffect(effect, 1.0F)); }
        return builder.build();
    }

    @Nullable private static Item tool(ItemDef def, ItemVariant variant, Item.Properties properties) {
        MaterialDef material = ContentRegistry.material(def.material(), variant.id());
        if (material == null) { return null; }
        ToolMaterial tool = ContentMaterials.tool(material);
        Item.Properties lasting = lasting(properties, tool.durability());
        Item made = switch (def.toolClass()) {
            case "pickaxe" -> new Item(lasting.pickaxe(tool, 1.0F, speed(def, -2.8F)));
            case "axe" -> LineCompat.axe(lasting, tool, 6.0F, speed(def, -3.2F));
            case "shovel" -> LineCompat.shovel(lasting, tool, 1.5F, speed(def, -3.0F));
            case "sword" -> new Item(lasting.tool(tool, SWORD_MINEABLE, 3.0F, speed(def, -2.4F), 0.0F));
            default -> {
                ContentLog.LOGGER.error("Unknown toolClass '{}' in {}, the item is skipped. Known classes are pickaxe, axe, shovel and sword", def.toolClass(), variant.id());
                yield null;
            }
        };
        if (made != null) { ContentMaterials.repairWith(ResourceKey.create(Registries.ITEM, variant.id()), material); }
        return made;
    }

    private static float speed(ItemDef def, float fallback) { return Float.isNaN(def.attackSpeed()) ? fallback : def.attackSpeed(); }

    @Nullable private static Item armor(ItemDef def, ItemVariant variant, Item.Properties properties) {
        MaterialDef material = ContentRegistry.material(def.material(), variant.id());
        ArmorType slot = SLOTS.get(def.slot());
        if (material == null) { return null; }
        if (slot == null) {
            ContentLog.LOGGER.error("Unknown armor slot '{}' in {}, the item is skipped. Known slots are {}", def.slot(), variant.id(), SLOTS.keySet());
            return null;
        }
        int uses = slot.getDurability(material.durability() / 10);
        Item made = new Item(lasting(properties.humanoidArmor(ContentMaterials.armor(material), slot), uses));
        if (ContentMaterials.customArmor(material)) { ContentMaterials.repairWith(ResourceKey.create(Registries.ITEM, variant.id()), null); }
        return made;
    }

    private static Item.Properties lasting(Item.Properties properties, int uses) { return uses > 0 ? properties : properties.component(DataComponents.UNBREAKABLE, Unit.INSTANCE).component(DataComponents.TOOLTIP_DISPLAY, TooltipDisplay.DEFAULT.withHidden(DataComponents.UNBREAKABLE, true)); }

    @Nullable private static Item seed(ItemDef def, ItemVariant variant, Item.Properties properties) {
        Identifier named = Identifier.tryParse(def.crop());
        ContentRegistry.BlockEntry made = named == null ? null : ContentRegistry.block(named);
        Block crop = made != null ? made.block() : Registered.find(BuiltInRegistries.BLOCK, named);
        if (crop == null) {
            ContentLog.LOGGER.error("Seed {} plants '{}', which is not a registered block, the item is skipped", variant.id(), def.crop());
            return null;
        }
        return new BlockItem(crop, properties.useItemDescriptionPrefix());
    }
}
