package mctmods.resourcedatapackloader.content;

import mctmods.resourcedatapackloader.content.def.ItemDef;
import mctmods.resourcedatapackloader.util.ContentLog;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nullable;

public final class ContentFormats {
    public static final String LOOT_FOLDER = "loot_table";
    public static final String BLOCK_TAGS = "tags/block";
    public static final String ITEM_TAGS = "tags/item";
    public static final String BIOME_TAGS = "tags/worldgen/biome";
    public static final String ENTITY_TYPE_TAGS = "tags/entity_type";
    public static final String BIOME_MODIFIERS = "neoforge/biome_modifier";
    public static final String ADD_FEATURES = "neoforge:add_features";
    public static final String ANY_HOLDER_SET = "neoforge:any";
    public static final String CONFIGURED_FEATURES = "worldgen/configured_feature";
    public static final String PLACED_FEATURES = "worldgen/placed_feature";
    public static final String CONVENTION = "c";
    public static final String CONVENTION_HOLDER_SETS = "neoforge";
    public static final String SPARSE_TAG = "is_sparse_vegetation";
    public static final String DENSE_TAG = "is_dense_vegetation";
    public static final String WATER_TAG = "is_aquatic";
    public static final String POI_TAGS = "tags/point_of_interest_type";
    public static final String MINEABLE_SWORD = "resourcedatapackloader:mineable/sword";
    public static final String NEEDS_NETHERITE_TOOL = "neoforge:needs_netherite_tool";
    public static final String FUNCTION_TAGS = "tags/function";
    private static final String SILK_TOUCH = "{\"condition\":\"minecraft:match_tool\",\"predicate\":{\"predicates\":{\"minecraft:enchantments\":[{\"enchantments\":\"minecraft:silk_touch\",\"levels\":{\"min\":1}}]}}}";
    private static final String SHEARS = "{\"condition\":\"minecraft:match_tool\",\"predicate\":{\"items\":\"minecraft:shears\"}}";
    private static final Set<String> WARNED = ConcurrentHashMap.newKeySet();

    private ContentFormats() {}

    public static JsonObject silkTouch() { return JsonParser.parseString(SILK_TOUCH).getAsJsonObject(); }

    public static JsonObject shears() { return JsonParser.parseString(SHEARS).getAsJsonObject(); }

    public static List<String> equipmentTags(ItemDef def) {
        return switch (def.type()) {
            case "tool" -> switch (def.toolClass()) {
                case "pickaxe" -> List.of("minecraft:pickaxes", "minecraft:cluster_max_harvestables", "c:tools/mining_tool", "c:tools");
                case "axe" -> List.of("minecraft:axes", "c:tools/mining_tool", "c:tools/melee_weapon", "c:tools");
                case "shovel" -> List.of("minecraft:shovels", "c:tools/mining_tool", "c:tools");
                case "sword" -> List.of("minecraft:enchantable/mining", "minecraft:enchantable/mining_loot", "minecraft:enchantable/durability", "c:tools/melee_weapon", "c:tools");
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

    public static JsonObject anyBiomes() {
        JsonObject any = new JsonObject();
        any.addProperty("type", ANY_HOLDER_SET);
        return any;
    }

    @Nullable public static String biomeTag(String type) {
        String wanted = type.trim().toLowerCase(Locale.ROOT);
        return switch (wanted) {
            case "ocean" -> "minecraft:is_ocean";
            case "deepocean", "deep_ocean" -> "minecraft:is_deep_ocean";
            case "beach" -> "minecraft:is_beach";
            case "river" -> "minecraft:is_river";
            case "mountain", "mountains" -> "minecraft:is_mountain";
            case "mesa", "badlands" -> "minecraft:is_badlands";
            case "hills", "hill" -> "minecraft:is_hill";
            case "coniferous" -> "minecraft:is_taiga";
            case "jungle" -> "minecraft:is_jungle";
            case "forest" -> "minecraft:is_forest";
            case "savanna" -> "minecraft:is_savanna";
            case "overworld" -> "minecraft:is_overworld";
            case "nether" -> "minecraft:is_nether";
            case "end" -> "minecraft:is_end";
            case "hot" -> CONVENTION + ":is_hot";
            case "cold" -> CONVENTION + ":is_cold";
            case "sparse" -> CONVENTION + ":" + SPARSE_TAG;
            case "dense" -> CONVENTION + ":" + DENSE_TAG;
            case "wet" -> CONVENTION + ":is_wet";
            case "dry" -> CONVENTION + ":is_dry";
            case "spooky" -> CONVENTION + ":is_spooky";
            case "dead" -> CONVENTION + ":is_dead";
            case "lush" -> CONVENTION + ":is_lush";
            case "mushroom" -> CONVENTION + ":is_mushroom";
            case "magical" -> CONVENTION + ":is_magical";
            case "rare" -> CONVENTION + ":is_rare";
            case "plateau" -> CONVENTION + ":is_plateau";
            case "modified" -> CONVENTION + ":is_modified";
            case "water" -> CONVENTION + ":" + WATER_TAG;
            case "desert" -> CONVENTION + ":is_desert";
            case "plains" -> CONVENTION + ":is_plains";
            case "swamp" -> CONVENTION + ":is_swamp";
            case "sandy" -> CONVENTION + ":is_sandy";
            case "snowy" -> CONVENTION + ":is_snowy";
            case "wasteland" -> CONVENTION + ":is_wasteland";
            case "void" -> CONVENTION + ":is_void";
            default -> wanted.contains(":") ? wanted : null;
        };
    }

    public static String dimensionId(String named) {
        String wanted = named.trim();
        return switch (wanted) {
            case "0" -> "minecraft:overworld";
            case "-1" -> "minecraft:the_nether";
            case "1" -> "minecraft:the_end";
            default -> {
                if (!wanted.isEmpty() && (wanted.charAt(0) == '-' || Character.isDigit(wanted.charAt(0))) && wanted.matches("-?\\d+") && WARNED.add(wanted)) { ContentLog.LOGGER.warn("Dimension {} is a 1.12.2 dimension number, and only 0, -1 and 1 still stand for a dimension, so it names none. Name the dimension by its id, such as mypack:verdant", wanted); }
                yield wanted;
            }
        };
    }

    @Nullable public static String vanillaTab(String named) {
        return switch (named) {
            case "buildingBlocks", "building_blocks" -> "minecraft:building_blocks";
            case "decorations", "functional_blocks" -> "minecraft:functional_blocks";
            case "redstone", "redstone_blocks" -> "minecraft:redstone_blocks";
            case "transportation", "tools", "tools_and_utilities" -> "minecraft:tools_and_utilities";
            case "misc", "materials", "ingredients" -> "minecraft:ingredients";
            case "food", "brewing", "food_and_drinks" -> "minecraft:food_and_drinks";
            case "combat", "colored_blocks", "natural_blocks", "spawn_eggs", "op_blocks" -> "minecraft:" + named;
            default -> null;
        };
    }
}
