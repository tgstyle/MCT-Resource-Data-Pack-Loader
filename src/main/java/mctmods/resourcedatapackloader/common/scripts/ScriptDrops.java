package mctmods.resourcedatapackloader.common.scripts;

import java.util.Collection;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

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

    public ItemStack stack(int index) { return at(index).getItem(); }

    public void remove(int index) { drops.remove(at(index)); }

    public void clear() { drops.clear(); }

    public void add(String item, int count) {
        ResourceLocation id = item == null ? null : ResourceLocation.tryParse(item);
        Item found = id == null ? null : ForgeRegistries.ITEMS.getValue(id);
        if (found == null || !ForgeRegistries.ITEMS.containsKey(id) || count < 1) { throw new IllegalArgumentException("Unknown item: " + item); }
        if (added >= MAX_ADDS) { throw new IllegalArgumentException("A loot_drop script can add at most 32 stacks"); }
        added++;
        drops.add(new ItemEntity(mob.level(), mob.getX(), mob.getY(), mob.getZ(), new ItemStack(found, count)));
    }

    private ItemEntity at(int index) {
        if (index < 0 || index >= drops.size()) { throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + drops.size()); }
        int at = 0;
        for (ItemEntity drop : drops) {
            if (at++ == index) { return drop; }
        }
        throw new IndexOutOfBoundsException("Index: " + index);
    }
}
