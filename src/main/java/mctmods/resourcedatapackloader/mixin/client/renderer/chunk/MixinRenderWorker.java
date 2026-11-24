package mctmods.resourcedatapackloader.mixin.client.renderer.chunk;

import mctmods.resourcedatapackloader.common.rubic.world.interfaces.ICube;
import mctmods.resourcedatapackloader.common.rubic.world.interfaces.IRubicWorld;
import mctmods.resourcedatapackloader.common.util.world.Coords;
import mctmods.resourcedatapackloader.common.util.worldgen.GenHeights;

import net.minecraft.client.renderer.chunk.ChunkRenderWorker;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ChunkRenderWorker.class) public abstract class MixinRenderWorker {
    @Redirect(method = "processTask", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/chunk/ChunkRenderWorker;isChunkExisting(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/World;)Z", ordinal = 0)) private boolean rdpl$onIsChunkExisting(ChunkRenderWorker chunkRenderWorker, BlockPos pos, World worldIn) {
        BlockPos.MutableBlockPos p = (BlockPos.MutableBlockPos) pos;
        if (GenHeights.rubic(worldIn)) {
            if (!this.isChunkExisting(p.move(EnumFacing.EAST, ICube.SIZE).move(EnumFacing.DOWN, ICube.SIZE), worldIn)) { return false; }
            if (!this.isChunkExisting(p.move(EnumFacing.UP, 2 * ICube.SIZE), worldIn)) { return false; }
            p.move(EnumFacing.DOWN, ICube.SIZE).move(EnumFacing.WEST, ICube.SIZE);
        }
        return this.isChunkExisting(p, worldIn);
    }

    /**
     * @author tgstyle
     * @reason Check for the loaded cube at the position on rubic worlds instead of vanilla's column emptiness test.
     */

    @Overwrite private boolean isChunkExisting(BlockPos pos, World worldIn) {
        if (GenHeights.rubic(worldIn)) { return ((IRubicWorld) worldIn).rdpl$getCubeCache().getLoadedCube(Coords.blockToCube(pos.getX()), Coords.blockToCube(pos.getY()), Coords.blockToCube(pos.getZ())) != null; }
        else { return !worldIn.getChunk(Coords.blockToCube(pos.getX()), Coords.blockToCube(pos.getZ())).isEmpty(); }
    }
}
