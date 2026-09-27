package mctmods.resourcedatapackloader.mixin.rdpl.common;

import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.world.level.ChunkPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ChunkMap.class) public interface IChunkMap {
    @Accessor("visibleChunkMap") Long2ObjectLinkedOpenHashMap<ChunkHolder> rdpl$visibleChunkMap();

    @Invoker("isExistingChunkFull") boolean rdpl$isExistingChunkFull(ChunkPos pos);
}
