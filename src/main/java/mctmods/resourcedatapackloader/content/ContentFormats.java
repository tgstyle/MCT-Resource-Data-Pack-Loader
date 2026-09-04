package mctmods.resourcedatapackloader.content;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public final class ContentFormats {
    public static final String LOOT_FOLDER = "loot_table";
    public static final String BLOCK_TAGS = "tags/block";
    public static final String ITEM_TAGS = "tags/item";
    public static final String POI_TAGS = "tags/point_of_interest_type";
    private static final String SILK_TOUCH = "{\"condition\":\"minecraft:match_tool\",\"predicate\":{\"predicates\":{\"minecraft:enchantments\":[{\"enchantments\":\"minecraft:silk_touch\",\"levels\":{\"min\":1}}]}}}";
    private static final String SHEARS = "{\"condition\":\"minecraft:match_tool\",\"predicate\":{\"items\":\"minecraft:shears\"}}";

    private ContentFormats() {}

    public static JsonObject silkTouch() { return JsonParser.parseString(SILK_TOUCH).getAsJsonObject(); }

    public static JsonObject shears() { return JsonParser.parseString(SHEARS).getAsJsonObject(); }
}
