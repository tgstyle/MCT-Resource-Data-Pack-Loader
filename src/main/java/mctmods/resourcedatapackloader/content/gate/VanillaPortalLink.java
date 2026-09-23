package mctmods.resourcedatapackloader.content.gate;

import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.EntityTravelToDimensionEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;

public final class VanillaPortalLink {
    private static final String TAG = "NetherPortal";
    private static final String X = "LastPortalX";
    private static final String Y = "LastPortalY";
    private static final String Z = "LastPortalZ";
    private static final String EARLIER_TAG = "rdplNetherPortal";
    private static final String EARLIER_X = "X";
    private static final String EARLIER_Y = "Y";
    private static final String EARLIER_Z = "Z";
    private static final Set<UUID> ARRIVING = new HashSet<>();

    private VanillaPortalLink() {}

    public static void onTravel(EntityTravelToDimensionEvent event) {
        Entity entity = event.getEntity();
        if (entity.level().isClientSide()) { return; }
        ARRIVING.remove(entity.getUUID());
        if (entity.level().dimension().equals(Level.OVERWORLD) && event.getDimension().equals(Level.NETHER)) {
            CompoundTag data = entity.getPersistentData();
            CompoundTag portal = data.getCompound(TAG);
            portal.putDouble(X, entity.getX());
            portal.putDouble(Y, entity.getY());
            portal.putDouble(Z, entity.getZ());
            data.put(TAG, portal);
            data.remove(EARLIER_TAG);
            return;
        }
        if (!entity.level().dimension().equals(Level.NETHER) || !event.getDimension().equals(Level.OVERWORLD) || stored(entity) == null) { return; }
        if (entity.level().getBlockStates(entity.getBoundingBox()).anyMatch(state -> state.is(Blocks.NETHER_PORTAL))) { ARRIVING.add(entity.getUUID()); }
    }

    public static void onDimensionChange(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player) || !event.getFrom().equals(Level.NETHER) || !event.getTo().equals(Level.OVERWORLD) || !ARRIVING.remove(player.getUUID())) { return; }
        double[] stored = stored(player);
        if (stored == null) { return; }
        player.teleportTo(stored[0], stored[1], stored[2]);
        ContentLog.LOGGER.debug("Player {} came back out of the Nether and was set down where they left, {} {} {}", player.getName().getString(), stored[0], stored[1], stored[2]);
    }

    public static void onJoin(EntityJoinLevelEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof ServerPlayer || !(event.getLevel() instanceof ServerLevel level) || !level.dimension().equals(Level.OVERWORLD) || !ARRIVING.remove(entity.getUUID())) { return; }
        double[] stored = stored(entity);
        if (stored != null) { entity.moveTo(stored[0], stored[1], stored[2], entity.getYRot(), entity.getXRot()); }
    }

    @Nullable private static double[] stored(Entity entity) {
        CompoundTag data = entity.getPersistentData();
        double[] earlier = point(data, EARLIER_TAG, EARLIER_X, EARLIER_Y, EARLIER_Z);
        return earlier != null ? earlier : point(data, TAG, X, Y, Z);
    }

    @Nullable private static double[] point(CompoundTag data, String tag, String x, String y, String z) {
        if (!data.contains(tag, CompoundTag.TAG_COMPOUND)) { return null; }
        CompoundTag portal = data.getCompound(tag);
        if (!portal.contains(x) || !portal.contains(y) || !portal.contains(z)) { return null; }
        return new double[] { portal.getDouble(x), portal.getDouble(y), portal.getDouble(z) };
    }
}
