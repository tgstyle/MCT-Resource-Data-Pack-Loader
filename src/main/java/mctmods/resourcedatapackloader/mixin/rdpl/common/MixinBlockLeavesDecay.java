package mctmods.resourcedatapackloader.mixin.rdpl.common;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockLeaves.class) public abstract class MixinBlockLeavesDecay extends Block {
    protected MixinBlockLeavesDecay(Material materialIn) { super(materialIn); }

    @Inject(method = "destroy", at = @At("HEAD"), cancellable = true) private void rdpl$decayOnlyOurselves(World worldIn, BlockPos pos, CallbackInfo ci) {
        Block self = this;
        if (worldIn.getBlockState(pos).getBlock() == self) { return; }
        worldIn.setBlockToAir(pos);
        ci.cancel();
    }
}
