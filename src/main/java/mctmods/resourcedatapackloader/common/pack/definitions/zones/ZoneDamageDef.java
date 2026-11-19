package mctmods.resourcedatapackloader.common.pack.definitions.zones;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.ForgeRegistries;

public final class ZoneDamageDef {
    private final Set<ResourceLocation> ids = new HashSet<>();
    private final EnumSet<MobCategory> kinds = EnumSet.noneOf(MobCategory.class);
    public final boolean unlessAttackerInside;
    public final boolean allowNoAttacker;

    public ZoneDamageDef(List<String> entities, boolean unlessAttackerInside, boolean allowNoAttacker) {
        for (String entity : entities) {
            if (entity.startsWith("#")) { addKind(entity.substring(1)); }
            else { addId(entity); }
        }
        this.unlessAttackerInside = unlessAttackerInside;
        this.allowNoAttacker = allowNoAttacker;
    }

    public boolean covers(Entity victim) { return ids.contains(ForgeRegistries.ENTITY_TYPES.getKey(victim.getType())) || kinds.contains(victim.getType().getCategory()); }

    private void addId(String entity) {
        ResourceLocation id = ResourceLocation.tryParse(entity);
        if (id != null) { ids.add(id); }
    }

    private void addKind(String name) {
        for (MobCategory kind : MobCategory.values()) {
            if (kind.getName().equals(name)) { kinds.add(kind); }
        }
    }
}
