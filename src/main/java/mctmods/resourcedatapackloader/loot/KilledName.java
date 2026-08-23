package mctmods.resourcedatapackloader.loot;

import mctmods.resourcedatapackloader.util.ContentLog;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.functions.LootFunction;
import java.util.IllegalFormatException;
import java.util.Random;
import javax.annotation.Nonnull;

public class KilledName extends LootFunction {
    private final String format;
    private final String tag;

    public KilledName(LootCondition[] conditions, String format, String tag) {
        super(conditions);
        this.format = format;
        this.tag = tag;
    }

    @Override @Nonnull public ItemStack apply(@Nonnull ItemStack stack, @Nonnull Random rand, @Nonnull LootContext context) {
        Entity looted = context.getLootedEntity();
        if (looted == null) { return stack; }
        String name = looted.getName();
        if (!tag.isEmpty()) {
            NBTTagCompound held = stack.getTagCompound();
            if (held == null) {
                held = new NBTTagCompound();
                stack.setTagCompound(held);
            }
            held.setString(tag, name);
        }
        if (!format.isEmpty()) {
            try {
                stack.setStackDisplayName(String.format(format, name));
            } catch (IllegalFormatException bad) {
                ContentLog.LOGGER.error("A killed_name format '{}' is not a valid format, using the plain name", format);
                stack.setStackDisplayName(name);
            }
        }
        return stack;
    }

    public static class Serializer extends LootFunction.Serializer<KilledName> {
        public Serializer() { super(new ResourceLocation("rdpl", "killed_name"), KilledName.class); }

        @Override public void serialize(@Nonnull JsonObject json, @Nonnull KilledName value, @Nonnull JsonSerializationContext context) {
            if (!value.format.isEmpty()) { json.addProperty("format", value.format); }
            if (!value.tag.isEmpty()) { json.addProperty("tag", value.tag); }
        }

        @Override @Nonnull public KilledName deserialize(@Nonnull JsonObject json, @Nonnull JsonDeserializationContext context, @Nonnull LootCondition[] conditions) {
            return new KilledName(conditions, JsonUtils.getString(json, "format", "%s"), JsonUtils.getString(json, "tag", ""));
        }
    }
}
