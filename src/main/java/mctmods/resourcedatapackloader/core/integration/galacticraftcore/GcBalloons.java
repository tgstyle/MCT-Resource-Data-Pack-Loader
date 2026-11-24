package mctmods.resourcedatapackloader.core.integration.galacticraftcore;

import micdoodle8.mods.galacticraft.core.util.CompatibilityManager;
import micdoodle8.mods.galacticraft.planets.mars.entities.EntityLandingBalloons;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.WorldServer;

final class GcBalloons {
    private GcBalloons() {}

    static void land(WorldServer world, EntityPlayerMP player) {
        EntityLandingBalloons balloons = new EntityLandingBalloons(player);
        spawnForced(world, balloons, () -> { });
    }

    static void spawnForced(WorldServer world, Entity entity, Runnable inside) {
        boolean previous = CompatibilityManager.forceLoadChunks(world);
        entity.forceSpawn = true;
        world.spawnEntity(entity);
        inside.run();
        CompatibilityManager.forceLoadChunksEnd(world, previous);
    }
}
