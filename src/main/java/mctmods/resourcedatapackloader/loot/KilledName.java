package mctmods.resourcedatapackloader.loot;

import mctmods.resourcedatapackloader.util.ContentLog;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.entity.vehicle.AbstractMinecartContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.phys.Vec3;
import java.util.IllegalFormatException;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

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
        Entity looted = looted(context);
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

    @Nullable private static Entity looted(LootContext context) {
        if (context.getParamOrNull(LootContextParams.THIS_ENTITY) instanceof FishingHook) { return null; }
        Vec3 origin = context.getParamOrNull(LootContextParams.ORIGIN);
        if (origin == null || context.hasParam(LootContextParams.DAMAGE_SOURCE) || context.hasParam(LootContextParams.BLOCK_STATE) || context.hasParam(LootContextParams.TOOL)) { return context.getParamOrNull(LootContextParams.THIS_ENTITY); }
        if (context.getParamOrNull(LootContextParams.KILLER_ENTITY) instanceof AbstractMinecartContainer cart) { return cart; }
        BlockPos pos = BlockPos.containing(origin);
        if (origin.equals(Vec3.atCenterOf(pos)) && context.getLevel().getBlockEntity(pos) instanceof RandomizableContainerBlockEntity) { return null; }
        return context.getParamOrNull(LootContextParams.THIS_ENTITY);
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
