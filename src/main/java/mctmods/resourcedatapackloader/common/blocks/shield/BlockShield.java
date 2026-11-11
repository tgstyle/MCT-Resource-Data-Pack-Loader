package mctmods.resourcedatapackloader.common.blocks.shield;

import mctmods.resourcedatapackloader.common.pack.definitions.blocks.SpawnShieldDef;

import java.util.List;
import java.util.Set;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Enemy;

public final class BlockShield {
    public final int spawnRadius;
    public final int weatherRadius;
    private final SpawnShieldDef.Mobs mobs;
    private final Set<Identifier> entities;
    private final List<TagKey<EntityType<?>>> tags;

    public BlockShield(int spawnRadius, SpawnShieldDef.Mobs mobs, Set<Identifier> entities, List<TagKey<EntityType<?>>> tags, int weatherRadius) {
        this.spawnRadius = spawnRadius;
        this.mobs = mobs;
        this.entities = entities;
        this.tags = tags;
        this.weatherRadius = weatherRadius;
    }

    public boolean blocksSpawns() { return spawnRadius > 0; }

    public boolean blocksWeather() { return weatherRadius > 0; }

    public boolean stops(Entity entity) {
        boolean hostile = entity instanceof Enemy;
        if (mobs == SpawnShieldDef.Mobs.HOSTILE && !hostile) { return false; }
        if (mobs == SpawnShieldDef.Mobs.PASSIVE && hostile) { return false; }
        if (entities.isEmpty() && tags.isEmpty()) { return true; }
        EntityType<?> type = entity.getType();
        if (entities.contains(BuiltInRegistries.ENTITY_TYPE.getKey(type))) { return true; }
        for (TagKey<EntityType<?>> tag : tags) {
            if (BuiltInRegistries.ENTITY_TYPE.wrapAsHolder(type).is(tag)) { return true; }
        }
        return false;
    }
}
