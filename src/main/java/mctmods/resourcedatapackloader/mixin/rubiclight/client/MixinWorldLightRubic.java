package mctmods.resourcedatapackloader.mixin.rubiclight.client;

import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IRubicWorldInternal;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(World.class) public abstract class MixinWorldLightRubic implements IRubicWorldInternal {
    @Shadow @Final public WorldProvider provider;

    @Shadow public abstract Chunk getChunk(BlockPos pos);

    @Shadow public abstract IBlockState getBlockState(BlockPos pos);

    @Shadow public abstract boolean isValid(BlockPos pos);

    @Shadow public abstract boolean isBlockLoaded(BlockPos pos);

    @Shadow public abstract int getLightFor(EnumSkyBlock type, BlockPos pos);

    @Inject(method = "getLightFromNeighborsFor", at = @At("HEAD"), cancellable = true)
    private void rdpl$lightFromNeighborsRubic(EnumSkyBlock type, BlockPos pos, CallbackInfoReturnable<Integer> cir) {
        if (!rdpl$isRubicWorld()) { return; }
        cir.setReturnValue(rdpl$neighborLight(type, pos));
    }

    @Unique private int rdpl$neighborLight(EnumSkyBlock type, BlockPos pos) {
        if (!provider.hasSkyLight() && type == EnumSkyBlock.SKY) { return 0; }
        BlockPos at = pos.getY() < rdpl$getMinHeight() ? new BlockPos(pos.getX(), rdpl$getMinHeight(), pos.getZ()) : pos;
        if (!isValid(at) || !isBlockLoaded(at)) { return type.defaultLightValue; }
        if (getBlockState(at).useNeighborBrightness()) {
            int most = getLightFor(type, at.up());
            most = Math.max(most, getLightFor(type, at.east()));
            most = Math.max(most, getLightFor(type, at.west()));
            most = Math.max(most, getLightFor(type, at.south()));
            return Math.max(most, getLightFor(type, at.north()));
        }
        return getChunk(at).getLightFor(type, at);
    }
}
