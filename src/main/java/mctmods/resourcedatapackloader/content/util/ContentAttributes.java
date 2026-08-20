package mctmods.resourcedatapackloader.content.util;

import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttribute;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import javax.annotation.Nullable;

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

    @Nullable public static IAttribute find(String name, Object context) {
        IAttribute attribute = ATTRIBUTES.get(key(name));
        if (attribute != null) { return attribute; }
        ContentLog.LOGGER.error("Unknown attribute '{}' in {}, skipping that modifier", name, context);
        return null;
    }

    private static String key(String name) { return name.toLowerCase(Locale.ROOT); }
}
