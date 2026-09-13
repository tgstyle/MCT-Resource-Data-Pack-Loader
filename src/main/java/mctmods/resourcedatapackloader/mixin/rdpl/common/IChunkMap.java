package mctmods.resourcedatapackloader.mixin.rdpl.common;

import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ChunkMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ChunkMap.class) public interface IChunkMap { @Invoker("getChunks") Iterable<ChunkHolder> rdpl$getChunks(); }
