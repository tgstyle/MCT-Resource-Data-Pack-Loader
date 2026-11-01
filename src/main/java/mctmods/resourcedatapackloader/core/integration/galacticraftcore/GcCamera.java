package mctmods.resourcedatapackloader.core.integration.galacticraftcore;

import micdoodle8.mods.galacticraft.api.entity.ICameraZoomEntity;
import micdoodle8.mods.galacticraft.api.vector.Vector3;
import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import micdoodle8.mods.galacticraft.core.entities.EntityParachest;
import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerStats;
import micdoodle8.mods.galacticraft.core.network.PacketSimple;
import micdoodle8.mods.galacticraft.core.network.PacketSimple.EnumSimplePacket;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import java.util.ArrayList;
import java.util.List;

public final class GcCamera {
    private static final double CHEST_REACH = 8.0D;
    private static final double CHEST_HEIGHT = 256.0D;

    private GcCamera() {}

    public static List<ItemStack> chestCargo(EntityPlayerMP player) {
        List<ItemStack> cargo = new ArrayList<>();
        Vector3 spot = GCPlayerStats.get(player).getChestSpawnVector();
        if (spot == null) { return cargo; }
        AxisAlignedBB box = new AxisAlignedBB(spot.x - CHEST_REACH, 0.0D, spot.z - CHEST_REACH, spot.x + CHEST_REACH, CHEST_HEIGHT, spot.z + CHEST_REACH);
        for (EntityParachest chest : player.world.getEntitiesWithinAABB(EntityParachest.class, box)) {
            for (ItemStack stack : chest.cargo) {
                if (!stack.isEmpty()) { cargo.add(stack); }
            }
            chest.setDead();
        }
        return cargo;
    }

    public static void restore(EntityPlayerMP player, Entity vehicle) {
        if (!(vehicle instanceof ICameraZoomEntity) || !((ICameraZoomEntity) vehicle).defaultThirdPerson()) { return; }
        GalacticraftCore.packetPipeline.sendTo(new PacketSimple(EnumSimplePacket.C_RESET_THIRD_PERSON, player.dimension, new Object[] {}), player);
    }
}
