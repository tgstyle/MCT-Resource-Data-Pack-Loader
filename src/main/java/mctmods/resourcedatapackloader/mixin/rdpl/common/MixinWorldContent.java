package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.ContentServer;
import mctmods.resourcedatapackloader.content.entity.ContentEntityTicks;
import mctmods.resourcedatapackloader.content.worldgen.ContentGameRules;
import mctmods.resourcedatapackloader.content.worldgen.ContentPregen;
import mctmods.resourcedatapackloader.content.worldgen.ContentSpawnChunks;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.List;

@SuppressWarnings("ConstantConditions") @Mixin(World.class) public abstract class MixinWorldContent {
    @Unique private static final int rdpl$NOTIFY_NEIGHBORS = 1;
    @Unique private static final int rdpl$SUPPRESS_OBSERVERS = 16;
    @Shadow @Final public boolean isRemote;
    @Shadow @Final public List<Entity> loadedEntityList;
    @Shadow @Final protected List<Entity> unloadedEntityList;

    @Shadow protected abstract boolean isChunkLoaded(int x, int z, boolean allowEmpty);

    @Shadow public abstract void onEntityRemoved(Entity entityIn);

    @Inject(method = "getDifficulty", at = @At("HEAD"), cancellable = true) private void rdpl$difficultyAsAsked(CallbackInfoReturnable<EnumDifficulty> cir) {
        World self = (World) (Object) this;
        if (self.provider == null) { return; }
        EnumDifficulty asked = ContentServer.difficultyFor(self.provider.getDimension());
        if (asked != null) { cir.setReturnValue(asked); }
    }

    @Inject(method = "updateEntities", at = @At("HEAD"), cancellable = true) private void rdpl$standStillWhileLandIsMade(CallbackInfo ci) {
        if (isRemote || !ContentPregen.busy()) { return; }
        rdpl$letGoOfUnloadedEntities();
        ci.cancel();
    }

    @Unique private void rdpl$letGoOfUnloadedEntities() {
        if (unloadedEntityList.isEmpty()) { return; }
        loadedEntityList.removeAll(unloadedEntityList);
        for (Entity leaving : unloadedEntityList) {
            if (leaving.addedToChunk && isChunkLoaded(leaving.chunkCoordX, leaving.chunkCoordZ, true)) { ((World) (Object) this).getChunk(leaving.chunkCoordX, leaving.chunkCoordZ).removeEntity(leaving); }
        }
        for (Entity leaving : unloadedEntityList) { onEntityRemoved(leaving); }
        unloadedEntityList.clear();
    }

    @Redirect(method = "updateEntityWithOptionalForce", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;onUpdate()V"))
    private void rdpl$slowDistant(Entity entity) {
        if (ContentEntityTicks.slowedNow(entity)) { ContentEntityTicks.age(entity); }
        else { entity.onUpdate(); }
    }

    @Inject(method = "isSpawnChunk", at = @At("HEAD"), cancellable = true) private void rdpl$spawnChunkRadius(int x, int z, CallbackInfoReturnable<Boolean> cir) {
        World world = (World) (Object) this;
        int dimension = world.provider.getDimension();
        if (ContentSpawnChunks.radius(dimension) <= 0) {
            cir.setReturnValue(false);
            return;
        }
        int reach = ContentSpawnChunks.chunks(dimension);
        BlockPos spawn = world.getSpawnPoint();
        cir.setReturnValue(Math.abs(x - (spawn.getX() >> 4)) <= reach && Math.abs(z - (spawn.getZ() >> 4)) <= reach);
    }

    @Inject(method = "getGameRules", at = @At("HEAD"), cancellable = true) private void rdpl$dimensionRules(CallbackInfoReturnable<GameRules> cir) {
        GameRules rules = ContentGameRules.forWorld((World) (Object) this);
        if (rules != null) { cir.setReturnValue(rules); }
    }

    @ModifyVariable(method = "markAndNotifyBlock", at = @At("HEAD"), argsOnly = true, index = 5, remap = false) private int rdpl$suppressObserverScan(int flags) {
        if (isRemote || IChunk.rdpl$getPopulating() == null) { return flags; }
        return (flags | rdpl$SUPPRESS_OBSERVERS) & ~rdpl$NOTIFY_NEIGHBORS;
    }
}
