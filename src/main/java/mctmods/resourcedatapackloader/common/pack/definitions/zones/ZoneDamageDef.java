package mctmods.resourcedatapackloader.common.pack.definitions.zones;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public final class ZoneDamageDef {
    private final Set<ResourceLocation> ids = new HashSet<>();
    private final List<MobCategory> kinds = new ArrayList<>();
    private final List<TagKey<EntityType<?>>> tags = new ArrayList<>();
    public final boolean unlessAttackerInside;
    public final boolean allowNoAttacker;

    public ZoneDamageDef(List<String> entities, boolean unlessAttackerInside, boolean allowNoAttacker) {
        for (String entity : entities) {
            if (!entity.startsWith("#")) { ids.add(ResourceLocation.parse(entity)); }
            else if (entity.indexOf(':') > 0) { tags.add(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.parse(entity.substring(1)))); }
            else { addKind(entity.substring(1)); }
        }
        this.unlessAttackerInside = unlessAttackerInside;
        this.allowNoAttacker = allowNoAttacker;
    }

    public boolean covers(Entity victim) {
        EntityType<?> type = victim.getType();
        if (ids.contains(BuiltInRegistries.ENTITY_TYPE.getKey(type)) || kinds.contains(type.getCategory())) { return true; }
        for (TagKey<EntityType<?>> tag : tags) {
            if (type.is(tag)) { return true; }
        }
        return false;
    }

    private void addKind(String name) {
        for (MobCategory category : MobCategory.values()) {
            if (category.getName().equals(name)) { kinds.add(category); }
        }
    }
}
