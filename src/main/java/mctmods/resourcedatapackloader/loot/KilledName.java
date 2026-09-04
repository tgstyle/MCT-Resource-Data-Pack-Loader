package mctmods.resourcedatapackloader.loot;

import mctmods.resourcedatapackloader.util.ContentLog;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.network.chat.Component;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import java.util.IllegalFormatException;
import javax.annotation.Nonnull;

public final class KilledName extends LootItemConditionalFunction {
    private static final String FORMAT = "format";
    private static final String TAG = "tag";
    private final String format;
    private final String tag;

    KilledName(LootItemCondition[] conditions, String format, String tag) {
        super(conditions);
        this.format = format;
        this.tag = tag;
    }

    @Override @Nonnull public LootItemFunctionType getType() { return LootFunctions.KILLED_NAME.get(); }

    @Override @Nonnull protected ItemStack run(@Nonnull ItemStack stack, @Nonnull LootContext context) {
        Entity looted = context.getParamOrNull(LootContextParams.THIS_ENTITY);
        if (looted == null) { return stack; }
        String name = looted.getName().getString();
        if (!tag.isEmpty()) { stack.getOrCreateTag().putString(tag, name); }
        if (!format.isEmpty()) {
            try { stack.setHoverName(Component.literal(String.format(format, name))); }
            catch (IllegalFormatException bad) {
                ContentLog.LOGGER.error("A killed_name format '{}' is not a valid format, using the plain name", format);
                stack.setHoverName(Component.literal(name));
            }
        }
        return stack;
    }

    public static final class Serializer extends LootItemConditionalFunction.Serializer<KilledName> {
        @Override public void serialize(@Nonnull JsonObject json, @Nonnull KilledName value, @Nonnull JsonSerializationContext context) {
            super.serialize(json, value, context);
            if (!value.format.isEmpty()) { json.addProperty(FORMAT, value.format); }
            if (!value.tag.isEmpty()) { json.addProperty(TAG, value.tag); }
        }

        @Override @Nonnull public KilledName deserialize(@Nonnull JsonObject json, @Nonnull JsonDeserializationContext context, @Nonnull LootItemCondition[] conditions) {
            return new KilledName(conditions, GsonHelper.getAsString(json, FORMAT, "%s"), GsonHelper.getAsString(json, TAG, ""));
        }
    }
}
