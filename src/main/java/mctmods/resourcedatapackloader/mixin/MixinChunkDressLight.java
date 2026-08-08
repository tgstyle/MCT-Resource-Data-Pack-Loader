package mctmods.resourcedatapackloader.mixin;

import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Chunk.class)
public abstract class MixinChunkDressLight {
    @Shadow private boolean isLightPopulated;
    @Shadow protected abstract void updateSkylightNeighborHeight(int x, int z, int lower, int upper);

    @Redirect(method = "relightBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/Chunk;updateSkylightNeighborHeight(IIII)V"))
    private void rdpl$skipSkyWhileDressing(Chunk chunk, int x, int z, int lower, int upper) {
        if (rdpl$dressingThis(x, z)) { return; }

        updateSkylightNeighborHeight(x, z, lower, upper);
    }

    @Unique private boolean rdpl$dressingThis(int x, int z) {
        if (isLightPopulated) { return false; }

        ChunkPos dressing = AccessorChunk.rdpl$getPopulating();
        return dressing != null && (x >> 4) == dressing.x && (z >> 4) == dressing.z;
    }
}
