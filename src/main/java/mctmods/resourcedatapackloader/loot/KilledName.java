package mctmods.resourcedatapackloader.loot;

import mctmods.resourcedatapackloader.util.ContentLog;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import java.util.IllegalFormatException;
import java.util.List;
import javax.annotation.Nonnull;

public final class KilledName extends LootItemConditionalFunction {
    public static final MapCodec<KilledName> CODEC = RecordCodecBuilder.mapCodec(instance -> commonFields(instance)
            .and(instance.group(
                    Codec.STRING.optionalFieldOf("format", "%s").forGetter(function -> function.format),
                    Codec.STRING.optionalFieldOf("tag", "").forGetter(function -> function.tag)))
            .apply(instance, KilledName::new));
    private final String format;
    private final String tag;

    private KilledName(List<LootItemCondition> conditions, String format, String tag) {
        super(conditions);
        this.format = format;
        this.tag = tag;
    }

    @Override @Nonnull public LootItemFunctionType<KilledName> getType() { return LootFunctions.KILLED_NAME.get(); }

    @Override @Nonnull protected ItemStack run(@Nonnull ItemStack stack, @Nonnull LootContext context) {
        Entity looted = context.getParamOrNull(LootContextParams.THIS_ENTITY);
        if (looted == null) { return stack; }
        String name = looted.getName().getString();
        if (!tag.isEmpty()) { CustomData.update(DataComponents.CUSTOM_DATA, stack, held -> held.putString(tag, name)); }
        if (!format.isEmpty()) {
            try { stack.set(DataComponents.CUSTOM_NAME, Component.literal(String.format(format, name))); }
            catch (IllegalFormatException bad) {
                ContentLog.LOGGER.error("A killed_name format '{}' is not a valid format, using the plain name", format);
                stack.set(DataComponents.CUSTOM_NAME, Component.literal(name));
            }
        }
        return stack;
    }
}
