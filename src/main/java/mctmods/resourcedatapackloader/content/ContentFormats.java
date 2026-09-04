package mctmods.resourcedatapackloader.content;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public final class ContentFormats {
    public static final String LOOT_FOLDER = "loot_tables";
    public static final String BLOCK_TAGS = "tags/blocks";
    public static final String ITEM_TAGS = "tags/items";
    private static final String SILK_TOUCH = "{\"condition\":\"minecraft:match_tool\",\"predicate\":{\"enchantments\":[{\"enchantment\":\"minecraft:silk_touch\",\"levels\":{\"min\":1}}]}}";
    private static final String SHEARS = "{\"condition\":\"minecraft:match_tool\",\"predicate\":{\"items\":[\"minecraft:shears\"]}}";

    private ContentFormats() {}

    public static JsonObject silkTouch() { return JsonParser.parseString(SILK_TOUCH).getAsJsonObject(); }

    public static JsonObject shears() { return JsonParser.parseString(SHEARS).getAsJsonObject(); }
}
