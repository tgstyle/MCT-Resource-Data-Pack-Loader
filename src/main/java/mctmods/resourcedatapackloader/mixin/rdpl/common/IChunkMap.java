package mctmods.resourcedatapackloader.mixin.rdpl.common;

import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.world.level.ChunkPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import javax.annotation.Nullable;

@Mixin(ChunkMap.class) public interface IChunkMap {
    @Invoker("getChunks") Iterable<ChunkHolder> rdpl$getChunks();

    @Invoker("getVisibleChunkIfPresent") @Nullable ChunkHolder rdpl$getVisibleChunkIfPresent(long pos);

    @Invoker("isExistingChunkFull") boolean rdpl$isExistingChunkFull(ChunkPos pos);
}
