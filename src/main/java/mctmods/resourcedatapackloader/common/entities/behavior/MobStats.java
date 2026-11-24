package mctmods.resourcedatapackloader.common.entities.behavior;

import mctmods.resourcedatapackloader.common.util.ContentAttributes;
import mctmods.resourcedatapackloader.core.config.Settings;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.IdNames;
import mctmods.resourcedatapackloader.core.util.json.Json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.util.ResourceLocation;

public final class MobStats {
    public static final String MONSTER = "#monster";
    private static final List<String> KINDS = Arrays.asList(MONSTER, "#creature", "#ambient", "#water_creature");

    private MobStats() {}

    public static boolean unknownEntity(String id) { return !IdNames.isTag(id) && !EntityList.isRegistered(new ResourceLocation(id)); }

    public static float health(EntityLivingBase living) { return living.getHealth() / living.getMaxHealth(); }

    @Nullable public static List<String> required(String owner, JsonObject json, String member, String label) {
        List<String> found = json.has(member) ? entities(owner, label, json.get(member)) : null;
        return found == null || found.isEmpty() ? null : found;
    }

    @Nullable public static List<String> entities(String owner, String label, JsonElement value) {
        if (!value.isJsonArray()) {
            ContentLog.LOGGER.error("{} gives {} that is not a list of entity ids and kinds, ignoring it", owner, label);
            return null;
        }
        List<String> found = new ArrayList<>();
        for (JsonElement entry : value.getAsJsonArray()) {
            if (!Json.isString(entry)) {
                ContentLog.LOGGER.error("{} gives {} an entry {} that is not text, ignoring it", owner, label, entry);
                continue;
            }
            String id = Settings.lower(entry.getAsString());
            if (IdNames.isTag(id)) {
                if (KINDS.contains(id)) { found.add(id); }
                else { ContentLog.LOGGER.error("{} gives {} the kind '{}', which is not one of #monster, #creature, #ambient or #water_creature, ignoring it", owner, label, id); }
            }
            else { found.add(id.indexOf(':') < 0 ? "minecraft:" + id : id); }
        }
        return found;
    }

    public static boolean targets(List<String> entities, Entity mob) {
        ResourceLocation key = EntityList.getKey(mob);
        String id = key == null ? "" : key.toString();
        for (String entity : entities) {
            if (IdNames.isTag(entity)) {
                if (mob.isCreatureType(EnumCreatureType.valueOf(IdNames.tagPath(entity).toUpperCase(Locale.ROOT)), false)) { return true; }
            }
            else if (entity.equals(id)) { return true; }
        }
        return false;
    }

    public static void scale(EntityLiving mob, IAttribute attribute, UUID id, float multiplier, String name) {
        IAttributeInstance instance = attribute(mob, attribute);
        if (instance == null || multiplier == 1.0F) { return; }
        ContentAttributes.replace(instance, id, name, multiplier - 1.0D, 2, true);
    }

    @Nullable public static IAttributeInstance attribute(EntityLiving mob, IAttribute attribute) { return mob.getAttributeMap().getAttributeInstanceByName(attribute.getName()); }
}
