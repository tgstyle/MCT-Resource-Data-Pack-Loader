package mctmods.resourcedatapackloader.content;

import mctmods.resourcedatapackloader.compat.Compat;
import mctmods.resourcedatapackloader.content.def.BlockVariant;
import mctmods.resourcedatapackloader.content.def.DropDef;
import mctmods.resourcedatapackloader.util.WeightedPicks;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import javax.annotation.Nullable;

public final class ContentDrops {
    private ContentDrops() {}

    public static void removed(Level level, BlockPos pos, BlockState state, BlockState replaced) { if (level instanceof ServerLevel server && !state.is(replaced.getBlock())) { release(server, pos, state); } }

    private static void release(ServerLevel level, BlockPos pos, BlockState state) {
        ContentRegistry.BlockEntry entry = ContentRegistry.entry(state.getBlock());
        if (entry == null || entry.def().opensWith() != null) { return; }
        release(level, pos, entry.variant());
    }

    public static void release(ServerLevel level, BlockPos pos, BlockVariant variant) {
        pass(variant, true, level.getRandom(), drop -> spawn(level, pos, drop));
    }

    public static List<ItemStack> roll(BlockVariant variant, RandomSource random, int fortune) {
        List<ItemStack> made = new ArrayList<>();
        pass(variant, false, random, drop -> give(made, drop, random, fortune));
        return made;
    }

    private static void pass(BlockVariant variant, boolean entities, RandomSource random, Consumer<DropDef> apply) {
        List<DropDef> pool = new ArrayList<>();
        for (DropDef drop : variant.drops()) {
            if (drop.isEntity() != entities) { continue; }
            if (drop.weighted()) { pool.add(drop); }
            else { apply.accept(drop); }
        }
        DropDef chosen = pick(pool, random);
        if (chosen != null) { apply.accept(chosen); }
    }

    private static void give(List<ItemStack> made, DropDef drop, RandomSource random, int fortune) {
        Item item = ContentStacks.item(drop.item());
        if (item == null) { return; }
        int copies = DropDef.copies(random, drop.chance(), drop.bonusChance(), fortune);
        int amount = drop.amount().pick(random);
        if (amount <= 0) { return; }
        ItemStack stack = new ItemStack(item, amount);
        if (stack.isEmpty()) { return; }
        for (int i = 0; i < copies; i++) { made.add(stack.copy()); }
    }

    @Nullable public static DropDef pick(List<DropDef> pool, RandomSource random) { return WeightedPicks.pick(pool, drop -> Math.max(1, drop.weight()), random); }

    private static void spawn(ServerLevel level, BlockPos pos, DropDef drop) {
        if (drop.entity() == null || 1 + level.getRandom().nextInt(100) > drop.chance()) { return; }
        EntityType<?> type = Compat.entityType(drop.entity());
        if (type == null) { return; }
        int amount = drop.amount().pick(level.getRandom());
        for (int i = 0; i < amount; i++) { type.spawn(level, pos, EntitySpawnReason.MOB_SUMMONED); }
    }
}
