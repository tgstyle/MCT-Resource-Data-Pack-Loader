package mctmods.resourcedatapackloader.loot;

import mctmods.resourcedatapackloader.content.def.DropDef;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import javax.annotation.Nonnull;

public final class DropRoll extends LootItemConditionalFunction {
    private static final String CHANCE = "chance";
    private static final String BONUS_CHANCE = "bonusChance";
    private final int chance;
    private final int[] bonusChance;

    DropRoll(LootItemCondition[] conditions, int chance, int[] bonusChance) {
        super(conditions);
        this.chance = chance;
        this.bonusChance = bonusChance;
    }

    @Override @Nonnull public LootItemFunctionType getType() { return LootFunctions.DROP_ROLL.get(); }

    @Override @Nonnull protected ItemStack run(@Nonnull ItemStack stack, @Nonnull LootContext context) {
        ItemStack tool = context.getParamOrNull(LootContextParams.TOOL);
        int fortune = tool == null ? 0 : tool.getEnchantmentLevel(Enchantments.BLOCK_FORTUNE);
        stack.setCount(stack.getCount() * DropDef.copies(context.getRandom(), chance, bonusChance, fortune));
        return stack;
    }

    public static final class Serializer extends LootItemConditionalFunction.Serializer<DropRoll> {
        @Override public void serialize(@Nonnull JsonObject json, @Nonnull DropRoll value, @Nonnull JsonSerializationContext context) {
            super.serialize(json, value, context);
            json.addProperty(CHANCE, value.chance);
            JsonArray bonus = new JsonArray();
            for (int chance : value.bonusChance) { bonus.add(chance); }
            json.add(BONUS_CHANCE, bonus);
        }

        @Override @Nonnull public DropRoll deserialize(@Nonnull JsonObject json, @Nonnull JsonDeserializationContext context, @Nonnull LootItemCondition[] conditions) {
            JsonArray bonus = GsonHelper.getAsJsonArray(json, BONUS_CHANCE, new JsonArray());
            int[] bonusChance = new int[bonus.size()];
            for (int i = 0; i < bonusChance.length; i++) { bonusChance[i] = GsonHelper.convertToInt(bonus.get(i), BONUS_CHANCE); }
            return new DropRoll(conditions, GsonHelper.getAsInt(json, CHANCE, 100), bonusChance);
        }
    }
}
