package mctmods.resourcedatapackloader.mixin.rubiclight.common;

import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IRubicWorldInternal;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(World.class) public abstract class MixinWorldLightRubic implements IRubicWorldInternal {
    @Shadow protected int skylightSubtracted;

    @Shadow public abstract Chunk getChunk(BlockPos pos);

    @Shadow public abstract IBlockState getBlockState(BlockPos pos);

    @Shadow public abstract int getLight(BlockPos pos, boolean checkNeighbors);

    @Inject(method = "checkLightFor", at = @At("HEAD"), cancellable = true)
    private void rdpl$checkLightForRubic(EnumSkyBlock lightType, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (!rdpl$isRubicWorld()) { return; }
        cir.setReturnValue(rdpl$getLightingManager().checkLightFor(lightType, pos));
    }

    @Inject(method = "getLight(Lnet/minecraft/util/math/BlockPos;Z)I", at = @At("HEAD"), cancellable = true)
    private void rdpl$getLightRubic(BlockPos pos, boolean checkNeighbors, CallbackInfoReturnable<Integer> cir) {
        if (!rdpl$isRubicWorld()) { return; }
        cir.setReturnValue(rdpl$lightAt(pos, checkNeighbors));
    }

    @Unique private int rdpl$lightAt(BlockPos pos, boolean checkNeighbors) {
        if (pos.getX() < -30000000 || pos.getZ() < -30000000 || pos.getX() >= 30000000 || pos.getZ() >= 30000000) { return 15; }
        if (checkNeighbors && getBlockState(pos).useNeighborBrightness()) {
            int most = getLight(pos.up(), false);
            most = Math.max(most, getLight(pos.east(), false));
            most = Math.max(most, getLight(pos.west(), false));
            most = Math.max(most, getLight(pos.south(), false));
            return Math.max(most, getLight(pos.north(), false));
        }
        if (pos.getY() < rdpl$getMinHeight()) { return 0; }
        BlockPos at = pos.getY() >= rdpl$getMaxHeight() ? new BlockPos(pos.getX(), rdpl$getMaxHeight() - 1, pos.getZ()) : pos;
        return getChunk(at).getLightSubtracted(at, skylightSubtracted);
    }
}
