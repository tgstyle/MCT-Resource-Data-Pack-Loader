package mctmods.resourcedatapackloader.content.entity;

import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Settings;

import com.google.gson.JsonElement;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;

public final class MobStats {
    public static final String MONSTER = "#monster";
    private static final List<String> KINDS = List.of("#monster", "#creature", "#ambient", "#water_creature");

    private MobStats() {}

    @Nullable public static List<String> entities(String owner, String label, JsonElement element) {
        if (!element.isJsonArray()) {
            ContentLog.LOGGER.error("{} gives {} that is not a list of entity ids and kinds, ignoring it", owner, label);
            return null;
        }
        List<String> listed = new ArrayList<>();
        for (JsonElement item : element.getAsJsonArray()) {
            if (!item.isJsonPrimitive() || !item.getAsJsonPrimitive().isString()) {
                ContentLog.LOGGER.error("{} gives a {} entry that is not text, ignoring it", owner, label);
                continue;
            }
            String id = Settings.lower(item.getAsString());
            if (id.startsWith("#")) {
                if (KINDS.contains(id)) { listed.add(id); }
                else { ContentLog.LOGGER.error("{} gives {} '{}', which is not a kind, ignoring it", owner, label, id); }
            }
            else { listed.add(id.indexOf(':') < 0 ? "minecraft:" + id : id); }
        }
        return listed;
    }

    public static boolean targets(List<String> entities, Mob mob) {
        if (entities.isEmpty()) { return false; }
        String id = String.valueOf(BuiltInRegistries.ENTITY_TYPE.getKey(mob.getType()));
        String kind = "#" + mob.getType().getCategory().getName();
        return entities.contains(id) || entities.contains(kind);
    }

    public static boolean scale(Mob mob, Holder<Attribute> attribute, ResourceLocation id, float multiplier) {
        AttributeInstance instance = mob.getAttribute(attribute);
        if (instance == null || multiplier == 1.0F) { return false; }
        instance.removeModifier(id);
        instance.addPermanentModifier(new AttributeModifier(id, multiplier - 1.0F, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
        return true;
    }
}
