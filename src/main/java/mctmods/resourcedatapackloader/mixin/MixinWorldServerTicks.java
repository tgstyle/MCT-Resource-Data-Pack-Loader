package mctmods.resourcedatapackloader.mixin;

import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.NextTickListEntry;
import net.minecraft.world.WorldServer;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

@Mixin(WorldServer.class)
public abstract class MixinWorldServerTicks {
    @Shadow @Final private TreeSet<NextTickListEntry> pendingTickListEntriesTreeSet;
    @Shadow @Final private List<NextTickListEntry> pendingTickListEntriesThisTick;
    @Unique private final Map<Long, List<NextTickListEntry>> rdpl$byChunk = new HashMap<>();
    @Unique private int rdpl$builtFromCount = -1;
    @Unique private long rdpl$builtOnTick = -1L;

    @Inject(method = "getPendingBlockUpdates(Lnet/minecraft/world/gen/structure/StructureBoundingBox;Z)Ljava/util/List;", at = @At("HEAD"), cancellable = true)
    private void rdpl$answerFromTheIndex(StructureBoundingBox structureBB, boolean remove, CallbackInfoReturnable<List<NextTickListEntry>> cir) {
        if (remove) {
            rdpl$builtFromCount = -1;
            return;
        }

        if (pendingTickListEntriesTreeSet.isEmpty() && pendingTickListEntriesThisTick.isEmpty()) {
            cir.setReturnValue(null);
            return;
        }

        int waiting = pendingTickListEntriesTreeSet.size();
        long now = ((WorldServer) (Object) this).getTotalWorldTime();
        if (waiting != rdpl$builtFromCount || now != rdpl$builtOnTick) {
            rdpl$byChunk.clear();
            rdpl$gather(pendingTickListEntriesTreeSet);
            rdpl$builtFromCount = waiting;
            rdpl$builtOnTick = now;
        }

        List<NextTickListEntry> found = null;
        int buckets = 0;
        for (int x = structureBB.minX >> 4; x <= structureBB.maxX >> 4; x++) {
            for (int z = structureBB.minZ >> 4; z <= structureBB.maxZ >> 4; z++) {
                List<NextTickListEntry> here = rdpl$byChunk.get(ChunkPos.asLong(x, z));
                if (here == null) { continue; }

                buckets++;
                for (NextTickListEntry entry : here) {
                    if (rdpl$outside(entry, structureBB)) { continue; }
                    if (found == null) { found = new ArrayList<>(); }

                    found.add(entry);
                }
            }
        }
        if (found != null && buckets > 1) { Collections.sort(found); }
        for (NextTickListEntry entry : pendingTickListEntriesThisTick) {
            if (rdpl$outside(entry, structureBB)) { continue; }
            if (found == null) { found = new ArrayList<>(); }

            found.add(entry);
        }
        cir.setReturnValue(found);
    }

    @Unique private static boolean rdpl$outside(NextTickListEntry entry, StructureBoundingBox structureBB) {
        return entry.position.getX() < structureBB.minX || entry.position.getX() >= structureBB.maxX || entry.position.getZ() < structureBB.minZ || entry.position.getZ() >= structureBB.maxZ;
    }

    @Unique private void rdpl$gather(Iterable<NextTickListEntry> entries) {
        for (NextTickListEntry entry : entries) {
            long key = ChunkPos.asLong(entry.position.getX() >> 4, entry.position.getZ() >> 4);
            rdpl$byChunk.computeIfAbsent(key, at -> new ArrayList<>()).add(entry);
        }
    }

    @Inject(method = "tickUpdates", at = @At("RETURN"))
    private void rdpl$forgetAfterTicking(boolean runAllPending, CallbackInfoReturnable<Boolean> cir) { rdpl$builtFromCount = -1; }
}
