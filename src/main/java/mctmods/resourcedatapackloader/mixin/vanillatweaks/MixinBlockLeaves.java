package mctmods.resourcedatapackloader.mixin.vanillatweaks;

import mctmods.resourcedatapackloader.util.Config;

import net.minecraft.block.BlockLeaves;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = BlockLeaves.class, remap = false) public abstract class MixinBlockLeaves {
    @Inject(method = "beginLeavesDecay", at = @At("TAIL")) private void rdpl$decaySoon(IBlockState state, World world, BlockPos pos, CallbackInfo ci) {
        if (!Config.tweaks.promptLeafDecay || !world.getChunk(pos).isPopulated() || !state.getValue(BlockLeaves.DECAYABLE)) { return; }

        world.scheduleUpdate(pos, state.getBlock(), MathHelper.getInt(world.rand, 10, 20));
    }
}
