package mctmods.resourcedatapackloader.content.extra;

import mctmods.resourcedatapackloader.content.ContentStacks;
import mctmods.resourcedatapackloader.content.def.TradeDef;
import mctmods.resourcedatapackloader.content.def.TradeStackDef;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.world.item.Item;
import javax.annotation.Nullable;

public final class ContentTrade {
    private static final float REPUTATION_DISCOUNT = 0.05F;

    private ContentTrade() {}

    public static JsonObject json(TradeDef def, Item buy, @Nullable Item buySecondary, Item sell) {
        JsonObject json = new JsonObject();
        json.add("wants", cost(buy, def.buy()));
        if (buySecondary != null) { json.add("additional_wants", cost(buySecondary, def.buySecondary())); }
        JsonObject gives = new JsonObject();
        gives.addProperty("id", ContentStacks.id(sell));
        gives.addProperty("count", def.sell().min());
        json.add("gives", gives);
        if (def.sell().max() > def.sell().min()) {
            JsonObject setCount = new JsonObject();
            setCount.addProperty("function", "minecraft:set_count");
            setCount.add("count", count(def.sell()));
            JsonArray modifiers = new JsonArray();
            modifiers.add(setCount);
            json.add("given_item_modifiers", modifiers);
        }
        json.addProperty("max_uses", def.maxUses());
        json.addProperty("xp", def.xp());
        json.addProperty("reputation_discount", REPUTATION_DISCOUNT);
        return json;
    }

    private static JsonObject cost(Item item, TradeStackDef stack) {
        JsonObject json = new JsonObject();
        json.addProperty("id", ContentStacks.id(item));
        json.add("count", count(stack));
        return json;
    }

    private static JsonElement count(TradeStackDef stack) {
        if (stack.max() <= stack.min()) { return new JsonPrimitive(stack.min()); }
        JsonObject uniform = new JsonObject();
        uniform.addProperty("type", "minecraft:uniform");
        uniform.addProperty("min", stack.min());
        uniform.addProperty("max", stack.max());
        return uniform;
    }
}
