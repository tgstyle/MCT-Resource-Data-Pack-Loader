package mctmods.resourcedatapackloader.content.util;

import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Registered;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.registries.ForgeRegistries;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import javax.annotation.Nullable;

public final class ContentAttributes {
    private static final Map<String, Attribute> ATTRIBUTES = new HashMap<>();

    private ContentAttributes() {}

    static {
        put("maxHealth", Attributes.MAX_HEALTH);
        put("followRange", Attributes.FOLLOW_RANGE);
        put("knockbackResistance", Attributes.KNOCKBACK_RESISTANCE);
        put("movementSpeed", Attributes.MOVEMENT_SPEED);
        put("flyingSpeed", Attributes.FLYING_SPEED);
        put("attackDamage", Attributes.ATTACK_DAMAGE);
        put("attackSpeed", Attributes.ATTACK_SPEED);
        put("attackKnockback", Attributes.ATTACK_KNOCKBACK);
        put("armor", Attributes.ARMOR);
        put("armorToughness", Attributes.ARMOR_TOUGHNESS);
        put("luck", Attributes.LUCK);
    }

    private static void put(String name, Attribute attribute) {
        ATTRIBUTES.put(key(name), attribute);
        ATTRIBUTES.put(key("generic." + name), attribute);
    }

    @Nullable public static Attribute find(String name, Object context) {
        Attribute attribute = ATTRIBUTES.get(key(name));
        if (attribute != null) { return attribute; }
        ResourceLocation id = ResourceLocation.tryParse(name);
        attribute = Registered.find(ForgeRegistries.ATTRIBUTES, id);
        if (attribute != null) { return attribute; }
        ContentLog.LOGGER.error("Unknown attribute '{}' in {}, skipping that modifier. Known names are {} or a registry id such as minecraft:generic.attack_damage", name, context, ATTRIBUTES.keySet());
        return null;
    }

    private static String key(String name) { return name.toLowerCase(Locale.ROOT); }
}
