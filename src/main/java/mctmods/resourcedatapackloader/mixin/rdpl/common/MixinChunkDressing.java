package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.rubic.RubicWorldControl;
import mctmods.resourcedatapackloader.content.worldgen.ContentCascade;
import mctmods.resourcedatapackloader.content.worldgen.ContentChunkWatch;
import mctmods.resourcedatapackloader.content.worldgen.ContentPregen;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraft.world.gen.IChunkGenerator;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.HashSet;
import java.util.Set;

@Mixin(value = Chunk.class, priority = 999)
public abstract class MixinChunkDressing {
    @Shadow @Final private World world;
    @Shadow @Final public int x;
    @Shadow @Final public int z;
    @Shadow private boolean isTerrainPopulated;
    @Unique private static final Set<String> rdpl$told = new HashSet<>();
    @Shadow public abstract ChunkPos getPos();

    @Inject(method = "populate(Lnet/minecraft/world/chunk/IChunkProvider;Lnet/minecraft/world/gen/IChunkGenerator;)V", at = @At("HEAD"))
    private void rdpl$traceCascade(IChunkProvider chunkProvider, IChunkGenerator chunkGenrator, CallbackInfo ci) {
        ChunkPos parent = IChunk.rdpl$getPopulating();
        if (parent != null) { ContentCascade.report(parent, getPos()); }
    }

    @Inject(method = "logCascadingWorldGeneration", at = @At("HEAD"), remap = false) private void rdpl$whoAsked(CallbackInfo ci) {
        if (!ContentLog.LOGGER.debugEnabled() || rdpl$told.size() >= 12) { return; }
        Throwable trace = new Throwable("who reached for land that was not there");
        StringBuilder key = new StringBuilder();
        int named = 0;
        for (StackTraceElement frame : trace.getStackTrace()) {
            String owner = frame.getClassName();
            if (owner.startsWith("net.minecraft.") || owner.startsWith("java.") || owner.startsWith("mctmods.")) { continue; }
            key.append(owner).append('.').append(frame.getMethodName()).append(' ');
            if (++named >= 4) { break; }
        }
        if (named == 0) { key.append("nothing outside the game itself"); }
        if (!rdpl$told.add(key.toString())) { return; }
        ContentLog.LOGGER.debug("Land was made in the middle of making other land, by a caller not seen before. This is number {} of the different ones", rdpl$told.size(), trace);
    }

    @SuppressWarnings("ConstantValue") @Inject(method = "populate(Lnet/minecraft/world/chunk/IChunkProvider;Lnet/minecraft/world/gen/IChunkGenerator;)V", at = @At("HEAD"), cancellable = true)
    private void rdpl$dressNothingWhileLighting(IChunkProvider chunkProvider, IChunkGenerator chunkGenrator, CallbackInfo ci) {
        if (isTerrainPopulated || !ContentPregen.dressLater((Chunk) (Object) this)) { return; }
        ContentChunkWatch.dressingHeldOff();
        ci.cancel();
    }

    @Inject(method = "onTick", at = @At("HEAD"))
    private void rdpl$dressWhenStranded(boolean skipRecheckGaps, CallbackInfo ci) {
        if (isTerrainPopulated || world.isRemote || ContentPregen.lightingOnly()) { return; }
        if (((x + z) & 15) != (int) (world.getTotalWorldTime() & 15L)) { return; }
        IChunkProvider provider = world.getChunkProvider();
        if (!(provider instanceof ChunkProviderServer)) { return; }
        ChunkProviderServer server = (ChunkProviderServer) provider;
        Chunk self = (Chunk) (Object) this;
        if (RubicWorldControl.rubicWorld(server) || server.getLoadedChunk(x, z) != self) { return; }
        self.populate(server, server.chunkGenerator);
        if (isTerrainPopulated) { ContentLog.LOGGER.debug("Chunk {}, {} sat undressed in a player's view and is dressed on its tick", x, z); }
    }

    @Redirect(method = "populate(Lnet/minecraft/world/gen/IChunkGenerator;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/Chunk;checkLight()V"))
    private void rdpl$lightAfterDressing(Chunk chunk) {
        isTerrainPopulated = true;
        ContentChunkWatch.lightDeferred();
    }

    @Redirect(method = "populate(Lnet/minecraft/world/gen/IChunkGenerator;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/gen/IChunkGenerator;populate(II)V"))
    private void rdpl$timeDecoration(IChunkGenerator generator, int x, int z) {
        if (!ContentChunkWatch.watching()) {
            generator.populate(x, z);
            return;
        }
        long start = System.nanoTime();
        generator.populate(x, z);
        ContentChunkWatch.decorated(System.nanoTime() - start);
    }
}
