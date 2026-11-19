package mctmods.resourcedatapackloader.common.pack.definitions.zones;

import mctmods.resourcedatapackloader.common.entities.behavior.MobStats;

import java.util.List;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;

public record ZoneDamageDef(List<Identifier> ids, List<String> kinds, boolean unlessAttackerInside, boolean allowNoAttacker) {
    public boolean covers(Entity victim) { return MobStats.targets(ids, kinds, victim); }
}
