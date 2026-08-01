package mctmods.resourcedatapackloader.content.gate;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.EntityTravelToDimensionEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public final class VanillaPortalLink {
    private static final String TAG = "NetherPortal";
    private static final String X = "LastPortalX";
    private static final String Y = "LastPortalY";
    private static final String Z = "LastPortalZ";
    private static final int OVERWORLD = 0;
    private static final int NETHER = -1;

    private VanillaPortalLink() {}

    @SubscribeEvent public static void onTravel(EntityTravelToDimensionEvent event) {
        Entity entity = event.getEntity();
        if (entity == null || entity.getEntityWorld().provider == null) { return; }
        if (entity.getEntityWorld().provider.getDimension() != OVERWORLD || event.getDimension() != NETHER) { return; }

        write(entity);
    }

    public static double[] stored(Entity entity) {
        if (entity == null || entity.getEntityWorld().provider == null) { return new double[0]; }
        if (entity.getEntityWorld().provider.getDimension() != NETHER) { return new double[0]; }

        return read(entity);
    }

    private static void write(Entity entity) {
        NBTTagCompound data = entity.getEntityData();
        NBTTagCompound portal = data.getCompoundTag(TAG);
        portal.setDouble(X, entity.posX);
        portal.setDouble(Y, entity.posY);
        portal.setDouble(Z, entity.posZ);
        data.setTag(TAG, portal);
    }

    private static double[] read(Entity entity) {
        NBTTagCompound data = entity.getEntityData();
        if (!data.hasKey(TAG, 10)) { return new double[0]; }

        NBTTagCompound portal = data.getCompoundTag(TAG);
        if (!portal.hasKey(X) || !portal.hasKey(Y) || !portal.hasKey(Z)) { return new double[0]; }

        return new double[] { portal.getDouble(X), portal.getDouble(Y), portal.getDouble(Z) };
    }
}
