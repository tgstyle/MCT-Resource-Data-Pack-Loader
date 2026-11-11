package mctmods.resourcedatapackloader.mixin.world.chunk;

import mctmods.resourcedatapackloader.common.blocks.shield.ShieldIndexes;
import mctmods.resourcedatapackloader.common.util.worldgen.GenHeights;

import net.minecraft.block.state.IBlockState;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Chunk.class) public abstract class MixinChunkShield {
    @Inject(method = "setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;)Lnet/minecraft/block/state/IBlockState;", at = @At("RETURN")) private void rdpl$shieldChanged(BlockPos pos, IBlockState state, CallbackInfoReturnable<IBlockState> cir) {
        IBlockState old = cir.getReturnValue();
        if (old != null) { ShieldIndexes.changed(((Chunk) (Object) this).getWorld(), pos, old, state); }
    }

    @Inject(method = "read(Lnet/minecraft/network/PacketBuffer;IZ)V", at = @At("RETURN")) private void rdpl$shieldRead(PacketBuffer buf, int availableSections, boolean groundUpContinuous, CallbackInfo ci) {
        Chunk chunk = (Chunk) (Object) this;
        World world = chunk.getWorld();
        if (world.isRemote && !GenHeights.rubic(world)) { ShieldIndexes.scan(world, chunk); }
    }
}
