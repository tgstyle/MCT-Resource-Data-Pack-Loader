package mctmods.resourcedatapackloader.util.compat;

import micdoodle8.mods.galacticraft.core.util.CompatibilityManager;
import micdoodle8.mods.galacticraft.planets.mars.entities.EntityLandingBalloons;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.WorldServer;

final class GcBalloons {
    private GcBalloons() {}

    static void land(WorldServer world, EntityPlayerMP player) {
        EntityLandingBalloons balloons = new EntityLandingBalloons(player);
        boolean previous = CompatibilityManager.forceLoadChunks(world);
        balloons.forceSpawn = true;
        world.spawnEntity(balloons);
        CompatibilityManager.forceLoadChunksEnd(world, previous);
    }
}
