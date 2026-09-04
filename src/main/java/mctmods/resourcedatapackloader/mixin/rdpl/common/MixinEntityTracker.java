package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.rubic.entity.IRubicEntityTracker;
import mctmods.resourcedatapackloader.content.rubic.server.interfaces.IRubicPlayerList;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.ICube;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityTracker;
import net.minecraft.entity.EntityTrackerEntry;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketEntityAttach;
import net.minecraft.network.play.server.SPacketSetPassengers;
import net.minecraft.world.WorldServer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.Objects;
import java.util.Set;

@Mixin(EntityTracker.class) @Implements(@Interface(iface = IRubicEntityTracker.class, prefix = "tracker$")) public class MixinEntityTracker {
    @Shadow @Final private Set<EntityTrackerEntry> entries;
    @Unique private int rdpl$maxVertTrackingDistanceThreshold;

    @Inject(method = "<init>", at = @At("RETURN")) private void onConstruct(WorldServer theWorldIn, CallbackInfo ci) {
        tracker$setVertViewDistance(((IRubicPlayerList) Objects.requireNonNull(theWorldIn.getMinecraftServer()).getPlayerList()).getVerticalViewDistance());
    }

    @Redirect(method = "track(Lnet/minecraft/entity/Entity;IIZ)V", at = @At(value = "NEW",
            target = "net/minecraft/entity/EntityTrackerEntry"))
    @SuppressWarnings({"ConstantConditions", "DataFlowIssue"}) private EntityTrackerEntry onCreateEntry(Entity entityIn, int rangeIn, int maxRangeIn, int updateFrequencyIn, boolean sendVelocityUpdatesIn) {
        EntityTrackerEntry e = new EntityTrackerEntry(entityIn, rangeIn, maxRangeIn, updateFrequencyIn, sendVelocityUpdatesIn);
        ((IRubicEntityTracker.IEntry) e).setMaxVertRange(rdpl$maxVertTrackingDistanceThreshold);
        return e;
    }

    @SuppressWarnings({"ConstantValue", "ConstantConditions"}) public void tracker$sendLeashedEntitiesInCube(EntityPlayerMP player, ICube cubeIn) {
        for (EntityTrackerEntry entitytrackerentry : this.entries) {
            Entity entity = entitytrackerentry.getTrackedEntity();
            if (entity != player &&
                    entity.chunkCoordX == cubeIn.getX() &&
                    entity.chunkCoordZ == cubeIn.getZ() &&
                    entity.chunkCoordY == cubeIn.getY()) {
                entitytrackerentry.updatePlayerEntity(player);
                if (entity instanceof EntityLiving) {
                    Entity leashHolder = ((EntityLiving) entity).getLeashHolder();
                    if (leashHolder != null) { player.connection.sendPacket(new SPacketEntityAttach(entity, leashHolder)); }
                }
                if (!entity.getPassengers().isEmpty()) { player.connection.sendPacket(new SPacketSetPassengers(entity)); }
            }
        }
    }

    @SuppressWarnings("ConstantConditions") public void tracker$setVertViewDistance(int viewDistance) {
        this.rdpl$maxVertTrackingDistanceThreshold = (viewDistance - 1) * 16;
        for (EntityTrackerEntry e : this.entries) { ((IRubicEntityTracker.IEntry) e).setMaxVertRange(this.rdpl$maxVertTrackingDistanceThreshold); }
    }
}
