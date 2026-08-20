package mctmods.resourcedatapackloader.mixin.rdpl.client;

import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IRubicWorld;

import net.minecraft.client.renderer.chunk.ChunkRenderWorker;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ChunkRenderWorker.class) public abstract class MixinRenderWorker {
    @Redirect(method = "processTask", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/chunk/ChunkRenderWorker;isChunkExisting(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/World;)Z",
            ordinal = 0))
    private boolean onIsChunkExisting(ChunkRenderWorker chunkRenderWorker, BlockPos pos, World worldIn) {
        BlockPos.MutableBlockPos p = (BlockPos.MutableBlockPos) pos;
        if (((IRubicWorld) worldIn).rdpl$isRubicWorld()) {
            if (!this.isChunkExisting(p.move(EnumFacing.EAST, 16).move(EnumFacing.DOWN, 16), worldIn)) { return false; }
            if (!this.isChunkExisting(p.move(EnumFacing.UP, 32), worldIn)) { return false; }
            p.move(EnumFacing.DOWN, 16).move(EnumFacing.WEST, 16);
        }
        return this.isChunkExisting(p, worldIn);
    }

    /**
     * @author tgstyle
     * @reason Check for the loaded cube at the position on rubic worlds instead of vanilla's column emptiness test.
     */

    @Overwrite private boolean isChunkExisting(BlockPos pos, World worldIn) {
        if (((IRubicWorld) worldIn).rdpl$isRubicWorld()) {
            return ((IRubicWorld) worldIn).rdpl$getCubeCache()
                    .getLoadedCube(pos.getX() >> 4, pos.getY() >> 4, pos.getZ() >> 4) != null;
        }
        else { return !worldIn.getChunk(pos.getX() >> 4, pos.getZ() >> 4).isEmpty(); }
    }
}
