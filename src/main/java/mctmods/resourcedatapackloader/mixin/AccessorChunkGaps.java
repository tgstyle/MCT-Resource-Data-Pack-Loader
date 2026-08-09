package mctmods.resourcedatapackloader.mixin;

import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Chunk.class)
public interface AccessorChunkGaps {
    @Invoker("recheckGaps") void rdpl$recheckGaps(boolean isClient);
}
