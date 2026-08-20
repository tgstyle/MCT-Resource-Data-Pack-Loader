package mctmods.resourcedatapackloader.mixin.rdpl.common;

import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.IChunkGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Chunk.class) public interface IChunk {
    @Accessor(value = "populating", remap = false) static ChunkPos rdpl$getPopulating() { throw new AssertionError(); }

    @Invoker("populate") void rdpl$dress(IChunkGenerator generator);
}
