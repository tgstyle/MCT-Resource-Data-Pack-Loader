package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.interfaces.ILightAreaHolder;
import mctmods.resourcedatapackloader.content.worldgen.ContentChunkWatch;
import mctmods.resourcedatapackloader.content.worldgen.ContentLightArea;
import mctmods.resourcedatapackloader.content.worldgen.ContentPregen;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.util.math.ChunkPos;

@Mixin(Chunk.class) public abstract class MixinChunkLight {
    @Shadow private boolean isLightPopulated;
    @Unique private static final ThreadLocal<Long> rdpl$litStart = ThreadLocal.withInitial(() -> 0L);
    @Unique private static final ThreadLocal<Long> rdpl$relitStart = ThreadLocal.withInitial(() -> 0L);

    @Inject(method = "checkLight()V", at = @At("HEAD"))
    private void rdpl$startLight(CallbackInfo ci) {
        Chunk chunk = (Chunk) (Object) this;
        ContentLightArea.enter(chunk.getWorld(), chunk.x, chunk.z);
        if (ContentChunkWatch.watching()) { rdpl$litStart.set(System.nanoTime()); }
    }

    @Inject(method = "checkLight()V", at = @At("RETURN"))
    private void rdpl$endLight(CallbackInfo ci) {
        ContentLightArea.leave(((Chunk) (Object) this).getWorld());
        if (ContentChunkWatch.watching()) { ContentChunkWatch.lit(System.nanoTime() - rdpl$litStart.get(), isLightPopulated); }
    }

    @Redirect(method = "onTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/Chunk;recheckGaps(Z)V"))
    private void rdpl$gapsAfterwards(Chunk chunk, boolean onlyOne) {
        if (ContentPregen.holds(chunk.x, chunk.z) || ContentPregen.covers(chunk.getWorld(), chunk.x, chunk.z)) { return; }
        ((IChunkGaps) chunk).rdpl$recheckGaps(onlyOne);
    }

    @Redirect(method = "onTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/Chunk;checkLight()V"))
    private void rdpl$countRetry(Chunk chunk) {
        if (ContentPregen.holds(chunk.x, chunk.z) || ContentPregen.covers(chunk.getWorld(), chunk.x, chunk.z)) { return; }
        if (ContentChunkWatch.watching()) { ContentChunkWatch.retried(); }
        chunk.checkLight();
    }

    @Redirect(method = "enqueueRelightChecks", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/EnumFacing;values()[Lnet/minecraft/util/EnumFacing;"))
    private EnumFacing[] rdpl$sidesWithoutCopying() { return EnumFacing.VALUES; }

    @Redirect(method = {"generateSkylightMap", "relightBlock"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;notifyLightSet(Lnet/minecraft/util/math/BlockPos;)V"))
    private void rdpl$tellNobodyEither(World world, BlockPos pos) {
        if (((ILightAreaHolder) world).rdpl$quietLight()) { return; }
        world.notifyLightSet(pos);
    }

    @Inject(method = "enqueueRelightChecks", at = @At("HEAD")) private void rdpl$startRelight(CallbackInfo ci) {
        if (ContentChunkWatch.watching()) { rdpl$relitStart.set(System.nanoTime()); }
    }

    @Inject(method = "enqueueRelightChecks", at = @At("RETURN")) private void rdpl$endRelight(CallbackInfo ci) {
        if (ContentChunkWatch.watching()) { ContentChunkWatch.relit(System.nanoTime() - rdpl$relitStart.get()); }
    }

    @Redirect(method = "checkLight(II)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;checkLight(Lnet/minecraft/util/math/BlockPos;)Z"))
    private boolean rdpl$skipSettledSky(World world, BlockPos pos) {
        if (!world.provider.hasSkyLight() || !ContentLightArea.skySettled(world, pos)) { return world.checkLight(pos); }
        ContentChunkWatch.skySettled();
        return world.checkLightFor(EnumSkyBlock.BLOCK, pos);
    }

    @Shadow protected abstract void updateSkylightNeighborHeight(int x, int z, int startY, int endY);

    @Redirect(method = "relightBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/Chunk;updateSkylightNeighborHeight(IIII)V"))
    private void rdpl$skipSkyWhileDressing(Chunk chunk, int x, int z, int startY, int endY) {
        if (rdpl$dressingThis(x, z)) { return; }
        updateSkylightNeighborHeight(x, z, startY, endY);
    }

    @Unique private boolean rdpl$dressingThis(int x, int z) {
        if (isLightPopulated) { return false; }
        ChunkPos dressing = IChunk.rdpl$getPopulating();
        return dressing != null && (x >> 4) == dressing.x && (z >> 4) == dressing.z;
    }
}
