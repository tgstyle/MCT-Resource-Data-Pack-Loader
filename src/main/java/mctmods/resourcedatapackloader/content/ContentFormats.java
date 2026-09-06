package mctmods.resourcedatapackloader.content;

import mctmods.resourcedatapackloader.content.def.ItemDef;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.List;

public final class ContentFormats {
    public static final String LOOT_FOLDER = "loot_table";
    public static final String BLOCK_TAGS = "tags/block";
    public static final String ITEM_TAGS = "tags/item";
    public static final String ENTITY_TYPE_TAGS = "tags/entity_type";
    public static final String BIOME_MODIFIERS = "neoforge/biome_modifier";
    public static final String ADD_SPAWNS = "neoforge:add_spawns";
    public static final String CONVENTION = "c";
    public static final String SPARSE_TAG = "is_sparse_vegetation";
    public static final String DENSE_TAG = "is_dense_vegetation";
    public static final String WATER_TAG = "is_aquatic";
    public static final String POI_TAGS = "tags/point_of_interest_type";
    private static final String SILK_TOUCH = "{\"condition\":\"minecraft:match_tool\",\"predicate\":{\"predicates\":{\"minecraft:enchantments\":[{\"enchantments\":\"minecraft:silk_touch\",\"levels\":{\"min\":1}}]}}}";
    private static final String SHEARS = "{\"condition\":\"minecraft:match_tool\",\"predicate\":{\"items\":\"minecraft:shears\"}}";

    private ContentFormats() {}

    public static JsonObject silkTouch() { return JsonParser.parseString(SILK_TOUCH).getAsJsonObject(); }

    public static JsonObject shears() { return JsonParser.parseString(SHEARS).getAsJsonObject(); }

    public static List<String> equipmentTags(ItemDef def) {
        return switch (def.type()) {
            case "tool" -> switch (def.toolClass()) {
                case "pickaxe" -> List.of("minecraft:pickaxes", "minecraft:cluster_max_harvestables", "c:tools/mining_tool", "c:tools");
                case "axe" -> List.of("minecraft:axes", "c:tools/mining_tool", "c:tools/melee_weapon", "c:tools");
                case "shovel" -> List.of("minecraft:shovels", "c:tools/mining_tool", "c:tools");
                case "sword" -> List.of("minecraft:swords", "c:tools/melee_weapon", "c:tools");
                default -> List.of();
            };
            case "armor" -> switch (def.slot()) {
                case "helmet", "head" -> List.of("minecraft:head_armor", "minecraft:trimmable_armor", "c:armors");
                case "chestplate", "chest" -> List.of("minecraft:chest_armor", "minecraft:trimmable_armor", "c:armors");
                case "leggings", "legs" -> List.of("minecraft:leg_armor", "minecraft:trimmable_armor", "c:armors");
                case "boots", "feet" -> List.of("minecraft:foot_armor", "minecraft:trimmable_armor", "c:armors");
                default -> List.of();
            };
            default -> List.of();
        };
    }
}
