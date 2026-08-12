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

@Mixin(value = StructureSpawnContext.class, remap = false)
public abstract class MixinStructureSpawnContext {
    @Unique private static boolean rdpl$told;

    @Inject(method = "setBlock", at = @At("HEAD"), cancellable = true, remap = false)
    private void rdpl$leaveForLater(BlockPos pos, IBlockState state, int flag, CallbackInfoReturnable<Boolean> cir) {
        StructureSpawnContext self = (StructureSpawnContext) (Object) this;
        if (self.environment == null) { return; }
        if (self.environment.world.getChunkProvider().getLoadedChunk(pos.getX() >> 4, pos.getZ() >> 4) != null) { return; }

        if (!rdpl$told) {
            rdpl$told = true;
            ContentLog.LOGGER.info("A structure reached into land that has not been made yet, which would have made it and everything around it in turn. That part is left for Recurrent Complex to fill in when the chunk comes, which is what its own complementing is for");
        }
        cir.setReturnValue(false);
    }
}
