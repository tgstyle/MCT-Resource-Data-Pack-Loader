package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.rubic.entity.IRubicEntityTracker;
import mctmods.resourcedatapackloader.content.rubic.server.PlayerCubeMap;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IRubicWorld;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityTrackerEntry;
import net.minecraft.entity.player.EntityPlayerMP;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityTrackerEntry.class) @Implements(@Interface(iface = IRubicEntityTracker.IEntry.class, prefix = "entry$")) public class MixinEntityTrackerEntry {
    @Shadow @Final private int range;
    @Shadow private long encodedPosY;
    @Shadow @Final private Entity trackedEntity;
    @Unique private int rdpl$maxVertRange;

    @Inject(method = "isVisibleTo", cancellable = true, at = @At("RETURN")) private void isVisibleToRubic(EntityPlayerMP playerMP, CallbackInfoReturnable<Boolean> cir) {
        boolean ret = cir.getReturnValue();
        if (ret && ((IRubicWorld) playerMP.world).rdpl$isRubicWorld()) {
            int rangeY = Math.min(this.range, this.rdpl$maxVertRange);
            double dy = playerMP.posY - this.encodedPosY / 4096.0D;
            cir.setReturnValue(dy >= -rangeY && dy <= rangeY);
        }
    }

    @Inject(method = "isPlayerWatchingThisChunk", cancellable = true, at = @At("HEAD")) private void isPlayerWatchingThisChunkRubic(EntityPlayerMP playerMP, CallbackInfoReturnable<Boolean> cir) {
        if (((IRubicWorld) playerMP.world).rdpl$isRubicWorld()) {
            boolean ret = ((PlayerCubeMap) playerMP.getServerWorld().getPlayerChunkMap())
                    .isPlayerWatchingCube(playerMP, this.trackedEntity.chunkCoordX, this.trackedEntity.chunkCoordY, this.trackedEntity.chunkCoordZ);
            cir.setReturnValue(ret);
        }
    }

    public void entry$setMaxVertRange(int maxVertTrackingDistanceThreshold) { this.rdpl$maxVertRange = maxVertTrackingDistanceThreshold; }
}
