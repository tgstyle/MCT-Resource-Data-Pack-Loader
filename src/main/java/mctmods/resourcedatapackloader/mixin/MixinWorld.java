package mctmods.resourcedatapackloader.mixin;

import mctmods.resourcedatapackloader.content.entity.ContentEntityTicks;
import mctmods.resourcedatapackloader.content.worldgen.ContentGameRules;
import mctmods.resourcedatapackloader.content.worldgen.ContentPregen;
import mctmods.resourcedatapackloader.content.worldgen.ContentSpawnChunks;
import mctmods.resourcedatapackloader.content.worldgen.beard.PredictedChunk;
import mctmods.resourcedatapackloader.util.ContentLog;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(World.class)
public abstract class MixinWorld {
    @Unique private static boolean rdpl$told;
    @Unique private static ChunkPos rdpl$during;
    @Unique private static int rdpl$lastX = Integer.MIN_VALUE;
    @Unique private static int rdpl$lastZ = Integer.MIN_VALUE;

    @Unique private static final int rdpl$NOTIFY_NEIGHBORS = 1;
    @Unique private static final int rdpl$SUPPRESS_OBSERVERS = 16;

    @Inject(method = "updateEntities", at = @At("HEAD"), cancellable = true)
    private void rdpl$standStillWhileLandIsMade(CallbackInfo ci) {
        if (((World) (Object) this).isRemote || !ContentPregen.busy()) { return; }

        ci.cancel();
    }

    @Redirect(method = "updateEntityWithOptionalForce", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;onUpdate()V"))
    private void rdpl$slowDistant(Entity entity) {
        if (ContentEntityTicks.slowedNow(entity)) { ContentEntityTicks.age(entity); }
        else { entity.onUpdate(); }
    }

    @WrapOperation(method = "getBlockState", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getChunk(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/world/chunk/Chunk;"))
    private Chunk rdpl$leaveLandAlone(World self, BlockPos pos, Operation<Chunk> original) {
        ChunkPos populating = AccessorChunk.rdpl$getPopulating();
        if (populating == null) { return original.call(self, pos); }
        if (pos.getX() >> 4 == populating.x && pos.getZ() >> 4 == populating.z) { return original.call(self, pos); }

        return rdpl$predictInstead(self, pos, populating, original);
    }

    @Unique private Chunk rdpl$predictInstead(World self, BlockPos pos, ChunkPos populating, Operation<Chunk> original) {
        if (populating != rdpl$during) {
            rdpl$during = populating;
            rdpl$lastX = Integer.MIN_VALUE;
            rdpl$lastZ = Integer.MIN_VALUE;
        }

        int chunkX = pos.getX() >> 4;
        int chunkZ = pos.getZ() >> 4;
        if (chunkX == populating.x && chunkZ == populating.z) { return original.call(self, pos); }
        if (chunkX == rdpl$lastX && chunkZ == rdpl$lastZ) { return original.call(self, pos); }

        Chunk loaded = self.getChunkProvider().getLoadedChunk(chunkX, chunkZ);
        if (loaded != null) {
            rdpl$lastX = chunkX;
            rdpl$lastZ = chunkZ;
            return loaded;
        }

        if (!rdpl$told) {
            rdpl$told = true;
            ContentLog.LOGGER.info("Something read land that has not been made yet while another chunk was being filled in. Making it there and then would have made everything around it in turn, so it is answered from the shape the land will take instead");
        }
        return PredictedChunk.of(self);
    }

    @Inject(method = "isSpawnChunk", at = @At("HEAD"), cancellable = true)
    private void rdpl$spawnChunkRadius(int x, int z, CallbackInfoReturnable<Boolean> cir) {
        World world = (World) (Object) this;
        int radius = ContentSpawnChunks.radius(world.provider.getDimension());
        if (radius == ContentSpawnChunks.VANILLA) { return; }
        if (radius <= 0) {
            cir.setReturnValue(false);
            return;
        }

        BlockPos spawn = world.getSpawnPoint();
        int offsetX = x * 16 + 8 - spawn.getX();
        int offsetZ = z * 16 + 8 - spawn.getZ();
        cir.setReturnValue(offsetX >= -radius && offsetX <= radius && offsetZ >= -radius && offsetZ <= radius);
    }

    @Inject(method = "getGameRules", at = @At("HEAD"), cancellable = true)
    private void rdpl$dimensionRules(CallbackInfoReturnable<GameRules> cir) {
        GameRules rules = ContentGameRules.forWorld((World) (Object) this);
        if (rules != null) { cir.setReturnValue(rules); }
    }

    @ModifyVariable(method = "markAndNotifyBlock", at = @At("HEAD"), argsOnly = true, index = 5, remap = false)
    private int rdpl$suppressObserverScan(int flags) {
        if (AccessorChunk.rdpl$getPopulating() == null) { return flags; }
        return (flags | rdpl$SUPPRESS_OBSERVERS) & ~rdpl$NOTIFY_NEIGHBORS;
    }
}
