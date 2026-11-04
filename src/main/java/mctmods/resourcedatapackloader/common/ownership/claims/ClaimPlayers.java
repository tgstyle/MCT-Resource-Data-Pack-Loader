package mctmods.resourcedatapackloader.common.ownership.claims;

import mctmods.resourcedatapackloader.common.dimension.ContentDimensions;
import mctmods.resourcedatapackloader.common.util.dimension.Travel;

import java.util.ArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;

public final class ClaimPlayers {
    private static final int RINGS = 4;

    private ClaimPlayers() {}

    public static boolean occupied(ServerLevel world, int chunkX, int chunkZ) {
        for (Player player : world.players()) {
            if (inside(player, chunkX, chunkZ)) { return true; }
        }
        return false;
    }

    public static void move(ServerLevel world, ClaimIndex index, int chunkX, int chunkZ) {
        for (ServerPlayer player : new ArrayList<>(world.players())) {
            if (!inside(player, chunkX, chunkZ)) { continue; }
            BlockPos target = outside(world, index, player, chunkX, chunkZ);
            Travel.to(player, world, target.getX() + 0.5D, target.getY(), target.getZ() + 0.5D, player.getYRot(), player.getXRot());
        }
    }

    public static void removeEntities(ServerLevel world, int chunkX, int chunkZ, int sectionY) {
        int minX = SectionPos.sectionToBlockCoord(chunkX);
        int minY = SectionPos.sectionToBlockCoord(sectionY);
        int minZ = SectionPos.sectionToBlockCoord(chunkZ);
        AABB box = new AABB(minX, minY, minZ, minX + 16, minY + 16, minZ + 16);
        for (Entity entity : world.getEntitiesOfClass(Entity.class, box, other -> !(other instanceof Player) && inside(other, chunkX, chunkZ))) { entity.discard(); }
    }

    private static boolean inside(Entity entity, int chunkX, int chunkZ) { return entity.chunkPosition().x() == chunkX && entity.chunkPosition().z() == chunkZ; }

    private static BlockPos outside(ServerLevel world, ClaimIndex index, Player player, int chunkX, int chunkZ) {
        for (int ring = 1; ring <= RINGS; ring++) {
            int bestX = 0;
            int bestZ = 0;
            double best = Double.MAX_VALUE;
            for (int dx = -ring; dx <= ring; dx++) {
                for (int dz = -ring; dz <= ring; dz++) {
                    if (Math.max(Math.abs(dx), Math.abs(dz)) != ring || index.pendingAt(chunkX + dx, chunkZ + dz) != null) { continue; }
                    double gap = player.distanceToSqr(SectionPos.sectionToBlockCoord(chunkX + dx, 8), player.getY(), SectionPos.sectionToBlockCoord(chunkZ + dz, 8));
                    if (gap < best) {
                        best = gap;
                        bestX = chunkX + dx;
                        bestZ = chunkZ + dz;
                    }
                }
            }
            if (best < Double.MAX_VALUE) { return ContentDimensions.landing(world, new BlockPos(SectionPos.sectionToBlockCoord(bestX, 8), Mth.floor(player.getY()), SectionPos.sectionToBlockCoord(bestZ, 8)), Heightmap.Types.MOTION_BLOCKING_NO_LEAVES); }
        }
        return ContentDimensions.landing(world, world.getRespawnData().pos(), Heightmap.Types.MOTION_BLOCKING_NO_LEAVES);
    }
}
