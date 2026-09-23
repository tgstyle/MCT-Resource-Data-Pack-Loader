package mctmods.resourcedatapackloader.content.raid;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.PoiTypeTags;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.phys.AABB;
import java.util.List;
import javax.annotation.Nullable;

public record RaidVillage(BlockPos center, int radius) {
    private static final int GATHER = 64;
    private static final int LEAST_RADIUS = 32;
    private static final int VILLAGER_RISE = 4;

    @Nullable public static RaidVillage nearest(ServerLevel level, BlockPos at, int reach) {
        List<BlockPos> points = level.getPoiManager().findAll(type -> type.is(PoiTypeTags.VILLAGE), pos -> true, at, GATHER + reach, PoiManager.Occupancy.ANY).toList();
        if (points.isEmpty()) { return null; }
        long x = 0;
        long y = 0;
        long z = 0;
        for (BlockPos point : points) {
            x += point.getX();
            y += point.getY();
            z += point.getZ();
        }
        BlockPos center = new BlockPos((int) (x / points.size()), (int) (y / points.size()), (int) (z / points.size()));
        double farthest = 0.0D;
        for (BlockPos point : points) { farthest = Math.max(farthest, point.distSqr(center)); }
        int radius = Math.max(LEAST_RADIUS, (int) Math.sqrt(farthest) + 1);
        double span = reach + radius;
        return center.distSqr(at) <= span * span ? new RaidVillage(center, radius) : null;
    }

    public int villagers(ServerLevel level) {
        AABB around = new AABB(center.getX() - radius, center.getY() - VILLAGER_RISE, center.getZ() - radius, center.getX() + radius, center.getY() + VILLAGER_RISE, center.getZ() + radius);
        return level.getEntitiesOfClass(Villager.class, around).size();
    }
}
