package mctmods.resourcedatapackloader.common.scripts;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public final class ScriptDrops {
    private static final int MAX_ADDS = 32;
    private final Collection<ItemEntity> drops;
    private final LivingEntity mob;
    private int added;

    ScriptDrops(Collection<ItemEntity> drops, LivingEntity mob) {
        this.drops = drops;
        this.mob = mob;
    }

    public int size() { return drops.size(); }

    public ItemStack stack(int index) { return list().get(index).getItem(); }

    public void remove(int index) { drops.remove(list().get(index)); }

    public void clear() { drops.clear(); }

    public void add(String item, int count) {
        Identifier id = item == null ? null : Identifier.tryParse(item);
        Item found = id == null ? null : BuiltInRegistries.ITEM.getOptional(id).orElse(null);
        if (found == null || count < 1) { throw new IllegalArgumentException("Unknown item: " + item); }
        if (added >= MAX_ADDS) { throw new IllegalArgumentException("A loot_drop script can add at most 32 stacks"); }
        added++;
        drops.add(new ItemEntity(mob.level(), mob.getX(), mob.getY(), mob.getZ(), new ItemStack(found, count)));
    }

    private List<ItemEntity> list() { return new ArrayList<>(drops); }
}
