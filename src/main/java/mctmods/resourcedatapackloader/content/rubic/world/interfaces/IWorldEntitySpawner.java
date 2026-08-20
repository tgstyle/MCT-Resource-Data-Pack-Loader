package mctmods.resourcedatapackloader.content.rubic.world.interfaces;

import net.minecraft.world.WorldServer;
import javax.annotation.Nullable;

public interface IWorldEntitySpawner {
    int findChunksForSpawning(WorldServer world, boolean hostileEnable, boolean peacefulEnable, boolean spawnOnSetTickRate);

    interface Handler { void rdpl$setEntitySpawner(@Nullable IWorldEntitySpawner spawner); }
}
