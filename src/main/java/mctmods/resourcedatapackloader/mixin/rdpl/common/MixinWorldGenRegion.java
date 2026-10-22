package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.worldgen.ContentRuleSwitch;
import mctmods.resourcedatapackloader.util.interfaces.IPieceLaid;

import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WorldGenRegion.class) public abstract class MixinWorldGenRegion implements IPieceLaid {
    @Unique private final LongSet rdpl$laidBlocks = new LongOpenHashSet();
    @Unique private boolean rdpl$layingPieces;

    @Inject(method = "setBlock", at = @At("RETURN")) private void rdpl$laid(BlockPos pos, BlockState blockState, int updateFlags, int updateLimit, CallbackInfoReturnable<Boolean> cir) { if (rdpl$layingPieces && cir.getReturnValueZ() && !blockState.isAir()) { rdpl$laidBlocks.add(pos.asLong()); } }

    @SuppressWarnings("deprecation") @Inject(method = "getCurrentDifficultyAt(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/DifficultyInstance;", at = @At("RETURN"), cancellable = true) private void rdpl$localDifficulty(BlockPos pos, CallbackInfoReturnable<DifficultyInstance> cir) { cir.setReturnValue(ContentRuleSwitch.local(((WorldGenRegion) (Object) this).getLevel(), pos, cir.getReturnValue())); }

    @Override public void rdpl$laying(boolean laying) { rdpl$layingPieces = laying; }

    @Override public boolean rdpl$laid(int x, int y, int z) { return rdpl$laidBlocks.contains(BlockPos.asLong(x, y, z)); }
}
