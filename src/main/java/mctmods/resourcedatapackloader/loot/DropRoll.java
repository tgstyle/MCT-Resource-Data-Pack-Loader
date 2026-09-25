package mctmods.resourcedatapackloader.loot;

import mctmods.resourcedatapackloader.content.def.DropDef;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nonnull;

public final class DropRoll extends LootItemConditionalFunction {
    public static final MapCodec<DropRoll> CODEC = RecordCodecBuilder.mapCodec(instance -> commonFields(instance)
            .and(instance.group(
                    Codec.INT.optionalFieldOf("chance", 100).forGetter(function -> function.chance),
                    Codec.INT.listOf().optionalFieldOf("bonusChance", List.of()).forGetter(function -> Arrays.stream(function.bonusChance).boxed().toList())))
            .apply(instance, DropRoll::new));
    private final int chance;
    private final int[] bonusChance;

    private DropRoll(List<LootItemCondition> conditions, int chance, List<Integer> bonusChance) {
        super(conditions);
        this.chance = chance;
        this.bonusChance = new IntArrayList(bonusChance).toIntArray();
    }

    @Override @Nonnull public LootItemFunctionType<DropRoll> getType() { return LootFunctions.DROP_ROLL.get(); }

    @Override @Nonnull protected ItemStack run(@Nonnull ItemStack stack, @Nonnull LootContext context) {
        ItemStack tool = context.getParamOrNull(LootContextParams.TOOL);
        int fortune = tool == null ? 0 : BlockDrops.level(context.getLevel(), tool, Enchantments.FORTUNE);
        stack.setCount(stack.getCount() * DropDef.copies(context.getRandom(), chance, bonusChance, fortune));
        return stack;
    }
}
