package mctmods.resourcedatapackloader.mixin.reccomplex;

import mctmods.resourcedatapackloader.util.ContentLog;

import ivorius.reccomplex.world.gen.feature.structure.context.StructureSpawnContext;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = StructureSpawnContext.class, remap = false) public abstract class MixinStructureSpawnContext {
    @Unique private static boolean rdpl$told;
    @Unique private static int rdpl$dropped;
    @Unique private static int rdpl$named;

    @Inject(method = "setBlock", at = @At("HEAD"), cancellable = true, remap = false) private void rdpl$leaveForLater(BlockPos pos, IBlockState state, int flag, CallbackInfoReturnable<Boolean> cir) {
        StructureSpawnContext self = (StructureSpawnContext) (Object) this;
        if (self.environment == null) { return; }
        if (self.environment.world.getChunkProvider().getLoadedChunk(pos.getX() >> 4, pos.getZ() >> 4) != null) { return; }
        if (!rdpl$told) {
            rdpl$told = true;
            ContentLog.LOGGER.info("A structure reached into land that has not been made yet, which would have made it and everything around it in turn. That part is left for Recurrent Complex to fill in when the chunk comes, which is what its own complementing is for");
        }
        rdpl$dropped++;
        if (self.generationBB != null && self.generationBB.isVecInside(pos) && rdpl$named < 60) {
            rdpl$named++;
            ContentLog.LOGGER.debug("A structure standing at {}, {} was refused {} at {}, {}, {}, which its own pass had asked for, so nothing will lay it there", self.boundingBox.minX, self.boundingBox.minZ, state.getBlock().getRegistryName(), pos.getX(), pos.getY(), pos.getZ());
        }
        if (rdpl$dropped % 500 == 0) { ContentLog.LOGGER.debug("{} block(s) have now been refused for standing in land that is not made yet", rdpl$dropped); }
        cir.setReturnValue(false);
    }
}
