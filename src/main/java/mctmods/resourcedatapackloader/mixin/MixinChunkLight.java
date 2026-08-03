package mctmods.resourcedatapackloader.mixin;

import mctmods.resourcedatapackloader.content.interfaces.LightAreaHolder;
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

@Mixin(Chunk.class)
public abstract class MixinChunkLight {
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

    @Redirect(method = "onTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/Chunk;checkLight()V"))
    private void rdpl$countRetry(Chunk chunk) {
        if (ContentChunkWatch.watching()) { ContentChunkWatch.retried(); }
        if (ContentPregen.busy() && ContentPregen.holds(chunk.x, chunk.z)) { return; }

        chunk.checkLight();
    }

    @Redirect(method = "enqueueRelightChecks", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/EnumFacing;values()[Lnet/minecraft/util/EnumFacing;"))
    private EnumFacing[] rdpl$sidesWithoutCopying() { return EnumFacing.VALUES; }

    @Redirect(method = {"generateSkylightMap", "relightBlock"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;notifyLightSet(Lnet/minecraft/util/math/BlockPos;)V"))
    private void rdpl$tellNobodyEither(World world, BlockPos pos) {
        if (((LightAreaHolder) world).rdpl$quietLight()) { return; }

        world.notifyLightSet(pos);
    }

    @Inject(method = "enqueueRelightChecks", at = @At("HEAD"))
    private void rdpl$startRelight(CallbackInfo ci) {
        if (ContentChunkWatch.watching()) { rdpl$relitStart.set(System.nanoTime()); }
    }

    @Inject(method = "enqueueRelightChecks", at = @At("RETURN"))
    private void rdpl$endRelight(CallbackInfo ci) {
        if (ContentChunkWatch.watching()) { ContentChunkWatch.relit(System.nanoTime() - rdpl$relitStart.get()); }
    }
}
