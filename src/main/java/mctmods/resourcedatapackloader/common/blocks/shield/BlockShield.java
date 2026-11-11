package mctmods.resourcedatapackloader.common.blocks.shield;

import mctmods.resourcedatapackloader.common.pack.definitions.blocks.SpawnShieldDef;

import java.util.Set;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.monster.IMob;
import net.minecraft.util.ResourceLocation;

public final class BlockShield {
    public final int spawnRadius;
    public final int weatherRadius;
    private final SpawnShieldDef.Mobs mobs;
    private final Set<ResourceLocation> entities;

    public BlockShield(int spawnRadius, SpawnShieldDef.Mobs mobs, Set<ResourceLocation> entities, int weatherRadius) {
        this.spawnRadius = spawnRadius;
        this.mobs = mobs;
        this.entities = entities;
        this.weatherRadius = weatherRadius;
    }

    public boolean blocksSpawns() { return spawnRadius > 0; }

    public boolean blocksWeather() { return weatherRadius > 0; }

    public boolean stops(Entity entity) {
        boolean hostile = entity instanceof IMob;
        if (mobs == SpawnShieldDef.Mobs.HOSTILE && !hostile) { return false; }
        if (mobs == SpawnShieldDef.Mobs.PASSIVE && hostile) { return false; }
        if (entities.isEmpty()) { return true; }
        ResourceLocation key = EntityList.getKey(entity);
        return key != null && entities.contains(key);
    }
}
