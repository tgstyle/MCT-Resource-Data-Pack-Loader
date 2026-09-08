package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.util.Config;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LeavesBlock.class) public abstract class MixinLeavesBlock {
    @Shadow protected abstract boolean decaying(BlockState state);

    @Inject(method = "tick", at = @At("TAIL")) private void rdpl$decaySoon(BlockState state, ServerLevel level, BlockPos pos, RandomSource random, CallbackInfo ci) {
        if (!Config.tweaks.promptLeafDecay()) { return; }
        Block self = (Block) (Object) this;
        BlockState now = level.getBlockState(pos);
        if (!now.is(self) || !decaying(now)) { return; }
        if (decaying(state)) {
            Block.dropResources(now, level, pos);
            level.removeBlock(pos, false);
            return;
        }
        level.scheduleTick(pos, self, Mth.nextInt(random, 10, 20));
    }
}
