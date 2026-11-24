package mctmods.resourcedatapackloader.common.enchantments;

import mctmods.resourcedatapackloader.common.pack.definitions.effects.EnchantmentDef;
import mctmods.resourcedatapackloader.common.pack.definitions.effects.EnchantmentEffectDef;
import mctmods.resourcedatapackloader.common.util.TableMath;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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

public class ContentEnchantment extends Enchantment {
    static final Map<String, Rarity> RARITIES = new LinkedHashMap<>();
    static final Map<String, EnumEnchantmentType> CATEGORIES = new LinkedHashMap<>();
    static final Map<String, Predicate<DamageSource>> PROTECTIONS = new LinkedHashMap<>();
    static final Map<String, EnumCreatureAttribute> MOBS = new LinkedHashMap<>();
    static {
        RARITIES.put("common", Rarity.COMMON);
        RARITIES.put("uncommon", Rarity.UNCOMMON);
        RARITIES.put("rare", Rarity.RARE);
        RARITIES.put("very_rare", Rarity.VERY_RARE);
        CATEGORIES.put("armor", EnumEnchantmentType.ARMOR);
        CATEGORIES.put("armor_head", EnumEnchantmentType.ARMOR_HEAD);
        CATEGORIES.put("armor_chest", EnumEnchantmentType.ARMOR_CHEST);
        CATEGORIES.put("armor_legs", EnumEnchantmentType.ARMOR_LEGS);
        CATEGORIES.put("armor_feet", EnumEnchantmentType.ARMOR_FEET);
        CATEGORIES.put("weapon", EnumEnchantmentType.WEAPON);
        CATEGORIES.put("digger", EnumEnchantmentType.DIGGER);
        CATEGORIES.put("fishing_rod", EnumEnchantmentType.FISHING_ROD);
        CATEGORIES.put("breakable", EnumEnchantmentType.BREAKABLE);
        CATEGORIES.put("bow", EnumEnchantmentType.BOW);
        CATEGORIES.put("wearable", EnumEnchantmentType.WEARABLE);
        PROTECTIONS.put("all", source -> true);
        PROTECTIONS.put("fire", DamageSource::isFireDamage);
        PROTECTIONS.put("fall", source -> source == DamageSource.FALL);
        PROTECTIONS.put("explosion", DamageSource::isExplosion);
        PROTECTIONS.put("projectile", DamageSource::isProjectile);
        MOBS.put("any", null);
        MOBS.put("undead", EnumCreatureAttribute.UNDEAD);
        MOBS.put("arthropod", EnumCreatureAttribute.ARTHROPOD);
        MOBS.put("illager", EnumCreatureAttribute.ILLAGER);
    }

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

    private static Rarity rarity(String name) { return RARITIES.getOrDefault(name, Rarity.COMMON); }

    private static EnumEnchantmentType type(String name) { return CATEGORIES.getOrDefault(name, EnumEnchantmentType.BREAKABLE); }

    private static EntityEquipmentSlot[] slots(List<String> names) {
        EntityEquipmentSlot[] slots = new EntityEquipmentSlot[names.size()];
        for (int i = 0; i < slots.length; i++) { slots[i] = slot(names.get(i)); }
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

    @Override public int getMinEnchantability(int level) { return (int) TableMath.perLevel(def.minBase, def.minPerLevel, level); }

    @Override public int getMaxEnchantability(int level) { return (int) TableMath.perLevel(def.maxBase, def.maxPerLevel, level); }

    @Override public boolean isTreasureEnchantment() { return def.treasure; }

    @Override public boolean canApplyAtEnchantingTable(@Nonnull ItemStack stack) {
        Item item = stack.getItem();
        if (category != null && category.canEnchantItem(item)) { return true; }
        return item.getRegistryName() != null && def.items.contains(item.getRegistryName().toString());
    }

    @Override public int calcModifierDamage(int level, @Nonnull DamageSource source) {
        if (source.canHarmInCreative()) { return 0; }
        return (int) sum("protection", effect -> protects(effect, source), level);
    }

    private float sum(String type, Predicate<EnchantmentEffectDef> applies, int level) {
        float total = 0.0F;
        for (EnchantmentEffectDef effect : effects) {
            if (effect.type.equals(type) && applies.test(effect)) { total += (float) effect.amount(level); }
        }
        return total;
    }

    private static boolean protects(EnchantmentEffectDef effect, DamageSource source) {
        for (String kind : effect.from) {
            Predicate<DamageSource> covers = PROTECTIONS.get(kind);
            if (covers != null && covers.test(source)) { return true; }
        }
        return false;
    }

    @Override public float calcDamageByCreature(int level, @Nonnull EnumCreatureAttribute creature) {
        return sum("damage", effect -> hits(effect.mob, creature), level);
    }

    private static boolean hits(String mob, EnumCreatureAttribute creature) {
        EnumCreatureAttribute wanted = MOBS.get(mob);
        return wanted == null || creature == wanted;
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
