package mctmods.resourcedatapackloader.mixin.client.renderer;

import mctmods.resourcedatapackloader.core.integration.optifine.IVerticalRenderDistance;

import javax.annotation.Nullable;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.ViewFrustum;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = RenderGlobal.class, priority = 2000) public class MixinRenderGlobalNoOptifine {
    @Shadow private ViewFrustum viewFrustum;
    @Shadow private int renderDistanceChunks;

    /**
     * @author tgstyle
     * @reason Bound the vertical neighbor lookup by the cube-based vertical render distance instead of vanilla's fixed column height.
     */

    @Overwrite @Nullable private RenderChunk getRenderChunkOffset(BlockPos playerPos, RenderChunk renderChunkBase, EnumFacing facing) {
        BlockPos blockpos = renderChunkBase.getBlockPosOffset16(facing);
        if (MathHelper.abs(playerPos.getX() - blockpos.getX()) <= this.renderDistanceChunks * 16
                && MathHelper.abs(playerPos.getY() - blockpos.getY()) <= ((IVerticalRenderDistance) this).rdpl$getVerticalRenderDistanceCubes() * 16
                && MathHelper.abs(playerPos.getZ() - blockpos.getZ()) <= this.renderDistanceChunks * 16) { return this.viewFrustum.getRenderChunk(blockpos); }
        return null;
    }
}
