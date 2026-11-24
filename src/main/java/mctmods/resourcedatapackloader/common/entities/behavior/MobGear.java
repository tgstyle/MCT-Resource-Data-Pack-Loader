package mctmods.resourcedatapackloader.common.entities.behavior;

import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.Enums;
import mctmods.resourcedatapackloader.core.util.Registries;

import java.util.List;
import java.util.Map;
import java.util.Random;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public final class MobGear {
    private static final EntityEquipmentSlot[] ARMOR = { EntityEquipmentSlot.HEAD, EntityEquipmentSlot.CHEST, EntityEquipmentSlot.LEGS, EntityEquipmentSlot.FEET };

    private MobGear() {}

    public static void fixed(EntityLiving mob, Map<String, String> equipment, float dropChance, String owner) {
        for (Map.Entry<String, String> entry : equipment.entrySet()) {
            EntityEquipmentSlot slot = Enums.byName(EntityEquipmentSlot.class, entry.getKey());
            if (slot == null) {
                ContentLog.LOGGER.error("{} names equipment slot '{}', which is not one of mainhand, offhand, head, chest, legs or feet", owner, entry.getKey());
                continue;
            }
            ResourceLocation name = new ResourceLocation(entry.getValue());
            Item item = Registries.find(ForgeRegistries.ITEMS, name);
            if (item == null) {
                ContentLog.LOGGER.error("{} gives {}, which nothing registers", owner, name);
                continue;
            }
            mob.setItemStackToSlot(slot, new ItemStack(item));
            mob.setDropChance(slot, dropChance);
        }
    }

    public static float[] chances(EntityLiving mob, EntityEquipmentSlot slot) { return slot.getSlotType() == EntityEquipmentSlot.Type.HAND ? mob.inventoryHandsDropChances : mob.inventoryArmorDropChances; }

    public static void random(EntityLiving mob, float armor, float armorPiece, float weapon, float offhand, float enchant, boolean digs, List<String> exclude, float dropChance) {
        Random rand = mob.world.rand;
        if (rand.nextFloat() < armor) {
            for (EntityEquipmentSlot slot : ARMOR) {
                if (slot != EntityEquipmentSlot.HEAD && rand.nextFloat() >= armorPiece) { continue; }
                give(mob, slot, enchant, exclude, dropChance);
            }
        }
        if (!digs && !(mob instanceof IRangedAttackMob) && rand.nextFloat() < weapon) { give(mob, EntityEquipmentSlot.MAINHAND, enchant, exclude, dropChance); }
        if (rand.nextFloat() < offhand) { give(mob, EntityEquipmentSlot.OFFHAND, enchant, exclude, dropChance); }
    }

    private static void give(EntityLiving mob, EntityEquipmentSlot slot, float enchant, List<String> exclude, float dropChance) {
        if (!mob.getItemStackFromSlot(slot).isEmpty()) { return; }
        List<Item> pool = MobGearPool.pool(slot, exclude);
        if (pool.isEmpty()) { return; }
        Random rand = mob.world.rand;
        ItemStack stack = new ItemStack(pool.get(rand.nextInt(pool.size())));
        if (rand.nextFloat() < enchant) { EnchantmentHelper.addRandomEnchantment(rand, stack, 5 + rand.nextInt(25), true); }
        mob.setItemStackToSlot(slot, stack);
        mob.setDropChance(slot, dropChance);
    }
}
