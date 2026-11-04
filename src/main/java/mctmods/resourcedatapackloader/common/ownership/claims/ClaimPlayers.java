package mctmods.resourcedatapackloader.common.ownership.claims;

import mctmods.resourcedatapackloader.common.util.dimension.Travel;
import mctmods.resourcedatapackloader.common.util.world.Coords;
import mctmods.resourcedatapackloader.common.util.worldgen.GroundLevel;

import java.util.ArrayList;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.WorldServer;

public final class ClaimPlayers {
    private static final int RINGS = 4;

    private ClaimPlayers() {}

    public static boolean occupied(WorldServer world, int chunkX, int chunkZ) {
        for (EntityPlayer player : world.playerEntities) {
            if (inside(player, chunkX, chunkZ)) { return true; }
        }
        return false;
    }

    public static void move(WorldServer world, ClaimIndex index, int chunkX, int chunkZ) {
        for (EntityPlayer player : new ArrayList<>(world.playerEntities)) {
            if (!(player instanceof EntityPlayerMP) || !inside(player, chunkX, chunkZ)) { continue; }
            BlockPos target = outside(world, index, player, chunkX, chunkZ);
            Travel.to(player, world.provider.getDimension(), target.getX() + 0.5D, target.getY(), target.getZ() + 0.5D, player.rotationYaw, player.rotationPitch);
        }
    }

    public static void removeEntities(WorldServer world, int chunkX, int chunkZ, int sectionY) {
        int minX = Coords.cubeToMinBlock(chunkX);
        int minY = Coords.cubeToMinBlock(sectionY);
        int minZ = Coords.cubeToMinBlock(chunkZ);
        AxisAlignedBB box = new AxisAlignedBB(minX, minY, minZ, minX + 16, minY + 16, minZ + 16);
        for (Entity entity : world.getEntitiesWithinAABB(Entity.class, box, other -> other != null && !(other instanceof EntityPlayer) && inside(other, chunkX, chunkZ))) { entity.setDead(); }
    }

    private static boolean inside(Entity entity, int chunkX, int chunkZ) { return Coords.getCubeXForEntity(entity) == chunkX && Coords.getCubeZForEntity(entity) == chunkZ; }

    private static BlockPos outside(WorldServer world, ClaimIndex index, EntityPlayer player, int chunkX, int chunkZ) {
        for (int ring = 1; ring <= RINGS; ring++) {
            int bestX = 0;
            int bestZ = 0;
            double best = Double.MAX_VALUE;
            for (int dx = -ring; dx <= ring; dx++) {
                for (int dz = -ring; dz <= ring; dz++) {
                    if (Math.max(Math.abs(dx), Math.abs(dz)) != ring || index.pendingAt(chunkX + dx, chunkZ + dz) != null) { continue; }
                    double gap = player.getDistanceSq(Coords.localToBlock(chunkX + dx, 8), player.posY, Coords.localToBlock(chunkZ + dz, 8));
                    if (gap < best) {
                        best = gap;
                        bestX = chunkX + dx;
                        bestZ = chunkZ + dz;
                    }
                }
            }
            if (best < Double.MAX_VALUE) { return GroundLevel.inWindow(world, new BlockPos(Coords.localToBlock(bestX, 8), MathHelper.floor(player.posY), Coords.localToBlock(bestZ, 8))); }
        }
        return GroundLevel.inWindow(world, world.getSpawnPoint());
    }
}
