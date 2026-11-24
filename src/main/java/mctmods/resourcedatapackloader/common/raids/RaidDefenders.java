package mctmods.resourcedatapackloader.common.raids;

import mctmods.resourcedatapackloader.common.ownership.claims.ClaimOwners;
import mctmods.resourcedatapackloader.common.util.messages.BossBars;
import mctmods.resourcedatapackloader.common.util.world.PlayerNear;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;

public final class RaidDefenders {
    private RaidDefenders() {}

    public static boolean defends(EntityPlayerMP player, int owner, @Nullable UUID target) {
        if (target != null && target.equals(player.getUniqueID())) { return true; }
        return owner != 0 && ClaimOwners.isMember(player, owner);
    }

    public static List<EntityPlayerMP> inReach(WorldServer world, BlockPos center, int reach, int owner, @Nullable UUID target) {
        List<EntityPlayerMP> defenders = new ArrayList<>();
        if (owner == 0 && target == null) { return defenders; }
        for (EntityPlayerMP player : BossBars.near(world, center, reach)) {
            if (defends(player, owner, target)) { defenders.add(player); }
        }
        return defenders;
    }

    public static boolean online(WorldServer world, int owner, @Nullable UUID target) { return !inWorld(world, owner, target).isEmpty(); }

    public static List<EntityPlayerMP> inWorld(WorldServer world, int owner, @Nullable UUID target) {
        MinecraftServer server = world.getMinecraftServer();
        if (server == null || owner == 0 && target == null) { return new ArrayList<>(); }
        return PlayerNear.playersIn(server, world.provider.getDimension(), player -> defends(player, owner, target));
    }

    public static int villagers(WorldServer world, BlockPos center, int radius) {
        if (!world.isBlockLoaded(center)) { return 0; }
        int height = Math.max(radius, 16);
        AxisAlignedBB box = new AxisAlignedBB(center.add(-radius, -height, -radius), center.add(radius + 1, height + 1, radius + 1));
        return world.getEntitiesWithinAABB(EntityVillager.class, box).size();
    }

    public static int count(WorldServer world, BlockPos center, int reach, int radius, int owner, @Nullable UUID target) {
        return inReach(world, center, reach, owner, target).size() + villagers(world, center, radius);
    }
}
