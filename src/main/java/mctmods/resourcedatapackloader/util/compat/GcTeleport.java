package mctmods.resourcedatapackloader.util.compat;

import mctmods.resourcedatapackloader.content.def.DimensionDef;
import mctmods.resourcedatapackloader.content.def.GalacticraftDef;
import mctmods.resourcedatapackloader.content.worldgen.ContentDimensions;

import micdoodle8.mods.galacticraft.api.vector.Vector3;
import micdoodle8.mods.galacticraft.api.world.ITeleportType;
import micdoodle8.mods.galacticraft.core.entities.EntityLander;
import micdoodle8.mods.galacticraft.core.entities.EntityLanderBase;
import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerStats;
import micdoodle8.mods.galacticraft.core.util.CompatibilityManager;
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.Loader;
import java.util.Random;
import javax.annotation.Nullable;

public final class GcTeleport implements ITeleportType {
    private static final double LANDER_HEIGHT = 900.0D;
    private static final double PARACHUTE_HEIGHT = 250.0D;
    private static final double CHEST_HEIGHT = 220.0D;
    private static final String PLANETS = "galacticraftplanets";
    private static final String EXTRA_PLANETS = "extraplanets";
    private int landingDimension;

    @Nullable private static GalacticraftDef gc(int dimension) {
        DimensionDef def = ContentDimensions.byId(dimension);
        return def == null ? null : def.galacticraft;
    }

    private static boolean parachutes(int dimension) {
        GalacticraftDef gc = gc(dimension);
        return ConfigManagerCore.disableLander || gc != null && GalacticraftDef.PARACHUTE.equals(gc.landing);
    }

    private static double height(int dimension) {
        GalacticraftDef gc = gc(dimension);
        if (gc != null && gc.landingHeight > 0.0D) { return gc.landingHeight; }
        return parachutes(dimension) ? PARACHUTE_HEIGHT : LANDER_HEIGHT;
    }

    private static boolean balloons(int dimension) {
        GalacticraftDef gc = gc(dimension);
        return gc != null && GalacticraftDef.BALLOONS.equals(gc.landing) && Loader.isModLoaded(PLANETS);
    }

    @Override public boolean useParachute() { return parachutes(landingDimension); }

    @Override @Nullable public Vector3 getPlayerSpawnLocation(WorldServer world, EntityPlayerMP player) {
        landingDimension = world.provider.getDimension();
        if (player == null) { return null; }
        double height = height(landingDimension);
        GalacticraftDef gc = gc(landingDimension);
        if (gc != null && GalacticraftDef.SPAWN.equals(gc.arrival)) {
            BlockPos spawn = world.getSpawnPoint();
            return new Vector3(spawn.getX() + 0.5D, height, spawn.getZ() + 0.5D);
        }
        GCPlayerStats stats = GCPlayerStats.get(player);
        double x = stats.getCoordsTeleportedFromX();
        double z = stats.getCoordsTeleportedFromZ();
        int limit = ConfigManagerCore.otherPlanetWorldBorders - 2;
        if (limit > 20) {
            if (x > limit) {
                z *= limit / x;
                x = limit;
            }
            else if (x < -limit) {
                z *= -limit / x;
                x = -limit;
            }
            if (z > limit) {
                x *= limit / z;
                z = limit;
            }
            else if (z < -limit) {
                x *= -limit / z;
                z = -limit;
            }
        }
        return new Vector3(x, height, z);
    }

    @Override public Vector3 getEntitySpawnLocation(WorldServer world, Entity entity) {
        landingDimension = world.provider.getDimension();
        return new Vector3(entity.posX, height(landingDimension), entity.posZ);
    }

    @Override @Nullable public Vector3 getParaChestSpawnLocation(WorldServer world, EntityPlayerMP player, Random rand) {
        if (!parachutes(world.provider.getDimension())) { return null; }
        double x = (rand.nextDouble() * 2.0D - 1.0D) * 4.0D;
        double z = (rand.nextDouble() * 2.0D - 1.0D) * 4.0D;
        return new Vector3(player.posX + x, CHEST_HEIGHT, player.posZ + z);
    }

    @Override public void onSpaceDimensionChanged(World newWorld, EntityPlayerMP player, boolean ridingAutoRocket) {
        if (ridingAutoRocket || parachutes(newWorld.provider.getDimension())) { return; }
        GCPlayerStats stats = GCPlayerStats.get(player);
        if (stats.getTeleportCooldown() > 0) { return; }
        player.capabilities.isFlying = false;
        if (balloons(newWorld.provider.getDimension())) {
            if (!newWorld.isRemote) { GcBalloons.land((WorldServer) newWorld, player); }
            stats.setTeleportCooldown(10);
            return;
        }
        GalacticraftDef gc = gc(newWorld.provider.getDimension());
        EntityLanderBase lander = gc != null && gc.extraPlanets != null && gc.extraPlanets.lander != null && Loader.isModLoaded(EXTRA_PLANETS) ? EpPlanets.lander(gc.extraPlanets.lander, player) : new EntityLander(player);
        lander.setPosition(player.posX, player.posY, player.posZ);
        if (!newWorld.isRemote) {
            boolean previous = CompatibilityManager.forceLoadChunks((WorldServer) newWorld);
            lander.forceSpawn = true;
            newWorld.spawnEntity(lander);
            lander.setWorld(newWorld);
            newWorld.updateEntityWithOptionalForce(lander, true);
            player.startRiding(lander);
            CompatibilityManager.forceLoadChunksEnd((WorldServer) newWorld, previous);
        }
        stats.setTeleportCooldown(10);
    }

    @Override public void setupAdventureSpawn(EntityPlayerMP player) { }
}
