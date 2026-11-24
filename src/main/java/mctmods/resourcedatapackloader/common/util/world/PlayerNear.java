package mctmods.resourcedatapackloader.common.util.world;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public final class PlayerNear {
    private PlayerNear() {}

    public static List<EntityPlayerMP> playersIn(MinecraftServer server, int dimension, Predicate<EntityPlayerMP> keep) {
        List<EntityPlayerMP> found = new ArrayList<>();
        for (EntityPlayerMP player : server.getPlayerList().getPlayers()) {
            if (player.dimension == dimension && keep.test(player)) { found.add(player); }
        }
        return found;
    }

    public static boolean within(World world, AxisAlignedBB box, int distance) {
        double limit = (double) distance * distance;
        for (EntityPlayer player : world.playerEntities) {
            if (player.isSpectator()) { continue; }
            double dx = player.posX - MathHelper.clamp(player.posX, box.minX, box.maxX);
            double dy = player.posY - MathHelper.clamp(player.posY, box.minY, box.maxY);
            double dz = player.posZ - MathHelper.clamp(player.posZ, box.minZ, box.maxZ);
            if (dx * dx + dy * dy + dz * dz <= limit) { return true; }
        }
        return false;
    }
}
