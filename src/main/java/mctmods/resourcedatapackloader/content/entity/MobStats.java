package mctmods.resourcedatapackloader.content.entity;

import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Settings;

import com.google.gson.JsonElement;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import java.util.Collection;
import java.util.List;

public final class MobStats {
    public static final String MONSTER = "monster";
    private static final List<String> KINDS = List.of("monster", "creature", "ambient", "water_creature");

    private MobStats() {}

    public static boolean entities(String owner, String label, JsonElement element, Collection<Identifier> ids, Collection<String> kinds) {
        if (!element.isJsonArray()) {
            ContentLog.LOGGER.error("{} gives {} that is not a list of entity ids or kinds, ignoring it", owner, label);
            return false;
        }
        for (JsonElement listedEntry : element.getAsJsonArray()) {
            if (!listedEntry.isJsonPrimitive()) {
                ContentLog.LOGGER.error("{} gives a {} entry that is not text, ignoring it", owner, label);
                continue;
            }
            String text = Settings.lower(listedEntry.getAsString());
            if (text.startsWith("#")) {
                String kind = text.substring(1);
                if (KINDS.contains(kind)) { kinds.add(kind); }
                else { ContentLog.LOGGER.error("{} gives {} '{}', which is not one of #monster, #creature, #ambient or #water_creature, ignoring it", owner, label, text); }
                continue;
            }
            Identifier id = Identifier.tryParse(text);
            if (id == null) { ContentLog.LOGGER.error("{} gives {} '{}', which is not an entity id, ignoring it", owner, label, text); }
            else { ids.add(id); }
        }
        return true;
    }

    public static boolean targets(Collection<Identifier> ids, Collection<String> kinds, Entity entity) {
        return ids.contains(BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType())) || kinds.contains(entity.getType().getCategory().getName());
    }

    public static boolean scale(Mob mob, Holder<Attribute> attribute, Identifier id, float multiplier) {
        AttributeInstance instance = mob.getAttribute(attribute);
        if (instance == null || multiplier == 1.0F) { return false; }
        instance.addOrReplacePermanentModifier(new AttributeModifier(id, multiplier - 1.0D, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
        return true;
    }
}
