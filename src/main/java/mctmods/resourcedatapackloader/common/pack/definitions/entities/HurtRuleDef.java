package mctmods.resourcedatapackloader.common.pack.definitions.entities;

import mctmods.resourcedatapackloader.common.entities.behavior.MobStats;

import java.util.List;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

public record HurtRuleDef(Who attackers, Who victims, boolean cancel, String runs) {
    public record Who(List<Identifier> ids, List<String> kinds, List<TagKey<EntityType<?>>> tags) {
        public boolean listsOnlyIds() { return kinds.isEmpty() && tags.isEmpty(); }

        public boolean matches(Entity entity) {
            if (MobStats.targets(ids, kinds, entity)) { return true; }
            for (TagKey<EntityType<?>> tag : tags) {
                if (BuiltInRegistries.ENTITY_TYPE.wrapAsHolder(entity.getType()).is(tag)) { return true; }
            }
            return false;
        }
    }
}
