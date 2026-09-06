package mctmods.resourcedatapackloader.content;

import mctmods.resourcedatapackloader.content.def.ItemDef;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.List;

public final class ContentFormats {
    public static final String LOOT_FOLDER = "loot_tables";
    public static final String BLOCK_TAGS = "tags/blocks";
    public static final String ITEM_TAGS = "tags/items";
    public static final String BIOME_MODIFIERS = "forge/biome_modifier";
    public static final String ADD_SPAWNS = "forge:add_spawns";
    public static final String CONVENTION = "forge";
    public static final String SPARSE_TAG = "is_sparse";
    public static final String DENSE_TAG = "is_dense";
    public static final String WATER_TAG = "is_water";
    public static final String POI_TAGS = "tags/point_of_interest_type";
    private static final String SILK_TOUCH = "{\"condition\":\"minecraft:match_tool\",\"predicate\":{\"enchantments\":[{\"enchantment\":\"minecraft:silk_touch\",\"levels\":{\"min\":1}}]}}";
    private static final String SHEARS = "{\"condition\":\"minecraft:match_tool\",\"predicate\":{\"items\":[\"minecraft:shears\"]}}";

    private ContentFormats() {}

    public static JsonObject silkTouch() { return JsonParser.parseString(SILK_TOUCH).getAsJsonObject(); }

    public static JsonObject shears() { return JsonParser.parseString(SHEARS).getAsJsonObject(); }

    public static List<String> equipmentTags(ItemDef def) {
        return switch (def.type()) {
            case "tool" -> switch (def.toolClass()) {
                case "pickaxe" -> List.of("minecraft:pickaxes", "minecraft:cluster_max_harvestables", "forge:tools");
                case "axe" -> List.of("minecraft:axes", "forge:tools");
                case "shovel" -> List.of("minecraft:shovels", "forge:tools");
                case "sword" -> List.of("minecraft:swords", "forge:tools");
                default -> List.of();
            };
            case "armor" -> switch (def.slot()) {
                case "helmet", "head" -> List.of("forge:armors/helmets", "forge:armors", "minecraft:trimmable_armor");
                case "chestplate", "chest" -> List.of("forge:armors/chestplates", "forge:armors", "minecraft:trimmable_armor");
                case "leggings", "legs" -> List.of("forge:armors/leggings", "forge:armors", "minecraft:trimmable_armor");
                case "boots", "feet" -> List.of("forge:armors/boots", "forge:armors", "minecraft:trimmable_armor");
                default -> List.of();
            };
            default -> List.of();
        };
    }
}
