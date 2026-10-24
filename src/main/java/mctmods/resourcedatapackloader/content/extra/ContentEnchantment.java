package mctmods.resourcedatapackloader.content.extra;

import mctmods.resourcedatapackloader.content.def.EnchantmentDef;
import mctmods.resourcedatapackloader.content.def.EnchantmentEffectDef;
import mctmods.resourcedatapackloader.content.util.ContentEffects;

import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.registries.ForgeRegistries;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class ContentEnchantment extends Enchantment {
    private final EnchantmentDef def;
    private final List<EnchantmentEffectDef> effects;
    @Nullable private final EnchantmentCategory named;

    public ContentEnchantment(EnchantmentDef def, List<EnchantmentEffectDef> effects) {
        super(rarity(def.rarity()), category(def.category()), slots(def.slots()));
        this.def = def;
        this.effects = effects;
        this.named = def.category().isEmpty() ? null : category(def.category());
    }

    private static Rarity rarity(String name) {
        return switch (name) {
            case "uncommon" -> Rarity.UNCOMMON;
            case "rare" -> Rarity.RARE;
            case "very_rare" -> Rarity.VERY_RARE;
            default -> Rarity.COMMON;
        };
    }

    private static EnchantmentCategory category(String name) {
        return switch (name) {
            case "armor" -> EnchantmentCategory.ARMOR;
            case "armor_head" -> EnchantmentCategory.ARMOR_HEAD;
            case "armor_chest" -> EnchantmentCategory.ARMOR_CHEST;
            case "armor_legs" -> EnchantmentCategory.ARMOR_LEGS;
            case "armor_feet" -> EnchantmentCategory.ARMOR_FEET;
            case "weapon" -> EnchantmentCategory.WEAPON;
            case "digger" -> EnchantmentCategory.DIGGER;
            case "fishing_rod" -> EnchantmentCategory.FISHING_ROD;
            case "bow" -> EnchantmentCategory.BOW;
            case "wearable" -> EnchantmentCategory.WEARABLE;
            default -> EnchantmentCategory.BREAKABLE;
        };
    }

    private static EquipmentSlot[] slots(List<String> names) {
        EquipmentSlot[] slots = new EquipmentSlot[names.size()];
        for (int i = 0; i < slots.length; i++) {
            slots[i] = EquipmentSlot.byName(names.get(i));
        }
        return slots;
    }

    @Override @Nonnull public String getDescriptionId() { return def.name(); }

    @Override public int getMaxLevel() { return def.maxLevel(); }

    @Override public int getMinCost(int level) { return def.minBase() + def.minPerLevel() * (level - 1); }

    @Override public int getMaxCost(int level) { return def.maxBase() + def.maxPerLevel() * (level - 1); }

    @Override public boolean isTreasureOnly() { return def.treasure(); }

    @Override public boolean canApplyAtEnchantingTable(@Nonnull ItemStack stack) {
        Item item = stack.getItem();
        if (named != null && named.canEnchant(item) && !(named == EnchantmentCategory.DIGGER && item instanceof HoeItem)) { return true; }
        return def.items().contains(String.valueOf(ForgeRegistries.ITEMS.getKey(item)));
    }

    @Override public int getDamageProtection(int level, @Nonnull DamageSource source) {
        if (source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) { return 0; }
        int total = 0;
        for (EnchantmentEffectDef effect : effects) {
            if (effect.type().equals("protection") && protects(effect, source)) { total += (int) effect.amount(level); }
        }
        return total;
    }

    private static boolean protects(EnchantmentEffectDef effect, DamageSource source) {
        for (String kind : effect.from()) {
            boolean match = switch (kind) {
                case "all" -> true;
                case "fire" -> source.is(DamageTypeTags.IS_FIRE);
                case "fall" -> source.is(DamageTypeTags.IS_FALL);
                case "explosion" -> source.is(DamageTypeTags.IS_EXPLOSION);
                case "projectile" -> source.is(DamageTypeTags.IS_PROJECTILE);
                default -> false;
            };
            if (match) { return true; }
        }
        return false;
    }

    @Override public float getDamageBonus(int level, @Nonnull MobType type, @Nonnull ItemStack stack) {
        float total = 0.0F;
        for (EnchantmentEffectDef effect : effects) {
            if (effect.type().equals("damage") && hits(effect.mob(), type)) { total += (float) effect.amount(level); }
        }
        return total;
    }

    private static boolean hits(String mob, MobType type) {
        return switch (mob) {
            case "undead" -> type == MobType.UNDEAD;
            case "arthropod" -> type == MobType.ARTHROPOD;
            case "illager" -> type == MobType.ILLAGER;
            default -> true;
        };
    }

    @Override public void doPostAttack(@Nonnull LivingEntity attacker, @Nonnull Entity target, int level) { apply("attack", attacker, target, level); }

    @Override public void doPostHurt(@Nonnull LivingEntity victim, @Nonnull Entity attacker, int level) { apply("hurt", victim, attacker, level); }

    private void apply(String trigger, LivingEntity user, Entity affected, int level) {
        if (!(affected instanceof LivingEntity living) || EnchantmentHelper.getEnchantmentLevel(this, user) <= 0) { return; }
        for (EnchantmentEffectDef effect : effects) {
            if (!effect.type().equals("apply_effect") || !effect.on().equals(trigger)) { continue; }
            MobEffect mobEffect = ContentEffects.find(effect.potion());
            if (mobEffect != null) { living.addEffect(new MobEffectInstance(mobEffect, effect.duration(level), effect.amplifier())); }
        }
    }
}
