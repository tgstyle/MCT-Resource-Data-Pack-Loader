package mctmods.resourcedatapackloader.content.gate;

import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.EntityTravelToDimensionEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

public final class VanillaPortalLink {
    private static final String TAG = "rdplNetherPortal";
    private static final String X = "X";
    private static final String Y = "Y";
    private static final String Z = "Z";

    private VanillaPortalLink() {}

    public static void onTravel(EntityTravelToDimensionEvent event) {
        Entity entity = event.getEntity();
        if (entity.level().isClientSide() || !entity.level().dimension().equals(Level.OVERWORLD) || !event.getDimension().equals(Level.NETHER)) { return; }
        CompoundTag portal = new CompoundTag();
        portal.putDouble(X, entity.getX());
        portal.putDouble(Y, entity.getY());
        portal.putDouble(Z, entity.getZ());
        entity.getPersistentData().put(TAG, portal);
    }

    public static void onDimensionChange(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player) || !event.getFrom().equals(Level.NETHER) || !event.getTo().equals(Level.OVERWORLD)) { return; }
        double[] stored = take(player);
        if (stored == null) { return; }
        player.teleportTo(stored[0], stored[1], stored[2]);
        ContentLog.LOGGER.debug("Player {} came back out of the Nether and was set down where they left, {} {} {}", player.getName().getString(), stored[0], stored[1], stored[2]);
    }

    public static void onJoin(EntityJoinLevelEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof ServerPlayer || !(event.getLevel() instanceof ServerLevel level) || !level.dimension().equals(Level.OVERWORLD) || !entity.getPersistentData().contains(TAG)) { return; }
        double[] stored = take(entity);
        if (stored != null) { entity.moveTo(stored[0], stored[1], stored[2], entity.getYRot(), entity.getXRot()); }
    }

    private static double[] take(Entity entity) {
        CompoundTag data = entity.getPersistentData();
        if (!data.contains(TAG, CompoundTag.TAG_COMPOUND)) { return null; }
        CompoundTag portal = data.getCompound(TAG);
        data.remove(TAG);
        if (!portal.contains(X) || !portal.contains(Y) || !portal.contains(Z)) { return null; }
        return new double[] { portal.getDouble(X), portal.getDouble(Y), portal.getDouble(Z) };
    }
}
