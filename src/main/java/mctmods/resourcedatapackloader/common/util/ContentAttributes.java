package mctmods.resourcedatapackloader.common.util;

import mctmods.resourcedatapackloader.core.util.ContentLog;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;

public final class ContentAttributes {
    private static final Map<String, IAttribute> ATTRIBUTES = new HashMap<>();

    private ContentAttributes() {}

    static {
        put("maxHealth", SharedMonsterAttributes.MAX_HEALTH);
        put("followRange", SharedMonsterAttributes.FOLLOW_RANGE);
        put("knockbackResistance", SharedMonsterAttributes.KNOCKBACK_RESISTANCE);
        put("movementSpeed", SharedMonsterAttributes.MOVEMENT_SPEED);
        put("flyingSpeed", SharedMonsterAttributes.FLYING_SPEED);
        put("attackDamage", SharedMonsterAttributes.ATTACK_DAMAGE);
        put("attackSpeed", SharedMonsterAttributes.ATTACK_SPEED);
        put("armor", SharedMonsterAttributes.ARMOR);
        put("armorToughness", SharedMonsterAttributes.ARMOR_TOUGHNESS);
        put("luck", SharedMonsterAttributes.LUCK);
    }

    private static void put(String name, IAttribute attribute) {
        ATTRIBUTES.put(key(name), attribute);
        ATTRIBUTES.put(key("generic." + name), attribute);
    }

    @Nullable public static IAttribute lookup(String name) { return ATTRIBUTES.get(key(name)); }

    public static double followRange(EntityLivingBase living) { return living.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).getAttributeValue(); }

    @Nullable public static IAttributeInstance instance(AbstractAttributeMap map, String name) {
        IAttribute attribute = lookup(name);
        IAttributeInstance instance = map.getAttributeInstanceByName(attribute == null ? name : attribute.getName());
        return instance == null && attribute != null ? map.registerAttribute(attribute) : instance;
    }

    @Nullable public static IAttributeInstance instance(AbstractAttributeMap map, String name, Object context) {
        IAttributeInstance instance = instance(map, name);
        if (instance == null) { unknown(name, context); }
        return instance;
    }

    @Nullable public static IAttribute find(String name, Object context) {
        IAttribute attribute = lookup(name);
        if (attribute == null) { unknown(name, context); }
        return attribute;
    }

    public static void replace(IAttributeInstance instance, UUID id, String name, double amount, int operation, boolean saved) {
        instance.removeModifier(id);
        instance.applyModifier(new AttributeModifier(id, name, amount, operation).setSaved(saved));
    }

    private static void unknown(String name, Object context) { ContentLog.LOGGER.error("Unknown attribute '{}' in {}, skipping that modifier", name, context); }

    private static String key(String name) { return name.toLowerCase(Locale.ROOT); }
}
