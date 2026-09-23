package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.util.WriteBoxWatch;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Chunk.class) public class MixinChunkWriteBox {
    @Inject(method = "setBlockState", at = @At("RETURN")) private void rdpl$watchWrite(BlockPos pos, IBlockState state, CallbackInfoReturnable<IBlockState> cir) {
        IBlockState was = cir.getReturnValue();
        if (was != null) { WriteBoxWatch.write(pos.getX(), pos.getY(), pos.getZ(), was, state); }
    }
}
