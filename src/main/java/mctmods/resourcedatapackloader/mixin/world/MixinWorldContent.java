package mctmods.resourcedatapackloader.mixin.world;

import mctmods.resourcedatapackloader.common.dimension.ContentDimensionRules;
import mctmods.resourcedatapackloader.common.entities.behavior.ContentEntityTicks;
import mctmods.resourcedatapackloader.common.environment.ContentWeather;
import mctmods.resourcedatapackloader.common.pack.loading.ContentServer;
import mctmods.resourcedatapackloader.common.pregen.ContentPregen;
import mctmods.resourcedatapackloader.common.rules.ContentGameRules;
import mctmods.resourcedatapackloader.common.rules.ContentRuleSwitch;
import mctmods.resourcedatapackloader.common.rules.ContentSpawnChunks;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.chunk.Chunk;
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
        EnumDifficulty asked = ContentRuleSwitch.difficulty(self);
        if (asked == null) { asked = ContentServer.difficultyFor(self.provider.getDimension()); }
        if (asked != null) { cir.setReturnValue(asked); }
    }

    @Inject(method = "getDifficultyForLocation", at = @At("RETURN"), cancellable = true) private void rdpl$localDifficultyAsAsked(BlockPos pos, CallbackInfoReturnable<DifficultyInstance> cir) {
        World self = (World) (Object) this;
        if (isRemote || self.provider == null) { return; }
        cir.setReturnValue(ContentRuleSwitch.local(self, pos, cir.getReturnValue()));
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

    @Redirect(method = "updateEntityWithOptionalForce", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;onUpdate()V")) private void rdpl$slowDistant(Entity entity) {
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
        if (isRemote || Chunk.populating == null) { return flags; }
        return (flags | rdpl$SUPPRESS_OBSERVERS) & ~rdpl$NOTIFY_NEIGHBORS;
    }

    @Inject(method = "updateEntityWithOptionalForce", at = @At("HEAD"), cancellable = true) private void rdpl$holdStillWhileLandIsMade(Entity entityIn, boolean forceUpdate, CallbackInfo ci) {
        if (entityIn instanceof EntityPlayer) { return; }
        if (ContentPregen.holdsStill((World) (Object) this)) { ci.cancel(); }
    }

    @WrapOperation(method = "getWorldTime", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/WorldProvider;getWorldTime()J", remap = false)) private long rdpl$existingWorldTime(WorldProvider provider, Operation<Long> original) { return ContentDimensionRules.existingWorldTime((World) (Object) this, original.call(provider)); }

    @WrapOperation(method = "isDaytime", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/WorldProvider;isDaytime()Z", remap = false)) private boolean rdpl$existingDaytime(WorldProvider provider, Operation<Boolean> original) { return ContentDimensionRules.existingDaytime((World) (Object) this, original.call(provider)); }

    @WrapOperation(method = "getCelestialAngle", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/WorldProvider;calculateCelestialAngle(JF)F")) private float rdpl$existingCelestialAngle(WorldProvider provider, long worldTime, float partialTicks, Operation<Float> original) { return ContentDimensionRules.existingCelestialAngle((World) (Object) this, worldTime, partialTicks, original.call(provider, worldTime, partialTicks)); }

    @WrapOperation(method = "updateWeather", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/WorldProvider;updateWeather()V", remap = false)) private void rdpl$existingWeather(WorldProvider provider, Operation<Void> original) { ContentDimensionRules.existingWeather((World) (Object) this, () -> original.call(provider)); }

    @WrapOperation(method = "canBlockFreeze", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/WorldProvider;canBlockFreeze(Lnet/minecraft/util/math/BlockPos;Z)Z", remap = false)) private boolean rdpl$existingFreezes(WorldProvider provider, BlockPos pos, boolean byWater, Operation<Boolean> original) { return ContentDimensionRules.existingFreezes((World) (Object) this, original.call(provider, pos, byWater)); }

    @WrapOperation(method = "canSnowAt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/WorldProvider;canSnowAt(Lnet/minecraft/util/math/BlockPos;Z)Z", remap = false)) private boolean rdpl$existingSnows(WorldProvider provider, BlockPos pos, boolean checkLight, Operation<Boolean> original) { return ContentDimensionRules.existingSnows((World) (Object) this, original.call(provider, pos, checkLight)); }

    @Inject(method = "isRainingAt", at = @At("HEAD"), cancellable = true) private void rdpl$rainBelowCeiling(BlockPos position, CallbackInfoReturnable<Boolean> cir) {
        if (ContentWeather.above((World) (Object) this, position.getY())) { cir.setReturnValue(false); }
    }

    @Inject(method = "canSnowAt", at = @At("HEAD"), cancellable = true) private void rdpl$snowBelowCeiling(BlockPos pos, boolean checkLight, CallbackInfoReturnable<Boolean> cir) {
        if (ContentWeather.above((World) (Object) this, pos.getY())) { cir.setReturnValue(false); }
    }
}
