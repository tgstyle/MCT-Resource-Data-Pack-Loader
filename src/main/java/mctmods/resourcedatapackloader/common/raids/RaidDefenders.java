package mctmods.resourcedatapackloader.common.raids;

import mctmods.resourcedatapackloader.common.ownership.claims.ClaimOwners;
import mctmods.resourcedatapackloader.common.util.messages.BossBars;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.phys.AABB;

public final class RaidDefenders {
    private RaidDefenders() {}

    public static boolean defends(ServerPlayer player, int owner, @Nullable UUID target) {
        if (target != null && target.equals(player.getUUID())) { return true; }
        return owner != 0 && ClaimOwners.isMember(player, owner);
    }

    public static List<ServerPlayer> inReach(ServerLevel level, BlockPos center, int reach, int owner, @Nullable UUID target) {
        List<ServerPlayer> defenders = new ArrayList<>();
        if (owner == 0 && target == null) { return defenders; }
        for (ServerPlayer player : BossBars.near(level, center, reach)) {
            if (defends(player, owner, target)) { defenders.add(player); }
        }
        return defenders;
    }

    public static boolean online(ServerLevel level, int owner, @Nullable UUID target) { return !inWorld(level, owner, target).isEmpty(); }

    public static List<ServerPlayer> inWorld(ServerLevel level, int owner, @Nullable UUID target) {
        List<ServerPlayer> defenders = new ArrayList<>();
        if (owner == 0 && target == null) { return defenders; }
        for (ServerPlayer player : level.getServer().getPlayerList().getPlayers()) {
            if (player.level().dimension().equals(level.dimension()) && defends(player, owner, target)) { defenders.add(player); }
        }
        return defenders;
    }

    public static int villagers(ServerLevel level, BlockPos center, int radius) {
        if (!level.isLoaded(center)) { return 0; }
        int height = Math.max(radius, 16);
        AABB box = new AABB(center.getX() - radius, center.getY() - height, center.getZ() - radius, center.getX() + radius + 1, center.getY() + height + 1, center.getZ() + radius + 1);
        return level.getEntitiesOfClass(Villager.class, box).size();
    }

    public static int count(ServerLevel level, BlockPos center, int reach, int radius, int owner, @Nullable UUID target) {
        return inReach(level, center, reach, owner, target).size() + villagers(level, center, radius);
    }
}
