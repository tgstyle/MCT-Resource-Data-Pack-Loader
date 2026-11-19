package mctmods.resourcedatapackloader.common.pack.definitions.zones;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.ResourceLocation;

public final class ZoneDamageDef {
    private final Set<ResourceLocation> ids = new HashSet<>();
    private final EnumCreatureType[] kinds;
    public final boolean unlessAttackerInside;
    public final boolean allowNoAttacker;

    public ZoneDamageDef(List<String> entities, boolean unlessAttackerInside, boolean allowNoAttacker) {
        List<EnumCreatureType> found = new ArrayList<>();
        for (String entity : entities) {
            if (entity.startsWith("#")) { found.add(EnumCreatureType.valueOf(entity.substring(1).toUpperCase(Locale.ROOT))); }
            else { ids.add(new ResourceLocation(entity)); }
        }
        this.kinds = found.toArray(new EnumCreatureType[0]);
        this.unlessAttackerInside = unlessAttackerInside;
        this.allowNoAttacker = allowNoAttacker;
    }

    public boolean covers(Entity victim) {
        if (ids.contains(EntityList.getKey(victim))) { return true; }
        for (EnumCreatureType kind : kinds) {
            if (victim.isCreatureType(kind, false)) { return true; }
        }
        return false;
    }
}
