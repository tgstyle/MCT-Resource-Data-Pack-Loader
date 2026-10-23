package mctmods.resourcedatapackloader.content.entity;

import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Settings;

import com.google.gson.JsonElement;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.util.ResourceLocation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import javax.annotation.Nullable;

public final class MobStats {
    public static final String MONSTER = "#monster";
    private static final List<String> KINDS = Arrays.asList("#monster", "#creature", "#ambient", "#water_creature");

    private MobStats() {}

    @Nullable public static List<String> entities(String owner, String label, JsonElement value) {
        if (!value.isJsonArray()) {
            ContentLog.LOGGER.error("{} gives {} that is not a list of entity ids and kinds, ignoring it", owner, label);
            return null;
        }
        List<String> found = new ArrayList<>();
        for (JsonElement entry : value.getAsJsonArray()) {
            if (!entry.isJsonPrimitive() || !entry.getAsJsonPrimitive().isString()) {
                ContentLog.LOGGER.error("{} gives {} an entry {} that is not text, ignoring it", owner, label, entry);
                continue;
            }
            String id = Settings.lower(entry.getAsString());
            if (id.startsWith("#")) {
                if (KINDS.contains(id)) { found.add(id); }
                else { ContentLog.LOGGER.error("{} gives {} the kind '{}', which is not one of #monster, #creature, #ambient or #water_creature, ignoring it", owner, label, id); }
            }
            else { found.add(id.indexOf(':') < 0 ? "minecraft:" + id : id); }
        }
        return found;
    }

    public static boolean targets(List<String> entities, EntityLiving mob) {
        ResourceLocation key = EntityList.getKey(mob);
        String id = key == null ? "" : key.toString();
        for (String entity : entities) {
            if (entity.startsWith("#")) {
                if (mob.isCreatureType(EnumCreatureType.valueOf(entity.substring(1).toUpperCase(Locale.ROOT)), false)) { return true; }
            }
            else if (entity.equals(id)) { return true; }
        }
        return false;
    }

    public static void scale(EntityLiving mob, IAttribute attribute, UUID id, float multiplier, String name) {
        IAttributeInstance instance = attribute(mob, attribute);
        if (instance == null || multiplier == 1.0F) { return; }
        if (instance.getModifier(id) != null) { instance.removeModifier(id); }
        instance.applyModifier(new AttributeModifier(id, name, multiplier - 1.0D, 2));
    }

    @Nullable public static IAttributeInstance attribute(EntityLiving mob, IAttribute attribute) { return mob.getAttributeMap().getAttributeInstanceByName(attribute.getName()); }
}
