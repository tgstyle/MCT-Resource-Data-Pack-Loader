package mctmods.resourcedatapackloader.common.scripts;

import java.util.List;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public final class ScriptDrops {
    private static final int MAX_ADDS = 32;
    private final List<EntityItem> drops;
    private final EntityLivingBase mob;
    private int added;

    ScriptDrops(List<EntityItem> drops, EntityLivingBase mob) {
        this.drops = drops;
        this.mob = mob;
    }

    public int size() { return drops.size(); }

    public ItemStack stack(int index) { return drops.get(index).getItem(); }

    public void remove(int index) { drops.remove(index); }

    public void clear() { drops.clear(); }

    public void add(String item, int count) {
        Item found = item == null ? null : Item.getByNameOrId(item);
        if (found == null || count < 1) { throw new IllegalArgumentException("Unknown item: " + item); }
        if (added >= MAX_ADDS) { throw new IllegalArgumentException("A loot_drop script can add at most 32 stacks"); }
        added++;
        drops.add(new EntityItem(mob.world, mob.posX, mob.posY, mob.posZ, new ItemStack(found, count)));
    }
}
