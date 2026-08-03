package mctmods.resourcedatapackloader.mixin;

import mctmods.resourcedatapackloader.content.worldgen.ContentChunkWatch;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkProviderServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.Arrays;

@Mixin(ChunkProviderServer.class)
public abstract class MixinChunkProviderLookup {
    @Unique private static final int RDPL_SLOTS = 64;
    @Unique private final long[] rdpl$keys = new long[RDPL_SLOTS];
    @Unique private final Chunk[] rdpl$chunks = new Chunk[RDPL_SLOTS];
    @Unique private boolean rdpl$ready;

    @Unique private int rdpl$slotOf(long key) { return ((int) key & 7) | ((int) (key >>> 32) & 7) << 3; }

    @Unique private Chunk rdpl$remembered(long key) {
        if (!rdpl$ready) { return null; }

        int slot = rdpl$slotOf(key);
        return rdpl$keys[slot] == key ? rdpl$chunks[slot] : null;
    }

    @Unique private void rdpl$ready() {
        if (rdpl$ready) { return; }

        Arrays.fill(rdpl$keys, Long.MIN_VALUE);
        rdpl$ready = true;
    }

    @Unique private void rdpl$remember(long key, Chunk chunk) {
        rdpl$ready();
        int slot = rdpl$slotOf(key);
        rdpl$keys[slot] = key;
        rdpl$chunks[slot] = chunk;
    }

    @Redirect(method = "getLoadedChunk", at = @At(value = "INVOKE", target = "Lit/unimi/dsi/fastutil/longs/Long2ObjectMap;get(J)Ljava/lang/Object;", remap = false))
    private Object rdpl$rememberLoaded(Long2ObjectMap<Chunk> loaded, long key) {
        Chunk known = rdpl$remembered(key);
        if (known != null) { return known; }
        if (ContentChunkWatch.watching()) { ContentChunkWatch.chunkLookup(); }

        Chunk found = loaded.get(key);
        if (found != null) { rdpl$remember(key, found); }

        return found;
    }

    @Redirect(method = "chunkExists", at = @At(value = "INVOKE", target = "Lit/unimi/dsi/fastutil/longs/Long2ObjectMap;containsKey(J)Z", remap = false))
    private boolean rdpl$rememberExists(Long2ObjectMap<Chunk> loaded, long key) {
        if (rdpl$remembered(key) != null) { return true; }
        if (ContentChunkWatch.watching()) { ContentChunkWatch.chunkLookup(); }

        return loaded.containsKey(key);
    }

    @Redirect(method = "provideChunk", at = @At(value = "INVOKE", target = "Lit/unimi/dsi/fastutil/longs/Long2ObjectMap;put(JLjava/lang/Object;)Ljava/lang/Object;", remap = false))
    private Object rdpl$rememberWhenMade(Long2ObjectMap<Chunk> loaded, long key, Object chunk) { return rdpl$putAndRemember(loaded, key, chunk); }

    @Redirect(method = "loadChunk(IILjava/lang/Runnable;)Lnet/minecraft/world/chunk/Chunk;", at = @At(value = "INVOKE", target = "Lit/unimi/dsi/fastutil/longs/Long2ObjectMap;put(JLjava/lang/Object;)Ljava/lang/Object;", remap = false), remap = false)
    private Object rdpl$rememberWhenRead(Long2ObjectMap<Chunk> loaded, long key, Object chunk) { return rdpl$putAndRemember(loaded, key, chunk); }

    @Unique private Object rdpl$putAndRemember(Long2ObjectMap<Chunk> loaded, long key, Object chunk) {
        rdpl$remember(key, (Chunk) chunk);

        return loaded.put(key, (Chunk) chunk);
    }

    @Inject(method = "queueUnload", at = @At("HEAD"))
    private void rdpl$forgetQueued(Chunk chunkIn, CallbackInfo ci) {
        if (!rdpl$ready) { return; }

        int slot = rdpl$slotOf(ChunkPos.asLong(chunkIn.x, chunkIn.z));
        if (rdpl$chunks[slot] != chunkIn) { return; }

        rdpl$keys[slot] = Long.MIN_VALUE;
        rdpl$chunks[slot] = null;
    }
}
