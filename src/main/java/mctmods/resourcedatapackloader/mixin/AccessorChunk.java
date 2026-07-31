package mctmods.resourcedatapackloader.mixin;

import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Chunk.class)
public interface AccessorChunk {
    @Accessor(value = "populating", remap = false) static ChunkPos rdpl$getPopulating() { throw new AssertionError(); }
}
