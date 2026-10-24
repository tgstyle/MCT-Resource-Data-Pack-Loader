package mctmods.resourcedatapackloader.content.extra;

import mctmods.resourcedatapackloader.content.def.EnchantmentDef;
import mctmods.resourcedatapackloader.content.def.EnchantmentEffectDef;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ContentEnchantment extends Enchantment {
    private final EnchantmentDef def;
    private final List<EnchantmentEffectDef> effects;
    @Nullable private final EnumEnchantmentType category;

    public ContentEnchantment(EnchantmentDef def, List<EnchantmentEffectDef> effects) {
        super(rarity(def.rarity), type(def.category), slots(def.slots));
        this.def = def;
        this.effects = effects;
        this.category = def.category.isEmpty() ? null : type(def.category);
        setRegistryName(def.registryName);
    }

    private static Rarity rarity(String name) {
        switch (name) {
            case "uncommon": return Rarity.UNCOMMON;
            case "rare": return Rarity.RARE;
            case "very_rare": return Rarity.VERY_RARE;
            default: return Rarity.COMMON;
        }
    }

    private static EnumEnchantmentType type(String name) {
        switch (name) {
            case "armor": return EnumEnchantmentType.ARMOR;
            case "armor_head": return EnumEnchantmentType.ARMOR_HEAD;
            case "armor_chest": return EnumEnchantmentType.ARMOR_CHEST;
            case "armor_legs": return EnumEnchantmentType.ARMOR_LEGS;
            case "armor_feet": return EnumEnchantmentType.ARMOR_FEET;
            case "weapon": return EnumEnchantmentType.WEAPON;
            case "digger": return EnumEnchantmentType.DIGGER;
            case "fishing_rod": return EnumEnchantmentType.FISHING_ROD;
            case "bow": return EnumEnchantmentType.BOW;
            case "wearable": return EnumEnchantmentType.WEARABLE;
            default: return EnumEnchantmentType.BREAKABLE;
        }
    }

    private static EntityEquipmentSlot[] slots(List<String> names) {
        EntityEquipmentSlot[] slots = new EntityEquipmentSlot[names.size()];
        for (int i = 0; i < slots.length; i++) {
            slots[i] = slot(names.get(i));
        }
        return slots;
    }

    private static EntityEquipmentSlot slot(String name) {
        for (EntityEquipmentSlot slot : EntityEquipmentSlot.values()) {
            if (slot.getName().equals(name)) { return slot; }
        }
        return EntityEquipmentSlot.MAINHAND;
    }

    @Override @Nonnull public String getName() { return def.name; }

    @Override public int getMaxLevel() { return def.maxLevel; }

    @Override public int getMinEnchantability(int level) { return def.minBase + def.minPerLevel * (level - 1); }

    @Override public int getMaxEnchantability(int level) { return def.maxBase + def.maxPerLevel * (level - 1); }

    @Override public boolean isTreasureEnchantment() { return def.treasure; }

    @Override public boolean canApplyAtEnchantingTable(@Nonnull ItemStack stack) {
        Item item = stack.getItem();
        if (category != null && category.canEnchantItem(item)) { return true; }
        return item.getRegistryName() != null && def.items.contains(item.getRegistryName().toString());
    }

    @Override public int calcModifierDamage(int level, @Nonnull DamageSource source) {
        if (source.canHarmInCreative()) { return 0; }
        int total = 0;
        for (EnchantmentEffectDef effect : effects) {
            if (effect.type.equals("protection") && protects(effect, source)) { total += (int) effect.amount(level); }
        }
        return total;
    }

    private static boolean protects(EnchantmentEffectDef effect, DamageSource source) {
        for (String kind : effect.from) {
            switch (kind) {
                case "all": return true;
                case "fire": if (source.isFireDamage()) { return true; } break;
                case "fall": if (source == DamageSource.FALL) { return true; } break;
                case "explosion": if (source.isExplosion()) { return true; } break;
                case "projectile": if (source.isProjectile()) { return true; } break;
                default: break;
            }
        }
        return false;
    }

    @Override public float calcDamageByCreature(int level, @Nonnull EnumCreatureAttribute creature) {
        float total = 0.0F;
        for (EnchantmentEffectDef effect : effects) {
            if (effect.type.equals("damage") && hits(effect.mob, creature)) { total += (float) effect.amount(level); }
        }
        return total;
    }

    private static boolean hits(String mob, EnumCreatureAttribute creature) {
        switch (mob) {
            case "undead": return creature == EnumCreatureAttribute.UNDEAD;
            case "arthropod": return creature == EnumCreatureAttribute.ARTHROPOD;
            case "illager": return creature == EnumCreatureAttribute.ILLAGER;
            default: return true;
        }
    }

    @Override public void onEntityDamaged(@Nonnull EntityLivingBase user, @Nonnull Entity target, int level) { apply("attack", user, target, level); }

    @Override public void onUserHurt(@Nonnull EntityLivingBase user, @Nonnull Entity attacker, int level) { apply("hurt", user, attacker, level); }

    private void apply(String trigger, EntityLivingBase user, Entity affected, int level) {
        if (!(affected instanceof EntityLivingBase) || EnchantmentHelper.getMaxEnchantmentLevel(this, user) <= 0) { return; }
        for (EnchantmentEffectDef effect : effects) {
            if (!effect.type.equals("apply_effect") || !effect.on.equals(trigger)) { continue; }
            Potion potion = Potion.getPotionFromResourceLocation(effect.potion);
            if (potion != null) { ((EntityLivingBase) affected).addPotionEffect(new PotionEffect(potion, effect.duration(level), effect.amplifier)); }
        }
    }
}
