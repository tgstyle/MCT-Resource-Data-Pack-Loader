package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.util.PieceLaid;

import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WorldGenRegion.class) public abstract class MixinWorldGenRegion implements PieceLaid {
    @Unique private final LongSet rdpl$laidBlocks = new LongOpenHashSet();
    @Unique private boolean rdpl$layingPieces;

    @Inject(method = "setBlock", at = @At("RETURN"))
    private void rdpl$laid(BlockPos pos, BlockState state, int flags, int recursionLeft, CallbackInfoReturnable<Boolean> cir) {
        if (rdpl$layingPieces && cir.getReturnValueZ() && !state.isAir()) { rdpl$laidBlocks.add(pos.asLong()); }
    }

    @Override public void rdpl$laying(boolean laying) { rdpl$layingPieces = laying; }

    @Override public boolean rdpl$laid(int x, int y, int z) { return rdpl$laidBlocks.contains(BlockPos.asLong(x, y, z)); }
}
