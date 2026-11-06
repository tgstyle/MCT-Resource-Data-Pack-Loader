package mctmods.resourcedatapackloader.common.entities.behavior;

import mctmods.resourcedatapackloader.common.stacks.ContentStacks;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.Enums;

import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

public final class MobGear {
    private static final EquipmentSlot[] ARMOR = { EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET };

    private MobGear() {}

    public static void fixed(Mob mob, Map<String, String> equipment, float dropChance, String owner) {
        for (Map.Entry<String, String> entry : equipment.entrySet()) {
            EquipmentSlot slot = slot(entry.getKey());
            if (slot == null) {
                ContentLog.LOGGER.error("Entity variant {} names equipment slot '{}', which is not one of mainhand, offhand, head, chest, legs or feet", owner, entry.getKey());
                continue;
            }
            Item item = ContentStacks.item(ResourceLocation.tryParse(entry.getValue()));
            if (item == null) {
                ContentLog.LOGGER.error("Entity variant {} gives {}, which nothing registers", owner, entry.getValue());
                continue;
            }
            mob.setItemSlot(slot, new ItemStack(item));
            mob.setDropChance(slot, dropChance);
        }
    }

    public static void random(Mob mob, float armor, float armorPiece, float weapon, float offhand, float enchant, boolean digs, List<String> exclude, float dropChance) {
        RandomSource rand = mob.getRandom();
        if (rand.nextFloat() < armor) {
            for (EquipmentSlot slot : ARMOR) {
                if (slot != EquipmentSlot.HEAD && rand.nextFloat() >= armorPiece) { continue; }
                give(mob, slot, enchant, exclude, dropChance);
            }
        }
        if (!digs && !(mob instanceof RangedAttackMob) && rand.nextFloat() < weapon) { give(mob, EquipmentSlot.MAINHAND, enchant, exclude, dropChance); }
        if (rand.nextFloat() < offhand) { give(mob, EquipmentSlot.OFFHAND, enchant, exclude, dropChance); }
    }

    private static void give(Mob mob, EquipmentSlot slot, float enchant, List<String> exclude, float dropChance) {
        if (!mob.getItemBySlot(slot).isEmpty()) { return; }
        List<Item> pool = MobGearPool.pool(slot, exclude);
        if (pool.isEmpty()) { return; }
        RandomSource rand = mob.getRandom();
        ItemStack stack = new ItemStack(pool.get(rand.nextInt(pool.size())));
        if (rand.nextFloat() < enchant) { stack = EnchantmentHelper.enchantItem(rand, stack, 5 + rand.nextInt(25), true); }
        mob.setItemSlot(slot, stack);
        mob.setDropChance(slot, dropChance);
    }

    @Nullable static EquipmentSlot slot(String name) { return Enums.byName(EquipmentSlot.class, name); }
}
